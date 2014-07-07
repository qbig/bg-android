package sg.com.bigspoon.www.activities;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.R.id;
import sg.com.bigspoon.www.R.layout;
import sg.com.bigspoon.www.adapters.ActionBarMenuAdapter;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

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
		final EditText input = new EditText(this);
		// String[] gv_options = {"Water","Waiter","Bill","Orders"};
		// int[] gv_icons =
		// {R.drawable.fb_1,R.drawable.fb_2,R.drawable.fb_3,R.drawable.fb_4};
		final AlertDialog alert = new AlertDialog.Builder(this).create();
		final AlertDialog alert2 = new AlertDialog.Builder(this).create();
		gridView = (GridView) findViewById(R.id.gv_action_menu);
		// Create the Custom Adapter Object
		ActionBarMenuAdapter actionBarMenuAdapter = new ActionBarMenuAdapter(
				this, 4);
		// Set the Adapter to GridView
		gridView.setAdapter(actionBarMenuAdapter);

		// Handling touch/click Event on GridView Item
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long arg3) {
				Intent i = null;
				switch (position) {
				case 0:
					i = new Intent(ctx, WaterServiceActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
					break;
				case 1:
					alert.setMessage("Please enter your table ID located on the BigSpoon table stand");
					// i = new Intent(ctx, StoreListActivity.class);
					// Set an EditText view to get user input

					alert.setView(input,10,0,10,0);

				    alert.setButton2("Cancel",new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,int whichButton) {
                                  //
								}
							});
					alert.setButton("Okay",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int whichButton) {
                           //
						}
					});
				    alert.show(); 
				    TextView messageView = (TextView)alert.findViewById(android.R.id.message);
	                messageView.setGravity(Gravity.CENTER);
	                //messageView.setHeight(140);
	                messageView.setTextSize(17);
				    
	                Button bq1 = alert.getButton(DialogInterface.BUTTON1);
	                bq1.setTextColor(Color.parseColor("#117AFE"));
	                bq1.setTypeface(null,Typeface.BOLD);
	                bq1.setTextSize(19);
	                Button bq2 = alert.getButton(DialogInterface.BUTTON2);
	                bq2.setTextColor(Color.parseColor("#117AFE"));
	                //bq2.setTypeface(null,Typeface.BOLD);
	                bq2.setTextSize(19);
					break;
				case 2:
					// i = new Intent(ctx, CartActivity.class);
					alert2.setMessage("Would you like your bill?");
					alert2.setView(null);
					// Set an EditText view to get user input

		            alert2.setButton2("Cancel",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int whichButton) {
                          //
						}
					});
					alert2.setButton("Yes",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int whichButton) {
                           //
						}
					});
		            alert2.show(); 
				    TextView messageView2 = (TextView)alert2.findViewById(android.R.id.message);
	                messageView2.setGravity(Gravity.CENTER);
	                //messageView.setHeight(140);
	                messageView2.setTextSize(17);
				    
	                Button bq3 = alert2.getButton(DialogInterface.BUTTON1);
	                bq3.setTextColor(Color.parseColor("#117AFE"));
	                bq3.setTypeface(null,Typeface.BOLD);
	                bq3.setTextSize(19);
	                Button bq4 = alert2.getButton(DialogInterface.BUTTON2);
	                bq4.setTextColor(Color.parseColor("#117AFE"));
	                //bq4.setTypeface(null,Typeface.BOLD);
	                bq4.setTextSize(19);
					break;
				case 3:
					i = new Intent(ctx, ItemsActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);					
					break;
				}
				
			}
		});

	}

}
