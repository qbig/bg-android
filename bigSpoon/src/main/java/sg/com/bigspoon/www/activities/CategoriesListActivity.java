package sg.com.bigspoon.www.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.crashlytics.android.Crashlytics;
import com.github.johnpersano.supertoasts.SuperToast;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.adapters.CategoriesAdapter;
import sg.com.bigspoon.www.data.OutletDetailsModel;
import sg.com.bigspoon.www.data.User;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static sg.com.bigspoon.www.data.Constants.LIST_OUTLETS;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_AUTHTOKEN;
import static sg.com.bigspoon.www.data.Constants.OUTLET_ICON;
import static sg.com.bigspoon.www.data.Constants.OUTLET_ID;
import static sg.com.bigspoon.www.data.Constants.OUTLET_NAME;
import static sg.com.bigspoon.www.data.Constants.POS_FOR_CLICKED_CATEGORY;
import static sg.com.bigspoon.www.data.Constants.PREFS_NAME;
import static sg.com.bigspoon.www.data.Constants.SHOULD_SHOW_STEPS_REMINDER;
import static sg.com.bigspoon.www.data.Constants.TABLE_ID;
import static sg.com.bigspoon.www.data.Constants.getURL;

public class CategoriesListActivity extends Activity implements AdapterView.OnItemClickListener {
	private SharedPreferences loginPreferences;
	private static final String ION_LOGGING_CATEGORY_LIST = "ion-category-list";
	private static final int MAX_TRY_COUNT = 6;
	ListView catrgoriesList;
	private ActionBar mActionBar;
	private View mActionBarView;
	private boolean shouldShowSteps = false;
	private int loadCount = 0;
	private Handler handler;
	private ProgressBar mProgressBar;
	private String mOutletIcon;
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
		final User user = User.getInstance(this);
		user.verifyLoginToken();

        if (user.shouldShowRemidnerPopup) {
            startActivity(new Intent(this, ImageDialogAfterSent.class));
			user.shouldShowRemidnerPopup = false;
        } else if (shouldShowSteps) {
			startActivity(new Intent(this, ImageDialogSteps.class));
			shouldShowSteps = false;
		}
		try {
			if (user.prevOrderTime != -1){
				long currentTime = System.currentTimeMillis();
				if (currentTime - user.prevOrderTime > user.currentOutlet.clearPastOrdersInterval * 1000){
					user.clearPastOrder();
					user.prevOrderTime = -1;
				}
			}
			if (user.prevActiveTime != -1){
				long currentTime = System.currentTimeMillis();
				if (currentTime - user.prevActiveTime > 60 * 1000){
					user.currentSession.clearCurrentOrder();
					user.prevActiveTime= -1;
				}
			}
		} catch (NullPointerException npe) {
			Crashlytics.log(npe.toString());
		}

    }


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handler = new Handler();
		setContentView(R.layout.activity_categories_list);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		mProgressBar.setVisibility(View.VISIBLE);
		loginPreferences = getSharedPreferences(PREFS_NAME, 0);
		catrgoriesList = (ListView) findViewById(R.id.category_list);
		catrgoriesList.setOnItemClickListener(this);
		Ion.getDefault(this).configure().setLogging(ION_LOGGING_CATEGORY_LIST, Log.DEBUG);
		final Intent intent = getIntent();
		final int outletId = intent.getIntExtra(OUTLET_ID, loginPreferences.getInt(OUTLET_ID, -1));
		shouldShowSteps = intent.getBooleanExtra(SHOULD_SHOW_STEPS_REMINDER, false);
		mOutletIcon = loginPreferences.getString(OUTLET_ICON, null);
		final User user = User.getInstance(this);
		if (outletId == -1 || mOutletIcon == null) {
			final Intent intentBackToOutlets = new Intent(this, OutletListActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intentBackToOutlets);
		} else {
			if (user.currentOutlet != null && user.currentOutlet.outletID == outletId){
				CategoriesAdapter categoriesAdapter = new CategoriesAdapter(CategoriesListActivity.this,
						user.currentOutlet, mOutletIcon);
				catrgoriesList.setAdapter(categoriesAdapter);
				mProgressBar.setVisibility(View.GONE);
				try {
					final TextView title = (TextView) mActionBarView.findViewById(R.id.title);
					title.setText(user.currentOutlet.name);
				} catch (NullPointerException ne) {
					Crashlytics.logException(ne);
				}
			}
			loadOutletDetails(outletId, mOutletIcon);
		}


	}

	private void loadOutletDetails(final int outletId, final String outletIcon) {
		Ion.with(this).load(getURL(LIST_OUTLETS) + "/" + outletId)
				.setHeader("Authorization", "Token " + loginPreferences.getString(LOGIN_INFO_AUTHTOKEN, "xxx"))
				.setHeader("Content-Type", "application/json; charset=utf-8").asJsonObject()
				.setCallback(new FutureCallback<JsonObject>() {
					@Override
					public void onCompleted(Exception e, JsonObject result) {
						loadCount++;
						final User user = User.getInstance(getApplicationContext());
						user.logRemote("loading categories", e, result);
						if (result != null) {
							final OutletDetailsModel outletDetails = OutletDetailsModel
									.getInstanceFromJsonObject(result);
							outletDetails.sortOrder();
							if (user.currentSession == null){
								user.startSession(outletDetails.name);
							}
							user.currentSession.getCurrentOrder(outletDetails.name);
							if (user.currentOutlet == null || user.currentOutlet.outletID != outletId) {
								CategoriesAdapter categoriesAdapter = new CategoriesAdapter(CategoriesListActivity.this,
										outletDetails, mOutletIcon);
								catrgoriesList.setAdapter(categoriesAdapter);
							}

							user.currentOutlet = outletDetails;

							// Test
							Manager manager;
							final String TAG = "HelloWorld";
							try {
								manager = new Manager(new AndroidContext(CategoriesListActivity.this), Manager.DEFAULT_OPTIONS);
								com.couchbase.lite.util.Log.d(TAG, "Manager created");
								final String dbname = "hello";
								Database database = manager.getDatabase(dbname);
								Map<String, Object> docContent = new HashMap<String, Object>();
								docContent.put("outletDetails", outletDetails);
								Document document = database.createDocument();
								document.putProperties(docContent);

								String docID = document.getId();
								Document retrievedDocument = database.getDocument(docID);
								System.out.println(retrievedDocument.toString());
							} catch (IOException e1) {
								com.couchbase.lite.util.Log.e(TAG, "Cannot create manager object");
								return;
							} catch (CouchbaseLiteException e2) {
								com.couchbase.lite.util.Log.e(TAG, "Cannot get database");
								return;
							}

							user.currentOutlet = OutletDetailsModel
									.getInstanceFromJsonObject(outletDetails.toJsonObject());
							result = outletDetails.toJsonObject();
							Gson gson = new Gson();
							System.out.println(gson.toJsonTree(result).toString());

							result = gson.fromJson(gson.toJsonTree(result).toString(), JsonObject.class);
							// Test -- End

							mProgressBar.setVisibility(View.GONE);

							try {
								final TextView title = (TextView) mActionBarView.findViewById(R.id.title);
								title.setText(user.currentOutlet.name);
							} catch (NullPointerException ne) {
								Crashlytics.logException(ne);
							}

						} else if (user.currentOutlet == null) {
							if (loadCount < MAX_TRY_COUNT){
								handler.postDelayed(new Runnable() {
									@Override
									public void run() {
										loadOutletDetails(outletId, outletIcon);
									}
								}, 1000);
							} else {
								loadCount = 0;
								if (user.wifiIsConnected()){
									user.mMixpanel.track("Categories loading failed, WIFI Connected", null);
								} else {
									user.mMixpanel.track("Categories loading failed, WIFI Disconnected", null);
								}
								mProgressBar.setVisibility(View.GONE);
								SuperToast.create(getApplicationContext(), "Sorry:( Please order directly from the counter.", SuperToast.Duration.EXTRA_LONG).show();
								finish();
							}
						}
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		setupActionButton();
		setupBackToOutletListButton();
		setupHistoryButton();

		return super.onCreateOptionsMenu(menu);
	}

	private void setupActionButton() {
		mActionBar = getActionBar();
		mActionBar.setDisplayShowHomeEnabled(false);

		mActionBarView = getLayoutInflater().inflate(R.layout.action_bar, null);
		mActionBar.setCustomView(mActionBarView);
		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

		final ImageButton toggleButton = (ImageButton) mActionBarView.findViewById(R.id.toggleButton);
		toggleButton.setVisibility(View.GONE);
	}

	private void setupHistoryButton() {
		ImageButton historyButton = (ImageButton) mActionBarView.findViewById(R.id.order_history);
		historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearConfigPopup();
//                Intent intent = new Intent(getApplicationContext(), OrderHistoryListActivity.class);
//                intent.putExtra("callingActivityName", "CategoriesListActivity");
//                startActivity(intent);
            }
        });
	}

    @SuppressWarnings("deprecation")
    private void clearConfigPopup() {
        final AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setTitle("Staff?");
        final EditText input = new EditText(this);
        alert.setMessage(
                "Pls key in password to reset.");
        alert.setView(input, 10, 0, 10, 0);
        alert.setButton2("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String tableCode = input.getText().toString();
                if (tableCode != null && tableCode.equals("1106")){
                    //clear
                    final SharedPreferences.Editor loginEditor = loginPreferences.edit();
                    User.getInstance(CategoriesListActivity.this).tableId = -1;
                    loginEditor.remove(TABLE_ID);
					loginEditor.remove(OUTLET_ID);
					loginEditor.remove(OUTLET_NAME);
                    loginEditor.commit();
                    Toast.makeText(CategoriesListActivity.this, "Success", Toast.LENGTH_LONG);
                } else {
                    Toast.makeText(CategoriesListActivity.this, "Sry, wrong password", Toast.LENGTH_LONG);
                }

            }
        });
        alert.setButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {}
        });
        alert.show();
        TextView messageView = (TextView) alert.findViewById(android.R.id.message);
        messageView.setGravity(Gravity.CENTER);
        // messageView.setHeight(140);
        messageView.setTextSize(17);
        //TODO refactor naming ...
        Button bq1 = alert.getButton(DialogInterface.BUTTON1);
        bq1.setTextColor(Color.parseColor("#117AFE"));
        bq1.setTypeface(null, Typeface.BOLD);
        bq1.setTextSize(19);
        Button bq2 = alert.getButton(DialogInterface.BUTTON2);
        bq2.setTextColor(Color.parseColor("#117AFE"));
        bq2.setTextSize(19);

    }

	private void setupBackToOutletListButton() {
		final ImageButton homeBackButton = (ImageButton) mActionBarView.findViewById(R.id.btn_back);
        homeBackButton.setVisibility(View.INVISIBLE);
//		homeBackButton.setImageResource(R.drawable.home_with_arrow);
//		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
//				RelativeLayout.LayoutParams.WRAP_CONTENT);
//		params.addRule(RelativeLayout.CENTER_VERTICAL);
//		homeBackButton.setLayoutParams(params);
//		homeBackButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
//		homeBackButton.setPadding(22, 0, 0, 0);
//
//		final StateListDrawable states = new StateListDrawable();
//		states.addState(new int[] { android.R.attr.state_pressed },
//				getResources().getDrawable(R.drawable.home_with_arrow_pressed));
//		states.addState(new int[] {}, getResources().getDrawable(R.drawable.home_with_arrow));
//		homeBackButton.setImageDrawable(states);
//
//		homeBackButton.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				Intent intent = new Intent(getApplicationContext(), OutletListActivity.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				startActivity(intent);
//			}
//		});
	}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(getApplicationContext(), OutletListActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		super.onBackPressed();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		Intent i = new Intent(getApplicationContext(), MenuActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		if (position == 0) {
			i.putExtra(POS_FOR_CLICKED_CATEGORY, position);
		} else {
			i.putExtra(POS_FOR_CLICKED_CATEGORY, position - 1);
		}

		startActivity(i);
	}
}
