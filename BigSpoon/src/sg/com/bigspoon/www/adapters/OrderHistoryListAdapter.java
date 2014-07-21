package sg.com.bigspoon.www.adapters;

import java.util.List;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.R.id;
import sg.com.bigspoon.www.R.layout;
import sg.com.bigspoon.www.data.RetrievedOrder;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class OrderHistoryListAdapter extends ArrayAdapter<RetrievedOrder> {

	private final Activity context;
	private LayoutInflater inflater;

	public OrderHistoryListAdapter(Activity context, List<RetrievedOrder> result) {
		super(context, R.layout.list_order_history, result);
		this.context = context;
		this.inflater = context.getLayoutInflater();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final RetrievedOrder item = getItem(position);
		final HistoryListViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new HistoryListViewHolder();
			convertView = inflater.inflate(R.layout.list_order_history, null, true);
			viewHolder.txtTitles = (TextView) convertView.findViewById(R.id.txt_title);
			viewHolder.txtDate = (TextView) convertView.findViewById(R.id.date);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (HistoryListViewHolder) convertView.getTag();
		}
		
		
		viewHolder.txtTitles.setText(item.outlet.name);
		viewHolder.txtDate.setText(item.orderTime);
		return convertView;
	}
	
	class HistoryListViewHolder {
		TextView txtTitles, txtDate;
	}
}
