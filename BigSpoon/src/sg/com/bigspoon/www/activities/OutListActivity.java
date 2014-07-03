package sg.com.bigspoon.www.activities;

import com.facebook.Session;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.adapters.CustomListAdapter;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class OutListActivity extends Activity {

	ImageButton orderButton;
	private Context context = this;
	ListView list;
	String[] web = { "Welcome to Kith ", "Testing !! The Groc.....",
			"Testing !! Strictly Pancakes" };
	String[] webdesc = { "5 Simon Road Singapore ", "81 Upper East Coast Rd ",
			"Infinte Studios , #1-06,21...." };
	Integer[] imageId = { R.drawable.kith, R.drawable.shinkushiya,
			R.drawable.strictlypancakes };
	String[] comingsoon={"Coming soon!"," ","Coming soon!"};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// MenuInflater inflater = getMenuInflater();
		// inflater.inflate(R.menu.action_bar, menu);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		// addListenerOnButtonLogout();
		// displaying custom ActionBar
		View mActionBarView = getLayoutInflater().inflate(R.layout.action_bar,
				null);
		actionBar.setCustomView(mActionBarView);
		//actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		TextView title = (TextView) mActionBarView.findViewById(R.id.title);
        
		title.setText("OutLets");

		ImageButton ibItem1 = (ImageButton) mActionBarView
				.findViewById(R.id.btn_logout);
		ImageButton togglebutton = (ImageButton) mActionBarView
				.findViewById(R.id.toggleButton);
		togglebutton.setVisibility(View.GONE);
		ibItem1.setImageResource(R.drawable.logout_button);
		RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	    params.addRule(RelativeLayout.CENTER_VERTICAL);
		ibItem1.setLayoutParams(params);
	    ibItem1.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
		ibItem1.setPadding(22, 0, 0, 0);
		
		StateListDrawable states = new StateListDrawable();
		states.addState(new int[] {android.R.attr.state_pressed},
		    getResources().getDrawable(R.drawable.logout_button_pressed));
		states.addState(new int[] { },
		    getResources().getDrawable(R.drawable.logout_button));
		ibItem1.setImageDrawable(states);

		ibItem1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// ...
				Session session = Session.getActiveSession();  
	        	if (!session.isClosed()) {
	                session.closeAndClearTokenInformation();
	            }
				Intent intent = new Intent(getApplicationContext(),
						EntryActivity.class);
				startActivity(intent);
			}
		});

		ImageButton ibItem2 = (ImageButton) mActionBarView
				.findViewById(R.id.order_history);
		ibItem2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// ...

				Intent intent = new Intent(getApplicationContext(),
						OrderHistoryListActivity.class);
				startActivity(intent);
			}
		});
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_out_list);

		list = (ListView) findViewById(R.id.list);
		CustomListAdapter adapter = new CustomListAdapter(this, web, imageId, webdesc,comingsoon);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position==1){Intent intent = new Intent(getApplicationContext(),
						CategoriesListActivity.class);
				startActivity(intent);}
				if(position==0||position==2){
					AlertDialog alertDialog = new AlertDialog.Builder(OutListActivity.this).create();
	 
					// Setting Dialog Title
	                TextView title =  new TextView(context);
	                title.setText("The restaurant is coming soon");
	                title.setGravity(Gravity.CENTER);
	                title.setHeight(120);
	                title.setTextSize(17);
	                title.setTypeface(Typeface.DEFAULT_BOLD);
	                alertDialog.setCustomTitle(title);
	                // Setting OK Button
	                alertDialog.setButton("Okay", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                // Write your code here to execute after dialog closed
	                }
	                });
	 
	                // Showing Alert Message
	                alertDialog.show();
				}
			}
		});

	}

}
