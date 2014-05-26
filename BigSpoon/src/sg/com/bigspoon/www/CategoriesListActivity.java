package sg.com.bigspoon.www;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class CategoriesListActivity extends Activity implements AdapterView.OnItemClickListener{

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
		getMenuInflater().inflate(R.menu.categories_list, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent i = new Intent(getApplicationContext(), MenuListActivity.class);
        startActivity(i);
        finish();
	}

}
