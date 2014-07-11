package sg.com.bigspoon.www.activities;

import com.facebook.Session;
import com.facebook.SessionState;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.adapters.CustomListAdapter;
import static sg.com.bigspoon.www.data.Constants.*;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class OutletListActivity extends Activity {

	ImageButton orderButton;
	private Context context = this;
	ListView list;
	String[] web = { "Welcome to Kith ", "Testing !! The Groc.....", "Testing !! Strictly Pancakes" };
	String[] webdesc = { "5 Simon Road Singapore ", "81 Upper East Coast Rd ", "Infinte Studios , #1-06,21...." };
	Integer[] imageId = { R.drawable.kith, R.drawable.shinkushiya, R.drawable.strictlypancakes };
	String[] comingsoon = { "Coming soon!", " ", "Coming soon!" };
	private View mActionBarView;
	private ActionBar actionBar;
	private ImageButton orderHistoryButton;
	private ImageButton toggleButton;
	private ImageButton logoutButton;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		setupActionBar();		
		setupLogoutButton();
		setupHistoryButton();
		
		return super.onCreateOptionsMenu(menu);
	}

	private void setupHistoryButton() {
		orderHistoryButton = (ImageButton) mActionBarView.findViewById(R.id.order_history);
		orderHistoryButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(), OrderHistoryListActivity.class);
				startActivity(intent);
			}
		});
	}

	private void setupLogoutButton() {
		
		final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		logoutButton = (ImageButton) mActionBarView.findViewById(R.id.btn_logout);
		logoutButton.setImageResource(R.drawable.logout_button);
		logoutButton.setLayoutParams(params);
		logoutButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
		logoutButton.setPadding(22, 0, 0, 0);

		final StateListDrawable states = new StateListDrawable();
		states.addState(new int[] { android.R.attr.state_pressed },
				getResources().getDrawable(R.drawable.logout_button_pressed));
		states.addState(new int[] {}, getResources().getDrawable(R.drawable.logout_button));
		logoutButton.setImageDrawable(states);
		logoutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Session session = Session.getActiveSession();
				if (session != null && !session.isClosed()) {
					session.closeAndClearTokenInformation();
				}
				Intent intent = new Intent(OutletListActivity.this, EntryActivity.class);
				startActivity(intent);
			}
		});
	}

	private void setupActionBar() {
		mActionBarView = getLayoutInflater().inflate(R.layout.action_bar, null);
		
		toggleButton = (ImageButton) mActionBarView.findViewById(R.id.toggleButton);
		toggleButton.setVisibility(View.GONE);
		
		final TextView title = (TextView) mActionBarView.findViewById(R.id.title);
		title.setText(R.string.outlet_title);
		
		actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setCustomView(mActionBarView);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
	}
	
	private void initFBSession(Bundle savedInstanceState) {
		Session session = Session.getActiveSession();
		if (session == null) {
			if (savedInstanceState != null) {
				session = Session.restoreSession(this, null, null,
						savedInstanceState);
			}
			if (session == null) {
				session = new Session(this);
			}
			Session.setActiveSession(session);
			if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
				session.closeAndClearTokenInformation();
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_out_list);
		initFBSession(savedInstanceState);
		
		list = (ListView) findViewById(R.id.outlist);
		CustomListAdapter adapter = new CustomListAdapter(this, web, imageId, webdesc, comingsoon);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 1) {
					Intent intent = new Intent(getApplicationContext(), CategoriesListActivity.class);
					startActivity(intent);
				}
				if (position == 0 || position == 2) {
					AlertDialog alertDialog = new AlertDialog.Builder(OutletListActivity.this).create();
					alertDialog.setMessage("The restaurant is coming soon.");
					// Setting OK Button
					alertDialog.setButton("Okay", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// Write your code here to execute after
							// dialog closed
						}
					});

					// Showing Alert Message
					alertDialog.show();

					// Change the style of the button text and message
					TextView messageView = (TextView) alertDialog.findViewById(android.R.id.message);
					messageView.setGravity(Gravity.CENTER);
					messageView.setHeight(140);
					messageView.setTextSize(17);
					Button bq = alertDialog.getButton(DialogInterface.BUTTON1);
					bq.setTextColor(Color.parseColor("#117AFE"));
					bq.setTypeface(null, Typeface.BOLD);
					bq.setTextSize(19);
				}
			}
		});
		
	}
}
