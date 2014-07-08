package sg.com.bigspoon.www.activities;

import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;

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
import android.widget.Toast;

public class MainActivity extends Activity {


	ImageButton loginImageButton, signUpImageButton;
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
    private ImageButton fbLoginButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		fbLoginButton = (ImageButton) findViewById(R.id.imageButton1);
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
		Session session = Session.getActiveSession();
		
		if(session == null) {
            if(savedInstanceState != null) {
                session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
            }
            if(session== null) {
                session = new Session(this);
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
            }
        }
		
		addListenerOnButtonLogin();
		addListenerOnButtonSignUp();
        updateView();
		//addListenerOnButtonFBSignUp();
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
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }
    
    
    private void updateView() {
         Session session = Session.getActiveSession();    
        if (session.isOpened()) {
            //textInstructionsOrLink.setText(URL_PREFIX_FRIENDS + session.getAccessToken());
        	//fbLoginButton.setText(R.string.logout);
            Intent intent = new Intent(MainActivity.this, OutListActivity.class);
            MainActivity.this.startActivity(intent);
        } else {
            //textInstructionsOrLink.setText(R.string.instructions);
            //buttonLoginLogout.setText(R.string.login);
        	fbLoginButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) { onClickLogin(); }
            });
        }
    }
	
    
    public void loginfb() {
        //start the new activity here
        //Toast.makeText(getApplicationContext(), "Works first", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(MainActivity.this, OutListActivity.class);
        MainActivity.this.startActivity(intent);

    }
    
    private void onClickLogin() {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
        } else {
            Session.openActiveSession(this, true, statusCallback);
        }
       // Intent intent = new Intent(MainActivity.this, OutListActivity.class);
       // MainActivity.this.startActivity(intent);
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

	/* public void addListenerOnButtonFBSignUp() {
		 fbLoginButton = (ImageButton) findViewById(R.id.imageButton1);
		 fbLoginButton.setOnClickListener(new OnClickListener() {

	            @Override
	            public void onClick(View arg0) {

	                Intent intent = new Intent
	                        (getApplicationContext(), LoginActivity.class);
	                    startActivity(intent); 
	            }
	        });
	    }*/
	 public void onBackPressed() {
			   //Log.i("HA", "Finishing");
			   Intent intent = new Intent(Intent.ACTION_MAIN);
			   intent.addCategory(Intent.CATEGORY_HOME);
			   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			   startActivity(intent);
			 }
	 
	 private class SessionStatusCallback implements Session.StatusCallback {
	        @Override
	        public void call(Session session, SessionState state, Exception exception) {
	           updateView();
	        }
	    }
	}
