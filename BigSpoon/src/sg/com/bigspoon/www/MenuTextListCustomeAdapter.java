package sg.com.bigspoon.www;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MenuTextListCustomeAdapter extends ArrayAdapter<String>{
	Context context;
	String[] itemnames;
	String[] itemdesc;
	String[] itemprice;
	public MenuTextListCustomeAdapter(Context context,String[] itemnames,String[] itemdesc,String[] itemprice) {
		super(context,R.layout.menu_text_item_row,R.id.textitemname,itemnames);
		this.context=context;
		this.itemnames=itemnames;
		this.itemdesc=itemdesc;
		this.itemprice=itemprice;
		// TODO Auto-generated constructor stub
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View row=convertView;
		if(convertView==null)
		{
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row=inflater.inflate(R.layout.menu_text_item_row,parent,false);
		}

		TextView itemName=(TextView) row.findViewById(R.id.textitemname);
		TextView itemDesc=(TextView) row.findViewById(R.id.textitemdesc);
		TextView itemPrice=(TextView) row.findViewById(R.id.textitemprice);
	
		itemName.setText(itemnames[position]);
		itemDesc.setText(itemdesc[position]);
		itemPrice.setText(itemprice[position]);
		return row;
	}

}