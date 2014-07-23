package sg.com.bigspoon.www.activities;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.adapters.ActionBarMenuAdapter;
import sg.com.bigspoon.www.adapters.ExpandableAdapter;
import sg.com.bigspoon.www.adapters.PastOrdersAdapter;
import sg.com.bigspoon.www.data.Order;
import sg.com.bigspoon.www.data.User;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.StateListDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ItemsActivity extends ExpandableListActivity {

	ListView list;
	ImageButton imageplus, imageminus;
	Boolean isExpanded = false;
	private GridView gridView;
	private TextView mOrderedDishCounterText;
	private View bottomActionBar;
	public static final int WATER = 1;
	public static final int WAITER = 2;
	public static final int BILL = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_items);

		setupBottomActionBar();

		final ExpandableListView expandableList = getExpandableListView();
		expandableList.setDividerHeight(2);
		expandableList.setGroupIndicator(null);
		expandableList.setClickable(true);

		// This is to unable the function of expanding when clicking the gourp
		// item
		expandableList.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				return true;
			}
		});

		final ExpandableAdapter adapter = new ExpandableAdapter(this);

		adapter.setInflater(
				(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE),
				this);

		expandableList.setAdapter(adapter);
		expandableList.setOnChildClickListener(this);
		expandableList.setChildDivider(getResources()
				.getDrawable(R.color.white));
		expandableList.setDivider(getResources().getDrawable(R.color.white));
		expandableList.setDividerHeight(2);

		Button addNote = (Button) findViewById(R.id.button1);
		addNote.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!isExpanded) {
					for (int i = 0; i < User.getInstance(ItemsActivity.this).currentSession.currentOrder.mItems
							.size(); i++) {
						expandableList.expandGroup(i, true);
						isExpanded = true;
					}
				} else {
					for (int i = 0; i < User.getInstance(ItemsActivity.this).currentSession.currentOrder.mItems
							.size(); i++) {
						expandableList.collapseGroup(i);
						isExpanded = false;
					}
				}
			}
		});

		expandableList.setOnGroupExpandListener(new OnGroupExpandListener() {
			@Override
			public void onGroupExpand(int groupPosition) {
				// TODO Auto-generated method stub
				ExpandableViewUtil.setExpandedListViewHeightBasedOnChildren(
						expandableList, groupPosition);
			}
		});
		expandableList
				.setOnGroupCollapseListener(new OnGroupCollapseListener() {
					@Override
					public void onGroupCollapse(int groupPosition) {
						// TODO Auto-generated method stub
						ExpandableViewUtil
								.setCollapseListViewHeightBasedOnChildren(
										expandableList, groupPosition);
					}
				});

		new ListViewHeightUtil().setListViewHeightBasedOnChildren(
				expandableList, 0);

		// loadMenu();

		Button placeOrder = (Button) findViewById(R.id.button2);
		placeOrder.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				placeOrderAlertDialog();

			}

			@SuppressWarnings("deprecation")
			private void placeOrderAlertDialog() {

				if (User.getInstance(ItemsActivity.this).currentSession.currentOrder.mItems
						.isEmpty()) {
					AlertDialog alertNoOrder = new AlertDialog.Builder(
							ItemsActivity.this).create();
					alertNoOrder.setTitle("Place Order");
					alertNoOrder.setMessage("You haven't selected anything.");
					alertNoOrder.setView(null);
					alertNoOrder.setButton("Okay",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									//
								}
							});
					alertNoOrder.show();
					int divierId = alertNoOrder
							.getContext()
							.getResources()
							.getIdentifier("android:id/titleDivider", null,
									null);
					View divider = alertNoOrder.findViewById(divierId);
					divider.setBackgroundColor(getResources().getColor(
							android.R.color.transparent));
					final int alertTitle = alertNoOrder.getContext()
							.getResources()
							.getIdentifier("alertTitle", "id", "android");
					TextView titleView = (TextView) alertNoOrder
							.findViewById(alertTitle);
					titleView.setGravity(Gravity.CENTER);
					titleView.setTypeface(null, Typeface.BOLD);
					titleView.setTextSize(19);
					titleView.setTextColor(getResources().getColor(
							android.R.color.black));

					TextView messageView = (TextView) alertNoOrder
							.findViewById(android.R.id.message);
					messageView.setGravity(Gravity.CENTER);
					// messageView.setHeight(140);
					messageView.setTextSize(17);

					Button okButton = alertNoOrder
							.getButton(DialogInterface.BUTTON1);
					okButton.setTextColor(Color.parseColor("#117AFE"));
					okButton.setTypeface(null, Typeface.BOLD);
					okButton.setTextSize(19);

				} else {

					LayoutInflater inflater = getLayoutInflater();

					AlertDialog.Builder alertbuilder = new AlertDialog.Builder(
							ItemsActivity.this);

					View dialoglayout = inflater.inflate(
							R.layout.dialog_layout, null);

					LinearLayout layoutholder = (LinearLayout) dialoglayout
							.findViewById(R.id.dialog_layout_root);

					TextView textTitle = new TextView(getBaseContext());
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.WRAP_CONTENT,
							LinearLayout.LayoutParams.WRAP_CONTENT);
					params.gravity = Gravity.CENTER;
					final float scale = getBaseContext().getResources()
							.getDisplayMetrics().density;
					int padding_10dp = (int) (10 * scale + 0.5f);
					int padding_15dp = (int) (15 * scale + 0.5f);
					int padding_25dp = (int) (25 * scale + 0.5f);
					int padding_35dp = (int) (35 * scale + 0.5f);

					params.setMargins(0, 0, 0, padding_10dp);
					textTitle.setLayoutParams(params);
					textTitle.setText("New Order");
					textTitle.setTextSize(19);
					textTitle.setTextColor(getResources().getColor(
							android.R.color.black));
					textTitle.setTypeface(null, Typeface.BOLD);
					layoutholder.addView(textTitle);

					for (int i = 0; i < User.getInstance(ItemsActivity.this).currentSession.currentOrder.mItems
							.size(); i++) {
						FrameLayout childLayout = new FrameLayout(
								getBaseContext());
						TextView textNumber = new TextView(getBaseContext());
						// textNumber.setId(R.id.textNumber);
						FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(
								FrameLayout.LayoutParams.WRAP_CONTENT,
								FrameLayout.LayoutParams.WRAP_CONTENT);
						params2.setMargins(padding_10dp, 0, 0, 0);
						textNumber.setLayoutParams(params2);
						textNumber.setText(Integer.toString(User
								.getInstance(ItemsActivity.this).currentSession.currentOrder
								.getQuantityOfDishByIndex(i)));
						textNumber.setTextColor(getResources().getColor(
								android.R.color.black));
						childLayout.addView(textNumber);

						TextView xMark = new TextView(getBaseContext());
						// textNumber.setId(R.id.textNumber);
						FrameLayout.LayoutParams params3 = new FrameLayout.LayoutParams(
								FrameLayout.LayoutParams.WRAP_CONTENT,
								FrameLayout.LayoutParams.WRAP_CONTENT);
						params3.setMargins(padding_25dp, 0, 0, 0);
						xMark.setLayoutParams(params3);
						xMark.setText("x");
						xMark.setTextColor(getResources().getColor(
								android.R.color.black));
						childLayout.addView(xMark);

						TextView itemName = new TextView(getBaseContext());
						// textNumber.setId(R.id.textNumber);
						FrameLayout.LayoutParams params4 = new FrameLayout.LayoutParams(
								FrameLayout.LayoutParams.WRAP_CONTENT,
								FrameLayout.LayoutParams.WRAP_CONTENT);
						params4.setMargins(padding_35dp, 0, padding_15dp, 0);
						params4.gravity = Gravity.RIGHT;
						itemName.setLayoutParams(params4);
						itemName.setText(User.getInstance(ItemsActivity.this).currentSession.currentOrder.mItems
								.get(i).dish.name);
						itemName.setTextColor(getResources().getColor(
								android.R.color.black));
						itemName.setTextSize(12);
						childLayout.addView(itemName);

						LinearLayout.LayoutParams parentParams = new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.MATCH_PARENT,
								LinearLayout.LayoutParams.MATCH_PARENT);
						layoutholder.addView(childLayout, parentParams);
					}

					alertbuilder.setView(layoutholder);

					alertbuilder.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									//
								}
							});
					alertbuilder.setPositiveButton("Okay",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									/*
									 * ThreadSafeSingleton.getInstance().itemnameOld
									 * .
									 * addAll(ThreadSafeSingleton.getInstance().
									 * itemname);
									 * ThreadSafeSingleton.getInstance
									 * ().numberOld
									 * .addAll(ThreadSafeSingleton.getInstance
									 * ().number);
									 * ThreadSafeSingleton.getInstance
									 * ().priceOld
									 * .addAll(ThreadSafeSingleton.getInstance
									 * ().price);
									 * ThreadSafeSingleton.getInstance
									 * ().initialize();
									 */
									User.getInstance(ItemsActivity.this).currentSession.pastOrder.mergeWithAnotherOrder(User
											.getInstance(ItemsActivity.this).currentSession.currentOrder);
									User.getInstance(ItemsActivity.this).currentSession.currentOrder = new Order();
									Toast.makeText(
											getApplicationContext(),
											"Your order has been sent. Our food is prepared with love, thank you for being patient.",
											Toast.LENGTH_LONG).show();
									Intent i = new Intent(getBaseContext(),
											ItemsActivity.class);
									startActivity(i);
									finish();
								}
							});

					AlertDialog alertDialog = alertbuilder.create();
					alertDialog.show();

					Button okButtom = alertDialog
							.getButton(DialogInterface.BUTTON1);
					okButtom.setTextColor(Color.parseColor("#117AFE"));
					okButtom.setTextSize(16);
					okButtom.setTypeface(null, Typeface.BOLD);
					Button cancelButton = alertDialog
							.getButton(DialogInterface.BUTTON2);
					cancelButton.setTextColor(Color.parseColor("#117AFE"));
					cancelButton.setTextSize(16);
					cancelButton.setTypeface(null, Typeface.BOLD);
				}
			}

		});

		ListView listOfOrderPlaced = (ListView) findViewById(R.id.listOfOrderPlaced);
		PastOrdersAdapter adapterForPlaced = new PastOrdersAdapter(this,
				User.getInstance(this).currentSession.pastOrder.mItems);
		listOfOrderPlaced.setAdapter(adapterForPlaced);
		new ListViewHeightUtil().setListViewHeightBasedOnChildren(
				listOfOrderPlaced, 0);

	}

	private void setupBottomActionBar() {
		bottomActionBar = findViewById(R.id.gv_action_menu);
		bottomActionBar.getBackground().setAlpha(230);
		mOrderedDishCounterText = (TextView) findViewById(R.id.corner);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.items, menu);
		// MenuInflater inflater = getMenuInflater();
		// inflater.inflate(R.menu.items, menu);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		// addListenerOnButtonLogout();
		// displaying custom ActionBar
		View mActionBarView = getLayoutInflater().inflate(
				R.layout.action_bar_items_activity, null);
		actionBar.setCustomView(mActionBarView);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ImageButton ibItem1 = (ImageButton) mActionBarView
				.findViewById(R.id.btn_menu);
		ibItem1.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
		ibItem1.setPadding(22, 0, 0, 0);

		StateListDrawable states = new StateListDrawable();
		states.addState(new int[] { android.R.attr.state_pressed },
				getResources().getDrawable(R.drawable.menu_pressed));
		states.addState(new int[] {},
				getResources().getDrawable(R.drawable.menu));
		ibItem1.setImageDrawable(states);

		ibItem1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// Intent intent = new Intent(getApplicationContext(),
				// MenuPhotoListActivity.class);
				// startActivity(intent);
				finish();
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
				intent.putExtra("callingActivityName", "ItemsActivity");
				startActivity(intent);
			}
		});

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onResume() {
		super.onResume();
		loadMenu();
		updateOrderedDishCounter();
	}

	private void updateOrderedDishCounter() {
		final int orderCount = User.getInstance(this).currentSession.currentOrder
				.getTotalQuantity();
		if (orderCount != 0) {
			mOrderedDishCounterText.setVisibility(View.VISIBLE);
			mOrderedDishCounterText.setText(String.valueOf(orderCount));
		} else {
			mOrderedDishCounterText.setVisibility(View.GONE);
		}
	}

	public class ListViewHeightUtil {

		public void setListViewHeightBasedOnChildren(ListView listView,
				int attHeight) {
			ListAdapter listAdapter = listView.getAdapter();
			if (listAdapter == null) {
				// pre-condition
				return;
			}

			int totalHeight = 0;
			for (int i = 0; i < listAdapter.getCount(); i++) {
				View listItem = listAdapter.getView(i, null, listView);
				listItem.measure(0, 0);
				totalHeight += listItem.getMeasuredHeight();
			}

			ViewGroup.LayoutParams params = listView.getLayoutParams();
			params.height = totalHeight
					+ (listView.getDividerHeight() * (listAdapter.getCount() - 1))
					+ attHeight;
			listView.setLayoutParams(params);
		}
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
					if (User.getInstance(ItemsActivity.this).checkLocation()) {
						if (User.getInstance(ItemsActivity.this).isfindTableCode == false) {
							int requestCode = WATER;
							setUpTablePopup(requestCode);
						} else {
							int requestCode = WATER;
							onTablePopupResult(requestCode);
						}
					} else {
						setUpLocationFailPopup();
						User.getInstance(ItemsActivity.this).isfindTableCode = false;
					}
					break;

				case 1:
					if (User.getInstance(ItemsActivity.this).checkLocation()) {
						if (User.getInstance(ItemsActivity.this).isfindTableCode == false) {
							int requestCode = WAITER;
							setUpTablePopup(requestCode);
						} else {
							int requestCode = WAITER;
							onTablePopupResult(requestCode);
						}
					} else {
						setUpLocationFailPopup();
						User.getInstance(ItemsActivity.this).isfindTableCode = false;
					}
					break;
				case 2:
					if (User.getInstance(ItemsActivity.this).checkLocation()) {
						if (User.getInstance(ItemsActivity.this).isfindTableCode == false) {
							int requestCode = BILL;
							setUpTablePopup(requestCode);
						} else {
							int requestCode = BILL;
							onTablePopupResult(requestCode);
						}
					} else {
						setUpLocationFailPopup();
						User.getInstance(ItemsActivity.this).isfindTableCode = false;
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
			Intent j = new Intent(ItemsActivity.this,
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
				User.getInstance(ItemsActivity.this).isfindTableCode = false;
				String tableCode = input.getText().toString();
				for (int k = 0; k < User.getInstance(ItemsActivity.this).currentOutlet.tables.length; k++) {
					if (User.getInstance(ItemsActivity.this).currentOutlet.tables[k].code
							.toLowerCase().equals(tableCode.toLowerCase())) {
						User.getInstance(ItemsActivity.this).isfindTableCode = true;
					}
				}
				if (!User.getInstance(ItemsActivity.this).isfindTableCode) {
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
				User.getInstance(ItemsActivity.this).isfindTableCode = false;
				String tableCode = inputIncorrect.getText().toString();
				for (int k = 0; k < User.getInstance(ItemsActivity.this).currentOutlet.tables.length; k++) {
					if (User.getInstance(ItemsActivity.this).currentOutlet.tables[k].code
							.toLowerCase().equals(tableCode.toLowerCase())) {
						User.getInstance(ItemsActivity.this).isfindTableCode = true;
					}
				}
				if (!User.getInstance(ItemsActivity.this).isfindTableCode) {
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
				ItemsActivity.this).create();
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