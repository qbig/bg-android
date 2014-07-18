package sg.com.bigspoon.www.activities;

import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_AUTHTOKEN;
import static sg.com.bigspoon.www.data.Constants.ORDER_HISTORY_URL;
import static sg.com.bigspoon.www.data.Constants.PREFS_NAME;
import static sg.com.bigspoon.www.data.Constants.SELECTED_HISTORY_ITEM_POSITION;

import java.util.List;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.adapters.OrderHistoryListAdapter;
import sg.com.bigspoon.www.data.OrderHistoryItem;
import sg.com.bigspoon.www.data.User;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class OrderHistoryListActivity extends Activity {

	private ImageButton mTellUsImageButton;
	private ListView mList;
	private ActionBar mTopActionBar;
	private static final String ION_LOGGING_HISTORY_LIST = "ion-hisotry-list";
	private SharedPreferences loginPreferences;
	
	private OnClickListener sendEmailButtonListener = new OnClickListener() {
		public void onClick(View view) {

			final Intent intentToSendEmail = new Intent(android.content.Intent.ACTION_SEND);

			intentToSendEmail.setType("plain/text");
			intentToSendEmail.putExtra(Intent.EXTRA_EMAIL, new String[] { "jay@bigspoon.sg" });
			intentToSendEmail.putExtra(Intent.EXTRA_CC, new String[] { "leon@bigspoon.sg" });
			intentToSendEmail.putExtra(Intent.EXTRA_SUBJECT, "Hello BigSpoon!");
			intentToSendEmail.putExtra(Intent.EXTRA_TEXT, " ");
			try {
				OrderHistoryListActivity.this.startActivity(Intent.createChooser(intentToSendEmail, "Send mail..."));
			} catch (android.content.ActivityNotFoundException ex) {
				Toast.makeText(OrderHistoryListActivity.this, "There are no email clients installed.",
						Toast.LENGTH_SHORT).show();
			}
		}
	};
	private View mActionBarView;
	private ImageButton mBackButton;
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		initTopActionBar();
		setupBackButton();
		hideTopRightButton();
		return super.onCreateOptionsMenu(menu);

	}

	private void initTopActionBar() {
		mTopActionBar = getActionBar();
		mTopActionBar.setDisplayShowHomeEnabled(false);
		
		mTellUsImageButton = (ImageButton) findViewById(R.id.imagetellus);
		mTellUsImageButton.setOnClickListener(sendEmailButtonListener);
		mActionBarView = getLayoutInflater().inflate(R.layout.action_bar_items_activity, null);
		mTopActionBar.setCustomView(mActionBarView);

		TextView title = (TextView) mActionBarView.findViewById(R.id.title);
		title.setText("Order History");
		mTopActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
	}

	private void setupBackButton() {
		mBackButton = (ImageButton) mActionBarView.findViewById(R.id.btn_menu);
		mBackButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
		mBackButton.setPadding(22, 0, 0, 0);

		StateListDrawable states = new StateListDrawable();
		states.addState(new int[] { android.R.attr.state_pressed }, getResources().getDrawable(R.drawable.menu_pressed));
		states.addState(new int[] {}, getResources().getDrawable(R.drawable.menu));
		mBackButton.setImageDrawable(states);
		mBackButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
	}

	private void hideTopRightButton() {
		ImageButton mRightCornerButton = (ImageButton) mActionBarView.findViewById(R.id.order_history);
		mRightCornerButton.setVisibility(View.INVISIBLE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_history);
		Ion.getDefault(this).configure().setLogging(ION_LOGGING_HISTORY_LIST, Log.DEBUG);
		mList = (ListView) findViewById(R.id.listoforder);
		loginPreferences = getSharedPreferences(PREFS_NAME,0);
		
		Ion.with(this)
		.load(ORDER_HISTORY_URL)
		.setHeader("Content-Type", "application/json; charset=utf-8")
		.setHeader("Authorization", "Token " + loginPreferences.getString(LOGIN_INFO_AUTHTOKEN, ""))
		.as(new TypeToken<List<OrderHistoryItem>>() {
        })
		.setCallback(new FutureCallback<List<OrderHistoryItem>>() {
            @Override
            public void onCompleted(Exception e, List<OrderHistoryItem> result) {
                if (e != null) {
                    Toast.makeText(OrderHistoryListActivity.this, "Error login with FB", Toast.LENGTH_LONG).show();
                    return;
                }
                
                User.getInstance(OrderHistoryListActivity.this).diningHistory = result;
                OrderHistoryListAdapter adapter = new OrderHistoryListAdapter(OrderHistoryListActivity.this, result);
        		mList.setAdapter(adapter);
        		mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

        			@Override
        			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        				Intent intent = new Intent(getApplicationContext(), OrderHistoryDetailsActivity.class);
        				intent.putExtra(SELECTED_HISTORY_ITEM_POSITION, position); 
        				startActivity(intent);
        			}
        		});
            }
        });
		
	}
}
