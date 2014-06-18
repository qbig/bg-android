package sg.com.bigspoon.www.activities;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.R.drawable;
import sg.com.bigspoon.www.R.id;
import sg.com.bigspoon.www.R.layout;
import sg.com.bigspoon.www.adapters.CategoriesCustomAdapter;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CategoriesListActivity extends Activity implements
		AdapterView.OnItemClickListener {

	ListView catrgoriesList;
	String[] categories = { "Popular Items", "Brunch", "Dinner", "BreakFast",
			"Beers", "Roasted" };
	int[] images = { R.drawable.fb_1, R.drawable.fb_2, R.drawable.fb_3,
			R.drawable.fb_4, R.drawable.fb_5, R.drawable.fb_6 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_categories_list);

		catrgoriesList = (ListView) findViewById(R.id.list);

		CategoriesCustomAdapter categoriesCustomAdapter = new CategoriesCustomAdapter(
				this, categories, images);
		catrgoriesList.setAdapter(categoriesCustomAdapter);
		catrgoriesList.setOnItemClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.categories_list, menu);
		// MenuInflater inflater = getMenuInflater();
		// inflater.inflate(R.menu.action_bar, menu);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		// addListenerOnButtonLogout();
		// displaying custom ActionBar
		View mActionBarView = getLayoutInflater().inflate(R.layout.action_bar,
				null);
		actionBar.setCustomView(mActionBarView);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		TextView title = (TextView) mActionBarView.findViewById(R.id.title);
		title.setText("Categories");
		ImageButton togglebutton = (ImageButton) mActionBarView
				.findViewById(R.id.toggleButton);
		togglebutton.setVisibility(View.GONE);
		ImageButton ibItem1 = (ImageButton) mActionBarView
				.findViewById(R.id.btn_logout);
		ibItem1.setImageResource(R.drawable.home_with_arrow);
		RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	    params.addRule(RelativeLayout.CENTER_VERTICAL);
		ibItem1.setLayoutParams(params);
	    ibItem1.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
		ibItem1.setPadding(22, 0, 0, 0);
		
		StateListDrawable states = new StateListDrawable();
		states.addState(new int[] {android.R.attr.state_pressed},
		    getResources().getDrawable(R.drawable.home_with_arrow_pressed));
		states.addState(new int[] { },
		    getResources().getDrawable(R.drawable.home_with_arrow));
		ibItem1.setImageDrawable(states);
		
		
		
		ibItem1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// ...

				Intent intent = new Intent(getApplicationContext(),
						OutListActivity.class);
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
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent i = new Intent(getApplicationContext(),
				MenuPhotoListActivity.class);
		startActivity(i);
		finish();
	}

}
