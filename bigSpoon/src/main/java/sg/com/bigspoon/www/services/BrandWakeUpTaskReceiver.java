package sg.com.bigspoon.www.services;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import sg.com.bigspoon.www.activities.BrandStoryListsActivity;
import sg.com.bigspoon.www.data.User;

import static sg.com.bigspoon.www.data.Constants.BRAND_WAKE_UP_SIGNAL;

/**
 * Created by qiaoliang89 on 5/6/15.
 */
public class BrandWakeUpTaskReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        User user = User.getInstance(arg0);
        if (user.shouldShowStory()){
            User.getInstance(arg0).shouldGoToOutlet = false;
            final Intent i = new Intent(arg0, BrandStoryListsActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra(BRAND_WAKE_UP_SIGNAL, true);
            arg0.startActivity(i);
        } else {
            user.cancelAlarm();
        }
    }
}