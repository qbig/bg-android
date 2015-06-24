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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import java.util.Vector;

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
import static sg.com.bigspoon.www.data.Constants.POS_FOR_CLICKED_CATEGORY;


public class MenuActivity extends ActionBarActivity{

    private EditText mSearchField;
    public static boolean isPhotoMode = true;

    private View mActionBarView;
    private android.support.v7.widget.SearchView mSearchView;
    private ImageButton toggleButton;
    private ImageButton backToOutletList;
    private ImageButton historyButton;
    private View bottomActionBar;
    private TextView mOrderedDishCounterText;
    private int mCategoryPosition = 0;
    public Handler mHandler;
    private boolean shouldShowTabs;
    private Drawable outOfStockBackground;
    private ImageView mMagIcon;
    private SuperActivityToast mSuperActivityToast;
    private static final float PHOTO_ITEM_HEIGHT = 242;
    private static final float TEXT_ITEM_HEIGHT = 142;
    private static final long DURATION_LONG = 1000;
    private static final long DURATION_SHORT = 500;
    private TextView mCornerText;
    private ViewPager mViewPager;
    private MenuTabPagerAdapter mFragAdapter;
    public View mClickedViewToAnimate;
    public int mClickedPos;

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private BroadcastReceiver mUpdateCornerCounterReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MenuActivity.this.updateOrderedDishCounter();
        }
    };


    private BroadcastReceiver mAfterModifierPopupReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (context != null && intent != null && intent.getAction() != null && intent.getAction().equals(NOTIF_MODIFIER_OK)) {
                mHandler
                        .postDelayed(new Runnable() {
                                         @Override
                                         public void run() {
                                             try {
                                                 updateOrderCountAndDisplay();
                                                 if (MenuActivity.isPhotoMode) {
                                                     animatePhotoItemToCorner(mClickedViewToAnimate, mClickedPos, DURATION_LONG);
                                                 } else {
                                                     animateTextItemToCorner(mClickedViewToAnimate, mClickedPos, DURATION_LONG);
                                                 }

                                                 if (User.getInstance(MenuActivity.this).currentSession.getCurrentOrder().getTotalQuantity() == 1 && User.getInstance(MenuActivity.this).currentSession.getPastOrder().getTotalQuantity() != 0) {
                                                     MenuActivity.this.showClearOrderPopup();
                                                 }
                                                 User.getInstance(MenuActivity.this).showUndoDishPopup();
                                             } catch (Exception e) {
                                                 Crashlytics.log(e.toString());
                                             }
                                         }
                                     }, 100
                        );
            }
        }

    };


    private DishModel[] filterInactiveDish(DishModel[] dishes) {
        Vector dishList = new Vector();
        for(int i = 0; i<dishes.length; i++){
            if(dishes[i].isActive){
                dishList.addElement(dishes[i]);
            }
        }
        DishModel[] filteredDishes= new DishModel[dishList.size()];
        dishList.copyInto(filteredDishes);
        return filteredDishes;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_tabs);
        setupBottomActionBar();
        User.getInstance(this).currentOutlet.dishes = filterInactiveDish(User.getInstance(this).currentOutlet.dishes);
        mHandler = new Handler();
        this.outOfStockBackground = getResources().getDrawable(R.drawable.out_of_stock);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.tabanim_toolbar);
        mCornerText = (TextView) findViewById(R.id.corner);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewPager = (ViewPager) findViewById(R.id.tabanim_viewpager);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabanim_tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mCategoryPosition = tab.getPosition();
                mViewPager.setCurrentItem(mCategoryPosition);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mCategoryPosition = extras.getInt(POS_FOR_CLICKED_CATEGORY, 0);
        }
        mViewPager.setCurrentItem(mCategoryPosition);
        setupHideCategoriesTabsEvent();
        LocalBroadcastManager.getInstance(this).registerReceiver(mUpdateCornerCounterReceiver,
                new IntentFilter(NOTIF_UNDO_ORDER));
        LocalBroadcastManager.getInstance(this).registerReceiver(mAfterModifierPopupReceiver,
                new IntentFilter(NOTIF_MODIFIER_OK));
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
    }


    private void setupViewPager(ViewPager viewPager) {

        mFragAdapter = new MenuTabPagerAdapter(getSupportFragmentManager());

        try {
            for (int i = 0, len = User.getInstance(this).currentOutlet.categoriesDetails.length; i < len; i++) {
                MenuAdapter adapter = new MenuAdapter(this, User.getInstance(this).currentOutlet, i);
                mFragAdapter.addFrag(new MenuTabFragment(User.getInstance(this).currentOutlet.categoriesDetails[i].name, adapter), User.getInstance(this).currentOutlet.categoriesDetails[i].name);
            }
            viewPager.setAdapter(mFragAdapter);
        } catch (NullPointerException e) {
            Crashlytics.log("currentOutlet is null:" + e.getMessage());
            Intent intent = new Intent(getApplicationContext(), CategoriesListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

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
            final Animation a = AnimationUtils.loadAnimation(this, R.anim.scale_up);
            mOrderedDishCounterText.startAnimation(a);
        } else {
            mOrderedDishCounterText.clearAnimation();
            mOrderedDishCounterText.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            setupActionButton(menu);
            return super.onCreateOptionsMenu(menu);
        } catch (NullPointerException npe) {
            Crashlytics.log(npe.getMessage());
            finish();
            return false;
        }
    }

    private void setupActionButton(Menu menu) {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            mSearchView = (android.support.v7.widget.SearchView) searchItem.getActionView();
        }
        if (mSearchView != null) {
            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(MenuActivity.this.getComponentName()));
        }


        mSearchView.setIconifiedByDefault(false);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryRefinementEnabled(true);

        mSearchField = (EditText) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
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
                final String firstSuggestionString = ((Cursor) mSearchView.getSuggestionsAdapter().getItem(0)).getString(1);
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
                mSearchField.clearFocus();
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
                mSearchField.clearFocus();
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
        LinearLayout searchBar = (LinearLayout) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_bar);
        searchBar.setLayoutTransition(new LayoutTransition());
    }

    private void displayDish(String name) {
        DishModel searchedDish = User.getInstance(MenuActivity.this).currentOutlet.getDishWithName(name);
        mCategoryPosition = User.getInstance(MenuActivity.this).currentOutlet.getCategoryPositionWithDishId(searchedDish.id);
        mViewPager.setCurrentItem(mCategoryPosition);
        final MenuTabFragment menuFrag = (MenuTabFragment) mFragAdapter.getItem(mCategoryPosition);
        menuFrag.mRecyclerView.smoothScrollToPosition(menuFrag.adapter.getDishPositionInFilteredList(searchedDish.id));
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

            }
        });
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
        final String[] columns = new String[]{"_id", "text"};
        final Object[] temp = new Object[]{0, "default"};
        final String queryLow = query.toLowerCase();
        MatrixCursor cursor = new MatrixCursor(columns);
        Arrays.sort(User.getInstance(this).currentOutlet.dishes, new Comparator<DishModel>() {
            @Override
            public int compare(final DishModel lhs, DishModel rhs) {

                final boolean lhsStartMatch = lhs.name.toLowerCase().contains(queryLow);
                final boolean rhsStartMatch = rhs.name.toLowerCase().contains(queryLow);
                if (query.length() <= 10) {
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

        for (int i = 0; i < User.getInstance(this).currentOutlet.dishes.length; i++) {

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

        public ListPhotoItemViewHolder(View itemView) {
            super(itemView);
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
                updateOrderCountAndDisplay();

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

    private class PlaceHolderViewHolder extends ListPhotoItemViewHolder {
        View placeholderView;

        public PlaceHolderViewHolder(View itemView) {
            super(itemView);
        }
    }

    private void updateOrderCountAndDisplay() {
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
        //ImageView imageViewToCopy = (ImageView) ((View) view.getParent()).findViewById(R.id.menuitem);
        ImageView imageViewToCopy = (ImageView) view.findViewById(R.id.menuitem);
        ImageView viewToAnimate = new ImageView(MenuActivity.this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(imageViewToCopy.getWidth(),
                imageViewToCopy.getHeight());
        viewToAnimate.setLayoutParams(params);
        viewToAnimate.setImageDrawable(imageViewToCopy.getDrawable());
        //((ViewGroup) view.getParent().getParent().getParent()).addView(viewToAnimate);
        ((ViewGroup) view.getParent().getParent()).addView(viewToAnimate);
        moveViewToScreenCorner(itemPosition, viewToAnimate, duration);
    }

    private void moveViewToScreenCorner(int position, final View start, long duration) {
        int fromLoc[] = new int[2];
        final MenuTabFragment menuFrag = (MenuTabFragment) mFragAdapter.getItem(mCategoryPosition);
        LinearLayoutManager layoutManager = (LinearLayoutManager) menuFrag.mRecyclerView.getLayoutManager();
        int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
        start.getLocationOnScreen(fromLoc);
        float startX = fromLoc[0];
        float startY = fromLoc[1];
        position -= firstVisiblePosition;
        startY += ((position - 1) * (MenuActivity.isPhotoMode ? PHOTO_ITEM_HEIGHT : TEXT_ITEM_HEIGHT));

        int toLoc[] = new int[2];
        menuFrag.mRecyclerView.getLocationOnScreen(toLoc);
        float destX = toLoc[0] + menuFrag.mRecyclerView.getWidth();
        float destY = toLoc[1] + menuFrag.mRecyclerView.getHeight();

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
                ((ViewGroup) start.getParent()).removeView(start);
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

        public int getDishPositionInFilteredList(int dishID) {
            for (int i = 0; i < mFilteredDishes.size(); i++) {
                if (dishID == mFilteredDishes.get(i).id && mFilteredDishes.get(i).isActive) {
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

            Ion.getDefault(context).configure().setLogging(ION_LOGGING_MENU_LIST, Log.DEBUG);
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

        public DishModel getItem(int position) {
            return mFilteredDishes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public int getItemCount() {
            return mFilteredDishes.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position < mFilteredDishes.size()) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public ListPhotoItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if (viewType == 0) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_photo_item_row, parent, false);
                return new ListPhotoItemViewHolder(view);
            } else {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_placeholder_layout, parent, false);
                return new PlaceHolderViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(ListPhotoItemViewHolder holder, int pos) {
            if (pos >= mFilteredDishes.size()) {
                return;
            }
            DishModel dish = mFilteredDishes.get(pos);
            holder.mPosition = pos;
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
        RecyclerView mRecyclerView;

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
            mRecyclerView = (RecyclerView) view.findViewById(R.id.menu_tab_frag_scrollableview);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setAdapter(adapter);

            return view;
        }
    }
}
