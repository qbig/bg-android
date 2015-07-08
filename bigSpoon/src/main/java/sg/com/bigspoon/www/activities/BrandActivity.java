package sg.com.bigspoon.www.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkView;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.common.Util.OnSwipeTouchListener;
import sg.com.bigspoon.www.data.User;

import static sg.com.bigspoon.www.data.Constants.AUTO;
import static sg.com.bigspoon.www.data.Constants.CLOSE_BRAND_STORY_SIGNAL;
import static sg.com.bigspoon.www.data.Constants.MIXPANEL_TOKEN;
import static sg.com.bigspoon.www.data.Constants.STORY_LINK;

/**
 * Created by qiaoliang89 on 28/5/15.
 */
public class BrandActivity extends Activity {

    private XWalkView mWebView;
    private ImageButton closeBtn;
    private ProgressBar spinner;
    private Handler handler;
    private Runnable killTask;
    OnSwipeTouchListener onSwipeTouchListener;
    private static String STORY_INTERACTION = "Story Interaction";
    private MixpanelAPI mMixpanel;
    private boolean touched = false;

    class MyResourceClient extends XWalkResourceClient {
        MyResourceClient(XWalkView view) {
            super(view);
        }

        @Override
        public void onLoadFinished(XWalkView view, String url) {
            super.onLoadFinished(view, url);
            BrandActivity.this.handler.post(new Runnable() {
                @Override
                public void run() {
                    spinner.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        final String link = intent.getStringExtra(STORY_LINK);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.brand_activity_layout);
        mMixpanel = MixpanelAPI.getInstance(this, MIXPANEL_TOKEN);
        handler = new Handler();
        closeBtn = (ImageButton) findViewById(R.id.brand_close_btn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);
        reloadIfNecessary(5000);

        mWebView = (XWalkView) findViewById(R.id.brand_webview);
        if (link == null) {
            finish();
        }
        if (intent.getBooleanExtra(AUTO, false)) {
            killTask = new Runnable() {
                @Override
                public void run() {
                    LocalBroadcastManager.getInstance(BrandActivity.this).sendBroadcast(new Intent(CLOSE_BRAND_STORY_SIGNAL));
                    finish();
                }
            };
            handler.postDelayed(killTask, 60 * 1000);
        }

        mWebView.load(link, null);
        mWebView.setResourceClient(new MyResourceClient(mWebView));
        onSwipeTouchListener = new OnSwipeTouchListener(BrandActivity.this) {
            public void onSwipeTop() {trackTouchEvent(); }
            public void onSwipeRight() {
                trackTouchEvent();
            }
            public void onSwipeLeft() {
                trackTouchEvent();
            }
            public void onSwipeBottom() {
                trackTouchEvent();
            }
        };
        mWebView.setOnTouchListener(onSwipeTouchListener);
    }

    private void trackTouchEvent() {
        if (!this.touched){
            User.getInstance(this).mMixpanel.track("User Touches Story", null);
            mMixpanel.getPeople().increment(STORY_INTERACTION, 1);
            this.touched = true;
        }
    }

    private void reloadIfNecessary (final int delay){

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (spinner.getVisibility() != View.GONE) {

                    reloadIfNecessary (delay + 2000);
                    mWebView.reload(XWalkView.RELOAD_NORMAL);
                }
            }
        }, delay);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        onSwipeTouchListener.gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        if (killTask != null) {
            handler.removeCallbacks(killTask);
        }
        return super.onKeyDown(keyCode, event);
    }
}
