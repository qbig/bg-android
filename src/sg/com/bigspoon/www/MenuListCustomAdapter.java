package sg.com.bigspoon.www;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuListCustomAdapter extends ArrayAdapter<String>{
	Context context;
	int[] images;
	String[] categories;
	public MenuListCustomAdapter(Context context,String[] categories,int[] images) {
		super(context, R.layout.category_row,R.id.categoryname,categories);
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
			row=inflater.inflate(R.layout.menuitem_row,parent,false);
		}
		ImageView image= (ImageView) row.findViewById(R.id.menuitem);
		ImageButton ibutton = (ImageButton) row.findViewById(R.id.addbutton);
		//TextView categoryName=(TextView) row.findViewById(R.id.itemdesc);
		image.setImageResource(images[position]);
		//categoryName.setText("Testing...!!!");
		
		return row;
	}

}
