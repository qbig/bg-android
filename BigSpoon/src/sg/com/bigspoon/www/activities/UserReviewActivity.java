package sg.com.bigspoon.www.activities;

import java.util.ArrayList;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.adapters.CustomListOfUserReview;
import sg.com.bigspoon.www.data.OrderItem;
import sg.com.bigspoon.www.data.User;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public class UserReviewActivity extends Activity {

	ListView list;
	public static ArrayList<Float> ratingsArray;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_review);
		
		for(int i=0;i<User.getInstance(this).currentSession.pastOrder.mItems
				.size();i++){
			ratingsArray.add((float) -1);
		}
		
		
		Button cancel = (Button) findViewById(R.id.button1);
		Button submit = (Button) findViewById(R.id.button2);
		
		cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	finish();
            }
        });
		
		submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	//performRatingSubmission();
            	//performFeedbackSubmission();
            }
        });
		
		list = (ListView) findViewById(R.id.ratingList);
		CustomListOfUserReview adapter = new CustomListOfUserReview(
				this, User.getInstance(this).currentSession.pastOrder.mItems);
		list.setAdapter(adapter);
		
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getApplicationContext(),
						OrderHistoryDetailsActivity.class);
				startActivity(intent);
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_review, menu);
		return true;
	}

}
