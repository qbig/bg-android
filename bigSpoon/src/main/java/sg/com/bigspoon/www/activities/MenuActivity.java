package sg.com.bigspoon.www.activities;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MatrixCursor;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Comparator;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.adapters.MenuAdapter;
import sg.com.bigspoon.www.adapters.MenuSearchSuggestionAdapter;
import sg.com.bigspoon.www.data.DishModel;
import sg.com.bigspoon.www.data.User;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static sg.com.bigspoon.www.data.Constants.MODIFIER_POPUP_REQUEST;
import static sg.com.bigspoon.www.data.Constants.POS_FOR_CLICKED_CATEGORY;


public class MenuActivity extends ActionBarActivity implements TabListener {

	ActionBar mCategoriesTabBar;
	public ListView listview;
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
        // mag icon left : android:id/search_mag_icon
        // voice icon right : android:id/search_voice_btn
        // cross right : android:id/search_close_btn
        // go right : android:id/search_go_btn
        final ImageView magIcon = (ImageView) mSearchView.findViewById(this.getResources().getIdentifier("android:id/search_mag_icon", null, null));
        magIcon.setImageResource(R.drawable.magnifier_icon_30);
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
                Cursor cursor = (Cursor) mSearchView.getSuggestionsAdapter().getItem(position);
                String feedName = cursor.getString(1);
                mSearchView.setQuery(feedName, false);
                mSearchView.clearFocus();
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = (Cursor) mSearchView.getSuggestionsAdapter().getItem(position);
                String feedName = cursor.getString(1);
                mSearchView.setQuery(feedName, false);
                mSearchView.clearFocus();
                displayDish(feedName);

                return true;
            }
        });
        mSearchField.setHint(R.string.dish_search_hint);
        mSearchField.setHintTextColor(getResources().getColor(R.color.light_gray));
    }

    private void displayDish(String name){
        DishModel searchedDish = User.getInstance(MenuActivity.this).currentOutlet.getDishWithName(name);
        mCategoryPosition = User.getInstance(MenuActivity.this).currentOutlet.getCategoryPositionWithDishId(searchedDish.id);
        mCategoriesTabBar.setSelectedNavigationItem(mCategoryPosition);
        adapter.mCurrentSelectedCategoryTabIndex = mCategoryPosition;
        adapter.updateFilteredList();
        adapter.notifyDataSetChanged();
        listview.setSelection(adapter.getDishPositionInFilteredList(searchedDish.id));
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

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
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
}
