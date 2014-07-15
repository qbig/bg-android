package sg.com.bigspoon.www.data;

import android.content.Context;

public class User {
    private static User sInstance;
    private final Context mContext;
    public OutletDetailsModel currentOutlet;
    
    private User(Context context) {
        mContext = context.getApplicationContext();
    }

    static public User getInstance(Context context) {
        synchronized (User.class) {
            if (sInstance == null) {
                sInstance = new User(context);
            }
            return sInstance;
        }
    }
}