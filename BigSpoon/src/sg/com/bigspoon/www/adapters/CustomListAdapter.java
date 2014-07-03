package sg.com.bigspoon.www.adapters;


import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.R.id;
import sg.com.bigspoon.www.R.layout;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomListAdapter extends ArrayAdapter<String>{
	
private final Activity context;
private final String[] web;
private final Integer[] imageId;
private final String[] webdesc;
private final String[] comingsoon;
public CustomListAdapter(Activity context,
String[] web, Integer[] imageId,String[] webdesc,String[] comingsoon) {
super(context, R.layout.list_single, web );
this.context = context;
this.web = web;
this.imageId = imageId;
this.webdesc=webdesc;
this.comingsoon=comingsoon;

}
@Override
public View getView(int position, View view, ViewGroup parent) {
LayoutInflater inflater = context.getLayoutInflater();
View rowView= inflater.inflate(R.layout.list_single, null, true);

TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
TextView txtDesc = (TextView) rowView.findViewById(R.id.desc);
TextView txtComing = (TextView) rowView.findViewById(R.id.coming);

ImageView imageView = (ImageView) rowView.findViewById(R.id.img);

txtTitle.setText(web[position]);
txtDesc.setText(webdesc[position]);
txtComing.setText(comingsoon[position]);
imageView.setImageResource(imageId[position]);

return rowView;
}
}
