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


package org.slc.sli.common.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Defines for entity names exposed by API.
 */
public final class EntityNames {
    public static final String ADMIN_DELEGATION = "adminDelegation";
    public static final String AGGREGATION = "aggregation";
    public static final String AGGREGATION_DEFINITION = "aggregationDefinition";
    public static final String APPLICATION = "application";
    public static final String APPLICATION_AUTHORIZATION = "applicationAuthorization";    
    public static final String ASSESSMENT = "assessment";
    public static final String ASSESSMENT_FAMILY = "assessmentFamily";
    public static final String ASSESSMENT_PERIOD_DESCRIPTOR = "assessmentPeriodDescriptor";
    public static final String ATTENDANCE = "attendance";
    public static final String COHORT = "cohort";
    public static final String COMPETENCY_LEVEL_DESCRIPTOR = "competencyLevelDescriptor";
    public static final String COMPETENCY_LEVEL_DESCRIPTOR_TYPE = "competencyLevelDescriptorType";
    public static final String COURSE = "course";
    public static final String COURSE_OFFERING = "courseOffering";
    public static final String COURSE_TRANSCRIPT = "courseTranscript";
    public static final String DISCIPLINE_INCIDENT = "disciplineIncident";
    public static final String DISCIPLINE_ACTION = "disciplineAction";
    public static final String EDUCATION_ORGANIZATION = "educationOrganization";
    public static final String GRADE = "grade";
    public static final String GRADEBOOK_ENTRY = "gradebookEntry";
    public static final String GRADING_PERIOD = "gradingPeriod";
    public static final String GRADUATION_PLAN = "graduationPlan";
    public static final String LEARNING_OBJECTIVE = "learningObjective";
    public static final String LEARNING_STANDARD = "learningStandard";
    public static final String OBJECTIVE_ASSESSMENT = "objectiveAssessment";
    public static final String PARENT = "parent";
    public static final String PROGRAM = "program";
    public static final String REALM = "realm";
    public static final String REPORT_CARD = "reportCard";
    public static final String SCHOOL = "school";
    public static final String SEARCH = "search";
    public static final String SECTION = "section";
    public static final String SECURITY_EVENT = "securityEvent";
    public static final String SESSION = "session";
    public static final String STAFF = "staff";
    public static final String STAFF_COHORT_ASSOCIATION = "staffCohortAssociation";
    public static final String STAFF_ED_ORG_ASSOCIATION = "staffEducationOrganizationAssociation";
    public static final String STAFF_PROGRAM_ASSOCIATION = "staffProgramAssociation";
    public static final String STUDENT = "student";
    public static final String STUDENT_ACADEMIC_RECORD = "studentAcademicRecord";
    public static final String STUDENT_ASSESSMENT = "studentAssessment";
    public static final String STUDENT_COHORT_ASSOCIATION = "studentCohortAssociation";
    public static final String STUDENT_COMPETENCY = "studentCompetency";
    public static final String STUDENT_COMPETENCY_OBJECTIVE = "studentCompetencyObjective";
    public static final String STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION = "studentDisciplineIncidentAssociation";
    public static final String STUDENT_OBJECTIVE_ASSESSMENT = "studentObjectiveAssessment";
    public static final String STUDENT_PROGRAM_ASSOCIATION = "studentProgramAssociation";
    public static final String STUDENT_GRADEBOOK_ENTRY = "studentGradebookEntry";
    public static final String STUDENT_SCHOOL_ASSOCIATION = "studentSchoolAssociation";
    public static final String STUDENT_SECTION_ASSOCIATION = "studentSectionAssociation";
    public static final String STUDENT_PARENT_ASSOCIATION = "studentParentAssociation";
    public static final String TEACHER = "teacher";
    public static final String TEACHER_SCHOOL_ASSOCIATION = "teacherSchoolAssociation";
    public static final String TEACHER_SECTION_ASSOCIATION = "teacherSectionAssociation";
    public static final String CALENDAR_DATE = "calendarDate";
    public static final String CLASS_PERIOD = "classPeriod";
    public static final String BELL_SCHEDULE = "bellSchedule";
    public static final String MEETING_TIME = "meetingTime";
                              
    public static final Set<String> PUBLIC_ENTITIES = new HashSet<String>(Arrays.asList(
            ASSESSMENT,
            ASSESSMENT_FAMILY,
            ASSESSMENT_PERIOD_DESCRIPTOR,
            COMPETENCY_LEVEL_DESCRIPTOR,
            COURSE,
            COURSE_OFFERING,
            EDUCATION_ORGANIZATION,
            GRADING_PERIOD,
            GRADUATION_PLAN,
            LEARNING_OBJECTIVE,
            LEARNING_STANDARD,
            OBJECTIVE_ASSESSMENT,
            PROGRAM,
            SCHOOL,
            SECTION,
            SESSION,
            STUDENT_COMPETENCY_OBJECTIVE,
            "localEducationAgency",
            "stateEducationAgency",
            CALENDAR_DATE,
            CLASS_PERIOD,
            BELL_SCHEDULE,
            MEETING_TIME
            ));


    public static boolean isPublic(String entityType) {
        return PUBLIC_ENTITIES.contains(entityType);
    }
    
    // there is no type safety, whoever calls this function should be
    // pretty sure that the entityType is valid
    public static String toResourceName(String entityType) {
        if (STAFF.equals(entityType) || COMPETENCY_LEVEL_DESCRIPTOR.equals(entityType)) {
            return entityType;
        }
    
        if (GRADEBOOK_ENTRY.equals(entityType) || STUDENT_GRADEBOOK_ENTRY.equals(entityType)
                || ASSESSMENT_FAMILY.equals(entityType) || STUDENT_COMPETENCY.equals(entityType)) {
            return entityType.replace("y", "ies");
        }
        
        return entityType + "s";
    }
}
