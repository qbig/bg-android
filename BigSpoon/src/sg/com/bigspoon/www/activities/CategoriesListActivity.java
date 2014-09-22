package sg.com.bigspoon.www.activities;

import static sg.com.bigspoon.www.data.Constants.BASE_URL;
import static sg.com.bigspoon.www.data.Constants.LIST_OUTLETS;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_AUTHTOKEN;
import static sg.com.bigspoon.www.data.Constants.OUTLET_ICON;
import static sg.com.bigspoon.www.data.Constants.OUTLET_ID;
import static sg.com.bigspoon.www.data.Constants.POS_FOR_CLICKED_CATEGORY;
import static sg.com.bigspoon.www.data.Constants.PREFS_NAME;

import org.json.JSONException;
import org.json.JSONObject;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.adapters.CategoriesAdapter;
import sg.com.bigspoon.www.data.Constants;
import sg.com.bigspoon.www.data.Order;
import sg.com.bigspoon.www.data.OutletDetailsModel;
import sg.com.bigspoon.www.data.User;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class CategoriesListActivity extends Activity implements AdapterView.OnItemClickListener {
	private SharedPreferences loginPreferences;
	private static final String ION_LOGGING_CATEGORY_LIST = "ion-category-list";
	ListView catrgoriesList;
	private ActionBar mActionBar;
	private View mActionBarView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_categories_list);
		final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
		progressBar.setVisibility(View.VISIBLE);
		loginPreferences = getSharedPreferences(PREFS_NAME, 0);
		catrgoriesList = (ListView) findViewById(R.id.category_list);
		Ion.getDefault(this).configure().setLogging(ION_LOGGING_CATEGORY_LIST, Log.DEBUG);
		final Intent intent = getIntent();
		final int outletId = intent.getIntExtra(OUTLET_ID, loginPreferences.getInt(OUTLET_ID, -1));
		final String outletIcon = loginPreferences.getString(OUTLET_ICON, null);
		if (outletId == -1 || outletIcon == null) {
			final Intent intentBackToOutlets = new Intent(this, OutletListActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intentBackToOutlets);
		} else {

			Ion.with(this).load(LIST_OUTLETS + "/" + outletId)
					.setHeader("Authorization", "Token " + loginPreferences.getString(LOGIN_INFO_AUTHTOKEN, "xxx"))
					.setHeader("Content-Type", "application/json; charset=utf-8").asJsonObject()
					.setCallback(new FutureCallback<JsonObject>() {
						@Override
						public void onCompleted(Exception e, JsonObject result) {
							if (e != null) {
								if (Constants.LOG) {
									Toast.makeText(CategoriesListActivity.this, "Error with category list loading",
											Toast.LENGTH_LONG).show();
								} else {
									final JSONObject info = new JSONObject();
									try {
										info.put("error", e.toString());
									} catch (JSONException e1) {
										e1.printStackTrace();
									}
									User.getInstance(CategoriesListActivity.this).mMixpanel.track(
											"Error with category list loading", info);
								}
								return;
							}
							progressBar.setVisibility(View.GONE);

							final OutletDetailsModel outletDetails = OutletDetailsModel
									.getInstanceFromJsonObject(result);
							final User user = User.getInstance(getApplicationContext());
							
							if (user.currentSession == null){
								user.startSession(outletDetails.name);
							}
							
							user.currentSession.getCurrentOrder(outletDetails.name);
							user.currentOutlet = outletDetails;

							final TextView title = (TextView) mActionBarView.findViewById(R.id.title);
							title.setText(outletDetails.name);

							CategoriesAdapter categoriesAdapter = new CategoriesAdapter(CategoriesListActivity.this,
									outletDetails, outletIcon);
							catrgoriesList.setAdapter(categoriesAdapter);
						}
					});
		}

		catrgoriesList.setOnItemClickListener(this);

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

		final ImageButton togglebutton = (ImageButton) mActionBarView.findViewById(R.id.toggleButton);
		togglebutton.setVisibility(View.GONE);
	}

	private void setupHistoryButton() {
		ImageButton hisotryButton = (ImageButton) mActionBarView.findViewById(R.id.order_history);
		hisotryButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(), OrderHistoryListActivity.class);
				intent.putExtra("callingActivityName", "CategoriesListActivity");
				startActivity(intent);
			}
		});
	}

	private void setupBackToOutletListButton() {
		final ImageButton homeBackButton = (ImageButton) mActionBarView.findViewById(R.id.btn_back);
		homeBackButton.setImageResource(R.drawable.home_with_arrow);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		homeBackButton.setLayoutParams(params);
		homeBackButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
		homeBackButton.setPadding(22, 0, 0, 0);

		final StateListDrawable states = new StateListDrawable();
		states.addState(new int[] { android.R.attr.state_pressed },
				getResources().getDrawable(R.drawable.home_with_arrow_pressed));
		states.addState(new int[] {}, getResources().getDrawable(R.drawable.home_with_arrow));
		homeBackButton.setImageDrawable(states);

		homeBackButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(), OutletListActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
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
		if (position == 0) {
			return;
		}
		Intent i = new Intent(getApplicationContext(), MenuPhotoListActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.putExtra(POS_FOR_CLICKED_CATEGORY, position - 1);
		startActivity(i);
	}
}
