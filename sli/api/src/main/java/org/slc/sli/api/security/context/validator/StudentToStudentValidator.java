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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.slc.sli.api.util.SecurityUtil;
import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.EntityNames;

/**
 * User: dkornishev
 */
@Component
public class StudentToStudentValidator extends BasicValidator {

    public StudentToStudentValidator() {
        super(false, Arrays.asList(EntityNames.STUDENT, EntityNames.PARENT), EntityNames.STUDENT);
    }

    @Override
    protected Set<String> doValidate(Set<String> ids, String entityType) {
        Set<String> validIds = new HashSet<String>(ids);
        validIds.retainAll(SecurityUtil.getSLIPrincipal().getOwnedStudentIds());
        return validIds;
    }

}
