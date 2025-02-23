require 'mongo'
require 'securerandom'
require 'rumbster'
require 'message_observers'

Before do
  @entity_type_to_uri = {
      "studentAssessment" => "studentAssessments",
      "studentSchoolAssociation" => "studentSchoolAssociations",
      "teacherSectionAssociation" => "teacherSectionAssociations",
      "session" => "sessions",
      "gradingPeriod" => "gradingPeriods",
      "courseOffering" => "courseOfferings",
      "course" => "courses"
}
end

Given /^I am logged in using "([^\"]*)" "([^\"]*)" to realm "([^\"]*)"$/ do |user, pass, realm|
  @user = user
  @passwd = pass
  @realm = realm
  idpRealmLogin(@user, @passwd, @realm)
end

Given /^format "([^\"]*)"$/ do |fmt|
  ["application/json", "application/json;charset=utf-8", "application/xml", "text/plain", "application/vnd.slc.full+json", "application/vnd.slc+json", "application/vnd.slc.full+json;charset=utf-8", "application/vnd.slc+json;charset=utf-8"].should include(fmt)
  @format = fmt
end

Given /^I want to use format "([^\"]*)"$/ do |fmt|
  [
    'application/json', 
    'application/json;charset=utf-8', 
    'application/xml', 
    'text/plain', 
    'application/vnd.slc.full+json', 
    'application/vnd.slc+json', 
    'application/vnd.slc.full+json;charset=utf-8', 
    'application/vnd.slc+json;charset=utf-8'
  ].should include(fmt)
  @format = fmt
end

# DEPRECATED use step given below this one
Then /^I should receive a return code of (\d+)$/ do |code|
  @res.code.should == code.to_i
end

Then /^the response status should be ([0-9]{3})(?:.*)$/ do |status|
  @res.code.should == status.to_i
end

Then /^I should receive an ID for the newly created ([\w-]+)$/ do |entity|
  headers = @res.raw_headers
  headers['location'].should_not be_nil, "Location header not found"
  s = headers['location'][0]
  @newId = s[s.rindex('/')+1..-1]
  @newId.should_not be_nil, "Location does not include ID"
end

When /^I navigate to GET "([^\"]*)"$/ do |uri|
  uri << "?#{@queryParams.join('&')}" if @queryParams && !@queryParams.empty?
  puts uri
  restHttpGet(uri)
  @res.should_not be_nil, "Response should not be nil"
  @res.body.should_not be_nil, "Response body does not exist"
  contentType = contentType(@res).gsub(/\s+/,"")
  jsonTypes = ["application/json", "application/json;charset=utf-8", "application/vnd.slc.full+json", "application/vnd.slc+json" "application/vnd.slc.full+json;charset=utf-8", "application/vnd.slc+json;charset=utf-8"].to_set

  @headers=@res.raw_headers.to_hash()  
  if jsonTypes.include? contentType
    @result = JSON.parse(@res.body)
    assert(@result != nil, "Result of JSON parsing is nil")
    #puts "\n\nDEBUG: common stepdef result from API call is: #{@result}"
  elsif /application\/xml/.match contentType
    doc = Document.new @res.body
    @result = doc.root
  else
    puts "Common stepdefs setting result to null"
    @result = {}
  end
end

When /^I navigate to HEAD "([^\"]*)"$/ do |uri|
  if defined? @queryParams
    uri = uri + "?#{@queryParams.join('&')}"
  end
  restHttpHead(uri)
  assert(@res != nil, "Response from rest-client GET is nil")

  @headers=@res.raw_headers.to_hash()
end

When /^I navigate to OPTIONS "([^\"]*)"$/ do |uri|
  if defined? @queryParams
    uri = uri + "?#{@queryParams.join('&')}"
  end
  restHttpOptions(uri)
  assert(@res != nil, "Response from rest-client GET is nil")

  @headers=@res.raw_headers.to_hash()
end


Given /^parameter "([^\"]*)" is "([^\"]*)"$/ do |param, value|
  step %Q{parameter "#{param}" "=" "#{value}"}
end

Given /^all parameters are cleared$/ do
  @queryParams = [] 
end

Given /^parameter "([^\"]*)" "([^\"]*)" "([^\"]*)"$/ do |param, op, value|
  if !defined? @queryParams
    @queryParams = []
  end
  @queryParams.delete_if do |entry|
    entry.start_with? param
  end
  @queryParams << URI.escape("#{param}#{op}#{value}")
end

When /^I navigate to PATCH "([^"]*)"$/ do |uri|
  data = prepareData(@format, @patch_body)
  restHttpPatch(uri, data)
  assert(@res != nil, "Response from rest-client PATCH is nil")
end

When /^I navigate to DELETE "([^"]*)"$/ do |uri|
  restHttpDelete(uri)
  assert(@res != nil, "Response from rest-client DELETE is nil")
end

Then /^I should receive a link named "([^"]*)" with URI "([^"]*)"$/ do |rel, href|
  assert(@result.has_key?("links"), "Response contains no links")
  found = false
  if !rel.nil? && !rel.empty?
    @result["links"].each do |link|
      if link["rel"] == rel && link["href"] =~ /#{Regexp.escape(href)}$/
        found = true
      end
    end
  else
    found = true
  end
   assert(found, "Link not found rel=#{rel}, href ends with=#{href}")  
end

Then /^I should not receive a link named "([^"]*)" with URI "([^"]*)"$/ do |rel, href|
  assert(@result.has_key?("links"), "Response contains no links")
  found = false
  if !rel.nil? && !rel.empty?
    @result["links"].each do |link|
      if link["rel"] == rel && link["href"] =~ /#{Regexp.escape(href)}$/
        found = true
      end
    end
  else
    found = true
  end
   assert(!found, "Link found rel=#{rel}, href ends with=#{href}")  
end


When /^I PUT the entity to "([^"]*)"$/ do |url|
  data = prepareData(@format, @result)
  restHttpPut(url, data)
  assert(@res != nil, "Response from rest-client PUT is nil '#{@res}'")
  assert(@res.body == nil || @res.body.length == 0, "Response body from rest-client PUT is not nil '#{@res.body}'")
end

When /^I try to PUT the entity to "([^"]*)"$/ do |url|
  data = prepareData(@format, @result)
  restHttpPut(url, data)
  assert(@res != nil, "Response from rest-client PUT is nil '#{@res}'")
end

When /^I POST the entity to "([^"]*)"$/ do |url|
  data = prepareData(@format, @result)
  restHttpPost(url, data)
  assert(@res != nil, "Response from rest-client POST is nil")
end

Given /^I have a "([^"]*)" SMTP\/Email server configured$/ do |live_or_mock|
  sender_email_address = "hlufhdsaffhuawiwhfkj@slidev.org"
  @email_name = "SLC Admin"
  test_port = 2525
  @live_email_mode = (live_or_mock == "live")
  
  if @live_email_mode
    @email_conf = {
      :host => Property['email_smtp_host'],
      :port => Property['email_smtp_port']
    }
  else
    @rumbster = Rumbster.new(test_port)
    @message_observer = MailMessageObserver.new
    @rumbster.add_observer @message_observer
    @rumbster.start
    @email_conf = {
      :host => '127.0.0.1',
      :port => test_port
    }
  end
  @email_conf[:sender_name] = @email_name
  @email_conf[:replacer] = { "__URI__" => "http://localhost:3000"}
  @email_conf[:sender_email_addr] = sender_email_address
end

Then /^I get a link to "(.*?)"$/ do |linkName|
  result = JSON.parse(@res.body)
  result.should_not be_nil
  links = result["links"]
  @link = nil
  for l in links do
    if l['rel'] == linkName
      @link = l["href"]
    end
  end
  @link.should_not be_nil
end

Then /^I navigate to that link$/ do
  restHttpGetAbs(@link)
  @result = JSON.parse(@res.body)
end

Given /^that dashboard has been authorized for all ed orgs$/ do
  disable_NOTABLESCAN()
  allLeaAllowApp("inBloom Dashboards")
  enable_NOTABLESCAN()
end

Given /^that databrowser has been authorized for all ed orgs$/ do
  allLeaAllowApp("inBloom Data Browser")
end

Then /^I should receive a link named "([^"]*)"$/ do |arg1|
  step "in an entity, I should receive a link named \"#{arg1}\""
end

Then /^in an entity, I should receive a link named "([^"]*)"$/ do |arg1|
  @the_link = []
  @id_link = []
  @result = JSON.parse(@res.body)
  found = false
  @result = [@result] unless @result.is_a? Array
  links = @result.map{|entity| entity["links"]}.flatten
  @result.each do |entity|
    #puts entity
    assert(entity.has_key?("links"), "Response #{entity} contains no links")
    entity["links"].each do |link|
      if link["rel"] == arg1
        @the_link.push link['href']
        @id_link.push({"id"=>entity["id"],"link"=>link["href"]})
        found = true
      end
    end
  end
  assert(found, "Link not found rel=#{arg1} only found #{links.map{|l| l['rel']}}")
end

Then /^in an entity "([^"]*)", I should receive a link named "([^"]*)"$/ do |id, arg1|
  @the_link = []
  @id_link = []
  @result = JSON.parse(@res.body)
  found = false
  @result = [@result] unless @result.is_a? Array
  @result.each do |entity|
    next if entity["id"] != id
    #puts entity
    assert(entity.has_key?("links"), "Response contains no links")
    entity["links"].each do |link|
      if link["rel"] == arg1
        @the_link.push link['href']
        @id_link.push({"id"=>entity["id"],"link"=>link["href"]})
        found = true
      end
    end
  end
  assert(found, "Link not found rel=#{arg1}")
end

Then /^the header "([^\"]*)" equals (\d+)$/ do |header, value|
  value = convert(value)
  header.downcase!
  headers = @res.raw_headers
  assert(headers != nil, "Headers came back nil.. there's a problem.")
  if @res.code >= 400
    assert(value == 0, "Received return code: #{@res.code}, but expected non-zero count (#{value}).")
  else
    assert(headers[header], "Header: #{header} not found.")
    assert(headers[header] != nil, "Header: #{header} not found.")
    resultValue = headers[header]
    assert(resultValue.kind_of?(Array), "Header: #{header} is of the wrong form.")
    assert(resultValue.length == 1, "Header: #{header} is of the wrong form.")
    singleValue = convert(resultValue[0])
    assert(singleValue == value, "Value in header: #{header} did not match. Expected: #{value}, received: #{singleValue}.")
  end
end

When /^I navigate to GET the link named "([^"]*)"$/ do |arg1|
  #Try to make test more deterministic by using ordered search
  @the_link = @the_link.sort
  @the_link.each { |link|
    restHttpGetAbs(link)
    @result = JSON.parse(@res.body)
    break if @result.length > 0
  }
end

When /^I follow the HATEOS link named "([^"]*)"$/ do |link|
  #Try to make test more deterministic by using ordered search
  #puts "\n\nDEBUG: Link is #{link}"
  restHttpGetAbs(link)
  @result = JSON.parse(@res.body)
end

Then /^I should see a count of (\d+)$/ do |arg1|
  data = JSON.parse(@res.body)
  if (@res.code == 403)
    assert(arg1.to_i == 0, "Received 403 HTML code but expected non-zero count")
  else
    assert(data.count == arg1.to_i, "Count should match (#{arg1} != #{data.count})")
  end
end

# HERE BELOW LIES IMPROVED STEP DEFS

# Set the session for the given type of user and also set up the +current_user+
Given /^I am logged in as an? (.*)$/ do |user_type|
  username, password, user_id, realm = *(credentials_for user_type)
  idpRealmLogin(username, password, realm)
  @sessionId.should_not be_nil
  restHttpGet("#{staff_endpoint}/#{user_id}")
  @res.code.should == 200
  @current_user = JSON.parse @res
end

Given /^I have a session as an? (.*)$/ do |user_type|
  @user, @passwd, user_id, @realm = *(credentials_for user_type)
  idpRealmLogin(@user, @passwd, @realm)
end

def current_user
  @current_user
end

def ed_orgs_for_staff(staff)
  ed_orgs = []
  if ed_orgs_link = staff['links'].find{|link| link['rel'] == 'getEducationOrganizations'}
    restHttpGetAbs(ed_orgs_link['href'])
    @res.code.should == 200
    ed_orgs = JSON.parse @res
  end
  ed_orgs
end

def staff_endpoint
  '/v1.5/staff'
end

# Map meaningful user types to user, password, and realm
def credentials_for(user_type)
  realm = "IL"
  users = { # logical name => [ username, password, id ]
    'tenant-level IT Administrator' => %w( rrogers    rrogers1234    85585b27-5368-4f10-a331-3abcaf3a3f4c ),
    'school-level IT Administrator' => %w( akopel     akopel1234     cdc2fe5a-5e5d-4b10-8caa-8f3be735a7d4 ),
    'local-level IT Administrator'  => %w( jstevenson jstevenson1234 e59d9991-9d8f-48ab-8790-59df9bcf9bc7 ),
    'school-level Educator'         => %w( rbraverman rbraverman1234 bcfcc33f-f4a6-488f-baee-b92fbd062e8d ),
    'school-level Leader'           => %w( mgonzales  mgonzales1234  4a39f944-c238-4787-965a-50f22f3a2d9c ),
    'ingestion user'                => ['ingestionuser', 'ingestionuser1234', nil, 'SLI']
  }
  user = users.detect{|k,v| k.downcase == user_type.downcase}
  user.should_not be_nil, "Unknown user type: #{user_type}"
  creds = user.last
  # username, password, user_id,  realm
  [creds[0], creds[1], creds[2], creds[3] || realm]
end

Given /^I navigated to the Data Browser Home URL$/ do
  @driver.get Property['databrowser_server_url']
end

Given /^I was redirected to the Realm page$/ do
  assertWithWait("Failed to navigate to Realm chooser") {@driver.title.index("Choose your realm") != nil}
end

Given /^I click on the realm page Go button$/ do
  assertWithWait("Could not find the Go button")  { @driver.find_element(:id, "go") }
  @driver.find_element(:id, "go").click
end

When /^I choose realm "([^"]*)" in the drop\-down list$/ do |arg1|
  select = Selenium::WebDriver::Support::Select.new(@driver.find_element(:tag_name, "select"))
  select.select_by(:text, arg1)
end

Then /^I should be redirected to the Data Browser home page$/ do
  assertWithWait("Failed to be directed to Databrowser's Home page")  {@driver.page_source.include?("Welcome to the inBloom, inc. Data Browser")}
end

Given /^I was redirected to the SLI IDP Login page$/ do
  assertWithWait("Was not redirected to the IDP login page")  { @driver.find_element(:name, "Login.Submit") }
end

When /^I enter "([^"]*)" in the username text field$/ do |arg1|
  @driver.find_element(:id, "username").send_keys arg1
end

When /^I enter "([^"]*)" in the password text field$/ do |arg1|
  @driver.find_element(:id, "password").send_keys arg1
end

When /^I click the Go button$/ do
  @driver.find_element(:id, "submit").click
end

Given /^the following collections are empty in datastore:$/ do |table|
  DbClient.new(:tenant => @tenant || 'Midgar').open do |db_client|
    table.hashes.map do |row|
      db_client.remove_all row['collectionName']
    end
  end
end

Given /^the "([^"]*)" collection is empty in the SLI datastore$/ do |collection|
  DbClient.new.for_sli.open {|db| db.remove_all collection}
end

Then /^I should be able to use the token to make valid API calls$/ do
  restHttpGet('/system/session/check', 'application/json')
  @res.should_not be_nil
  data = JSON.parse(@res.body)
  data['authenticated'].should be_true
end

Then /^I am redirected to the sample app home page$/ do
  assertWithWait("Failed to navigate to Sample App home page") {@driver.title.index("List of Students") != nil}
end

When /^I navigate to the sample app page$/ do
  @driver.get Property['sampleApp_server_address']+"sample"
  begin
    @driver.switch_to.alert.accept
  rescue
  end
end
