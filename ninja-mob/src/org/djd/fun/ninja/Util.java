package org.djd.fun.ninja;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created with IntelliJ IDEA.
 * User: acorn
 * Date: 9/29/12
 * Time: 10:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class Util {

  public static String getAccountId(Context context) {
    SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF_NAME, Activity.MODE_PRIVATE);
    return sharedPreferences.getString(Constants.PREF_KEY_ACCOUNT_ID, "No_AccountId_found_in_shared_preference.");
  }

  public static String createEventStartUrl(Context context, String eventName) {
    return Constants.HYPERION_ENDPOINT_EVENT + Util.getAccountId(context) + "/"+ eventName + "-start/";
  }

  public static String createEventCloseUrl(Context context, String eventName) {
    return Constants.HYPERION_ENDPOINT_EVENT + Util.getAccountId(context) + "/" + eventName + "-close/";
  }

  public static void dispatch(Context context, String eventUrl) {
    Intent intent = new Intent(context, EventDispatchService.class);
    intent.putExtra(Constants.EVENT_URL_INTENT_EXTRA_KEY, eventUrl);
    context.startService(intent);
  }
}
