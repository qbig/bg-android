package sg.com.bigspoon.www.adapters;

import java.util.ArrayList;
import java.util.List;

import sg.com.bigspoon.www.R;
import android.content.Context;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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
	public Integer value=0;
	private String[] itemTitle = {"Butter","Maple syrup","Chocolate Pancakes","Pefect Companions"};
	private String[] itemTitleDescription = {"( 2nd butter onwards $0.50/ea )","( 2nd syrup onwards $1/ea )","",""};
    String[][] items = new String[][]{
    		{ "Garlic + Herbs", "Salted", "Unsalted", "Rum + Raisin"},
    		{ "Maple syrup"},
    		{ "No","Yes (+$1)","test","test1","test2"},
    		{ "Corn Beef Hash (+$2.5)", "Scrambled Eggs (+$2.5)", "Strawberry (+$1.5)", "Sauteed Mushrooms (+$2.5)","Banana (+$1.5)", "Chicken Chipolata (+$2.5)","Bacon (+$2.5)"}
    };
    private String[] type = {"count","count","radio","count"};
    private int[] itemnumber = {items[0].length,items[1].length,items[2].length,items[3].length};
    int selectedPosition = 0;
    //RadioButton rb;
    List<Model> list = new ArrayList<Model>(){{
    	for(int i=0;i<items.length;i++){
			if(type[i]!="radio"){
    		for(int j=0;j<items[i].length;j++){
    			add(new Model(items[i][j]));
    			}
    		}
    	}
    }};
    
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
    		return itemnumber[section];//Number of items per section
    }
    
    static class ViewHolder {
        protected TextView text;
		public ImageButton incre;
		public ImageButton decre;
		public TextView textView;
    }

    @Override
    public View getItemView(int section, int position, View convertView, final ViewGroup parent) {
    	
    	ViewHolder viewHolder = null;
    	
    	if (null == convertView) {
    		LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		 //switch(type[section]){
    		 //case "radio":
    			 convertView = inflator.inflate(R.layout.activity_modifier_addon, null);  
    		//	 break;
    		 //case "count":
    		//	 convertView =  inflator.inflate(R.layout.activity_modifier_counter, null);
    		//	 break;
    		// }
    	}else{
    		LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		convertView = inflator.inflate(R.layout.activity_modifier_addon, null);  
        	viewHolder = (ViewHolder) convertView.getTag();
    	}
    	
        if(type[section]=="radio"){
        //if (convertView == null) {
               // LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                //convertView = inflator.inflate(R.layout.activity_modifier_radiobutton, null);            
                //RadioButton rb = (RadioButton)layout.findViewById(R.id.radioButton1);             
        //}
        	FrameLayout layoutholder = (FrameLayout)convertView.findViewById(R.id.frameLayout1);
        	RadioButton radiobtn = new RadioButton(parent.getContext());
        	final float scale = parent.getContext().getResources().getDisplayMetrics().density;
        	int padding_5dp = (int) (5 * scale + 0.5f);
        	int padding_25dp = (int) (25 * scale + 0.5f);
        	int padding_37dp = (int) (37 * scale + 0.5f);
        	int padding_35dp = (int) (35 * scale + 0.5f);
        	FrameLayout.LayoutParams params =new FrameLayout.LayoutParams(padding_35dp, padding_35dp);
        	params.gravity=Gravity.RIGHT;
        	params.setMargins(0,0,padding_37dp,0);
        	radiobtn.setLayoutParams(params);
        	radiobtn.setPadding(padding_5dp,padding_5dp,padding_25dp,padding_5dp);
        	radiobtn.setBackgroundResource(R.drawable.radiobackground);
        	radiobtn.setButtonDrawable(android.R.color.transparent);
        	layoutholder.addView(radiobtn);

        ((TextView) convertView.findViewById(R.id.text)).setText(items[section][position]); 
        //RadioButton rb = (RadioButton)convertView.findViewById(R.id.radioButton1); 
        radiobtn.setChecked(position == selectedPosition);
        radiobtn.setTag(position);
        radiobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPosition = (Integer)view.getTag();
                notifyDataSetChanged();
            }
        });

        return convertView;
        }
        
        
        

        if(type[section]=="count"){
    		//if (convertView == null) {
        	FrameLayout layoutholder = (FrameLayout)convertView.findViewById(R.id.frameLayout1);
        	ImageButton imagebtn1 = new ImageButton(parent.getContext());
        	final float scale = parent.getContext().getResources().getDisplayMetrics().density;
        	int padding_30dp = (int) (30 * scale + 0.5f);
        	int padding_35dp = (int) (35 * scale + 0.5f);
        	int width=padding_30dp;
        	int height=padding_30dp;
        	FrameLayout.LayoutParams params1 =new FrameLayout.LayoutParams(width, height,Gravity.CENTER|Gravity.LEFT);
        	//params1.gravity=Gravity.LEFT;
        	imagebtn1.setLayoutParams(params1);
        	//imagebtn1.setPadding(padding_5dp,padding_5dp,padding_25dp,padding_5dp);
        	imagebtn1.setBackgroundResource(R.drawable.btn_minus_with_circle);
        	layoutholder.addView(imagebtn1);
        	
        	
        	ImageButton imagebtn2 = new ImageButton(parent.getContext());
        	FrameLayout.LayoutParams params2 =new FrameLayout.LayoutParams(width, height,Gravity.CENTER|Gravity.RIGHT);
        	//params2.gravity=Gravity.RIGHT;
        	imagebtn2.setLayoutParams(params2);
        	//imagebtn1.setPadding(padding_5dp,padding_5dp,padding_25dp,padding_5dp);
        	imagebtn2.setBackgroundResource(R.drawable.add_btn_with_circle);
        	layoutholder.addView(imagebtn2);
        	
        	
        	TextView textview3 = new TextView(parent.getContext());
        	int width3=padding_35dp;
        	int height3=padding_35dp;
        	FrameLayout.LayoutParams params3 =new FrameLayout.LayoutParams(width3, height3);
        	params3.gravity=Gravity.CENTER;
        	textview3.setLayoutParams(params3);
        	textview3.setBackgroundResource(R.drawable.circle_for_text);
        	textview3.setText("0");
        	textview3.setTextColor( parent.getContext().getResources().getColor(R.color.black));
        	textview3.setTextSize(19);
        	textview3.setGravity(Gravity.CENTER);
        	textview3.setId(R.id.textPicker);
        	layoutholder.addView(textview3);
        	
        	
        	
    	    viewHolder = new ViewHolder();
            //LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //convertView =  inflator.inflate(R.layout.activity_modifier_counter, null);
            viewHolder.text=((TextView) convertView.findViewById(R.id.text));
            viewHolder.decre=imagebtn1;
            viewHolder.incre=imagebtn2;
            viewHolder.textView=textview3;
            final TextView temp= viewHolder.textView;

            //add listener for the text change in the textview

            viewHolder.decre.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                	value=Integer.parseInt(temp.getText().toString())-1;
                	if(value<0){value=0;}
                	temp.setText(String.valueOf(value));
                }
            });
            
            viewHolder.incre.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                	value=Integer.parseInt(temp.getText().toString())+1;
                	temp.setText(String.valueOf(value));
                }
            });
            
           viewHolder.textView.addTextChangedListener(new TextWatcher() {
            @Override
            	public void afterTextChanged(Editable s) {
            	// TODO Auto-generated method stub                       
                }
			@Override
				public void onTextChanged(CharSequence arg0, int arg1,
						int arg2, int arg3) {
					// TODO Auto-generated method stub
            	//Toast.makeText(parent.getContext(), "this is my Toast message!!! =)",
            	//		   Toast.LENGTH_LONG).show();
        	     int getPosition = (Integer) (temp.getTag());
        	     list.get(getPosition).setValue(arg0.toString());
				}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
        });
            convertView.setTag(viewHolder);
            convertView.setTag(R.id.textPicker, viewHolder.textView);
           // }else {
            //    viewHolder = (ViewHolder) convertView.getTag();
           // }

    		int identifier = position;
    		for(int i=0;i<section;i++){
    			if(type[i]!="radio"){
        		identifier = items[i].length+identifier;
    			}
    		}
    		viewHolder.textView.setTag(identifier);
    		viewHolder.text.setText(items[section][position]);   
            viewHolder.textView.setText(list.get(identifier).getValue());
 

            return convertView;
    	}   	
        else{return convertView;}
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
