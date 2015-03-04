package sg.com.bigspoon.www.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.adapters.CustomListOfUserReview;
import sg.com.bigspoon.www.data.Constants;
import sg.com.bigspoon.www.data.Order;
import sg.com.bigspoon.www.data.User;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static sg.com.bigspoon.www.data.Constants.FEEDBACK_URL;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_AUTHTOKEN;
import static sg.com.bigspoon.www.data.Constants.PREFS_NAME;
import static sg.com.bigspoon.www.data.Constants.RATING_URL;

public class UserReviewActivity extends Activity {

	ListView list;
	public static float[] ratingsArray;
	private SharedPreferences loginPrefs;

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_review);
		loginPrefs = getSharedPreferences(PREFS_NAME, 0);
		UserReviewActivity.ratingsArray = new float[User.getInstance(this).currentSession.getPastOrder().mItems.size()];

		Button cancel = (Button) findViewById(R.id.addNoteButton);
		Button submit = (Button) findViewById(R.id.sentToKitchenButton);

		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				User.getInstance(UserReviewActivity.this).currentSession.closeCurrentSession();
				User.getInstance(UserReviewActivity.this).tableId = -1;
				Intent i = new Intent(UserReviewActivity.this, OutletListActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				UserReviewActivity.this.finish();
			}
		});

		submit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					performRatingSubmission();
					performFeedbackSubmission();
				} catch (IndexOutOfBoundsException ie) {
					Crashlytics.logException(ie);
				}

				User.getInstance(UserReviewActivity.this).currentSession.closeCurrentSession();
				User.getInstance(UserReviewActivity.this).tableId = -1;
				Intent i = new Intent(UserReviewActivity.this, OutletListActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				UserReviewActivity.this.finish();
			}
		});

		list = (ListView) findViewById(R.id.ratingList);
		CustomListOfUserReview adapter = new CustomListOfUserReview(this,
				User.getInstance(this).currentSession.getPastOrder().mItems);
		list.setAdapter(adapter);

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getApplicationContext(), OrderHistoryDetailsActivity.class);
				startActivity(intent);
			}
		});

	}

	private void performRatingSubmission() {
		float sum = 0;
		Order submittingOrder = User.getInstance(this).currentSession.getPastOrder();
		for (int i = 0, len = UserReviewActivity.ratingsArray.length; i < len; i++) {
			sum += UserReviewActivity.ratingsArray[i];
		}
		if (sum == 0) {
			return;
		} else {
			ArrayList<HashMap<String, String>> ratings = new ArrayList<HashMap<String, String>>();
			for (int i = 0, len = UserReviewActivity.ratingsArray.length; i < len; i++) {
				HashMap<String, String> pair = new HashMap<String, String>();
				pair.put(submittingOrder.mItems.get(i).dish.id + "", UserReviewActivity.ratingsArray[i] + "");
				ratings.add(pair);
			}

			final Gson gson = new Gson();
			JsonObject json = new JsonObject();
			json.add("dishes", gson.toJsonTree(ratings));
			Ion.with(this).load(RATING_URL).setHeader("Content-Type", "application/json; charset=utf-8")
					.setHeader("Authorization", "Token " + loginPrefs.getString(LOGIN_INFO_AUTHTOKEN, ""))
					.setJsonObjectBody(json).asJsonObject().setCallback(new FutureCallback<JsonObject>() {

						@Override
						public void onCompleted(Exception e, JsonObject result) {
							if (e != null) {
								if (Constants.LOG) {
									Toast.makeText(getApplicationContext(), "Error sending ratings", Toast.LENGTH_LONG).show();
								} else {
									final JSONObject info = new JSONObject();
									try {
										info.put("error", e.toString());
									} catch (JSONException e1) {
										Crashlytics.logException(e1);
									}
									User.getInstance(UserReviewActivity.this).mMixpanel.track("Error sending ratings", info);
								}
								
								return;
							}
						}
					});

		}
	}

	private void performFeedbackSubmission() {
		final EditText feedbackTextFiled = (EditText) findViewById(R.id.editText1);
		if (feedbackTextFiled.getText().toString().isEmpty()) {
			return;
		}

		JsonObject json = new JsonObject();
		json.addProperty("outlet", User.getInstance(this).currentOutlet.outletID);
		json.addProperty("feedback", feedbackTextFiled.getText().toString());
		Ion.with(this).load(FEEDBACK_URL).setHeader("Content-Type", "application/json; charset=utf-8")
				.setHeader("Authorization", "Token " + loginPrefs.getString(LOGIN_INFO_AUTHTOKEN, ""))
				.setJsonObjectBody(json).asJsonObject().setCallback(new FutureCallback<JsonObject>() {

					@Override
					public void onCompleted(Exception e, JsonObject result) {
						if (e != null) {
							if (Constants.LOG) {
								Toast.makeText(getApplicationContext(), "Error sending feedback", Toast.LENGTH_LONG).show();
							} else {
								final JSONObject info = new JSONObject();
								try {
									info.put("error", e.toString());
								} catch (JSONException e1) {
									Crashlytics.logException(e1);
								}
								User.getInstance(UserReviewActivity.this).mMixpanel.track("Error sending feedback", info);
							}
							
							return;
						}
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.user_review, menu);
		return true;
	}

}
