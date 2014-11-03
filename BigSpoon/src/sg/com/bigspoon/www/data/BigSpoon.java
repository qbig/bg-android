package sg.com.bigspoon.www.data;

import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_EMAIL;
import static sg.com.bigspoon.www.data.Constants.MIXPANEL_TOKEN;
import static sg.com.bigspoon.www.data.Constants.NOTIF_LOCATION_KEY;
import static sg.com.bigspoon.www.data.Constants.NOTIF_LOCATION_UPDATED;
import static sg.com.bigspoon.www.data.Constants.NOTIF_TO_START_LOCATION_SERVICE;
import static sg.com.bigspoon.www.data.Constants.PREFS_NAME;
import static sg.com.bigspoon.www.data.Constants.TUTORIAL_SET;

import org.json.JSONException;
import org.json.JSONObject;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.activities.Foreground;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.mixpanel.android.mpmetrics.MPConfig;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

public class BigSpoon extends Application implements Foreground.Listener {
	final Handler mHandler = new Handler();
	private MixpanelAPI mMixpanel;
	private BroadcastReceiver mLocationUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final Location location = intent.getParcelableExtra(NOTIF_LOCATION_KEY);
			User.getInstance(getApplicationContext()).currentLocation = location;
		}
	};

	private BroadcastReceiver mLocationStartServiceReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			BigSpoon.this.startService(new Intent(BigSpoon.this, BGLocationService.class));
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		Foreground.get(this).addListener(this);
		LocalBroadcastManager.getInstance(this).registerReceiver(mLocationUpdateReceiver,
				new IntentFilter(NOTIF_LOCATION_UPDATED));
		LocalBroadcastManager.getInstance(this).registerReceiver(mLocationStartServiceReceiver,
				new IntentFilter(NOTIF_TO_START_LOCATION_SERVICE));
		BugSenseHandler.initAndStartSession(this, "625f7944");
		mMixpanel = MixpanelAPI.getInstance(this, MIXPANEL_TOKEN);
		mMixpanel.identify(mMixpanel.getDistinctId());
		mMixpanel.getPeople().identify(mMixpanel.getDistinctId());
		
		User.getInstance(this).mMixpanel = this.mMixpanel;
		final SharedPreferences pref = getSharedPreferences(PREFS_NAME, 0);
		if (pref.contains(LOGIN_INFO_EMAIL)) {
			final String email = pref.getString(LOGIN_INFO_EMAIL, null);
			if (email != null) {
				JSONObject props = new JSONObject();
				try {
					props.put("user", email);
					mMixpanel.track("Usage starts", props);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		}

	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		Foreground.get(this).removeListener(this);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocationUpdateReceiver);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocationStartServiceReceiver);
		BugSenseHandler.closeSession(this);
		mMixpanel.flush();
	}

	// Foreground Callback
	@Override
	public void onBecameForeground() {
		User.getInstance(getApplicationContext()).updateOrder();
		SocketIOManager.getInstance(this).setupSocketIOConnection();
		this.startService(new Intent(this, BGLocationService.class));
		checkLocationEnabledIfTutorialHasShown();
	}

	// Foreground Callback
	@Override
	public void onBecameBackground() {
		this.stopService(new Intent(this, BGLocationService.class));
		SocketIOManager.getInstance(this).disconnect();
	}

	public void checkLocationEnabledByForce() {
		checkLocationEnabled(true);
	}

	public void checkLocationEnabledIfTutorialHasShown() {
		checkLocationEnabled(false);
	}

	private void checkLocationEnabled(boolean force) {
		final SharedPreferences loginPreferences = getSharedPreferences(PREFS_NAME, 0);
		final SharedPreferences.Editor loginEditor = loginPreferences.edit();

		final boolean hasShownTutorial = loginPreferences.getBoolean(TUTORIAL_SET, false);
		if (hasShownTutorial || force) {
			final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

			Criteria locationCritera = new Criteria();
			locationCritera.setAccuracy(Criteria.ACCURACY_COARSE);
			locationCritera.setAltitudeRequired(false);
			locationCritera.setBearingRequired(false);
			locationCritera.setCostAllowed(true);
			locationCritera.setPowerRequirement(Criteria.NO_REQUIREMENT);

			final String providerName = locationManager.getBestProvider(locationCritera, true);

			if (providerName == null || providerName.equals(LocationManager.PASSIVE_PROVIDER)
					|| !locationManager.isProviderEnabled(providerName)) {
				Toast.makeText(this, R.string.please_turn_on_gps, Toast.LENGTH_LONG).show();
				Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(myIntent);
			}

			loginEditor.putBoolean(TUTORIAL_SET, true);
			loginEditor.commit();
		}
	}
}
