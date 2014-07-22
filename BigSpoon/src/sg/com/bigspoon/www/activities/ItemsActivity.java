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
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.util.Log;
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

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_AUTHTOKEN;
import static sg.com.bigspoon.www.data.Constants.ORDER_URL;
import static sg.com.bigspoon.www.data.Constants.PREFS_NAME;

public class ItemsActivity extends ExpandableListActivity {

	private static final String ION_LOGGING_ITEM_ACTIVITY = "ion-item-activity";
		
	Boolean isExpanded = false;
	private GridView mBottomGridView;
	private TextView orderCounterText;
	private Button mAddNote;
	private ExpandableListView mExpandableList;
	private Button mPlaceOrder;
	private ActionBar mActionBar;
	private View mActionBarView;
	private ImageButton mBackButton;
	private ImageButton historyButton;
	private ListView mListOfPlacedOrder;
	private ExpandableAdapter mCurrentOrderAdapter;
	private PastOrdersAdapter mPastOrderAdapter;
	private SharedPreferences loginPreferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Ion.getDefault(this).configure().setLogging(ION_LOGGING_ITEM_ACTIVITY, Log.DEBUG);
		loginPreferences = getSharedPreferences(PREFS_NAME,0);
		
		setContentView(R.layout.activity_items);
		orderCounterText = (TextView) findViewById(R.id.corner);
		updateOrderedDishCounter();
		setupExpandableCurrentOrdersListView();
		setupAddNoteButton();
		loadMenu();
		setupPlaceOrderButton();
		setupPlacedOrderListView();
		
		new ListViewHeightUtil().setListViewHeightBasedOnChildren(mExpandableList, 0);
		new ListViewHeightUtil().setListViewHeightBasedOnChildren(mListOfPlacedOrder, 0);

	}
	
	protected void updateDisplay() {
		updateOrderedDishCounter();
		mCurrentOrderAdapter.notifyDataSetChanged();
		mPastOrderAdapter.notifyDataSetChanged();
		new ListViewHeightUtil().setListViewHeightBasedOnChildren(mExpandableList, 0);
		new ListViewHeightUtil().setListViewHeightBasedOnChildren(mListOfPlacedOrder, 0);
	}
	
	private void setupPlacedOrderListView() {
		mListOfPlacedOrder = (ListView) findViewById(R.id.listOfOrderPlaced);
		mPastOrderAdapter = new PastOrdersAdapter(this,
				User.getInstance(this).currentSession.pastOrder.mItems);
		mListOfPlacedOrder.setAdapter(mPastOrderAdapter);
	}

	private void setupPlaceOrderButton() {
		mPlaceOrder = (Button) findViewById(R.id.button2);
		mPlaceOrder.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (User.getInstance(ItemsActivity.this).currentSession.currentOrder.mItems.isEmpty()) {
					showNoOrderPopup();
				} else {
					showOrderDetailsPopup();
				}
			}
		});
	}
	
	private void performSendOrderRequest() {

		//NSMutableArray *dishesArray = [[NSMutableArray alloc] init];
	    // For every dish that is currently in the order, we add it to the dishes dictionary:
		//======= 1 ==========
//	    for (int i = 0; i < [self.userInfo.currentOrder.dishes count]; i++) {
//	        Dish *dish = [self.userInfo.currentOrder.dishes objectAtIndex:i];
//	        NSNumber * quantity = [NSNumber numberWithInt:[self.userInfo.currentOrder getQuantityOfDishByDish: dish]];
//	        NSString * ID = [NSString stringWithFormat:@"%d", dish.ID];
//	        
//	        NSDictionary *newPair = [NSDictionary dictionaryWithObject:quantity forKey:ID];
//	        [dishesArray addObject:newPair];
//	    }
//	    
//	    NSMutableDictionary *parameters = [[NSMutableDictionary alloc] init];
//	    [parameters setObject:[NSArray arrayWithArray: dishesArray] forKey:@"dishes"];
		

		//======= 2 ==========
//	    [parameters setObject:[NSNumber numberWithInt: [User sharedInstance].tableID] forKey:@"table"];
//	    if (generalNote != nil && ![generalNote isEqualToString:@""] ) {
//	        [parameters setObject:generalNote forKey:@"note"];
//	    }
//	    
		//======= 3 ==========
//	    if ((self.userInfo.currentOrder.notes != nil && [self.userInfo.currentOrder.notes count] > 0) || [self.userInfo.currentOrder.modifierAnswers count] > 0) {
//	        [parameters setObject:[self.userInfo.currentOrder getMergedTextForNotesAndModifier] forKey:@"notes"];
//	    }
//	    
		//======= 4 ==========
//	    if (self.userInfo.currentOrder.modifierAnswers != nil && [self.userInfo.currentOrder.modifierAnswers count] != 0){
//	        [parameters setObject:self.userInfo.currentOrder.modifierAnswers forKey:@"modifiers"];
//	    }
		final Order currentOrder = User.getInstance(this).currentSession.currentOrder;  
		
		Ion.with(this)
		.load(ORDER_URL)
		.setHeader("Content-Type", "application/json; charset=utf-8")
		.setHeader("Authorization", "Token " + loginPreferences.getString(LOGIN_INFO_AUTHTOKEN, ""))
		.setJsonObjectBody(currentOrder.getJsonOrders())
		.asJsonObject()
		.setCallback(new FutureCallback<JsonObject>() {
			
			@Override
			public void onCompleted(Exception e, JsonObject result) {
				if (e != null) {
                    Toast.makeText(ItemsActivity.this, "Error sending orders", Toast.LENGTH_LONG).show();
                    return;
                }
				Toast.makeText(ItemsActivity.this, "Success", Toast.LENGTH_LONG).show();
			}
		});
	}
	
	private void showOrderDetailsPopup() {
		LayoutInflater inflater = getLayoutInflater();

		AlertDialog.Builder alertbuilder = new AlertDialog.Builder(ItemsActivity.this);

		View dialoglayout = inflater.inflate(R.layout.dialog_layout, null);

		LinearLayout layoutholder = (LinearLayout) dialoglayout.findViewById(R.id.dialog_layout_root);

		TextView textTitle = new TextView(getBaseContext());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
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

		for (int i = 0; i < User.getInstance(ItemsActivity.this).currentSession.currentOrder.mItems.size(); i++) {
			final FrameLayout childLayout = new FrameLayout(getBaseContext());
			final TextView textNumber = new TextView(getBaseContext());
			final FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			params2.setMargins(padding_10dp, 0, 0, 0);
			textNumber.setLayoutParams(params2);
			textNumber.setText(Integer.toString(User.getInstance(ItemsActivity.this).currentSession.currentOrder
					.getQuantityOfDishByIndex(i)));
			textNumber.setTextColor(getResources().getColor(android.R.color.black));
			childLayout.addView(textNumber);

			TextView xMark = new TextView(getBaseContext());
			FrameLayout.LayoutParams params3 = new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			params3.setMargins(padding_25dp, 0, 0, 0);
			xMark.setLayoutParams(params3);
			xMark.setText("x");
			xMark.setTextColor(getResources().getColor(android.R.color.black));
			childLayout.addView(xMark);

			TextView itemName = new TextView(getBaseContext());
			FrameLayout.LayoutParams params4 = new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			params4.setMargins(padding_35dp, 0, padding_15dp, 0);
			params4.gravity = Gravity.RIGHT;
			itemName.setLayoutParams(params4);
			itemName.setText(User.getInstance(ItemsActivity.this).currentSession.currentOrder.mItems.get(i).dish.name);
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
				User.getInstance(ItemsActivity.this).currentSession.pastOrder.mergeWithAnotherOrder(User
						.getInstance(ItemsActivity.this).currentSession.currentOrder);
				User.getInstance(ItemsActivity.this).currentSession.currentOrder = new Order();
				Toast.makeText(
						getApplicationContext(),
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
		int dividerId = alertNoOrder.getContext().getResources()
				.getIdentifier("android:id/titleDivider", null, null);
		View divider = alertNoOrder.findViewById(dividerId);
		divider.setBackgroundColor(getResources().getColor(android.R.color.transparent));
		final int alertTitle = alertNoOrder.getContext().getResources()
				.getIdentifier("alertTitle", "id", "android");
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

		mCurrentOrderAdapter = new ExpandableAdapter(this);

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

	private void setupAddNoteButton() {
		mAddNote = (Button) findViewById(R.id.button1);
		mAddNote.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!isExpanded) {
					for (int i = 0; i < User.getInstance(ItemsActivity.this).currentSession.currentOrder.mItems.size(); i++) {
						mExpandableList.expandGroup(i, true);
						isExpanded = true;
					}
				} else {
					for (int i = 0; i < User.getInstance(ItemsActivity.this).currentSession.currentOrder.mItems.size(); i++) {
						mExpandableList.collapseGroup(i);
						isExpanded = false;
					}
				}
			}
		});
	}

	private void updateOrderedDishCounter() {
		if (User.getInstance(this).currentSession.currentOrder.getTotalQuantity() != 0) {
			orderCounterText.setVisibility(View.VISIBLE);
			orderCounterText.setText(User.getInstance(this).currentSession.currentOrder.getTotalQuantity() + "");
		} else {
			orderCounterText.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		mActionBar = getActionBar();
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBarView = getLayoutInflater().inflate(R.layout.action_bar_items_activity, null);
		mActionBar.setCustomView(mActionBarView);
		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		
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
				startActivity(intent);
			}
		});

		return super.onCreateOptionsMenu(menu);
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
				listItem.measure(0, 0);
				totalHeight += listItem.getMeasuredHeight();
			}

			ViewGroup.LayoutParams params = listView.getLayoutParams();
			params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + attHeight;
			listView.setLayoutParams(params);
		}
	}

	protected void loadMenu() {

		final Context ctx = getApplicationContext();
		final EditText input = new EditText(this);

		final AlertDialog tableCodePopup = new AlertDialog.Builder(this).create();
		final AlertDialog askForBillPopup = new AlertDialog.Builder(this).create();
		mBottomGridView = (GridView) findViewById(R.id.gv_action_menu);

		ActionBarMenuAdapter actionBarMenuAdapter = new ActionBarMenuAdapter(this, 4);
		mBottomGridView.setAdapter(actionBarMenuAdapter);
		mBottomGridView.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
				Intent i = null;
				switch (position) {
				case 0:
					i = new Intent(ctx, WaterServiceActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
					break;
				case 1:
					tableCodePopup.setMessage("Please enter your table ID located on the BigSpoon table stand");
					tableCodePopup.setView(input, 10, 0, 10, 0);
					tableCodePopup.setButton2("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							//
						}
					});
					tableCodePopup.setButton("Okay", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							//
						}
					});
					tableCodePopup.show();
					TextView messageView = (TextView) tableCodePopup.findViewById(android.R.id.message);
					messageView.setGravity(Gravity.CENTER);
					messageView.setTextSize(17);

					Button bq1 = tableCodePopup.getButton(DialogInterface.BUTTON1);
					bq1.setTextColor(Color.parseColor("#117AFE"));
					bq1.setTypeface(null, Typeface.BOLD);
					bq1.setTextSize(19);
					Button bq2 = tableCodePopup.getButton(DialogInterface.BUTTON2);
					bq2.setTextColor(Color.parseColor("#117AFE"));
					bq2.setTextSize(19);
					break;
				case 2:
					askForBillPopup.setMessage("Would you like your bill?");
					askForBillPopup.setView(null);

					askForBillPopup.setButton2("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {

						}
					});
					askForBillPopup.setButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {

						}
					});
					askForBillPopup.show();
					TextView messageView2 = (TextView) askForBillPopup.findViewById(android.R.id.message);
					messageView2.setGravity(Gravity.CENTER);
					messageView2.setTextSize(17);

					Button bq3 = askForBillPopup.getButton(DialogInterface.BUTTON1);
					bq3.setTextColor(Color.parseColor("#117AFE"));
					bq3.setTypeface(null, Typeface.BOLD);
					bq3.setTextSize(19);
					Button bq4 = askForBillPopup.getButton(DialogInterface.BUTTON2);
					bq4.setTextColor(Color.parseColor("#117AFE"));
					bq4.setTextSize(19);
					break;
				case 3:
					break;
				}
			}
		});
	}
}