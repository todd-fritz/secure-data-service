/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.api.resources.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.Resource;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.oauth.ApplicationAuthorizationValidator;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.api.util.SecurityUtil.SecurityTask;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;

/**
 * Used to retrieve the list of apps that a user is allowed to use.
 *
 * @author pwolf
 *
 */
@Component
@Scope("request")
@Path("/userapps")
@Produces({ Resource.JSON_MEDIA_TYPE + ";charset=utf-8" })
public class ApprovedApplicationResource {

    public static final String RESOURCE_NAME = "application";
    public static final String DELEGATED_ADMIN_PLACEHOLDER = "DELEGATED_ADMIN";

    private static final String[] ALLOWED_ATTRIBUTES = new String[] {
        "application_url", "administration_url", "image_url", "description",
 "name", "vendor", "version", "is_admin", "behavior", "endpoints"
    };


    @Autowired
    private EntityDefinitionStore store;

    @Autowired
    private ApplicationAuthorizationValidator appValidator;

    @Autowired
    @Qualifier("validationRepo")
    private Repository<Entity> repo;

    private EntityService service;

    @Autowired
    private DelegationUtil delegationUtil;

    @PostConstruct
    public void init() {
        EntityDefinition def = store.lookupByResourceName(RESOURCE_NAME);
        this.service = def.getService();
    }

    @SuppressWarnings("unchecked")
    @GET
    public Response getApplications(@DefaultValue("") @QueryParam("is_admin") String adminFilter) {
        List<String> allowedApps = getAllowedAppIds();

        List<EntityBody> results = new ArrayList<EntityBody>();

        for (final String id : allowedApps) {

            EntityBody result  = SecurityUtil.sudoRun(new SecurityTask<EntityBody>()  {
                @Override
                public  EntityBody execute() {
                    try {
                        return service.get(id);
                    } catch (EntityNotFoundException e) {
                        //Probably means that the application is in the appAuthorization
                        //collection but not the application one.
                        return null;
                    }
                }
            });


            if (result != null) {

                if (!shouldFilterApp(result, adminFilter)) {

                    filterAttributes(result);
                    results.add(result);
                }
            }
        }
        return Response.status(Status.OK).entity(results).build();
    }

    private boolean shouldFilterApp(EntityBody result, String adminFilter) {
        if (result.containsKey("endpoints")) {
            List<Map<String, Object>> endpoints = (List<Map<String, Object>>) result.get("endpoints");
            filterEndpoints(endpoints);

            //we ended up filtering out all the endpoints - no reason to display the app
            if (endpoints.size() == 0) {
                return true;
            }
        }

        boolean isAdminApp = result.containsKey("is_admin") ? Boolean.valueOf((Boolean) result.get("is_admin")) : false;

        //is_admin query param specified
        if (!adminFilter.equals("")) {
            boolean adminFilterVal = Boolean.valueOf(adminFilter);

            //non-admin app, but is_admin == true
            if (!isAdminApp && adminFilterVal) {
                return true;
            }

            //admin app, but is_admin == false
            if (isAdminApp && !adminFilterVal) {
                return true;
            }
        }

        // don't allow "installed" apps
        if (result.get("installed") == null || (Boolean) result.get("installed")) {
            return true;
        }

        //make sure hosted SLI users can only see admin and portal
        if (SecurityUtil.isHostedUser(repo, (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal())) {
            Boolean adminVisible = (Boolean) result.get("admin_visible");
            if (adminVisible == null || !adminVisible) {
                return true;
            }
        }
        return false;
    }

    private List<String> getUsersRoles() {
        SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ArrayList<String> toReturn = new ArrayList(principal.getRoles());

        //This is a fake role we use mean that a user is either an LEA admin or an SEA admin with delegated rights
        if (hasAppAuthorizationRight()) {
            toReturn.add(DELEGATED_ADMIN_PLACEHOLDER);
        }

        return toReturn;
    }

    private boolean hasAppAuthorizationRight() {
        if (SecurityUtil.hasRight(Right.EDORG_APP_AUTHZ)) {
            //edorg authz users always have the right to authorize apps
            return true;
        } else if (SecurityUtil.hasRight(Right.EDORG_DELEGATE)) {
            //We need to figure out if any districts have delegated to us
            return delegationUtil.getDelegateEdOrgs().size() > 0;
        }
        return false;
    }

    private void filterEndpoints(List<Map<String, Object>> endpoints) {
        List<String> userRoles = getUsersRoles();

        for (Iterator<Map<String, Object>> i = endpoints.iterator(); i.hasNext();) {

            @SuppressWarnings("unchecked")
            List<String> reqRoles = (List<String>) i.next().get("roles");

            //if no roles specified, don't filter it
            if (reqRoles.size() == 0) {
                continue;
            }

            List<String> intersection = new ArrayList<String>(reqRoles);
            intersection.retainAll(userRoles);
            if (userRoles.size() == 0 || intersection.size() == 0) {
                debug("Removing endpoint because users roles {} did not match required roles {}.", userRoles, reqRoles);
                i.remove();
            }
        }
    }

    private List<String> getAllowedAppIds() {
        SLIPrincipal principal = null;
        SecurityContext context = SecurityContextHolder.getContext();
        if (context.getAuthentication() != null) {
            principal = (SLIPrincipal) context.getAuthentication().getPrincipal();
        }
        if (principal == null) {
            throw new InsufficientAuthenticationException("Application list is a protected resource.");
        }
        List<String> toReturn = appValidator.getAuthorizedApps(principal);

        //For now, null (meaning no LEA data for the user) defaults to all apps
        if (toReturn == null) {
            toReturn = new ArrayList<String>();

            //the app list is an admin-protected resource, so we must sudo it
            Iterable<String> appIds = SecurityUtil.sudoRun(new SecurityTask<Iterable<String>>()  {
                @Override
                public  Iterable<String> execute() {
                    return service.listIds(new NeutralQuery());
                }
            });

            for (String id : appIds) {
                toReturn.add(id);
            }
        }
        return toReturn;
    }

    /**
     * Filters out attributes we don't want ordinary users to see.
     * For example, they should never see the client_secret.
     *
     * @param result
     */
    private void filterAttributes(EntityBody result) {
        for (Iterator<Map.Entry<String, Object>> i = result.entrySet().iterator(); i.hasNext();) {
            String key = i.next().getKey();
            if (!Arrays.asList(ALLOWED_ATTRIBUTES).contains(key)) {
                i.remove();
            }
        }

    }
}
