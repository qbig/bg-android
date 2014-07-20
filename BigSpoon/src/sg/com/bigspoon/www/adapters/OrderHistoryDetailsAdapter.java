package sg.com.bigspoon.www.adapters;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.data.OrderItem;
import sg.com.bigspoon.www.data.RetrievedOrder;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class OrderHistoryDetailsAdapter extends ArrayAdapter<OrderItem> {

	private final Activity context;
	private LayoutInflater inflater;

	public OrderHistoryDetailsAdapter(Activity context, RetrievedOrder selectedItem) {
		super(context, R.layout.list_order_history, selectedItem.orders);
		this.context = context;
		inflater = context.getLayoutInflater();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final OrderItem order = getItem(position);
		final OrderHistoryDetailsItemViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_views_order_history_details, null, true);
			viewHolder = new OrderHistoryDetailsItemViewHolder();
			viewHolder.txtNumber = (TextView) convertView.findViewById(R.id.number);
			viewHolder.txtDescription = (TextView) convertView.findViewById(R.id.descriptionOrder);
			viewHolder.txtPrice = (TextView) convertView.findViewById(R.id.price);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (OrderHistoryDetailsItemViewHolder) convertView.getTag();
		}

		viewHolder.txtNumber.setText(order.quantity + "");
		viewHolder.txtDescription.setText(order.dish.name);
		viewHolder.txtPrice.setText(order.dish.price + "");

		return convertView;
	}
	
	class OrderHistoryDetailsItemViewHolder {
		TextView txtNumber, txtDescription, txtPrice;
	}
}
