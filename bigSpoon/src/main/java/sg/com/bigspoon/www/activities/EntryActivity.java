package sg.com.bigspoon.www.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.Session;
import com.facebook.SessionState;
import com.google.gson.JsonObject;
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

import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_AUTHTOKEN;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_AVATAR_URL;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_EMAIL;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_FIRST_NAME;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_LAST_NAME;
import static sg.com.bigspoon.www.data.Constants.MIXPANEL_TOKEN;
import static sg.com.bigspoon.www.data.Constants.NOTIF_TO_START_LOCATION_SERVICE;
import static sg.com.bigspoon.www.data.Constants.PREFS_NAME;
import static sg.com.bigspoon.www.data.Constants.TUTORIAL_SET;
import static sg.com.bigspoon.www.data.Constants.USER_LOGIN_WITH_FB;

public class EntryActivity extends Activity {

	private static String ION_LOGGING_FB_LOGIN = "ion-fb-login";
	private static String FB_LOGIN_COUNT = "fb-login-count";
	private ImageButton loginImageButton;
	private ImageButton signUpImageButton;
	private ImageButton fbLoginButton;

	private SharedPreferences loginPreferences;
	private SharedPreferences.Editor loginPrefsEditor;
	private MixpanelAPI mMixpanel;
	private boolean firstTimeStartingApp;
	ProgressBar progressBar;
	private boolean doubleBackToExitPressedOnce;
	private Session.StatusCallback fbStatusCallback = new Session.StatusCallback() {
		public void call(Session session, SessionState state, Exception exception) {
			updateAccordingToFBSessionChange();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.progressBarMain);
		Ion.getDefault(this).configure().setLogging(ION_LOGGING_FB_LOGIN, Log.DEBUG);
		loginPreferences = getSharedPreferences(PREFS_NAME, 0);
		loginPrefsEditor = loginPreferences.edit();

		mMixpanel = MixpanelAPI.getInstance(EntryActivity.this, MIXPANEL_TOKEN);
		initFBSession(savedInstanceState);
		addListenerOnButtonLogin();
		addListenerOnButtonSignUp();
		addListenerOnButtonFBSignUp();
		updateAccordingToFBSessionChange();

		firstTimeStartingApp = true;
		final boolean hasShownTutorial = loginPreferences.getBoolean(TUTORIAL_SET, false);
		if (!hasShownTutorial) {
			((BigSpoon) getApplication()).checkLocationEnabledByForce();
		} else {
			firstTimeStartingApp = false;
		}
	}

	private void initFBSession(Bundle savedInstanceState) {
		Session session = Session.getActiveSession();
		if (session == null) {
			if (savedInstanceState != null) {
				session = Session.restoreSession(this, null, fbStatusCallback, savedInstanceState);
			}
			if (session == null) {
				session = new Session(this);
			}
			Session.setActiveSession(session);
			if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
				session.openForRead(new Session.OpenRequest(this).setCallback(fbStatusCallback));
			}
		}
	}

	private void updateAccordingToFBSessionChange() {
		Session session = Session.getActiveSession();
		if (session.isOpened()) {
			progressBar.setVisibility(View.VISIBLE);
			final JsonObject json = new JsonObject();
			json.addProperty("access_token", session.getAccessToken());
			Ion.with(this).load(USER_LOGIN_WITH_FB).setHeader("Content-Type", "application/json; charset=utf-8")
					.setJsonObjectBody(json).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
						@Override
						public void onCompleted(Exception e, JsonObject result) {
							if (e != null || result == null) {
								if (Constants.LOG) {
									Toast.makeText(EntryActivity.this, "Error login with FB", Toast.LENGTH_LONG).show();
								} else {
									final JSONObject errorJson = new JSONObject();
									try {
										final String email = loginPreferences.getString(LOGIN_INFO_EMAIL, null);
										errorJson.put(email, e.toString());
										User.getInstance(EntryActivity.this).mMixpanel
												.registerSuperPropertiesOnce(errorJson);
									} catch (JSONException e1) {
										Crashlytics.logException(e1);
									}
								}
								return;
							}
							
							final String email = result.get(LOGIN_INFO_EMAIL) == null ? null : result.get(LOGIN_INFO_EMAIL).getAsString();
							final String lastName = result.get(LOGIN_INFO_LAST_NAME) == null ? null : result.get(LOGIN_INFO_LAST_NAME).getAsString();
							final String firstName = result.get(LOGIN_INFO_FIRST_NAME) == null ? null : result.get(LOGIN_INFO_FIRST_NAME).getAsString();
							final String authToken = result.get(LOGIN_INFO_AUTHTOKEN) == null ? null : result.get(LOGIN_INFO_AUTHTOKEN).getAsString();
							final String avatarUrl = result.get(LOGIN_INFO_AVATAR_URL) == null ? null : result.get(LOGIN_INFO_AVATAR_URL).getAsString();
							
							try {
								loginPrefsEditor.putString(LOGIN_INFO_EMAIL, email);
								loginPrefsEditor.putString(LOGIN_INFO_LAST_NAME, lastName);
								loginPrefsEditor.putString(LOGIN_INFO_FIRST_NAME, firstName);
								loginPrefsEditor.putString(LOGIN_INFO_AUTHTOKEN, authToken);
								loginPrefsEditor.putString(LOGIN_INFO_AVATAR_URL, avatarUrl);
								loginPrefsEditor.commit();
							} catch (NullPointerException ne){
								Crashlytics.logException(ne);
							}

							mMixpanel = MixpanelAPI.getInstance(EntryActivity.this, MIXPANEL_TOKEN);
							JSONObject firstTime = new JSONObject();
							try {
								mMixpanel.alias(email, mMixpanel.getDistinctId());
								mMixpanel.getPeople().identify(mMixpanel.getDistinctId());
								firstTime.put(email, new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
								mMixpanel.registerSuperPropertiesOnce(firstTime);
								mMixpanel.getPeople().increment(FB_LOGIN_COUNT, 1);
								mMixpanel.track("fbLogin Success", firstTime);
							} catch (JSONException e1) {
								Crashlytics.logException(e1);
							}
							SocketIOManager.getInstance((BigSpoon)getApplicationContext()).setupSocketIOConnection();
							Intent intent = new Intent(EntryActivity.this, OutletListActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
							EntryActivity.this.startActivity(intent);
							progressBar.setVisibility(View.GONE);
							EntryActivity.this.finish();
						}
					});

		} else {
			loginPrefsEditor.remove(LOGIN_INFO_EMAIL);
			loginPrefsEditor.remove(LOGIN_INFO_LAST_NAME);
			loginPrefsEditor.remove(LOGIN_INFO_FIRST_NAME);
			loginPrefsEditor.remove(LOGIN_INFO_AUTHTOKEN);
			loginPrefsEditor.remove(LOGIN_INFO_AVATAR_URL);
			loginPrefsEditor.commit();
		}
	}

	public void addListenerOnButtonLogin() {
		loginImageButton = (ImageButton) findViewById(R.id.imageButton3);
		loginImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				final Intent intent = new Intent(getApplicationContext(), EmailLoginActivity.class);
				startActivity(intent);
			}
		});
	}

	public void addListenerOnButtonSignUp() {
		signUpImageButton = (ImageButton) findViewById(R.id.imageButton2);
		signUpImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
				startActivity(intent);
			}
		});
	}

	public void addListenerOnButtonFBSignUp() {
		fbLoginButton = (ImageButton) findViewById(R.id.imageButton1);
		fbLoginButton.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				if (loginPreferences.contains(LOGIN_INFO_EMAIL)) {
					final String email = loginPreferences.getString(LOGIN_INFO_EMAIL, null);
					if (email != null) {
						JSONObject json = new JSONObject();
						try {
							json.put("email", email);
							mMixpanel.track("fbLogin", json);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

				}
				Toast.makeText(getApplicationContext(), "This may take a few seconds.", Toast.LENGTH_SHORT).show();
				Session session = Session.getActiveSession();
				if (!session.isOpened() && !session.isClosed()) {
					session.openForRead(new Session.OpenRequest(EntryActivity.this).setCallback(fbStatusCallback));
				} else {
					Session.openActiveSession(EntryActivity.this, true, fbStatusCallback);
				}
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();
		Session.getActiveSession().addCallback(fbStatusCallback);
	}

	@Override
	public void onStop() {
		super.onStop();
		Session.getActiveSession().removeCallback(fbStatusCallback);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (firstTimeStartingApp) {
			Intent intent = new Intent(NOTIF_TO_START_LOCATION_SERVICE);
			LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
		}
	}

	@Override
	public void onBackPressed() {
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