package sg.com.bigspoon.www.activities;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.R.layout;
import sg.com.bigspoon.www.adapters.WaterQuantityPickerAdapter;
import sg.com.bigspoon.www.data.User;
import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class WaterServiceActivity extends Activity {
	ActionBar actionBar;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_water_service);
		Button btn1 = (Button) findViewById(R.id.ok);
        Button btn2 = (Button) findViewById(R.id.cancle);
        
        btn2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	WaterServiceActivity.this.finish();
            }
        });
        
        btn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	WaterQuantityPickerAdapter warmWaterPicker = (WaterQuantityPickerAdapter) findViewById(R.id.Picker2);
            	WaterQuantityPickerAdapter coldWaterPicker = (WaterQuantityPickerAdapter) findViewById(R.id.Picker1);
            	User.getInstance(WaterServiceActivity.this.getApplicationContext()).requestForWater(String.format("Cold Water: %d cups. Warm Water: %d cups", coldWaterPicker.value, warmWaterPicker.value));
            	WaterServiceActivity.this.finish();
            }
        });

    }
}