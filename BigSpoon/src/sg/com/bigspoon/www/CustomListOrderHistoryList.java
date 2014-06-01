package sg.com.bigspoon.www;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.TextView;

public class CustomListOrderHistoryList extends ArrayAdapter<String>{
	
	private final Activity context;
	private final String[] titles;
	private final String[] date;
	
	public CustomListOrderHistoryList(Activity context,String[] titles, String[] date) {	
			super(context, R.layout.list_order_history, titles);
			this.context = context;
			this.titles = titles;
			this.date=date;

			}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
	LayoutInflater inflater = context.getLayoutInflater();
	View rowView= inflater.inflate(R.layout.list_order_history, null, true);
	TextView txtTitles = (TextView) rowView.findViewById(R.id.txt_title);
	TextView txtDate = (TextView) rowView.findViewById(R.id.date);
	txtTitles.setText(titles[position]);
	txtDate.setText(date[position]);
	return rowView;
	}
}

