package no.incent.viking.receiver;

import no.incent.viking.R;
import no.incent.viking.util.HttpRequest;
import no.incent.viking.util.PreferenceWrapper;

import java.util.List;
import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.os.SystemClock;
import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.util.Log;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class OrderStatusReceiver extends BroadcastReceiver {
	private final String TAG = "VIKING";
	private Context context;
	private static final String ORDER_STATUS_RECEIVER = "no.incent.viking.ORDER_STATUS_RECEIVER";
	private static AlarmManager alarmManager;
	private static PendingIntent mOrderStatusReceiverIntent;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		final PreferenceWrapper prefsWrapper = new PreferenceWrapper(context);
		
		this.context = context;
		
		if(prefsWrapper.getPreferenceIntValue(PreferenceWrapper.ORDER_STATUS) == PreferenceWrapper.ORDER_STATUS_RECEIVED &&
				prefsWrapper.getPreferenceStringValue(PreferenceWrapper.ORDER_MISSION) != null) {
			new Thread(new Runnable() {
				public void run() {
					int status = 0;
					
					if((status = checkOrderAssistanceStatus(prefsWrapper.getPreferenceStringValue(
							PreferenceWrapper.ORDER_MISSION))) != PreferenceWrapper.ORDER_STATUS_RECEIVED) {
						prefsWrapper.setPreferenceIntValue(PreferenceWrapper.ORDER_STATUS, status);
						prefsWrapper.setPreferenceStringValue(PreferenceWrapper.ORDER_MISSION, null);
						OrderStatusReceiver.stopAlarm();
					}
				}
			}).start();
		}
	}
	
	public static void startAlarm(Context context) {
		alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(ORDER_STATUS_RECEIVER);
		mOrderStatusReceiverIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		
		int type = AlarmManager.ELAPSED_REALTIME_WAKEUP;
		long interval = 60 * 1000;
		long triggerTime = SystemClock.elapsedRealtime() + interval;
		
		alarmManager.setRepeating(type, triggerTime, interval, mOrderStatusReceiverIntent);
	}
	
	public static void stopAlarm() {
		if(alarmManager != null && mOrderStatusReceiverIntent != null)
			alarmManager.cancel(mOrderStatusReceiverIntent);
	}
	
	private int checkOrderAssistanceStatus(String mission) {
		HttpRequest request = HttpRequest.getInstance();
		List<NameValuePair> queries = new ArrayList<NameValuePair>();
		
		queries.add(new BasicNameValuePair("mission", mission));
		
		String result = request.send(context.getString(R.string.api_url) + "road_assistance_hentoppdrag/json", queries, HttpRequest.POST);
		Log.i(TAG, "status: " + result);
		if(result != null) {
			try {
				JSONObject json = new JSONObject(result);
				JSONArray oppdragrekords = json.getJSONArray("oppdragrekords");
				
				if(oppdragrekords.length() > 0) {
					String status = oppdragrekords.getJSONObject(0).getString("Status");
					
					if(status.equals("RECEIVED")) {
						return PreferenceWrapper.ORDER_STATUS_RECEIVED;
					} else if(status.equals("ORDERED")) {
						return PreferenceWrapper.ORDER_STATUS_ORDERED;
					} else if(status.equals("CANCELLED")) {
						return PreferenceWrapper.ORDER_STATUS_CANCELLED;
					}
				}
			} catch(JSONException ex) {
				Log.e(TAG, ex.getMessage());
			}
		}
		
		return PreferenceWrapper.ORDER_STATUS_RECEIVED;
	}
}
