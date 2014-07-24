package sg.com.bigspoon.www.activities;

import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_AUTHTOKEN;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_AVATAR_URL;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_EMAIL;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_FIRST_NAME;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_LAST_NAME;
import static sg.com.bigspoon.www.data.Constants.PREFS_NAME;
import static sg.com.bigspoon.www.data.Constants.TUTORIAL_SET;
import static sg.com.bigspoon.www.data.Constants.USER_LOGIN_WITH_FB;
import static sg.com.bigspoon.www.data.Constants.MIXPANEL_TOKEN;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.data.BigSpoon;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

public class EntryActivity extends Activity {

	private static String ION_LOGGING_FB_LOGIN = "ion-fb-login";

	private ImageButton loginImageButton;
	private ImageButton signUpImageButton;
	private ImageButton fbLoginButton;

	private SharedPreferences loginPreferences;
	private SharedPreferences.Editor loginPrefsEditor;

	private Session.StatusCallback statusCallback = new Session.StatusCallback() {
		public void call(Session session, SessionState state,
				Exception exception) {
			updateAccordingToFBSessionChange();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Ion.getDefault(this).configure()
				.setLogging(ION_LOGGING_FB_LOGIN, Log.DEBUG);
		loginPreferences = getSharedPreferences(PREFS_NAME, 0);
		loginPrefsEditor = loginPreferences.edit();

		initFBSession(savedInstanceState);

		addListenerOnButtonLogin();
		addListenerOnButtonSignUp();
		addListenerOnButtonFBSignUp();
		updateAccordingToFBSessionChange();

		final boolean hasShownTutorial = loginPreferences.getBoolean(
				TUTORIAL_SET, false);
		if (!hasShownTutorial) {
			((BigSpoon) getApplication()).checkLocationEnabledByForce();
		}
	}

	private void initFBSession(Bundle savedInstanceState) {
		Session session = Session.getActiveSession();
		if (session == null) {
			if (savedInstanceState != null) {
				session = Session.restoreSession(this, null, statusCallback,
						savedInstanceState);
			}
			if (session == null) {
				session = new Session(this);
			}
			Session.setActiveSession(session);
			if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
				session.openForRead(new Session.OpenRequest(this)
						.setCallback(statusCallback));
			}
		}
	}

	private void updateAccordingToFBSessionChange() {
		Session session = Session.getActiveSession();
		if (session.isOpened()) {

			final JsonObject json = new JsonObject();
			json.addProperty("access_token", session.getAccessToken());
			Ion.with(this)
					.load(USER_LOGIN_WITH_FB)
					.setHeader("Content-Type",
							"application/json; charset=utf-8")
					.setJsonObjectBody(json).asJsonObject()
					.setCallback(new FutureCallback<JsonObject>() {
						@Override
						public void onCompleted(Exception e, JsonObject result) {
							if (e != null) {
								Toast.makeText(EntryActivity.this,
										"Error login with FB",
										Toast.LENGTH_LONG).show();
								return;
							}
							final String email = result.get(LOGIN_INFO_EMAIL)
									.getAsString();
							final String lastName = result.get(
									LOGIN_INFO_LAST_NAME).getAsString();
							final String firstName = result.get(
									LOGIN_INFO_FIRST_NAME).getAsString();
							final String authToken = result.get(
									LOGIN_INFO_AUTHTOKEN).getAsString();
							final String avatarUrl = result.get(
									LOGIN_INFO_AVATAR_URL).getAsString();
							
							
							loginPrefsEditor.putString(LOGIN_INFO_EMAIL, email);
							loginPrefsEditor.putString(LOGIN_INFO_LAST_NAME,
									lastName);
							loginPrefsEditor.putString(LOGIN_INFO_FIRST_NAME,
									firstName);
							loginPrefsEditor.putString(LOGIN_INFO_AUTHTOKEN,
									authToken);
							loginPrefsEditor.putString(LOGIN_INFO_AVATAR_URL,
									avatarUrl);
							loginPrefsEditor.commit();
							
							MixpanelAPI mixpanel =
								    MixpanelAPI.getInstance(EntryActivity.this, MIXPANEL_TOKEN);
							JSONObject firstTime = new JSONObject();
							try {
								firstTime.put(email, new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
								mixpanel.registerSuperPropertiesOnce(firstTime);
							} catch (JSONException e1) {
								e1.printStackTrace();
							}
							
							Intent intent = new Intent(EntryActivity.this,
									OutletListActivity.class);
							EntryActivity.this.startActivity(intent);
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
				final Intent intent = new Intent(getApplicationContext(),
						EmailLoginActivity.class);
				startActivity(intent);
			}
		});
	}

	public void addListenerOnButtonSignUp() {
		signUpImageButton = (ImageButton) findViewById(R.id.imageButton2);
		signUpImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(),
						SignUpActivity.class);
				startActivity(intent);
			}
		});
	}

	public void addListenerOnButtonFBSignUp() {
		fbLoginButton = (ImageButton) findViewById(R.id.imageButton1);
		fbLoginButton.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				Session session = Session.getActiveSession();
				if (!session.isOpened() && !session.isClosed()) {
					session.openForRead(new Session.OpenRequest(
							EntryActivity.this).setCallback(statusCallback));
				} else {
					Session.openActiveSession(EntryActivity.this, true,
							statusCallback);
				}
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();
		Session.getActiveSession().addCallback(statusCallback);
	}

	@Override
	public void onStop() {
		super.onStop();
		Session.getActiveSession().removeCallback(statusCallback);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
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

	public void onBackPressed() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
}