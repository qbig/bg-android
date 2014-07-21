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

	public PastOrdersAdapter(Activity context, ArrayList<OrderItem> orders) {
		super(context, R.layout.activity_items, orders);
		this.mContext = context;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final OrderItem item = getItem(position);
		final LayoutInflater inflater = mContext.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.list_items, null, true);

		TextView numberView = (TextView) rowView.findViewById(R.id.quantityText);
		TextView itemdescView = (TextView) rowView.findViewById(R.id.nameText);
		TextView priceView = (TextView) rowView.findViewById(R.id.priceText);

		numberView.setText(item.quantity + "");
		itemdescView.setText(item.dish.name);
		priceView
				.setText(item.dish.price + "");

		return rowView;

	}
}