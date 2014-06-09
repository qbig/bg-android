package sg.com.bigspoon.www;

import sg.com.bigspoon.www.WaterQuantityPicker;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class WaterServiceActivity extends Activity {
	ActionBar actionBar;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_water_service);

    }
}