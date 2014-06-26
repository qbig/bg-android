package sg.com.bigspoon.www.adapters;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.R.id;
import sg.com.bigspoon.www.R.layout;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomListOfUserReview extends ArrayAdapter<String>{
	
	
	private final Activity context;
	private final String[] number;

	
	public CustomListOfUserReview(Activity context,String[] number) {	
		super(context, R.layout.list_user_review, number );
		this.context = context;
		this.number=number;
	
		}

	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
	
	LayoutInflater inflater = context.getLayoutInflater();
	View rowView= inflater.inflate(R.layout.list_user_review, null, true);
	TextView txtNumber = (TextView) rowView.findViewById(R.id.list_review_text);

	txtNumber.setText(number[position]);
	
	return rowView;
	}
}
