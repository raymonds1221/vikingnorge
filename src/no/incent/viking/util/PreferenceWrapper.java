package no.incent.viking.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceWrapper {
	private SharedPreferences sharedPrefs;
	public static final String ORDER_STATUS = "order_status";
	public static final String ORDER_MISSION = "mission";
	public static final String ORDER_STATUS_CLOSED = "order_status_closed";
	public static final String IS_LOGGED_IN = "is_logged_in";
	public static final String OWNER_ID = "owner_id";
	public static final String HAS_USE_LOCATION = "has_use_location";
	public static final String USE_LOCATION = "use_locatioin";
	public static final int ORDER_STATUS_ORDERED = 1;
	public static final int ORDER_STATUS_CANCELLED = 2;
	public static final int ORDER_STATUS_RECEIVED = 3;
	
	public PreferenceWrapper(Context context) {
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	public void setPreferenceIntValue(String key, int value) {
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	
	public int getPreferenceIntValue(String key) {
		return sharedPrefs.getInt(key, 0);
	}
	
	public void setPreferenceStringValue(String key, String value) {
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public String getPreferenceStringValue(String key) {
		return sharedPrefs.getString(key, null);
	}
	
	public void setPreferenceBooleanValue(String key, boolean value) {
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	public boolean getPreferenceBooleanValue(String key) {
		return sharedPrefs.getBoolean(key, false);
	}
}
