package sg.com.bigspoon.www.activities;

import static sg.com.bigspoon.www.data.Constants.LIST_OUTLETS;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_AUTHTOKEN;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_AVATAR_URL;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_EMAIL;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_FIRST_NAME;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_LAST_NAME;
import static sg.com.bigspoon.www.data.Constants.PREFS_NAME;
import static sg.com.bigspoon.www.data.Constants.OUTLET_ID;
import static sg.com.bigspoon.www.data.Constants.OUTLET_ICON;

import java.util.List;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.adapters.OutletListAdapter;
import sg.com.bigspoon.www.data.OutletModel;
import sg.com.bigspoon.www.data.User;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.StateListDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class OutletListActivity extends Activity {
	private static String ION_LOGGING_OUTLET_LIST = "ion-outlet-list";
	private SharedPreferences loginPreferences;
	public static List<OutletModel> outlets;
	ImageButton orderButton;

	ListView list;

	private View mActionBarView;
	private ActionBar actionBar;
	private ImageButton orderHistoryButton;
	private ImageButton toggleButton;
	private ImageButton logoutButton;
	OutletModel outletSelected;
	private boolean doubleBackToExitPressedOnce;

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
				intent.putExtra("callingActivityName", "OutletListActivity");
				startActivity(intent);
			}
		});
	}

	private void setupLogoutButton() {

		final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		logoutButton = (ImageButton) mActionBarView.findViewById(R.id.btn_back);
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
				final SharedPreferences.Editor loginPrefsEditor = loginPreferences.edit();
				loginPrefsEditor.clear();
				loginPrefsEditor.commit();

				Session session = Session.getActiveSession();
				if (session != null && !session.isClosed()) {
					session.closeAndClearTokenInformation();
				}
				if (OutletListActivity.this.isTaskRoot()) {
					Intent intent = new Intent(OutletListActivity.this, EntryActivity.class);
					startActivity(intent);
				} else {
					finish();
				}

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
				session = Session.restoreSession(this, null, null, savedInstanceState);
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
		Ion.getDefault(this).configure().setLogging(ION_LOGGING_OUTLET_LIST, Log.DEBUG);
		initFBSession(savedInstanceState);

		loginPreferences = getSharedPreferences(PREFS_NAME, 0);
		Ion.with(this).load(LIST_OUTLETS).setHeader("Content-Type", "application/json; charset=utf-8")
				.as(new TypeToken<List<OutletModel>>() {
				}).setCallback(new FutureCallback<List<OutletModel>>() {
					@Override
					public void onCompleted(Exception e, List<OutletModel> result) {
						if (e != null) {
							Toast.makeText(OutletListActivity.this, "Error loading history", Toast.LENGTH_LONG).show();
							return;
						}

						outlets = result;
						list = (ListView) findViewById(R.id.outlist);
						list.setAdapter(new OutletListAdapter(OutletListActivity.this, result));
						list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								outletSelected = outlets.get(position);
								if (outletSelected.isActive) {
									Intent intent = new Intent(OutletListActivity.this, CategoriesListActivity.class);
									intent.putExtra(OUTLET_ID, outletSelected.outletID);

									final Editor loginPrefEditor = loginPreferences.edit();
									loginPrefEditor.putInt(OUTLET_ID, outletSelected.outletID);
									loginPrefEditor.putString(OUTLET_ICON, outletSelected.restaurant.icon.thumbnail);
									loginPrefEditor.commit();

									OutletListActivity.this.startActivity(intent);
								} else {
									showComingSoonDialog();
								}
							}

							@SuppressWarnings("deprecation")
							private void showComingSoonDialog() {
								AlertDialog alertDialog = new AlertDialog.Builder(OutletListActivity.this).create();
								alertDialog.setMessage("The restaurant is coming soon.");

								alertDialog.setButton("Okay", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {

									}
								});

								alertDialog.show();

								// Change the style of the button text and
								// message
								TextView messageView = (TextView) alertDialog.findViewById(android.R.id.message);
								messageView.setGravity(Gravity.CENTER);
								messageView.setHeight(140);
								messageView.setTextSize(17);
								Button okButton = alertDialog.getButton(DialogInterface.BUTTON1);
								okButton.setTextColor(Color.parseColor("#117AFE"));
								okButton.setTypeface(null, Typeface.BOLD);
								okButton.setTextSize(19);
							}
						});
					}
				});
	}
	
	@Override
	public void onBackPressed() {
	    if (doubleBackToExitPressedOnce || !this.isTaskRoot()) {
	        super.onBackPressed();
	        return;
	    }

	    this.doubleBackToExitPressedOnce = true;
	    Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

	    new Handler().postDelayed(new Runnable() {

	        @Override
	        public void run() {
	            doubleBackToExitPressedOnce=false;                       
	        }
	    }, 2000);
	} 
	
}
