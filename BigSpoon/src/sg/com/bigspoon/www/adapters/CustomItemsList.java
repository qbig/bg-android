package sg.com.bigspoon.www.adapters;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.data.User;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class CustomItemsList extends ArrayAdapter<String>{


	private final Activity mContext;

	public CustomItemsList(Activity context)
			{			super(context,R.layout.activity_items);
			this.mContext = context;
				}


	@Override
	public int getCount(){
		return User.getInstance(mContext).currentSession.currentOrder.mItems.size();
		
	}
	@Override
	public View getView(int position, View view, ViewGroup parent) {

	LayoutInflater inflater = mContext.getLayoutInflater();
	View rowView= inflater.inflate(R.layout.list_items, null, true);

	TextView numberView = (TextView) rowView.findViewById(R.id.quantityText);
	TextView itemdescView = (TextView) rowView.findViewById(R.id.nameText);
	TextView priceView = (TextView) rowView.findViewById(R.id.priceText);

	numberView.setText(Integer.toString(User.getInstance(mContext).currentSession.pastOrder.getQuantityOfDishByIndex(position)));   	
	itemdescView.setText(User.getInstance(mContext).currentSession.pastOrder.mItems.get(position).dish.name);
	priceView.setText(Double.toString(User.getInstance(mContext).currentSession.pastOrder.mItems.get(position).dish.price));
	

	return rowView;

	}
	}