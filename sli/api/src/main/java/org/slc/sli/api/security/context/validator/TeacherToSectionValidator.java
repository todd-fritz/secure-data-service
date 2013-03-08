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

package org.slc.sli.api.security.context.validator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Can see the sections that I teach.
 * 
 */
@Component
public class TeacherToSectionValidator extends AbstractContextValidator {
    
    @Autowired
    private PagingRepositoryDelegate<Entity> repo;
    
    @Autowired
    private DateHelper dateHelper;

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return isTeacher() && EntityNames.SECTION.equals(entityType) && !isTransitive;
    }
    
    @Override
    public boolean validate(String entityType, Set<String> ids) {
        if (!areParametersValid(EntityNames.SECTION, entityType, ids)) {
            return false;
        }
        Set<String> toValidateList = new HashSet<String>(ids);
        NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.SECTION_ID,
                NeutralCriteria.CRITERIA_IN, ids));
        NeutralCriteria staffCriteria = new NeutralCriteria(ParameterConstants.TEACHER_ID,
                NeutralCriteria.OPERATOR_EQUAL, SecurityUtil.getSLIPrincipal().getEntity().getEntityId());
        basicQuery.addCriteria(staffCriteria);
        Iterable<Entity> assocs = repo.findAll(EntityNames.TEACHER_SECTION_ASSOCIATION, basicQuery);
        for (Entity entity : assocs) {
        	if (!dateHelper.isFieldExpired(entity.getBody(), ParameterConstants.END_DATE)) {
        		toValidateList.remove((String) entity.getBody().get(ParameterConstants.SECTION_ID));
        	}
        }
        
        return toValidateList.isEmpty();
    }
    
}
