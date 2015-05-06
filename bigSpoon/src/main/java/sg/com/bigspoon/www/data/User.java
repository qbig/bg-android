package sg.com.bigspoon.www.data;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static sg.com.bigspoon.www.data.Constants.CLEAR_BILL_URL;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_AUTHTOKEN;
import static sg.com.bigspoon.www.data.Constants.NOTIF_ORDER_UPDATE;
import static sg.com.bigspoon.www.data.Constants.ORDER_URL;
import static sg.com.bigspoon.www.data.Constants.PREFS_NAME;
import static sg.com.bigspoon.www.data.Constants.REQUEST_URL;

public class User {
	private static User sInstance;
	private Context mContext;
	public OutletDetailsModel currentOutlet;
	public DiningSession currentSession;
	public List<RetrievedOrder> diningHistory;
	private SharedPreferences loginPrefs;
	public Location currentLocation;
	public boolean isfindTableCode = false;
	private static int FOR_WATER = 0;
	private static int FOR_WAITER = 1;
	private static int SEND_RETRY_NUM = 3;
	private int currentRetryCount = 0;
	public int tableId = -1;
	public boolean isContainDessert = false;
	public boolean isForTakeAway = false;
	public MixpanelAPI mMixpanel;
    public boolean shouldShowRemidnerPopup = false;
	public boolean shouldGoToOutlet = true;
	private ScheduledFuture scheduledFutureClearPastOrders;


	private User(Context context) {
		setContext(context.getApplicationContext());
		loginPrefs = context.getSharedPreferences(PREFS_NAME, 0);
	}
	
	public void startSession(String currentOutletName) {
		this.currentSession = new DiningSession(currentOutletName);
	}
	
	public Boolean checkLocation() {
        return true;
//		final String email = loginPrefs.getString(LOGIN_INFO_EMAIL, null);
//		if ( email!= null && email.equals("jay.tjk@gmail.com")){
//			return true;
//		}
//
//		if (currentLocation == null) {
//			Intent intent = new Intent(NOTIF_TO_START_LOCATION_SERVICE);
//			LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
//			return false;
//		}
//		Boolean checkLocationPass = false;
//		double accuracy = 0;
//		final Location locationOfCurrentOutlet = new Location("Current Outlet");
//		locationOfCurrentOutlet.setLatitude(currentOutlet.lat);
//		locationOfCurrentOutlet.setLongitude(currentOutlet.lng);
//		double distance = currentLocation.distanceTo(locationOfCurrentOutlet);
//		if (currentLocation.hasAccuracy()) {
//			accuracy = currentLocation.getAccuracy();
//		}
//		if (distance <= accuracy + currentOutlet.locationThreshold) {
//			checkLocationPass = true;
//		} else {
//			checkLocationPass = false;
//		}
//
//		return checkLocationPass;
	}

	static public User getInstance(Context context) {
		synchronized (User.class) {
			if (sInstance == null) {
				sInstance = new User(context);
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

	public void checkThrough(RetrievedOrder updatedOrder) {
		if (currentSession == null) {
			this.startSession(updatedOrder.outlet.name);
		}
		
		final Order existingOrder = currentSession.getPastOrder();
		boolean changed = false;
		for (int i = 0, len = updatedOrder.orders.length; i < len; i++) {
			OrderItem item = updatedOrder.orders[i];
			if (existingOrder.getQuantityOfDishByID(item.dish.id) != item.quantity) {
				changed = true;
				break;
			}
		}

		if (changed) {
			currentSession.setPastOrder(updatedOrder.toOrder());
			Intent intent = new Intent(NOTIF_ORDER_UPDATE);
			LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
		}
	}

	public void updateOrder() {
		final String authToken = loginPrefs.getString(LOGIN_INFO_AUTHTOKEN, null);
		if (authToken == null) {
			return;
		}
		Ion.with(mContext).load(ORDER_URL).setHeader("Content-Type", "application/json; charset=utf-8")
				.setHeader("Authorization", "Token " + authToken).as(new TypeToken<RetrievedOrder>() {
				}).setCallback(new FutureCallback<RetrievedOrder>() {
					@Override
					public void onCompleted(Exception e, RetrievedOrder result) {
						if (e != null) {
							if (Constants.LOG) {
								Toast.makeText(mContext, "Error updating orders", Toast.LENGTH_LONG).show();
							} else {
								final JSONObject info = new JSONObject();
								try {
									info.put("error", e.toString());
								} catch (JSONException e1) {
									e1.printStackTrace();
								}
								User.getInstance(mContext).mMixpanel.track("Error updating orders", info);
							}

							return;
						}

						if (result == null || result.orders == null) {
							if (User.getInstance(mContext).currentSession != null){
								User.getInstance(mContext).currentSession.closeCurrentSession();
							}
						} else {
							User.getInstance(mContext).checkThrough(result);
						}
					}
				});
	}

	public boolean wifiIsConnected() {
		final ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return mWifi != null && mWifi.isConnected();
	}

	public JsonObject getTableId() {
		final Gson gson = new Gson();
		HashMap<String, Integer> pair = new HashMap<String, Integer>();
		pair.put("table", tableId);
		return gson.toJsonTree(pair).getAsJsonObject();
	}

	private void requestWithType(int typeCode, String note) {
		if (tableId == -1) {
			return;
		}
		final JsonObject json = new JsonObject();
		json.addProperty("table", tableId);
		json.addProperty("request_type", typeCode);
		json.addProperty("note", note);

		Ion.with(mContext).load(REQUEST_URL).setHeader("Content-Type", "application/json; charset=utf-8")
				.setHeader("Authorization", "Token " + loginPrefs.getString(LOGIN_INFO_AUTHTOKEN, ""))
				.setJsonObjectBody(json).asJsonObject().setCallback(new FutureCallback<JsonObject>() {

			@Override
			public void onCompleted(Exception e, JsonObject result) {
				if (e != null) {
					if (Constants.LOG) {
						Toast.makeText(mContext, "Error sending request", Toast.LENGTH_LONG).show();
					} else {
						final JSONObject info = new JSONObject();
						try {
							info.put("error", e.toString());
						} catch (JSONException e1) {
							e1.printStackTrace();
						}
						User.getInstance(mContext).mMixpanel.track("Error sending request", info);
					}

					return;
				}
				Toast.makeText(mContext, "Success", Toast.LENGTH_LONG).show();
			}
		});
	}

	public void clearPastOrder(){
		JsonObject tableInfo = new JsonObject();
		tableInfo.addProperty("table", Integer.valueOf(User.getInstance(mContext).tableId));
		User.getInstance(mContext).currentSession.clearPastOrder();
		Ion.with(mContext).load(CLEAR_BILL_URL).setHeader("Content-Type", "application/json; charset=utf-8")
				.setHeader("Authorization", "Token " + mContext.getSharedPreferences(PREFS_NAME, 0).getString(LOGIN_INFO_AUTHTOKEN, ""))
				.setJsonObjectBody(tableInfo)
				.asJsonObject().setCallback(new FutureCallback<JsonObject>() {

			@Override
			public void onCompleted(Exception e, JsonObject result) {
				if (e != null) {
					final String errorMsg = e.toString();
					if (User.this.currentRetryCount < SEND_RETRY_NUM) {
						User.this.currentRetryCount++;
						User.this.clearPastOrder();
						return;
					} else {
						User.this.currentRetryCount = 0;
					}

					if (Constants.LOG) {
						Toast.makeText(mContext, "Error clearing orders", Toast.LENGTH_LONG).show();
					} else {
						final JSONObject info = new JSONObject();
						try {
							info.put("error", errorMsg);
							Crashlytics.logException(e);
						} catch (JSONException e1) {
							Crashlytics.logException(e1);
						}
						User.getInstance(mContext).mMixpanel.track("Error clearing orders",
								info);
					}

					return;
				}
				User.this.currentRetryCount = 0;
				Toast.makeText(mContext, "Cleared", Toast.LENGTH_LONG).show();
			}
		});
	}

	public void scheduleClearPastOrders(int delay) {

		final ScheduledExecutorService scheduler =
				Executors.newSingleThreadScheduledExecutor();

		if (scheduledFutureClearPastOrders != null) {
			scheduledFutureClearPastOrders.cancel(true);
		}

		scheduledFutureClearPastOrders= scheduler.schedule(
				new Runnable() {
					public void run() {
						if (currentSession != null) {
							User.this.clearPastOrder();
						}
					}
				}, delay, TimeUnit.SECONDS);
	}

	public void requestForWater(String waterInfo) {
		requestWithType(FOR_WATER, waterInfo);
	}

	public void requestForWaiter(String waiterServiceInfo) {
		requestWithType(FOR_WAITER, waiterServiceInfo);
	}

	public boolean isTableValidForCurrentOutlet() {
		return currentOutlet.hasTable(tableId);
	}

}