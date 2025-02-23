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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.context.EntityOwnershipValidator;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.security.roles.SecureRoleRightAccessImpl;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.api.util.RequestUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * Utility class for constructing ed-org hierarchies for use in test classes.
 *
 *
 * @author kmyers
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class EdOrgHelperTest {

    @Autowired
    SecurityContextInjector injector;

    @Autowired
    EdOrgHelper helper;

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    private EntityOwnershipValidator ownership;

    /*
     *  Create an EdOrg Hierarchy that looks like
     *  sea1 --> staff4
     *   | \
     *   |  lea4
     *  lea1 --> staff1
     *   |  \
     *   |   school1 --> teacher1, student1
     *  lea2 --> staff2
     *   |  \
     *   |   school2 --> teacher2, student2
     *  lea3 --> staff2
     *   |  \
     *   |   school3 --> teacher3, student3
     */

    Entity staff1 = null;   //directly associated with lea1
    Entity staff2 = null;   //directly associated with lea2
    Entity staff3 = null;   //directly associated with lea3
    Entity staff4 = null;   //directly associated with sea1
    Entity sea1 = null;
    Entity lea1 = null;
    Entity lea2 = null;
    Entity lea3 = null;
    Entity lea4 = null;
    Entity school1 = null;
    Entity school2 = null;
    Entity school3 = null;
    Entity teacher1 = null;
    Entity teacher2 = null;
    Entity teacher3 = null;
    Entity student1 = null;
    Entity student2 = null;
    Entity student3 = null;

    /*
     * leaCycle1 <-- leaCycle2 <-- leaCycle3
     *     |___________________________^
     */
    Entity seaCycle = null;
    Entity leaCycle1 = null;
    Entity leaCycle2 = null;
    Entity leaCycle3 = null;

    private void setContext(Entity actor, List<String> roles) {
        String user = "fake actor";
        String fullName = "Fake Actor";
        injector.setCustomContext(user, fullName, "MERPREALM", roles, actor, "111");
    }

    @Before
    public void setup() {
        ownership = Mockito.mock(EntityOwnershipValidator.class);
        Mockito.when(ownership.canAccess((Entity) Mockito.any())).thenReturn(true);
        helper.setEntityOwnershipValidator(ownership);

        repo.deleteAll(EntityNames.EDUCATION_ORGANIZATION, null);
        repo.deleteAll(EntityNames.STAFF, null);
        repo.deleteAll(EntityNames.STUDENT, null);
        repo.deleteAll(EntityNames.STUDENT_SCHOOL_ASSOCIATION, null);

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("staffUniqueStateId", "staff1");
        staff1 = repo.create(EntityNames.STAFF, body);

        body = new HashMap<String, Object>();
        body.put("staffUniqueStateId", "staff2");
        staff2 = repo.create(EntityNames.STAFF, body);

        body = new HashMap<String, Object>();
        body.put("staffUniqueStateId", "staff3");
        staff3 = repo.create(EntityNames.STAFF, body);

        body = new HashMap<String, Object>();
        body.put("staffUniqueStateId", "staff4");
        staff4 = repo.create(EntityNames.STAFF, body);

        body = new HashMap<String, Object>();
        teacher1 = repo.create(EntityNames.TEACHER, body);

        body = new HashMap<String, Object>();
        teacher2 = repo.create(EntityNames.TEACHER, body);

        body = new HashMap<String, Object>();
        teacher3 = repo.create(EntityNames.TEACHER, body);

        body = new HashMap<String, Object>();
        body.put("studentUniqueStateId", "student1");
        student1 = repo.create(EntityNames.STUDENT, body);

        body = new HashMap<String, Object>();
        body.put("studentUniqueStateId", "student2");
        student2 = repo.create(EntityNames.STUDENT, body);

        body = new HashMap<String, Object>();
        body.put("studentUniqueStateId", "student3");
        student3 = repo.create(EntityNames.STUDENT, body);

        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("State Education Agency"));
        body.put(ParameterConstants.STATE_ORGANIZATION_ID, "sea1");
        sea1 = repo.create(EntityNames.EDUCATION_ORGANIZATION, body);

        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("Local Education Agency"));
        body.put("parentEducationAgencyReference", Arrays.asList(sea1.getEntityId()));
        body.put(ParameterConstants.STATE_ORGANIZATION_ID, "lea1");
        lea1 = repo.create(EntityNames.EDUCATION_ORGANIZATION, body);

        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("Local Education Agency"));
        body.put("parentEducationAgencyReference", Arrays.asList(lea1.getEntityId()));
        body.put(ParameterConstants.STATE_ORGANIZATION_ID, "lea2");
        lea2 = repo.create(EntityNames.EDUCATION_ORGANIZATION, body);

        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("Local Education Agency"));
        body.put("parentEducationAgencyReference", Arrays.asList(lea2.getEntityId()));
        body.put(ParameterConstants.STATE_ORGANIZATION_ID, "lea3");
        lea3 = repo.create(EntityNames.EDUCATION_ORGANIZATION, body);

        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("Local Education Agency"));
        body.put("parentEducationAgencyReference", Arrays.asList(sea1.getEntityId()));
        body.put(ParameterConstants.STATE_ORGANIZATION_ID, "lea4");
        lea4 = repo.create(EntityNames.EDUCATION_ORGANIZATION, body);

        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("School"));
        body.put("parentEducationAgencyReference", Arrays.asList(lea1.getEntityId()));
        body.put(ParameterConstants.STATE_ORGANIZATION_ID, "school1");
        Map<String, Object> metadata = new HashMap<String, Object>();
        metadata.put("edOrgs", Arrays.asList(lea1.getEntityId()));
        school1 = repo.create(EntityNames.EDUCATION_ORGANIZATION, body, metadata, EntityNames.EDUCATION_ORGANIZATION);

        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("School"));
        body.put("parentEducationAgencyReference", Arrays.asList(lea2.getEntityId()));
        body.put(ParameterConstants.STATE_ORGANIZATION_ID, "school2");
        school2 = repo.create(EntityNames.EDUCATION_ORGANIZATION, body);

        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("School"));
        body.put("parentEducationAgencyReference", Arrays.asList(lea3.getEntityId()));
        body.put(ParameterConstants.STATE_ORGANIZATION_ID, "school3");
        school3 = repo.create(EntityNames.EDUCATION_ORGANIZATION, body);

        body = new HashMap<String, Object>();
        body.put("educationOrganizationReference", lea1.getEntityId());
        body.put("staffReference", staff1.getEntityId());
        repo.create(EntityNames.STAFF_ED_ORG_ASSOCIATION, body);

        body = new HashMap<String, Object>();
        body.put("educationOrganizationReference", lea2.getEntityId());
        body.put("staffReference", staff2.getEntityId());
        repo.create(EntityNames.STAFF_ED_ORG_ASSOCIATION, body);

        body = new HashMap<String, Object>();
        body.put("educationOrganizationReference", lea3.getEntityId());
        body.put("staffReference", staff3.getEntityId());
        repo.create(EntityNames.STAFF_ED_ORG_ASSOCIATION, body);

        body = new HashMap<String, Object>();
        body.put("educationOrganizationReference", sea1.getEntityId());
        body.put("staffReference", staff4.getEntityId());
        repo.create(EntityNames.STAFF_ED_ORG_ASSOCIATION, body);

        body = new HashMap<String, Object>();
        body.put(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE, school1.getEntityId());
        body.put(ParameterConstants.STAFF_REFERENCE, teacher1.getEntityId());
        repo.create(EntityNames.STAFF_ED_ORG_ASSOCIATION, body);

        body = new HashMap<String, Object>();
        body.put(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE, school2.getEntityId());
        body.put(ParameterConstants.STAFF_REFERENCE, teacher2.getEntityId());
        repo.create(EntityNames.STAFF_ED_ORG_ASSOCIATION, body);

        body = new HashMap<String, Object>();
        body.put(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE, school3.getEntityId());
        body.put(ParameterConstants.STAFF_REFERENCE, teacher3.getEntityId());
        repo.create(EntityNames.STAFF_ED_ORG_ASSOCIATION, body);

        body = new HashMap<String, Object>();
        body.put(ParameterConstants.STUDENT_ID, student1.getEntityId());
        body.put(ParameterConstants.SCHOOL_ID, school1.getEntityId());
        body.put("entryDate", new DateTime().minusDays(3));
        repo.create(EntityNames.STUDENT_SCHOOL_ASSOCIATION, body);

        body = new HashMap<String, Object>();
        body.put(ParameterConstants.STUDENT_ID, student2.getEntityId());
        body.put(ParameterConstants.SCHOOL_ID, school2.getEntityId());
        body.put("entryDate", new DateTime().minusDays(3));
        repo.create(EntityNames.STUDENT_SCHOOL_ASSOCIATION, body);

        body = new HashMap<String, Object>();
        body.put(ParameterConstants.STUDENT_ID, student3.getEntityId());
        body.put(ParameterConstants.SCHOOL_ID, school3.getEntityId());
        body.put("entryDate", new DateTime().minusDays(3));
        repo.create(EntityNames.STUDENT_SCHOOL_ASSOCIATION, body);

        // entities for lea cycle
        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("Local Education Agency"));
        body.put(ParameterConstants.STATE_ORGANIZATION_ID, "leaCycle1");
        leaCycle1 = repo.create(EntityNames.EDUCATION_ORGANIZATION, body);

        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("Local Education Agency"));
        body.put("parentEducationAgencyReference", Arrays.asList(leaCycle1.getEntityId()));
        body.put(ParameterConstants.STATE_ORGANIZATION_ID, "leaCycle2");
        leaCycle2 = repo.create(EntityNames.EDUCATION_ORGANIZATION, body);

        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("Local Education Agency"));
        body.put("parentEducationAgencyReference", Arrays.asList(leaCycle2.getEntityId()));
        body.put(ParameterConstants.STATE_ORGANIZATION_ID, "leaCycle3");
        leaCycle3 = repo.create(EntityNames.EDUCATION_ORGANIZATION, body);

        // update the parent ref now that we know the id
        leaCycle1.getBody().put("parentEducationAgencyReference", Arrays.asList(leaCycle3.getEntityId()));
        repo.update(EntityNames.EDUCATION_ORGANIZATION, leaCycle1, false);
    }


    @Test
    public void testStaff1() {
        setContext(staff1, Arrays.asList(SecureRoleRightAccessImpl.IT_ADMINISTRATOR));
        List<String> leas = helper.getDistricts(staff1);
        assertTrue("staff1 must see lea1", leas.contains(lea1.getEntityId()));
        assertEquals("staff1 must only see one district", 1, leas.size());
    }

    @Test
    public void testStaff2() {
        setContext(staff2, Arrays.asList(SecureRoleRightAccessImpl.IT_ADMINISTRATOR));
        List<String> leas = helper.getDistricts(staff2);
        assertTrue("staff2 must see lea1", leas.contains(lea1.getEntityId()));
        assertEquals("staff2 must only see one district", 1, leas.size());
    }

    @Test
    public void testStaff3() {
        setContext(staff3, Arrays.asList(SecureRoleRightAccessImpl.IT_ADMINISTRATOR));
        List<String> leas = helper.getDistricts(staff3);
        assertTrue("staff3 must see lea1", leas.contains(lea1.getEntityId()));
        assertEquals("staff3 must only see one district", 1, leas.size());
    }

    @Test
    public void testStaff4() {
        setContext(staff4, Arrays.asList(SecureRoleRightAccessImpl.IT_ADMINISTRATOR));
        List<String> leas = helper.getDistricts(staff4);
        assertTrue("staff4 must see lea1", leas.contains(lea1.getEntityId()));
        assertTrue("staff4 must see lea4", leas.contains(lea4.getEntityId()));
        assertEquals("staff4 must only see two districts", 2, leas.size());
    }

    @Test
    public void testTeacher1() {
        setContext(teacher1, Arrays.asList(SecureRoleRightAccessImpl.EDUCATOR));
        List<String> leas = helper.getDistricts(teacher1);
        assertTrue("teacher1 must see lea1", leas.contains(lea1.getEntityId()));
        assertEquals("teacher1 must only see one district", 1, leas.size());
    }

    @Test
    public void testTeacher2() {
        setContext(teacher2, Arrays.asList(SecureRoleRightAccessImpl.EDUCATOR));
        List<String> leas = helper.getDistricts(teacher2);
        assertTrue("teacher2 must see lea1", leas.contains(lea1.getEntityId()));
        assertEquals("teacher2 must only see one district", 1, leas.size());
    }

    @Test
    public void testTeacher3() {
        setContext(teacher3, Arrays.asList(SecureRoleRightAccessImpl.EDUCATOR));
        List<String> leas = helper.getDistricts(teacher3);
        assertTrue("teacher3 must see lea1", leas.contains(lea1.getEntityId()));
        assertEquals("teacher3 must only see one district", 1, leas.size());
    }

    @Test
    public void testTeacher3LocateSEOAs() {

        NeutralQuery staffQuery = new NeutralQuery();
        staffQuery.addCriteria(new NeutralCriteria(ParameterConstants.STAFF_UNIQUE_STATE_ID, NeutralCriteria.OPERATOR_EQUAL, teacher3.getEntityId()));
        repo.deleteAll(EntityNames.STAFF_ED_ORG_ASSOCIATION, staffQuery);
        Date now = new Date();
        setupSEOAs(repo, now.getTime(), teacher3, school3.getEntityId());

        Set<Entity> associations = helper.locateNonExpiredSEOAs(teacher3.getEntityId());
        assertTrue("teacher3 should have 3 valid associations", associations.size() == 3);
    }

    @Test
    public void testParents() {
        RequestUtil.setCurrentRequestId();
        List<String> edorgs = helper.getParentEdOrgs(school3);
        assertTrue(edorgs.contains(sea1.getEntityId()));
        assertTrue(edorgs.contains(lea1.getEntityId()));
        assertTrue(edorgs.contains(lea2.getEntityId()));
        assertTrue(edorgs.contains(lea3.getEntityId()));
        assertFalse(edorgs.contains(school1.getEntityId()));
        assertFalse(edorgs.contains(school2.getEntityId()));
        assertFalse(edorgs.contains(school3.getEntityId()));
        assertEquals(4, edorgs.size());
    }

    @Test
    public void testParentsWithCycle() {
        RequestUtil.setCurrentRequestId();
        List<String> edorgs = helper.getParentEdOrgs(leaCycle1);
        assertFalse("leaCycle1 should not be a child of leaCycle1", edorgs.contains(leaCycle1.getEntityId()));
        assertTrue("leaCycle2 should be a child of leaCycle1", edorgs.contains(leaCycle2.getEntityId()));
        assertTrue("leaCycle3 should be a child of leaCycle1", edorgs.contains(leaCycle3.getEntityId()));
        assertEquals(2, edorgs.size());
    }

    @Test
    public void testParentsOfSea() {
        RequestUtil.setCurrentRequestId();
        List<String> edorgs = helper.getParentEdOrgs(sea1);
        assertEquals(0, edorgs.size());
    }

    @Test
    public void testStudent1() {
        Set<String> edorgs = helper.getDirectEdorgs(student1);
        assertEquals(1, edorgs.size());
        assertTrue("student1 should see school1", edorgs.contains(school1.getEntityId()));
        assertFalse("student1 should not see school2", edorgs.contains(school2.getEntityId()));
        assertFalse("student1 should not see school3", edorgs.contains(school3.getEntityId()));
    }

    @Test
    public void testStudent2() {
        Set<String> edorgs = helper.getDirectEdorgs(student2);
        assertEquals(1, edorgs.size());
        assertFalse("student2 should not see school1", edorgs.contains(school1.getEntityId()));
        assertTrue("student2 should see school2", edorgs.contains(school2.getEntityId()));
        assertFalse("student2 should not see school3", edorgs.contains(school3.getEntityId()));
    }

    @Test
    public void testStudent3() {
        Set<String> edorgs = helper.getDirectEdorgs(student3);
        assertEquals(1, edorgs.size());
        assertFalse("student3 should not see school1", edorgs.contains(school1.getEntityId()));
        assertFalse("student3 should not see school2", edorgs.contains(school2.getEntityId()));
        assertTrue("student3 should see school3", edorgs.contains(school3.getEntityId()));
    }

    @Test
    public void testHeirarchicalEdorgs() {
        RequestUtil.setCurrentRequestId();
        List<String> edorgs = helper.getParentEdOrgs(school1);
        assertEquals(2, edorgs.size());
        assertFalse("school1 should not see lea3", edorgs.contains(lea3.getEntityId()));
        assertFalse("school1 should not see lea2", edorgs.contains(lea2.getEntityId()));
        assertTrue("school1 should see lea1", edorgs.contains(lea1.getEntityId()));
        assertTrue("school1 should see sea1", edorgs.contains(sea1.getEntityId()));

    }

    @Test
    public void testGetChildLeaOfEdorgs() {
        List<String> edorgs = helper.getDirectChildLEAsOfEdOrg(lea2);
        assertEquals(1, edorgs.size());
        assertFalse("lea1 should not be a child of lea2", edorgs.contains(lea1.getEntityId()));
        assertFalse("lea2 should not be a child of lea2", edorgs.contains(lea2.getEntityId()));
        assertTrue("lea3 should be a child of lea2", edorgs.contains(lea3.getEntityId()));
    }

    @Test
    public void testGetAllChildLeaOfEdorgs() {
        Set<String> edorgs = helper.getAllChildLEAsOfEdOrg(sea1);
        assertEquals(4, edorgs.size());
        assertTrue("lea1 should be a child of sea1", edorgs.contains(lea1.getEntityId()));
        assertTrue("lea2 should be a child of sea1", edorgs.contains(lea2.getEntityId()));
        assertTrue("lea3 should be a child of sea1", edorgs.contains(lea3.getEntityId()));
        assertTrue("lea4 should be a child of sea1", edorgs.contains(lea4.getEntityId()));
    }

    @Test
    public void testGetAllChildLeaOfEdorgsWithCycle() {
        Set<String> edorgs = helper.getAllChildLEAsOfEdOrg(leaCycle1);
        assertEquals(2, edorgs.size());
        assertFalse("leaCycle1 should not be a child of leaCycle1", edorgs.contains(leaCycle1.getEntityId()));
        assertTrue("leaCycle2 should be a child of leaCycle1", edorgs.contains(leaCycle2.getEntityId()));
        assertTrue("leaCycle3 should be a child of leaCycle1", edorgs.contains(leaCycle3.getEntityId()));
    }

    Map<String, Object> createSEOA(String edorg, String staff, String endDate) {
        Map<String, Object> seoa = new HashMap<String, Object>();
        seoa.put(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE, edorg);
        seoa.put(ParameterConstants.STAFF_REFERENCE, staff);
        if(endDate != null) {
            seoa.put(ParameterConstants.STAFF_EDORG_ASSOC_END_DATE, endDate);
        }

        return seoa;
    }

    @Test
    public void testGetChildEdOrgsNameWithoutParents() {

        Set<String> children = helper.getChildEdOrgsName(Arrays.asList(lea2.getEntityId(), leaCycle1.getEntityId()));

        assertTrue("expected to see school2 in the list of children",
                children.contains(school2.getBody().get(ParameterConstants.STATE_ORGANIZATION_ID)));
        assertTrue("expected to see lea3 in the list of children",
                children.contains(lea3.getBody().get(ParameterConstants.STATE_ORGANIZATION_ID)));
        assertTrue("expected to see school3 in the list of children",
                children.contains(school3.getBody().get(ParameterConstants.STATE_ORGANIZATION_ID)));
        assertTrue("expected to see leaCycle2 in the list of children",
                children.contains(leaCycle2.getBody().get(ParameterConstants.STATE_ORGANIZATION_ID)));
        assertTrue("expected to see leaCycle3 in the list of children",
                children.contains(leaCycle3.getBody().get(ParameterConstants.STATE_ORGANIZATION_ID)));

        assertEquals(5, children.size());
    }

    @Test
    public void testGetChildEdOrgsNameWithParents() {
        Set<String> children = helper.getChildEdOrgsName(Arrays.asList(lea2.getEntityId(), leaCycle1.getEntityId()),
                true);

        assertTrue("expected to see lea2 in the list of children",
                children.contains(lea2.getBody().get(ParameterConstants.STATE_ORGANIZATION_ID)));
        assertTrue("expected to see leaCycle1 in the list of children",
                children.contains(leaCycle1.getBody().get(ParameterConstants.STATE_ORGANIZATION_ID)));
        assertTrue("expected to see school2 in the list of children",
                children.contains(school2.getBody().get(ParameterConstants.STATE_ORGANIZATION_ID)));
        assertTrue("expected to see lea3 in the list of children",
                children.contains(lea3.getBody().get(ParameterConstants.STATE_ORGANIZATION_ID)));
        assertTrue("expected to see school3 in the list of children",
                children.contains(school3.getBody().get(ParameterConstants.STATE_ORGANIZATION_ID)));
        assertTrue("expected to see leaCycle2 in the list of children",
                children.contains(leaCycle2.getBody().get(ParameterConstants.STATE_ORGANIZATION_ID)));
        assertTrue("expected to see leaCycle3 in the list of children",
                children.contains(leaCycle3.getBody().get(ParameterConstants.STATE_ORGANIZATION_ID)));

        assertEquals(7, children.size());
    }


    private void setupSEOAs(Repository repo, long time, Entity staffEntity, String edorg) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        Date date = new Date(time);
        long milSecInAYear = 31557600000L;
        long milSecInADay = 86400000L;

        String dateString = df.format(date);

        repo.create(EntityNames.STAFF_ED_ORG_ASSOCIATION, createSEOA(edorg, staffEntity.getEntityId(), dateString));

        date = new Date(time - milSecInAYear);
        dateString = df.format(date);
        repo.create(EntityNames.STAFF_ED_ORG_ASSOCIATION, createSEOA(edorg, staffEntity.getEntityId(), dateString));

        date = new Date(time + milSecInADay);
        dateString = df.format(date);
        repo.create(EntityNames.STAFF_ED_ORG_ASSOCIATION, createSEOA( edorg, staffEntity.getEntityId(), dateString));

        date = new Date(time + milSecInAYear);
        dateString = df.format(date);
        repo.create(EntityNames.STAFF_ED_ORG_ASSOCIATION, createSEOA(edorg, staffEntity.getEntityId(), dateString));

        repo.create(EntityNames.STAFF_ED_ORG_ASSOCIATION, createSEOA(edorg, staffEntity.getEntityId(), null));

    }

}
