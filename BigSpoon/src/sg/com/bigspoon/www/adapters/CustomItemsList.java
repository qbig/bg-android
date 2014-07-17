package sg.com.bigspoon.www.adapters;

import sg.com.bigspoon.www.R;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.R.id;
import sg.com.bigspoon.www.R.layout;
import sg.com.bigspoon.www.data.ThreadSafeSingleton;
import android.content.Context;


public class CustomItemsList extends ArrayAdapter<String>{


	private final Activity context;

	public CustomItemsList(Activity context)
			{			super(context, R.layout.activity_items, ThreadSafeSingleton.getInstance().numberOld);
			this.context = context;
				}


	@Override
	public int getCount(){
		return ThreadSafeSingleton.getInstance().numberOld.size();
		
	}
	@Override
	public View getView(int position, View view, ViewGroup parent) {

	LayoutInflater inflater = context.getLayoutInflater();
	View rowView= inflater.inflate(R.layout.list_items, null, true);

	TextView numberView = (TextView) rowView.findViewById(R.id.quantityText);
	TextView itemdescView = (TextView) rowView.findViewById(R.id.nameText);
	TextView priceView = (TextView) rowView.findViewById(R.id.priceText);

	numberView.setText(ThreadSafeSingleton.getInstance().numberOld.get(position));
	itemdescView.setText(ThreadSafeSingleton.getInstance().itemnameOld.get(position));
	priceView.setText(ThreadSafeSingleton.getInstance().priceOld.get(position));

	return rowView;

	}
	}