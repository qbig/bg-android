package sg.com.bigspoon.www.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.adapters.CardArrayAdapter;
import sg.com.bigspoon.www.data.BigSpoon;
import sg.com.bigspoon.www.data.StoryModel;
import sg.com.bigspoon.www.data.User;

import static sg.com.bigspoon.www.data.Constants.AUTO;
import static sg.com.bigspoon.www.data.Constants.BRAND_WAKE_UP_SIGNAL;
import static sg.com.bigspoon.www.data.Constants.CLOSE_BRAND_STORY_SIGNAL;
import static sg.com.bigspoon.www.data.Constants.STORY_LINK;

/**
 * Created by qiaoliang89 on 1/6/15.
 */
public class BrandStoryListsActivity extends Activity {
    private RecyclerView mListView;
    private StoryModel[] storys;
    private Integer[] storySequence;
    private android.os.Handler mHandler;

    private BroadcastReceiver mCloseSignalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BrandStoryListsActivity.this.mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    sleepScreen();
                    BrandStoryListsActivity.this.finish();
                }
            }, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_list);
        mHandler = new Handler();

        LinearLayoutManager llm = new LinearLayoutManager(this);
        mListView = (RecyclerView) findViewById(R.id.rv);
        mListView.setLayoutManager(llm);
        mListView.setHasFixedSize(true);


        try {
            storys = User.getInstance(this).currentOutlet.storys;
            storySequence = User.getInstance(this).currentOutlet.storySequence;
        } catch (NullPointerException npe) {
            Crashlytics.log(npe.getMessage());
        }

        initCards();
        setTitle(User.getInstance(this).currentOutlet.name + " Story");

        if (getIntent().getBooleanExtra(BRAND_WAKE_UP_SIGNAL, false)){
            if(! User.getInstance(this).shouldShowStory()) {
                finish();
                return;
            }
            unlockScreen();
            LocalBroadcastManager.getInstance(this).registerReceiver(mCloseSignalReceiver, new IntentFilter(CLOSE_BRAND_STORY_SIGNAL));
            Intent i = new Intent(BrandStoryListsActivity.this, BrandActivity.class);
            i.putExtra(STORY_LINK, getStory(storySequence[User.getInstance(this).storyDisplayCount]).url);
            User.getInstance(this).storyDisplayCount++;
            i.putExtra(AUTO, true);
            BrandStoryListsActivity.this.startActivity(i);
            sleepScreen();
        }
    }

    private void unlockScreen() {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        ((BigSpoon)getApplicationContext()).wakeDevice();
    }

    private void sleepScreen() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        ((BigSpoon)getApplicationContext()).releaseWakeLock();
    }

    private StoryModel getStory(int id) {
        for (int i=0; i< storys.length ;i++){
            if (storys[i].storyId == id) {
                return storys[i];
            }
        }
        return null;
    }

    private void initCards() {

        ArrayList<StoryModel> sortedStorys = new ArrayList<StoryModel>();
        for (int i=0; i< storySequence.length; i++){
            StoryModel story = getStory(storySequence[i]);
            if (story != null) {
                sortedStorys.add(story);
            }
        }

        CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(sortedStorys, this);

        if (mListView!=null){
            mListView.setAdapter(mCardArrayAdapter);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mCloseSignalReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
               finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
