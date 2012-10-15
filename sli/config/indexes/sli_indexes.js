//
// Copyright 2012 Shared Learning Collaborative, LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//


//
// Run the indexing script with data in collections to test for errors.
//
// Indexing Gotchas:
// - Long index names
// - Parallel indexes: creating an index key with more than one field that is an array
// - Redundant indexes: {a,b,c} makes {a,b}, {a} redundant
//
// Known problem fields for parallel indexes: (no index key with more
// than one of these)
// These can be found in ComplexTypex.xsd
// xpath=//xs:element[@type="reference"][@maxOccurs="unbounded"]
// - *:metaData.edOrgs
// - *:metaData.teacherContext
// - cohort:body.programId
// - disciplineAction:body.disciplineIncidentId
// - disciplineAction:body.staffId
// - disciplineAction:body.studentId
// - learningObjective:body.learningStandards
// - reportCard:body.grades
// - reportCard:body.studentCompetencyId
// - section:body.programReference
// - section:body.assessmentReference
// - session:body.gradingPeriodreference
// - staffCohortAssociation:body.cohortId
// - staffCohortAssociation:body.staffId
// - staffProgramAssociation:body.programId
// - staffProgramAssociation:body.staffId
//


//auth, realm, application, tenant
db["adminDelegation"].ensureIndex({"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["adminDelegation"].ensureIndex({"body.localEdOrgId":1,"body.appApprovalEnabled":1});

db["application"].ensureIndex({"body.allowed_for_all_edorgs":1});
db["application"].ensureIndex({"body.authorized_ed_orgs":1});
db["application"].ensureIndex({"body.authorized_for_all_edorgs":1});
db["application"].ensureIndex({"body.bootstrap":1});
db["application"].ensureIndex({"body.client_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["application"].ensureIndex({"body.client_secret":1,"body.client_id":1});
db["application"].ensureIndex({"body.name":1});

db["applicationAuthorization"].ensureIndex({"body.appIds":1});
db["applicationAuthorization"].ensureIndex({"body.authId":1});
db["applicationAuthorization"].ensureIndex({"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["applicationAuthorization"].ensureIndex({"body.authId":1,"body.authType":1});

db["realm"].ensureIndex({"_id":1,"body.uniqueIdentifier":1});
db["realm"].ensureIndex({"body.idp.id":1});
db["realm"].ensureIndex({"body.uniqueIdentifier":1});

db["tenant"].ensureIndex({"body.landingZone.ingestionServer":1});
db["tenant"].ensureIndex({"body.landingZone.path":1});
db["tenant"].ensureIndex({"body.tenantId":1,"_id":1});
db["tenant"].ensureIndex({"body.tenantId":1,"body.landingZone.educationOrganization":1});
db["tenant"].ensureIndex({"body.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["tenant"].ensureIndex({"type":1});
db["tenant"].ensureIndex({"body.tenantId":1},{unique:true});

db["userSession"].ensureIndex({"body.appSession.code.expiration":1,"body.appSession.clientId":1,"body.appSession.verified":1,"body.appSession.code.value":1}, {"name":"codeExpiration_clientId_verified_codeValue"});
db["userSession"].ensureIndex({"body.appSession.samlId":1});
db["userSession"].ensureIndex({"body.appSession.token":1});
db["userSession"].ensureIndex({"body.expiration":1,"body.hardLogout":1,"body.appSession.token":1});
db["userSession"].ensureIndex({"body.principal.externalId":1});
db["userSession"].ensureIndex({"body.principal.realm":1});

