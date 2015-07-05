package sg.com.bigspoon.www.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import java.util.List;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.data.OutletModel;

import static sg.com.bigspoon.www.data.Constants.BASE_URL;
import static sg.com.bigspoon.www.data.Constants.getURL;

public class OutletListAdapter extends ArrayAdapter<OutletModel> {

	private final Activity context;

	public OutletListAdapter(Activity context, List<OutletModel> outlets) {
		super(context, R.layout.list_single, outlets);
		this.context = context;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.list_single, null, true);
		final OutletModel outlet = getItem(position);

		final TextView txtTitle = (TextView) rowView.findViewById(R.id.name);
		final TextView txtAddress = (TextView) rowView.findViewById(R.id.address);
		final TextView txtComingSoon = (TextView) rowView.findViewById(R.id.coming);

		txtTitle.setText(outlet.name);
		txtAddress.setText(outlet.address);
		if (outlet.isActive) {
			txtComingSoon.setVisibility(View.GONE);
		} else {
			txtComingSoon.setVisibility(View.VISIBLE);
		}

		final ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
		Ion.with(imageView).load(getURL(BASE_URL) + outlet.restaurant.icon.thumbnail);
		if (position == 0){
			rowView.setBackgroundResource(R.drawable.outlet_item_border);
		} else {
			rowView.setBackgroundResource(0);
		}
	

		return rowView;
	}
}
