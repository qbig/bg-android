package sg.com.bigspoon.www.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.StateListDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.adapters.OutletListAdapter;
import sg.com.bigspoon.www.data.Constants;
import sg.com.bigspoon.www.data.OutletDetailsModel;
import sg.com.bigspoon.www.data.OutletModel;
import sg.com.bigspoon.www.data.User;

import static sg.com.bigspoon.www.data.Constants.LIST_OUTLETS;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_EMAIL;
import static sg.com.bigspoon.www.data.Constants.NOTIF_LOCATION_UPDATED;
import static sg.com.bigspoon.www.data.Constants.OUTLET_ICON;
import static sg.com.bigspoon.www.data.Constants.OUTLET_ID;
import static sg.com.bigspoon.www.data.Constants.OUTLET_LOCATION_FILTER_DISTANCE;
import static sg.com.bigspoon.www.data.Constants.PREFS_NAME;
import static sg.com.bigspoon.www.data.Constants.TUTORIAL_SET;

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
	private BroadcastReceiver mLocationUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateListData();
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		setupActionBar();
		setupLogoutButton();
		setupHistoryButton();

		return super.onCreateOptionsMenu(menu);
	}

	private void setupHistoryButton() {
		orderHistoryButton = (ImageButton) mActionBarView.findViewById(R.id.order_history);
		orderHistoryButton.setVisibility(View.INVISIBLE);
	}

	private void setupLogoutButton() {

		logoutButton = (ImageButton) mActionBarView.findViewById(R.id.btn_back);
        logoutButton.setVisibility(View.VISIBLE);
        logoutButton.setImageResource(R.drawable.white_menu_icon);
		logoutButton.setScaleType(ImageButton.ScaleType.FIT_CENTER);
		logoutButton.setPadding(22, 0, 0, 0);

		final StateListDrawable states = new StateListDrawable();
		states.addState(new int[] { android.R.attr.state_pressed },
				getResources().getDrawable(R.drawable.white_menu_icon_pressed));
		states.addState(new int[] {}, getResources().getDrawable(R.drawable.white_menu_icon));
		logoutButton.setImageDrawable(states);
		logoutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				final SharedPreferences.Editor loginPrefsEditor = loginPreferences.edit();
                final String previousEmail = loginPreferences.getString(LOGIN_INFO_EMAIL, "");
				loginPrefsEditor.clear();
				loginPrefsEditor.putBoolean(TUTORIAL_SET, true);
                loginPrefsEditor.putString(LOGIN_INFO_EMAIL, previousEmail);
				loginPrefsEditor.commit();

				Session session = Session.getActiveSession();
				if (session != null && !session.isClosed()) {
					session.closeAndClearTokenInformation();
				}
				if (OutletListActivity.this.isTaskRoot()) {
					Intent intent = new Intent(OutletListActivity.this, EmailLoginActivity.class);
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
	protected void onResume() {
		super.onResume();
        LocationLibrary.forceLocationUpdate(this);
		updateListData();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_outlet_list);
		list = (ListView) findViewById(R.id.outlet_list);
		final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
		progressBar.setVisibility(View.VISIBLE);
		Ion.getDefault(this).configure().setLogging(ION_LOGGING_OUTLET_LIST, Log.DEBUG);
		initFBSession(savedInstanceState);

		loginPreferences = getSharedPreferences(PREFS_NAME, 0);
		Ion.with(this).load(LIST_OUTLETS).setHeader("Content-Type", "application/json; charset=utf-8")
				.as(new TypeToken<List<OutletModel>>() {
				}).setCallback(new FutureCallback<List<OutletModel>>() {
					@Override
					public void onCompleted(Exception e, List<OutletModel> result) {
						if (e != null) {
							if (Constants.LOG) {
								Toast.makeText(OutletListActivity.this, "Error loading outlets", Toast.LENGTH_LONG)
										.show();
							} else {
								final JSONObject info = new JSONObject();
								try {
									info.put("error", e.toString());
								} catch (JSONException e1) {
									e1.printStackTrace();
								}
								User.getInstance(OutletListActivity.this).mMixpanel
										.track("Error loading outlets", info);
							}

							return;
						}
						progressBar.setVisibility(View.GONE);
						outlets = result;
						updateListData();
						list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								outletSelected = outlets.get(position);
								if (outletSelected.isActive) {
									Intent intent = new Intent(OutletListActivity.this, CategoriesListActivity.class);
									intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
									intent.putExtra(OUTLET_ID, outletSelected.outletID);

									final Editor loginPrefEditor = loginPreferences.edit();
									loginPrefEditor.putInt(OUTLET_ID, outletSelected.outletID);
									loginPrefEditor.putString(OUTLET_ICON, outletSelected.restaurant.icon.thumbnail);
									loginPrefEditor.commit();
									final OutletDetailsModel currentOutlet = User.getInstance(getApplicationContext()).currentOutlet;
									if (currentOutlet != null && outletSelected.outletID != currentOutlet.outletID) {
										User.getInstance(getApplicationContext()).currentSession.swithToOulet(outletSelected.name);
									}
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

	private void updateListData() {
		if (outlets == null) {
			return;
		}
		
		if (User.getInstance(getApplicationContext()).currentLocation != null) {
			final Iterator<OutletModel> it = outlets.iterator();
			final Location currentLocation = User.getInstance(getApplicationContext()).currentLocation;
			while (it.hasNext()){
				OutletModel outletToFilter = it.next();
				final Location locationToFilter = new Location("lhs");
				locationToFilter.setLatitude(outletToFilter.lat);
				locationToFilter.setLongitude(outletToFilter.lng);
				if (currentLocation.distanceTo(locationToFilter) > OUTLET_LOCATION_FILTER_DISTANCE) {
					it.remove();
				}
			}
			Collections.sort(outlets, new Comparator<OutletModel>() {
				@Override
				public int compare(OutletModel lhs, OutletModel rhs) {
					final Location currentLocation = User.getInstance(getApplicationContext()).currentLocation;
					final Location locationForLhs = new Location("lhs");
					locationForLhs.setLatitude(lhs.lat);
					locationForLhs.setLongitude(lhs.lng);
					final Location locationForRhs = new Location("rhs");
					locationForRhs.setLatitude(rhs.lat);
					locationForRhs.setLongitude(rhs.lng);
					int distanceForLhs = (int) currentLocation.distanceTo(locationForLhs);
					int distanceForRhs = (int) currentLocation.distanceTo(locationForRhs);

					return distanceForLhs - distanceForRhs;
				}
			});
			
		} else {
			LocalBroadcastManager.getInstance(this).registerReceiver(mLocationUpdateReceiver,
					new IntentFilter(NOTIF_LOCATION_UPDATED));
			LocalBroadcastManager.getInstance(this).registerReceiver(mLocationUpdateReceiver,
					new IntentFilter(LocationLibraryConstants.getLocationChangedPeriodicBroadcastAction()));
		}
		
		list.setAdapter(new OutletListAdapter(OutletListActivity.this, outlets));
	}

	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce || !this.isTaskRoot()) {
			super.onBackPressed();
			return;
		}

		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, "Please click BACK again to exit.", Toast.LENGTH_SHORT).show();

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				doubleBackToExitPressedOnce = false;
			}
		}, 2000);
	}

}
