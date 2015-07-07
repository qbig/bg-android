package sg.com.bigspoon.www.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.adapters.ActionBarMenuAdapter;
import sg.com.bigspoon.www.data.Constants;
import sg.com.bigspoon.www.data.User;

import static sg.com.bigspoon.www.data.Constants.BILL_URL;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_AUTHTOKEN;
import static sg.com.bigspoon.www.data.Constants.OUTLET_ID;
import static sg.com.bigspoon.www.data.Constants.OUTLET_NAME;
import static sg.com.bigspoon.www.data.Constants.PREFS_NAME;
import static sg.com.bigspoon.www.data.Constants.TABLE_ID;
import static sg.com.bigspoon.www.data.Constants.getURL;

public class ActionBarActivity extends AppCompatActivity {
	ActionBar actionBar;
	private GridView gridView;
	public static final int WATER = 1;
	public static final int WAITER = 2;
	public static final int BILL = 3;
	private SharedPreferences loginPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_action_bar);
//		actionBar = getSupportActionBar();
//		actionBar.setDisplayShowTitleEnabled(true);
		loginPreferences = getSharedPreferences(PREFS_NAME, 0);
	}

	protected void loadMenu() {

		final Context ctx = getApplicationContext();
		Resources res = ctx.getResources();
		gridView = (GridView) findViewById(R.id.gv_action_menu);
		// Create the Custom Adapter Object
		ActionBarMenuAdapter actionBarMenuAdapter = new ActionBarMenuAdapter(
				this, 3);
		// Set the Adapter to GridView
		gridView.setAdapter(actionBarMenuAdapter);

		// Handling touch/click Event on GridView Item
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long arg3) {

				switch (position) {
				case 0:
					if (User.getInstance(ActionBarActivity.this)
							.checkLocation()) {
						if (! User.getInstance(ActionBarActivity.this).isTableValidForCurrentOutlet()) {
							int requestCode = WAITER;
							setUpTablePopup(requestCode);
						} else {
							int requestCode = WAITER;
							onTablePopupResult(requestCode);
						}
					} else {
						setUpLocationFailPopup();
						User.getInstance(ActionBarActivity.this).tableId = -1;
					}
					break;
				case 1:
					if (User.getInstance(ActionBarActivity.this)
							.checkLocation()) {
						if (! User.getInstance(ActionBarActivity.this).isTableValidForCurrentOutlet()) {
							int requestCode = BILL;
							setUpTablePopup(requestCode);
						} else {
							int requestCode = BILL;
							onTablePopupResult(requestCode);
						}
					} else {
						setUpLocationFailPopup();
						User.getInstance(ActionBarActivity.this).tableId = -1;
					}
					break;
				case 2:
					Intent i = new Intent(ctx, ItemsActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					User.getInstance(ActionBarActivity.this).currentSession.getCurrentOrder().clearUndoCache();
					User.getInstance(ActionBarActivity.this).clearUndoPopup();
					startActivity(i);
					break;
				}

			}
		});

	}

	protected void onTablePopupResult(int requestCode) {
		switch (requestCode) {
		case WAITER:
				if (User.getInstance(this).currentOutlet.isWaterEnabled) {
					waitorPopup();
				}  else {
					AlertDialog alertLocationFail = new AlertDialog.Builder(ActionBarActivity.this).create();
					alertLocationFail.setMessage(User.getInstance(this).currentOutlet.waterText);
					alertLocationFail.setView(null);
					alertLocationFail.setButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
						}
					});
					alertLocationFail.show();
				}
			break;
		case BILL:
			if (User.getInstance(this).currentOutlet.isBillEnabled) {
				billPopup();
			} else {
				Intent i = new Intent(this, ItemsActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				User.getInstance(ActionBarActivity.this).currentSession.getCurrentOrder().clearUndoCache();
				User.getInstance(ActionBarActivity.this).clearUndoPopup();
				startActivity(i);
			}
			break;
		}

	}

	@SuppressWarnings("deprecation")
	private void billPopup() {
        User.getInstance(ActionBarActivity.this).mMixpanel.track("Show Bill Popup(Menu)", null);
		final AlertDialog alert2 = new AlertDialog.Builder(this).create();
		alert2.setMessage("Would you like your bill?");
		alert2.setView(null);
		// Set an EditText view to get user input

		alert2.setButton2("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//
			}
		});
		alert2.setButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if (User.getInstance(ActionBarActivity.this).currentSession.getPastOrder().getTotalQuantity() != 0){
                    User.getInstance(ActionBarActivity.this).mMixpanel.track("Ask Bills(Menu)", null);
					performBillRequest();
					Intent i = new Intent(ActionBarActivity.this,
							UserReviewActivity.class);
					startActivity(i);
				} else {
					Toast.makeText(ActionBarActivity.this, "You haven't ordered anything yet :)", Toast.LENGTH_LONG).show();
				}
				
			}
		});
		alert2.show();
		TextView messageView2 = (TextView) alert2
				.findViewById(android.R.id.message);
		messageView2.setGravity(Gravity.CENTER);
		// messageView.setHeight(140);
		messageView2.setTextSize(17);

		Button bq3 = alert2.getButton(DialogInterface.BUTTON1);
		bq3.setTextColor(Color.parseColor("#117AFE"));
		bq3.setTypeface(null, Typeface.BOLD);
		bq3.setTextSize(19);
		Button bq4 = alert2.getButton(DialogInterface.BUTTON2);
		bq4.setTextColor(Color.parseColor("#117AFE"));
		// bq4.setTypeface(null,Typeface.BOLD);
		bq4.setTextSize(19);
	}

	@SuppressWarnings("deprecation")
	protected void waitorPopup() {
        User.getInstance(ActionBarActivity.this).mMixpanel.track("Show Waiter Popup(Menu)", null);
		final EditText inputWaitor = new EditText(this);
		final AlertDialog alertWaitor = new AlertDialog.Builder(this).create();
		alertWaitor.setTitle("Call For Service");
		alertWaitor.setMessage("Require assistance from the waiter?");
		inputWaitor.setHint("How could we help?");
		alertWaitor.setView(inputWaitor, 10, 0, 10, 0);
		alertWaitor.setButton2("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//
			}
		});
		alertWaitor.setButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				User.getInstance(ActionBarActivity.this).mMixpanel.track("Ask Waiters(Menu)", null);
				User.getInstance(ActionBarActivity.this).requestForWaiter(
						inputWaitor.getText().toString());
			}
		});
		alertWaitor.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				final int msgId = alertWaitor.getContext().getResources()
						.getIdentifier("android:id/message", null, null);
				TextView msgView = (TextView) alertWaitor.findViewById(msgId);
				msgView.setTextSize(16);
			}
		});

		alertWaitor.show();


		final int alertTitle = alertWaitor.getContext().getResources()
				.getIdentifier("alertTitle", "id", "android");
		TextView titleView = (TextView) alertWaitor.findViewById(alertTitle);
		titleView.setGravity(Gravity.CENTER);
		titleView.setTypeface(null, Typeface.BOLD);
		titleView.setTextSize(19);
		titleView.setTextColor(getResources().getColor(android.R.color.black));
		int divierId = alertWaitor.getContext().getResources()
				.getIdentifier("android:id/titleDivider", null, null);
		View divider = alertWaitor.findViewById(divierId);
		divider.setBackgroundColor(getResources().getColor(
				android.R.color.transparent));
		TextView messageViewBill = (TextView) alertWaitor
				.findViewById(android.R.id.message);
		messageViewBill.setGravity(Gravity.CENTER);
		// messageView.setHeight(140);
		messageViewBill.setTextSize(17);

		Button bq1Waitor = alertWaitor.getButton(DialogInterface.BUTTON1);
		bq1Waitor.setTextColor(Color.parseColor("#117AFE"));
		bq1Waitor.setTypeface(null, Typeface.BOLD);
		bq1Waitor.setTextSize(19);
		Button bq2Waitor = alertWaitor.getButton(DialogInterface.BUTTON2);
		bq2Waitor.setTextColor(Color.parseColor("#117AFE"));
		// bq2.setTypeface(null,Typeface.BOLD);
		bq2Waitor.setTextSize(19);
	}

	@SuppressWarnings("deprecation")
	public void setUpTablePopup(final int requestCode) {
		final EditText input = new EditText(this);
		final AlertDialog alert = new AlertDialog.Builder(this).create();
		alert.setMessage("Please enter your table ID located on the BigSpoon table stand");
		alert.setView(input, 10, 0, 10, 0);

		alert.setButton2("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//
			}
		});
		alert.setButton("Okay", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String tableCode = input.getText().toString();
				for (int k = 0; k < User.getInstance(ActionBarActivity.this).currentOutlet.tables.length; k++) {
					if (User.getInstance(ActionBarActivity.this).currentOutlet.tables[k].code
							.toLowerCase().equals(tableCode.toLowerCase())) {
						User.getInstance(ActionBarActivity.this).tableId = User
								.getInstance(ActionBarActivity.this).currentOutlet.tables[k].id;
						User.getInstance(ActionBarActivity.this).isForTakeAway = User
								.getInstance(ActionBarActivity.this).currentOutlet.tables[k].isForTakeAway;
                        final SharedPreferences.Editor loginEditor = loginPreferences.edit();
                        loginEditor.putInt(TABLE_ID, User.getInstance(ActionBarActivity.this).tableId);
						loginEditor.putInt(OUTLET_ID, User.getInstance(ActionBarActivity.this).currentOutlet.outletID);
						loginEditor.putString(OUTLET_NAME, User.getInstance(ActionBarActivity.this).currentOutlet.name);
                        loginEditor.commit();
					}
				}
				if (! User.getInstance(ActionBarActivity.this).isTableValidForCurrentOutlet()) {
					incorrectTableCodePopup(requestCode);
				} else {
					onTablePopupResult(requestCode);
				}
			}
		});
		alert.show();
		
		(new Handler()).postDelayed(new Runnable() {
			public void run() {
				input.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
						MotionEvent.ACTION_DOWN, 0, 0, 0));
				input.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
						MotionEvent.ACTION_UP, 0, 0, 0));
			}
		}, 200);
		
		TextView messageView = (TextView) alert
				.findViewById(android.R.id.message);
		messageView.setGravity(Gravity.CENTER);
		// messageView.setHeight(140);
		messageView.setTextSize(17);

		Button bq1 = alert.getButton(DialogInterface.BUTTON1);
		bq1.setTextColor(Color.parseColor("#117AFE"));
		bq1.setTypeface(null, Typeface.BOLD);
		bq1.setTextSize(19);
		Button bq2 = alert.getButton(DialogInterface.BUTTON2);
		bq2.setTextColor(Color.parseColor("#117AFE"));
		// bq2.setTypeface(null,Typeface.BOLD);
		bq2.setTextSize(19);
	}

	@SuppressWarnings("deprecation")
	protected void incorrectTableCodePopup(final int requestCode) {
		final EditText inputIncorrect = new EditText(this);
		final AlertDialog alertIncorrect = new AlertDialog.Builder(this)
				.create();
		alertIncorrect
				.setMessage("Table ID incorrect. Please enter your table ID or ask your friendly waiter for assistance");
		alertIncorrect.setView(inputIncorrect, 10, 0, 10, 0);

		alertIncorrect.setButton2("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//
					}
				});
		alertIncorrect.setButton("Okay", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String tableCode = inputIncorrect.getText().toString();
				for (int k = 0; k < User.getInstance(ActionBarActivity.this).currentOutlet.tables.length; k++) {
					if (User.getInstance(ActionBarActivity.this).currentOutlet.tables[k].code
							.toLowerCase().equals(tableCode.toLowerCase())) {
						User.getInstance(ActionBarActivity.this).tableId = User
								.getInstance(ActionBarActivity.this).currentOutlet.tables[k].id;
						User.getInstance(ActionBarActivity.this).isForTakeAway = User
								.getInstance(ActionBarActivity.this).currentOutlet.tables[k].isForTakeAway;
                        final SharedPreferences.Editor loginEditor = loginPreferences.edit();
                        loginEditor.putInt(TABLE_ID, User.getInstance(ActionBarActivity.this).tableId);
						loginEditor.putInt(OUTLET_ID, User.getInstance(ActionBarActivity.this).currentOutlet.outletID);
						loginEditor.putString(OUTLET_NAME, User.getInstance(ActionBarActivity.this).currentOutlet.name);
                        loginEditor.commit();
					}
				}
				if (! User.getInstance(ActionBarActivity.this).isTableValidForCurrentOutlet()) {
					incorrectTableCodePopup(requestCode);
				} else {
					onTablePopupResult(requestCode);
				}
			}
		});
		alertIncorrect.show();
		TextView messageView = (TextView) alertIncorrect
				.findViewById(android.R.id.message);
		messageView.setGravity(Gravity.CENTER);
		// messageView.setHeight(140);
		messageView.setTextSize(17);

		Button bq1 = alertIncorrect.getButton(DialogInterface.BUTTON1);
		bq1.setTextColor(Color.parseColor("#117AFE"));
		bq1.setTypeface(null, Typeface.BOLD);
		bq1.setTextSize(19);
		Button bq2 = alertIncorrect.getButton(DialogInterface.BUTTON2);
		bq2.setTextColor(Color.parseColor("#117AFE"));
		// bq2.setTypeface(null,Typeface.BOLD);
		bq2.setTextSize(19);

	}

	@SuppressWarnings("deprecation")
	private void setUpLocationFailPopup() {
		final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		AlertDialog alertLocationFail = new AlertDialog.Builder(
				ActionBarActivity.this).create();
		alertLocationFail.setTitle("BigSpoon couldn't find you");
		alertLocationFail
				.setMessage("Orders can only be sent when you are at the restaurant."
						+ "\n\n"
						+ "If you are already there, kindly speak to the friendly waitor for your orders.");
		alertLocationFail.setView(null);
		alertLocationFail.setButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (!manager
								.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
							buildAlertMessageNoGps();
						}
					}
				});
		alertLocationFail.show();
		int divierId = alertLocationFail.getContext().getResources()
				.getIdentifier("android:id/titleDivider", null, null);
		View divider = alertLocationFail.findViewById(divierId);
		divider.setBackgroundColor(getResources().getColor(
				android.R.color.transparent));
		final int alertTitle = alertLocationFail.getContext().getResources()
				.getIdentifier("alertTitle", "id", "android");
		TextView titleView = (TextView) alertLocationFail
				.findViewById(alertTitle);
		titleView.setGravity(Gravity.CENTER);
		titleView.setTypeface(null, Typeface.BOLD);
		titleView.setTextSize(19);
		titleView.setTextColor(getResources().getColor(android.R.color.black));

		TextView messageView = (TextView) alertLocationFail
				.findViewById(android.R.id.message);
		messageView.setGravity(Gravity.CENTER);
		messageView.setPadding(20, 0, 20, 25);
		messageView.setTextSize(17);
	}

	private void buildAlertMessageNoGps() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Your GPS seems to be disabled, do you want to enable it?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(final DialogInterface dialog,
									final int id) {
								startActivity(new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog,
							final int id) {
						dialog.cancel();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	private void performBillRequest() {

		Ion.with(this)
				.load(getURL(BILL_URL))
				.setHeader("Content-Type", "application/json; charset=utf-8")
				.setHeader(
						"Authorization",
						"Token "
								+ loginPreferences.getString(
										LOGIN_INFO_AUTHTOKEN, ""))
				.setJsonObjectBody(
						User.getInstance(ActionBarActivity.this).getTableId())
				.asJsonObject().setCallback(new FutureCallback<JsonObject>() {

					@Override
					public void onCompleted(Exception e, JsonObject result) {
						if (e != null) {
							if (Constants.LOG) {
								Toast.makeText(ActionBarActivity.this,
										"Error requesting bills", Toast.LENGTH_LONG)
										.show();
							} else {
								final JSONObject info = new JSONObject();
								try {
									info.put("error", e.toString());
								} catch (JSONException e1) {
									Crashlytics.logException(e);
									e1.printStackTrace();
								}
								User.getInstance(ActionBarActivity.this).mMixpanel.track("Error requesting bills", info);
							}
							
							return;
						}
						if (User.getInstance(ActionBarActivity.this).currentOutlet.isBillEnabled){
							Toast.makeText(ActionBarActivity.this, "Request for bill is submitted, the waiter will be right with you.", Toast.LENGTH_LONG).show();
						}
					}
				});
	}

}
