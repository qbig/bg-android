package sg.com.bigspoon.www.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.data.Constants;
import sg.com.bigspoon.www.data.User;

import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_AUTHTOKEN;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_AVATAR_URL;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_EMAIL;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_FIRST_NAME;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_LAST_NAME;
import static sg.com.bigspoon.www.data.Constants.MIXPANEL_TOKEN;
import static sg.com.bigspoon.www.data.Constants.PREFS_NAME;
import static sg.com.bigspoon.www.data.Constants.USER_SIGNUP;
import static sg.com.bigspoon.www.data.Constants.getURL;

public class SignUpActivity extends Activity {
	private static String ION_LOGGING_SIGNUP = "ion-email-signup";
	EditText mSignupNameField;
	EditText mSignupEmailField;
	EditText mSignupPasswordField;
	private MixpanelAPI mMixpanel;
	final Handler mHandler = new Handler();
	ImageButton mSignUpConfirmButton;

	private SharedPreferences.Editor loginPrefsEditor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Ion.getDefault(this).configure().setLogging(ION_LOGGING_SIGNUP, Log.DEBUG);
		loginPrefsEditor = getSharedPreferences(PREFS_NAME, 0).edit();
		mMixpanel = MixpanelAPI.getInstance(this, MIXPANEL_TOKEN);
		
		setContentView(R.layout.activity_sign_up);
		mSignupNameField = (EditText) findViewById(R.id.signupFullName);
		mSignupEmailField = (EditText) findViewById(R.id.signupEmail);
		mSignupPasswordField = (EditText) findViewById(R.id.signupPassword);

		addSignupButtonHandler();
	}

	private void addSignupButtonHandler() {
		mSignUpConfirmButton = (ImageButton) findViewById(R.id.signupButton);
		mSignUpConfirmButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isSignUpFieldsValid()) {
					final JsonObject json = new JsonObject();
					json.addProperty("first_name", mSignupNameField.getText().toString());
					json.addProperty("email", mSignupEmailField.getText().toString());
					json.addProperty("password", mSignupPasswordField.getText().toString());

					Ion.with(SignUpActivity.this).load(getURL(USER_SIGNUP))
							.setHeader("Content-Type", "application/json; charset=utf-8").setJsonObjectBody(json)
							.asJsonObject().setCallback(new FutureCallback<JsonObject>() {
						@Override
						public void onCompleted(Exception e, JsonObject result) {
							if (e != null) {
								if (Constants.LOG) {
									Toast.makeText(SignUpActivity.this, "Error signing up with emails",
											Toast.LENGTH_LONG).show();
								} else {
									final JSONObject info = new JSONObject();
									try {
										info.put("error", e.toString());
										info.put("email", mSignupEmailField.getText().toString());
									} catch (JSONException e1) {
										e1.printStackTrace();
									}
									User.getInstance(SignUpActivity.this).mMixpanel.track(
											"Error signing up with emails", info);
								}

								return;
							}
							final String email = result.get(LOGIN_INFO_EMAIL).getAsString();
							if (email.contains("Enter a valid email address.")) {
								new Thread() {
									public void run() {
										SignUpActivity.this.mHandler.post(new Runnable() {
											public void run() {
												Toast.makeText(SignUpActivity.this, "Please enter a valid email address.", Toast.LENGTH_LONG).show();
											}
										});
									}
								}.start();
								return;
							}

							final String lastName = result.get(LOGIN_INFO_LAST_NAME).getAsString();
							final String firstName = result.get(LOGIN_INFO_FIRST_NAME).getAsString();
							final String authToken = result.get(LOGIN_INFO_AUTHTOKEN).getAsString();
							final String avatarUrl = result.get(LOGIN_INFO_AVATAR_URL).getAsString();
							mMixpanel.alias(email, mMixpanel.getDistinctId());
							mMixpanel.getPeople().identify(mMixpanel.getDistinctId());

							loginPrefsEditor.putString(LOGIN_INFO_EMAIL, email);
							loginPrefsEditor.putString(LOGIN_INFO_LAST_NAME, lastName);
							loginPrefsEditor.putString(LOGIN_INFO_FIRST_NAME, firstName);
							loginPrefsEditor.putString(LOGIN_INFO_AUTHTOKEN, authToken);
							loginPrefsEditor.putString(LOGIN_INFO_AVATAR_URL, avatarUrl);
							loginPrefsEditor.commit();

							Intent intent = new Intent(SignUpActivity.this, OutletListActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
							SignUpActivity.this.startActivity(intent);
							SignUpActivity.this.finish();
						}
					});
				} else {
					Toast.makeText(SignUpActivity.this, "Sorry, you need to fill up the forms", Toast.LENGTH_LONG)
							.show();
				}
			}
		});
	}

	private boolean isSignUpFieldsValid() {
		return !mSignupEmailField.getText().toString().isEmpty() && !mSignupNameField.getText().toString().isEmpty()
				&& !mSignupPasswordField.getText().toString().isEmpty();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.sign_up, menu);
		return true;
	}

}
