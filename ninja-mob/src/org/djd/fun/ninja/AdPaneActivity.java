package org.djd.fun.ninja;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;
import com.singly.sdk.APICallListener;
import com.singly.sdk.AuthorizedListener;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: acorn
 * Date: 9/29/12
 * Time: 4:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class AdPaneActivity extends Activity {

  private WebView webView;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_adpanel);
    webView = (WebView) super.findViewById(R.id.wv_adpanel);
    webView.getSettings().setBuiltInZoomControls(true);

    findViewById(R.id.btn_get_ad).setOnClickListener(new MyOnClickListener());

//    Toast.makeText(this, getAccountId(), Toast.LENGTH_SHORT).show();
  }

  private class MyOnClickListener implements View.OnClickListener {

    public void onClick(View v) {

      webView.scrollTo(0, 0);
      webView.loadData("<a href='http://www.google.com'><img src='https://www.google.com/images/srpr/logo3w.png'/></a>", HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);
      webView.loadData("<a href='http://www.google.com'><img src='" + createUrl() + "'/></a>", HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);

    }
  }

  private String getAccountId() {
    SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);
    return sharedPreferences.getString(Constants.PREF_KEY_ACCOUNT_ID, "No_AccountId_found_in_shared_preference.");
  }

  private String createUrl() {
    return Constants.AD_SERVE_ENDPOINT + "?appId=" + Constants.APP_ID + "&account=" + getAccountId() + "/";
  }
}