package sg.com.bigspoon.www.activities;

import static sg.com.bigspoon.www.data.Constants.POS_FOR_CLICKED_CATEGORY;
import static sg.com.bigspoon.www.data.Constants.MODIFIER_POPUP_REQUEST;
import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.adapters.MenuListViewAdapter;
import sg.com.bigspoon.www.data.User;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
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
	private TextView mOrderedDishCounterText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_tabs);
		setupBottomActionBar();		
		setupListView();
		setupCategoryTabs();
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
		mOrderedDishCounterText = (TextView) findViewById(R.id.corner);
	}

	private void updateOrderedDishCounter() {
		final int orderCount = User.getInstance(this).currentSession.currentOrder.getTotalQuantity();
		if (orderCount != 0) {
			mOrderedDishCounterText.setVisibility(View.VISIBLE);
			mOrderedDishCounterText.setText(String.valueOf(orderCount));
		} else {
			mOrderedDishCounterText.setVisibility(View.GONE);
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
		adapter.mCurrentSelectedCategoryTabIndex = tab.getPosition();
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
		updateOrderedDishCounter();
	}

	public View getActionBarView() {
		final Window window = getWindow();
		final View v = window.getDecorView();
		final int resId = getResources().getIdentifier("action_bar_container", "id", "android");
		return v.findViewById(resId);
	}
	
	  protected void onActivityResult(int requestCode, int resultCode,
	             Intent data) {
	         if (requestCode == MODIFIER_POPUP_REQUEST) {
	             if (resultCode == RESULT_OK) {
	            	 updateOrderedDishCounter();
	             } 
	         }
	     }
}
