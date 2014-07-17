package sg.com.bigspoon.www.data;

import android.content.Context;

public class User {
    private static User sInstance;
    private Context mContext;
    public OutletDetailsModel currentOutlet;
    public DiningSession currentSession;
    
    private User(Context context) {
        setContext(context.getApplicationContext());
        currentSession = new DiningSession();
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
}