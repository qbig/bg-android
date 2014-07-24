package sg.com.bigspoon.www.activities;

import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_AUTHTOKEN;
import static sg.com.bigspoon.www.data.Constants.PREFS_NAME;
import static sg.com.bigspoon.www.data.Constants.RATING_URL;
import static sg.com.bigspoon.www.data.Constants.FEEDBACK_URL;
import java.util.ArrayList;
import java.util.HashMap;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.adapters.CustomListOfUserReview;
import sg.com.bigspoon.www.data.Order;
import sg.com.bigspoon.www.data.User;
import android.app.Activity;
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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class UserReviewActivity extends Activity {

	ListView list;
	public static float[] ratingsArray;
	private SharedPreferences loginPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_review);
		loginPrefs = getSharedPreferences(PREFS_NAME, 0);
		UserReviewActivity.ratingsArray = new float[User.getInstance(this).currentSession.pastOrder.mItems.size()];

		Button cancel = (Button) findViewById(R.id.button1);
		Button submit = (Button) findViewById(R.id.button2);

		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				User.getInstance(UserReviewActivity.this).currentSession.closeCurrentSession();
				User.getInstance(UserReviewActivity.this).tableId = -1;
				finish();
			}
		});

		submit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				performRatingSubmission();
				performFeedbackSubmission();
				User.getInstance(UserReviewActivity.this).currentSession.closeCurrentSession();
				User.getInstance(UserReviewActivity.this).tableId = -1;
			}
		});

		list = (ListView) findViewById(R.id.ratingList);
		CustomListOfUserReview adapter = new CustomListOfUserReview(this,
				User.getInstance(this).currentSession.pastOrder.mItems);
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
		Order submittingOrder = User.getInstance(this).currentSession.pastOrder;
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
								//Toast.makeText(getApplicationContext(), "Error sending ratings", Toast.LENGTH_LONG).show();
								return;
							}
							Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
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
							Toast.makeText(getApplicationContext(), "Error sending feedback", Toast.LENGTH_LONG).show();
							return;
						}
						Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.user_review, menu);
		return true;
	}

}
