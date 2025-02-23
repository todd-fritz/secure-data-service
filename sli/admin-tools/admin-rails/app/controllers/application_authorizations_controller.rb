include EdorgTreeHelper

class ApplicationAuthorizationsController < ApplicationController
  before_filter :check_rights

  ROOT_ID = "root"
  CATEGORY_NODE_PREFIX = "cat_"
  
  #
  # Edit app authorizations
  #
  # Render a tree of all available edOrgs, w/ checkboxes
  # Each edOrg will be in one of three states:
  #      - disabled (grayed) -- not enabled by developer for the edOrg per the @apps "authorized_ed_orgs"
  #      - not authorized (unchecked) -- doesn't appear in the app authorizations' edorg list
  #      - authorized (checked) -- appears in app authorizaiton's edorg list
  #
  def edit
    if !is_admin_realm_authenticated? && is_app_authorizer?
      edorgs =  get_app_authorizer_edOrgs

        @edOrg = EducationOrganization.find(edorgs[0])
        raise NoUserEdOrgError.new "Educational organization '" + edOrgId + "' not found in educationOrganization collection" if @edOrg.nil?
    else
      # Get input objects
      edOrgId = session[:edOrgId]
      @edOrg = EducationOrganization.find(edOrgId)
      raise NoUserEdOrgError.new "Educational organization '" + edOrgId + "' not found in educationOrganization collection" if @edOrg.nil?
    end

    # Get app data
    load_apps
    appId = params[:application_authorization][:appId]
    @app = @apps_map[appId]
    raise "Application '" + appId + "' not found in sli.application" if @app.nil?

    @app_description = app_description @app
    @app_bulk_extract_description = @app.isBulkExtract ? 'Bulk Extract' : 'Non-Bulk Extract'
    @app_version_description = @app.version.blank? ? 'Unknown' : "v#{@app.version}"

    # Get developer-enabled edorgsfor the app.  NOTE: Even though the field is
    # sli.application.authorized_ed_orgs[] these edorgs are called "developer-
    # enabled" or just "enabled" edorgs.
    @enabled_ed_orgs = array_to_hash(@app.authorized_ed_orgs)

    # Get edOrgs already authorized in <tenant>.applicationAuthorization.edorgs[]
    # The are the "authorized" (by the edOrg admin) edorgs for the app
    @appAuth = ApplicationAuthorization.find(appId)
    edOrgTree = EdorgTree.new()
    @appAuth_edorgs = []
        @appAuth.edorgs.each do |edorg_entry|
          @appAuth_edorgs.push(edorg_entry.authorizedEdorg)
        end
    @edorg_tree_html = edOrgTree.get_authorization_tree_html(get_app_authorizer_edOrgs, appId, is_sea_admin?, @appAuth_edorgs || [])

    @treeHelperDebug=edOrgTree.get_debug()

  end
  

  # GET /application_authorizations
  # GET /application_authorizations.json
  def index
    @user_edorg = session[:edOrgId]

    load_apps()

    # create a list of app auth info including edorgs element, count of authorized edorgs, and app name
    @app_auth_info = []
    user_app_auths = ApplicationAuthorization.findAllInChunks
    user_app_auths.each do |auth|
      info = {}
      app = @apps_map[auth.appId]
      info[:edorg_auth] = auth
      info[:count] = get_edorg_auth_count(auth)
      info[:name] = app ? app.name : ''
      @app_auth_info.push(info)
    end

    respond_to do |format|
      format.html # index.html.erb
    end
  end

  # PUT /application_authorizations/1
  # PUT /application_authorizations/1.json
  def update

    # Only allow update by SEA  or LEA admin.
    unless is_app_authorizer?
      logger.warn 'User is not SEA or LEA admin and cannot update application authorizations'
      raise ActiveResource::ForbiddenAccess, caller
    end

    # Top level edOrg to expand
    edorg = session[:edOrgId]
    # EdOrgs selected using Tree Control
    added = params[:application_authorization][:edorgsAdded] || ''
    added.strip!
    addedEdOrgs = added.split(/,/)

    removed = params[:application_authorization][:edorgsRemoved] || ''
    removed.strip!
    removedEdOrgs = removed.split(/,/)

    # ID of app
    appId = params[:application_authorization][:appId]

    updates = {"appId" =>  appId, "authorized" => true, :edorgs => addedEdOrgs}
    @application_authorization = ApplicationAuthorization.find(params[:id])
    success = @application_authorization.update_attributes(updates)

    updates = {"appId" =>  appId, "authorized" => false, :edorgs => removedEdOrgs}
    @application_authorization = ApplicationAuthorization.find(params[:id])
    success = @application_authorization.update_attributes(updates)

    # Redirect to response page
    ApplicationAuthorization.cur_edorg = edorg
    respond_to do |format|
      if success
        format.html { redirect_to application_authorizations_path, notice: 'Application was succesfully updated.'}
        #format.html {redirect_to :action => 'index', notice: 'Application authorization was succesfully updated.'}
        format.json { head :ok }
      else
        format.html { render action: "edit" }
        format.json { render json: @application_authorization.errors, status: :unprocessable_entity }
      end
    end
  end

  private

  # NOTE this controller allows ed org super admins to enable/disable apps for their LEA(s)
  # It allows LEA(s) to see (but not change) their app authorizations
  def check_rights
    unless is_app_authorizer?
      logger.warn 'User is not lea or sea admin and cannot access application authorizations'
      raise ActiveResource::ForbiddenAccess, caller
    end
  end


  # Load up all apps into @apps_map
  def load_apps
    # Slurp all apps into @apps_map = a map of appId -> info
    @apps_map = {}
    allApps = App.findAllInChunks
    allApps.each { |app| @apps_map[app.id] = app }
  end

  # Calculates the number of authorized edorgs for the auth
  def get_edorg_auth_count(auth)
    count = 0

    return 0 if auth.nil? || auth.edorgs.nil?

    auth.edorgs.each do |auth_edorg|
      puts auth_edorg
      count += 1 if auth_edorg.authorizedEdorg
    end

    return count
  end

  # Convert array to map
  def array_to_hash(a)
    result = {}
    if !a.nil?
      a.each do|elt|
        result[elt] = true
      end
    end
    return result
  end

  # Format app description
  def app_description(a)
    s = ""
    s << a.name
    s << " (id #{a.client_id})" unless a.client_id.blank?
    s
  end

  # String is neither nil nor empty?
  def is_empty(s)
    return true if s.nil?
    return true if s.length == 0
    return false
  end
end
