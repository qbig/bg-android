package sg.com.bigspoon.www.activities;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.R.id;
import sg.com.bigspoon.www.R.layout;
import sg.com.bigspoon.www.R.menu;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.view.View.OnClickListener;

public class LoginActivity extends Activity {

		ImageButton loginImageButton;
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_login);
			addListenerOnButtonLogin();
		}

		private void addListenerOnButtonLogin() {
			
			 loginImageButton = (ImageButton) findViewById(R.id.imageButton1);
			 loginImageButton.setOnClickListener(new OnClickListener() {

		            @Override
		            public void onClick(View arg0) {

		                Intent intent = new Intent
		                        (getApplicationContext(), OutListActivity.class);
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
