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

import com.google.common.collect.Sets;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

/**
 * Validates Write context to a global course offering.
 */
@Component
public class GenericToGlobalCourseOfferingWriteValidator extends AbstractContextValidator {

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return isTransitive && EntityNames.COURSE_OFFERING.equals(entityType) && !isStudentOrParent();
    }

    @Override
    public Set<String> validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(EntityNames.COURSE_OFFERING, entityType, ids)) {
            return Collections.emptySet();
        }

        /*
         * Grab all the Course Offerings that are being requested AND contain a
         * reference to a school in your edorg hierarchy. Counts should be equal
         * if you can see all the course offerings you asked for
         */
        Set<String> edOrgLineage = getEdorgDescendents(getDirectEdorgs());
        NeutralQuery query = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.CRITERIA_IN, ids, false));
        query.addCriteria(new NeutralCriteria(ParameterConstants.SCHOOL_ID, NeutralCriteria.CRITERIA_IN, edOrgLineage));

        return Sets.newHashSet(getRepo().findAllIds(entityType, query));
    }

    @Override
    public SecurityUtil.UserContext getContext() {
        return SecurityUtil.UserContext.DUAL_CONTEXT;
    }
}
