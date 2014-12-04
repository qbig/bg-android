package sg.com.bigspoon.www.activities;

import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_AUTHTOKEN;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_AVATAR_URL;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_EMAIL;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_FIRST_NAME;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_LAST_NAME;
import static sg.com.bigspoon.www.data.Constants.MIXPANEL_TOKEN;
import static sg.com.bigspoon.www.data.Constants.PREFS_NAME;
import static sg.com.bigspoon.www.data.Constants.USER_LOGIN;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.data.BigSpoon;
import sg.com.bigspoon.www.data.Constants;
import sg.com.bigspoon.www.data.SocketIOManager;
import sg.com.bigspoon.www.data.User;
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

public class EmailLoginActivity extends Activity {
	private static String ION_LOGGING_LOGIN = "ion-email-login";
	private static String EMAIL_LOGIN_COUNT = "email-login-count";
	EditText mLoginEmailField;
	EditText mLoginPasswordField;

	ImageButton mLoginConfirmButton;

	private SharedPreferences.Editor loginPrefsEditor;
	private Runnable mShowToastTask;
	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		Ion.getDefault(this).configure().setLogging(ION_LOGGING_LOGIN, Log.DEBUG);
		loginPrefsEditor = getSharedPreferences(PREFS_NAME, 0).edit();

		mLoginEmailField = (EditText) findViewById(R.id.loginEmail);
		mLoginPasswordField = (EditText) findViewById(R.id.loginPassword);
		addListenerOnButtonLogin();
		mHandler = new Handler();
		mShowToastTask = new Runnable() {
			@Override
			public void run() {
				Toast.makeText(EmailLoginActivity.this, "No such user with given email and password:(", Toast.LENGTH_LONG).show();
			}
		};
	}
	
	
	
	private void addListenerOnButtonLogin() {

		mLoginConfirmButton = (ImageButton) findViewById(R.id.loginButton);
		mLoginConfirmButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (isLoginFieldsValid()) {
					mHandler.postDelayed(mShowToastTask, 2000);
					final JsonObject json = new JsonObject();
					json.addProperty("email", mLoginEmailField.getText().toString());
					json.addProperty("password", mLoginPasswordField.getText().toString());

					Ion.with(EmailLoginActivity.this).load(USER_LOGIN)
							.setHeader("Content-Type", "application/json; charset=utf-8").setJsonObjectBody(json)
							.asJsonObject().setCallback(new FutureCallback<JsonObject>() {
								@Override
								public void onCompleted(Exception e, JsonObject result) {
									mHandler.removeCallbacks(mShowToastTask);
									if (e != null) {
										if (Constants.LOG) {
											Toast.makeText(EmailLoginActivity.this, "Error during login",
													Toast.LENGTH_LONG).show();
										} else {
											final JSONObject errorJson = new JSONObject();
											try {
												errorJson.put(mLoginEmailField.getText().toString(), e.toString());
												User.getInstance(EmailLoginActivity.this).mMixpanel
														.registerSuperPropertiesOnce(errorJson);
											} catch (JSONException e1) {
												e1.printStackTrace();
											}
										}
										return;
									}
									try {
										final String email = result.get(LOGIN_INFO_EMAIL).getAsString();
										final String lastName = result.get(LOGIN_INFO_LAST_NAME).getAsString();
										final String firstName = result.get(LOGIN_INFO_FIRST_NAME).getAsString();
										final String authToken = result.get(LOGIN_INFO_AUTHTOKEN).getAsString();
										final String avatarUrl = result.get(LOGIN_INFO_AVATAR_URL).getAsString();

										loginPrefsEditor.putString(LOGIN_INFO_EMAIL, email);
										loginPrefsEditor.putString(LOGIN_INFO_LAST_NAME, lastName);
										loginPrefsEditor.putString(LOGIN_INFO_FIRST_NAME, firstName);
										loginPrefsEditor.putString(LOGIN_INFO_AUTHTOKEN, authToken);
										loginPrefsEditor.putString(LOGIN_INFO_AVATAR_URL, avatarUrl);
										loginPrefsEditor.commit();

										MixpanelAPI mixpanel = MixpanelAPI.getInstance(EmailLoginActivity.this,
												MIXPANEL_TOKEN);
										JSONObject firstTime = new JSONObject();
										try {
											mixpanel.identify(email);
											mixpanel.getPeople().identify(email);
											mixpanel.getPeople().increment(EMAIL_LOGIN_COUNT, 1);
											firstTime.put(email, new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
											mixpanel.registerSuperPropertiesOnce(firstTime);
										} catch (JSONException e1) {
											e1.printStackTrace();
										}
										
										SocketIOManager.getInstance((BigSpoon)getApplicationContext()).setupSocketIOConnection();
										Intent intent = new Intent(EmailLoginActivity.this, OutletListActivity.class);
										intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
										EmailLoginActivity.this.startActivity(intent);
										
									} catch (NullPointerException nullException) {
										Toast.makeText(EmailLoginActivity.this,
												"User not found, please check your email address.", Toast.LENGTH_LONG)
												.show();
									}

								}
							});
				} else {
					Toast.makeText(EmailLoginActivity.this, "Sorry, you need to fill up the forms", Toast.LENGTH_LONG)
							.show();
				}
			}
		});

	}

	private boolean isLoginFieldsValid() {
		return !mLoginEmailField.getText().toString().isEmpty() && !mLoginPasswordField.getText().toString().isEmpty();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
}
