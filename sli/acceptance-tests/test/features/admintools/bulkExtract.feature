Feature: Administrating role-based access to bulking

@sandbox
Scenario: Make sure it works
    Given I have an open web browser
    When I navigate to the Custom Role Mapping Page
    And I select "inBloom" from the dropdown and click go
    And I was redirected to the "Simple" IDP Login page
    When I submit the credentials "sunsetadmin" "sunsetadmin1234" for the "Simple" login page
    Then I have navigated to my Custom Role Mapping Page
    Then I should not see right "BULK_EXTRACT" on any existing role
    When I click on the Add Group button
    And I type the name "Balrogs" in the Group name textbox
    When I add the right "BULK_EXTRACT" to the group "Balrogs"
    When I add the right "STAFF_CONTEXT" to the group "Balrogs"
    And I add the role "Balrog" to the group "Balrogs"
    And I hit the save button
    And the group "Balrogs" contains the "right" rights "BULK_EXTRACT"

    # Now test generation and use of the endpoint
    Given I used the long lived session token generator script to create a token for user "manthony" with role "Balrog" for edorg "Sunset Central High School" in tenant "Midgar"  for realm "IL-Sunset" that will expire in "300" seconds with client_id "vavedRa9uB"
    Then I should see that my role is "Balrog"
    When I navigate to GET "/v1/sections"
    And I should receive a return code of 403
    When I make a call to the bulk extract end point "/bulk/extract"
    When the return code is 200 I get expected tar downloaded
    Then I check the http response headers

  @production
  Scenario: District administrator is alerted to applications that want bulk extract
    Given I have an open web browser
    Given I am an authenticated District Super Administrator for "Sunset School District"
  	And I see an application "Testing App" in the table
  	Then I see that it has an alert about bulk extract
