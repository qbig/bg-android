package sg.com.bigspoon.www.activities;

import static sg.com.bigspoon.www.data.Constants.MODIFIER_POPUP_DISH_ID;
import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.adapters.ModifierListViewAdapter;
import sg.com.bigspoon.www.data.DishModel;
import sg.com.bigspoon.www.data.User;
import za.co.immedia.pinnedheaderlistview.PinnedHeaderListView;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		final Bundle extras = getIntent().getExtras();
		if (extras != null) {
			dishId = extras.getInt(MODIFIER_POPUP_DISH_ID);
			mSelectedDish = User.getInstance(getApplicationContext()).currentOutlet.getDishWithId(dishId);
			if (mSelectedDish == null || !mSelectedDish.customizable) {
				finish();
			}
		} else {
			finish();
		}
		super.onCreate(savedInstanceState);

		uiSetup();
		setupCancelButtonHandler();
		setupOkButtonHandler();
		setupModifierListViewAdapter();

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
				User.getInstance(ModifierActivity.this).currentSession.currentOrder.addDish(mSelectedDish);
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
		mModifierListView = (PinnedHeaderListView) findViewById(R.id.pinnedListView);
		final LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mFooter = (LinearLayout) inflator.inflate(R.layout.modifier_footer, null);
		mModifierListView.addFooterView(mFooter);

		mModifierListView.getRootView().setBackgroundColor(
				Color.parseColor("#" + mSelectedDish.modifier.backgroundColor));
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
		mTitle.setTextColor(Color.parseColor("#" + mSelectedDish.modifier.itemTextColor));
	}

	private void setupTopActionBar() {
		mActionBar = getActionBar();
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBarView = getLayoutInflater().inflate(R.layout.action_bar_modifier, null);
		final LayoutParams layout = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		mActionBar.setCustomView(mActionBarView, layout);
		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
	}
}