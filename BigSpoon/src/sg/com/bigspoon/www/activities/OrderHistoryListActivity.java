package sg.com.bigspoon.www.activities;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.R.drawable;
import sg.com.bigspoon.www.R.id;
import sg.com.bigspoon.www.R.layout;
import sg.com.bigspoon.www.adapters.CustomListOrderHistoryListAdapter;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.StateListDrawable;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;




public class OrderHistoryListActivity extends Activity {
	
	ImageButton tellUsImage;
	ListView list;
	String[] titles = { "Testinbg!" ,"Testinbg!" ,"Testinbg!" ,"Testinbg!" ,"Testinbg!" ,"Testinbg!"};
	String[] dateDesc = { " 2014/05/05(14 days ago)"," 2014/05/05(14 days ago)"," 2014/05/05(14 days ago)", "2014/05/05(14 days ago)"," 2014/05/05(14 days ago)"," 2014/05/05(14 days ago)"};
	
	
	
	private OnClickListener abc = new OnClickListener() {
		public void onClick(View view) {
			/*Intent intent = new Intent(getApplicationContext(),
					UserReviewActivity.class);
			startActivity(intent);*/
			
			final Intent i = new Intent(android.content.Intent.ACTION_SEND);
            
            i.setType("plain/text");

			i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"jay@bigspoon.sg"});
			i.putExtra(Intent.EXTRA_CC  , new String[]{"leon@bigspoon.sg"});
			i.putExtra(Intent.EXTRA_SUBJECT, "Hello BigSpoon!");
			i.putExtra(Intent.EXTRA_TEXT   , " ");
			try {
				OrderHistoryListActivity.this.startActivity(Intent.createChooser(i, "Send mail..."));
			} catch (android.content.ActivityNotFoundException ex) {
			    Toast.makeText(OrderHistoryListActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
			}
			
			
		}
	};
	
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		// addListenerOnButtonLogout();
		// displaying custom ActionBar
		tellUsImage = (ImageButton) findViewById(R.id.imagetellus);
		tellUsImage.setOnClickListener(abc);
		View mActionBarView = getLayoutInflater().inflate(R.layout.action_bar_items_activity, null);
		actionBar.setCustomView(mActionBarView);

		TextView title = (TextView) mActionBarView.findViewById(R.id.title);
		title.setText("Order History");
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
				// ...

				Intent intent = new Intent(getApplicationContext(),
						MenuPhotoListActivity.class);
				startActivity(intent);
			}
		});

		ImageButton ibItem2 = (ImageButton) mActionBarView
				.findViewById(R.id.order_history);
		ibItem2.setVisibility(View.INVISIBLE);

		return super.onCreateOptionsMenu(menu);
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_history);
		list = (ListView) findViewById(R.id.listoforder);
		CustomListOrderHistoryListAdapter adapter = new CustomListOrderHistoryListAdapter(this, titles,dateDesc);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getApplicationContext(),
						OrderHistoryDetailsActivity.class);
				startActivity(intent);

			}
		});
	}
	}
