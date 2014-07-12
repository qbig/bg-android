package sg.com.bigspoon.www.activities;

import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_AUTHTOKEN;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_AVATAR_URL;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_EMAIL;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_FIRST_NAME;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_LAST_NAME;
import static sg.com.bigspoon.www.data.Constants.PREFS_NAME;
import static sg.com.bigspoon.www.data.Constants.USER_LOGIN;
import sg.com.bigspoon.www.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class EmailLoginActivity extends Activity {
	private static String ION_LOGGING_LOGIN = "ion-email-login";
	EditText mLoginEmailField;
	EditText mLoginPasswordField;

	ImageButton mLoginConfirmButton;

	private SharedPreferences.Editor loginPrefsEditor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		Ion.getDefault(this).configure()
				.setLogging(ION_LOGGING_LOGIN, Log.DEBUG);
		loginPrefsEditor = getSharedPreferences(PREFS_NAME, 0).edit();

		mLoginEmailField = (EditText) findViewById(R.id.loginEmail);
		mLoginPasswordField = (EditText) findViewById(R.id.loginPassword);
		addListenerOnButtonLogin();
	}

	private void addListenerOnButtonLogin() {

		mLoginConfirmButton = (ImageButton) findViewById(R.id.loginButton);
		mLoginConfirmButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (isLoginFieldsValid()) {
					final JsonObject json = new JsonObject();
					json.addProperty("email", mLoginEmailField.getText().toString());
					json.addProperty("password", mLoginPasswordField.getText().toString());
					
					Ion.with(EmailLoginActivity.this)
					.load(USER_LOGIN)
					.setHeader("Content-Type", "application/json; charset=utf-8")
					.setJsonObjectBody(json)
					.asJsonObject()
					.setCallback(new FutureCallback<JsonObject>() {
			            @Override
			            public void onCompleted(Exception e, JsonObject result) {
			                if (e != null) {
			                    Toast.makeText(EmailLoginActivity.this, "Error during login", Toast.LENGTH_LONG).show();
			                    return;
			                }
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
			                
				            Intent intent = new Intent(EmailLoginActivity.this, OutletListActivity.class);
				            EmailLoginActivity.this.startActivity(intent);
			            }
			        });
				} else {
					Toast.makeText(EmailLoginActivity.this, "Sorry, you need to fill up the forms", Toast.LENGTH_LONG).show();
				}
			}
		});

	}

	private boolean isLoginFieldsValid() {
		return !mLoginEmailField.getText().toString().isEmpty()
				&& !mLoginPasswordField.getText().toString().isEmpty();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
}