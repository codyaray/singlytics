package org.djd.fun.ninja;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
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
  private static final String TAG = AdPaneActivity.class.getSimpleName();
  private static final String APP_ID_ACCT = "?appId=" + Constants.APP_ID + "&account=";
  private String accountId;
  private WebView webView;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_adpanel);
    webView = (WebView) super.findViewById(R.id.wv_adpanel);
    webView.getSettings().setBuiltInZoomControls(true);

    findViewById(R.id.btn_get_ad).setOnClickListener(new MyOnClickListener());
    accountId = Util.getAccountId(this);

    webView.scrollTo(0, 0);
    String html = getAdHref();
    Log.i(TAG, "html:" + html);
    webView.loadData(html, HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);

  }

  private class MyOnClickListener implements View.OnClickListener {
    public void onClick(View v) {
      popUp("You've got ad!");
    }
  }

  private void popUp(String msg) {
    new AlertDialog.Builder(this).setMessage(msg).create().show();
  }

  private String createUrl(String adServEndpoint) {
    return adServEndpoint + APP_ID_ACCT + accountId + "/?u="+System.currentTimeMillis();
  }

  private String getAdHref() {
    return String.format("<a href='%s'/><img src='%s'/></a>",
        createUrl(Constants.AD_SERVE_ENDPOINT_HREF),
        createUrl(Constants.AD_SERVE_ENDPOINT_IMG));
  }

  @Override
  protected void onResume() {
    super.onResume();
    Util.dispatch(this, Util.createEventStartUrl(this, TAG));
  }

  @Override
  protected void onPause() {
    super.onPause();
    Util.dispatch(this, Util.createEventCloseUrl(this, TAG));
  }

}