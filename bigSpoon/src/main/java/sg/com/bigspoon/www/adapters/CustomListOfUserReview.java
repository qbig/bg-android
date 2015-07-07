package sg.com.bigspoon.www.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

import java.util.ArrayList;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.activities.UserReviewActivity;
import sg.com.bigspoon.www.data.OrderItem;
import sg.com.bigspoon.www.data.User;

public class CustomListOfUserReview extends ArrayAdapter<OrderItem> {

	private final Activity mContext;

	public CustomListOfUserReview(Activity context, ArrayList<OrderItem> mItems) {
		super(context, R.layout.list_user_review, mItems);
		this.mContext = context;

	}

	@Override
	public int getCount() {
		return User.getInstance(mContext).currentSession.getPastOrder().mItems
				.size();

	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {

		LayoutInflater inflater = mContext.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.list_user_review, null, true);
		TextView itemName = (TextView) rowView
				.findViewById(R.id.list_review_text);

		itemName.setText(User.getInstance(mContext).currentSession.getPastOrder().mItems
				.get(position).dish.name);
		final RatingBar ratingBar = (RatingBar) rowView.findViewById(R.id.ratingBar);
		ratingBar.setOnRatingBarChangeListener( new OnRatingBarChangeListener() {
			
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				UserReviewActivity.ratingsArray[position] = ratingBar.getRating();
			}
		});
		return rowView;
	}
}
