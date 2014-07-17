package sg.com.bigspoon.www.adapters;

import java.util.ArrayList;

import sg.com.bigspoon.www.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ExpandableAdapter extends BaseExpandableListAdapter 
{

    private Activity activity;
    private LayoutInflater inflater;
    private final ArrayList<String> number;
	private final ArrayList<String> name;
	private final ArrayList<String> price;
	

    // constructor
    public ExpandableAdapter(ArrayList<String> number,ArrayList<String> name,ArrayList<String> price)
    {
    	this.number = number;
		this.name = name;
		this.price = price;
    }

    public void setInflater(LayoutInflater inflater, Activity activity) 
    {
        this.inflater = inflater;
        this.activity = activity;
    }
    
    // method getChildView is called automatically for each child view.
    //  Implement this method as per your requirement
    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) 
    {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_items_child, null);
        }
        
        return convertView;
    }

    // method getGroupView is called automatically for each parent item
    // Implement this method as per your requirement
    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) 
    {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_items_parent, null);
        }

    	TextView numberView = (TextView) convertView.findViewById(R.id.quantitytxt);
    	TextView itemdescView = (TextView) convertView.findViewById(R.id.descriptiontxt);
    	TextView priceView = (TextView) convertView.findViewById(R.id.descriptionitemPrice);
    	
    	numberView.setText(number.get(groupPosition));   	
    	itemdescView.setText(name.get(groupPosition));
    	priceView.setText(price.get(groupPosition));
    	
    	ImageButton minusButton = (ImageButton) convertView.findViewById(R.id.imgMinus);
    	ImageButton plusButton = (ImageButton) convertView.findViewById(R.id.imgPlus);
    	minusButton.setOnClickListener(new OnClickListener(){
    		@Override
            public void onClick(View view) {
    			String temp;
    			
				if(Integer.parseInt(number.get(groupPosition))-1<0) temp = "0";
				else temp = Integer.toString(Integer.parseInt(number.get(groupPosition))-1);
				
    			number.set(groupPosition, temp);
    			notifyDataSetChanged();
    			}
    		 });
    	
    	plusButton.setOnClickListener(new OnClickListener(){
    		@Override
            public void onClick(View view) {
    			String temp = Integer.toString(Integer.parseInt(number.get(groupPosition))+1);
    			number.set(groupPosition, temp);
    			notifyDataSetChanged();
    			}
    		 });

        return convertView;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) 
    {
        return null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) 
    {
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) 
    {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) 
    {
        return null;
    }

    @Override
    public int getGroupCount() 
    {
        return name.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) 
    {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition)
    {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) 
    {
        return 0;
    }

    @Override
    public boolean hasStableIds() 
    {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return false;
    }

}