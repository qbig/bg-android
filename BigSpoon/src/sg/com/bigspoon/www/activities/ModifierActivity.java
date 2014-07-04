package sg.com.bigspoon.www.activities;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.adapters.ModifierAdapter;
import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;

public class ModifierActivity extends ListActivity {
	private ModifierAdapter mAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 mAdapter = new ModifierAdapter(this);
	        for (int i = 1; i < 30; i++) {
	        	
	        	if(i == 2 || i==3 || i==4 || i==6 || i==7 || i==8 || i==9)
	        	{
	        		mAdapter.addRadiobuttonItem("Item name #" + i);
	        	}
	        	if(i==9||i==10)
	        	{
	        		mAdapter.addItem("Row Item #" + i);
	        	}
	            if (i == 1 || i==5 || i==10) {
	                mAdapter.addSectionHeaderItem("Section #" + i);
	            }
	            	
	            
	        }
	        setListAdapter(mAdapter);
	    }

}
