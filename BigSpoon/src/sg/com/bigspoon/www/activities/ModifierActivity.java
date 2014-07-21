package sg.com.bigspoon.www.activities;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.adapters.TestSectionedAdapter;
import sg.com.bigspoon.www.data.User;
import za.co.immedia.pinnedheaderlistview.PinnedHeaderListView;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ModifierActivity extends Activity{
	//private ModifierAdapter mAdapter;
    private String itemname;
    private int position;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Bundle extras = getIntent().getExtras();
		//Obtain the parameter passed by MenuPhotoListActivity.java
		if (extras != null) {
			itemname = extras.getString("Item Name");
			position = extras.getInt("Position");
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modifier);
		PinnedHeaderListView listView = (PinnedHeaderListView) findViewById(R.id.pinnedListView);
        LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout footer = (LinearLayout) inflator.inflate(R.layout.modifier_footer, null);
        Button cancel = (Button)footer.findViewById(R.id.cancle);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	finish();
            }
        });
        
        Button okay = (Button)footer.findViewById(R.id.ok);
        okay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setTotalOrder();
                
                finish();
            }
        });
        
        listView.addFooterView(footer);
        TestSectionedAdapter sectionedAdapter = new TestSectionedAdapter();
        listView.setAdapter(sectionedAdapter);
        
	    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.items, menu);
		//  MenuInflater inflater = getMenuInflater();
		 //   inflater.inflate(R.menu.items, menu);
		   
		    ActionBar actionBar = getActionBar();
		    actionBar.setDisplayShowHomeEnabled(false); 
		    View mActionBarView = getLayoutInflater().inflate(R.layout.action_bar_modifier, null);
		    LayoutParams layout = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		    actionBar.setCustomView(mActionBarView,layout);
		    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);    
		    TextView title = (TextView) mActionBarView.findViewById(R.id.title);
		    title.setText(itemname);
		    return super.onCreateOptionsMenu(menu);
	}
	
    public void setTotalOrder(){
		TextView cornertext;
		MenuPhotoListActivity.totalOrderNumber++;
		cornertext=User.getInstance(this).cornerViewHolder;
		cornertext.setVisibility(View.VISIBLE);
		cornertext.setText(String.valueOf(MenuPhotoListActivity.totalOrderNumber));	
		Animation a = AnimationUtils.loadAnimation(getBaseContext(), R.anim.scale_up);
		cornertext.startAnimation(a);
    }

}