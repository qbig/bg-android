package sg.com.bigspoon.www.activities;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.R.drawable;
import sg.com.bigspoon.www.R.id;
import sg.com.bigspoon.www.R.layout;
import sg.com.bigspoon.www.fragments.MenuPhotoFragment;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MenuPhotoListActivity extends ActionBarActivity implements
		TabListener {
	ViewPager viewpager;
	ActionBar actionbar;
	private static final String[] CONTENT = new String[] { "Popular Items",
			"Brunch", "Dinner", "BreakFast", "Beers", "Roasted" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_tabs);
		View v = findViewById(R.id.gv_action_menu);
		v.getBackground().setAlpha(230);
		viewpager = (ViewPager) findViewById(R.id.pager);
		viewpager
				.setAdapter(new GoogleMusicAdapter(getSupportFragmentManager()));
		actionbar = getActionBar();
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		for (int i = 0; i < CONTENT.length; i++) {
			actionbar.addTab(actionbar.newTab().setText(CONTENT[i])
					.setTabListener(this));
		}
		viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				actionbar.setSelectedNavigationItem(position);
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	class GoogleMusicAdapter extends FragmentPagerAdapter {
		public GoogleMusicAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return MenuPhotoFragment.newInstance(CONTENT[position
					% CONTENT.length]);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return CONTENT[position % CONTENT.length];
		}

		@Override
		public int getCount() {
			return CONTENT.length;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.menu_list, menu);
		View mActionBarView = getLayoutInflater().inflate(R.layout.action_bar,
				null);
		actionbar.setCustomView(mActionBarView);
		actionbar.setIcon(R.drawable.dummy_icon);
		actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME);
		TextView title = (TextView) mActionBarView.findViewById(R.id.title);
		title.setText("Menu List");
		ImageButton togglebutton = (ImageButton) mActionBarView
				.findViewById(R.id.toggleButton);
		togglebutton.setBackgroundResource(R.drawable.list_icon);
		ImageButton ibItem1 = (ImageButton) mActionBarView
				.findViewById(R.id.btn_logout);
		ibItem1.setImageResource(R.drawable.home_with_arrow);
		RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	    params.addRule(RelativeLayout.CENTER_VERTICAL);
		ibItem1.setLayoutParams(params);
	    ibItem1.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
		ibItem1.setPadding(-2, 0, 0, 0);
		
		
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

		togglebutton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// ...

				Intent intent = new Intent(getApplicationContext(),
						MenuTextListActivity.class);
				startActivity(intent);
			}
		});
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		viewpager.setCurrentItem(tab.getPosition());

	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}
	@Override
	protected void onResume() {
		super.onResume();
		loadMenu();
	}

}
