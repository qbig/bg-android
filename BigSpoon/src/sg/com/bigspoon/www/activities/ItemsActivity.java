package sg.com.bigspoon.www.activities;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.R.id;
import sg.com.bigspoon.www.R.layout;
import sg.com.bigspoon.www.R.menu;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;

public class ItemsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_items);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.items, menu);
		  MenuInflater inflater = getMenuInflater();
		    inflater.inflate(R.menu.items, menu);
		   
		    ActionBar actionBar = getActionBar();
		    actionBar.setDisplayShowHomeEnabled(false);
		    //addListenerOnButtonLogout();	    
		    //displaying custom ActionBar
		    View mActionBarView = getLayoutInflater().inflate(R.layout.action_bar_items_activity, null);
		    actionBar.setCustomView(mActionBarView);
		    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		    
		    
		    ImageButton ibItem1 = (ImageButton) mActionBarView.findViewById(R.id.btn_menu);
		    ibItem1.setOnClickListener(new View.OnClickListener() {
		        @Override
		        public void onClick(View view) {
		                	Intent intent = new Intent(getApplicationContext(),
							MenuPhotoListActivity.class);
					startActivity(intent);
		        }
		    });
		    
		    
		    
		    
		    ImageButton ibItem2 = (ImageButton) mActionBarView.findViewById(R.id.order_history);
		    ibItem2.setOnClickListener(new View.OnClickListener() {
		        @Override
		        public void onClick(View view) {
		            // ...
		        	
		        	Intent intent = new Intent(getApplicationContext(),
							OrderHistoryListActivity.class);
					startActivity(intent);
		        }
		    });
		    
		     
		    return true;
	}

}
