package sg.com.bigspoon.www.data;

import android.app.Application;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.multidex.MultiDex;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.crashlytics.android.Crashlytics;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.novoda.merlin.Merlin;
import com.novoda.merlin.NetworkStatus;
import com.novoda.merlin.registerable.bind.Bindable;
import com.novoda.merlin.registerable.connection.Connectable;
import com.novoda.merlin.registerable.disconnection.Disconnectable;

import org.json.JSONException;
import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;
import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.activities.Foreground;
import sg.com.bigspoon.www.activities.OutletListActivity;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import static sg.com.bigspoon.www.data.Constants.NETWORK_CONNECTED;
import static sg.com.bigspoon.www.data.Constants.NETWORK_DISCONNECTED;
import static sg.com.bigspoon.www.data.Constants.NOTIF_LOCATION_KEY;
import static sg.com.bigspoon.www.data.Constants.OUTLET_ID;
import static sg.com.bigspoon.www.data.Constants.PREFS_NAME;
import static sg.com.bigspoon.www.data.Constants.TABLE_ID;
import static sg.com.bigspoon.www.data.Constants.TUTORIAL_SET;
import static sg.com.bigspoon.www.data.Constants.NOTIF_LOCATION_UPDATED;
import static sg.com.bigspoon.www.data.Constants.NOTIF_TO_START_LOCATION_SERVICE;
import static sg.com.bigspoon.www.data.Constants.MIXPANEL_TOKEN;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_EMAIL;

public class BigSpoon extends Application implements Foreground.Listener, Connectable, Disconnectable, Bindable {
	final Handler mHandler = new Handler();
	public boolean onStart = true;
    private Merlin merlin;
	private MixpanelAPI mMixpanel;
	private PowerManager.WakeLock fullWakeLock;
	private PowerManager.WakeLock partialWakeLock;
	protected void createWakeLocks(){
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		fullWakeLock = powerManager.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "Loneworker - FULL WAKE LOCK");
		partialWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Loneworker - PARTIAL WAKE LOCK");
	}

	public void wakeDevice() {
		fullWakeLock.acquire();

		KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
		keyguardLock.disableKeyguard();
	}


    public void releaseWakeLock() {
        if(fullWakeLock.isHeld()){
            fullWakeLock.release();
        }
        if(partialWakeLock.isHeld()){
            partialWakeLock.release();
        }
    }

	private BroadcastReceiver mLocationUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final Location newLocation = intent.getParcelableExtra(NOTIF_LOCATION_KEY);
			Location currentLoc = User.getInstance(getApplicationContext()).currentLocation;
			if (currentLoc == null || (currentLoc.hasAccuracy() && newLocation.getAccuracy() < currentLoc.getAccuracy())){
				User.getInstance(getApplicationContext()).currentLocation = newLocation;
			}
		}
	};

	private BroadcastReceiver mLocationStartServiceReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			BigSpoon.this.startService(new Intent(BigSpoon.this, BGLocationService.class));
		}
	};

	private final BroadcastReceiver lftBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final LocationInfo locationInfo = (LocationInfo) intent
					.getSerializableExtra(LocationLibraryConstants.LOCATION_BROADCAST_EXTRA_LOCATIONINFO);
			if (locationInfo.anyLocationDataReceived()) {
				Location currentLoc = User.getInstance(getApplicationContext()).currentLocation;
				if (currentLoc == null || (currentLoc.hasAccuracy() &&
						locationInfo.lastAccuracy < currentLoc.getAccuracy() && locationInfo.lastLocationUpdateTimestamp > currentLoc.getTime())){
					currentLoc = new Location("toUpdate");
					currentLoc.setLatitude(locationInfo.lastLat);
					currentLoc.setLongitude(locationInfo.lastLong);
					User.getInstance(getApplicationContext()).currentLocation = currentLoc;
				}
			}
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
        Fabric.with(this, new Crashlytics());
		try {
			LocationLibrary.initialiseLibrary(getBaseContext(), "sg.com.bigspoon.www");
		} catch (UnsupportedOperationException ex) {
			Crashlytics.logException(ex);
		}

		Foreground.get(this).addListener(this);
		LocalBroadcastManager.getInstance(this).registerReceiver(mLocationUpdateReceiver,
				new IntentFilter(NOTIF_LOCATION_UPDATED));
		LocalBroadcastManager.getInstance(this).registerReceiver(mLocationStartServiceReceiver,
				new IntentFilter(NOTIF_TO_START_LOCATION_SERVICE));
		LocalBroadcastManager.getInstance(this).registerReceiver(lftBroadcastReceiver, new IntentFilter(LocationLibraryConstants.getLocationChangedPeriodicBroadcastAction()));
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

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
						.setDefaultFontPath("fonts/ProximaNovaSemibold.otf")
						.setFontAttrId(R.attr.fontPath)
						.build()
		);

		createWakeLocks();
        merlin = new Merlin.Builder()
                .withConnectableCallbacks()
                .withDisconnectableCallbacks()
                .withBindableCallbacks()
                .withLogging(true)
                .build(this);
        merlin.registerBindable(this);
        merlin.registerConnectable(this);
        merlin.registerDisconnectable(this);
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
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
		//User.getInstance(getApplicationContext()).updateOrder();
		final User user = User.getInstance(this);
        user.tableId = getSharedPreferences(PREFS_NAME, 0).getInt(TABLE_ID, -1);
		SocketIOManager.getInstance(this).setupSocketIOConnection();
		this.startService(new Intent(this, BGLocationService.class));
		checkLocationEnabledIfTutorialHasShown();
		LocationLibrary.startAlarmAndListener(this);
		final int outletId = getSharedPreferences(PREFS_NAME, 0).getInt(OUTLET_ID, -1);
		final int delay = onStart ? 5000 : 0;
		if (outletId != -1){
			// fix on app start, popup gets dismissed.
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if (user.shouldGoToOutlet) {
						Intent intent = new Intent(BigSpoon.this, OutletListActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
						BigSpoon.this.startActivity(intent);
					}
				}
			}, delay);
		}
		onStart = false; // only delay on app start.
        releaseWakeLock();
        merlin.bind();
	}


    // Foreground Callback
	@Override
	public void onBecameBackground() {
		this.stopService(new Intent(this, BGLocationService.class));
		SocketIOManager.getInstance(this).disconnect();
		LocationLibrary.stopAlarmAndListener(this);
		User.getInstance(this).shouldGoToOutlet = true;
		User.getInstance(this).prevActiveTime= System.currentTimeMillis();

		partialWakeLock.acquire();
        merlin.unbind();
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
			if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				Toast.makeText(this, R.string.please_turn_on_gps, Toast.LENGTH_LONG).show();
				Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(myIntent);
			}

			loginEditor.putBoolean(TUTORIAL_SET, true);
			loginEditor.commit();
		}
	}


	@Override
	public void onBind(NetworkStatus networkStatus) {
        if (!networkStatus.isAvailable()) {
            onDisconnect();
        }
	}

	@Override
	public void onConnect() {
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(NETWORK_CONNECTED));
        //Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        //Constants.isLocal = false;
	}

	@Override
	public void onDisconnect(){
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(NETWORK_DISCONNECTED));
        //Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show();
        //Constants.isLocal = true;
	}

}
