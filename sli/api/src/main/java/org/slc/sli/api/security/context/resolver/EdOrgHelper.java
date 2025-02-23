/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

package org.slc.sli.api.security.context.resolver;


import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slc.sli.api.resources.security.DelegationUtil;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.EntityOwnershipValidator;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.util.RequestUtil;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.utils.EdOrgHierarchyHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Contains helper methods for traversing the edorg hierarchy.
 *
 * Assumptions it makes
 *
 * <ul>
 * <li>SEAs, LEAs, and Schools are all edorgs with organizationCategories of 'State Education
 * Agency' 'Local Education Agency', and 'School' respectively.</li>
 * <li>The parentEducationAgencyReference of a school always points to an LEA</li>
 * <li>The parentEducationAgencyReference of an LEA can point to either an SEA or another LEA</li>
 * <li>SEAs don't have a parentEducationAgencyReference and therefore are always at the top of the
 * tree</li>
 * </ul>
 *
 *
 */
@Component
public class EdOrgHelper {
    private static final Logger LOG = LoggerFactory.getLogger(EdOrgHelper.class);

    @Autowired
    protected PagingRepositoryDelegate<Entity> repo;

    @Autowired
    protected DateHelper dateHelper;

    @Autowired
    protected EntityOwnershipValidator ownership;

    @Autowired
    protected DelegationUtil delegationUtil;

    private EdOrgHierarchyHelper helper;

    private static ThreadLocal<UUID> currentRequestIdTL = new ThreadLocal<UUID>() {
        @Override
        protected UUID initialValue()
        {
            return RequestUtil.generateRequestId();
        }
    };

    private static ThreadLocal<Map<String, Entity>> edOrgCacheTL = new ThreadLocal<Map<String, Entity>>() {
        @Override
        protected Map<String, Entity> initialValue()
        {
            return new HashMap<String, Entity>();
        }
    };

    @PostConstruct
    public void init() {
        helper = new EdOrgHierarchyHelper(repo);
    }

    /**
     * Determine the districts of the user.
     *
     * If the user is directly associated with an SEA, this is any LEA directly below the SEA. If
     * the user is directly
     * associated with an LEA, this is the top-most LEA i.e. the LEA directly associated with the
     * SEA.
     *
     * @param user - User entity
     *
     * @return - List of entity IDs
     */
    public List<String> getDistricts(Entity user) {
        Set<String> directAssoc = getDirectEdorgs(user);
        return getDistricts(directAssoc);
    }

    /**
     * Determine the districts based upon the EdOrgs supplied.
     *
     * If an SEA is encountered, this is any LEA directly below the SEA.
     * If an LEA or school is encountered, this is the top-most LEA, i.e. the LEA directly associated with the SEA.
     *
     * @param edOrgs - EdOrgs to search
     *
     * @return - List of district entity IDs
     */
    public List<String> getDistricts(Set<String> edOrgs) {
        NeutralQuery query = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.CRITERIA_IN,
                edOrgs, false));
        Set<String> entities = new HashSet<String>();
        for (Entity entity : repo.findAll(EntityNames.EDUCATION_ORGANIZATION, query)) {
            if (helper.isLEA(entity)) {
            	List<Entity> topLEAs = helper.getTopLEAOfEdOrg(entity);
            	if(topLEAs!=null) {
            		for(Entity topLEA: topLEAs) {
            			entities.add(topLEA.getEntityId());
            		}
            	}
            } else if (helper.isSchool(entity)) {
            	List<Entity> topLEAs = helper.getTopLEAOfEdOrg(entity);
            	if(topLEAs!=null) {
            		for(Entity topLEA: topLEAs) {
            			entities.add(topLEA.getEntityId());
            		}
            	}
            } else { // isSEA
                entities.addAll(getDirectChildLEAsOfEdOrg(entity));
            }
        }
        return new ArrayList<String>(entities);
    }

    /**
     * Get a list of the direct child LEAs of an EdOrg.
     *
     * @param edOrgEntity - EdOrg from which to get child LEAs
     *
     * @return - List of the EdOrg's child LEAs
     */
    public List<String> getDirectChildLEAsOfEdOrg(Entity edOrgEntity) {
        Set<String> result;

        if (edOrgEntity == null) {
            return null;
        }

        result = getDirectChildLEAsOfEdOrg(edOrgEntity.getEntityId());
        if (result == null || result.isEmpty()) {
            return null;
        }

        return new ArrayList<String>(result);
    }

    private Set<String> getDirectChildLEAsOfEdOrg(String edOrgId) {
        Set<String> toReturn = new HashSet<String>();
        NeutralQuery query = new NeutralQuery(0);
        query.addCriteria(new NeutralCriteria("parentEducationAgencyReference", "=", edOrgId));

        for (Entity entity : repo.findAll(EntityNames.EDUCATION_ORGANIZATION, query)) {
            if (helper.isLEA(entity)) {
                toReturn.add(entity.getEntityId());
            }
        }
        return toReturn;
    }

    public Set<String> getAllChildLEAsOfEdOrg(Entity edOrgEntity) {
        String myId;
        Set<String> edOrgs = new HashSet<String>();
        Set<String> result = new HashSet<String>();

        if (edOrgEntity == null || edOrgEntity.getEntityId() == null) {
            return null;
        }
        myId = edOrgEntity.getEntityId();
        edOrgs.add(myId);

        result = getAllChildLEAsOfEdOrg(edOrgs, new HashSet<String>());
        result.remove(myId);
        return result;
    }

    private Set<String> getAllChildLEAsOfEdOrg(Set<String> edOrgIds, Set<String>toReturn) {
        Set<String> childLEAs = new HashSet<String>();

        // collect all direct child LEAs
        for (String edOrgId : edOrgIds) {
            childLEAs.addAll(getDirectChildLEAsOfEdOrg(edOrgId));
        }

        // remove any we have already processed
        if (toReturn != null) {
            childLEAs.removeAll(toReturn);
        }

        // base case: no new children so just return the accumulated set
        if (childLEAs.isEmpty()) {
            return toReturn;
        }

        // add the new children to those we will ultimately return
        toReturn.addAll(childLEAs);
        return getAllChildLEAsOfEdOrg(childLEAs, toReturn);
    }


    /**
     * Get the parents of an EdOrg.
     *
     * @param edOrgEntity - EdOrg from which to get parents
     *
     * @return - set of the EdOrg's parents
     */
    public List<String> getParentEdOrgs(Entity edOrgEntity) {
        List<String> toReturn = new ArrayList<String>();

        Map<String, Entity> edOrgCache = loadEdOrgCache();
        Set<String> visitedEdOrgs = new HashSet<String>();

        if (edOrgEntity != null) {
            String myId = edOrgEntity.getEntityId();
            if (myId != null) {
                visitedEdOrgs.add(myId);
                toReturn = getParentEdOrgs(edOrgEntity, edOrgCache, visitedEdOrgs, toReturn);
            }
        }

        return toReturn;
    }

    private List<String> getParentEdOrgs(final Entity edOrg, final Map<String, Entity> edOrgCache, final Set<String> visitedEdOrgs, List<String> toReturn) {
        // base case
        if (edOrg == null || visitedEdOrgs.contains(edOrg)) {
            return toReturn;
        }

        if (edOrg != null && edOrg.getBody() != null) {
            @SuppressWarnings("unchecked")
            List<String> parentIds = (List<String>) edOrg.getBody().get("parentEducationAgencyReference");
            if (parentIds != null) {
                for (String parentId : parentIds) {
                    if (parentId != null && !visitedEdOrgs.contains(parentId)) {
                        visitedEdOrgs.add(parentId);

                        Entity parentEdOrg = edOrgCache.get(parentId);
                        if (parentEdOrg != null) {
                            toReturn.add(parentId);
                            getParentEdOrgs(parentEdOrg, edOrgCache, visitedEdOrgs, toReturn);
                        }
                    }
                }
            }
        }

        return toReturn;
    }

    private Map<String, Entity> loadEdOrgCache() {
        if (!currentRequestIdTL.get().equals(RequestUtil.getCurrentRequestId())) {
            Map<String, Entity> edOrgCache = new HashMap<String, Entity>();

            Iterator<Entity> edOrgs = repo.findEach(EntityNames.EDUCATION_ORGANIZATION, (NeutralQuery) null);
            if (edOrgs != null) {
                while (edOrgs.hasNext()) {
                    Entity eo = edOrgs.next();
                    edOrgCache.put(eo.getEntityId(), eo);
                }
            }

            edOrgCacheTL.set(edOrgCache);
            currentRequestIdTL.set(RequestUtil.getCurrentRequestId());
        }

        return edOrgCacheTL.get();
    }

    public Entity byId(String edOrgId) {
        return repo.findById(EntityNames.EDUCATION_ORGANIZATION, edOrgId);
    }

    public boolean isSEA(Entity entity) {
        // passing through
        return helper.isSEA(entity);
    }

    /**
     * Given an edorg entity, returns the SEA to which it belongs
     *
     * @param entity
     *
     * @return SEA
     */
    public String getSEAOfEdOrg(Entity entity) {
        return helper.getSEAOfEdOrg(entity);
    }

    /**
     * Finds schools directly associated to this user
     *
     * @param principal
     * @return
     */
    public List<String> getDirectSchools(Entity principal) {
        return getDirectSchoolsLineage(principal, false);
    }

    public List<String> getDirectSchoolsLineage(Entity principal, boolean getLineage) {
        Set<String> ids = getDirectEdorgs(principal);
        Iterable<Entity> edorgs = repo.findAll(EntityNames.EDUCATION_ORGANIZATION, new NeutralQuery(
                new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.CRITERIA_IN, ids, false)));

        List<String> schools = new ArrayList<String>();
        for (Entity e : edorgs) {
            if (helper.isSchool(e)) {
                schools.add(e.getEntityId());
                if (getLineage) {
                    schools.addAll(extractEdorgFromMeta(e));
                }
            }
        }

        return schools;
    }

    @SuppressWarnings("unchecked")
    private Set<String> extractEdorgFromMeta( Entity e) {
        Set<String> edOrgs = new HashSet<String>();
        Map<String,Object> meta = e.getMetaData();

        if( meta == null || !meta.containsKey( ParameterConstants.EDORGS_ARRAY )) {
            return edOrgs;
        }

        edOrgs.addAll( (Collection<? extends String>) meta.get( ParameterConstants.EDORGS_ARRAY  ));

        return edOrgs;
    }
    /**
     * Recursively returns the list of all child edorgs
     *
     * @param edOrgs
     * @return
     */
    public Set<String> getChildEdOrgs(Collection<String> edOrgs) {
        Set<String> visitedEdOrgs = new HashSet<String>();
        return getChildEdOrgs(visitedEdOrgs, edOrgs);
    }

	public Set<String> getChildEdOrgs(String edOrg) {
		Set<String> edOrgs = new HashSet<String>();
		edOrgs.add(edOrg);
		return getChildEdOrgs(edOrgs);
	}

    public Set<String> getChildEdOrgs(final Set<String> visitedEdOrgs, Collection<String> edOrgs) {

        if (edOrgs.isEmpty()) {
            return new HashSet<String>();
        }

        NeutralQuery query = new NeutralQuery(new NeutralCriteria(ParameterConstants.PARENT_EDUCATION_AGENCY_REFERENCE,
                NeutralCriteria.CRITERIA_IN, edOrgs));
        Iterable<Entity> children = repo.findAll(EntityNames.EDUCATION_ORGANIZATION, query);

        visitedEdOrgs.addAll(edOrgs);

        Set<String> childIds = new HashSet<String>();
        for (Entity child : children) {
            if (!visitedEdOrgs.contains(child.getEntityId())) {
                childIds.add(child.getEntityId());
            }
        }
        if (!childIds.isEmpty()) {
            childIds.addAll(getChildEdOrgs(visitedEdOrgs, childIds));
        }
        return childIds;
    }


    /**
     * Recursively returns the list of all child edorgs By Name
     *
     * @param parentEdOrgIds a list of edOrg ids
     * @return a list of edOrg state unique ids (names!) of child edOrgs
     */
    public Set<String> getChildEdOrgsName(Collection<String> parentEdOrgIds) {
        return getChildEdOrgsName(parentEdOrgIds, false);
    }

    /**
     * Recursively returns the list of all child edorgs By Name
     *
     * @param parentEdOrgIds a list of parent edOrg ids
     * @param includeParents whether to include the given parents in the results as well
     * @return a list of edOrg state unique ids (names!) of child edOrgs
     */
    public Set<String> getChildEdOrgsName(Collection<String> parentEdOrgIds, boolean includeParents) {
        // get child edOrgs reusing existing code
        Set<String> childEdOrgIds = getChildEdOrgs(parentEdOrgIds);
        if (includeParents) {
            childEdOrgIds.addAll(parentEdOrgIds);
        }

        // do a single query to get the names (stateOrganizationId)
        NeutralQuery query = new NeutralQuery(
                new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.CRITERIA_IN,
                        new ArrayList<String>(childEdOrgIds))).setIncludeFields(Arrays.asList(ParameterConstants.STATE_ORGANIZATION_ID));

        Iterable<Entity> children = repo.findAll(EntityNames.EDUCATION_ORGANIZATION, query);
        Set<String> names = new HashSet<String>();
        for (Entity child : children) {
            names.add((String) child.getBody().get(ParameterConstants.STATE_ORGANIZATION_ID));
        }
        return names;
    }

    private Entity getTopLEAOfEdOrg(Entity entity) {
        if (entity.getBody().containsKey("parentEducationAgencyReference")) {
        	@SuppressWarnings("unchecked")
			List<String> parents = (List<String>) entity.getBody().get("parentEducationAgencyReference");
        	if ( null != parents ) {
	        	for ( String parent : parents ) {
	        		Entity parentEdorg = repo.findById(EntityNames.EDUCATION_ORGANIZATION, parent);
	        		if (isLEA(parentEdorg)) {
	        			return getTopLEAOfEdOrg(parentEdorg);
	        		}
	        	}
        	}
        }
        return entity;
    }

    /**
     * Get the collection of ed-orgs that will determine a user's security context
     *
     * @param principal
     * @return
     */
    public Collection<String> getUserEdOrgs(Entity principal) {
        return (isTeacher(principal)) ? getDirectSchools(principal) : getStaffEdOrgsAndChildren();
    }

    /**
     * Will go through staffEdorgAssociations that are current and get the descendant
     * edorgs that you have.
     *
     * @return a set of the edorgs you are associated to and their children.
     */
    public Set<String> getStaffEdOrgsAndChildren() {
        Set<String> edOrgLineage = getDirectEdorgs();
        return getEdorgDescendents(edOrgLineage);
    }

    public Set<String> getEdorgDescendents(Set<String> edOrgLineage) {
        edOrgLineage.addAll(getChildEdOrgs(edOrgLineage));
        return edOrgLineage;
    }

    public Set<String> getDelegatedEdorgDescendents() {

    	List<String> getSecurityEventDelegateEdOrg =  delegationUtil.getSecurityEventDelegateStateIds();
    	List<String> getSecurityEventDelegateEdOrgIds = delegationUtil.getSecurityEventDelegateEdOrgs();
    	Set<String> result = new HashSet<String>();
    	result.addAll(getSecurityEventDelegateEdOrg);
    	result.addAll(getChildEdOrgsName(getSecurityEventDelegateEdOrgIds));
    	return result;
    }

    /**
     * Calls date helper to check whether the specified field on the input body is expired.
     *
     * @param body
     *            Map representing entity's body.
     * @param fieldName
     *            Name of field to extract from entity's body.
     * @param useGracePeriod
     *            Flag indicating whether to allow for grace period when determining expiration.
     * @return True if field is expired, false otherwise.
     */
    public boolean isFieldExpired(Map<String, Object> body, String fieldName, boolean useGracePeriod) {
        return dateHelper.isFieldExpired(body, fieldName, useGracePeriod);
    }

    @SuppressWarnings("unchecked")
    public boolean isLEA(Entity entity) {
        // passing through
        return helper.isLEA(entity);
    }

    @SuppressWarnings("unchecked")
    public boolean isSchool(Entity entity) {
        // passing through
        return helper.isSchool(entity);
    }

    /**
     * Determines if the specified principal is of type 'teacher'.
     *
     * @param principal
     *            Principal to check type for.
     *
     * @return True if the principal is of type 'teacher', false otherwise.
     */
    private boolean isTeacher(Entity principal) {
        return EntityNames.TEACHER.equals(principal.getType());
    }

    /**
     * Determines if the specified principal is of type 'staff'.
     *
     * @param principal
     *            Principal to check type for.
     *
     * @return True if the principal is of type 'staff', false otherwise.
     */
    private boolean isStaff(Entity principal) {
        return EntityNames.STAFF.equals(principal.getType());
    }

    /**
     * Determines if the specified principal is of type 'student'.
     *
     * @param principal
     *            Principal to check type for.
     *
     * @return True if the principal is of type 'student', false otherwise.
     */
    private boolean isStudent(Entity principal) {
        return EntityNames.STUDENT.equals(principal.getType());
    }

    /**
     * Determines if the specified principal is of type 'parent'.
     *
     * @param principal
     *            Principal to check type for.
     *
     * @return True if the principal is of type 'parent', false otherwise.
     */
    private boolean isParent(Entity principal) {
        return EntityNames.PARENT.equals(principal.getType());
    }

    /**
     * Get directly associated education organizations for the authenticated principal.
     */
    public Set<String> getDirectEdorgs() {
        LOG.trace(">>>EdOrgHelper.getDirectEdorgs()");
        return getDirectEdorgs(SecurityUtil.getSLIPrincipal().getEntity());
    }

    /**
     * Get directly associated education organizations for the specified principal, filtered by
     * data ownership.
     */
    public Set<String> getDirectEdorgs(Entity principal) {
        LOG.trace(">>>getDirectEdorgs.getDirectEdorgs(using security principal)");
        LOG.trace("  principal: " + ToStringBuilder.reflectionToString(principal, ToStringStyle.MULTI_LINE_STYLE));
        return getEdOrgs(principal, true);
    }
    
    public List<String> getUserEdorgs(Entity principal) {
        Set<String> directEdOrgs = getDirectEdorgs(principal);
        List<String> userEdOrgs = new ArrayList<String>(getChildEdOrgs(directEdOrgs));
        userEdOrgs.addAll(directEdOrgs);
        return userEdOrgs;
    }
 
    /**
     * Get directly associated education organizations for the specified principal, not filtered by
     * data ownership.
     */
    public Set<String> locateDirectEdorgs(Entity principal) {
        return getEdOrgs(principal, false);
    }

    private Set<String> getEdOrgs(Entity principal, boolean filterByOwnership) {
        LOG.trace(">>>EdOrgHelper.getDirectEdorgs()");
        LOG.trace("  principal: " + ToStringBuilder.reflectionToString(principal, ToStringStyle.MULTI_LINE_STYLE));
        LOG.trace("  filterByOwnership: " + filterByOwnership);

        Set<String> result = new HashSet<String>();

        if (isStaff(principal) || isTeacher(principal)) {
            LOG.trace("  ...staff or teacher");
            result = getStaffDirectlyAssociatedEdorgs(principal, filterByOwnership);
        } else if (isStudent(principal)) {
            LOG.trace("  ...student");
            result = getStudentsCurrentAssociatedEdOrgs(Collections.singleton(principal.getEntityId()), filterByOwnership);
        } else if (isParent(principal)) {
            LOG.trace("  ...parent");
            SLIPrincipal prince = new SLIPrincipal();
            prince.setEntity(principal);
            prince.populateChildren(repo);
            result = getStudentsCurrentAssociatedEdOrgs(prince.getOwnedStudentIds(), false);
        }

        if (result == null) {
            LOG.debug("  ...method did not do anything so return empty set...");
        }

        return result;
    }

    /**
     * Get all the valid StaffEdorg associations.
     * @param staffId
     *      The staffId the SEOAs belong to.
     * @param filterByOwnership
     *      flag to check ownership
     * @return
     */
    public Set<Entity> locateValidSEOAs(String staffId, boolean filterByOwnership) {
        LOG.trace(">>>EdOrgHelper.locateValidSEOAs()");
        LOG.trace("  staffId: " + staffId);

        Set<Entity> validAssociations = new HashSet<Entity>();
        Iterable<Entity> associations = locateNonExpiredSEOAs(staffId);

        for (Entity association : associations) {
            LOG.trace("  ...association");
            if (!filterByOwnership || ownership.canAccess(association)) {
                validAssociations.add(association);
            }
        }
        LOG.trace("  ...count: " + validAssociations.size());
        return validAssociations;
    }

    /**
     * Get all non expired StaffEdorg associations.
     * @param staffId
     *      The staffId the SEOAs belong to.
     * @return
     */
    public Set<Entity> locateNonExpiredSEOAs(String staffId) {
        Set<Entity> validAssociations = new HashSet<Entity>();
        NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.STAFF_REFERENCE,
                NeutralCriteria.OPERATOR_EQUAL, staffId));
        Iterable<Entity> associations = repo.findAll(EntityNames.STAFF_ED_ORG_ASSOCIATION, basicQuery);
        for (Entity association : associations) {
            if (!dateHelper.isFieldExpired(association.getBody(), ParameterConstants.END_DATE, false)) {
                validAssociations.add(association);
            }
        }
        return validAssociations;
    }

    /**
     * Get current education organizations for the specified staff member.
     */
    private Set<String> getStaffDirectlyAssociatedEdorgs(Entity staff, boolean filterByOwnership) {
        LOG.trace(">>>EdOrgHelper.getStaffDirectlyAssociatedEdorgs()");
        LOG.trace("  staff: " + ToStringBuilder.reflectionToString(staff, ToStringStyle.MULTI_LINE_STYLE));
        LOG.trace("  filterByOwnership: " + filterByOwnership);
        Set<String> edorgs = new HashSet<String>();
        Iterable<Entity> associations = locateValidSEOAs(staff.getEntityId(), filterByOwnership);

        for (Entity association : associations) {
            LOG.trace("  association: " + ToStringBuilder.reflectionToString(association, ToStringStyle.MULTI_LINE_STYLE));
            edorgs.add((String) association.getBody().get(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE));
        }

        return edorgs;
    }

    /**
     * Get current education organizations for the specified students.
     */
    private Set<String> getStudentsCurrentAssociatedEdOrgs(Set<String> studentIds, boolean filterByOwnership) {
        Set<String> edOrgIds = new HashSet<String>();

        NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.STUDENT_ID,
                NeutralCriteria.CRITERIA_IN, studentIds));
        Iterable<Entity> associations = repo.findAll(EntityNames.STUDENT_SCHOOL_ASSOCIATION, basicQuery);

        if (associations != null) {
            for (Entity association : associations) {
                if (!filterByOwnership || ownership.canAccess(association)) {
                    if (!isFieldExpired(association.getBody(), ParameterConstants.EXIT_WITHDRAW_DATE, false)) {
                        edOrgIds.add((String) association.getBody().get(ParameterConstants.SCHOOL_ID));
                    }
                }
            }
        }
        return edOrgIds;
    }




    public Set<String> getEdOrgStateOrganizationIds(Set<String> edOrgIds) {
        NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.CRITERIA_IN, edOrgIds));
        Iterable<Entity> edOrgs = repo.findAll(EntityNames.EDUCATION_ORGANIZATION, basicQuery);
        Set<String> stateOrganizationIds = new HashSet<String>();
        for (Entity edOrg : edOrgs) {
            Map<String, Object> body = edOrg.getBody();
            if (body != null) {
                String stateId = (String) body.get("stateOrganizationId");
                if (stateId != null) {
                    stateOrganizationIds.add(stateId);
                }
            }
        }
        return stateOrganizationIds;
    }

    /**
     * Set the entity ownership validator (used primarily for unit testing).
     */
    protected void setEntityOwnershipValidator(EntityOwnershipValidator newOwner) {
        this.ownership = newOwner;

    }
}
