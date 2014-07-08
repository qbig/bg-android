package sg.com.bigspoon.www.adapters;

import sg.com.bigspoon.www.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import za.co.immedia.pinnedheaderlistview.SectionedBaseAdapter;

public class TestSectionedAdapter extends SectionedBaseAdapter {
//	{
//		  "itemTextColor":"#FFFFFF",
//		  "itemTitleColor":"#E29B5C",
//		  "sections":[
//		    {
//		      "threshold":0.5,
//		      "itemTitle":"Butter",
//		      "type":"count",
//		      "itemTitleDescription":"2nd butter onwards $0.50/ea",
//		      "items":{
//		        "Garlic + Herbs":0.5,
//		        "Salted":0.5,
//		        "Unsalted":0.5,
//		        "Rum + Raisin":0.5
//		      }
//		    },
//		    {
//		      "threshold":1,
//		      "itemTitle":"Maple syrup",
//		      "type":"count",
//		      "itemTitleDescription":"2nd syrup onwards $1/ea",
//		      "items":{
//		        "Maple syrup":1
//		      }
//		    },
//		    {
//		      "itemTitle":"Chocolate Pancakes",
//		      "type":"radio",
//		      "itemTitleDescription":"",
//		      "items":{
//		        "Yes (+$1)":1,
//		        "No":0
//		      }
//		    },
//		    {
//		      "threshold":0,
//		      "itemTitle":"Perfect Companions",
//		      "type":"count",
//		      "itemTitleDescription":"",
//		      "items":{
//		        "Chicken Chipolata (+$2.5)":2.5,
//		        "Scrambled Eggs (+$2.5)":2.5,
//		        "Strawberry (+$1.5)":1.5,
//		        "Sauteed Mushrooms (+$2.5)":2.5,
//		        "Banana (+$1.5)":1.5,
//		        "Corn Beef Hash (+$2.5)":2.5,
//		        "Bacon (+$2.5)":2.5
//		      }
//		    }
//		  ],
//		  "backgroundColor":"#434751"
//		}
	private String[] sectionname = {"Butter","Maple syrup","Chocolate Pancakes","Pefect Companions"};
	private String[] sectionsub = {"( 2nd butter onwards $0.50/ea )","( 2nd syrup onwards $1/ea )","",""};
    String[][] itemnames = new String[][]{
    		{ "Garlic + Herbs", "Salted", "Unsalted", "Rum + Raisin"},
    		{ "Maple syrup"},
    		{ ""},//The text for radio button items are initialized in the layout
    		{ "Corn Beef Hash (+$2.5)", "Scrambled Eggs (+$2.5)", "Strawberry (+$1.5)", "Sauteed Mushrooms (+$2.5)","Banana (+$1.5)", "Chicken Chipolata (+$2.5)","Bacon (+$2.5)"}
    };
    private int[] itemnumber = {itemnames[0].length,itemnames[1].length,itemnames[2].length,itemnames[3].length};
    
    
	@Override
    public Object getItem(int section, int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int section, int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getSectionCount() {
        return sectionname.length;//Number of sections
    }

    @Override
    public int getCountForSection(int section) {
        return itemnumber[section];//Number of items per section
    }

    @Override
    public View getItemView(int section, int position, View convertView, ViewGroup parent) {
        LinearLayout layout = null;
        if (convertView == null) {
        	if(section==2){
        		//for Chocolate Pancakes
                LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                layout = (LinearLayout) inflator.inflate(R.layout.activity_modifier_radiobutton, null);
                
        	}else{
                LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                layout = (LinearLayout) inflator.inflate(R.layout.activity_modifier_counter, null);    
                ((TextView) layout.findViewById(R.id.text)).setText(itemnames[section][position]);
        	}
        } else {
            layout = (LinearLayout) convertView;
        }
        
        return layout;
    }

    @Override
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
        LinearLayout layout = null;
        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = (LinearLayout) inflator.inflate(R.layout.header_item, null);
        } else {
            layout = (LinearLayout) convertView;
        }
        ((TextView) layout.findViewById(R.id.maintitle)).setText(sectionname[section]);
        ((TextView) layout.findViewById(R.id.subtitle)).setText(sectionsub[section]);
        return layout;
    }

}
