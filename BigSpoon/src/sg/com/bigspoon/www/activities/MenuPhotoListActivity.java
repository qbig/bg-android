package sg.com.bigspoon.www.activities;

import java.util.ArrayList;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.data.User;
import sg.com.bigspoon.www.adapters.MenuListViewAdapter;
import static sg.com.bigspoon.www.data.Constants.POS_FOR_CLICKED_CATEGORY;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MenuPhotoListActivity extends ActionBarActivity implements TabListener {

	ActionBar actionbar;
	ListView listview;
	MenuListViewAdapter adapter;
	public static boolean isPhotoMode = true;
	public static int totalOrderNumber = 0;

	

	private View mActionBarView;
	private ImageButton togglebutton;
	private ImageButton backToOutletList;
	private ImageButton historyButton;
	private View bottomActionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_tabs);
		setupBottomActionBar();
		setupOrderedDishCounter();
		setupListView();
		setupCategoryTabs();
		adapter.notifyDataSetChanged();
	}

	private void setupListView() {
		adapter = new MenuListViewAdapter(this, User.getInstance(this).currentOutlet);
		listview = (ListView) findViewById(R.id.list);
		final LayoutInflater inflater = getLayoutInflater();
		final ViewGroup footer = (ViewGroup) inflater.inflate(R.layout.footer, listview, false);
		listview.addFooterView(footer, null, false);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				isPhotoMode = true;
				getActionBarView().findViewById(R.id.toggleButton).setBackgroundResource(R.drawable.list_icon);
				adapter.notifyDataSetChanged();
				listview.setSelection(position);
			}
		});
	}

	private void setupBottomActionBar() {
		bottomActionBar = findViewById(R.id.gv_action_menu);
		bottomActionBar.getBackground().setAlpha(230);
	}

	private void setupOrderedDishCounter() {
		final TextView orderedDishCounterText = (TextView) findViewById(R.id.corner);
		if (totalOrderNumber != 0) {
			orderedDishCounterText.setVisibility(View.VISIBLE);
			orderedDishCounterText.setText(String.valueOf(totalOrderNumber));
		}
	}

	private void setupCategoryTabs() {
		int initialCategoryPosition = 0;
		final Bundle extras = getIntent().getExtras();
		if (extras != null) {
			initialCategoryPosition = extras.getInt(POS_FOR_CLICKED_CATEGORY, 0);
		}
		actionbar = getActionBar();
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		for (int i = 0, len = User.getInstance(this).currentOutlet.categoriesDetails.length; i < len; i++) {
			actionbar.addTab(actionbar.newTab().setText(User.getInstance(this).currentOutlet.categoriesDetails[i].name).setTabListener(this));
		}

		actionbar.setSelectedNavigationItem(initialCategoryPosition);
	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		setupActionButton();
		setupBackToOutletButton();
		setupHistoryButton();
		setupToggleButton();
		
		return super.onCreateOptionsMenu(menu);
	}

	private void setupActionButton() {
		mActionBarView = getLayoutInflater().inflate(R.layout.action_bar, null);
		actionbar.setCustomView(mActionBarView);
		actionbar.setIcon(R.drawable.dummy_icon);
		actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
		final TextView title = (TextView) mActionBarView.findViewById(R.id.title);
		title.setText(R.string.menu_title);
	}

	private void setupHistoryButton() {
		historyButton = (ImageButton) mActionBarView.findViewById(R.id.order_history);
		historyButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(), OrderHistoryListActivity.class);
				startActivity(intent);
			}
		});
	}

	private void setupToggleButton() {
		togglebutton = (ImageButton) mActionBarView.findViewById(R.id.toggleButton);
		togglebutton.setBackgroundResource(R.drawable.list_icon);
		togglebutton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				if (isPhotoMode == true) {
					isPhotoMode = false;
					view.setBackgroundResource(R.drawable.photo_icon);
				} else {
					isPhotoMode = true;
					view.setBackgroundResource(R.drawable.list_icon);
				}
				adapter.notifyDataSetChanged();
			}
		});
	}

	private void setupBackToOutletButton() {
		backToOutletList = (ImageButton) mActionBarView.findViewById(R.id.btn_back);
		backToOutletList.setImageResource(R.drawable.home_with_arrow);
		final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		backToOutletList.setLayoutParams(params);
		backToOutletList.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
		backToOutletList.setPadding(-2, 0, 0, 0);

		final StateListDrawable states = new StateListDrawable();
		states.addState(new int[] { android.R.attr.state_pressed },
				getResources().getDrawable(R.drawable.home_with_arrow_pressed));
		states.addState(new int[] {}, getResources().getDrawable(R.drawable.home_with_arrow));
		backToOutletList.setImageDrawable(states);

		backToOutletList.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(), OutletListActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});
	}

	
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		adapter.currentSelectedCategoryTabIndex = tab.getPosition();
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {}
	
	@Override
	protected void onResume() {
		super.onResume();
		loadMenu();
	}

	public class ListItem {
		private Drawable image;
		private String itemnames, itemdesc, itemprice;

		public Drawable getImage() {
			return image;
		}

		public void setImage(Drawable image) {
			this.image = image;
		}

		public String getItemName() {
			return itemnames;
		}

		public void setItemname(String string) {
			itemnames = string;
		}

		public String getItemDesc() {
			return itemdesc;
		}

		public void setItemDesc(String string) {
			itemdesc = string;
		}

		public String getItemPrice() {
			return itemprice;
		}

		public void setItemPrice(String string) {
			itemprice = string;
		}
	}

	public class ListTextItem {
		private String itemnames, itemdesc, itemprice;

		public String getItemName() {
			return itemnames;
		}

		public void setItemname(String string) {
			itemnames = string;
		}

		public String getItemDesc() {
			return itemdesc;
		}

		public void setItemDesc(String string) {
			itemdesc = string;
		}

		public String getItemPrice() {
			return itemprice;
		}

		public void setItemPrice(String string) {
			itemprice = string;
		}

	}

	public View getActionBarView() {
		final Window window = getWindow();
		final View v = window.getDecorView();
		final int resId = getResources().getIdentifier("action_bar_container", "id", "android");
		return v.findViewById(resId);
	}
}
