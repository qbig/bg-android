package sg.com.bigspoon.www.data;

import static sg.com.bigspoon.www.data.Constants.NOTIF_LOCATION_KEY;
import static sg.com.bigspoon.www.data.Constants.NOTIF_LOCATION_UPDATED;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class AndroidLocationService {
	/*
	 * Wrapper for Android Location service without using Google Play Service
	 */
	
	private static final long ONE_MIN = 1000 * 60;
	private static final float MIN_LAST_READ_ACCURACY = 500.0f;
	private static final long MEASURE_TIME = 1000 * 30;
	private static final long POLLING_FREQ = 1000 * 10;
	private static final float MIN_ACCURACY = 25.0f;
	private static final long TWO_MIN = ONE_MIN * 2;
	private static final long FIVE_MIN = ONE_MIN * 5;
	
	private static final float MIN_DISTANCE = 10.0f;
	
	private Location mBestReading;
	private LocationManager mLocationManager;
	private LocationListener mLocationListener;

	private final String TAG = "AndroidLocationService";
	
	private Context mContext;
	
	public AndroidLocationService(Context context) {
		this.mContext = context;
		
		mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		if (null == mLocationManager) {
			return;
		}
		mLocationListener = getLocationListenerForBestReading();
		mBestReading = bestLastKnownLocation(MIN_LAST_READ_ACCURACY, FIVE_MIN);
	}
	
	private LocationListener getLocationListenerForBestReading() {
		return new LocationListener() {
			// helper
			private boolean isMoreAccurateLocationAvailable( Location location){
				return null == mBestReading
						|| location.getAccuracy() < mBestReading.getAccuracy();
			}

			public void onLocationChanged(Location location) {
				if (isMoreAccurateLocationAvailable(location)) {
					mBestReading = location;
					broadcastUpdatedLocation(location);
					if (mBestReading.getAccuracy() < MIN_ACCURACY) {
						mLocationManager.removeUpdates(mLocationListener);
					}
				}
			}

			public void onStatusChanged(String provider, int status,Bundle extras) {}
			public void onProviderEnabled(String provider) {}
			public void onProviderDisabled(String provider) {}
		};
	}
	
	private void broadcastUpdatedLocation(Location location) {
		Log.d("sender", "Broadcasting message");
		Intent intent = new Intent(NOTIF_LOCATION_UPDATED);
		// You can also include some extra data.

		intent.putExtra(NOTIF_LOCATION_KEY, location);
		LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
	}
	
	private Location bestLastKnownLocation(float minAccuracy, long maxAge) {

		Location bestResult = null;
		float bestAccuracy = Float.MAX_VALUE;
		long bestAge = Long.MIN_VALUE;

		List<String> matchingProviders = mLocationManager.getAllProviders();

		for (String provider : matchingProviders) {
			Location location = mLocationManager.getLastKnownLocation(provider);
			if (location != null) {
				float accuracy = location.getAccuracy();
				long time = location.getTime();

				if (accuracy < bestAccuracy) {
					bestResult = location;
					bestAccuracy = accuracy;
					bestAge = time;
				}
			}
		}

		// Return best reading or null
		if (bestAccuracy > minAccuracy || (System.currentTimeMillis() - bestAge) > maxAge) {
			return null;
		} else {
			return bestResult;
		}
	}
	
	public void startUpdate() {
		if (null == mLocationManager) {
			return;
		}
		
		if (mBestReading == null || mBestReading.getAccuracy() > MIN_LAST_READ_ACCURACY
				|| mBestReading.getTime() < System.currentTimeMillis()
						- TWO_MIN) {

			// Register for network location updates
			mLocationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, POLLING_FREQ, MIN_DISTANCE,
					mLocationListener);

			// Register for GPS location updates
			mLocationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, POLLING_FREQ, MIN_DISTANCE,
					mLocationListener);

			// Schedule a runnable to unregister location listeners
			Executors.newScheduledThreadPool(1).schedule(new Runnable() {
				@Override
				public void run() {
					Log.i(TAG, "location updates cancelled");
					mLocationManager.removeUpdates(mLocationListener);
				}
			}, MEASURE_TIME, TimeUnit.MILLISECONDS);
		} else {
			broadcastUpdatedLocation(mBestReading);
		}
	}
}
