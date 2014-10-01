
package sg.com.bigspoon.www.adapters;

import sg.com.bigspoon.www.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ActionBarMenuAdapter extends ArrayAdapter<String> {
	 Context context;
	 int count;
	 String[] options;
	 int[] icons;

	public ActionBarMenuAdapter(Context context, int count) {
		super(context, 0);
		this.context = context;
		this.count = count;
	}
	public int getCount() {
		return count;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;

		if (row == null) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.layout_action_bar, parent, false);

			TextView textViewTitle = (TextView) row
					.findViewById(R.id.grd_option_txt);
			ImageView imageViewIte = (ImageView) row
					.findViewById(R.id.grd_option_icon);
		}
		return row;
	}

}
