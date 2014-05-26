package sg.com.bigspoon.www;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomList extends ArrayAdapter<String>{
	
private final Activity context;
private final String[] web;
private final Integer[] imageId;
private final String[] webdesc;
public CustomList(Activity context,
String[] web, Integer[] imageId,String[] webdesc) {
super(context, R.layout.list_single, web );
this.context = context;
this.web = web;
this.imageId = imageId;
this.webdesc=webdesc;

}
@Override
public View getView(int position, View view, ViewGroup parent) {
LayoutInflater inflater = context.getLayoutInflater();
View rowView= inflater.inflate(R.layout.list_single, null, true);

TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
TextView txtDesc = (TextView) rowView.findViewById(R.id.desc);

ImageView imageView = (ImageView) rowView.findViewById(R.id.img);

txtTitle.setText(web[position]);
txtDesc.setText(webdesc[position]);
imageView.setImageResource(imageId[position]);

return rowView;
}
}
