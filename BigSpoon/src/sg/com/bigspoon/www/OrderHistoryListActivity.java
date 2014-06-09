package sg.com.bigspoon.www;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class OrderHistoryListActivity extends Activity {
	
	ListView list;
	String[] titles = { "Testinbg!" ,"Testinbg!" ,"Testinbg!" ,"Testinbg!" ,"Testinbg!" ,"Testinbg!"};
	String[] dateDesc = { " 2014/05/05(14 days ago)"," 2014/05/05(14 days ago)"," 2014/05/05(14 days ago)", "2014/05/05(14 days ago)"," 2014/05/05(14 days ago)"," 2014/05/05(14 days ago)"};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		// addListenerOnButtonLogout();
		// displaying custom ActionBar
		View mActionBarView = getLayoutInflater().inflate(R.layout.action_bar,
				null);
		actionBar.setCustomView(mActionBarView);

		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		TextView title = (TextView) mActionBarView.findViewById(R.id.title);
		title.setText("Order History");
		
		ImageButton ibItem1 = (ImageButton) mActionBarView
				.findViewById(R.id.btn_logout);
		ImageButton togglebutton = (ImageButton) mActionBarView
				.findViewById(R.id.toggleButton);
		togglebutton.setVisibility(View.GONE);
		ibItem1.setBackgroundResource(R.drawable.logout_button);
		ibItem1.setLayoutParams(new RelativeLayout.LayoutParams(150, 50));
		ibItem1.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
		ibItem1.setPadding(0, 50, 50, 0);
		ibItem1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// ...

				Intent intent = new Intent(getApplicationContext(),
						EntryActivity.class);
				startActivity(intent);
			}
		});

		ImageButton ibItem2 = (ImageButton) mActionBarView
				.findViewById(R.id.order_history);
		ibItem2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// ...

				Intent intent = new Intent(getApplicationContext(),
						ItemsActivity.class);
				startActivity(intent);
			}
		});
		return super.onCreateOptionsMenu(menu);
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_history);
		list = (ListView) findViewById(R.id.listoforder);
		CustomListOrderHistoryList adapter = new CustomListOrderHistoryList(this, titles,dateDesc);
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
