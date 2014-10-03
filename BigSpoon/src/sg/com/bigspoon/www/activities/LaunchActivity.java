package sg.com.bigspoon.www.activities;

import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_AUTHTOKEN;
import static sg.com.bigspoon.www.data.Constants.PREFS_NAME;
import static sg.com.bigspoon.www.data.Constants.TUTORIAL_SET;
import sg.com.bigspoon.www.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;

import com.crashlytics.android.Crashlytics;


public class LaunchActivity extends Activity {
	
	private static int SPLASH_TIME_OUT = 1800;
	private SharedPreferences loginPreferences;
	private SharedPreferences.Editor loginPrefsEditor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Crashlytics.start(this);
		setContentView(R.layout.activity_launch);
		new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

            	loginPreferences = getSharedPreferences(PREFS_NAME,0);
            	final boolean hasShownTutorial = loginPreferences.getBoolean(TUTORIAL_SET, false);

            	if(! hasShownTutorial)
            	{
                    startTutorialActivity();
            	} else {
            		if (loginPreferences.contains(LOGIN_INFO_AUTHTOKEN)){
            			startOutletActivity();
            		} else {
            			startEntryActivity();
            		}
            		
            	}
            	
            	finish();
            }
            
            private void startOutletActivity() {
				Intent i = new Intent(LaunchActivity.this, OutletListActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}
            
			private void startEntryActivity() {
				Intent i = new Intent(LaunchActivity.this, EntryActivity.class);
				startActivity(i);
			}

			private void startTutorialActivity() {
				Intent i = new Intent(LaunchActivity.this, TutorialActivity.class);
				startActivity(i);
			}

        }, SPLASH_TIME_OUT);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.launch, menu);
		return true;
	}

}

