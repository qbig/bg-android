package sg.com.bigspoon.www.activities;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.R.id;
import sg.com.bigspoon.www.R.layout;
import sg.com.bigspoon.www.R.menu;
import sg.com.bigspoon.www.adapters.CustomItemsList;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.StateListDrawable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;



import java.util.ArrayList;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

public class ItemsActivity extends Activity {

		ListView list;
        ImageButton imageplus,imageminus;	
        
        
        
	Integer[] minusImage = { R.drawable.button_minus_no_circle, R.drawable.button_minus_no_circle,
			R.drawable.button_minus_no_circle ,R.drawable.button_minus_no_circle,R.drawable.button_minus_no_circle, R.drawable.button_minus_no_circle,
			R.drawable.button_minus_no_circle ,R.drawable.button_minus_no_circle};
	String[] number= {"1","1","1","1","1","1","1","1"};
	Integer[] addImage = { R.drawable.button_add_no_circle, R.drawable.button_add_no_circle,
			R.drawable.button_add_no_circle ,R.drawable.button_add_no_circle,R.drawable.button_add_no_circle, R.drawable.button_add_no_circle,
			R.drawable.button_add_no_circle ,R.drawable.button_add_no_circle };
	String[] itemdesc = { "Welcome to Kith ", "Testing !! The Groc.....",
			"Testing !! Strictly Pancakes" ,"Testing !! Strictly Pancakes","Welcome to Kith ", "Testing !! The Groc.....",
			"Testing !! Strictly Pancakes" ,"Testing !! Strictly Pancakes"};
  String[] price= {"$5","$25","$15","$12","$5","$25","$15","$12"};
	
		
  
  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_items);
		list = (ListView) findViewById(R.id.listofFinalOrder);
		CustomItemsList adapter = new CustomItemsList(this, minusImage , number, addImage, itemdesc,price );
		list.setAdapter(adapter);
	
	//	list.setScrollContainer(null);
		       	
		}

	
	public void addListenerOnImagePlus() {
		  imageplus = (ImageButton) findViewById(R.id.imgPlus);
		  imageplus.setOnClickListener(new OnClickListener() {

		            @Override
		            public void onClick(View arg0) {

		                Intent intent = new Intent
		                        (getApplicationContext(), LoginActivity.class);
		                    startActivity(intent); 
		            }
		        });
		    }

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.items, menu);
		//  MenuInflater inflater = getMenuInflater();
		 //   inflater.inflate(R.menu.items, menu);
		   
		    ActionBar actionBar = getActionBar();
		    actionBar.setDisplayShowHomeEnabled(false);
		    //addListenerOnButtonLogout();	    
		    //displaying custom ActionBar
		    View mActionBarView = getLayoutInflater().inflate(R.layout.action_bar_items_activity, null);
		    actionBar.setCustomView(mActionBarView);
		    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);    
		    ImageButton ibItem1 = (ImageButton) mActionBarView.findViewById(R.id.btn_menu);
		    ibItem1.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
			ibItem1.setPadding(22, 0, 0, 0);

			StateListDrawable states = new StateListDrawable();
			states.addState(new int[] {android.R.attr.state_pressed},
			    getResources().getDrawable(R.drawable.menu_pressed));
			states.addState(new int[] { },
			    getResources().getDrawable(R.drawable.menu));
			ibItem1.setImageDrawable(states);
			
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
		    
		    return super.onCreateOptionsMenu(menu);
	}

}
