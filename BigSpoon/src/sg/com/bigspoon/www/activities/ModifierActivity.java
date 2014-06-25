package sg.com.bigspoon.www.activities;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.adapters.ModifierAdapter;
import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;

public class ModifierActivity extends ListActivity {
	private ModifierAdapter mAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modifier);
		 mAdapter = new ModifierAdapter(this);
	        for (int i = 1; i < 30; i++) {
	            mAdapter.addItem("Row Item #" + i);
	            if (i % 4 == 0) {
	                mAdapter.addSectionHeaderItem("Section #" + i);
	            }
	        }
	        setListAdapter(mAdapter);
	    }

}
