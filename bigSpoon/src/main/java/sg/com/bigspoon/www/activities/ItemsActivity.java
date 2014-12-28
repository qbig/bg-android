package sg.com.bigspoon.www.activities;

import static sg.com.bigspoon.www.data.Constants.BILL_URL;
import static sg.com.bigspoon.www.data.Constants.DESSERT_CATEGORY_ID;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_AUTHTOKEN;
import static sg.com.bigspoon.www.data.Constants.MIXPANEL_TOKEN;
import static sg.com.bigspoon.www.data.Constants.NOTIF_ORDER_UPDATE;
import static sg.com.bigspoon.www.data.Constants.ORDER_URL;
import static sg.com.bigspoon.www.data.Constants.PREFS_NAME;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.adapters.ActionBarMenuAdapter;
import sg.com.bigspoon.www.adapters.CurrentOrderExpandableAdapter;
import sg.com.bigspoon.www.adapters.PastOrdersAdapter;
import sg.com.bigspoon.www.data.Constants;
import sg.com.bigspoon.www.data.DiningSession;
import sg.com.bigspoon.www.data.Order;
import sg.com.bigspoon.www.data.OutletDetailsModel;
import sg.com.bigspoon.www.data.User;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ExpandableListActivity;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.StateListDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import android.widget.TimePicker;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

public class ItemsActivity extends ExpandableListActivity {

	private static final String ION_LOGGING_ITEM_ACTIVITY = "ion-item-activity";

	Boolean isExpanded = false;
	private GridView mBottomGridView;
	private TextView orderCounterText;
	private Button mAddNote;
	private ExpandableListView mExpandableList;
	private CurrentOrderExpandableAdapter mCurrentOrderAdapter;
	private Button mPlaceOrder;
	private ActionBar mActionBar;
	private View mActionBarView;
	private ImageButton mBackButton;
	private ImageButton historyButton;
	private ListView mPastOrderList;
	private PastOrdersAdapter mPastOrderAdapter;
	private SharedPreferences loginPreferences;
	public static final int WATER = 1;
	public static final int WAITER = 2;
	public static final int BILL = 3;
	public static final int PLACE_ORDER = 4;
	public static final int TAKE_AWAY = 5;
	public static final int MODIFIER_TEXT_WIDTH = 600;
	static EditText textTime;
	private MixpanelAPI mMixpanel;
	
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("receiver", "Got broadcast " + NOTIF_ORDER_UPDATE);
			ItemsActivity.this.updateDisplay();
		}
	};

	private TextView mCurrentSubTotalValue;

	private TextView mCurrentServiceChargeLabel;

	private TextView mCurrentServiceChargeValue;

	private TextView mCurrentGSTLabel;

	private TextView mCurrentTotalValue;

	private TextView mOrderredSubTotalValue;

	private TextView mOrderredServiceChargeLabel;

	private TextView mOrderredServiceChargeValue;

	private TextView mOrderredGSTLabel;

	private TextView mOrderredTotalValue;

	private TextView mCurrentGSTValue;

	private TextView mOrderredGSTValue;

	private OutletDetailsModel mCurrentOutlet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Ion.getDefault(this).configure().setLogging(ION_LOGGING_ITEM_ACTIVITY, Log.DEBUG);
		loginPreferences = getSharedPreferences(PREFS_NAME, 0);
		mCurrentOutlet = User.getInstance(this).currentOutlet;
		mMixpanel = MixpanelAPI.getInstance(ItemsActivity.this, MIXPANEL_TOKEN);
		
		setContentView(R.layout.activity_items);
		orderCounterText = (TextView) findViewById(R.id.corner);
		updateOrderedDishCounter();
		setupExpandableCurrentOrdersListView();
		setupAddNoteButton();
		loadMenu();
		setupPlaceOrderButton();
		setupPlacedOrderListView();
		setupPriceLabels();
		
		LocalBroadcastManager.getInstance(this)
				.registerReceiver(mMessageReceiver, new IntentFilter(NOTIF_ORDER_UPDATE));
		new ListViewHeightUtil().setListViewHeightBasedOnChildren(mExpandableList, 0);
		new ListViewHeightUtil().setListViewHeightBasedOnChildren(mPastOrderList, 0);

	}

	private void setupPriceLabels() {
		
		final String serviceChargeLabelString = "Service Charge(" + (int) (mCurrentOutlet.serviceChargeRate * 100) + "%)";
		final String GSTChargeLabelString = "GST (" + (int) (mCurrentOutlet.gstRate * 100) + "%) :";
		
		mCurrentSubTotalValue = (TextView) findViewById(R.id.textView2);
		mCurrentServiceChargeLabel = (TextView) findViewById(R.id.textView3);
		mCurrentServiceChargeValue = (TextView) findViewById(R.id.textView4);
		mCurrentGSTLabel = (TextView) findViewById(R.id.textView5);
		mCurrentGSTValue = (TextView) findViewById(R.id.textView6);
		mCurrentTotalValue = (TextView) findViewById(R.id.textView8);
		
		mCurrentServiceChargeLabel.setText(serviceChargeLabelString);
		mCurrentGSTLabel.setText(GSTChargeLabelString);
		
		mOrderredSubTotalValue = (TextView) findViewById(R.id.textView10);
		mOrderredServiceChargeLabel = (TextView) findViewById(R.id.textView11);
		mOrderredServiceChargeValue = (TextView) findViewById(R.id.textView12);
		mOrderredGSTLabel = (TextView) findViewById(R.id.textView13);
		mOrderredGSTValue = (TextView) findViewById(R.id.textView14);
		mOrderredTotalValue = (TextView) findViewById(R.id.textView16);
		
		mOrderredGSTLabel.setText(GSTChargeLabelString);
		mOrderredServiceChargeLabel.setText(serviceChargeLabelString);
	}
	
	private void updatePriceLabels() {
		final DiningSession session = User.getInstance(this).currentSession;
		
		mCurrentSubTotalValue.setText("$" + String.format("%.2f", session.getCurrentOrder().getTotalPrice()));
		mCurrentServiceChargeValue.setText("$" + String.format("%.2f", session.getCurrentOrder().getTotalPrice() * mCurrentOutlet.serviceChargeRate));
		mCurrentGSTValue.setText(String.format("$" + "%.2f", session.getCurrentOrder().getTotalPrice() * mCurrentOutlet.gstRate));
		mCurrentTotalValue.setText(String.format("$" + "%.2f", session.getCurrentOrder().getTotalPrice() + session.getCurrentOrder().getTotalPrice() * mCurrentOutlet.serviceChargeRate + session.getCurrentOrder().getTotalPrice() * mCurrentOutlet.gstRate));
		
		mOrderredSubTotalValue.setText("$" + String.format("%.2f", session.getPastOrder().getTotalPrice()));
		mOrderredServiceChargeValue.setText("$" + String.format("%.2f", session.getPastOrder().getTotalPrice() * mCurrentOutlet.serviceChargeRate));
		mOrderredGSTValue.setText("$" + String.format("%.2f", session.getPastOrder().getTotalPrice() * mCurrentOutlet.gstRate));
		mOrderredTotalValue.setText("$" + String.format("%.2f",  session.getPastOrder().getTotalPrice() + session.getPastOrder().getTotalPrice() * mCurrentOutlet.serviceChargeRate + session.getPastOrder().getTotalPrice() * mCurrentOutlet.gstRate));
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (isExpanded) {
			toggleAddNoteState();
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(
				mMessageReceiver);
	}

	protected void updateDisplay() {
		updateOrderedDishCounter();
		updatePriceLabels();
		mCurrentOrderAdapter.notifyDataSetChanged();
		mPastOrderAdapter.notifyDataSetChanged();
		new ListViewHeightUtil().setListViewHeightBasedOnChildren(mExpandableList, 0);
		new ListViewHeightUtil().setListViewHeightBasedOnChildren(mPastOrderList, 0);		
	}

	private void setupPlacedOrderListView() {
		mPastOrderList = (ListView) findViewById(R.id.listOfOrderPlaced);
		mPastOrderAdapter = new PastOrdersAdapter(this, User.getInstance(this).currentSession.getPastOrder().mItems);
		mPastOrderList.setAdapter(mPastOrderAdapter);
	}

	private void setupPlaceOrderButton() {
		mPlaceOrder = (Button) findViewById(R.id.button2);
		mPlaceOrder.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (isExpanded) {
					toggleAddNoteState();
				}

				if (User.getInstance(ItemsActivity.this).currentSession.getCurrentOrder().mItems.isEmpty()) {
					showNoOrderPopup();
				} else {
					if (User.getInstance(ItemsActivity.this).checkLocation()) {
						checkIfContainDessert();
						if (User.getInstance(ItemsActivity.this).tableId == -1) {
							int requestCode = PLACE_ORDER;
							setUpTablePopup(requestCode);
						} else {
							if (User.getInstance(ItemsActivity.this).isForTakeAway) {
								int requestCode = TAKE_AWAY;
								onTablePopupResult(requestCode);
							} else {
								int requestCode = PLACE_ORDER;
								onTablePopupResult(requestCode);
							}
						}
					} else {
						setUpLocationFailPopup();
						User.getInstance(ItemsActivity.this).tableId = -1;
					}
				}
			}
		});
	}

	protected void checkIfContainDessert() {
		for (int i = 0; i < User.getInstance(ItemsActivity.this).currentSession.getCurrentOrder().mItems.size(); i++) {
			if (User.getInstance(ItemsActivity.this).currentSession.getCurrentOrder().mItems.get(i).dish.categories[0].id == DESSERT_CATEGORY_ID) {
				User.getInstance(ItemsActivity.this).isContainDessert = true;
			}
		}

	}

	private void performSendOrderRequest() {

		final Order currentOrder = User.getInstance(this).currentSession.getCurrentOrder();
		Ion.with(this).load(ORDER_URL).setHeader("Content-Type", "application/json; charset=utf-8")
				.setHeader("Authorization", "Token " + loginPreferences.getString(LOGIN_INFO_AUTHTOKEN, ""))
				.setJsonObjectBody(currentOrder.getJsonOrders(User.getInstance(ItemsActivity.this).tableId))
				.asJsonObject().setCallback(new FutureCallback<JsonObject>() {

					@Override
					public void onCompleted(Exception e, JsonObject result) {
						if (e != null) {
							if (Constants.LOG) {
								Toast.makeText(ItemsActivity.this, "Error sending orders", Toast.LENGTH_LONG).show();
							} else {
								final JSONObject info = new JSONObject();
								try {
									info.put("error", e.toString());
								} catch (JSONException e1) {
									Crashlytics.logException(e1);
								}
								User.getInstance(ItemsActivity.this).mMixpanel.track("Error sending orders",
										info);
							}
							
							return;
						}
						Toast.makeText(ItemsActivity.this, "Sent :)", Toast.LENGTH_LONG).show();
					}
				});
	}

	private void showOrderDetailsPopup() {
		LayoutInflater inflater = getLayoutInflater();

		AlertDialog.Builder alertbuilder = new AlertDialog.Builder(ItemsActivity.this);

		View dialoglayout = inflater.inflate(R.layout.dialog_layout, null);

		LinearLayout layoutholder = (LinearLayout) dialoglayout.findViewById(R.id.dialog_layout_root);

		TextView textTitle = new TextView(getBaseContext());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		final float scale = getBaseContext().getResources().getDisplayMetrics().density;
		int padding_10dp = (int) (10 * scale + 0.5f);
		int padding_15dp = (int) (15 * scale + 0.5f);
		int padding_25dp = (int) (25 * scale + 0.5f);
		int padding_35dp = (int) (35 * scale + 0.5f);

		params.setMargins(0, 0, 0, padding_10dp);
		textTitle.setLayoutParams(params);
		textTitle.setText("New Order");
		textTitle.setTextSize(19);
		textTitle.setTextColor(getResources().getColor(android.R.color.black));
		textTitle.setTypeface(null, Typeface.BOLD);
		layoutholder.addView(textTitle);

		for (int i = 0; i < User.getInstance(ItemsActivity.this).currentSession.getCurrentOrder().mItems.size(); i++) {
			final FrameLayout childLayout = new FrameLayout(getBaseContext());
			final TextView textNumber = new TextView(getBaseContext());
			final FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			params2.setMargins(padding_10dp, 0, 0, 0);
			textNumber.setLayoutParams(params2);
			textNumber.setText(Integer.toString(User.getInstance(ItemsActivity.this).currentSession.getCurrentOrder()
					.getQuantityOfDishByIndex(i)));
			textNumber.setTextColor(getResources().getColor(android.R.color.black));
			childLayout.addView(textNumber);

			TextView xMark = new TextView(getBaseContext());
			FrameLayout.LayoutParams params3 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
					FrameLayout.LayoutParams.WRAP_CONTENT);
			params3.setMargins(padding_25dp, 0, 0, 0);
			xMark.setLayoutParams(params3);
			xMark.setText("x");
			xMark.setTextColor(getResources().getColor(android.R.color.black));
			childLayout.addView(xMark);

			TextView itemName = new TextView(getBaseContext());
			FrameLayout.LayoutParams params4 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
					FrameLayout.LayoutParams.WRAP_CONTENT);
			params4.setMargins(padding_35dp, 0, padding_15dp, 0);
			params4.gravity = Gravity.RIGHT;
			itemName.setLayoutParams(params4);
			itemName.setText(User.getInstance(ItemsActivity.this).currentSession.getCurrentOrder().mItems.get(i).dish.name);
			itemName.setTextColor(getResources().getColor(android.R.color.black));
			itemName.setTextSize(12);
			childLayout.addView(itemName);

			LinearLayout.LayoutParams parentParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
			layoutholder.addView(childLayout, parentParams);
		}

		alertbuilder.setView(layoutholder);

		alertbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});
		alertbuilder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				performSendOrderRequest();
				User.getInstance(ItemsActivity.this).currentSession.getPastOrder().mergeWithAnotherOrder(User
						.getInstance(ItemsActivity.this).currentSession.getCurrentOrder());
				User.getInstance(ItemsActivity.this).currentSession.clearCurrentOrder();
				User.getInstance(ItemsActivity.this).isContainDessert = false;
				Toast.makeText(getApplicationContext(),
						"Your order has been sent. Our food is prepared with love, thank you for being patient.",
						Toast.LENGTH_LONG).show();
				updateDisplay();
			}
		});

		AlertDialog alertDialog = alertbuilder.create();
		alertDialog.show();

		Button okButtom = alertDialog.getButton(DialogInterface.BUTTON1);
		okButtom.setTextColor(Color.parseColor("#117AFE"));
		okButtom.setTextSize(16);
		okButtom.setTypeface(null, Typeface.BOLD);
		Button cancelButton = alertDialog.getButton(DialogInterface.BUTTON2);
		cancelButton.setTextColor(Color.parseColor("#117AFE"));
		cancelButton.setTextSize(16);
		cancelButton.setTypeface(null, Typeface.BOLD);
	}

	@SuppressWarnings("deprecation")
	private void showNoOrderPopup() {
		final AlertDialog alertNoOrder = new AlertDialog.Builder(ItemsActivity.this).create();
		alertNoOrder.setTitle("Place Order");
		alertNoOrder.setMessage("You haven't selected anything.");
		alertNoOrder.setView(null);
		alertNoOrder.setButton("Okay", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int whichButton) {
				//
			}
		});
		alertNoOrder.show();
		int dividerId = alertNoOrder.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
		View divider = alertNoOrder.findViewById(dividerId);
		divider.setBackgroundColor(getResources().getColor(android.R.color.transparent));
		final int alertTitle = alertNoOrder.getContext().getResources().getIdentifier("alertTitle", "id", "android");
		TextView titleView = (TextView) alertNoOrder.findViewById(alertTitle);
		titleView.setGravity(Gravity.CENTER);
		titleView.setTypeface(null, Typeface.BOLD);
		titleView.setTextSize(19);
		titleView.setTextColor(getResources().getColor(android.R.color.black));

		TextView messageView = (TextView) alertNoOrder.findViewById(android.R.id.message);
		messageView.setGravity(Gravity.CENTER);
		messageView.setTextSize(17);

		Button okButton = alertNoOrder.getButton(DialogInterface.BUTTON1);
		okButton.setTextColor(Color.parseColor("#117AFE"));
		okButton.setTypeface(null, Typeface.BOLD);
		okButton.setTextSize(19);
	}

	private void setupExpandableCurrentOrdersListView() {
		mExpandableList = getExpandableListView();
		mExpandableList.setDividerHeight(2);
		mExpandableList.setGroupIndicator(null);
		mExpandableList.setClickable(true);
		mExpandableList.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				return true;
			}
		});

		mCurrentOrderAdapter = new CurrentOrderExpandableAdapter(this);

		mCurrentOrderAdapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);

		mExpandableList.setAdapter(mCurrentOrderAdapter);
		mExpandableList.setOnChildClickListener(this);
		mExpandableList.setChildDivider(getResources().getDrawable(R.color.white));
		mExpandableList.setDivider(getResources().getDrawable(R.color.white));
		mExpandableList.setDividerHeight(2);

		mExpandableList.setOnGroupExpandListener(new OnGroupExpandListener() {
			@Override
			public void onGroupExpand(int groupPosition) {
				ExpandableViewUtil.setExpandedListViewHeightBasedOnChildren(mExpandableList, groupPosition);
			}
		});
		mExpandableList.setOnGroupCollapseListener(new OnGroupCollapseListener() {
			@Override
			public void onGroupCollapse(int groupPosition) {
				ExpandableViewUtil.setCollapseListViewHeightBasedOnChildren(mExpandableList, groupPosition);
			}
		});
	}

	private void toggleAddNoteState() {
		if (!isExpanded) {
			for (int i = 0; i < User.getInstance(ItemsActivity.this).currentSession.getCurrentOrder().mItems.size(); i++) {
				mExpandableList.expandGroup(i, true);
				isExpanded = true;
			}
		} else {
			for (int i = 0; i < User.getInstance(ItemsActivity.this).currentSession.getCurrentOrder().mItems.size(); i++) {
				mExpandableList.collapseGroup(i);
				isExpanded = false;
			}
		}
	}

	private void setupAddNoteButton() {
		mAddNote = (Button) findViewById(R.id.button1);
		mAddNote.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				toggleAddNoteState();
			}
		});
	}

	private void updateOrderedDishCounter() {
		if (User.getInstance(this).currentSession.getCurrentOrder().getTotalQuantity() != 0) {
			orderCounterText.setVisibility(View.VISIBLE);
			orderCounterText.setText(User.getInstance(this).currentSession.getCurrentOrder().getTotalQuantity() + "");
		} else {
			orderCounterText.setVisibility(View.GONE);
		}
		
		final Animation a = AnimationUtils.loadAnimation(this, R.anim.scale_up);
		orderCounterText.startAnimation(a);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		mActionBar = getActionBar();
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBarView = getLayoutInflater().inflate(R.layout.action_bar_items_activity, null);
		mActionBar.setCustomView(mActionBarView);
		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		
		final TextView title = (TextView) mActionBarView.findViewById(R.id.title);
		title.setText(User.getInstance(this).currentOutlet.name);

		mBackButton = (ImageButton) mActionBarView.findViewById(R.id.btn_menu);
		mBackButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
		mBackButton.setPadding(22, 0, 0, 0);
		final StateListDrawable states = new StateListDrawable();
		states.addState(new int[] { android.R.attr.state_pressed }, getResources().getDrawable(R.drawable.menu_pressed));
		states.addState(new int[] {}, getResources().getDrawable(R.drawable.menu));
		mBackButton.setImageDrawable(states);
		mBackButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});

		historyButton = (ImageButton) mActionBarView.findViewById(R.id.order_history);
		historyButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(), OrderHistoryListActivity.class);
				intent.putExtra("callingActivityName", "ItemsActivity");
				startActivity(intent);
			}
		});

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateDisplay();
	}

	public class ListViewHeightUtil {

		public void setListViewHeightBasedOnChildren(ListView listView, int attHeight) {
			ListAdapter listAdapter = listView.getAdapter();
			if (listAdapter == null) {
				return;
			}

			int totalHeight = 0;
			for (int i = 0; i < listAdapter.getCount(); i++) {
				View listItem = listAdapter.getView(i, null, listView);
				listItem.measure(
						MeasureSpec.makeMeasureSpec(MODIFIER_TEXT_WIDTH, MeasureSpec.EXACTLY),
		                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
				totalHeight += listItem.getMeasuredHeight();
			}

			ViewGroup.LayoutParams params = listView.getLayoutParams();
			params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + attHeight;
			listView.setLayoutParams(params);
			listView.requestLayout();
		}
	}

	protected void loadMenu() {

		final Context ctx = getApplicationContext();

		final EditText input = new EditText(this);

		mBottomGridView = (GridView) findViewById(R.id.gv_action_menu);

		ActionBarMenuAdapter actionBarMenuAdapter = new ActionBarMenuAdapter(this, 3);
		mBottomGridView.setAdapter(actionBarMenuAdapter);
		mBottomGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
				Intent i = null;
				switch (position) {
//				case 0:
//					if (User.getInstance(ItemsActivity.this).checkLocation()) {
//						if (User.getInstance(ItemsActivity.this).tableId == -1) {
//							int requestCode = WATER;
//							setUpTablePopup(requestCode);
//						} else {
//							int requestCode = WATER;
//							onTablePopupResult(requestCode);
//						}
//					} else {
//						setUpLocationFailPopup();
//						User.getInstance(ItemsActivity.this).tableId = -1;
//					}
//					break;
				case 0:
					if (User.getInstance(ItemsActivity.this).checkLocation()) {
						if (User.getInstance(ItemsActivity.this).tableId == -1) {
							int requestCode = WAITER;
							setUpTablePopup(requestCode);
						} else {
							int requestCode = WAITER;
							onTablePopupResult(requestCode);
						}
					} else {
						setUpLocationFailPopup();
						User.getInstance(ItemsActivity.this).tableId = -1;
					}
					break;
				case 1:
					if (User.getInstance(ItemsActivity.this).checkLocation()) {
						if (User.getInstance(ItemsActivity.this).tableId == -1) {
							int requestCode = BILL;
							setUpTablePopup(requestCode);
						} else {
							int requestCode = BILL;
							onTablePopupResult(requestCode);
						}
					} else {
						setUpLocationFailPopup();
						User.getInstance(ItemsActivity.this).tableId = -1;
					}
					break;
				case 2:
					// i = new Intent(ctx, ItemsActivity.class);
					// i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					// startActivity(i);
					break;
				}
			}
		});
	}

	protected void onTablePopupResult(int requestCode) {
		switch (requestCode) {
		case WATER:
			if (User.getInstance(this).currentOutlet.isWaterEnabled) {
				Intent j = new Intent(ItemsActivity.this, WaterServiceActivity.class);
				j.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(j);
			} else {
				AlertDialog alertLocationFail = new AlertDialog.Builder(ItemsActivity.this).create();
				alertLocationFail.setMessage(User.getInstance(this).currentOutlet.waterText);
				alertLocationFail.setView(null);
				alertLocationFail.setButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				});
				alertLocationFail.show();
			}
			
			break;
		case WAITER:
			waitorPopup();
			break;
		case BILL:
			billPopup();
			break;
		case PLACE_ORDER:
			if (User.getInstance(ItemsActivity.this).isContainDessert) {
				showServeDessertPopup();
			} else
				showOrderDetailsPopup();
			break;
		case TAKE_AWAY:
			takeAwayPopup();
			break;
		}
	}

	@SuppressWarnings("deprecation")
	private void takeAwayPopup() {
		final AlertDialog alertTakeAway = new AlertDialog.Builder(this).create();
		alertTakeAway.setTitle("Pick a time :)");
		alertTakeAway.setMessage("and leave your phone number");
		LinearLayout textInputLayoutHolder = new LinearLayout(this);
		textInputLayoutHolder.setOrientation(LinearLayout.VERTICAL);
		textInputLayoutHolder.setPadding(30, 0, 30, 30);
		textTime = new EditText(this);
		textTime.setHint("Time to pick up");
		textInputLayoutHolder.addView(textTime);
		textTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showTimePickerDialog();
			}
		});

		final EditText textPhoneNumber = new EditText(this);
		textPhoneNumber.setHint("Your phone number");
		textPhoneNumber.setInputType(InputType.TYPE_CLASS_PHONE);
		textInputLayoutHolder.addView(textPhoneNumber);
		alertTakeAway.setView(textInputLayoutHolder);

		alertTakeAway.setButton2("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//
			}
		});
		alertTakeAway.setButton("Okay", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				User.getInstance(ItemsActivity.this).currentSession.getCurrentOrder().mGeneralNote = "Takeaway: "
						+ textTime.getText() + ", " + "phone: " + textPhoneNumber.getText();
				if (textTime.getText().toString() == null || textTime.getText().toString().equals("")
						|| textPhoneNumber.getText().toString().equals("")
						|| textPhoneNumber.getText().toString() == null
						|| textPhoneNumber.getText().toString().length() != 8) {
					takeAwayPopup();
				} else {
					showOrderDetailsPopup();
				}
			}
		});
		alertTakeAway.show();
		final int alertTitle = alertTakeAway.getContext().getResources().getIdentifier("alertTitle", "id", "android");
		TextView titleView = (TextView) alertTakeAway.findViewById(alertTitle);
		titleView.setGravity(Gravity.CENTER);
		titleView.setTypeface(null, Typeface.BOLD);
		titleView.setTextSize(19);
		titleView.setTextColor(getResources().getColor(android.R.color.black));
		int divierId = alertTakeAway.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
		View divider = alertTakeAway.findViewById(divierId);
		divider.setBackgroundColor(getResources().getColor(android.R.color.transparent));
		TextView messageViewBill = (TextView) alertTakeAway.findViewById(android.R.id.message);
		messageViewBill.setGravity(Gravity.CENTER);
		// messageView.setHeight(140);
		messageViewBill.setTextSize(17);

		Button bq1 = alertTakeAway.getButton(DialogInterface.BUTTON1);
		bq1.setTextColor(Color.parseColor("#117AFE"));
		bq1.setTypeface(null, Typeface.BOLD);
		bq1.setTextSize(19);
		Button bq2 = alertTakeAway.getButton(DialogInterface.BUTTON2);
		bq2.setTextColor(Color.parseColor("#117AFE"));
		// bq2.setTypeface(null,Typeface.BOLD);
		bq2.setTextSize(19);

	}

	protected void showTimePickerDialog() {
		DialogFragment newFragment = new TimePickerFragment();
		newFragment.show(getFragmentManager(), "timePicker");
	}

	public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			String currentDate = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
			String minuteDisplay;
			if (minute < 10)
				minuteDisplay = "0" + Integer.toString(minute);
			else
				minuteDisplay = Integer.toString(minute);
			textTime.setText(currentDate + " " + hourOfDay + ":" + minuteDisplay);
		}
	}

	@SuppressWarnings("deprecation")
	private void showServeDessertPopup() {
		final AlertDialog alert = new AlertDialog.Builder(this).create();
		alert.setMessage("Would you like your dessert to be served now?");
		alert.setView(null);

		alert.setButton2("Now", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				User.getInstance(ItemsActivity.this).currentSession.getCurrentOrder().mGeneralNote = "Serve dessert now";
				showOrderDetailsPopup();
			}
		});
		alert.setButton("Serve later", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				User.getInstance(ItemsActivity.this).currentSession.getCurrentOrder().mGeneralNote = "Serve dessert later";
				showOrderDetailsPopup();
			}
		});
		alert.show();
		TextView messageView = (TextView) alert.findViewById(android.R.id.message);
		messageView.setGravity(Gravity.CENTER);
		// messageView.setHeight(140);
		messageView.setTextSize(17);

		Button bq1 = alert.getButton(DialogInterface.BUTTON1);
		bq1.setTextColor(Color.parseColor("#117AFE"));
		bq1.setTypeface(null, Typeface.BOLD);
		bq1.setTextSize(19);
		Button bq2 = alert.getButton(DialogInterface.BUTTON2);
		bq2.setTextColor(Color.parseColor("#117AFE"));
		// bq2.setTypeface(null, Typeface.BOLD);
		bq2.setTextSize(19);

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
				if (User.getInstance(ItemsActivity.this).currentSession.getPastOrder().getTotalQuantity() != 0){
					performBillRequest();
					Intent i = new Intent(ItemsActivity.this, UserReviewActivity.class);
					startActivity(i);
					if (! User.getInstance(ItemsActivity.this).currentOutlet.isBillEnabled) {
						Toast.makeText(ItemsActivity.this, User.getInstance(ItemsActivity.this).currentOutlet.billText, Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(ItemsActivity.this, "You haven't ordered anything yet :)", Toast.LENGTH_LONG).show();
				}
			}
		});
		alert2.show();
		TextView messageView2 = (TextView) alert2.findViewById(android.R.id.message);
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
				User.getInstance(ItemsActivity.this).requestForWaiter(inputWaitor.getText().toString());
			}
		});
		alertWaitor.show();
		final int alertTitle = alertWaitor.getContext().getResources().getIdentifier("alertTitle", "id", "android");
		TextView titleView = (TextView) alertWaitor.findViewById(alertTitle);
		titleView.setGravity(Gravity.CENTER);
		titleView.setTypeface(null, Typeface.BOLD);
		titleView.setTextSize(19);
		titleView.setTextColor(getResources().getColor(android.R.color.black));
		int divierId = alertWaitor.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
		View divider = alertWaitor.findViewById(divierId);
		divider.setBackgroundColor(getResources().getColor(android.R.color.transparent));
		TextView messageViewBill = (TextView) alertWaitor.findViewById(android.R.id.message);
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
				for (int k = 0; k < User.getInstance(ItemsActivity.this).currentOutlet.tables.length; k++) {
					if (User.getInstance(ItemsActivity.this).currentOutlet.tables[k].code.toLowerCase().equals(
							tableCode.toLowerCase())) {
						User.getInstance(ItemsActivity.this).tableId = User.getInstance(ItemsActivity.this).currentOutlet.tables[k].id;
						User.getInstance(ItemsActivity.this).isForTakeAway = User.getInstance(ItemsActivity.this).currentOutlet.tables[k].isForTakeAway;
						mMixpanel.getPeople().setOnce("Type", "Restaurant");
						mMixpanel.getPeople().increment("Orders Placed", 1);
					}
				}
				if (User.getInstance(ItemsActivity.this).tableId == -1) {
					incorrectTableCodePopup(requestCode);
				} else {
					if (User.getInstance(ItemsActivity.this).isForTakeAway) {
						int requestCodeTake = TAKE_AWAY;
						onTablePopupResult(requestCodeTake);
					} else {
						onTablePopupResult(requestCode);
					}
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
		
		TextView messageView = (TextView) alert.findViewById(android.R.id.message);
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
		final AlertDialog alertIncorrect = new AlertDialog.Builder(this).create();
		alertIncorrect
				.setMessage("Table ID incorrect. Please enter your table ID or ask your friendly waiter for assistance");
		alertIncorrect.setView(inputIncorrect, 10, 0, 10, 0);

		alertIncorrect.setButton2("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//
			}
		});
		alertIncorrect.setButton("Okay", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String tableCode = inputIncorrect.getText().toString();
				for (int k = 0; k < User.getInstance(ItemsActivity.this).currentOutlet.tables.length; k++) {
					if (User.getInstance(ItemsActivity.this).currentOutlet.tables[k].code.toLowerCase().equals(
							tableCode.toLowerCase())) {
						User.getInstance(ItemsActivity.this).tableId = User.getInstance(ItemsActivity.this).currentOutlet.tables[k].id;
						User.getInstance(ItemsActivity.this).isForTakeAway = User.getInstance(ItemsActivity.this).currentOutlet.tables[k].isForTakeAway;
					}
				}
				if (User.getInstance(ItemsActivity.this).tableId == -1) {
					incorrectTableCodePopup(requestCode);
				} else {
					onTablePopupResult(requestCode);
				}
			}
		});
		alertIncorrect.show();
		TextView messageView = (TextView) alertIncorrect.findViewById(android.R.id.message);
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
		AlertDialog alertLocationFail = new AlertDialog.Builder(ItemsActivity.this).create();
		alertLocationFail.setTitle("BigSpoon couldn't find you");
		alertLocationFail.setMessage("Orders can only be sent when you are at the restaurant." + "\n\n"
				+ "If you are already there, kindly speak to the friendly waitor for your orders.");
		alertLocationFail.setView(null);
		alertLocationFail.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					buildAlertMessageNoGps();
				}
			}
		});
		alertLocationFail.show();
		int divierId = alertLocationFail.getContext().getResources()
				.getIdentifier("android:id/titleDivider", null, null);
		View divider = alertLocationFail.findViewById(divierId);
		divider.setBackgroundColor(getResources().getColor(android.R.color.transparent));
		final int alertTitle = alertLocationFail.getContext().getResources()
				.getIdentifier("alertTitle", "id", "android");
		TextView titleView = (TextView) alertLocationFail.findViewById(alertTitle);
		titleView.setGravity(Gravity.CENTER);
		titleView.setTypeface(null, Typeface.BOLD);
		titleView.setTextSize(19);
		titleView.setTextColor(getResources().getColor(android.R.color.black));

		TextView messageView = (TextView) alertLocationFail.findViewById(android.R.id.message);
		messageView.setGravity(Gravity.CENTER);
		messageView.setPadding(20, 0, 20, 25);
		messageView.setTextSize(17);
	}

	private void buildAlertMessageNoGps() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Your GPS seems to be disabled, do you want to enable it?").setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, final int id) {
						startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
					}
				}).setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, final int id) {
						dialog.cancel();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	private void performBillRequest() {

		Ion.with(this).load(BILL_URL).setHeader("Content-Type", "application/json; charset=utf-8")
				.setHeader("Authorization", "Token " + loginPreferences.getString(LOGIN_INFO_AUTHTOKEN, ""))
				.setJsonObjectBody(User.getInstance(ItemsActivity.this).getTableId()).asJsonObject()
				.setCallback(new FutureCallback<JsonObject>() {

					@Override
					public void onCompleted(Exception e, JsonObject result) {
						if (e != null) {
							if (Constants.LOG) {
								Toast.makeText(ItemsActivity.this, "Error requesting bills", Toast.LENGTH_LONG).show();
							} else {
								final JSONObject info = new JSONObject();
								try {
									info.put("error", e.toString());
								} catch (JSONException e1) {
									Crashlytics.logException(e1);
								}
								User.getInstance(ItemsActivity.this).mMixpanel.track("Error requesting bills",
										info);
							}
							
							return;
						}
						Toast.makeText(ItemsActivity.this, "Request for bill is submitted, the waiter will be right with you.", Toast.LENGTH_LONG).show();
					}
				});
	}
}