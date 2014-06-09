package sg.com.bigspoon.www;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomListOfOrderHistoryDetails extends ArrayAdapter<String>{
	
	
	private final Activity context;
	private final String[] number;
	private final String[] dishes;
	private final String[] price;
	
	public CustomListOfOrderHistoryDetails(Activity context,String[] number,String[] dishes, String[] price) {	
			super(context, R.layout.list_order_history, dishes);
			this.context = context;
			this.number=number;
			this.dishes = dishes;
			this.price=price;

			}

	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
	
	LayoutInflater inflater = context.getLayoutInflater();
	View rowView= inflater.inflate(R.layout.list_views_order_history_details, null, true);
	TextView txtNumber = (TextView) rowView.findViewById(R.id.number);
	TextView txtDescription = (TextView) rowView.findViewById(R.id.descriptionOrder);
	TextView txtPrice = (TextView) rowView.findViewById(R.id.price);
	
	txtNumber.setText(number[position]);
	
	txtDescription.setText(dishes[position]);
	
	txtPrice.setText(price[position]);
	
	return rowView;
	}
}

