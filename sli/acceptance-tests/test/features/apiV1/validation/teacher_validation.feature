@RALLY_US209
Feature: Test schema based validation on entities/associations for teachers


Background: Logged in as a teacher and using the small data set
	Given I am logged in using "cgrayadmin" "cgray1234" to realm "IL"

Scenario: Post a valid base Student/School with bare minimum required data
	Given format "application/json"
	And I create a valid base level student object
	When I navigate to POST "/v1/students"
	Then I should receive a return code of 201
	Given I create a valid base level school object
	When I navigate to POST "/v1/schools"
	Then I should receive a return code of 201


Scenario: Fail when posting a School object during a Student POST operation
	Given format "application/json"
	Given I create a valid base level school object
	When I navigate to POST "/v1/students"
	Then I should receive a return code of 400

#tests all non-nullable fields
Scenario: Fail when passing blank object during POST for student
	Given format "application/json"
	Given I create a blank json object
	When I navigate to POST "/v1/students"
	Then I should receive a return code of 400
	When I navigate to POST "/v1/schools"
	Then I should receive a return code of 400
#	When I navigate to POST "/v1/studentSchoolAssociations"
#	Then I should receive a return code of 400
#	   And the response body should tell me why the request was invalid

Scenario: Fail when passing bad enum during POST for student
	Given format "application/json"
	Given I create a student object with "sex" set to Guy
	When I navigate to POST "/v1/students"
    Then I should receive a return code of 400
#	   And the response body should tell me why the request was invalid


Scenario: Fail when passing an incorrectly capitalized enum during POST for student
	Given format "application/json"
    Given I create a student object with sex equal to "MALE" instead of "Male"
	When I navigate to POST "/v1/students"
    Then I should receive a return code of 400
#	   And the response body should tell me why the request was invalid


Scenario: Fail when passing map instead of array during POST for school
	Given format "application/json"
	Given I create a school object with "address" set to a single map
	When I navigate to POST "/v1/schools"
    Then I should receive a return code of 400
    Given I create the same school object with "address" as an array with the same map
	When I navigate to POST "/v1/schools"
    Then I should receive a return code of 201


Scenario: Fail when passing array instead of map during POST for student
	Given format "application/json"
    Given I create a student object with "name" set to an array of names
	When I navigate to POST "/v1/students"
    Then I should receive a return code of 400
#	   And the response body should tell me why the request was invalid
    Given I create the same student object with "name" as a map with the same data
	When I navigate to POST "/v1/students"
    Then I should receive a return code of 201


Scenario: Fail when posting a StudentSchoolAssociation with invalid school ID
	Given format "application/json"
    Given an SSA object is valid except for "schoolID"
#	When I navigate to POST "/v1/studentSchoolAssociations"
#    Then I should receive a return code of 400
#	   And the response body should tell me why the request was invalid


Scenario: Fail when posting a string in a field expecting an integer
	Given format "application/json"
    Given I create a student object with "learningStyles.visualLearning" equal to a string
	When I navigate to POST "/v1/students"
    Then I should receive a return code of 400
#	   And the response body should tell me why the request was invalid


Scenario: Fail when posting an integer in a field expecting a string
	Given format "application/json"
    Given I create a student object with "learningStyles.visualLearning" equal to a integer
	When I navigate to POST "/v1/students"
    Then I should receive a return code of 400
#	   And the response body should tell me why the request was invalid


Scenario: Fail when posting a string to a field that has more characters than the schema allows
	Given format "application/json"
    Given I create a school object with "nameOfInstitution" equal to a 76 character string
	When I navigate to POST "/v1/schools"
    Then I should receive a return code of 400
#	   And the response body should tell me why the request was invalid


Scenario: Fail when posting a string to a field that has fewer characters than the schema allows
	Given format "application/json"
    Given I create a school object with "webSite" equal to a 4 character string
	When I navigate to POST "/v1/schools"
    Then I should receive a return code of 400
#	   And the response body should tell me why the request was invalid


Scenario: Succeed when posting a string "true" when a boolean is expected
	Given format "application/json"
    Given I create a student object with "hispanicLatinoEthnicity" set to a true string
	When I navigate to POST "/v1/students"
    Then I should receive a return code of 201


Scenario: Fail when posting a date in the wrong format
	Given format "application/json"
    Given I create a student object with "birthDate" set to MM-DD-YYYY
	When I navigate to POST "/v1/students"
    Then I should receive a return code of 400
#	   And the response body should tell me why the request was invalid

Scenario: Passing blank object to a valid entity with PUT should fail with validation error (not patch the existing object)
	Given format "application/json"
    When I navigate to GET "/v1/students/<'Christoff' ID>"
    Then I should receive a return code of 200   
    When I create a blank request body object
      And I navigate to PUT "/v1/students/<'Christoff' ID>"
    Then I should receive a return code of 409

Scenario: Given a known school object, perform a PUT with a base school object to confirm option attributes are gone (test non-patching)
	Given format "application/json"
    When I navigate to GET "/v1/schools/<'Daybreak Central High' ID>"
    Then I should receive a return code of 200
    When I create a valid base level school object without parent education organization reference
      And "stateOrganizationId" has a value of "Daybreak Central High"
      And I navigate to PUT "/v1/schools/<'Daybreak Central High' ID>"
    Then I should receive a return code of 204
    When I navigate to GET "/v1/schools/<'Daybreak Central High' ID>"
    Then I should receive a return code of 200
      And "nameOfInstitution" should be "school name"
      And "stateOrganizationId" should be "Daybreak Central High"
      And "gradesOffered" should contain "First_grade" and "Second_grade"
      And "entityType" should be "educationOrganization"
      And there should be no other contents in the response body other than links

  Scenario: Given an invalid id, when I try to GET a target endpoint through 4-part URL, I should receive a 200 
    Given format "application/json"
    And I am logged in using "linda.kim" "linda.kim1234" to realm "IL"
    When I navigate to GET "/v1/sections/<'Valid Section' ID>/studentSectionAssociations/students"
    Then I should receive a return code of 200
    And a collection of size 1
    When I navigate to GET "/v1/sections/<'Invalid Section' ID>/studentSectionAssociations/students"
    Then I should receive a return code of 404

  @wip @DE1876
  Scenario: Given a valid JSON document for a staff, when I POST it multiple times I should only find one record
    Given format "application/json"
    And a valid json document for staff
    When I navigate to POST "/v1/staff"
    Then I should receive a return code of 201
    And I should receive a new entity URI
    When I navigate to POST "/v1/staff"
    Then I should receive a return code of 409

  @wip @DE1876
  Scenario: Given a valid JSON document for a teacher, when I POST it multiple times I should only find one record
    Given format "application/json"
    And a valid json document for teacher
    When I navigate to POST "/v1/teachers"
    Then I should receive a return code of 201
    And I should receive a new entity URI
    When I navigate to POST "/v1/teachers"
    Then I should receive a return code of 409

  Scenario: Given an invalid enumeration type in an entity body, when I do a POST the error message should be clear and easy to read
    Given format "application/json"
    And a valid json document for student
    When I set the sex to InvalidValue
    And I navigate to POST "/v1/students"
    Then I should receive a return code of 400
    And the error message should contain "expectedTypes=['Female', 'Male']"
