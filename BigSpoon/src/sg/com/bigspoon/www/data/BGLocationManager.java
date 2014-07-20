package sg.com.bigspoon.www.data;

import static sg.com.bigspoon.www.data.Constants.PREFS_NAME;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class BGLocationManager {
	private static BGLocationManager sInstance;
	private Context mContext;
	private SharedPreferences loginPrefs;
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	
	private BGLocationManager(Context context) {
		setContext(context.getApplicationContext());
		loginPrefs = context.getSharedPreferences(PREFS_NAME, 0);
	}

	static public BGLocationManager getInstance(Context context) {
		synchronized (BGLocationManager.class) {
			if (sInstance == null) {
				sInstance = new BGLocationManager(context);
			}
			sInstance.setContext(context);
			return sInstance;
		}
	}

	public Context getContext() {
		return mContext;
	}

	public void setContext(Context mContext) {
		this.mContext = mContext;
	}
	
	// TODO Check availability of Google Play Service HERE!
}
