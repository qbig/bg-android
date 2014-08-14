package sg.com.bigspoon.www.data;

import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_AUTHTOKEN;
import static sg.com.bigspoon.www.data.Constants.NOTIF_ORDER_UPDATE;
import static sg.com.bigspoon.www.data.Constants.ORDER_URL;
import static sg.com.bigspoon.www.data.Constants.PREFS_NAME;
import static sg.com.bigspoon.www.data.Constants.REQUEST_URL;

import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import sg.com.bigspoon.www.activities.UserReviewActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

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
	public int tableId = -1;
	public Boolean isContainDessert = false;
	public Boolean isForTakeAway = false;
	public MixpanelAPI mMixpanel;

	private User(Context context) {
		setContext(context.getApplicationContext());
		currentSession = new DiningSession();
		loginPrefs = context.getSharedPreferences(PREFS_NAME, 0);
	}

	public Boolean checkLocation() {

		if (currentLocation == null) {
			return false;
		}
		Boolean checkLocationPass = false;
		double accuracy = 0;
		final Location locationOfCurrentOutlet = new Location("Current Outlet");
		locationOfCurrentOutlet.setLatitude(currentOutlet.lat);
		locationOfCurrentOutlet.setLongitude(currentOutlet.lng);
		double distance = currentLocation.distanceTo(locationOfCurrentOutlet);
		if (currentLocation.hasAccuracy()) {
			accuracy = currentLocation.getAccuracy();
		}
		if (distance <= accuracy + currentOutlet.locationThreshold) {
			checkLocationPass = true;
		} else {
			checkLocationPass = false;
		}

		return checkLocationPass;
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
		final Order existingOrder = currentSession.pastOrder;
		boolean changed = false;
		for (int i = 0, len = updatedOrder.orders.length; i < len; i++) {
			OrderItem item = updatedOrder.orders[i];
			if (existingOrder.getQuantityOfDishByID(item.dish.id) != item.quantity) {
				changed = true;
				break;
			}
		}

		if (changed) {
			currentSession.pastOrder = updatedOrder.toOrder();
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
							User.getInstance(mContext).currentSession.closeCurrentSession();
						} else {
							User.getInstance(mContext).checkThrough(result);
						}
					}
				});
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

	public void requestForWater(String waterInfo) {
		requestWithType(FOR_WATER, waterInfo);
	}

	public void requestForWaiter(String waiterServiceInfo) {
		requestWithType(FOR_WAITER, waiterServiceInfo);
	}

}