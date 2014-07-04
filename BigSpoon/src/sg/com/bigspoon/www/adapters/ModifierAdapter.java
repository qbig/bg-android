package sg.com.bigspoon.www.adapters;

import java.util.ArrayList;
import java.util.TreeSet;

import sg.com.bigspoon.www.R;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.os.Build;

public class ModifierAdapter extends BaseAdapter {
	
	 private static final int TYPE_ITEM = 0;
	    private static final int TYPE_SEPARATOR = 1;
	    private static final int TYPE_ITEM_RADIO = 2;
	 
	    private ArrayList<String> mData = new ArrayList<String>();
	    private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();
	 
	    private LayoutInflater mInflater;
	 
	    public ModifierAdapter(Context context) {
	        mInflater = (LayoutInflater) context
	                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    }
	 
	    public void addItem(final String item) {
	        mData.add(item);
	        notifyDataSetChanged();
	    }
	 
	    public void addSectionHeaderItem(final String item) {
	        mData.add(item);
	        sectionHeader.add(mData.size() - 1);
	        notifyDataSetChanged();
	    }
	    public void addRadiobuttonItem(final String item) {
	        mData.add(item);
	        notifyDataSetChanged();
	    }
	    @Override
	    public int getItemViewType(int position) {
	    	
	    	  if (position == 1)
	    	  {
	    		  return TYPE_SEPARATOR; 
	    	  }
	    	  else if(position == 2)
	    	  {
	    		  return TYPE_ITEM_RADIO; 
	    	  }
	    	  else
	    	  {
	    		  return TYPE_ITEM;
	    		  
	    	  }
	    }
	 
	    @Override
	    public int getViewTypeCount() {
	        return 3;
	    }
	 
	    @Override
	    public int getCount() {
	        return mData.size();
	    }
	 
	    @Override
	    public String getItem(int position) {
	        return mData.get(position);
	    }
	 
	    @Override
	    public long getItemId(int position) {
	        return position;
	    }
	 
	    public View getView(int position, View convertView, ViewGroup parent) {
	        ViewHolder holder = null;
	        int rowType = getItemViewType(position);
	 
	        if (convertView == null) {
	            holder = new ViewHolder();
	            switch (rowType) {
	            case TYPE_ITEM:
	                convertView = mInflater.inflate(R.layout.activity_modifier_counter, null);
	                holder.textView = (TextView) convertView.findViewById(R.id.text);
	                break;
	            case TYPE_SEPARATOR:
	                convertView = mInflater.inflate(R.layout.modifier_header, null);
	                holder.textView = (TextView) convertView.findViewById(R.id.textSeparator);
	                break;
	            case TYPE_ITEM_RADIO:
	                convertView = mInflater.inflate(R.layout.activity_modifier_radiobutton, null);
	                holder.textView = (TextView) convertView.findViewById(R.id.text);
	                break;
	            }
	            convertView.setTag(holder);
	        } else {
	            holder = (ViewHolder) convertView.getTag();
	        }
	        holder.textView.setText(mData.get(position));
	 
	        return convertView;
	    }
	 
	    public static class ViewHolder {
	        public TextView textView;
	    }

}
