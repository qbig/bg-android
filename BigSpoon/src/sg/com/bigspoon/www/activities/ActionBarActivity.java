package sg.com.bigspoon.www.activities;

import static sg.com.bigspoon.www.data.Constants.BILL_URL;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_AUTHTOKEN;
import static sg.com.bigspoon.www.data.Constants.PREFS_NAME;
import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.adapters.ActionBarMenuAdapter;
import sg.com.bigspoon.www.data.Order;
import sg.com.bigspoon.www.data.TableModel;
import sg.com.bigspoon.www.data.User;
import android.app.ActionBar;
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
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class ActionBarActivity extends FragmentActivity {
	ActionBar actionBar;
	private GridView gridView;
	public static final int WATER = 1;
	public static final int WAITER = 2;
	public static final int BILL = 3;
	private SharedPreferences loginPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_action_bar);
		actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		loginPreferences = getSharedPreferences(PREFS_NAME, 0);
	}

	protected void loadMenu() {

		final Context ctx = getApplicationContext();
		Resources res = ctx.getResources();
		gridView = (GridView) findViewById(R.id.gv_action_menu);
		// Create the Custom Adapter Object
		ActionBarMenuAdapter actionBarMenuAdapter = new ActionBarMenuAdapter(
				this, 4);
		// Set the Adapter to GridView
		gridView.setAdapter(actionBarMenuAdapter);

		// Handling touch/click Event on GridView Item
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long arg3) {
				Intent i = null;
				switch (position) {
				case 0:
					if (User.getInstance(ActionBarActivity.this)
							.checkLocation()) {
						if (User.getInstance(ActionBarActivity.this).tableId == -1) {
							int requestCode = WATER;
							setUpTablePopup(requestCode);
						} else {
							int requestCode = WATER;
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
						if (User.getInstance(ActionBarActivity.this).tableId == -1) {
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
				case 2:
					if (User.getInstance(ActionBarActivity.this)
							.checkLocation()) {
						if (User.getInstance(ActionBarActivity.this).tableId == -1) {
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
				case 3:
					i = new Intent(ctx, ItemsActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
					break;
				}

			}
		});

	}

	protected void onTablePopupResult(int requestCode) {
		// TODO Auto-generated method stub
		switch (requestCode) {
		case WATER:
			Intent j = new Intent(ActionBarActivity.this,
					WaterServiceActivity.class);
			j.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(j);
			break;
		case WAITER:
			waitorPopup();
			break;
		case BILL:
			billPopup();
			break;
		}

	}

	@SuppressWarnings("deprecation")
	private void billPopup() {
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
				performBillRequest();
				Intent i = new Intent(ActionBarActivity.this,
						UserReviewActivity.class);
				startActivity(i);
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
		// TODO Auto-generated method stub
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
				//
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
					}
				}
				if (User.getInstance(ActionBarActivity.this).tableId == -1) {
					incorrectTableCodePopup(requestCode);
				} else {
					onTablePopupResult(requestCode);
				}
			}
		});
		alert.show();
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
		// TODO Auto-generated method stub
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
					}
				}
				if (User.getInstance(ActionBarActivity.this).tableId == -1) {
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
				.load(BILL_URL)
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
							Toast.makeText(ActionBarActivity.this,
									"Error requesting bills", Toast.LENGTH_LONG)
									.show();
							return;
						}
						Toast.makeText(ActionBarActivity.this, "Success",
								Toast.LENGTH_LONG).show();
					}
				});
	}

}
