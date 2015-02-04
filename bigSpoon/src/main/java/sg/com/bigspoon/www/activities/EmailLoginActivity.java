package sg.com.bigspoon.www.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.data.BigSpoon;
import sg.com.bigspoon.www.data.Constants;
import sg.com.bigspoon.www.data.SocketIOManager;
import sg.com.bigspoon.www.data.User;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_AUTHTOKEN;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_AVATAR_URL;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_EMAIL;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_FIRST_NAME;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_LAST_NAME;
import static sg.com.bigspoon.www.data.Constants.MIXPANEL_TOKEN;
import static sg.com.bigspoon.www.data.Constants.NOTIF_TO_START_LOCATION_SERVICE;
import static sg.com.bigspoon.www.data.Constants.PREFS_NAME;
import static sg.com.bigspoon.www.data.Constants.TUTORIAL_SET;
import static sg.com.bigspoon.www.data.Constants.USER_LOGIN;

public class EmailLoginActivity extends Activity {
	private static String ION_LOGGING_LOGIN = "ion-email-login";
	private static String EMAIL_LOGIN_COUNT = "email-login-count";
	EditText mLoginEmailField;
	EditText mLoginPasswordField;
	Button mLoginConfirmButton;

	private SharedPreferences.Editor loginPrefsEditor;
    private SharedPreferences loginPreferences;
	private Runnable mShowToastTask;
	private Handler mHandler;
    private boolean doubleBackToExitPressedOnce;
    ProgressBar progressBar;
    private Future<com.google.gson.JsonObject> loginFuture;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		Ion.getDefault(this).configure().setLogging(ION_LOGGING_LOGIN, Log.DEBUG);
        loginPreferences = getSharedPreferences(PREFS_NAME, 0);
		loginPrefsEditor = loginPreferences.edit();
        progressBar = (ProgressBar) findViewById(R.id.progressBarMain);
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


        if (!loginPreferences.getBoolean(TUTORIAL_SET, false)) {
            ((BigSpoon) getApplication()).checkLocationEnabledByForce();
        }
	}

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }
	
	private void addListenerOnButtonLogin() {

		mLoginConfirmButton = (Button) findViewById(R.id.loginButton);
        //Start Ordering, Om nom nom
        mLoginConfirmButton.setText(Html.fromHtml("<font color='white'>Start Ordering, </font><font color='#5BD6CD'>Om nom nom</font>"));
		mLoginConfirmButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (isLoginFieldsValid()) {
                    progressBar.setVisibility(View.VISIBLE);
					final JsonObject json = new JsonObject();
					json.addProperty("email", mLoginEmailField.getText().toString());
					json.addProperty("password", "bigspoon");
					EmailLoginActivity.this.loginFuture = Ion.with(EmailLoginActivity.this).load(USER_LOGIN)
							.setHeader("Content-Type", "application/json; charset=utf-8").setJsonObjectBody(json)
							.asJsonObject().setCallback(new FutureCallback<JsonObject>() {
								@Override
								public void onCompleted(Exception e, JsonObject result) {
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

                                        EmailLoginActivity.this.progressBar.setVisibility(View.INVISIBLE);
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
		return !mLoginEmailField.getText().toString().isEmpty();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

    @Override
    protected void onResume() {
        super.onResume();
        mLoginEmailField.setText(loginPreferences.getString(LOGIN_INFO_EMAIL, ""));
        if (loginPreferences.getBoolean(TUTORIAL_SET, false)) {
            Intent intent = new Intent(NOTIF_TO_START_LOCATION_SERVICE);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (this.progressBar.getVisibility() == View.VISIBLE) {
            EmailLoginActivity.this.progressBar.setVisibility(View.INVISIBLE);
            if (this.loginFuture != null) {
                this.loginFuture.cancel();
            }
            return;
        }

        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

}
