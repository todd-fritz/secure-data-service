@RALLY_US1129
Feature: Test CRUD fuctionality of Custom Entities per application 
Application is authorized using OAuth (there is an client_id and client_secret).  Custom entities are partitioned by application. Access control inherited from parent.


Scenario:  As an IT Admin, I want to add custom entitiy to a core entity belonging to my application
    #create a new custom entity for demoClient
    Given  I am a valid SEA/LEA end user "rrogers" with password "rrogers1234"
    And the clientID is "demoClient"
    And I am authenticated on "IL"
   
    Given format "application/json"
    And a valid entity json object for a "educationOrganizations"
    And I add a key value pair "CustomConfig" : "<?xml version=1.0?><DisplayName>StateTest Reading Results</DisplayName>" to the object
	When I navigate to POST "/<EDUCATION ORGANIZATION URI>/<EDUCATION ORGANIZATION ID>/<CUSTOM URI>"
	Then I should receive a return code of 201
	And I should receive a Location header for the custom entity

	#create a new custom entity for sampleApplication
	Given  I am a valid SEA/LEA end user "rrogers" with password "rrogers1234"
    And the clientID is "SampleApplication"
     And I am authenticated on "IL"
     
    Given format "application/json" 
    And a valid entity json object for a "educationOrganizations"
    And I add a key value pair "CustomConfig" : "<?xml version=1.0?><DisplayName>SAT Scores</DisplayName>" to the object
	When I navigate to POST "/<EDUCATION ORGANIZATION URI>/<EDUCATION ORGANIZATION ID>/<CUSTOM URI>"
	Then I should receive a return code of 201
	
	#retrieve correct custom entity for correct application
    Given  I am a valid SEA/LEA end user "rrogers" with password "rrogers1234"
    And the clientID is "demoClient"
    And I am authenticated on "IL"
	When I navigate to GET "/<EDUCATION ORGANIZATION URI>/<EDUCATION ORGANIZATION ID>/<CUSTOM URI>"
    Then I should receive a key value pair "CustomConfig" : "<?xml version=1.0?><DisplayName>StateTest Reading Results</DisplayName>" in the result


Scenario: As an IT Admin, I want to update custom entity associated with any core entity belonging to my application

    Given  I am a valid SEA/LEA end user "rrogers" with password "rrogers1234"
    And the clientID is "demoClient"
    And I am authenticated on "IL"
    
    Given format "application/json" 
    And a valid entity json object for a "educationOrganizations"
    And I add a key value pair "CustomConfig" : "<?xml version=1.0?><DisplayName>StateTest Reading Results</DisplayName>" to the object
	When I navigate to POST "/<EDUCATION ORGANIZATION URI>/<EDUCATION ORGANIZATION ID>/<CUSTOM URI>"
	Then I should receive a return code of 201

	Given  I am a valid SEA/LEA end user "rrogers" with password "rrogers1234"
    And the clientID is "SampleApplication"
    And I am authenticated on "IL"
    
     Given format "application/json"
    And a valid entity json object for a "educationOrganizations"
    And I add a key value pair "ColumnConfig" : "<?xml version=1.0?><DisplayName>SAT Scores</DisplayName>" to the object
	When I navigate to POST "/<EDUCATION ORGANIZATION URI>/<EDUCATION ORGANIZATION ID>/<CUSTOM URI>"
	Then I should receive a return code of 201

	Given  I am a valid SEA/LEA end user "rrogers" with password "rrogers1234"
    And the clientID is "demoClient"
    And I am authenticated on "IL"
    
    Given format "application/json"
    And a valid entity json object for a "educationOrganizations"
    And I add a key value pair "CustomConfig" : "<?xml version=1.0?><DisplayName>StateTest Writing Results</DisplayName>" to the object
	When I navigate to PUT "/<EDUCATION ORGANIZATION URI>/<EDUCATION ORGANIZATION ID>/<CUSTOM URI>"
	Then I should receive a return code of 204
	
	When I navigate to GET "/<EDUCATION ORGANIZATION URI>/<EDUCATION ORGANIZATION ID>/<CUSTOM URI>"
	Then I should receive a key value pair "CustomConfig" : "<?xml version=1.0?><DisplayName>StateTest Writing Results</DisplayName>" in the result
	
	Given  I am a valid SEA/LEA end user "rrogers" with password "rrogers1234"
    And the clientID is "SampleApplication"
    And I am authenticated on "IL"
    
	When I navigate to GET "/<EDUCATION ORGANIZATION URI>/<EDUCATION ORGANIZATION ID>/<CUSTOM URI>"
    Then I should receive a key value pair "ColumnConfig" : "<?xml version=1.0?><DisplayName>SAT Scores</DisplayName>" in the result

Scenario:  As an Educator, I can retrieve correct custom entity for correct application, and I can also create and update custom entities
	#As an educator I can also retrieve correct custom entity for correct application
    Given  I am a valid SEA/LEA end user "rbraverman" with password "rbraverman1234"
    And the clientID is "demoClient"
    And I am authenticated on "IL"
	When I navigate to GET "/<EDUCATION ORGANIZATION URI>/<EDUCATION ORGANIZATION ID>/<CUSTOM URI>"
	Then I should receive a key value pair "CustomConfig" : "<?xml version=1.0?><DisplayName>StateTest Writing Results</DisplayName>" in the result
    
    Given format "application/json" 
    And the sli securityEvent collection is empty
    And a valid entity json object for a "educationOrganizations"
    And I add a key value pair "CustomConfig" : "<?xml version=1.0?><DisplayName>SAT Scores</DisplayName>" to the object
	When I navigate to POST "/<EDUCATION ORGANIZATION URI>/<EDUCATION ORGANIZATION ID>/<CUSTOM URI>"
    Then I should receive a return code of 201

    Given format "application/json"
    And the sli securityEvent collection is empty
    And a valid entity json object for a "educationOrganizations"
    And I add a key value pair "CustomConfig" : "<?xml version=1.0?><DisplayName>StateTest Writing Results</DisplayName>" to the object
	When I navigate to PUT "/<EDUCATION ORGANIZATION URI>/<EDUCATION ORGANIZATION ID>/<CUSTOM URI>"
	Then I should receive a return code of 204

Scenario Outline: As an educator or leader, I want to read a custom entity associated with any core entity belonging to my application 
	   Given  I am a valid SEA/LEA end user "rrogers" with password "rrogers1234"
       And the clientID is "demoClient"
       And I am authenticated on "IL"
	    
	    Given format "application/json"
	    And a valid entity json object for a "students"
	    And I add a key value pair "Drives" : "True" to the object
		When I navigate to POST "/<STUDENT URI>/<STUDENT ID>/<CUSTOM URI>"
		Then I should receive a return code of 201
		
		Given  I am a valid SEA/LEA end user <Username> with password <Password>
	    And the clientID is <ClientID>
	     And I am authenticated on "IL"
	     
	When I navigate to GET "/<STUDENT URI>/<STUDENT ID>"?includeCustom="true"
		Then I should receive a return code of <Code>
		And I should recieve the "student" object with "<STUDENT ID>"
		And additionally I should receive a key value pair <Key> : <Value> in the result
		
	When I navigate to GET "/<STUDENT URI>/<STUDENT ID>"?includeCustom="false"
		Then I should receive a return code of <Code>
		And I should recieve the "student" object with "<STUDENT ID>"
		And there is no other custom data returned
		
		When I navigate to GET "/<STUDENT URI>/<STUDENT ID>"
		Then I should receive a return code of <Code>
		And I should recieve the "student" object with "<STUDENT ID>"
		And there is no other custom data returned
		
	Examples:
| Username     | Password         | AnyDefaultSLIRole  | ClientID     | Key            | Value    | Code  |
| "sbantu"     | "sbantu1234"     | "Leader"           | "demoClient" | "Drives"       | "True"   | 200   |
| "rbraverman" | "rbraverman1234" | "Educator"         | "demoClient" | "Drives"       | "True"   | 200   |
#| "msmith" | "msmith1234" | "AggregateViewer"  | "demoClient" | ""             | ""       | 403   |
 	

Scenario Outline: As an user, I want to delete a custom entity associated with any core entity belonging to my application
	 Given  I am a valid SEA/LEA end user "rrogers" with password "rrogers1234"
       And the clientID is "demoClient"
       And I am authenticated on "IL"
       
	     Given format "application/json"
	    And a valid entity json object for a "students"
	     And I add a key value pair "Drives" : "True" to the object
		When I navigate to POST "/<STUDENT URI>/<STUDENT ID>/<CUSTOM URI>"
		Then I should receive a return code of 201
		
		Given  I am a valid SEA/LEA end user <Username> with password <Password>
	    And the clientID is <ClientID>
	     And I am authenticated on "IL"
	    
	    Given format "application/json" 
		When I navigate to DELETE "/<STUDENT URI>/<STUDENT ID>/<CUSTOM URI>"
		Then I should receive a return code of <DelCode>
		
		Given format "application/json"
		When I navigate to GET "/<STUDENT URI>/<STUDENT ID>/<CUSTOM URI>"
		Then I should receive a return code of <ReadCode>
		And I should receive a key value pair <Key> : <Value> in the result

		
	Examples:
	| Username      | Password          | AnyDefaultSLIRole  | ClientID     | Key            | Value    | DelCode   | ReadCode |
	| "sbantu"      | "sbantu1234"      | "Leader"           | "demoClient" | ""             | ""   |  204      |  404     |
	| "rbraverman"  | "rbraverman1234"  | "Educator"         | "demoClient" | ""             | ""   |  204      |  404     |
	| "msmith"      | "msmith1234"      | "AggregateViewer"  | "demoClient" | ""             | ""       |  403      |  403     |
	| "rrogers"     | "rrogers1234"     | "ITAdmin"          | "demoClient" | ""             | ""       |  204      |  404     |

Scenario Outline: As an user, I want to update  a custom entity associated with any core entity belonging to my application 
	 Given  I am a valid SEA/LEA end user "rrogers" with password "rrogers1234"
       And the clientID is "demoClient"
       And I am authenticated on "IL"
       
	     Given format "application/json"
	    And a valid entity json object for a "students"
	     And I add a key value pair "Drives" : "True" to the object
		When I navigate to POST "/<STUDENT URI>/<STUDENT ID>/<CUSTOM URI>"
		Then I should receive a return code of 201
		
		Given  I am a valid SEA/LEA end user <Username> with password <Password>
	    And the clientID is <ClientID>
	     And I am authenticated on "IL"
	     
	     Given format "application/json" 
	   And a valid entity json object for a "students"
	    And I add a key value pair <Key> : <Value> to the object
		When I navigate to PUT "/<STUDENT URI>/<STUDENT ID>/<CUSTOM URI>"
		Then I should receive a return code of <Code>

	Examples:
	| Username  | Password      | AnyDefaultSLIRole  | ClientID     | Key            | Value    | Code   | 
	| "sbantu"  | "sbantu1234"  | "Leader"           | "demoClient" | "Drives"       | "True"   |  204   |
	| "rbraverman"  | "rbraverman1234"  | "Educator" | "demoClient" | "Drives"       | "True"   |  204   |
	| "msmith"  | "msmith1234"  | "AggregateViewer"  | "demoClient" | "Drives"       | "True"   |  204   |
	| "rrogers" | "rrogers1234" | "ITAdmin"          | "demoClient" | "Drives"       | "True"   |  204   |
	
Scenario Outline: As an user, I want to create  a custom entity associated with any association belonging to my application 		
		Given  I am a valid SEA/LEA end user <Username> with password <Password>
	     And the clientID is <ClientID>
	     And I am authenticated on "IL"
	     
	    Given format "application/json"
	    And a valid entity json object for a "studentSchoolAssociations"
	    And I add a key value pair <Key> : <Value> to the object
		When I navigate to <Action> "/<STUDENT SCHOOL ASSOCIATION URI>/<STUDENT SCHOOL ASSOC ID>/<CUSTOM URI>"
		Then I should receive a return code of <Code>	
	Examples:
| Username  | Password      | AnyDefaultSLIRole  | ClientID     | Key                       | Value     | Code   | Action |
| "sbantu"  | "sbantu1234"  | "Leader"           | "demoClient" | "currentlyEnrolled"       | "False"   |  201   | POST   |
| "rbraverman"  | "rbraverman1234"  | "Educator" | "demoClient" | "currentlyEnrolled"       | "False"   |  201   | POST   |
| "msmith"  | "msmith1234"  | "AggregateViewer"  | "demoClient" | "currentlyEnrolled"       | "False"   |  201   | POST   |
| "rrogers" | "rrogers1234" | "ITAdmin"          | "demoClient" | "currentlyEnrolled"       | "False"   |  201   | POST   | 
	

Scenario Outline: As an user, I want to update  a custom entity associated with any association belonging to my application 
	Given  I am a valid SEA/LEA end user "rrogers" with password "rrogers1234"
       And the clientID is "demoClient"
       And I am authenticated on "IL"
       
        Given format "application/json"
	    And a valid entity json object for a "studentSchoolAssociations"
	    And I add a key value pair "currentlyEnrolled" : "True" to the object
		When I navigate to POST "/<STUDENT SCHOOL ASSOCIATION URI>/<STUDENT SCHOOL ASSOC ID>/<CUSTOM URI>"
		Then I should receive a return code of 201
		
		Given  I am a valid SEA/LEA end user <Username> with password <Password>
	    And the clientID is <ClientID>
	     And I am authenticated on "IL"
	    
	    Given format "application/json"  
	    And a valid entity json object for a "studentSchoolAssociations"
	    And I add a key value pair <Key> : <Value> to the object
		When I navigate to <Action> "/<STUDENT SCHOOL ASSOCIATION URI>/<STUDENT SCHOOL ASSOC ID>/<CUSTOM URI>"
		Then I should receive a return code of <Code>
	Examples:
	| Username  | Password      | AnyDefaultSLIRole  | ClientID     | Key                       | Value     | Code   | Action |
	| "sbantu"  | "sbantu1234"  | "Leader"           | "demoClient" | "currentlyEnrolled"       | "False"   |  204   | PUT    |
	| "rbraverman"  | "rbraverman1234"  | "Educator" | "demoClient" | "currentlyEnrolled"       | "False"   |  204   | PUT    |
	| "msmith"  | "msmith1234"  | "AggregateViewer"  | "demoClient" | "currentlyEnrolled"       | "False"   |  204   | PUT    |
	| "rrogers" | "rrogers1234" | "ITAdmin"          | "demoClient" | "currentlyEnrolled"       | "False"   |  204   | PUT    | 
	

Scenario Outline: As an user, I want to delete and then read a custom entity associated with any association belonging to my application 		
		Given  I am a valid SEA/LEA end user "rrogers" with password "rrogers1234"
	    And the clientID is "demoClient"
	    And I am authenticated on "IL"
	    
	     Given format "application/json" 
	    And a valid entity json object for a "studentSchoolAssociations"
	    And I add a key value pair "currentlyEnrolled" : "True" to the object
		When I navigate to POST "/<STUDENT SCHOOL ASSOCIATION URI>/<STUDENT SCHOOL ASSOC ID>/<CUSTOM URI>"
		Then I should receive a return code of 201
		
		Given  I am a valid SEA/LEA end user <Username> with password <Password>
	    And the clientID is <ClientID>
	    And I am authenticated on "IL"
	    
	     Given format "application/json" 
	    And a valid entity json object for a "studentSchoolAssociations"
		When I navigate to <Action> "/<STUDENT SCHOOL ASSOCIATION URI>/<STUDENT SCHOOL ASSOC ID>/<CUSTOM URI>"
		Then I should receive a return code of <Code>	
		
		When I navigate to GET "/<STUDENT SCHOOL ASSOCIATION URI>/<STUDENT SCHOOL ASSOC ID>/<CUSTOM URI>"
		Then I should receive a return code of <ReadCode>
		And I should receive a key value pair <Key> : <Value> in the result
		
	Examples:
	| Username  | Password      | AnyDefaultSLIRole  | ClientID     | Key                       | Value     | Code   | Action | ReadCode |
	| "sbantu"  | "sbantu1234"  | "Educator"         | "demoClient" | ""                        | ""    |  204   | DELETE |  404     |
	| "rbraverman"  | "rbraverman1234"  | "Educator" | "demoClient" | ""                        | ""    |  204   | DELETE |  404     |
	| "msmith"  | "msmith1234"  | "AggregateViewer"  | "demoClient" | ""                        | ""    |  403   | DELETE |  403     |
	| "rrogers" | "rrogers1234" | "ITAdmin"          | "demoClient" | ""                        | ""    |  204   | DELETE |  404     |


Scenario:  As an IT Admin, I want to add a large custom entitiy to a core entity belonging to my application
    Given  I am a valid SEA/LEA end user "rrogers" with password "rrogers1234"
    And the clientID is "demoClient"
    And I am authenticated on "IL"

    Given format "application/json"

    And a valid entity json object for a "educationOrganizations"
    And I add a large random file with key "custom_app_data" to the object
    When I navigate to POST "/<EDUCATION ORGANIZATION URI>/<EDUCATION ORGANIZATION ID>/<CUSTOM URI>"
    Then I should receive a return code of 201
    And I should receive a Location header for the custom entity

    #retrieve correct custom entity for correct application
    Given  I am a valid SEA/LEA end user "rrogers" with password "rrogers1234"
    And the clientID is "demoClient"
    And I am authenticated on "IL"
    When I navigate to GET "/<EDUCATION ORGANIZATION URI>/<EDUCATION ORGANIZATION ID>/<CUSTOM URI>"
    Then I should receive the same large random file in key "custom_app_data" in the result


Scenario:  As an IT Admin, I want to add a truncated (thus faulty) large custom entitiy to a core entity belonging to my application and get a 400 server error.
    Given  I am a valid SEA/LEA end user "rrogers" with password "rrogers1234"
    And the clientID is "demoClient"
    And I am authenticated on "IL"

    Given format "application/json"
    And a valid entity json object for a "educationOrganizations"
    And I add a large random file with key "custom_app_data" to the object
    When I navigate to a truncated, faulty POST "/<EDUCATION ORGANIZATION URI>/<EDUCATION ORGANIZATION ID>/<CUSTOM URI>"
    Then I should receive a return code of 400

Scenario:  As an IT Admin, I want to add custom entitiy with invalid/blacklisted characters and get a 400 server error
    #create a new custom entity for demoClient
    Given  I am a valid SEA/LEA end user "rrogers" with password "rrogers1234"
    And the clientID is "demoClient"
    And I am authenticated on "IL"

    Given format "application/json"
    And a valid entity json object for a "educationOrganizations"
    And I navigate to DELETE "/<EDUCATION ORGANIZATION URI>/<EDUCATION ORGANIZATION ID>/<CUSTOM URI>"
    And I add a key value pair "Bad\x00Name" : "<?xml version=1.0?><DisplayName>StateTest Reading Results</DisplayName>" to the object
	When I navigate to POST "/<EDUCATION ORGANIZATION URI>/<EDUCATION ORGANIZATION ID>/<CUSTOM URI>"
	Then I should receive a return code of 400

	When I navigate to GET "/<EDUCATION ORGANIZATION URI>/<EDUCATION ORGANIZATION ID>/<CUSTOM URI>"
    Then I should receive a return code of 404

Scenario: As an IT Admin, I want to add custom entity of calendarDates to a core entity belonging to my application
    Given  I am a valid SEA/LEA end user "rrogers" with password "rrogers1234"
     And the clientID is "demoClient"
     And I am authenticated on "IL"
     And format "application/json"
     And a valid entity json object for a "calendarDates"
     And I add a key value pair "CustomConfig" : "<?xml version=1.0?><DisplayName>StateTest Programming Results</DisplayName>" to the object
	When I navigate to POST "/<CALENDARDATE URI>/<CALENDARDATE ID>/<CUSTOM URI>"
	Then I should receive a return code of 201

	When I navigate to GET "/<CALENDARDATE URI>/<CALENDARDATE ID>/<CUSTOM URI>"
    Then I should receive a key value pair "CustomConfig" : "<?xml version=1.0?><DisplayName>StateTest Programming Results</DisplayName>" in the result

    And I add a key value pair "CustomConfig" : "<?xml version=1.0?><DisplayName>StateTest Writing Results</DisplayName>" to the object
	When I navigate to PUT "/<CALENDARDATE URI>/<CALENDARDATE ID>/<CUSTOM URI>"
	Then I should receive a return code of 204

	When I navigate to DELETE "/<CALENDARDATE URI>/<CALENDARDATE ID>/<CUSTOM URI>"
	Then I should receive a return code of 204
