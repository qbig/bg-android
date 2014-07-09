package sg.com.bigspoon.www.adapters;

import sg.com.bigspoon.www.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
	private String[] itemTitle = {"Butter","Maple syrup","Chocolate Pancakes","Pefect Companions"};
	private String[] itemTitleDescription = {"( 2nd butter onwards $0.50/ea )","( 2nd syrup onwards $1/ea )","",""};
    String[][] items = new String[][]{
    		{ "Garlic + Herbs", "Salted", "Unsalted", "Rum + Raisin"},
    		{ "Maple syrup"},
    		{ "No","Yes (+$1)"},
    		{ "Corn Beef Hash (+$2.5)", "Scrambled Eggs (+$2.5)", "Strawberry (+$1.5)", "Sauteed Mushrooms (+$2.5)","Banana (+$1.5)", "Chicken Chipolata (+$2.5)","Bacon (+$2.5)"}
    };
    private String[] type = {"count","count","radio","count"};
    private int[] itemnumber = {items[0].length,items[1].length,items[2].length,items[3].length};
    int selectedPosition = 0;
    //RadioButton rb;
    
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
        return itemTitle.length;//Number of sections
    }

    @Override
    public int getCountForSection(int section) {
    	if(type[section]=="radio"){return 1;}
    	else{	
    		return itemnumber[section];//Number of items per section
    	}
    }

    @Override
    public View getItemView(int section, int position, View convertView, ViewGroup parent) {
    	LinearLayout layout = null;
        if (convertView == null) {
        	if(type[section]=="radio"){
        		//for Chocolate Pancakes
                LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                
                if(items[section].length==2){
                	layout = (LinearLayout) inflator.inflate(R.layout.activity_modifier_radiobutton2, null);
                	((TextView) layout.findViewById(R.id.text1)).setText(items[section][0]);
                	((TextView) layout.findViewById(R.id.text2)).setText(items[section][1]);
                	
                }
                if(items[section].length==3){
                	layout = (LinearLayout) inflator.inflate(R.layout.activity_modifier_radiobutton3, null);
                	((TextView) layout.findViewById(R.id.text1)).setText(items[section][0]);
                	((TextView) layout.findViewById(R.id.text2)).setText(items[section][1]);
                	((TextView) layout.findViewById(R.id.text3)).setText(items[section][2]);
                }
                if(items[section].length==4){
                	layout = (LinearLayout) inflator.inflate(R.layout.activity_modifier_radiobutton4, null);
                	((TextView) layout.findViewById(R.id.text1)).setText(items[section][0]);
                	((TextView) layout.findViewById(R.id.text2)).setText(items[section][1]);
                	((TextView) layout.findViewById(R.id.text3)).setText(items[section][2]);
                	((TextView) layout.findViewById(R.id.text4)).setText(items[section][3]);
                }
                if(items[section].length==5){
                	layout = (LinearLayout) inflator.inflate(R.layout.activity_modifier_radiobutton5, null);
                	((TextView) layout.findViewById(R.id.text1)).setText(items[section][0]);
                	((TextView) layout.findViewById(R.id.text2)).setText(items[section][1]);
                	((TextView) layout.findViewById(R.id.text3)).setText(items[section][2]);
                	((TextView) layout.findViewById(R.id.text4)).setText(items[section][3]);
                	((TextView) layout.findViewById(R.id.text5)).setText(items[section][4]);
                }
                if(items[section].length==6){
                	layout = (LinearLayout) inflator.inflate(R.layout.activity_modifier_radiobutton6, null);
                	((TextView) layout.findViewById(R.id.text1)).setText(items[section][0]);
                	((TextView) layout.findViewById(R.id.text2)).setText(items[section][1]);
                	((TextView) layout.findViewById(R.id.text3)).setText(items[section][2]);
                	((TextView) layout.findViewById(R.id.text4)).setText(items[section][3]);
                	((TextView) layout.findViewById(R.id.text5)).setText(items[section][4]);
                	((TextView) layout.findViewById(R.id.text6)).setText(items[section][5]);
                }
                
        	}
        	else{
                LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                layout = (LinearLayout) inflator.inflate(R.layout.activity_modifier_counter, null);    
                ((TextView) layout.findViewById(R.id.text)).setText(items[section][position]);
        	}
        } else {
            layout = (LinearLayout) convertView;
        }
        
        return layout;
       // LinearLayout layout = null;
        
        /*if(type[section]=="radio"){
        //if (convertView == null) {
                LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflator.inflate(R.layout.activity_modifier_radiobutton, null);
                //RadioButton rb = (RadioButton)layout.findViewById(R.id.radioButton1);             
        //}   
        ((TextView) convertView.findViewById(R.id.text)).setText(items[section][position]); 
        RadioButton rb = (RadioButton)convertView.findViewById(R.id.radioButton1); 
        rb.setChecked(position == selectedPosition);
        rb.setTag(position);
        rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPosition = (Integer)view.getTag();
                notifyDataSetChanged();
            }
        });

        return convertView;
        }
        
        if(type[section]=="count"){
    		if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView =  inflator.inflate(R.layout.activity_modifier_counter, null);              
            }
    		((TextView) convertView.findViewById(R.id.text)).setText(items[section][position]);    
            return convertView;
    	}   	
        else{return convertView;}*/
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
        ((TextView) layout.findViewById(R.id.maintitle)).setText(itemTitle[section]);
        ((TextView) layout.findViewById(R.id.subtitle)).setText(itemTitleDescription[section]);
        return layout;
    }

}
