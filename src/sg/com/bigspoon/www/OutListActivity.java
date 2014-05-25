package sg.com.bigspoon.www;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

public class OutListActivity extends Activity {

	ImageButton orderButton;
	
		ListView list;
		String[] web = {
			"Welcome to Kith ","Testing !! The Groc.....","Testing !! Strictly Pancakes"				
		} ;
		String[] webdesc = {
				"5 Simon Road Singapore ","81 Upper East Coast Rd ","Infinte Studios , #1-06,21...."				
			} ;
		Integer[] imageId = {
				R.drawable.kith,
				R.drawable.shinkushiya,
				R.drawable.strictlypancakes				
		};
		
		@Override
		  public boolean onCreateOptionsMenu(Menu menu) {
		    MenuInflater inflater = getMenuInflater();
		    inflater.inflate(R.menu.out_list, menu);
		   
		    ActionBar actionBar = getActionBar();
		    actionBar.setDisplayShowHomeEnabled(false);
		    //addListenerOnButtonLogout();	    
		    //displaying custom ActionBar
		    View mActionBarView = getLayoutInflater().inflate(R.layout.action_bar, null);
		    actionBar.setCustomView(mActionBarView);
		    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		    
		    
		    ImageButton ibItem1 = (ImageButton) mActionBarView.findViewById(R.id.btn_logout);
		    ibItem1.setOnClickListener(new View.OnClickListener() {
		        @Override
		        public void onClick(View view) {
		            // ...
		        	
		        	Intent intent = new Intent(getApplicationContext(),
							EntryActivity.class);
					startActivity(intent);
		        }
		    });
		    
		    
		    
		    
		    ImageButton ibItem2 = (ImageButton) mActionBarView.findViewById(R.id.order_history);
		    ibItem2.setOnClickListener(new View.OnClickListener() {
		        @Override
		        public void onClick(View view) {
		            // ...
		        	
		        	Intent intent = new Intent(getApplicationContext(),
		        			ItemsActivity.class);
					startActivity(intent);
		        }
		    });
		    
		    
		    
		    
		    return true;
		  } 
		
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_out_list);
			list=(ListView)findViewById(R.id.list);
			CustomList adapter = new
					CustomList(this, web, imageId,webdesc);
					list.setAdapter(adapter);
					list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			            @Override
			            public void onItemClick(AdapterView<?> parent, View view,
			                                    int position, long id) {
			            	Intent intent = new Intent(getApplicationContext(),
			            			CategoriesListActivity.class);
							startActivity(intent);

			            }
			        });
					
		}
	
				
	}
