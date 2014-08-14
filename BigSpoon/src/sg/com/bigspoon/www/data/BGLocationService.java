package sg.com.bigspoon.www.data;

import static sg.com.bigspoon.www.data.Constants.NOTIF_LOCATION_KEY;
import static sg.com.bigspoon.www.data.Constants.NOTIF_LOCATION_UPDATED;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import sg.com.bigspoon.www.activities.UserReviewActivity;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class BGLocationService extends Service implements GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

	private final static float TEN_METERS = 10.0f;
	private static final long POLLING_FREQ = 1000;
	private static final long RESTART_TIME = 120 * 1000;
	private static final String TAG = "BGLocationService";

	private boolean currentlyProcessingLocation = false;
	private LocationRequest locationRequest;
	private LocationClient locationClient;
	
	private AndroidLocationService mNoGooglePlayAlternativeLocationService;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		if (!currentlyProcessingLocation) {
			currentlyProcessingLocation = true;
			startTracking();
		}

		return START_STICKY;
	}

	private void startTracking() {
		Log.d(TAG, "startTracking");

		if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
			locationClient = new LocationClient(this, this, this);

			if (!locationClient.isConnected() || !locationClient.isConnecting()) {
				locationClient.connect();
			}
		} else {
			Log.e(TAG, "unable to connect to google play services.");
			if (Constants.LOG) {
				Toast.makeText(this, "Google Play Service not available.", Toast.LENGTH_LONG).show();
			} else {
				final JSONObject info = new JSONObject();
				try {
					info.put("error", "Google Play Service not available.");
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				User.getInstance(this).mMixpanel.track("Google Play Service not available.", info);
			}
			
			mNoGooglePlayAlternativeLocationService = new AndroidLocationService(this);
			mNoGooglePlayAlternativeLocationService.startUpdate();
		}
	}
	
	@Override
	public void onDestroy() {
		stopLocationUpdates();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {
			Log.e(TAG, "position: " + location.getLatitude() + ", " + location.getLongitude() + " accuracy: "
					+ location.getAccuracy());
			
			broadcastUpdatedLocation(location);
			if (location.getAccuracy() < TEN_METERS) {
				stopLocationUpdates();
				Executors.newScheduledThreadPool(1).schedule(new Runnable() {
					@Override
					public void run() {
						Log.i(TAG, "location updates cancelled");
						BGLocationService.this.startTracking();
					}
				}, RESTART_TIME, TimeUnit.MILLISECONDS);
			}
		}
	}

	private void broadcastUpdatedLocation(Location location) {
		Log.d("sender", "Broadcasting message");
		Intent intent = new Intent(NOTIF_LOCATION_UPDATED);
		// You can also include some extra data.

		intent.putExtra(NOTIF_LOCATION_KEY, location);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	private void stopLocationUpdates() {
		if (locationClient != null && locationClient.isConnected()) {
			locationClient.removeLocationUpdates(this);
			locationClient.disconnect();
		}
		
		if (mNoGooglePlayAlternativeLocationService != null){
			mNoGooglePlayAlternativeLocationService.stopUpdate();
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Log.e(TAG, "onConnectionFailed");
	}

	@Override
	public void onConnected(Bundle arg0) {
		Log.d(TAG, "onConnected");

		locationRequest = LocationRequest.create();
		locationRequest.setInterval(POLLING_FREQ); 
		locationRequest.setFastestInterval(POLLING_FREQ);
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		locationClient.requestLocationUpdates(locationRequest, this);
	}

	@Override
	public void onDisconnected() {
		Log.e(TAG, "onDisconnected");
	}
}
