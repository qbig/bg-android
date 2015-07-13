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
import com.github.johnpersano.supertoasts.SuperToast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.adapters.OutletListAdapter;
import sg.com.bigspoon.www.data.BigSpoon;
import sg.com.bigspoon.www.data.OutletDetailsModel;
import sg.com.bigspoon.www.data.OutletModel;
import sg.com.bigspoon.www.data.User;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static sg.com.bigspoon.www.data.Constants.LIST_OUTLETS;
import static sg.com.bigspoon.www.data.Constants.NOTIF_LOCATION_UPDATED;
import static sg.com.bigspoon.www.data.Constants.OUTLET_ICON;
import static sg.com.bigspoon.www.data.Constants.OUTLET_ID;
import static sg.com.bigspoon.www.data.Constants.OUTLET_LOCATION_FILTER_DISTANCE;
import static sg.com.bigspoon.www.data.Constants.PREFS_NAME;
import static sg.com.bigspoon.www.data.Constants.SHOULD_SHOW_STEPS_REMINDER;
import static sg.com.bigspoon.www.data.Constants.getURL;

public class OutletListActivity extends Activity implements AdapterView.OnItemClickListener {
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
	private ProgressBar progressBar;
    private static int MAX_TRY_COUNT = 6;
    private int loadCount = 0;
    private Handler handler;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		setupActionBar();
		setupLogoutButton();
		setupHistoryButton();

		return super.onCreateOptionsMenu(menu);
	}

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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
		states.addState(new int[]{}, getResources().getDrawable(R.drawable.white_menu_icon));
		logoutButton.setImageDrawable(states);
		logoutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

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
		if (User.getInstance(this).shouldGoToOutlet){
			loadOutlets();
		}
        LocationLibrary.forceLocationUpdate(this);
		updateListData();
	}

	@Override
	protected  void onPause() {
		super.onPause();
		progressBar.setVisibility(View.GONE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_outlet_list);
		list = (ListView) findViewById(R.id.outlet_list);
		list.setOnItemClickListener(this);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		Ion.getDefault(this).configure().setLogging(ION_LOGGING_OUTLET_LIST, Log.DEBUG);
		initFBSession(savedInstanceState);
		loginPreferences = getSharedPreferences(PREFS_NAME, 0);
        handler = new Handler();
	}

	private void loadOutlets(){
		progressBar.setVisibility(View.VISIBLE);
		Ion.with(this).load(getURL(LIST_OUTLETS)).setHeader("Content-Type", "application/json; charset=utf-8")
				.as(new TypeToken<List<OutletModel>>() {
				}).setCallback(new FutureCallback<List<OutletModel>>() {
			@Override
			public void onCompleted(Exception e, List<OutletModel> result) {
				final User user = User.getInstance(getApplicationContext());
				user.logRemote("loading outlets", e, null);
				if (result != null) {
					progressBar.setVisibility(View.GONE);
					loadCount = 0;
					outlets = result;
					// Test
					Gson gson = new Gson();
					System.out.println(gson.toJsonTree(outlets).toString());

					outlets = gson.fromJson(gson.toJsonTree(outlets).toString(), new TypeToken<List<OutletModel>>() {}.getType());
					// Test -- End
					updateListData();
					OutletListActivity.this.navigateToPresetOutletIfNecessary();
				} else {
					if (loadCount < MAX_TRY_COUNT) {
						loadCount++;
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								loadOutlets();
							}
						}, 1000);
					} else {
						loadCount = 0;
						if (user.wifiIsConnected()) {
							user.mMixpanel.track("Outlets loading failed, WIFI Connected", null);
						} else {
							user.mMixpanel.track("Outlets loading failed, WIFI Disconnected", null);
						}
						progressBar.setVisibility(View.GONE);
						SuperToast.create(getApplicationContext(), "Sorry:(. Please order directly from the counter.", SuperToast.Duration.EXTRA_LONG).show();
						finish();
                    }
				}
			}
		});
	}

	private void navigateToPresetOutletIfNecessary() {
		final int presetOutletId = getSharedPreferences(PREFS_NAME, 0).getInt(OUTLET_ID, -1);
		final OutletModel presetOutlet = getOutletWithId(presetOutletId);
		if (presetOutletId != -1 && presetOutlet != null && User.getInstance(this).shouldGoToOutlet){
			Intent intent = new Intent(OutletListActivity.this, CategoriesListActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra(OUTLET_ID, presetOutlet.outletID);
			intent.putExtra(SHOULD_SHOW_STEPS_REMINDER, true);
			final Editor loginPrefEditor = loginPreferences.edit();
			loginPrefEditor.putString(OUTLET_ICON, presetOutlet.restaurant.icon.thumbnail);
			loginPrefEditor.commit();
			final OutletDetailsModel currentOutlet = User.getInstance(getApplicationContext()).currentOutlet;
			if (currentOutlet != null && presetOutlet.outletID != currentOutlet.outletID) {
				User.getInstance(getApplicationContext()).currentSession.swithToOulet(presetOutlet.name);
			}
			OutletListActivity.this.startActivity(intent);
			this.startActivity(intent);
			User.getInstance(this).shouldGoToOutlet = false;
		}
	}
	private OutletModel getOutletWithId(int outletId){
		if (outlets!=null){
			for (OutletModel outlet: outlets){
				if (outlet.outletID == outletId){
					return outlet;
				}
			}
		}
		return null;
	}

	private void updateListData() {
		progressBar.setVisibility(View.GONE);
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
			final int outletId = getSharedPreferences(PREFS_NAME, 0).getInt(OUTLET_ID, -1);
			Collections.sort(outlets, new Comparator<OutletModel>() {
				@Override
				public int compare(OutletModel lhs, OutletModel rhs) {
					if (lhs.outletID == outletId) {
						return -1;
					}
					if (rhs.outletID == outletId) {
						return 1;
					}
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
			((BigSpoon)getApplicationContext()).onStart = true;
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(outlets == null) return ;
		outletSelected = outlets.get(position);
		if (outletSelected.isActive) {
			Intent intent = new Intent(OutletListActivity.this, CategoriesListActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra(OUTLET_ID, outletSelected.outletID);

			final Editor loginPrefEditor = loginPreferences.edit();
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

}
