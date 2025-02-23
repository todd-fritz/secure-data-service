@smoke
Feature: In order to retrieve session details from the API

Scenario: Authenticate with valid session ID and get the session debug context successfully
        
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    And I get an authentication session ID from the gatekeeper
	When I GET the url "/system/session/debug" using that session ID
    Then I should receive a return code of 200
    And I should see the session debug context in the response body

Scenario: Deny access when request session debug context without session ID

	When I GET the url "/system/session/debug" using a blank session ID
    Then I should receive a return code of 401
    And I should see a link in the responce header telling me where to authenicate

Scenario: Deny access when request session debug context with invalid session ID

    When the sli securityEvent collection is empty
	And I GET the url "/system/session/debug" using an invalid session ID
    Then I should receive a return code of 401
    And I should see a link in the responce header telling me where to authenicate
    And I check to find if record is in sli db collection:
        | collectionName  | expectedRecordCount | searchParameter         | searchValue                                            | searchType |
        | securityEvent   | 1                   | body.appId              | UNKNOWN                                                | string     |
        | securityEvent   | 1                   | body.className          | org.slc.sli.api.resources.SecuritySessionResource      | string     |
        # user and targetEdOrgs are not known since no session was established
    And "1" security event with field "body.actionUri" matching "http.*/api/rest/system/session/debug" should be in the sli db
    And "1" security event matching "Access Denied: User must be logged in" should be in the sli db

Scenario: Access the session check resource with valid authentication session ID

    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    And I get an authentication session ID from the gatekeeper
	When I GET the url "/system/session/check" using that session ID
    Then I should receive a return code of 200
    And I should see the authenticated object in the response body

Scenario: Access the session check resource without authentication session ID

	When I GET the url "/system/session/check" using a blank session ID
    Then I should receive a return code of 200
    And I should see the non-authenticated object in the response body

Scenario: Access the session check resource with invalid authentication session ID
        
	When I GET the url "/system/session/check" using an invalid session ID
    Then I should receive a return code of 200
    And I should see the non-authenticated object in the response body
    
@US2926
Scenario: Checking that we get an email back with session check

	When I GET the url "/system/session/check" using a staff ID
    Then I should receive a return code of 200
    And I should see the work email address in the response
  When I GET the url "/system/session/check" using an admin ID
    Then I should receive a return code of 200
    And I should see the email address in the response
