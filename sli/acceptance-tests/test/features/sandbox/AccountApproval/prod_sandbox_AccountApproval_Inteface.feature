Feature: SLC operator approves/disables sandbox accounts using the account approval application

Background: 
Given I have an open web browser
And an LDAP server has been setup and running

@production
Scenario: production account management tool is disabled in prod
And I navigate to the account management page
And I select "inBloom" from the dropdown and click go
And I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page
When I hit the Admin Application Account Approval page
And I got the 404 page

@sandbox
Scenario: As a slc operator an sandbox accounts are automatically approved
And I navigate to the account management page
And I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page
Given there is an approved sandbox account for vendor "Macro Corp"
When I hit the Admin Application Account Approval page
Then I see one account with name "Loraine Plyler"
And his account status is "approved"

@sandbox
Scenario: As a slc operator I disable an approved sandbox account by clicking the reject button
And I navigate to the account management page
And I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page
Given there is an approved sandbox account for vendor "Macro Corp"
When I hit the Admin Application Account Approval page
Then I see one account with name "Loraine Plyler"
And his account status is "approved"
When I click the "Disable" button
And I am asked "Do you really want to disable this user account?"
 When I click on Ok
Then his account status changed to "disabled"
