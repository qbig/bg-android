package sg.com.bigspoon.www.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import sg.com.bigspoon.www.R;

/**
 * Created by qiaoliang89 on 11/3/15.
 */
public class ImageDialogAfterSent extends Activity {

    private ImageView mDialog;
    private android.os.Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_dialog_layout);
        handler = new Handler();
        mDialog = (ImageView)findViewById(R.id.popup_image);
        mDialog.setClickable(true);
        mDialog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageDialogAfterSent.this.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);
            }
        });

    }
}