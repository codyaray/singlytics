package org.djd.fun.ninja;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.singly.util.HttpException;
import com.singly.util.SinglyHttpClient;

/**
 * Created with IntelliJ IDEA.
 * User: acorn
 * Date: 9/30/12
 * Time: 10:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class EventDispatchService extends IntentService {

  private SinglyHttpClient singlyHttpClient = new SinglyHttpClient();
  private static final String TAG = EventDispatchService.class.getSimpleName();

  public EventDispatchService(){
    super(TAG);
  }
  @Override
  protected void onHandleIntent(Intent intent) {
    try {
      singlyHttpClient.postEvent(intent.getExtras().getString(Constants.EVENT_URL_INTENT_EXTRA_KEY));
    } catch (HttpException e) {
      Log.e(TAG, e.getMessage());
    }
  }
}
