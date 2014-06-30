package sg.com.bigspoon.www.activities;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.R.layout;
import sg.com.bigspoon.www.R.menu;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;

public class LaunchActivity extends Activity {
	
	private static int SPLASH_TIME_OUT = 1800;
	private SharedPreferences loginPreferences;
	private SharedPreferences.Editor loginPrefsEditor;
	private Boolean status;
	public static final String PREFS_NAME = "MyPrefsFile";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);
		new Handler().postDelayed(new Runnable() {
			 
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
 
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
            	loginPreferences = getSharedPreferences(PREFS_NAME,0);
            	
            	
            	status = loginPreferences.getBoolean("tutorial", false);
            	if(status == false)
            	{
            		loginPrefsEditor = loginPreferences.edit();
            		loginPrefsEditor.putBoolean("tutorial", true);
            		loginPrefsEditor.commit();
                    Intent i = new Intent(LaunchActivity.this, TutorialActivity.class);
                    startActivity(i);
                    finish();
            	}
            	else if (status == true)
            	{
            		Intent i = new Intent(LaunchActivity.this, EntryActivity.class);
                    startActivity(i);
                    finish();
            	}
                
            }
        }, SPLASH_TIME_OUT);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.launch, menu);
		return true;
	}

}

