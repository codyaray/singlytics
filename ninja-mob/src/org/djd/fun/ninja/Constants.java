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
  public static final String AD_SERVE_ENDPOINT_IMG = "http://singlytics-adserve.herokuapp.com/ad/img";
  public static final String AD_SERVE_ENDPOINT_HREF = "http://singlytics-adserve.herokuapp.com/ad/href";
  public static final String HYPERION_ENDPOINT_PROFILE = "http://singlytics-hyperion.herokuapp.com/profile/" + APP_ID + "/";
  public static final String HYPERION_ENDPOINT_EVENT = "http://singlytics-hyperion.herokuapp.com/event/" + APP_ID + "/";

  public static final String END_POINT_SERVICES = "/services";
  public static final String END_POINT_PROFILES = "/profiles";
  public static final String END_POINT_FACEBOOK = "/services/facebook";
  public static final String END_POINT_FACEBOOK_SELF = "/services/facebook/self";
  public static final String END_POINT_GITHUB = "/services/github";
  public static final String END_POINT_GITHUB_SELF = "/services/github/self";
  public static final String END_POINT_LINKEDIN = "/services/linkedin";
  public static final String END_POINT_LINKEDIN_SELF = "/services/linkedin/self";

  public static final String END_POINT_TWITTER= "/services/twitter";
  public static final String END_POINT_TWITTER_SELF = "/services/twitter/self";
  public static final String SERVICE_NAME_FACEBOOK = "facebook";
  public static final String SERVICE_NAME_FLICKR = "flickr";
  public static final String SERVICE_NAME_GITHUB = "github";
  public static final String SERVICE_NAME_LINKEDIN = "linkedin";
  public static final String SERVICE_NAME_TWITTER = "twitter";


  public static final Set<String> PROFILE_ATTRIBUTES_FILTER = Constants.createFilter();

  private static final Set<String> createFilter() {
    Set<String> filter = new HashSet<String>();
    filter.add("id");
    filter.add("bio");
    filter.add("birthday");
    filter.add("dateOfBirth");
    filter.add("description");
    filter.add("lang");
    filter.add("gender");
    filter.add("timezone");
    filter.add("languages");
    filter.add("locale");
    filter.add("location");
    filter.add("utc_offset");
    return filter;
  }
}