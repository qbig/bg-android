package sg.com.bigspoon.www;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class OrderHistoryDetailsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_history_details);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.order_history_details, menu);
		return true;
	}

}
