package sg.com.bigspoon.www.activities;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.R.id;
import sg.com.bigspoon.www.R.layout;
import sg.com.bigspoon.www.adapters.ActionBarMenuAdapter;
import sg.com.bigspoon.www.data.User;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
		final EditText inputWaitor = new EditText(this);
		final AlertDialog alert = new AlertDialog.Builder(this).create();
		final AlertDialog alertWaitor = new AlertDialog.Builder(this).create();
		final AlertDialog alert2 = new AlertDialog.Builder(this).create();
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
							.checkLocation(
									User.getInstance(ActionBarActivity.this).currentLocation)) {
						setUpTableCodePopupWater(ctx, input, alert);
					} else {
						setUpLocationFailPopup();

					}
					break;

				case 1:
					if (User.getInstance(ActionBarActivity.this)
							.checkLocation(
									User.getInstance(ActionBarActivity.this).currentLocation)) {
						setUpTableCodePopupWaitor(input,inputWaitor, alert,alertWaitor);
					} else {
						setUpLocationFailPopup();
					}
					break;
				case 2:
					if (User.getInstance(ActionBarActivity.this)
							.checkLocation(
									User.getInstance(ActionBarActivity.this).currentLocation)) {
						setUpTableCodePopupBill(input, alert, alert2);
					} else {
						setUpLocationFailPopup();

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

	@SuppressWarnings("deprecation")
	private void billPopup(final AlertDialog alert2) {
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
				//
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
	private void setUpTableCodePopupWater(final Context ctx,
			final EditText input, final AlertDialog alert) {
		alert.setMessage("Please enter your table ID located on the BigSpoon table stand");
		alert.setView(input, 10, 0, 10, 0);

		alert.setButton2("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//
			}
		});
		alert.setButton("Okay", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Intent j = new Intent(ctx, WaterServiceActivity.class);
				j.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(j);
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
	private void setUpTableCodePopupWaitor(final EditText input,
			final EditText inputWaitor, final AlertDialog alert,
			final AlertDialog alertWaitor) {
		alert.setMessage("Please enter your table ID located on the BigSpoon table stand");
		alert.setView(input, 10, 0, 10, 0);

		alert.setButton2("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//
			}
		});
		alert.setButton("Okay", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				waitorPopup(inputWaitor, alertWaitor);
			}
		});
		alert.show();
		TextView messageViewWaitor = (TextView) alert
				.findViewById(android.R.id.message);
		messageViewWaitor.setGravity(Gravity.CENTER);
		// messageView.setHeight(140);
		messageViewWaitor.setTextSize(17);

		Button bq1Waitor = alert.getButton(DialogInterface.BUTTON1);
		bq1Waitor.setTextColor(Color.parseColor("#117AFE"));
		bq1Waitor.setTypeface(null, Typeface.BOLD);
		bq1Waitor.setTextSize(19);
		Button bq2Waitor = alert.getButton(DialogInterface.BUTTON2);
		bq2Waitor.setTextColor(Color.parseColor("#117AFE"));
		// bq2.setTypeface(null,Typeface.BOLD);
		bq2Waitor.setTextSize(19);
	}

	@SuppressWarnings("deprecation")
	protected void waitorPopup(final EditText input, final AlertDialog alert) {
		// TODO Auto-generated method stub
		alert.setTitle("Call For Service");
		alert.setMessage("Require assistance from the waiter?");
		input.setHint("How could we help?");
		alert.setView(input, 10, 0, 10, 0);
		alert.setButton2("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//
			}
		});
		alert.setButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//
			}
		});
		alert.show();
		final int alertTitle = alert.getContext()
				.getResources()
				.getIdentifier("alertTitle", "id", "android");
		TextView titleView = (TextView) alert
				.findViewById(alertTitle);
		titleView.setGravity(Gravity.CENTER);
		titleView.setTypeface(null, Typeface.BOLD);
		titleView.setTextSize(19);
		titleView.setTextColor(getResources().getColor(
				android.R.color.black));
		int divierId = alert.getContext().getResources()
				.getIdentifier("android:id/titleDivider", null, null);
		View divider = alert.findViewById(divierId);
		divider.setBackgroundColor(getResources().getColor(
				android.R.color.transparent));
		TextView messageViewBill = (TextView) alert
				.findViewById(android.R.id.message);
		messageViewBill.setGravity(Gravity.CENTER);
		// messageView.setHeight(140);
		messageViewBill.setTextSize(17);

		Button bq1Waitor = alert.getButton(DialogInterface.BUTTON1);
		bq1Waitor.setTextColor(Color.parseColor("#117AFE"));
		bq1Waitor.setTypeface(null, Typeface.BOLD);
		bq1Waitor.setTextSize(19);
		Button bq2Waitor = alert.getButton(DialogInterface.BUTTON2);
		bq2Waitor.setTextColor(Color.parseColor("#117AFE"));
		// bq2.setTypeface(null,Typeface.BOLD);
		bq2Waitor.setTextSize(19);
	}

	@SuppressWarnings("deprecation")
	private void setUpTableCodePopupBill(final EditText input,
			final AlertDialog alert, final AlertDialog alert2) {
		alert.setMessage("Please enter your table ID located on the BigSpoon table stand");
		alert.setView(input, 10, 0, 10, 0);

		alert.setButton2("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//
			}
		});
		alert.setButton("Okay", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				billPopup(alert2);
			}
		});
		alert.show();
		TextView messageViewBill = (TextView) alert
				.findViewById(android.R.id.message);
		messageViewBill.setGravity(Gravity.CENTER);
		// messageView.setHeight(140);
		messageViewBill.setTextSize(17);

		Button bq1Bill = alert.getButton(DialogInterface.BUTTON1);
		bq1Bill.setTextColor(Color.parseColor("#117AFE"));
		bq1Bill.setTypeface(null, Typeface.BOLD);
		bq1Bill.setTextSize(19);
		Button bq2Bill = alert.getButton(DialogInterface.BUTTON2);
		bq2Bill.setTextColor(Color.parseColor("#117AFE"));
		// bq2.setTypeface(null,Typeface.BOLD);
		bq2Bill.setTextSize(19);
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
							public void onClick(
									@SuppressWarnings("unused") final DialogInterface dialog,
									@SuppressWarnings("unused") final int id) {
								startActivity(new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog,
							@SuppressWarnings("unused") final int id) {
						dialog.cancel();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
	}

}
