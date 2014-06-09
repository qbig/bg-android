package sg.com.bigspoon.www;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class ActionBarActivity extends FragmentActivity {
	ActionBar actionBar;
	private GridView gridView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_action_bar);
		actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
	}
		
		
		protected void loadMenu() {

		final Context ctx = getApplicationContext();
		Resources res = ctx.getResources();

		//String[] gv_options = {"Water","Waiter","Bill","Orders"};
		//int[] gv_icons = {R.drawable.fb_1,R.drawable.fb_2,R.drawable.fb_3,R.drawable.fb_4};

		gridView = (GridView) findViewById(R.id.gv_action_menu);
		// Create the Custom Adapter Object
		ActionBarMenuAdapter actionBarMenuAdapter = new ActionBarMenuAdapter(this,
				4);
		// Set the Adapter to GridView
		gridView.setAdapter(actionBarMenuAdapter);
	}


}
