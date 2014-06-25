package sg.com.bigspoon.www.activities;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.R.id;
import sg.com.bigspoon.www.R.layout;
import sg.com.bigspoon.www.R.menu;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class MainActivity extends Activity {
	
	
	ImageButton loginImageButton, signUpImageButton,fbLoginButton;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		addListenerOnButtonLogin();
		addListenerOnButtonSignUp();
		addListenerOnButtonFBSignUp();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	 public void addListenerOnButtonLogin() {
		 loginImageButton = (ImageButton) findViewById(R.id.imageButton3);
		 loginImageButton.setOnClickListener(new OnClickListener() {

	            @Override
	            public void onClick(View arg0) {

	                Intent intent = new Intent
	                        (getApplicationContext(), LoginActivity.class);
	                    startActivity(intent); 
	            }
	        });
	    }
	 
	 public void addListenerOnButtonSignUp() {
		 signUpImageButton = (ImageButton) findViewById(R.id.imageButton2);
		 signUpImageButton.setOnClickListener(new OnClickListener() {

	            @Override
	            public void onClick(View arg0) {

	                Intent intent = new Intent
	                        (getApplicationContext(), ModifierActivity.class);
	                    startActivity(intent); 
	            }
	        });
	    }
	 
	 public void addListenerOnButtonFBSignUp() {
		 fbLoginButton = (ImageButton) findViewById(R.id.imageButton1);
		 fbLoginButton.setOnClickListener(new OnClickListener() {

	            @Override
	            public void onClick(View arg0) {

	                Intent intent = new Intent
	                        (getApplicationContext(), LoginActivity.class);
	                    startActivity(intent); 
	            }
	        });
	    }
	 public void onBackPressed() {
			   //Log.i("HA", "Finishing");
			   Intent intent = new Intent(Intent.ACTION_MAIN);
			   intent.addCategory(Intent.CATEGORY_HOME);
			   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			   startActivity(intent);
			 }
	}

