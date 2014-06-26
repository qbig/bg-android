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
import android.content.Context;


public class CustomItemsList extends ArrayAdapter<String>{
	
	
	private final Activity context;
	private final Integer[] minusImage;
	private final String[] number;
	private final Integer[] addImage;
	private final String[] itemdesc;
	private final String[] price;
	
	
	public CustomItemsList(Activity context,Integer[] minusImage,
			String[] number,Integer[] addImage,String[] itemdesc,String[] price)
			{			super(context, R.layout.activity_items, number);
			this.context = context;
			this.minusImage = minusImage;
			this.number = number;
			this.addImage = addImage;
			this.itemdesc = itemdesc;
			this.price = price;
				}

	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
	
	LayoutInflater inflater = context.getLayoutInflater();
	View rowView= inflater.inflate(R.layout.list_items, null, true);

	ImageView minusImageView = (ImageView) rowView.findViewById(R.id.imgMinus);
	TextView numberView = (TextView) rowView.findViewById(R.id.quantitytxt);
	ImageView addImageView = (ImageView) rowView.findViewById(R.id.imgPlus);

	TextView itemdescView = (TextView) rowView.findViewById(R.id.Descriptiontxt);
	TextView priceView = (TextView) rowView.findViewById(R.id.DescriptionitemPrice);
	
	
	minusImageView.setImageResource(minusImage[position]);
	numberView.setText(number[position]);
	addImageView.setImageResource(addImage[position]);
	
	itemdescView.setText(itemdesc[position]);
	priceView.setText(price[position]);
	
	return rowView;
	
	}
	}
