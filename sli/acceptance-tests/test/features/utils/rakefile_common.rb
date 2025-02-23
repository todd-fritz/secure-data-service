require 'ldapstorage'
require 'digest/sha1'
require 'yaml'
require 'rest-client'
require_relative 'db_client'

def cleanUpLdapUser(user_email)
  ldap = LDAPStorage.new(
      Property[:ldap_hostname], Property[:ldap_port],
      Property[:ldap_base], Property[:ldap_admin_user],
      Property[:ldap_admin_pass], Property[:ldap_use_ssl]
  )
  cleanUpUser(user_email, ldap)
end

def cleanUpMiniSandboxLdapUser(user_email)
  # TODO: Once properties.yml is refactored and cleaned; these custom ldap properties probably go away
  ldap_sb = LDAPStorage.new(Property['minisb_ldap_hostname'], Property['minisb_ldap_port'],
                         Property['minisb_ldap_base'], Property['minisb_ldap_admin_user'],
                         Property['minisb_ldap_admin_pass'], Property['minisb_ldap_use_ssl'])
  cleanUpUser(user_email, ldap_sb)
end

def cleanUpUser(user_email, ldap)
  puts "Attempting to remove user: #{user_email}"
  ldap.get_user_groups(user_email).each do |group_id|
    ldap.remove_user_group(user_email, group_id)
  end 

  ldap.delete_user(user_email)
end

def allLeaAllowApp(appName)
  allLeaAllowAppForTenant(appName, 'Midgar')
  allLeaAllowAppForTenant(appName, 'Hyrule')
end

#
# Replace the doc for the app with the given name in
# "applicationAuthorization" so that the app is authorized for use by
# ALL edOrgs (appearing in "educationOrganization") in the given
# tenant
#
def allLeaAllowAppForTenant(app_name, tenant)
  disable_NOTABLESCAN
  db = DbClient.new.for_sli
  app = db.find_one('application', {'body.name' => app_name})
  raise "ERROR: Could not find an application named #{app_name}" unless app

  app_id = app["_id"]
  puts("The app #{app_name} id is #{app_id}") if ENV['DEBUG']

  db.for_tenant(tenant)

  app_auth_coll = db.collection('applicationAuthorization')
  needed_ed_orgs = db.find_ids('educationOrganization').map{ |id| {'authorizedEdorg' => id} }

  app_auth_coll.remove('body.applicationId' => app_id)
  new_app_auth = {
    '_id' => "2012ls-#{SecureRandom.uuid}",
    'body' => {
      'applicationId' => app_id,
      'edorgs' => needed_ed_orgs
    },
    'metaData' => {
      'tenantId' => tenant
    },
    'type' => 'applicationAuthorization'
  }
  app_auth_coll.insert(new_app_auth)
  db.close
  enable_NOTABLESCAN
end

def authorize_ed_org(app_name, tenant='Midgar')
  enable_NOTABLESCAN

  app_id = DbClient.new.for_sli.open {|db| db.find_one(:application, 'body.name' => app_name)}['_id']

  ed_org_ids = DbClient.new(:tenant => tenant).open do |db|
    ed_orgs = db.find_one(:applicationAuthorization, 'body.applicationId' => app_id)['body']['edorgs']
    ed_orgs.map{|edorg| edorg['authorizedEdorg']}
  end

  DbClient.new.for_sli.open do |db|
    ed_org_ids.each do |ed_org_id|
      db.update :application, {'_id' => app_id}, {'$push' => {'body.authorized_ed_orgs' => ed_org_id}}
    end
  end

  disable_NOTABLESCAN
end

def randomizeRcProdTenant()
  Property.update('tenant', "#{Property['tenant']}_#{Time.now.to_i}")
end

def randomizeRcSandboxTenant()
  email = Property['developer_sb_email_imap_registration_user_email']
  email2 = Property['developer2_sb_email_imap_registration_user_email']
  
  emailParts = email.split("@")
  randomEmail = "#{emailParts[0]}+#{Random.rand(1000)}"+"@"+"#{emailParts[1]}"
  Property.update('developer_sb_email_imap_registration_user_email', randomEmail)
  Property.update('sandbox_tenant', randomEmail)
  
  email2Parts = email2.split("@")
  randomEmail2 = "#{email2Parts[0]}+#{Random.rand(1000)}"+"@"+"#{email2Parts[1]}"
  Property.update('developer2_sb_email_imap_registration_user_email', randomEmail2)
end

def convertTenantIdToDbName(tenantId)
  return Digest::SHA1.hexdigest tenantId
end


def testTls(url, token, client_id, path)
  puts "Loading Key and Certificate for client ID #{client_id}"
  client_cert = OpenSSL::X509::Certificate.new File.read File.expand_path("../keys/#{client_id}.crt", __FILE__)
  private_key = OpenSSL::PKey::RSA.new File.read File.expand_path("../keys/#{client_id}.key", __FILE__)

  headers = {:accept => "application/x-tar", :Authorization => "bearer #{token}"}

  res = RestClient::Request.execute(:method => :get, :url => url, :headers => headers, :ssl_client_cert => client_cert, :ssl_client_key => private_key) {|response, request, result| response }
  puts "Return Code = #{res.code}, Header = #{res.raw_headers}"
  if res.code > 300
    puts "Error Response = #{res.body}"
    return
  end

  file = Dir.pwd + "/#{path}"
  File.delete(file) if File.exists?(file)
  File.open(file, "a") do |outf|
    outf << res.body
  end
  puts "File was saved to #{Dir.pwd + "/#{path}"}"
end

# Property Loader class
class Property
  properties_file = ENV['PROPERTIES'] || File.join(File.dirname(__FILE__),'properties.yml')
  puts "Loading properties from #{properties_file}"
  @@yml ||= YAML.load_file properties_file

  def self.getProps
    self.updateHash
    @@yml
  end

  def self.[](key)
    key = key.to_s # in case key is a symbol
    ENV[key] || @@yml[key]
  end

  def self.update(key, val)
    ENV[key] = val
  end

  private

  def self.updateHash
    @@yml.each do |key, value|
      @@yml[key] = ENV[key] if ENV[key]
    end
  end
end

##############################################################################
# turn ON --notablescan MongoDB flag, if set in ENV
##############################################################################
def enable_NOTABLESCAN
  DbClient.disallow_table_scan! if ENV['TOGGLE_TABLESCANS']
end

##############################################################################
# turn OFF --notablescan MongoDB flag, if set in ENV
##############################################################################
def disable_NOTABLESCAN
  DbClient.allow_table_scan! if ENV['TOGGLE_TABLESCANS']
end
