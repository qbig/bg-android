package sg.com.bigspoon.www.activities;

import static sg.com.bigspoon.www.data.Constants.MODIFIER_POPUP_DISH_ID;

import com.google.android.gms.drive.internal.t;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.adapters.ModifierListViewAdapter;
import sg.com.bigspoon.www.data.DishModel;
import sg.com.bigspoon.www.data.User;
import za.co.immedia.pinnedheaderlistview.PinnedHeaderListView;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		final Bundle extras = getIntent().getExtras();
		if (extras != null) {
			dishId = extras.getInt(MODIFIER_POPUP_DISH_ID);
			mSelectedDish = User.getInstance(getApplicationContext()).currentOutlet.getDishWithId(dishId);
			if (mSelectedDish == null || ! mSelectedDish.customizable) {
				finish();
			}
		} else {
			finish();
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modifier);
		
		final PinnedHeaderListView modifierListView = (PinnedHeaderListView) findViewById(R.id.pinnedListView);
		final LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final LinearLayout footer = (LinearLayout) inflator.inflate(R.layout.modifier_footer, null);
		modifierListView.addFooterView(footer);
		
		modifierListView.getRootView().setBackgroundColor(Color.parseColor("#" + mSelectedDish.modifier.backgroundColor));
		mCancelButton = (Button) footer.findViewById(R.id.cancle);
		mCancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});

		mOkayButton = (Button) footer.findViewById(R.id.ok);
		mOkayButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setResult(RESULT_OK);
				User.getInstance(ModifierActivity.this).currentSession.currentOrder.addDish(mSelectedDish);
				finish();
			}
		});

		
		ModifierListViewAdapter modifierListViewAdapter = new ModifierListViewAdapter(this, mSelectedDish.modifier);
		modifierListView.setAdapter(modifierListViewAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		final View mActionBarView = getLayoutInflater().inflate(R.layout.action_bar_modifier, null);
		final LayoutParams layout = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		actionBar.setCustomView(mActionBarView, layout);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		final TextView title = (TextView) mActionBarView.findViewById(R.id.title);
		title.setText(mSelectedDish.name);
		title.setTextColor(Color.parseColor("#" + mSelectedDish.modifier.itemTextColor));
		return super.onCreateOptionsMenu(menu);
	}
}