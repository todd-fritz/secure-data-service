=begin
#--

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


require "active_resource/base"
require "oauth_helper"
# require "breadcrumb"

#
# This is the standard base controller class that all others in the databrowser
# (and Rails) inherits from. We take advantage and put some common
# functionality here
#
# Namely we have a before_filter that will handle the basic oauth functionality
# to ensure you have a valid API token
#
# We also catch most of the common ActiveResource exceptions and force them to 
# display flash messages, clear your rails session, etc.
class ApplicationController < ActionController::Base
  protect_from_forgery
  ActionController::Base.request_forgery_protection_token = 'state'
  before_filter :handle_oauth
  # before_filter :handle_breadcrumb

  add_breadcrumb 'home', '/entities/home'

  rescue_from ActiveResource::ResourceNotFound, :with => :not_found
  
  rescue_from ActiveResource::UnauthorizedAccess do |exception|
    logger.debug {"401 detected"}
    logger.info { "Unauthorized Access: Redirecting..." }
    reset_session
    handle_oauth
  end
  
  rescue_from ActiveResource::ForbiddenAccess do |exception|
    logger.info { "Forbidden access" }
    respond_to do |format|
      format.html {
        flash[:error] = "Sorry, you don't have access to this page. If you feel like you are getting this page in error, please contact your administrator."
    
        # If 403 happened during login, we don't have a valid :back, so render 403 page.
        # Otherwise redirect back with the flash set
        if request.headers['referer'].nil? or !request.headers['referer'].include?(request.host)
            return render :status => :forbidden, :layout=> false, :file => "#{Rails.root}/public/403.html"
        end
        redirect_to :back
      }
      format.json { render json: { :alert => "403" } }
    end
  end
  
  rescue_from ActiveResource::ServerError do |exception|
    logger.error {"Exception on server, clearing your session."}
    reset_session
    SessionResource.auth_id = nil
    flash[:error] = "There was a problem in the API."
    #redirect_to :back
  end

  rescue_from OAuth2::Error do |exception|
    logger.error {"Oauth invalid, clearing your session."}
    reset_session
    SessionResource.auth_id = nil
    render :auth_error_page
    #redirect_to :back
  end
  
  #This method is where OAuth will finish. Basically after you send your credentials to the Api
  #it will redirect to this method to try and finish the OAuth process.
  #
  #From here we bounce you back internally to the page you originally wanted to see.
  def callback
    redirect_to session[:entry_url] unless session[:entry_url].include? '/callback'
    return
    if params[:state]
      redirectUrl = CGI::unescape(params[:state])
      redirect_to redirectUrl
      return
    end
    respond_to do |format|
      format.html {render :text => "", :status => :no_content}
    end
  end

  private

  def not_found
    logger.debug {"Not found"}
    respond_to do |format|
      format.html {
        flash[:alert] = "No resource found with id: #{params[:id] || params[:search_id]}"
        redirect_to :back
      }
      format.json { render json: { :alert => "404" } }
    end
  end
  
  def current_url
    request.url
  end

  #This method is critical.
  #This method is a before_filter that gets run on every request to ensure that
  #you have an OAuth token with the Api, and starts the process if you don't have
  #one.
  #
  #After you've successfully logged in, it makes a call to system/session/check to
  #get your name and display it in the header of the databrowser. It represents the
  #first successful call to the Api.
  def handle_oauth
    @header = nil
    @footer = nil
    SessionResource.access_token = nil
    oauth = session[:oauth]
    if oauth.nil?
      oauth = OauthHelper::Oauth.new()
      session[:entry_url] = current_url
      session[:oauth] = oauth 
    end
    if oauth.enabled?
      if oauth.token != nil
        begin
          @header = PortalHeader.get("", :isAdmin => true)
          @footer = PortalFooter.get("", :isAdmin => true)
        rescue Exception => e
          logger.warn {"We couldn't load the portal header and footer #{e.message}"}
        end
        SessionResource.access_token = oauth.token
        logger.info {"*********  Token is #{oauth.token}"}
        Check.url_type = "check"
        check = Check.get("")
        session[:full_name] = check["full_name"]
        session[:is_admin] = check["isAdminUser"]
        if !session[:is_admin] && !session[:is_admin].nil?
          logger.warn {"User is not an administrator, they cannot use this application"}
          render :auth_error_page
        end

      elsif params[:code] && !oauth.has_code
        SessionResource.access_token = oauth.get_token(params[:code])
      else
        redirect_to oauth.authorize_url + "&state=" + CGI::escape(form_authenticity_token)
      end
    else
      logger.info { "OAuth disabled."}
    end

  end

  # this routine handles adjustment of the session breadcrumbtrail, which
  # is an array of breadcrumb objects.  Each such object has a name (for
  # display) and a link; the list is presented at the top of each
  # databrowser page so the user knows what s/he clicked on to get to
  # the current page and has a way to return to a previous page 
  # with a single click.
  def handle_breadcrumb

    if request.format == "application/json"
      return
    end

    uri = URI request.url
    
    # do nothing for callbacks
    if uri.path.end_with? "/callback" then 
      return 
    end 

    trail = session[:breadcrumbtrail]

    if trail.nil? then
      # New session! Create home bread crumb!
      trail = []
      trail.push Breadcrumbhelper::Breadcrumb.new "home", uri.path, current_url
    end

    newtrail = trail.take_while{|crumb| crumb.actualLink != current_url}
    newtrail.push Breadcrumbhelper::Breadcrumb.new getBreadcrumbName(uri), uri.path, current_url
    
    session[:breadcrumbtrail] = newtrail
  end

  private
  def getBreadcrumbName(uri)

    if uri.query.to_s.include? "search" then
      # if the query string says search, assume we're searching
      return "search"
    else 
      # the last section of the path shall be our name
      name = uri.path.split("\/").last

      # Special cases:
      if (name.include? "zip")
        # if we're looking at an ingestion job
        name = name.split("-").first
      elsif (name == "ingestion")
        # if we're looking at all of the ingestion jobs
        name = "All Ingestion Jobs"
      elsif (is_id? name)
        # if we're looking at an individual record, so show what type of thing it is.
        name = uri.path.split("\/")[-2]
      end
      return name
    end
  end

  private
  def is_id?(text)
    text =~ /^\h{8}-\h{4}-\h{4}-\h{4}-\h{12}|_id$/
  end

end
