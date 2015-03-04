package sg.com.bigspoon.www.activities;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.adapters.MenuAdapter;
import sg.com.bigspoon.www.data.User;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static sg.com.bigspoon.www.data.Constants.MODIFIER_POPUP_REQUEST;
import static sg.com.bigspoon.www.data.Constants.POS_FOR_CLICKED_CATEGORY;

public class MenuActivity extends ActionBarActivity implements TabListener {

	ActionBar mCategoriesTabBar;
	public ListView listview;

	MenuAdapter adapter;
	public static boolean isPhotoMode = true;

	private View mActionBarView;
	private ImageButton toggleButton;
	private ImageButton backToOutletList;
	private ImageButton historyButton;
	private View bottomActionBar;
	private TextView mOrderedDishCounterText;
	private int mCategoryPosition;
	private Handler mHandler;
	private boolean shouldShowTabs;

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_tabs);
		setupBottomActionBar();
        try {
            setupListView();
            setupCategoryTabs();
        } catch (NullPointerException npe){
            Crashlytics.log(npe.getMessage());
            finish();
        }

		setupHideCategoriesTabsEvent();
	}

	private void setupHideCategoriesTabsEvent() {
		shouldShowTabs = false;
		listview.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				final Animation animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
				final Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:
                    try {
                        if (! shouldShowTabs && mActionBarView.getVisibility() == View.VISIBLE) {
                            animFadeOut.setAnimationListener(new AnimationListener() {

                                @Override
                                public void onAnimationStart(Animation animation) {}

                                @Override
                                public void onAnimationRepeat(Animation animation) {}

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    try {
                                        getActionBarViewContainer().startAnimation(animFadeIn);
                                        MenuActivity.this.mCategoriesTabBar.setDisplayShowHomeEnabled(false);
                                        MenuActivity.this.mCategoriesTabBar.setDisplayShowTitleEnabled(false);
                                        MenuActivity.this.mActionBarView.setVisibility(View.GONE);
                                    } catch (NullPointerException npe) {
                                        Crashlytics.log(npe.getMessage());
                                    }
                                }
                            });
                            getActionBarViewContainer().startAnimation(animFadeOut);

                        } else if (shouldShowTabs && mActionBarView.getVisibility() == View.GONE){
                            animFadeOut.setAnimationListener(new AnimationListener() {

                                @Override
                                public void onAnimationStart(Animation animation) {}

                                @Override
                                public void onAnimationRepeat(Animation animation) {}

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    getActionBarViewContainer().startAnimation(animFadeIn);
                                    mCategoriesTabBar.setDisplayShowHomeEnabled(true);
                                    mCategoriesTabBar.setDisplayShowTitleEnabled(true);
                                    mActionBarView.setVisibility(View.VISIBLE);
                                }
                            });
                            getActionBarViewContainer().startAnimation(animFadeOut);

                        }
                    } catch (NullPointerException npe) {
                        Crashlytics.log(npe.getMessage());
                    }

					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem == 0) {
					shouldShowTabs = true;
				} else {
					shouldShowTabs = false;
				}
			}
		});
	}

	private void setupListView() {
		listview = (ListView) findViewById(R.id.list);
		final LayoutInflater inflater = getLayoutInflater();
		final ViewGroup footer = (ViewGroup) inflater.inflate(R.layout.footer, listview, false);
		listview.addFooterView(footer, null, false);
        try {
            adapter = new MenuAdapter(this, User.getInstance(this).currentOutlet);
        } catch (NullPointerException e) {
            Crashlytics.log("currentOutlet is null:" + e.getMessage());
            Intent intent = new Intent(getApplicationContext(), CategoriesListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				isPhotoMode = true;
				getActionBarView().findViewById(R.id.toggleButton).setBackgroundResource(R.drawable.list_icon_new);
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
		final int orderCount = User.getInstance(this).currentSession.getCurrentOrder().getTotalQuantity();
		if (orderCount != 0) {
			mOrderedDishCounterText.setVisibility(View.VISIBLE);
			mOrderedDishCounterText.setText(String.valueOf(orderCount));
		} else {
			mOrderedDishCounterText.setVisibility(View.GONE);
		}
	}

	public View getActionBarViewContainer() {
		return ((ViewGroup)getWindow().
    			findViewById(
				Resources.getSystem().getIdentifier("action_bar_container", "id", "android")));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        try {
            setupActionButton();
            setupBackToOutletButton();
            setupHistoryButton();
            setupToggleButton();
            new Handler();
            return super.onCreateOptionsMenu(menu);
        } catch (NullPointerException npe) {
            Crashlytics.log(npe.getMessage());
            finish();
            return  false;
        }
	}

	private void setupActionButton() {
		mActionBarView = getLayoutInflater().inflate(R.layout.action_bar, null);
		mCategoriesTabBar.setCustomView(mActionBarView);
		mCategoriesTabBar.setIcon(R.drawable.dummy_icon);
		mCategoriesTabBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
		final TextView title = (TextView) mActionBarView.findViewById(R.id.title);
        title.setText(strElipsize(User.getInstance(this).currentOutlet.name, 20));
	}

    private String strElipsize(String str, int lengthLimit) {
        if (str.length() <= lengthLimit) {
            return str;
        } else {
            return str.substring(0, Math.min(str.length(), lengthLimit - 3)) + "...";
        }
    }

	private void setupHistoryButton() {
		historyButton = (ImageButton) mActionBarView.findViewById(R.id.order_history);
		historyButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(), OrderHistoryListActivity.class);
				intent.putExtra("callingActivityName", "MenuPhotoListActivity");
				startActivity(intent);
			}
		});
	}

	private void setupToggleButton() {
		toggleButton = (ImageButton) mActionBarView.findViewById(R.id.toggleButton);
		toggleButton.setBackgroundResource(R.drawable.list_icon_new);
		toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isPhotoMode == true) {
                    isPhotoMode = false;
                    view.setBackgroundResource(R.drawable.photo_icon_new);
                } else {
                    isPhotoMode = true;
                    view.setBackgroundResource(R.drawable.list_icon_new);
                }
                adapter.notifyDataSetChanged();
            }
        });
	}

	private void setupBackToOutletButton() {
		backToOutletList = (ImageButton) mActionBarView.findViewById(R.id.btn_back);
		backToOutletList.setImageResource(R.drawable.home_with_arrow);
		final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
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
				Intent intent = new Intent(getApplicationContext(), CategoriesListActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
	}

    private void setupCategoryTabs() {
        mCategoryPosition = 0;
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mCategoryPosition = extras.getInt(POS_FOR_CLICKED_CATEGORY, 0);
        }
        mCategoriesTabBar = getActionBar();
        if (mCategoriesTabBar != null) {
            mCategoriesTabBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

            for (int i = 0, len = User.getInstance(this).currentOutlet.categoriesDetails.length; i < len; i++) {
                mCategoriesTabBar.addTab(mCategoriesTabBar.newTab()
                        .setText(User.getInstance(this).currentOutlet.categoriesDetails[i].name).setTabListener(this));
            }
            mCategoriesTabBar.setSelectedNavigationItem(mCategoryPosition);
        }
    }


    @Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {

        if (isPhotoMode && User.getInstance(this).currentOutlet.categoriesDetails[tab.getPosition()].isListOnly) {
            isPhotoMode = false;
            toggleButton.setBackgroundResource(R.drawable.photo_icon_new);
        } else if (!isPhotoMode && User.getInstance(this).currentOutlet.categoriesDetails[adapter.mCurrentSelectedCategoryTabIndex].isListOnly && ! User.getInstance(this).currentOutlet.categoriesDetails[tab.getPosition()].isListOnly) {
            isPhotoMode = true;
            toggleButton.setBackgroundResource(R.drawable.list_icon_new);
        }
        adapter.mCurrentSelectedCategoryTabIndex = tab.getPosition();
		adapter.updateFilteredList();
		adapter.notifyDataSetChanged();
		listview.setSelection(0);
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

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

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MODIFIER_POPUP_REQUEST) {
			if (resultCode == RESULT_OK) {
				updateOrderedDishCounter();
			}
		}
	}
}
