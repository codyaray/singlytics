package com.singly.sdk;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.singly.util.JSON;
import com.singly.util.SinglyHttpClient;

/**
 * A client that handles all authentication with various services through Singly
 * and calls made to the Singly API.
 *
 * @see https://singly.com/docs/api
 */
public class SinglyClient {

  public static final String AUTH_REDIRECT = "singly://success";

  private static final String BASE_URL = "api.singly.com";
  private static final String TAG = SinglyClient.class.getSimpleName();

  private String clientId;
  private String clientSecret;
  private SharedPreferences prefs;
  private Context context;
  private SinglyHttpClient httpClient;

  /**
   * Returns true if the context has permission to access the network state and
   * access the internet.
   *
   * @return True if the context has the correct permissions.
   */
  private boolean hasNetworkPermissions() {

    // get permissions for accessing the internet and network state
    int internetPerm = context
        .checkCallingOrSelfPermission(Manifest.permission.INTERNET);
    int networkStatePerm = context
        .checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE);

    // are those permissions allowed
    boolean internetAllowed = internetPerm == PackageManager.PERMISSION_GRANTED;
    boolean networkAllowed = networkStatePerm == PackageManager.PERMISSION_GRANTED;

    // if they are check if we are connected to the network
    return (internetAllowed && networkAllowed);
  }

  /**
   * Returns true if the app is connected to the internet, meaning the network
   * is connected.
   *
   * @return True if the context can connect to the internet.
   */
  private boolean isConnectedToInternet() {

    // check if we are connected to the network
    ConnectivityManager cm = (ConnectivityManager) context
        .getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo netInfo = cm.getActiveNetworkInfo();
    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
      return true;
    }

    // not connected to the network
    return false;
  }

  /**
   * Creates a url using the base singly api url, the path, and the query
   * parameters specified.
   * <p/>
   * The url is assumed to be in UTF-8 format.  The query parameters are
   * not required.
   *
   * @param path    The url path.
   * @param qparams The optional url query parameters.
   * @return A formatted, UTF-8 singly url string.
   */
  private String createURL(String path, List<NameValuePair> qparams) {

    String formattedUrl = null;
    try {

      // query parameters are optional
      String query = null;
      if (qparams != null && qparams.size() > 0) {
        query = URLEncodedUtils.format(qparams, "UTF-8");
      }

      // create the formatted UTF-8 url, always https and using base singly
      URI uri = URIUtils.createURI("https", BASE_URL, -1, path, query, null);
      formattedUrl = uri.toASCIIString();
    } catch (URISyntaxException e) {
      Log.e(TAG, "Error creating url: " + path, e);
    }

    return formattedUrl;
  }

  /**
   * Creates a new SinglyClient instance using the context, Singly client id,
   * and Singly client secret passed.
   *
   * @param context      The current Android context.
   * @param clientId     The Singly client id.
   * @param clientSecret The Singly client secret.
   */
  public SinglyClient(Context context, String clientId, String clientSecret) {

    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.context = context;
    this.prefs = context.getSharedPreferences("singly", Context.MODE_PRIVATE);
    this.httpClient = new SinglyHttpClient();
//    httpClient.initialize();
  }

  /**
   * Called by Android activities to shutdown the Singly client and release
   * resources held.  Usually called during onStop lifecycle event.
   */
  public void shutdown() {
    httpClient.shutdown();
    httpClient = null;
  }

  /**
   * Called to authorize a user through singly for a specific service.
   * <p/>
   * Authorize will open a WebView to the service to authorize the user.  Once
   * the user authenticates with the service, calls to get an oauth access
   * token are performed in an AsyncTask.  You should be able to perform any
   * main thread activities, such as opening dialogs, in the listener callback
   * methods.
   *
   * @param service  The service to authorize the user, facebook for example.
   * @param callback The listener class used to callback at different point
   *                 during the authorization process.
   */
  public void authorize(final String service, final AuthorizedListener callback) {

    // start of the authorization process
    callback.onStart();

    // fail early if no permissions
    if (!hasNetworkPermissions()) {
      callback.onError(AuthorizedListener.Errors.NO_NETWORK_PERMISSIONS);
      return;
    }

    // fail early if no internet access
    if (!isConnectedToInternet()) {
      callback.onError(AuthorizedListener.Errors.NO_INTERNET_ACCESS);
      return;
    }

    // create the query parameters
    List<NameValuePair> qparams = new ArrayList<NameValuePair>();
    qparams.add(new BasicNameValuePair("client_id", clientId));
    qparams.add(new BasicNameValuePair("redirect_uri", AUTH_REDIRECT));
    qparams.add(new BasicNameValuePair("service", service));

    // create the authorization url
    String authorizeUrl = createURL("/oauth/authorize", qparams);
    if (authorizeUrl == null) {
      callback.onError(AuthorizedListener.Errors.AUTHORIZE_SERVICE_URL);
      return;
    }

    Log.d(TAG, authorizeUrl);
    new AuthDialog(context, authorizeUrl, new AuthDialog.DialogListener() {

      @Override
      public void onProgress(int progress) {
        callback.onProgress(progress);
      }

      @Override
      public void onPageLoaded() {
        callback.onPageLoaded();
      }

      public void onAuthorized(final String successUrl) {

        AsyncTask authTask = new AsyncTask<Object, Void, Boolean>() {

          @Override
          protected Boolean doInBackground(Object... params) {

            // get the auth code from the auth dialog return query parameter
            Uri uri = Uri.parse(successUrl);
            String authCode = uri.getQueryParameter("code");

            // create the access token url
            List<NameValuePair> qparams = new ArrayList<NameValuePair>();
            qparams.add(new BasicNameValuePair("client_id", clientId));
            qparams.add(new BasicNameValuePair("client_secret", clientSecret));
            qparams.add(new BasicNameValuePair("code", authCode));

            // access token url, parameters are added to the post instead of url
            String accessTokenUrl = createURL("/oauth/access_token", null);

            String accessToken = null;
            try {

              // do the post to get the access token
              byte[] responseBytes = httpClient.post(accessTokenUrl, qparams);
              JSONObject root = JSON.parse(new String(responseBytes));
              accessToken = JSON.getString(root, "access_token", null);

              // save the access token in the shared preferences
              if (accessToken != null) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("accessToken", accessToken);
                editor.commit();
              } else {

                // no access token, authorization failed
                return false;
              }

              // authorization succeeded
              return true;
            } catch (Exception e) {

              // exception, authorization failed
              return false;
            }
          }

          @Override
          protected void onPostExecute(Boolean result) {

            if (result) {
              callback.onAuthorized();
            } else {
              callback.onError(AuthorizedListener.Errors.NO_ACCESS_TOKEN);
            }
          }
        };

        // execute the auth task
        authTask.execute();
      }

      @Override
      public void onError() {
        callback.onError(AuthorizedListener.Errors.AUTHENTICATION_ERROR);
      }

      @Override
      public void onCancel() {
        callback.onCancel();
      }

    }).show();
  }

  /**
   * Performs an api call to the Singly API.
   * <p/>
   * The {@link #authorize(String, AuthorizedListener)} method muse have been
   * called for at least one service before any api calls are made.  Once
   * authorized the application stores an access token used to call the
   * Singly api.  That access token is then appended to any api calls made
   * through this method.
   * <p/>
   * This method performs the api call in an asynchronous method and returns
   * any response as a JSONObject to the APICallListener callback.  To perform
   * the call an AsyncTask is used with the success/error callbacks happening
   * in the PostExecute method.  This means clients should be fine performing
   * UI methods, such as Toasts and Dialogs, within the APICallListener callback
   * methods.
   *
   * @param endpoint   The singly api call to make.
   * @param parameters The api call parameters.  You do not need to include
   *                   the access token, that will be automatically appended.
   * @param callback   The listener class used to callback on success or error
   *                   of the api call.
   * @see https://singly.com/docs/api For documentation on Singly api calls.
   */
  public void apiCall(final String endpoint,
                      final Map<String, String> parameters, final APICallListener callback) {

    // fail early if no permissions
    if (!hasNetworkPermissions()) {
      callback.onError("This application doesn't have permissions to acces" +
          "the internet or network state.");
      return;
    }

    // fail early if no internet access
    if (!isConnectedToInternet()) {
      callback.onError("The application cannot connect to the internet.");
      return;
    }

    AsyncTask apiCallTask = new AsyncTask<Object, Integer, JSONObject>() {

      @Override
      protected JSONObject doInBackground(Object... params) {

        // convert the query parameters to name value pairs
        List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        if (parameters != null) {
          for (Map.Entry<String, String> entry : parameters.entrySet()) {
            BasicNameValuePair nameVal = new BasicNameValuePair(entry.getKey(),
                entry.getValue());
            qparams.add(nameVal);
          }
        }

        // add the access token to the parameters for every api call
        qparams.add(new BasicNameValuePair("access_token", prefs.getString(
            "accessToken", "")));

        try {

          // create the endpoint url
          String apiCallUrl = createURL(endpoint, qparams);

          // perform the request to the api
          Log.d(TAG, "API call to: " + apiCallUrl);
          byte[] responseBytes = httpClient.get(apiCallUrl);

          // pare and return the JSON response
          String response = new String(responseBytes);
          Log.d(TAG, response);

          // ninja: bug if response is JsonArray it pukes.

          JSONObject root = JSON.parse(response);

          return root;
        } catch (Exception e) {

          // error during the api call, return null which singles error
          String message = e.getMessage();
          Log.e(TAG, "Api call error:" + endpoint + " " + message);
          return null;
        }

      }

      @Override
      protected void onPostExecute(JSONObject result) {

        if (result != null) {
          callback.onSuccess(result);
        } else {
          callback.onError("Api call error");
        }
      }
    };

    // execute the api call in an async task
    apiCallTask.execute();
  }

  public Context getContext() {
    return context;
  }

  public void setContext(Context context) {
    this.context = context;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }

}
