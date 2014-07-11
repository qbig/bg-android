package sg.com.bigspoon.www.activities;

import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_AUTHTOKEN;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_AVATAR_URL;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_EMAIL;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_FIRST_NAME;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_LAST_NAME;
import static sg.com.bigspoon.www.data.Constants.PREFS_NAME;
import static sg.com.bigspoon.www.data.Constants.USER_SIGNUP;
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

public class SignUpActivity extends Activity {
	private static String ION_LOGGING_SIGNUP = "ion-email-signup";
	EditText mSignupNameField;
	EditText mSignupEmailField;
	EditText mSignupPasswordField;
	
	ImageButton mSignUpConfirmButton;
	
	private SharedPreferences.Editor loginPrefsEditor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Ion.getDefault(this).configure().setLogging(ION_LOGGING_SIGNUP, Log.DEBUG);
		loginPrefsEditor = getSharedPreferences(PREFS_NAME,0).edit();
		
		setContentView(R.layout.activity_sign_up);
		mSignupNameField = (EditText) findViewById(R.id.signupFullName);
		mSignupEmailField = (EditText) findViewById(R.id.signupEmail);
		mSignupPasswordField = (EditText) findViewById(R.id.signupPassword);
		mSignUpConfirmButton = (ImageButton) findViewById(R.id.signupButton);
		
		mSignUpConfirmButton.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isSignUpFieldsValid()){
					final JsonObject json = new JsonObject();
					json.addProperty("first_name", mSignupNameField.getText().toString());
					json.addProperty("email", mSignupEmailField.getText().toString());
					json.addProperty("password", mSignupPasswordField.getText().toString());
					
					Ion.with(SignUpActivity.this)
					.load(USER_SIGNUP)
					.setHeader("Content-Type", "application/json; charset=utf-8")
					.setJsonObjectBody(json)
					.asJsonObject()
					.setCallback(new FutureCallback<JsonObject>() {
			            @Override
			            public void onCompleted(Exception e, JsonObject result) {
			                if (e != null) {
			                    Toast.makeText(SignUpActivity.this, "Error during sign up", Toast.LENGTH_LONG).show();
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
			                
				            Intent intent = new Intent(SignUpActivity.this, OutletListActivity.class);
				   			SignUpActivity.this.startActivity(intent);
			            }
			        });
				} else {
					Toast.makeText(SignUpActivity.this, "Sorry, you need to fill up the forms", Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	
	private boolean isSignUpFieldsValid() {
		return ! mSignupEmailField.getText().toString().isEmpty() &&
				! mSignupNameField.getText().toString().isEmpty() &&
				! mSignupPasswordField.getText().toString().isEmpty();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.sign_up, menu);
		return true;
	}

}
