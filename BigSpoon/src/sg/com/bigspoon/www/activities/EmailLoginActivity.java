package sg.com.bigspoon.www.activities;

import static sg.com.bigspoon.www.data.Constants.PREFS_NAME;

import com.koushikdutta.ion.Ion;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.R.id;
import sg.com.bigspoon.www.R.layout;
import sg.com.bigspoon.www.R.menu;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.view.View.OnClickListener;

public class EmailLoginActivity extends Activity {
		private static String ION_LOGGING_LOGIN = "ion-email-login";
		ImageButton loginImageButton;
		
		private SharedPreferences.Editor loginPrefsEditor;
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_login);
			Ion.getDefault(this).configure().setLogging(ION_LOGGING_LOGIN, Log.DEBUG);
			loginPrefsEditor = getSharedPreferences(PREFS_NAME,0).edit();
			
			addListenerOnButtonLogin();
		}

		private void addListenerOnButtonLogin() {
			
			 loginImageButton = (ImageButton) findViewById(R.id.imageButton1);
			 loginImageButton.setOnClickListener(new OnClickListener() {

		            @Override
		            public void onClick(View arg0) {

		                Intent intent = new Intent
		                        (getApplicationContext(), OutletListActivity.class);
		                    startActivity(intent); 
		            }
		        });
			
			
		}

		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.login, menu);
			return true;
		}
	}
