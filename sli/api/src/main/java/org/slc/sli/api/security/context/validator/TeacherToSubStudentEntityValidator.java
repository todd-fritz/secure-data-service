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

import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Validates a teacher accessing a set of entities that are directly associated to a student.
 * Currently supported entities are:
 * attendance,
 * course transcript,
 * discipline action,
 * student academic record,
 * student assessment association,
 * student discipline incident association,
 * student grade book entry,
 * student section association,
 * student school association.
 *
 * @author shalka
 */
@Component
public class TeacherToSubStudentEntityValidator extends AbstractContextValidator {

    private static final Logger LOG = LoggerFactory.getLogger(TeacherToSubStudentEntityValidator.class);

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    @Autowired
    private TeacherToStudentValidator validator;

    /**
     * Determines if the entity type is a sub-entity of student.
     */
    @Override
    public boolean canValidate(String entityType, boolean through) {
        return isTeacher() && isSubEntityOfStudent(entityType);
    }

    /**
     * Determines if the teacher can see the set of entities specified by 'ids'.
     */
    @SuppressWarnings("unchecked")
	@Override
    public Set<String> validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(SUB_ENTITIES_OF_STUDENT, entityType, ids)) {
            return Collections.emptySet();
        }
        
        Map<String, Set<String>> students = new HashMap<String, Set<String>>();
        NeutralQuery query = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID,
                NeutralCriteria.CRITERIA_IN, new ArrayList<String>(ids)));
        Iterable<Entity> entities = repo.findAll(entityType, query);

        for (Entity entity : entities) {
            Map<String, Object> body = entity.getBody();
            if (body.get(ParameterConstants.STUDENT_ID) instanceof String) {
                students = putStudents(Arrays.asList((String) body.get(ParameterConstants.STUDENT_ID)), entity.getEntityId(), students);
            } else if (body.get(ParameterConstants.STUDENT_ID) instanceof List) {
                students = putStudents((List<String>) body.get(ParameterConstants.STUDENT_ID), entity.getEntityId(), students);
            } else {
                //Student ID was not a string or a list of strings, this is unexpected
                LOG.warn("Possible Corrupt Data detected at "+entityType+"/"+entity.getEntityId());
            }
        }

        if (students.isEmpty()) {
            return Collections.EMPTY_SET;
        }

        Set<String> validStudents = validator.validate(EntityNames.STUDENT, students.keySet());

        return getValidIds(validStudents, students);
    }

    /**
     * Sets the paging repository delegate (for unit testing).
     *
     * @param repo
     *            Paging Delete Repository to use.
     */
    public void setRepo(PagingRepositoryDelegate<Entity> repo) {
        this.repo = repo;
    }

    /**
     * Sets the teacher to student validator (for unit testing).
     *
     * @param teacherToStudentValidator
     *            Teacher To Student Validator.
     */
    public void setTeacherToStudentValidator(TeacherToStudentValidator teacherToStudentValidator) {
        this.validator = teacherToStudentValidator;
    }

    private Map<String, Set<String>> putStudents(Collection<String> studentInfo, String entityId, Map<String, Set<String>> studentToEntities) {
        for (String student : studentInfo) {
            if (!studentToEntities.containsKey(student)) {
                studentToEntities.put(student, new HashSet<String>());
            }
            studentToEntities.get(student).add(entityId);
        }

        return studentToEntities;
    }

    @Override
    public SecurityUtil.UserContext getContext() {
        return SecurityUtil.UserContext.TEACHER_CONTEXT;
    }

}
