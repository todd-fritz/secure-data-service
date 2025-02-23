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

package org.slc.sli.api.security.context.validator;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Validates teacher's access to given teachers
 * 
 * @author dkornishev
 * 
 */
@Component
public class TransitiveTeacherToTeacherValidator extends AbstractContextValidator {

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return EntityNames.TEACHER.equals(entityType) && isTeacher() && isTransitive;
    }

    @Override
    public Set<String> validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(EntityNames.TEACHER, entityType, ids)) {
            return Collections.emptySet();
        }

        NeutralQuery nq = new NeutralQuery(new NeutralCriteria(ParameterConstants.STAFF_REFERENCE, "=", SecurityUtil
                .getSLIPrincipal().getEntity().getEntityId()));
        Iterable<Entity> tsa = getRepo().findAll(EntityNames.STAFF_ED_ORG_ASSOCIATION, nq);

        List<String> schools = new ArrayList<String>();
        for (Entity e : tsa) {
            if (!isFieldExpired(e.getBody(), ParameterConstants.END_DATE, false)) {
                schools.add((String) e.getBody().get(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE));
            }
        }

        nq = new NeutralQuery(new NeutralCriteria(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE, "in", schools));
        nq.addCriteria(new NeutralCriteria(ParameterConstants.STAFF_REFERENCE, "in", ids));

        tsa = getRepo().findAll(EntityNames.STAFF_ED_ORG_ASSOCIATION, nq);

        Set<String> validIds = new HashSet<String>();
        for (Entity e : tsa) {
            if (!isFieldExpired(e.getBody(), ParameterConstants.END_DATE, false)) {
                validIds.add(e.getBody().get(ParameterConstants.STAFF_REFERENCE).toString());
            }
        }

        validIds.add(SecurityUtil.getSLIPrincipal().getEntity().getEntityId());

       validIds.retainAll(ids);
        return validIds;
    }

    @Override
    public SecurityUtil.UserContext getContext() {
        return SecurityUtil.UserContext.TEACHER_CONTEXT;
    }
}
