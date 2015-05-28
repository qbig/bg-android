package sg.com.bigspoon.www.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.os.Handler;

import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkView;

import sg.com.bigspoon.www.R;
/**
 * Created by qiaoliang89 on 28/5/15.
 */
public class BrandActivity extends Activity {

    private XWalkView mWebView;
    private ImageButton closeBtn;
    private ProgressBar spinner;
    private Handler handler;
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
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.brand_activity_layout);
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
        mWebView = (XWalkView) findViewById(R.id.brand_webview);
        mWebView.load("http://r.xiumi.us/stage/v3/29SuP/1031854?from=home_square", null);
        mWebView.clearCache(true);
        mWebView.setResourceClient(new MyResourceClient(mWebView));
        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
    }

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}
