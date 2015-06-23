package sg.com.bigspoon.www.activities;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.OnClickWrapper;
import com.koushikdutta.ion.Ion;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.adapters.MenuSearchSuggestionAdapter;
import sg.com.bigspoon.www.data.DishModel;
import sg.com.bigspoon.www.data.OutletDetailsModel;
import sg.com.bigspoon.www.data.User;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static sg.com.bigspoon.www.data.Constants.BASE_URL;
import static sg.com.bigspoon.www.data.Constants.MODIFIER_POPUP_DISH_ID;
import static sg.com.bigspoon.www.data.Constants.MODIFIER_POPUP_REQUEST;
import static sg.com.bigspoon.www.data.Constants.MODIFIER_REULST_OK;
import static sg.com.bigspoon.www.data.Constants.NOTIF_MODIFIER_OK;
import static sg.com.bigspoon.www.data.Constants.NOTIF_UNDO_ORDER;


public class MenuActivity extends ActionBarActivity implements TabListener {

	ActionBar mCategoriesTabBar;
	public RecyclerView listview;
    private EditText mSearchField;
	MenuAdapter adapter;
	public static boolean isPhotoMode = true;

	private View mActionBarView;
    private SearchView mSearchView;
	private ImageButton toggleButton;
	private ImageButton backToOutletList;
	private ImageButton historyButton;
	private View bottomActionBar;
	private TextView mOrderedDishCounterText;
	private int mCategoryPosition;
	public Handler mHandler;
	private boolean shouldShowTabs;
    private Drawable outOfStockBackground;
    private ImageView mMagIcon;
    private SuperActivityToast mSuperActivityToast;
    private View mClickedViewToAnimate;
    private int mClickedPos;
    private static final float PHOTO_ITEM_HEIGHT = 242;
    private static final float TEXT_ITEM_HEIGHT = 142;
    private static final long DURATION_LONG = 1000;
    private static final long DURATION_SHORT = 500;
    private TextView mCornerText;

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private BroadcastReceiver mUpdateCornerCounterReceiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MenuActivity.this.updateOrderedDishCounter();
        }
    };

    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_tabs);
        setupBottomActionBar();
        mHandler = new Handler();
        this.outOfStockBackground = getResources().getDrawable(R.drawable.out_of_stock);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.tabanim_toolbar);
        mCornerText = (TextView) findViewById(R.id.corner);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.tabanim_viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabanim_tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());

                switch (tab.getPosition()) {
                    case 0:
                        showToast("One");
                        break;
                    case 1:
                        showToast("Two");
                        break;
                    case 2:
                        showToast("Three");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        try {
            //setupListView();
            //setupCategoryTabs();
        } catch (NullPointerException npe) {
            Crashlytics.log(npe.getMessage());
            finish();
        }

        setupHideCategoriesTabsEvent();
        LocalBroadcastManager.getInstance(this).registerReceiver(mUpdateCornerCounterReceiver,
                new IntentFilter(NOTIF_UNDO_ORDER));

        mSuperActivityToast = new SuperActivityToast(this,
                SuperToast.Type.STANDARD);
        mSuperActivityToast.setText("Saved to 'Unsent Order'. Tab 'Orders' to view.");
        mSuperActivityToast.setTextSize(SuperToast.TextSize.LARGE);
        mSuperActivityToast.setAnimations(SuperToast.Animations.POPUP);
        mSuperActivityToast.setDuration(SuperToast.Duration.EXTRA_LONG);
        mSuperActivityToast.setBackground(SuperToast.Background.ORANGE);
        mSuperActivityToast.setOnClickWrapper(
                new OnClickWrapper("superactivitytoast",
                        new SuperToast.OnClickListener() {
                            @Override
                            public void onClick(View view, Parcelable token) {
                                mSuperActivityToast.dismiss();
                            }
                        }
                )
        );
        mSuperActivityToast.setIcon(SuperToast.Icon.Dark.INFO, SuperToast.IconPosition.LEFT);

	}

    void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void setupHideCategoriesTabsEvent() {
		shouldShowTabs = false;
//		listview.setOnScrollListener(new OnScrollListener() {
//
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                final Animation animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
//                final Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
//                switch (scrollState) {
//                    case OnScrollListener.SCROLL_STATE_IDLE:
//                        try {
//                            if (!shouldShowTabs && mActionBarView.getVisibility() == View.VISIBLE) {
//                                animFadeOut.setAnimationListener(new AnimationListener() {
//
//                                    @Override
//                                    public void onAnimationStart(Animation animation) {
//                                    }
//
//                                    @Override
//                                    public void onAnimationRepeat(Animation animation) {
//                                    }
//
//                                    @Override
//                                    public void onAnimationEnd(Animation animation) {
//                                        try {
//                                            getActionBarViewContainer().startAnimation(animFadeIn);
//                                            MenuActivity.this.mActionBarView.setVisibility(View.GONE);
//                                        } catch (NullPointerException npe) {
//                                            Crashlytics.log(npe.getMessage());
//                                        }
//                                    }
//                                });
//                                getActionBarViewContainer().startAnimation(animFadeOut);
//
//                            } else if (shouldShowTabs && mActionBarView.getVisibility() == View.GONE) {
//                                animFadeOut.setAnimationListener(new AnimationListener() {
//
//                                    @Override
//                                    public void onAnimationStart(Animation animation) {
//                                    }
//
//                                    @Override
//                                    public void onAnimationRepeat(Animation animation) {
//                                    }
//
//                                    @Override
//                                    public void onAnimationEnd(Animation animation) {
//                                        getActionBarViewContainer().startAnimation(animFadeIn);
//                                        mActionBarView.setVisibility(View.VISIBLE);
//                                    }
//                                });
//                                getActionBarViewContainer().startAnimation(animFadeOut);
//
//                            }
//                        } catch (NullPointerException npe) {
//                            Crashlytics.log(npe.getMessage());
//                        }
//
//                        break;
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                if (firstVisibleItem == 0) {
//                    shouldShowTabs = true;
//                } else {
//                    shouldShowTabs = false;
//                }
//            }
//        });
	}


    private void setupViewPager(ViewPager viewPager) {
        //listview = (RecyclerView) findViewById(R.id.list);
        //final LayoutInflater inflater = getLayoutInflater();
        //listview.setLayoutManager(new LinearLayoutManager(this));
        //final ViewGroup footer = (ViewGroup) inflater.inflate(R.layout.footer, listview, false);
        //listview.addFooterView(footer, null, false);
        final MenuTabPagerAdapter fragAdapter = new MenuTabPagerAdapter(getSupportFragmentManager());

        try {
            for (int i = 0, len = User.getInstance(this).currentOutlet.categoriesDetails.length; i < len; i++) {
                MenuAdapter adapter = new MenuAdapter(this, User.getInstance(this).currentOutlet, i);
                fragAdapter.addFrag(new MenuTabFragment(User.getInstance(this).currentOutlet.categoriesDetails[i].name, adapter),User.getInstance(this).currentOutlet.categoriesDetails[i].name);
            }
            viewPager.setAdapter(fragAdapter);
        } catch (NullPointerException e) {
            Crashlytics.log("currentOutlet is null:" + e.getMessage());
            Intent intent = new Intent(getApplicationContext(), CategoriesListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

//      listview.setAdapter(adapter);
//      listview.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                isPhotoMode = true;
//                getActionBarView().findViewById(R.id.toggleButton).setBackgroundResource(R.drawable.list_icon_new);
//                adapter.notifyDataSetChanged();
//                listview.setSelection(position);
//            }
//        });

    }

//	private void setupListView() {
//		listview = (RecyclerView) findViewById(R.id.list);
//		final LayoutInflater inflater = getLayoutInflater();
//        listview.setLayoutManager(new LinearLayoutManager(this));
//		//final ViewGroup footer = (ViewGroup) inflater.inflate(R.layout.footer, listview, false);
//		//listview.addFooterView(footer, null, false);
//        try {
//            adapter = new MenuAdapter(this, User.getInstance(this).currentOutlet);
//        } catch (NullPointerException e) {
//            Crashlytics.log("currentOutlet is null:" + e.getMessage());
//            Intent intent = new Intent(getApplicationContext(), CategoriesListActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//        }
//
//		listview.setAdapter(adapter);
////		listview.setOnItemClickListener(new OnItemClickListener() {
////            @Override
////            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////
////                isPhotoMode = true;
////                getActionBarView().findViewById(R.id.toggleButton).setBackgroundResource(R.drawable.list_icon_new);
////                adapter.notifyDataSetChanged();
////                listview.setSelection(position);
////            }
////        });
//
//	}

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
            final Animation a = AnimationUtils.loadAnimation(this, R.anim.scale_up);
            mOrderedDishCounterText.startAnimation(a);
		} else {
            mOrderedDishCounterText.clearAnimation();
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
            //setupActionButton();
            return super.onCreateOptionsMenu(menu);
        } catch (NullPointerException npe) {
            Crashlytics.log(npe.getMessage());
            finish();
            return  false;
        }
	}

	private void setupActionButton() {
		mActionBarView = getLayoutInflater().inflate(R.layout.menu_action_bar, null);
		mCategoriesTabBar.setCustomView(mActionBarView);
		mCategoriesTabBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView =
                (SearchView) mActionBarView.findViewById(R.id.menu_search_view);
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryRefinementEnabled(true);

        mMagIcon = (ImageView) mSearchView.findViewById(this.getResources().getIdentifier("android:id/search_mag_icon", null, null));
        mMagIcon.setImageResource(R.drawable.magnifier_icon_30);
        final ImageView voiceIcon = (ImageView) mSearchView.findViewById(this.getResources().getIdentifier("android:id/search_voice_btn", null, null));
        voiceIcon.setImageResource(R.drawable.abc_ic_voice_search_api_mtrl_alpha);
        final ImageView closeIcon = (ImageView) mSearchView.findViewById(this.getResources().getIdentifier("android:id/search_close_btn", null, null));
        closeIcon.setImageResource(R.drawable.close_icon_white_20);
        final ImageView goIcon = (ImageView) mSearchView.findViewById(this.getResources().getIdentifier("android:id/search_go_btn", null, null));
        goIcon.setImageResource(R.drawable.abc_ic_go_search_api_mtrl_alpha);

        mSearchField = (EditText) mSearchView.findViewById(this.getResources().getIdentifier("android:id/search_src_text", null, null));
        mSearchField.setTextColor(this.getResources().getColor(R.color.BigSpoonLightGray));
        mSearchField.setTextAppearance(this, R.style.FontProxiRegular);
        mSearchField.setTextSize(15);

        mSearchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setOnQueryTextListener(new OnQueryTextListener() {


            @Override
            public boolean onQueryTextChange(String query) {
                mSearchField.post(new Runnable() {
                    @Override
                    public void run() {
                        mSearchField.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(mSearchField, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
                searchDishAndLoadResult(query);
                return true;

            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchView.clearFocus();
                final String firstSuggestionString = ((Cursor) ((CursorAdapter) mSearchView.getSuggestionsAdapter()).getItem(0)).getString(1);
                displayDish(firstSuggestionString);
                return true;
            }

        });

        mSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                User.getInstance(MenuActivity.this).mMixpanel.track("Search Suggestion Selected", null);
                Cursor cursor = (Cursor) mSearchView.getSuggestionsAdapter().getItem(position);
                String feedName = cursor.getString(1);
                mSearchView.setQuery(feedName, false);
                mSearchView.clearFocus();
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                User.getInstance(MenuActivity.this).mMixpanel.track("Search Suggestion Selected", null);
                Cursor cursor = (Cursor) mSearchView.getSuggestionsAdapter().getItem(position);
                String feedName = cursor.getString(1);
                mSearchView.setQuery(feedName, false);
                mSearchView.clearFocus();
                displayDish(feedName);

                return true;
            }
        });

        mSearchField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    User.getInstance(MenuActivity.this).mMixpanel.track("Dish Search Start", null);
                }
            }
        });
        mSearchField.setHint(R.string.dish_search_hint);
        mSearchField.setHintTextColor(getResources().getColor(R.color.light_gray));
        int searchBarId = mSearchView.getContext().getResources().getIdentifier("android:id/search_bar", null, null);
        LinearLayout searchBar = (LinearLayout) mSearchView.findViewById(searchBarId);
        searchBar.setLayoutTransition(new LayoutTransition());

    }

    private void displayDish(String name){
        DishModel searchedDish = User.getInstance(MenuActivity.this).currentOutlet.getDishWithName(name);
        mCategoryPosition = User.getInstance(MenuActivity.this).currentOutlet.getCategoryPositionWithDishId(searchedDish.id);
        mCategoriesTabBar.setSelectedNavigationItem(mCategoryPosition);
        adapter.mCurrentSelectedCategoryTabIndex = mCategoryPosition;
        adapter.updateFilteredList();
        adapter.notifyDataSetChanged();
        //listview.setSelection(adapter.getDishPositionInFilteredList(searchedDish.id));
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

    private void setupCategoryTabs() {

//        mCategoryPosition = 0;
//        final Bundle extras = getIntent().getExtras();
//        if (extras != null) {
//            mCategoryPosition = extras.getInt(POS_FOR_CLICKED_CATEGORY, 0);
//        }
//        mCategoriesTabBar = getSupportActionBar();
//        if (mCategoriesTabBar != null) {
//            mCategoriesTabBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//
//            for (int i = 0, len = User.getInstance(this).currentOutlet.categoriesDetails.length; i < len; i++) {
//                mCategoriesTabBar.addTab(mCategoriesTabBar.newTab()
//                        .setText(User.getInstance(this).currentOutlet.categoriesDetails[i].name).setTabListener(this));
//            }
//            mCategoriesTabBar.setSelectedNavigationItem(mCategoryPosition);
//        }
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
		//listview.setSelection(0);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        User.getInstance(MenuActivity.this).mMixpanel.flush();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mUpdateCornerCounterReceiver);
    }

	public View getActionBarView() {
		final Window window = getWindow();
		final View v = window.getDecorView();
		final int resId = getResources().getIdentifier("action_bar_container", "id", "android");
        return v.findViewById(resId);
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MODIFIER_POPUP_REQUEST) {
			if (resultCode == MODIFIER_REULST_OK) {
				updateOrderedDishCounter();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(NOTIF_MODIFIER_OK);
                        LocalBroadcastManager.getInstance(MenuActivity.this).sendBroadcast(intent);
                    }
                }, 500);
			}
		}
	}

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            User.getInstance(MenuActivity.this).mMixpanel.track("Search Go Pressed", null);
            mSearchView.setQuery(query, false);
        }
    }

    private void searchDishAndLoadResult(final String query) {
        // Cursor
        final String[] columns = new String[] { "_id", "text" };
        final Object[] temp = new Object[] { 0, "default" };
        final String queryLow = query.toLowerCase();
        MatrixCursor cursor = new MatrixCursor(columns);

        Arrays.sort(User.getInstance(this).currentOutlet.dishes, new Comparator<DishModel>() {
            @Override
            public int compare(final DishModel lhs, DishModel rhs) {

                final boolean lhsStartMatch = lhs.name.toLowerCase().contains(queryLow);
                final boolean rhsStartMatch = rhs.name.toLowerCase().contains(queryLow);
                if (query.length() <= 10 ) {
                    if (lhsStartMatch && !rhsStartMatch) return -1;
                    else if (!lhsStartMatch && rhsStartMatch) return 1;
                }

                final int levValueLeft = StringUtils.getLevenshteinDistance(queryLow, lhs.name.toLowerCase());
                final int levValueRight = StringUtils.getLevenshteinDistance(queryLow, rhs.name.toLowerCase());
                if (levValueLeft < levValueRight) return -1;
                else if (levValueLeft > levValueRight) return 1;
                return 0;
            }
        });

        for(int i = 0; i < User.getInstance(this).currentOutlet.dishes.length; i++) {

            temp[0] = i;
            temp[1] = User.getInstance(this).currentOutlet.dishes[i].name;
            cursor.addRow(temp);

        }

        mSearchView.setSuggestionsAdapter(new MenuSearchSuggestionAdapter(this, cursor, User.getInstance(this).currentOutlet.dishes));
    }


    private class ListPhotoItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView, overlay;
        TextView textItemPrice, textItemName, textItemDesc;
        ImageButton imageAddButton;
        private static final String DEFAULT_DISH_PHOTO_URL = "default.jpg";
        private DishModel mDish;
        private int mPosition;

        public ListPhotoItemViewHolder(View itemView, int position) {
            super(itemView);
            mPosition = position;
            imageView = (ImageView) itemView.findViewById(R.id.menuitem);
            overlay = (ImageView) itemView.findViewById(R.id.overlay);
            textItemDesc = (TextView) itemView.findViewById(R.id.itemdesc);
            textItemPrice = (TextView) itemView.findViewById(R.id.textitemprice);
            textItemPrice.bringToFront();
            textItemName = (TextView) itemView.findViewById(R.id.textitemname);
            imageAddButton = (ImageButton) itemView.findViewById(R.id.addbutton);

            itemView.setOnClickListener(this);
        }

        public void bindDish(DishModel dish) {
            mDish = dish;
            if (mDish.photo.thumbnailLarge.contains(DEFAULT_DISH_PHOTO_URL)) {
                Ion.with(MenuActivity.this).load(BASE_URL + "media/" + User.getInstance(MenuActivity.this).currentOutlet.defaultDishPhoto)
                        .intoImageView(imageView);
            } else {
                Ion.with(MenuActivity.this).load(BASE_URL + mDish.photo.thumbnailLarge)
                        .intoImageView(imageView);
            }

            if (mDish.quantity <= 0) {
                overlay.setBackgroundDrawable(MenuActivity.this.outOfStockBackground);
                overlay.setVisibility(View.VISIBLE);
            } else {
                overlay.setBackgroundResource(0);
                overlay.setVisibility(View.GONE);
            }

            textItemName.setText(mDish.name);
            textItemDesc.setText(mDish.description);
            textItemPrice.setText(mDish.price + "");
            imageAddButton.setTag(mPosition);
            if (mDish.isDummyDish()) {
                imageAddButton.setVisibility(View.GONE);
                textItemPrice.setVisibility(View.GONE);
            } else {
                imageAddButton.setVisibility(View.VISIBLE);
                textItemPrice.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            if (mDish == null) {
                return;
            }

            mSuperActivityToast.dismiss();
            if (!mDish.isServedNow()) {
                AlertDialog alertLocationFail = new AlertDialog.Builder(MenuActivity.this).create();
                alertLocationFail.setTitle("Sorry");
                alertLocationFail.setMessage("This dish is only available from " + mDish.startTime + " to "
                        + mDish.endTime);
                alertLocationFail.setView(null);
                alertLocationFail.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
                alertLocationFail.show();

                return;
            }

            if (mDish.quantity == 0) {
                AlertDialog alertLocationFail = new AlertDialog.Builder(MenuActivity.this).create();
                alertLocationFail.setTitle("Sorry");
                alertLocationFail.setMessage("This is out of stock :(");
                alertLocationFail.setView(null);
                alertLocationFail.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
                alertLocationFail.show();
                return;
            }

            if (mDish.customizable) {
                final Intent intentForModifier = new Intent(MenuActivity.this, ModifierActivity.class);
                mClickedViewToAnimate = v;
                mClickedPos = mPosition;
                intentForModifier.putExtra(MODIFIER_POPUP_DISH_ID, mDish.id);
                MenuActivity.this
                        .startActivityForResult(intentForModifier, MODIFIER_POPUP_REQUEST);
            } else {

                User.getInstance(MenuActivity.this).currentSession.getCurrentOrder().addDish(mDish);
                User.getInstance(MenuActivity.this).showUndoDishPopup();
                updateOrderCountAndDisplay(v);

                if (User.getInstance(MenuActivity.this).currentSession.getCurrentOrder().getTotalQuantity() <= 3) {
                    //mSuperActivityToast.show();
                    if (User.getInstance(MenuActivity.this).currentSession.getCurrentOrder().getTotalQuantity() == 1 && User.getInstance(MenuActivity.this).currentSession.getPastOrder().getTotalQuantity() != 0) {
                        showClearOrderPopup();
                    }
                }

                if (MenuActivity.isPhotoMode) {
                    animatePhotoItemToCorner(v, mPosition, DURATION_SHORT);
                } else {
                    animateTextItemToCorner(v, mPosition, DURATION_SHORT);
                }
            }
        }
    }

    private class ListTextItemViewHolder {
        TextView textItemPrice, textItemName, textItemDesc;
        ImageButton imageAddButton;
    }

    private void updateOrderCountAndDisplay(View viewClicked) {
        mCornerText.setVisibility(View.VISIBLE);
        mCornerText.setText(String.valueOf(User.getInstance(this).currentSession.getCurrentOrder()
                .getTotalQuantity()));
        Animation a = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        mCornerText.startAnimation(a);
    }


    @SuppressWarnings("deprecation")
    private void showClearOrderPopup() {
        final AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setTitle("Just Arrived?");
        alert.setMessage(
                "We found an existing order. If it belongs " +
                        "to you, tap continue. Otherwise tap Start new session.");
        alert.setView(null);
        alert.setButton2("Start new session", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                User.getInstance(MenuActivity.this).clearPastOrder();
            }
        });
        alert.setButton("Continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();
        TextView messageView = (TextView) alert.findViewById(android.R.id.message);
        messageView.setGravity(Gravity.CENTER);
        // messageView.setHeight(140);
        messageView.setTextSize(17);
        //TODO refactor naming ...
        Button bq1 = alert.getButton(DialogInterface.BUTTON1);
        bq1.setTextColor(Color.parseColor("#117AFE"));
        bq1.setTypeface(null, Typeface.BOLD);
        bq1.setTextSize(19);
        Button bq2 = alert.getButton(DialogInterface.BUTTON2);
        bq2.setTextColor(Color.parseColor("#117AFE"));
        bq2.setTextSize(19);

    }


    private void animateTextItemToCorner(View view, final Integer itemPosition, long duration) {
        View viewToCopy = (View) view.getParent();

        ImageView viewToAnimate = new ImageView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(viewToCopy.getWidth(),
                viewToCopy.getHeight());
        viewToAnimate.setLayoutParams(params);

        viewToCopy.setDrawingCacheEnabled(true);
        viewToCopy.buildDrawingCache(true);
        Bitmap bm = Bitmap.createBitmap(viewToCopy.getDrawingCache());
        viewToCopy.setDrawingCacheEnabled(false);

        viewToAnimate.setImageBitmap(bm);
        ((ViewGroup) view.getParent().getParent().getParent()).addView(viewToAnimate);
        moveViewToScreenCorner(itemPosition, viewToAnimate, duration);
    }

    private void animatePhotoItemToCorner(View view, final Integer itemPosition, long duration) {
        ImageView imageViewToCopy = (ImageView) ((View) view.getParent()).findViewById(R.id.menuitem);
        ImageView viewToAnimate = new ImageView(MenuActivity.this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(imageViewToCopy.getWidth(),
                imageViewToCopy.getHeight());
        viewToAnimate.setLayoutParams(params);
        viewToAnimate.setImageDrawable(imageViewToCopy.getDrawable());
        ((ViewGroup) view.getParent().getParent().getParent()).addView(viewToAnimate);
        moveViewToScreenCorner(itemPosition, viewToAnimate, duration);
    }

    private void moveViewToScreenCorner(int position, final View start, long duration) {
        int fromLoc[] = new int[2];
        LinearLayoutManager layoutManager = ((LinearLayoutManager)listview.getLayoutManager());
        int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
        start.getLocationOnScreen(fromLoc);
        float startX = fromLoc[0];
        float startY = fromLoc[1];
        position -= firstVisiblePosition;
        startY += ((position - 1) * (MenuActivity.isPhotoMode ? PHOTO_ITEM_HEIGHT : TEXT_ITEM_HEIGHT));

        int toLoc[] = new int[2];
        MenuActivity.this.listview.getLocationOnScreen(toLoc);
        float destX = toLoc[0] + MenuActivity.this.listview.getWidth();
        float destY = toLoc[1] + MenuActivity.this.listview.getHeight();

        AnimationSet animSet = new AnimationSet(true);
        animSet.setFillAfter(true);
        animSet.setDuration(duration);
        ScaleAnimation scale = new ScaleAnimation(1, 0, 1, 0);
        animSet.addAnimation(scale);

        TranslateAnimation translate = new TranslateAnimation(Animation.ABSOLUTE, startX, Animation.ABSOLUTE, destX,
                Animation.ABSOLUTE, startY, Animation.ABSOLUTE, destY);

        animSet.addAnimation(translate);
        animSet.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                start.setVisibility(View.GONE);
            }
        });

        start.startAnimation(animSet);
    }


    private class MenuAdapter extends RecyclerView.Adapter<ListPhotoItemViewHolder> {

        private Context mContext;
        private OutletDetailsModel mOutletInfo;
        public int mCurrentSelectedCategoryTabIndex;
        private View.OnClickListener mOrderDishButtonOnClickListener;
        public ArrayList<DishModel> mFilteredDishes;
        private int currentRetryCount = 0;
        private Handler handler;


        private static final int SEND_RETRY_NUM = 3;
        private static final String ION_LOGGING_MENU_LIST = "ion-menu-list";
        private static final String DEFAULT_DISH_PHOTO_URL = "default.jpg";
        private static final int MENU_LIST_VIEW_TYPE_COUNT_IS_2 = 2;
        private static final int TYPE_PHOTO_ITEM = 0;
        private static final int TYPE_TEXT_ITEM = 1;
        private static final float CORNER_POS_X = 610;
        private static final float CORNER_POS_Y = 1160;
        private static final long DURATION_LONG = 1000;
        private static final long DURATION_SHORT = 500;

        private Runnable taskAfterModifierPopup;

        private BroadcastReceiver mAfterModifierPopupReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mContext != null && intent != null && intent.getAction() != null && intent.getAction().equals(NOTIF_MODIFIER_OK)) {
                    ((MenuActivity) mContext).mHandler
                            .postDelayed(new Runnable() {
                                             @Override
                                             public void run() {
                                                 try {
                                                     updateOrderCountAndDisplay(mClickedViewToAnimate);
                                                     if (MenuActivity.isPhotoMode) {
                                                         animatePhotoItemToCorner(mClickedViewToAnimate, mClickedPos, DURATION_LONG);
                                                     } else {
                                                         animateTextItemToCorner(mClickedViewToAnimate, mClickedPos, DURATION_LONG);
                                                     }

                                                     if (User.getInstance(mContext).currentSession.getCurrentOrder().getTotalQuantity() == 1 && User.getInstance(mContext).currentSession.getPastOrder().getTotalQuantity() != 0) {
                                                         MenuActivity.this.showClearOrderPopup();
                                                     }
                                                     User.getInstance(mContext).showUndoDishPopup();
                                                 } catch (Exception e) {
                                                     Crashlytics.log(e.toString());
                                                 }
                                             }
                                         }, 100
                            );
                }
            }

        };

        public int getDishPositionInFilteredList(int dishID) {
            for (int i = 0; i < mFilteredDishes.size(); i++) {
                if (dishID == mFilteredDishes.get(i).id) {
                    return i;
                }
            }
            return -1;
        }

        public MenuAdapter(Context context, final OutletDetailsModel outletInfo, int forIndex) {
            super();
            this.mOutletInfo = outletInfo;
            this.mContext = context;
            this.handler = new Handler(context.getMainLooper());
            this.mCurrentSelectedCategoryTabIndex = forIndex;
            LocalBroadcastManager.getInstance(context).registerReceiver(mAfterModifierPopupReceiver,
                    new IntentFilter(NOTIF_MODIFIER_OK));

            Ion.getDefault(context).configure().setLogging(ION_LOGGING_MENU_LIST, Log.DEBUG);
            initAddDishButtonListener();
            try {
                updateFilteredList();
            } catch (NullPointerException npe) {
                Crashlytics.log(npe.getMessage());
                ((Activity) mContext).finish();
            }

        }

        public void updateFilteredList() {
            //TODO dup code to clean up (Seen also in MenuActivity)

            mFilteredDishes = new ArrayList<DishModel>();
            if (mOutletInfo.dishes == null || mOutletInfo.dishes.length == 0) {
                return;
            }

            for (int i = 0, len = mOutletInfo.dishes.length; i < len; i++) {
                if (mOutletInfo.dishes[i].categories[0].id == mOutletInfo.categoriesDetails[mCurrentSelectedCategoryTabIndex].id) {
                    mFilteredDishes.add(mOutletInfo.dishes[i]);
                }
            }

            Collections.sort(mFilteredDishes, new Comparator<DishModel>() {
                @Override
                public int compare(DishModel lhs, DishModel rhs) {
                    int sortIndexLeft = lhs.pos > lhs.positionIndex ? lhs.pos : lhs.positionIndex;
                    int sortIndexRight = rhs.pos > rhs.positionIndex ? rhs.pos : rhs.positionIndex;
                    return sortIndexLeft - sortIndexRight;
                }
            });
        }

        private void initAddDishButtonListener() {
            mOrderDishButtonOnClickListener = new View.OnClickListener() {

                @Override
                public void onClick(final View view) {

                    final Integer itemPosition = (Integer) view.getTag();
                    final DishModel currentDish = (DishModel) getItem(itemPosition.intValue());
                    mSuperActivityToast.dismiss();
                    if (!currentDish.isServedNow()) {
                        AlertDialog alertLocationFail = new AlertDialog.Builder(mContext).create();
                        alertLocationFail.setTitle("Sorry");
                        alertLocationFail.setMessage("This dish is only available from " + currentDish.startTime + " to "
                                + currentDish.endTime);
                        alertLocationFail.setView(null);
                        alertLocationFail.setButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        });
                        alertLocationFail.show();

                        return;
                    }

                    if (currentDish.quantity == 0) {
                        AlertDialog alertLocationFail = new AlertDialog.Builder(mContext).create();
                        alertLocationFail.setTitle("Sorry");
                        alertLocationFail.setMessage("This is out of stock :(");
                        alertLocationFail.setView(null);
                        alertLocationFail.setButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        });
                        alertLocationFail.show();
                        return;
                    }

                    if (currentDish.customizable) {
                        final Intent intentForModifier = new Intent(mContext, ModifierActivity.class);
                        mClickedViewToAnimate = view;
                        mClickedPos = itemPosition;
                        intentForModifier.putExtra(MODIFIER_POPUP_DISH_ID, currentDish.id);
                        ((MenuActivity) mContext)
                                .startActivityForResult(intentForModifier, MODIFIER_POPUP_REQUEST);
                    } else {

                        User.getInstance(mContext).currentSession.getCurrentOrder().addDish(currentDish);
                        User.getInstance(mContext).showUndoDishPopup();
                        updateOrderCountAndDisplay(view);

                        if (User.getInstance(mContext).currentSession.getCurrentOrder().getTotalQuantity() <= 3) {
                            //mSuperActivityToast.show();
                            if (User.getInstance(mContext).currentSession.getCurrentOrder().getTotalQuantity() == 1 && User.getInstance(mContext).currentSession.getPastOrder().getTotalQuantity() != 0) {
                                MenuActivity.this.showClearOrderPopup();
                            }
                        }

                        if (MenuActivity.isPhotoMode) {
                            animatePhotoItemToCorner(view, itemPosition, DURATION_SHORT);
                        } else {
                            animateTextItemToCorner(view, itemPosition, DURATION_SHORT);
                        }
                    }
                }


            };
        }

        public DishModel getItem(int position) {
            return mFilteredDishes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public int getItemCount() {
            return mFilteredDishes.size();
        }

        @Override
        public ListPhotoItemViewHolder onCreateViewHolder(ViewGroup parent, int pos) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.menu_photo_item_row, parent, false);
            return new ListPhotoItemViewHolder(view, pos);
        }

        @Override
        public void onBindViewHolder(ListPhotoItemViewHolder holder, int pos) {
            DishModel dish = mFilteredDishes.get(pos);
            holder.bindDish(dish);
        }
    }


    static class MenuTabPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public MenuTabPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    static class MenuTabFragment extends Fragment {
        MenuAdapter adapter;
        ArrayList<DishModel> dishes;
        String categoryName;

        public MenuTabFragment() {
        }

        @SuppressLint("ValidFragment")
        public MenuTabFragment(String categoryName, MenuAdapter adapter) {
            this.categoryName = categoryName;
            this.adapter = adapter;
            this.dishes = adapter.mFilteredDishes;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.menu_tab_frag, container, false);

            final FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.menu_tab_frag_bg);
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.menu_tab_frag_scrollableview);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);

            return view;
        }
    }
}
