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

package org.slc.sli.bulk.extract.lea;

import java.io.File;
import java.security.PublicKey;
import java.util.Map;

import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.bulk.extract.util.SecurityEventUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

/**
 * Manufactures the various extractors.
 */
public class ExtractorFactory {

    public StudentExtractor buildStudentExtractor(EntityExtractor extractor, ExtractFileMap map,
            Repository<Entity> repo, EdOrgExtractHelper edOrgExtractHelper) {
        return new StudentExtractor(extractor, map, repo, new ExtractorHelper(edOrgExtractHelper), edOrgExtractHelper);
    }

    public EntityDatedExtract buildStudentAssessmentExtractor(EntityExtractor extractor, ExtractFileMap map,
            Repository<Entity> repo, EdOrgExtractHelper edOrgExtractHelper) {
        return new StudentAssessmentExtractor(extractor, map, repo, edOrgExtractHelper);
    }

    public YearlyTranscriptExtractor buildYearlyTranscriptExtractor(EntityExtractor extractor, ExtractFileMap map,
            Repository<Entity> repo, EdOrgExtractHelper edOrgExtractHelper) {
        return new YearlyTranscriptExtractor(extractor, map, repo, edOrgExtractHelper);
    }

    public EntityExtract buildParentExtractor(EntityExtractor extractor, ExtractFileMap map, Repository<Entity> repo, EdOrgExtractHelper edOrgExtractHelper) {
        return new ParentExtractor(extractor, map, repo, edOrgExtractHelper);
    }

    public StaffEdorgAssignmentExtractor buildStaffAssociationExtractor(EntityExtractor extractor, ExtractFileMap map,
            Repository<Entity> repo, EdOrgExtractHelper edOrgExtractHelper) {
        return new StaffEdorgAssignmentExtractor(extractor, map, repo, new ExtractorHelper(edOrgExtractHelper), edOrgExtractHelper);
    }

    public EntityDatedExtract buildStaffExtractor(EntityExtractor extractor, ExtractFileMap map, Repository<Entity> repo, EdOrgExtractHelper edOrgExtractHelper) {
        return new StaffExtractor(extractor, map, repo, edOrgExtractHelper);
    }

    public EntityDatedExtract buildTeacherSchoolAssociationExtractor(EntityExtractor extractor, ExtractFileMap map,
            Repository<Entity> repo, EdOrgExtractHelper edOrgExtractHelper) {
        return new TeacherSchoolAssociationExtractor(extractor, map, repo, edOrgExtractHelper);
    }

    public EntityDatedExtract buildAttendanceExtractor(EntityExtractor extractor, ExtractFileMap map,
            Repository<Entity> repo, EdOrgExtractHelper edOrgExtractHelper) {
        return new AttendanceExtractor(extractor, map, repo, edOrgExtractHelper);
    }

    public EntityDatedExtract buildStudentSchoolAssociationExtractor(EntityExtractor extractor, ExtractFileMap map,
            Repository<Entity> repo, EdOrgExtractHelper edOrgExtractHelper) {
        return new StudentSchoolAssociationExtractor(extractor, map, repo, edOrgExtractHelper);
    }

    public ExtractFile buildEdOrgExtractFile(String path, String edOrg, String archiveName,
            Map<String, PublicKey> appPublicKeys, SecurityEventUtil securityEventUtil) {
        File directory = new File(path, edOrg);
        directory.mkdirs();
        return new ExtractFile(directory, archiveName, appPublicKeys, securityEventUtil);
    }

    public EntityDatedExtract buildStaffCohortAssociationExtractor(EntityExtractor extractor, ExtractFileMap map,
            Repository<Entity> repo, EdOrgExtractHelper edOrgExtractHelper) {
        return new StaffCohortAssociationExtractor(extractor, map, repo, edOrgExtractHelper);
    }

    public SectionEmbeddedDocsExtractor buildSectionExtractor(EntityExtractor entityExtractor, ExtractFileMap extractFileMap, Repository<Entity> repository,
            EntityToEdOrgDateCache studentDatedCache, EdOrgExtractHelper edOrgExtractHelper, EntityToEdOrgDateCache staffDatedCache) {
        return new SectionEmbeddedDocsExtractor(entityExtractor, extractFileMap, repository, studentDatedCache, staffDatedCache);
    }

    public EntityDatedExtract buildStaffProgramAssociationExtractor(EntityExtractor extractor, ExtractFileMap map,
            Repository<Entity> repo, EdOrgExtractHelper edOrgExtractHelper) {
        return new StaffProgramAssociationExtractor(extractor, map, repo, edOrgExtractHelper);
    }

    public EntityDatedExtract buildCourseTranscriptExtractor(EntityExtractor extractor, ExtractFileMap map, Repository<Entity> repo,
            EntityToEdOrgDateCache studentDatedCache) {
        return new CourseTranscriptExtractor(extractor, map, repo, studentDatedCache);
    }

    public StudentGradebookEntryExtractor buildStudentGradebookEntryExtractor(EntityExtractor extractor, ExtractFileMap map,
            Repository<Entity> repo, EdOrgExtractHelper edOrgExtractHelper) {
        return new StudentGradebookEntryExtractor(extractor, map, repo, edOrgExtractHelper);
    }

    public EntityDatedExtract buildStudentCompetencyExtractor(EntityExtractor entityExtractor, ExtractFileMap extractFileMap, Repository<Entity> repository) {
        return new StudentCompetencyExtractor(entityExtractor, extractFileMap, repository);
    }

    public EntityDatedExtract buildDisciplineExtractor(EntityExtractor entityExtractor, ExtractFileMap extractFileMap, Repository<Entity> repository,
            EntityToEdOrgDateCache entityCache) {
        return new DisciplineExtractor(entityExtractor, extractFileMap, repository, entityCache);
    }
}
