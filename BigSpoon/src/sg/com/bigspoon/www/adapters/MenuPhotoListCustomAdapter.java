package sg.com.bigspoon.www.adapters;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.R.id;
import sg.com.bigspoon.www.R.layout;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuPhotoListCustomAdapter extends ArrayAdapter<String>{
	Context context;
	int[] images;
	String[] categories;
	public MenuPhotoListCustomAdapter(Context context,String[] categories,int[] images) {
		super(context, R.layout.menu_photo_item_row,R.id.itemdesc,categories);
		this.context=context;
		this.images=images;
		this.categories=categories;
		// TODO Auto-generated constructor stub
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View row=convertView;
		if(convertView==null)
		{
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row=inflater.inflate(R.layout.menu_photo_item_row,parent,false);
		}
		ImageView image= (ImageView) row.findViewById(R.id.menuitem);
		ImageButton ibutton = (ImageButton) row.findViewById(R.id.addbutton);
		TextView categoryName=(TextView) row.findViewById(R.id.itemdesc);
		image.setImageResource(images[position]);
		//categoryName.setText(categories[position]);
		//categoryName.setText("Testing...!!!");
		
		return row;
	}

}
