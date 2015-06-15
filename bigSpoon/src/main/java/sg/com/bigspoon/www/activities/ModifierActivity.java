package sg.com.bigspoon.www.activities;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.adapters.ModifierListViewAdapter;
import sg.com.bigspoon.www.data.DishModel;
import sg.com.bigspoon.www.data.Order;
import sg.com.bigspoon.www.data.User;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import za.co.immedia.pinnedheaderlistview.PinnedHeaderListView;

import static sg.com.bigspoon.www.data.Constants.MODIFIER_POPUP_DISH_ID;
import static sg.com.bigspoon.www.data.Constants.NOTIF_MODIFIER_OK;
import static sg.com.bigspoon.www.data.Constants.MODIFIER_REULST_OK;

public class ModifierActivity extends Activity {

	private int dishId;
	private Button mOkayButton;
	private Button mCancelButton;
	private DishModel mSelectedDish;
	private PinnedHeaderListView mModifierListView;
	private LinearLayout mFooter;
	private ModifierListViewAdapter mModifierListViewAdapter;
	private ActionBar mActionBar;
	private View mActionBarView;
	private TextView mTitle;
	private EditText mNoteField;
    private String mBackgroundColor;
    private String mItemTextColor;

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		final Bundle extras = getIntent().getExtras();
		if (extras != null) {
			dishId = extras.getInt(MODIFIER_POPUP_DISH_ID);
			mSelectedDish = User.getInstance(getApplicationContext()).currentOutlet.getDishWithId(dishId);
			if (mSelectedDish == null) {
				finish();
			}
        } else {
			finish();
		}
		super.onCreate(savedInstanceState);

		uiSetup();
		setupCancelButtonHandler();
		setupOkButtonHandler();
        if (mSelectedDish.customizable){
            setupModifierListViewAdapter();
        }
	}

	private void setupModifierListViewAdapter() {
		mModifierListViewAdapter = new ModifierListViewAdapter(this, mSelectedDish.modifier);
		mModifierListView.setAdapter(mModifierListViewAdapter);
	}

	private void setupOkButtonHandler() {
		mOkayButton = (Button) mFooter.findViewById(R.id.ok);
		mOkayButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setResult(RESULT_OK);
                final Order currentOrders = User.getInstance(ModifierActivity.this).currentSession.getCurrentOrder();
				final int dishIndex = currentOrders.addDish(mSelectedDish);
				final String note = mNoteField.getText().toString();
				if (StringUtils.isNotEmpty(note)){
                    if (StringUtils.isNotEmpty(currentOrders.mItems.get(dishIndex).note)) {
                        currentOrders.mItems.get(dishIndex).note += ("\n" + note);
                    } else {
                        currentOrders.mItems.get(dishIndex).note = note;
                    }

				}
                setResult(MODIFIER_REULST_OK);
				finish();
			}
		});
	}

	private void setupCancelButtonHandler() {
		mCancelButton = (Button) mFooter.findViewById(R.id.cancle);
		mCancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
	}

	private void uiSetup() {
		setContentView(R.layout.activity_modifier);
        if (mSelectedDish.customizable) {
            mBackgroundColor = mSelectedDish.modifier.backgroundColor.trim();
            mItemTextColor = mSelectedDish.modifier.itemTextColor.trim();
        } else {
            mBackgroundColor = "#434751";
            mItemTextColor = "#FFFFFF";
        }

		final LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mFooter = (LinearLayout) inflator.inflate(R.layout.modifier_footer, null);
        mNoteField = (EditText) mFooter.findViewById(R.id.add_note_textfield);
        mModifierListView = (PinnedHeaderListView) findViewById(R.id.pinnedListView);
        mModifierListView.addFooterView(mFooter);
        mModifierListView.setBackgroundColor(Color.parseColor(mBackgroundColor));
        mFooter.setBackgroundColor(Color.parseColor(mBackgroundColor));
        mModifierListView.getRootView().setBackgroundColor(Color.parseColor(mBackgroundColor));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		setupTopActionBar();
		setTitleColor();
		return super.onCreateOptionsMenu(menu);
	}

	private void setTitleColor() {
		mTitle = (TextView) mActionBarView.findViewById(R.id.title);
		mTitle.setText(mSelectedDish.name);
		mTitle.setTextColor(Color.parseColor(mItemTextColor));
	}

	private void setupTopActionBar() {
		mActionBar = getActionBar();
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBarView = getLayoutInflater().inflate(R.layout.action_bar_modifier, null);
        mActionBarView.setBackgroundColor(Color.parseColor(mBackgroundColor));
		final LayoutParams layout = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		mActionBar.setCustomView(mActionBarView, layout);
		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
	}
}