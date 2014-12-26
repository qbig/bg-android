package sg.com.bigspoon.www.activities;

import sg.com.bigspoon.www.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class CallWaterServiceActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_water_service);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.call_water, menu);
		return true;
	}
}
