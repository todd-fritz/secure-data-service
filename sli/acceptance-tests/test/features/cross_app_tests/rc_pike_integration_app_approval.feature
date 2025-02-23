@RALLY_US4835
@rc
Feature:  RC Integration Tests

Background:
Given I have an open web browser

Scenario: Realm Admin Logins to create realm
When I navigate to the Portal home page
When I see the realm selector I authenticate to "inBloom"
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "<SECONDARY_EMAIL>" "<SECONDARY_EMAIL_PASS>" for the "Simple" login page
Then I should be on Portal home page
And under System Tools, I click on "Realm Management"
And I should see that I am on the new realm page
And all of the input fields should be blank
And I should enter "Daybreak Test Realm" into the Display Name field
And I enter "<CI_IDP_Redirect_URL>" in the IDP URL field
And I enter "<CI_IDP_Redirect_URL>" in the Redirect Endpoint field
And I should enter "RC-IL-Daybreak" into Realm Identifier
And I should click the "Save" button
And I should receive a notice that the realm was successfully "created"
Then I see the realms for "Daybreak School District 4529 (IL-DAYBREAK)"
And the realm "Daybreak Test Realm" will exist

#And I click on log out

Scenario: User cannot access Bootstrapped Apps before approval
When I navigate to the Portal home page
When I selected the realm "Daybreak Test Realm"
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "jstevenson" "jstevenson1234" for the "Simple" login page    
Then I should be on Portal home page
Then I should not see "inBloom Dashboards"
And I should not see "inBloom Data Browser"
#And I click on log out

Scenario:  LEA gives IT Admins bulk extract permissions
    When I navigate to the Portal home page
    When I see the realm selector I authenticate to "inBloom"
     And I was redirected to the "Simple" IDP Login page
    When I submit the credentials "<SECONDARY_EMAIL>" "<SECONDARY_EMAIL_PASS>" for the "Simple" login page
    Then I should be on Portal home page
     And under System Tools, I click on "Custom Roles"
     And I edit the group "IT Administrator"
    When I add the right "BULK_EXTRACT" to the group "IT Administrator"
     And I hit the save button
    Then I am no longer in edit mode
     And the group "IT Administrator" contains the "right" rights "Bulk IT Administrator"
#    And I click on log out

Scenario:  SEA approves Dashboard, Databrowser and Bulk Extract 2 End Applications
  When I navigate to the Portal home page
  When I see the realm selector I authenticate to "Daybreak Test Realm"
   And I was redirected to the "Simple" IDP Login page
  When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
  Then I should be on Portal home page
   And under System Tools, I click on "Application Authorizations"
  Then I am redirected to the Admin Application Authorization Tool
#Authorize the Dashboard
   And I see an application "inBloom Dashboards" in the table
   And in Status it says "Not Approved"
   And I click on the "Edit Authorizations" button next to it
   And I expand all nodes
   And I authorize the educationalOrganization "Standard State Education Agency" in the production tenant
   And I click Update
   And I see an application "inBloom Dashboards" in the table
   And in Status it says "199 EdOrg(s)"
  Then there are "199" edOrgs for the "inBloom Dashboards" application in the production applicationAuthorization collection
#Authorize the Databrowser
   And I see an application "inBloom Data Browser" in the table
   And in Status it says "Not Approved"
   And I click on the "Edit Authorizations" button next to it
   And I expand all nodes
   And I authorize the educationalOrganization "Standard State Education Agency" in the production tenant
   And I click Update
   And I see an application "inBloom Data Browser" in the table
   And in Status it says "199 EdOrg(s)"
  Then there are "199" edOrgs for the "inBloom Data Browser" application in the production applicationAuthorization collection
  #Authorize the New Installed App
   And I see an application "Bulk Extract 2 End" in the table
   And in Status it says "Not Approved"
   And I click on the "Edit Authorizations" button next to it
   And I deselect hierarchical mode
   And I expand all nodes
   And I authorize the educationalOrganization "Standard State Education Agency" in the production tenant
   And I authorize the educationalOrganization "Daybreak School District 4529" in the production tenant
   And I authorize the educationalOrganization "IL-CHARTER-SCHOOL" in the production tenant
   And I click Update
   And I see an application "Bulk Extract 2 End" in the table
   And in Status it says "3 EdOrg(s)"
  Then there are "3" edOrgs for the "Bulk Extract 2 End" application in the production applicationAuthorization collection

Scenario: SEA admin makes an api call to PATCH the SEA
  Given the pre-existing bulk extract testing app key has been created
   When I navigate to the API authorization endpoint with my client ID
    And I select "Daybreak Test Realm" and click go
    And I was redirected to the "Simple" IDP Login page
   When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
   Then I should receive a json response containing my authorization code
   When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
   Then I should receive a json response containing my authorization token
    And I get the id for the edorg "STANDARD-SEA"
   When I PATCH the name for the current edorg entity to Education Agency for RC Tests
   Then I should receive a return code of 204

Scenario: Sessions are shared between Dashboard and Databrowser apps
  When I navigate to the Portal home page
  When I select "Daybreak Test Realm" and click go
   And I was redirected to the "Simple" IDP Login page
  When I submit the credentials "jstevenson" "jstevenson1234" for the "Simple" login page
  Then I should be on Portal home page
  When I navigate to the dashboard page
   And I am redirected to the dashboard home page
  When I navigate to the databrowser page
  Then I do not see any login pages
  Then I am redirected to the databrowser home page
#   And I click on log out
#   And I should forced to reauthenticate to gain access
#  When I navigate to the dashboard home page
#  Then I should forced to reauthenticate to gain access

Scenario: User sees non-installed Developer App 
  When I navigate to the Portal home page
  When I selected the realm "Daybreak Test Realm"
   And I was redirected to the "Simple" IDP Login page
  When I submit the credentials "linda.kim" "linda.kim1234" for the "Simple" login page
  Then I should be on Portal home page
   And under My Applications, I see the following apps: "inBloom Dashboards"

Scenario: App developer creates new Bulk Extract App
    When I navigate to the Portal home page
    When I see the realm selector I authenticate to the developer realm
    And I was redirected to the "Simple" IDP Login page
    When I submit the credentials "<DEVELOPER_EMAIL>" "<DEVELOPER_EMAIL_PASS>" for the "Simple" login page
    Then I should be on Portal home page
    And under System Tools, I click on "Register Application"
    Then I am redirected to the Application Registration Tool page
    And I have clicked to the button New
    And I am redirected to a new application page
    When I entered the name "BulkExtractApp2" into the field titled "Name"
    And I entered the name "Best.  Description.  Ever." into the field titled "Description"
    And I entered the name "0.0" into the field titled "Version"
    And I make my app an installed app
    And I check Bulk Extract
    And I click on the button Submit
    Then I am redirected to the Application Registration Tool page
    And the application "BulkExtractApp2" is listed in the table on the top
    And the client ID and shared secret fields are present
#    And I click on log out

Scenario: App developer enables Bulk Extract App
    When I navigate to the Portal home page
    When I see the realm selector I authenticate to the developer realm
     And I was redirected to the "Simple" IDP Login page
    When I submit the credentials "<DEVELOPER_EMAIL>" "<DEVELOPER_EMAIL_PASS>" for the "Simple" login page
    Then I should be on Portal home page
     And under System Tools, I click on "Register Application"
    Then I am redirected to the Application Registration Tool page
     And I see an application "BulkExtractApp2" in the table
     And the client ID and shared secret fields are present
    When I clicked on the button Edit for the application "BulkExtractApp2"
     And I expand all nodes
     And I deselect hierarchical mode
    When I enable the educationalOrganization "Daybreak School District 4529" in production
    When I enable the educationalOrganization "Education Agency for RC Tests" in production
    When I click on Save
#    And I click on log out
#    And "BulkExtractApp2" is enabled for "2" production education organizations

Scenario: App developer creates new non Bulk Extract App
    When I navigate to the Portal home page
    When I see the realm selector I authenticate to the developer realm
     And I was redirected to the "Simple" IDP Login page
    When I submit the credentials "<DEVELOPER_EMAIL>" "<DEVELOPER_EMAIL_PASS>" for the "Simple" login page
    Then I should be on Portal home page
     And under System Tools, I click on "Register Application"
    Then I am redirected to the Application Registration Tool page
     And I have clicked to the button New
     And I am redirected to a new application page
    When I entered the name "NotABulkExtractApp" into the field titled "Name"
     And I entered the name "Best.  Description.  Ever." into the field titled "Description"
     And I entered the name "0.0" into the field titled "Version"
     And I make my app an installed app
     And I click on the button Submit
    Then I am redirected to the Application Registration Tool page
     And the application "NotABulkExtractApp" is listed in the table on the top
     And the client ID and shared secret fields are present
#     And I click on log out

Scenario: App developer enables non Bulk Extract App
    When I navigate to the Portal home page
    When I see the realm selector I authenticate to the developer realm
     And I was redirected to the "Simple" IDP Login page
    When I submit the credentials "<DEVELOPER_EMAIL>" "<DEVELOPER_EMAIL_PASS>" for the "Simple" login page
    Then I should be on Portal home page
     And under System Tools, I click on "Apps"
    Then I am redirected to the Application Registration Tool page
     And I see an application "Not a bulk extract app" in the table
     And the client ID and shared secret fields are present
     And I clicked on the button Edit for the application "NotABulkExtractApp"
     And I expand all nodes
    When I enable the educationalOrganization "Education Agency for RC Tests" in production
    When I click on Save
#     And I click on log out
#     And "NotABulkExtractApp" is enabled for "199" production education organizations

Scenario:  SEA approves freshly registered Applications
  When I navigate to the Portal home page
  When I see the realm selector I authenticate to "Daybreak Test Realm"
   And I was redirected to the "Simple" IDP Login page
  When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
  Then I should be on Portal home page
   And under System Tools, I click on "Application Authorizations"
  Then I am redirected to the Admin Application Authorization Tool
  #Authorize fresh non-bulk extract app
   And I see an application "NotABulkExtractApp" in the table
   And in Status it says "Not Approved"
   And I click on the "Edit Authorizations" button next to it
   And I expand all nodes
   And I authorize the educationalOrganization "Education Agency for RC Tests" in the production tenant
   And I click Update
   And I see an application "NotABulkExtractApp" in the table
   And in Status it says "199 EdOrg(s)"
  Then there are "199" edOrgs for the "NotABulkExtractApp" application in the production applicationAuthorization collection
  #Authorize fresh bulk extract app
   And I see an application "BulkExtractApp2" in the table
   And in Status it says "Not Approved"
   And I click on the "Edit Authorizations" button next to it
   And I expand all nodes
   And I deselect hierarchical mode
   And I authorize the educationalOrganization "Daybreak School District 4529" in the production tenant
   And I click Update
   And I see an application "BulkExtractApp2" in the table
   And in Status it says "1 EdOrg(s)"
  Then there are "1" edOrgs for the "BulkExtractApp2" application in the production applicationAuthorization collection

Scenario: Operator triggers a bulk extract
  Given the production extraction zone is empty
    And the operator triggers a bulk extract for the production tenant
    And the operator triggers a delta for the production tenant

Scenario: App makes an api call to retrieve an non-top level edorg bulk extract
   #Get a session to trigger a bulk extract
   Given the pre-existing bulk extract testing app key has been created
    When I navigate to the API authorization endpoint with my client ID
    When I select "Daybreak Test Realm" and click go
     And I was redirected to the "Simple" IDP Login page
    When I submit the credentials "jstevenson" "jstevenson1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    Then I get the id for the edorg "IL-DAYBREAK"
    #Get bulk extract tar file
    Then there is no bulk extract files in the local directory
     And I request and download a "bulk" extract file for the edorg
     And there is a metadata file in the extract
     And the extract contains a file for each of the following entities:
      |  entityType                            |
      # |  assessment                            |
      |  attendance                            |
      |  courseTranscript                      |
      |  disciplineIncident                    |
      |  disciplineAction                      |
      |  grade                                 |
      |  gradebookEntry                        |
      # |  learningObjective                     |
      # |  learningStandard                      |
      |  parent                                |
      # |  program                               |
      |  reportCard                            |
      |  staff                                 |
      |  staffCohortAssociation                |
      |  staffEducationOrganizationAssociation |
      |  staffProgramAssociation               |
      |  student                               |
      |  studentAcademicRecord                 |
      |  studentAssessment                     |
      |  studentCohortAssociation              |
      |  studentCompetency                     |
      # |  studentCompetencyObjective            |
      |  studentDisciplineIncidentAssociation  |
      |  studentProgramAssociation             |
      |  studentGradebookEntry                 |
      |  studentSchoolAssociation              |
      |  studentSectionAssociation             |
      |  studentParentAssociation              |
      |  teacher                               |
      |  teacherSchoolAssociation              |
      |  teacherSectionAssociation             |

Scenario: Charter School - App makes an api call to retrieve an non-top level edorg bulk extract
   #Get a session to trigger a bulk extract
   Given the pre-existing bulk extract testing app key has been created
    When I navigate to the API authorization endpoint with my client ID
    When I select "Daybreak Test Realm" and click go
    And I was redirected to the "Simple" IDP Login page
    When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    Then I get the id for the edorg "IL-CHARTER-SCHOOL"
   #Get bulk extract tar file
    Then there is no bulk extract files in the local directory
     And I request and download a "bulk" extract file for the edorg
     And there is a metadata file in the extract
     And the extract contains a file for each of the following entities:
      |  entityType                            |
      |  attendance                            |
      |  courseTranscript                      |
      |  disciplineIncident                    |
      |  disciplineAction                      |
      |  grade                                 |
      |  gradebookEntry                        |
      |  parent                                |
      |  reportCard                            |
      |  staff                                 |
      |  staffCohortAssociation                |
      |  staffEducationOrganizationAssociation |
      |  staffProgramAssociation               |
      |  student                               |
      |  studentAcademicRecord                 |
      |  studentAssessment                     |
      |  studentCohortAssociation              |
      |  studentCompetency                     |
      |  studentDisciplineIncidentAssociation  |
      |  studentProgramAssociation             |
      |  studentGradebookEntry                 |
      |  studentSchoolAssociation              |
      |  studentSectionAssociation             |
      |  studentParentAssociation              |
      |  teacher                               |
      |  teacherSchoolAssociation              |
      |  teacherSectionAssociation             |

  Scenario: App makes an api call to retrieve a top level edorg bulk extract
  #Get a session to trigger a bulk extract
    Given the pre-existing bulk extract testing app key has been created
     When I navigate to the API authorization endpoint with my client ID
     When I select "Daybreak Test Realm" and click go
      And I was redirected to the "Simple" IDP Login page
     When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
     Then I should receive a json response containing my authorization code
     When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
     Then I should receive a json response containing my authorization token
     Then I get the id for the edorg "STANDARD-SEA"
  #Get bulk extract tar file
     Then there is no bulk extract files in the local directory
      And I request and download a "bulk" extract file for the edorg
      And there is a metadata file in the extract
      And the extract contains a file for each of the following entities:
        |  entityType                            |
        |  staff                                 |
        |  staffCohortAssociation                |
        |  staffEducationOrganizationAssociation |
        |  staffProgramAssociation               |

Scenario: App makes an api call to retrieve a public data bulk extract
   #Get a session to trigger a bulk extract
   Given the pre-existing bulk extract testing app key has been created
    When I navigate to the API authorization endpoint with my client ID
    When I select "Daybreak Test Realm" and click go
     And I was redirected to the "Simple" IDP Login page
    When I submit the credentials "jstevenson" "jstevenson1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
   #Get bulk extract tar file
    Then there is no bulk extract files in the local directory
     And I request and download a "public" extract file for the edorg
     And there is a metadata file in the extract
     And the extract contains a file for each of the following entities:
        |  entityType                            |
        |  assessment                            |
        |  learningObjective                     |
        |  learningStandard                      |
        |  competencyLevelDescriptor             |
        |  studentCompetencyObjective            |
        |  program                               |
        |  calendarDate                          |
        |  course                                |
        |  courseOffering                        |
        |  educationOrganization                 |
        |  graduationPlan                        |
        |  session                               |
        |  gradingPeriod                         |
        |  school                                |
        |  cohort                                |
        |  section                               |

Scenario: App makes an api call to retrieve a bulk extract delta
#Get a session to trigger a bulk extract
  Given the pre-existing bulk extract testing app key has been created
   When I navigate to the API authorization endpoint with my client ID
    And I select "Daybreak Test Realm" and click go
    And I was redirected to the "Simple" IDP Login page
   When I submit the credentials "jstevenson" "jstevenson1234" for the "Simple" login page
   Then I should receive a json response containing my authorization code
   When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
   Then I should receive a json response containing my authorization token
    And there is no bulk extract files in the local directory
   Then I get the id for the edorg "IL-DAYBREAK"
   When I PATCH the endDate for the staffProgramAssociation entity to 2011-05-05
   Then I should receive a return code of 204
   When the operator triggers a delta for the production tenant
   #And I make a call to the bulk extract end point "/v1.1/bulk/extract/list"
    And I make a call to the bulk extract end point "/v1.1/bulk/extract/list" using the certificate for app "<RC Server>"
    And I get back a response code of "200"
    And I store the URL for the latest delta for the edorg
    And the number of returned URLs is correct:
     |   fieldName    | count |
     |   fullEdOrgs   |  1    |
     |   deltaEdOrgs  |  1    |
    And I request and download a "delta" extract file for the edorg
    And there is a metadata file in the extract
    And the extract contains a file for each of the following entities:
     |  entityType                            |
     |  staffProgramAssociation               |

  Scenario: App makes an api call to retrieve a bulk extract delta for the top level edorg
  #Get a session to trigger a bulk extract
    Given the pre-existing bulk extract testing app key has been created
     When I navigate to the API authorization endpoint with my client ID
      And I select "Daybreak Test Realm" and click go
      And I was redirected to the "Simple" IDP Login page
     When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
     Then I should receive a json response containing my authorization code
     When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
     Then I should receive a json response containing my authorization token
      And there is no bulk extract files in the local directory

     Then I get the id for the staff "rrogers"
     When I PATCH the telephone number for the current staff entity to "555-555-5555"
     Then I should receive a return code of 204
     When the operator triggers a delta for the production tenant
  #And I make a call to the bulk extract end point "/v1.1/bulk/extract/list"
     Then I get the id for the edorg "STANDARD-SEA"
      And I make a call to the bulk extract end point "/v1.1/bulk/extract/list" using the certificate for app "<RC Server>"
      And I get back a response code of "200"
      And I store the URL for the latest delta for the edorg
      And the number of returned URLs is correct:
        |   fieldName    | count |
        |   fullEdOrgs   |  3    |
        |   deltaEdOrgs  |  3    |
      And I request and download a "delta" extract file for the edorg
      And there is a metadata file in the extract
      And the extract contains a file for each of the following entities:
        |  entityType            |
        |  staff                 |

Scenario: Ingestion user ingests additional public entities
  Given a landing zone
    And I drop the file "NewSimplePublicEntities.zip" into the landingzone
   When the most recent batch job for file "NewSimplePublicEntities.zip" has completed successfully
   Then I should not see an error log file created
    And I should not see a warning log file created

  Scenario: SEA admin makes an api call to PATCH the SEA
    Given the pre-existing bulk extract testing app key has been created
     When I navigate to the API authorization endpoint with my client ID
      And I select "Daybreak Test Realm" and click go
      And I was redirected to the "Simple" IDP Login page
     When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
     Then I should receive a json response containing my authorization code
     When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
     Then I should receive a json response containing my authorization token
      And I get the id for the edorg "STANDARD-SEA"
     When I PATCH the postalCode for the current edorg entity to 99999
     Then I should receive a return code of 204

Scenario: App makes an api call to retrieve a bulk extract public data delta
  #Get a session to trigger a bulk extract
  Given the pre-existing bulk extract testing app key has been created
   When I navigate to the API authorization endpoint with my client ID
    And I select "Daybreak Test Realm" and click go
    And I was redirected to the "Simple" IDP Login page
   When I submit the credentials "jstevenson" "jstevenson1234" for the "Simple" login page
   Then I should receive a json response containing my authorization code
   When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
   Then I should receive a json response containing my authorization token
    And there is no bulk extract files in the local directory

   When the operator triggers a delta for the production tenant
    And I make a call to the bulk extract end point "/v1.1/bulk/extract/list" using the certificate for app "<RC Server>"
    And I get back a response code of "200"
    And I store the URL for the latest delta for the Public
    And the number of returned URLs is correct:
      |   fieldName     | count |
      |   deltaPublic   |  1    |
    And I request and download a "delta" extract file for the edorg
    And there is a metadata file in the extract
    And the extract contains a file for each of the following entities:
      |  entityType                            |
      |  educationOrganization                 |
      |  course                                |
      |  competencyLevelDescriptor             |
      |  studentCompetencyObjective            |
      |  learningObjective                     |
      |  learningStandard                      |
      |  program                               |
      |  calendarDate                          |

