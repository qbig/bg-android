package sg.com.bigspoon.www.activities;

import static sg.com.bigspoon.www.data.Constants.SELECTED_HISTORY_ITEM_POSITION;
import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.adapters.OrderHistoryDetailsAdapter;
import sg.com.bigspoon.www.data.Order;
import sg.com.bigspoon.www.data.OrderItem;
import sg.com.bigspoon.www.data.RetrievedOrder;
import sg.com.bigspoon.www.data.User;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class OrderHistoryDetailsActivity extends Activity {

	ListView list;
	private ActionBar actionBar;
	private View mActionBarView;
	private ImageButton backButton;
	
	private Button addToItemsButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_history_details);

		final int selectedPosition = getIntent().getIntExtra(SELECTED_HISTORY_ITEM_POSITION, -1);
		if (selectedPosition == -1) {
			finish();
		}

		final RetrievedOrder selectedItem = User.getInstance(this).diningHistory.get(selectedPosition);
		list = (ListView) findViewById(R.id.listoforderDetails);
		list.setAdapter(new OrderHistoryDetailsAdapter(this, selectedItem));
		
		addToItemsButton = (Button) findViewById(R.id.buttonAddToItems);
		addToItemsButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final User user = User.getInstance(OrderHistoryDetailsActivity.this);
				if (user.currentOutlet.outletID == selectedItem.outlet.id) {
					user.currentSession.currentOrder.mergeWithAnotherOrder(selectedItem.toOrder());
				}
				final Intent intent = new Intent(OrderHistoryDetailsActivity.this, ItemsActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		setupActionBar();
		setupBackButton();
		hideHistoryButton();

		return super.onCreateOptionsMenu(menu);

	}

	private void setupBackButton() {
		backButton = (ImageButton) mActionBarView.findViewById(R.id.btn_menu);
		backButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
		backButton.setPadding(22, 0, 0, 0);

		final StateListDrawable states = new StateListDrawable();
		states.addState(new int[] { android.R.attr.state_pressed }, getResources().getDrawable(R.drawable.menu_pressed));
		states.addState(new int[] {}, getResources().getDrawable(R.drawable.menu));
		backButton.setImageDrawable(states);

		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (OrderHistoryDetailsActivity.this.isTaskRoot()){
					final Intent intent = new Intent(getApplicationContext(), OutletListActivity.class);
					startActivity(intent);
				} else {
					finish();
				}
			}
		});
	}

	private void hideHistoryButton() {
		ImageButton hisotryButton = (ImageButton) mActionBarView.findViewById(R.id.order_history);
		hisotryButton.setVisibility(View.INVISIBLE);
	}

	private void setupActionBar() {
		actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		mActionBarView = getLayoutInflater().inflate(R.layout.action_bar_items_activity, null);
		actionBar.setCustomView(mActionBarView);
		TextView title = (TextView) mActionBarView.findViewById(R.id.title);
		title.setText(R.string.order_history);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
	}

}