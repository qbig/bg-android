package sg.com.bigspoon.www.activities;

import static sg.com.bigspoon.www.data.Constants.PREFS_NAME;
import sg.com.bigspoon.www.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;


public class LaunchActivity extends Activity {
	
	private static int SPLASH_TIME_OUT = 1800;
	private static final String TUTORIAL_SET = "Tutorial Set";
	private SharedPreferences loginPreferences;
	private SharedPreferences.Editor loginPrefsEditor;
	private Boolean hasShownTutorial;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);
		new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

            	loginPreferences = getSharedPreferences(PREFS_NAME,0);
            	hasShownTutorial = loginPreferences.getBoolean(TUTORIAL_SET, false);

            	if(! hasShownTutorial)
            	{
            		setTutorialFlagTrue();
                    startTutorialActivity();
            	} else {
            		startEntryActivity();
            	}
            	
            	finish();
            }

			private void startEntryActivity() {
				Intent i = new Intent(LaunchActivity.this, EntryActivity.class);
				startActivity(i);
			}

			private void startTutorialActivity() {
				Intent i = new Intent(LaunchActivity.this, TutorialActivity.class);
				startActivity(i);
			}

			private void setTutorialFlagTrue() {
				loginPrefsEditor = loginPreferences.edit();
				loginPrefsEditor.putBoolean(TUTORIAL_SET, true);
				loginPrefsEditor.commit();
			}
        }, SPLASH_TIME_OUT);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.launch, menu);
		return true;
	}

}

