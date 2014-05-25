package sg.com.bigspoon.www;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoriesCustomAdapter extends ArrayAdapter<String>{
	Context context;
	int[] images;
	String[] categories;
	
	public CategoriesCustomAdapter(Context context, String[] categories,int[] images) {
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
			row=inflater.inflate(R.layout.category_row,parent,false);
		}
		ImageView image= (ImageView) row.findViewById(R.id.categoryimage);
		TextView categoryName=(TextView) row.findViewById(R.id.categoryname);
		image.setImageResource(images[position]);
		categoryName.setText(categories[position]);
		return row;
	}

}
