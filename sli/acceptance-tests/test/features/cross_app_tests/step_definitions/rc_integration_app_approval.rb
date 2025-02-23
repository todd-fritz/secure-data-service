=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end

require_relative '../../admintools/step_definitions/developer_enable_steps.rb'

#portal, which also imports dashboard step def
Dir["./test/features/liferay/step_definitions/*.rb"].each {|file| require file}

#admin tools
Dir["./test/features/admintools/step_definitions/*.rb"].each {|file| require file if(!file.include?("multiple_realms_steps.rb"))}

#databrowser
Dir["./test/features/databrowser/step_definitions/*.rb"].each {|file| require file}

#search
Dir["./test/features/search/step_definitions/*.rb"].each {|file| require file}

$client_id = nil
$client_secret = nil

When /^I make my app an installed app$/ do
  @driver.find_element(:css, 'input[id="app_installed"]').click
end

Then /^I authorize the educationalOrganization "(.*?)" in the production tenant$/ do |edOrgName|
  disable_NOTABLESCAN()
  db = @conn[convertTenantIdToDbName(Property['tenant'])]
  coll = db.collection("educationOrganization")
  record = coll.find_one("body.nameOfInstitution" => edOrgName.to_s)
  #puts record.to_s
  edOrgId = record["_id"]
  #puts edOrgId.to_s
  app = @driver.find_element(:id, edOrgId.to_s).click
  enable_NOTABLESCAN()
end

Then /^my new apps client ID is present$/ do
  @driver.find_element(:xpath, "//tbody/tr[1]/td[1]").click
  client_id = @driver.find_element(:xpath, '//tbody/tr[2]/td/dl/dd[1]').text
  puts "client_id: " + client_id
  assert(client_id != '', "Expected non empty client Id, got #{client_id}")
  assert(client_id != 'Pending', "Expected non 'Pending' client Id, got #{client_id}")
  $client_id = client_id
end

Then /^my new apps shared secret is present$/ do
  #@driver.find_element(:xpath, "//tbody/tr[1]/td[1]").click
  client_secret = @driver.find_element(:xpath, '//tbody/tr[2]/td/dl/dd[2]').text
  puts "client_secret: " + client_secret
  assert(client_secret != '', "Expected non empty shared secret, got #{client_secret}")
  assert(client_secret != 'Pending', "Expected non 'Pending' shared secret, got #{client_secret}")
  $client_secret = client_secret
end

Then /^I enable my app for all districts$/ do
  assertWithWait("Could not find Enable All link") {@driver.find_element(:link, "Enable All")}
  @driver.find_element(:link, "Enable All").click
end

Given /^the testing device app key has been created$/ do
  @oauthClientId = $client_id
  @oauthClientSecret = $client_secret
  @oauthRedirectURI = "http://device"
end

Given /^the pre-existing bulk extract testing app key has been created$/ do
  @oauthClientId = Property['bulk_extract_testapp_client_id']
  @oauthClientSecret = Property['bulk_extract_testapp_secret']
  @oauthRedirectURI = "http://device"
  assert(@oauthClientId != nil, "Pre-existing Bulk Extract App not yet created in this env, or property was not set: bulk_extract_testapp_client_id")
  assert(@oauthClientSecret != nil, "Pre-existing Bulk Extract App not yet created in this env, or property was not set: bulk_extract_testapp_secret")
end

When /^I navigate to the API authorization endpoint with my client ID$/ do
  url = Property['api_server_url'] + "/api/oauth/authorize?response_type=code&client_id=#{@oauthClientId}"
  puts url
  @driver.get url
end

Then /^I should receive a json response containing my authorization code$/ do  
  assertWithWait("Could not find text 'authorization_code' on page") {@driver.page_source.include?("authorization_code")}
  
  @oauthAuthCode = @driver.page_source.match(/"authorization_code":"(?<Code>[^"]*)"/)[:Code]
end

When /^I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI$/ do
  url = Property['api_server_url'] + "/api/oauth/token?response_type=code&client_id=#{@oauthClientId}" + "&client_secret=#{@oauthClientSecret}&code=#{@oauthAuthCode}&redirect_uri=#{@oauthRedirectURI}"
  puts url
  @driver.get url
end

Then /^I should receive a json response containing my authorization token$/ do
  assertWithWait("Could not find text 'authorization_token' on page") {@driver.page_source.include?("access_token")}
  @sessionId = @driver.page_source.match(/"access_token":"(?<Token>[^"]*)"/)[:Token]
  puts @sessionId
end

Then /^my current url is "(.*?)"$/ do |url|
  assertWithWait("Not in expected URL") {@driver.current_url == url}
end

Then /^I enter "(.*?)" in the IDP URL field$/ do |url|
  STDOUT.puts "url : #{url}"
  @driver.find_element(:name, 'realm[idp][id]').send_keys url
end

Then /^I enter "(.*?)" in the Redirect Endpoint field$/ do |url|
  STDOUT.puts "redirect url : #{url}"
  @driver.find_element(:name, 'realm[idp][redirectEndpoint]').send_keys url
end

Then /^I enter "(.*?)" in the Artifact Resolution Endpoint field$/ do |url|
  STDOUT.puts "artifact resolution endpoint : #{url}"
  @driver.find_element(:name, 'realm[idp][artifactResolutionEndpoint]').send_keys url
end

Then /^I enter "(.*?)" in the Source Id field$/ do |url|
  STDOUT.puts "source id : #{url}"
  @driver.find_element(:name, 'realm[idp][sourceId]').send_keys url
end

Then /^I request and download a "(.*?)" extract file for the edorg$/ do |arg1|
  env_key = Property['rc_env']
  restTls("/bulk/extract/#{@lea}", nil, "application/x-tar", @sessionId, env_key) if arg1 == "bulk"
  restTls("/#{@list_uri}", nil, "application/x-tar", @sessionId, env_key) if arg1 == "delta"
  restTls("/bulk/extract/public", nil, "application/x-tar", @sessionId, env_key) if arg1 == "public"

  assert(@res.code==200, "Bulk Extract file was unable to be retrieved: #{@res.to_s}")
  @filePath = Property['extract_to_directory'] + "/extract.tar"
  @unpackDir = File.dirname(@filePath) + '/unpack'
  if (!File.exists?("extract"))
      FileUtils.mkdir("extract")
  end
  step "the response is decrypted using the key for app \"#{env_key}\""
  File.open(@filePath, 'w') {|f| f.write(@plain) }
  assert(File.exists?(@filePath), "Bulk Extract file was unable to be download to: #{@filePath.to_s}")
end

Then /I get the id for the edorg "(.*?)"$/ do |arg1|
  restHttpGet("/v1/educationOrganizations?stateOrganizationId=#{arg1}", "application/json", @sessionId)
  assert(@res.code == 200)
  json = JSON.parse(@res.body)
  puts @res.headers
  puts json
  if json.is_a? Array
    @lea = json[0]['id']
  else
    @lea = json['id']
  end
end

Then /I get the id for the staff "(.*?)"$/ do |arg1|
  restHttpGet("/v1/staff?staffUniqueStateId=#{arg1}", "application/json", @sessionId)
  assert(@res.code == 200)
  json = JSON.parse(@res.body)
  puts @res.headers
  puts json
  if json.is_a? Array
    @staff = json[0]['id']
  else
    @staff = json['id']
  end
end

Then /^there are "(.*?)" edOrgs for the "(.*?)" application in the production applicationAuthorization collection$/ do |expected_count, application|
   db = @conn.db("sli")
   coll = db.collection("application")
   record = coll.find_one("body.name" => application)
   appId = record["_id"]
   db = @conn.db(convertTenantIdToDbName(Property['tenant']))
   coll = db.collection("applicationAuthorization")
   record = coll.find_one("body.applicationId" => appId.to_s)
   body = record["body"]
   edorgsArray = body["edorgs"]
   edorgsArrayCount = edorgsArray.count
   assert(edorgsArrayCount == expected_count.to_i, "Education organization count mismatch in applicationAuthorization collection. Expected #{expected_count}, actual #{edorgsArrayCount}")
end

Then /^"(.*?)" is enabled for "(.*?)" production education organizations$/ do |app, edOrgCount|
     db = @conn.db("sli")
     coll = db.collection("application")
     record = coll.find_one("body.name" => app)
     puts record.to_s
     body = record["body"]
     puts body.to_s
     edorgsArray = body["authorized_ed_orgs"]
     edorgsArrayCount = edorgsArray.count
     assert(edorgsArrayCount == edOrgCount.to_i, "Education organization count mismatch in application collection. Expected #{edOrgCount}, actual #{edorgsArrayCount}")
end

When /^I (enable|disable) the educationalOrganization "([^"]*?)" in production$/ do |action,edOrgName|
  # Note: there should be no need to disable table scan since there is an index on educationOrganization.nameOfInstitution
  db = @conn[convertTenantIdToDbName(Property['tenant'])]
  coll = db.collection("educationOrganization")
  record = coll.find_one("body.nameOfInstitution" => edOrgName.to_s)
  edOrgId = record["_id"]
  elt = @driver.find_element(:id, edOrgId)
  assert(elt, "Educational organization element for '" + edOrgName + "' (" + edOrgId + ") not found")
  assert(action == "enable" && !elt.selected? || action == "disable" && elt.selected?, "Cannot " + action + " educationalOrganization element with id '" + edOrgId + "' whose checked status is " + elt.selected?.to_s())
  elt.click()
end

And /^I manually navigate to "(.*?)" in admin$/ do |endpoint|
    @driver.get(Property['admintools_server_url'] + "/" + endpoint)
end

Then /^I enable all education organizations for this app$/ do
  @driver.find_element(:id, 'root').click
end

Then /^I click on the checkbox labeled "([^"]*)"$/ do |text|
  @driver.find_element(:xpath, "//span[text()='#{text}']/preceding-sibling::input[@type='checkbox']").click
end
