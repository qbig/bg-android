package sg.com.bigspoon.www.data;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.socketio.Acknowledge;
import com.koushikdutta.async.http.socketio.ConnectCallback;
import com.koushikdutta.async.http.socketio.DisconnectCallback;
import com.koushikdutta.async.http.socketio.ErrorCallback;
import com.koushikdutta.async.http.socketio.ExceptionCallback;
import com.koushikdutta.async.http.socketio.JSONCallback;
import com.koushikdutta.async.http.socketio.ReconnectCallback;
import com.koushikdutta.async.http.socketio.SocketIOClient;

import org.json.JSONException;
import org.json.JSONObject;

import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_AUTHTOKEN;
import static sg.com.bigspoon.www.data.Constants.NOTIF_ORDER_UPDATE;
import static sg.com.bigspoon.www.data.Constants.PORT;
import static sg.com.bigspoon.www.data.Constants.PREFS_NAME;
import static sg.com.bigspoon.www.data.Constants.SOCKET_IO_TOKEN_BILL_CLOSED;
import static sg.com.bigspoon.www.data.Constants.SOCKET_URL;
import static sg.com.bigspoon.www.data.Constants.getURL;

public class SocketIOManager {
	private static SocketIOManager sInstance;
	private SharedPreferences loginPrefs;
	private ConnectCallback socketIOCallback;
	private BigSpoon mContext;
	private int failCount;
	private SocketIOClient mClient;
	private SocketIOManager(BigSpoon context) {
		mContext = context;
		loginPrefs = context.getSharedPreferences(PREFS_NAME, 0);
		failCount = 0;
	}

	static public SocketIOManager getInstance(BigSpoon context) {
		synchronized (SocketIOManager.class) {
			if (sInstance == null) {
				sInstance = new SocketIOManager(context);
			}
			sInstance.mContext = context;
			return sInstance;
		}
	}

	public void setupSocketIOConnection() {
		if (socketIOCallback == null) {
			socketIOCallback = getSocketIOCallback();
		}

		connectToSocketIO(socketIOCallback);
	}

	private ConnectCallback getSocketIOCallback() {
		return new ConnectCallback() {

			@SuppressWarnings("deprecation")
			@Override
			public void onConnectCompleted(Exception ex, final SocketIOClient client) {
				if (ex != null) {
					ex.printStackTrace();
					return;
				}
				
				subscribe(client);

				client.setJSONCallback(new JSONCallback() {

					@Override
					public void onJSON(final JSONObject json, final Acknowledge acknowledge) {
						System.out.println("socketIO: onJSON" + json.toString());
						try {
							handleJson(json);
						} catch (final JSONException e) {}
					}
				});

				client.setDisconnectCallback(new DisconnectCallback() {

					@Override
					public void onDisconnect(Exception arg0) {
						failCount = 0;
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
						if (failCount <= 10) {
							connectToSocketIO(socketIOCallback);
							failCount++;
						}
					}
				});

				client.setExceptionCallback(new ExceptionCallback() {

					@Override
					public void onException(Exception arg0) {
						if (failCount <= 10) {
							connectToSocketIO(socketIOCallback);
							failCount++;
						}
					}
				});
			}

			private void subscribe(SocketIOClient client) {
				mClient = client;
				failCount = 0;
				final String authToken = loginPrefs.getString(LOGIN_INFO_AUTHTOKEN, null);
				if (authToken == null) {
					return;
				}
				client.emit(String.format("subscribe:u_%s", authToken));
			}
			
			private void handleJson(final JSONObject json) throws JSONException {
				final JSONObject content = (JSONObject) json.get("message");
				final String type = content.getString("type");
				if (type.equals("message")) {
					final String message = content.getString("data");
					final int indexForDishNameToken = message.indexOf("[");

					if (indexForDishNameToken != -1) {
						User.getInstance(mContext).updateOrder();
						final Intent intent = new Intent(NOTIF_ORDER_UPDATE);
						LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
					} else if (message.indexOf(SOCKET_IO_TOKEN_BILL_CLOSED) != -1) {
						User.getInstance(mContext).currentSession.closeCurrentSession();
					}
					
					new Thread() {
					    public void run() {
					    	mContext.mHandler.post(new Runnable() {
					    	    public void run() {
					    	    	Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
					    	    }});
					    }
					}.start();
					System.out.println(message);
				}
			}
		};
	}

	private void connectToSocketIO(final ConnectCallback callback) {
		try {
			SocketIOClient.connect(AsyncHttpClient.getDefaultInstance(), "http://" + getURL(SOCKET_URL) + ":" + PORT, callback);
		} catch (NullPointerException npe) {
			if (npe != null && npe.toString() != null)
			Crashlytics.log(npe.toString());
		}
		
	}
	
	public void disconnect() {
		if (mClient != null){
			mClient.disconnect();
			failCount = 0;
		}
	}
}
