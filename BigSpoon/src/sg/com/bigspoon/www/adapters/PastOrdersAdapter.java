package sg.com.bigspoon.www.adapters;

import java.util.ArrayList;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.data.OrderItem;
import sg.com.bigspoon.www.data.User;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PastOrdersAdapter extends ArrayAdapter<OrderItem> {

	private final Activity mContext;

	public PastOrdersAdapter(Activity context,ArrayList <OrderItem> items) {
		super(context, R.layout.activity_items, items);
		this.mContext = context;
	}

	@Override
	public int getCount() {
		return User.getInstance(mContext).currentSession.getPastOrder().mItems
				.size();

	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {

		LayoutInflater inflater = mContext.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.list_items, null, true);

		TextView numberView = (TextView) rowView
				.findViewById(R.id.quantityText);
		TextView itemdescView = (TextView) rowView.findViewById(R.id.nameText);
		TextView priceView = (TextView) rowView.findViewById(R.id.priceText);
		TextView modifierSubTitle = (TextView) rowView
				.findViewById(R.id.subTitle);
		modifierSubTitle.setVisibility(View.GONE);

		final int dishQuantity = User.getInstance(mContext).currentSession.getPastOrder()
				.getQuantityOfDishByIndex(position);
		numberView
				.setText(Integer.toString(dishQuantity));
		itemdescView
				.setText(User.getInstance(mContext).currentSession.getPastOrder().mItems
						.get(position).dish.name);
		
		if (User.getInstance(mContext).currentSession.getPastOrder().mItems
				.get(position).dish.customizable) {
			modifierSubTitle.setVisibility(View.VISIBLE);
			final String test = User.getInstance(mContext).currentSession.getPastOrder()
					.getModifierDetailsTextAtIndex(position);
			modifierSubTitle.setText(test);
			
			priceView
			.setText("$" + Double.toString(User.getInstance(mContext).currentSession.getPastOrder().mItems
					.get(position).dish.price ));
		} else {
			priceView
			.setText("$" + Double.toString(User.getInstance(mContext).currentSession.getPastOrder().mItems
					.get(position).dish.price * dishQuantity));
		}
		
		

		return rowView;

	}
}