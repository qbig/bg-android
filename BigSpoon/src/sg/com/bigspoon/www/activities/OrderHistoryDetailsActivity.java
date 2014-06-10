package sg.com.bigspoon.www.activities;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.R.drawable;
import sg.com.bigspoon.www.R.id;
import sg.com.bigspoon.www.R.layout;
import sg.com.bigspoon.www.adapters.CustomListOfOrderHistoryDetailsAdapter;
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

public class OrderHistoryDetailsActivity extends Activity {

	ListView list;
	String[] number={"1" ,"2", "3"};
	String[] itemDescription = {"		LemonTart" ,"		LemonTart", "		LemonTart" };
	String[] price = {"					$1.0","					$1.0", "					$1.0"};
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_history_details);
		list = (ListView) findViewById(R.id.listoforderDetails);
	CustomListOfOrderHistoryDetailsAdapter adapter = new CustomListOfOrderHistoryDetailsAdapter(this, number,itemDescription,price);
		list.setAdapter(adapter);
		
//		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				Intent intent = new Intent(getApplicationContext(),
//						OrderHistoryListActivity.class);
//				startActivity(intent);
//
//			}
//		});
		
	}


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
		title.setText("Order History ");
		
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

}