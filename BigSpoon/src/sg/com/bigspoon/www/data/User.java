package sg.com.bigspoon.www.data;

import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_AUTHTOKEN;
import static sg.com.bigspoon.www.data.Constants.NOTIF_ORDER_UPDATE;
import static sg.com.bigspoon.www.data.Constants.ORDER_URL;
import static sg.com.bigspoon.www.data.Constants.PREFS_NAME;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class User {
    private static User sInstance;
    private Context mContext;
    public OutletDetailsModel currentOutlet;
    public DiningSession currentSession;
    public List<RetrievedOrder> diningHistory;
    private SharedPreferences loginPrefs;
    public Location currentLocation;
    public TextView cornerViewHolder;
    
    private User(Context context) {
        setContext(context.getApplicationContext());
        currentSession = new DiningSession();
        loginPrefs = context.getSharedPreferences(PREFS_NAME, 0);
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
		for (int i = 0, len = updatedOrder.orders.length; i < len; i++){
			OrderItem item = updatedOrder.orders[i];
			if(existingOrder.getQuantityOfDishByID(item.dish.id) != item.quantity){
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
				.setHeader("Authorization", "Token " + authToken)
				.as(new TypeToken<RetrievedOrder>() {
		        })
				.setCallback(new FutureCallback<RetrievedOrder>() {
		            @Override
		            public void onCompleted(Exception e, RetrievedOrder result) {
		                if (e != null) {
		                    Toast.makeText(mContext, "Error login with FB", Toast.LENGTH_LONG).show();
		                    return;
		                }
		                
		                if (result == null) {
		                	User.getInstance(mContext).currentSession.closeCurrentSession();
		                } else {
		                	User.getInstance(mContext).checkThrough(result);
		                }
		            }
		        });
	}
}