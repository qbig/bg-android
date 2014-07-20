package sg.com.bigspoon.www.data;

import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_AUTHTOKEN;
import static sg.com.bigspoon.www.data.Constants.NOTIF_ORDER_UPDATE;
import static sg.com.bigspoon.www.data.Constants.PORT;
import static sg.com.bigspoon.www.data.Constants.PREFS_NAME;
import static sg.com.bigspoon.www.data.Constants.SOCKET_IO_TOKEN_BILL_CLOSED;
import static sg.com.bigspoon.www.data.Constants.SOCKET_URL;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import sg.com.bigspoon.www.activities.Foreground;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.socketio.Acknowledge;
import com.koushikdutta.async.http.socketio.ConnectCallback;
import com.koushikdutta.async.http.socketio.DisconnectCallback;
import com.koushikdutta.async.http.socketio.ErrorCallback;
import com.koushikdutta.async.http.socketio.ExceptionCallback;
import com.koushikdutta.async.http.socketio.JSONCallback;
import com.koushikdutta.async.http.socketio.ReconnectCallback;
import com.koushikdutta.async.http.socketio.SocketIOClient;
import com.koushikdutta.ion.Ion;

public class BigSpoon extends Application implements Foreground.Listener {
	private SharedPreferences loginPrefs;
	private ConnectCallback socketIOCallback;
	private static final String ION_LOGGING_APP = "ion-bigspoon-application";

	@Override
	public void onCreate() {
		super.onCreate();
		loginPrefs = getSharedPreferences(PREFS_NAME, 0);
		Ion.getDefault(this).configure().setLogging(ION_LOGGING_APP, Log.DEBUG);
		Foreground.get(this).addListener(this);
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		Foreground.get(this).removeListener(this);
	}

	private void setupSocketIOConnection() {
		socketIOCallback = new ConnectCallback() {

			@Override
			public void onConnectCompleted(Exception ex, final SocketIOClient client) {
				if (ex != null) {
					ex.printStackTrace();
					return;
				}
				subscribe(client);

				client.setJSONCallback(new JSONCallback() {

					@Override
					public void onJSON(JSONObject json, Acknowledge acknowledge) {
						System.out.println("socketIO: onJSON" + json.toString());
						try {
							final JSONObject content = (JSONObject) json.get("message");
							final String type = content.getString("tyep");
							if (type.equals("message")) {
								final String message = content.getString("data");
								final int indexForDishNameToken = message.indexOf("[");

								if (indexForDishNameToken != -1) {
									User.getInstance(getApplicationContext()).updateOrder();
									Intent intent = new Intent(NOTIF_ORDER_UPDATE);
									LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
								} else if (message.indexOf(SOCKET_IO_TOKEN_BILL_CLOSED) != -1) {
									User.getInstance(getApplicationContext()).currentSession.closeCurrentSession();
								}
								Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
							}
						} catch (JSONException e) {

						}
					}
				});

				client.setDisconnectCallback(new DisconnectCallback() {

					@Override
					public void onDisconnect(Exception arg0) {
						connectToSocketIO(socketIOCallback);
					}
				});

				client.setReconnectCallback(new ReconnectCallback() {

					@Override
					public void onReconnect() {
						subscribe(client);
					}
				});

				client.setErrorCallback(new ErrorCallback() {

					@Override
					public void onError(String arg0) {
						connectToSocketIO(socketIOCallback);
					}
				});

				client.setExceptionCallback(new ExceptionCallback() {

					@Override
					public void onException(Exception arg0) {
						connectToSocketIO(socketIOCallback);
					}
				});
			}

			private void subscribe(SocketIOClient client) {
				final String authToken = loginPrefs.getString(LOGIN_INFO_AUTHTOKEN, null);
				if (authToken == null) {
					return;
				}
				client.emit(String.format("subscribe:u_%s", authToken));
			}
		};

		connectToSocketIO(socketIOCallback);
	}

	private void connectToSocketIO(final ConnectCallback callback) {
		SocketIOClient.connect(AsyncHttpClient.getDefaultInstance(), "http://" + SOCKET_URL + ":" + PORT, callback);
	}

	// Foreground Callback
	@Override
	public void onBecameForeground() {
		Toast.makeText(getApplicationContext(), "Active!!!", Toast.LENGTH_LONG).show();
		User.getInstance(getApplicationContext()).updateOrder();
		setupSocketIOConnection();
	}

	// Foreground Callback
	@Override
	public void onBecameBackground() {
		Toast.makeText(getApplicationContext(), "IINNNNActive!!!", Toast.LENGTH_LONG).show();
	}
}
