package org.djd.fun.ninja;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: acorn
 * Date: 9/27/12
 * Time: 11:42 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Constants {
  public static final String APPLICATION_JSON = "application/json";

  public static final String CLIENT_ID = "08bbf7c48b591aeee5f57ac18bb6adbf";
  public static final String CLIENT_SECRET = "f139d79372a461ce2b5ea6affede2df7";

  public static final String SHARED_PREF_NAME = "NINJA-MOB-SHARED-PREF-NAME";
  public static final String PREF_KEY_ACCOUNT_ID = "PREF_KEY_ACCOUNT_ID";
  public static final String APP_ID = "ninja-mob";
  // ?appId=ninja-mob&account=7ca4317f9af2216c8ced0ad386298c3f/
  public static final String AD_SERVE_ENDPOINT = "http://singlytics-adserve.herokuapp.com/ad";
  public static final String HYPERION_ENDPOINT = "http://singlytics-hyperion.herokuapp.com/profile/" + APP_ID + "/";

  public static final String END_POINT_SERVICES = "/services";
  public static final String END_POINT_PROFILES = "/profiles";
  public static final String END_POINT_FACEBOOK = "/services/facebook";
  public static final String END_POINT_FACEBOOK_SELF = "/services/facebook/self";
  public static final String END_POINT_GITHUB = "/services/github";
  public static final String END_POINT_GITHUB_SELF = "/services/github/self";
  public static final String END_POINT_LINKEDIN = "/services/linkedin";
  public static final String END_POINT_LINKEDIN_SELF = "/services/linkedin/self";
  public static final String SERVICE_NAME_FACEBOOK = "facebook";
  public static final String SERVICE_NAME_FLICKR = "flickr";
  public static final String SERVICE_NAME_GITHUB = "github";
  public static final String SERVICE_NAME_LINKEDIN = "linkedin";


  public static final Set<String> PROFILE_ATTRIBUTES_FILTER = Constants.createFilter();

  private static final Set<String> createFilter() {
    Set<String> filter = new HashSet<String>();
    filter.add("id");
    filter.add("bio");
    filter.add("birthday");
    filter.add("gender");
    filter.add("timezone");
    filter.add("languages");
    filter.add("locale");
    filter.add("location");
    return filter;
  }
}