package sg.com.bigspoon.www.activities;

import java.util.ArrayList;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.data.ThreadSafeSingleton;
import sg.com.bigspoon.www.data.MenuListItem;
import sg.com.bigspoon.www.data.MenuListTextItem;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MenuPhotoListActivity extends ActionBarActivity implements TabListener{
	//ViewPager viewpager;
	ActionBar actionbar;
	ListView listview;
	MainListViewAdapter adapter;
	Boolean isPhoto=true;
	//private ArrayList<ListItem> mList;  
	private static final String[] CONTENT = new String[] { "Popular Items",
			"Brunch", "Dinner", "BreakFast", "Beers", "Roasted" };
	
	String[] categories = { "Popular Items", "Brunch", "Dinner", "BreakFast",
			"Beers", "Roasted"}; 
	
	int[][] images = new int[][]{
			{ R.drawable.babycinno_640_400, R.drawable.banana_earl_grey_tart_640_400, R.drawable.honey_paprika_crispy_wings_640_400,R.drawable.iced_mocha_640_400, R.drawable.mushroom_melt_640_400, R.drawable.wittekerke_belguim_wheat_ale_1_640_400},
			{ R.drawable.banana_earl_grey_tart_640_400, R.drawable.honey_paprika_crispy_wings_640_400,R.drawable.iced_mocha_640_400, R.drawable.mushroom_melt_640_400, R.drawable.wittekerke_belguim_wheat_ale_1_640_400,R.drawable.babycinno_640_400},
			{ R.drawable.honey_paprika_crispy_wings_640_400,R.drawable.iced_mocha_640_400, R.drawable.mushroom_melt_640_400, R.drawable.wittekerke_belguim_wheat_ale_1_640_400,R.drawable.babycinno_640_400, R.drawable.banana_earl_grey_tart_640_400},
			{ R.drawable.iced_mocha_640_400, R.drawable.mushroom_melt_640_400, R.drawable.wittekerke_belguim_wheat_ale_1_640_400,R.drawable.babycinno_640_400, R.drawable.banana_earl_grey_tart_640_400, R.drawable.honey_paprika_crispy_wings_640_400},
			{ R.drawable.mushroom_melt_640_400, R.drawable.wittekerke_belguim_wheat_ale_1_640_400,R.drawable.babycinno_640_400, R.drawable.banana_earl_grey_tart_640_400, R.drawable.honey_paprika_crispy_wings_640_400,R.drawable.iced_mocha_640_400},
			{ R.drawable.wittekerke_belguim_wheat_ale_1_640_400,R.drawable.babycinno_640_400, R.drawable.banana_earl_grey_tart_640_400, R.drawable.honey_paprika_crispy_wings_640_400,R.drawable.iced_mocha_640_400, R.drawable.mushroom_melt_640_400}
	};
	
    String[][] itemnames = new String[][]{
    		{ "Bratwruts Ball1", "Mushroom Melt1", "Breakfast Butter1", "Avacado Eggs1","Bread1", "Roasted Chicken1"},
    		{ "Bratwruts Ball2", "Mushroom Melt2", "Breakfast Butter2", "Avacado Eggs2","Bread2", "Roasted Chicken2"},
    		{ "Bratwruts Ball3", "Mushroom Melt3", "Breakfast Butter3", "Avacado Eggs3","Bread3", "Roasted Chicken3"},
    		{ "Bratwruts Ball4", "Mushroom Melt4", "Breakfast Butter4", "Avacado Eggs4","Bread4", "Roasted Chicken4"},
    		{ "Bratwruts Ball5", "Mushroom Melt5", "Breakfast Butter5", "Avacado Eggs5","Bread5", "Roasted Chicken5"},
    		{ "Bratwruts Ball6", "Mushroom Melt6", "Breakfast Butter6", "Avacado Eggs6","Bread6", "Roasted Chicken6"}
    };
    
    String[][] itemdesc =new String[][]{ { "**1**refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash ", 
    		"**1**refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash ",
    		"**1**refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash ",
    		"**1**refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash ",
    		"**1**refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash ",
    		"**1**refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash "},
    		{ "**2**refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash ", 
        	  "**2**refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash ",
        	  "**2**refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash ",
        	  "**2**refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash ",
        	  "**2**refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash ",
        	  "**2**refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash "},
      		{ "**3**refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash ", 
              "**3**refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash ",
              "**3**refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash ",
           	  "**3**refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash ",
           	  "**3**refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash ",
           	  "**3**refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash "},
           	{ "**4**refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash ", 
          	  "**4**refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash ",
              "**4**refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash ",
          	  "**4**refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash ",
          	  "**4**refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash ",
          	  "**4**refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash "},
          	{ "**5**refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash ", 
              "**5**refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash ",
           	  "**5**refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash ",
           	  "**5**refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash ",
           	  "**5**refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash ",
           	  "**5**refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash "},
      		{ "**6**refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash ", 
              "**6**refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash ",
              "**6**refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash ",
           	  "**6**refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash ",
          	  "**6**refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash ",
              "**6**refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash "}
    };
    
    String[][] itemprice = new String[][]{
    		{ "8.0", "8.0", "8.0", "8.0","8.0", "8.0"},
    		{ "8.0", "8.0", "8.0", "8.0","8.0", "8.0"},
    		{ "8.0", "8.0", "8.0", "8.0","8.0", "8.0"},
    		{ "8.0", "8.0", "8.0", "8.0","8.0", "8.0"},
    		{ "8.0", "8.0", "8.0", "8.0","8.0", "8.0"},
    		{ "8.0", "8.0", "8.0", "8.0","8.0", "8.0"}
    };
	
	public ArrayList<MenuListItem>[] totalList= (ArrayList<MenuListItem>[]) new ArrayList[categories.length];
	
	public ArrayList<MenuListTextItem>[] totalTextList= (ArrayList<MenuListTextItem>[]) new ArrayList[categories.length];
    //m indicates the current selected tab
	int m=0,startposition=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_tabs);
		View v = findViewById(R.id.gv_action_menu);
		v.getBackground().setAlpha(230);
		
		Bundle extras = getIntent().getExtras();
		//Obtain the parameter passed by CatergoriesListActivity.java
		if (extras != null) {
		    startposition = extras.getInt("startposition");
		}

		TextView cornertext1;
		cornertext1=(TextView)findViewById(R.id.corner);
		ThreadSafeSingleton.getInstance().corner=cornertext1;
		if(ThreadSafeSingleton.getInstance().totalOrderNumber!=0){
		cornertext1.setVisibility(View.VISIBLE);
		cornertext1.setText(String.valueOf(ThreadSafeSingleton.getInstance().totalOrderNumber));	
		}
		
		
	   	for(int k=0;k<totalList.length;k++)
	   	{
	   		totalList[k] = new ArrayList<MenuListItem>();  
	   	}
	   	
	   	for(int k=0;k<totalTextList.length;k++)
	   	{
	   		totalTextList[k] = new ArrayList<MenuListTextItem>();  
	   	}
	   	
		listview = (ListView)findViewById(R.id.list); 
		Resources res = this.getResources();  
		//mList = new ArrayList<MenuPhotoListActivity.ListItem>();  
		
		for(int i=0;i<6;i++){
		  for(int j=0;j<6;j++){
			  MenuListItem item = new MenuListItem();  
           item.setImage(res.getDrawable(images[i][j]));  
           //item.setNumber(0);
           totalList[i].add(item);  
		  }
		}
		
		
		for(int i=0;i<6;i++){
			  for(int j=0;j<6;j++){
				  MenuListTextItem item = new MenuListTextItem();  
	           item.setItemname(itemnames[i][j]);  
	           item.setItemDesc(itemdesc[i][j]); 
	           item.setItemPrice(itemprice[i][j]); 
	           //item.setNumber(0);
	           totalTextList[i].add(item);  
			  }
			}
		
		LayoutInflater inflater=getLayoutInflater();
		
		ViewGroup footer = (ViewGroup) inflater.inflate(R.layout.footer, listview,false);
		listview.addFooterView(footer, null, false);
		
		
		adapter = new MainListViewAdapter();  
		listview.setAdapter(adapter);
		
        listview.setOnItemClickListener(new OnItemClickListener(){
		@Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			//if(isPhoto==false){
				isPhoto=true;
				getActionBarView().findViewById(R.id.toggleButton).setBackgroundResource(R.drawable.list_icon);
			    adapter.notifyDataSetChanged();
			    listview.setSelection(position);
			  //  }	-
			    
			}
		 });
			
		/*viewpager = (ViewPager) findViewById(R.id.pager);
		viewpager
				.setAdapter(new GoogleMusicAdapter(getSupportFragmentManager()));*/
		actionbar = getActionBar();
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		for (int i = 0; i < CONTENT.length; i++) {
			actionbar.addTab(actionbar.newTab().setText(CONTENT[i]).setTabListener(this));
		}
		
		actionbar.setSelectedNavigationItem(startposition); 
		//set the initial tab position according to the obtained parameter
		
		/*viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				actionbar.setSelectedNavigationItem(position);
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});*/
		loadMenu();
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.menu_list, menu);
		View mActionBarView = getLayoutInflater().inflate(R.layout.action_bar,
				null);
		actionbar.setCustomView(mActionBarView);
		actionbar.setIcon(R.drawable.dummy_icon);
		actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME);
		TextView title = (TextView) mActionBarView.findViewById(R.id.title);
		title.setText("Menu List");
		ImageButton togglebutton = (ImageButton) mActionBarView
				.findViewById(R.id.toggleButton);
		togglebutton.setBackgroundResource(R.drawable.list_icon);
		ImageButton ibItem1 = (ImageButton) mActionBarView
				.findViewById(R.id.btn_logout);
		ibItem1.setImageResource(R.drawable.home_with_arrow);
		RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	    params.addRule(RelativeLayout.CENTER_VERTICAL);
		ibItem1.setLayoutParams(params);
	    ibItem1.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
		ibItem1.setPadding(-2, 0, 0, 0);
		
		
		StateListDrawable states = new StateListDrawable();
		states.addState(new int[] {android.R.attr.state_pressed},
		    getResources().getDrawable(R.drawable.home_with_arrow_pressed));
		states.addState(new int[] { },
		    getResources().getDrawable(R.drawable.home_with_arrow));
		ibItem1.setImageDrawable(states);
		
		
		ibItem1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// ...

				Intent intent = new Intent(getApplicationContext(),
						OutListActivity.class);
				startActivity(intent);
			}
		});

		ImageButton ibItem2 = (ImageButton) mActionBarView
				.findViewById(R.id.order_history);
		ibItem2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// ...

				Intent intent = new Intent(getApplicationContext(),
						OrderHistoryListActivity.class);
				startActivity(intent);
			}
		});

		togglebutton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// ...
				if(isPhoto==true){
				isPhoto=false;
				view.setBackgroundResource(R.drawable.photo_icon);	
				}
				else{isPhoto=true;
				view.setBackgroundResource(R.drawable.list_icon);}
				adapter.notifyDataSetChanged();
				//add code to change the drawable of the icon

				/*Intent intent = new Intent(getApplicationContext(),
						MenuTextListActivity.class);
				startActivity(intent);*/
			}
		});
		return super.onCreateOptionsMenu(menu);
	}
	
	
	class MainListViewAdapter extends BaseAdapter  {
		/*public GoogleMusicAdapter(FragmentManager fm) {
			super(fm);
		}*/
		
		@Override
		public Object getItem(int position) {
			if(isPhoto==true){return totalList[m].get(position); }
			else{return totalTextList[m].get(position);}
		}
		
        @Override  
	    public long getItemId(int position) {  
	            // TODO Auto-generated method stub  
	            return position;  
	    }  
	       
		/*@Override
		public CharSequence getPageTitle(int position) {
			return CONTENT[position % CONTENT.length];
		}*/

		@Override
		public int getCount() {
			if(isPhoto==true){return totalList[m].size();}
			else{return totalTextList[m].size();}
		}
		
		@Override  
	    public View getView(final int position, View convertView, ViewGroup parent) {  
	         ListItemView listItemView;  
	         ListTextItemView listTextItemView;  
	  
	         if(isPhoto==true){	          
	           // if (convertView == null) {  
	                convertView = LayoutInflater.from(MenuPhotoListActivity.this).inflate(  
	                        R.layout.menu_photo_item_row, null);  

	                listItemView = new ListItemView();  
	                listItemView.imageView = (ImageView) convertView  
	                        .findViewById(R.id.menuitem);  
	                listItemView.textitemdesc = (TextView) convertView  
	                        .findViewById(R.id.itemdesc);  
	                listItemView.textitemprice = (TextView) convertView  
	                        .findViewById(R.id.textitemprice);  
	                listItemView.textitemname = (TextView) convertView  
	                        .findViewById(R.id.textitemname);  
	                listItemView.imageButton=(ImageButton)convertView  
	                        .findViewById(R.id.addbutton);    

	                listItemView.imageButton.setOnClickListener(new View.OnClickListener() {
	        			@Override
	        			public void onClick(View view) {
	        				if(position==0){
	        	                Intent intent = new Intent
	        	                        (getApplicationContext(), ModifierActivity.class);
	        	                intent.putExtra("Item Name",totalTextList[m].get(position).getItemName());
	        	                intent.putExtra("Position",position);
	        	                startActivity(intent); 
	        				}
	        				else{
	        					ThreadSafeSingleton.getInstance().totalOrderNumber++;
	        				if(!ThreadSafeSingleton.getInstance().itemname.contains(totalTextList[m].get(position).getItemName()))        					
	        				{//if the instance has not been created before
	        					
	        					ThreadSafeSingleton.getInstance().itemname.add(totalTextList[m].get(position).getItemName());
	        					ThreadSafeSingleton.getInstance().price.add(totalTextList[m].get(position).getItemPrice());
	        				    totalTextList[m].get(position).setNumber(totalTextList[m].get(position).getNumber()+1);
	        				    ThreadSafeSingleton.getInstance().number.add(Integer.toString(totalTextList[m].get(position).getNumber()));
	        				    
	        				}else{//if the instance has already been created before
	        					totalTextList[m].get(position).setNumber(totalTextList[m].get(position).getNumber()+1);	
	        					ThreadSafeSingleton.getInstance().number.set(ThreadSafeSingleton.getInstance().itemname.indexOf(totalTextList[m].get(position).getItemName()),Integer.toString(totalTextList[m].get(position).getNumber()));
	        				}
	        				View parent = (View)view.getParent().getParent().getParent();
	        				TextView cornertext;
	        				cornertext=(TextView)parent.findViewById(R.id.corner);
	        				cornertext.setVisibility(View.VISIBLE);
	        				cornertext.setText(String.valueOf(ThreadSafeSingleton.getInstance().totalOrderNumber));	
	        				Animation a = AnimationUtils.loadAnimation(getBaseContext(), R.anim.scale_up);
	        				cornertext.startAnimation(a);
	        				}
	        		}});

	            //   convertView.setTag(listItemView);  
	           // } else {  
	            //   listItemView = (ListItemView) convertView.getTag();  
	           // }
	            
	            Drawable img = totalList[m].get(position).getImage();  
	            String name = totalTextList[m].get(position).getItemName();  
	            String desc = totalTextList[m].get(position).getItemDesc();  
	            String price = totalTextList[m].get(position).getItemPrice();  

	  
	            listItemView.imageView.setImageDrawable(img);  
	            listItemView.textitemname.setText(name);  
	            listItemView.textitemdesc.setText(desc);  
	            listItemView.textitemprice.setText(price);     

	            return convertView;  
	            }
	         else
	         {
		                convertView = LayoutInflater.from(MenuPhotoListActivity.this).inflate(  
		                        R.layout.menu_text_item_row, null);  

		                listTextItemView = new ListTextItemView();  
		                listTextItemView.textitemprice = (TextView) convertView  
		                        .findViewById(R.id.textitemprice);  
		                listTextItemView.textitemname = (TextView) convertView  
		                        .findViewById(R.id.textitemname);  
		                listTextItemView.textitemdesc = (TextView) convertView  
		                        .findViewById(R.id.textitemdesc);  
		                listTextItemView.imageButton=(ImageButton)convertView  
		                        .findViewById(R.id.addbutton); 
		                
		                listTextItemView.imageButton.setOnClickListener(new View.OnClickListener() {
		        			@Override
		        			public void onClick(View view) {
		        				if(position==0){
		        	                Intent intent = new Intent
		        	                        (getApplicationContext(), ModifierActivity.class);
		        	                intent.putExtra("Item Name",totalTextList[m].get(position).getItemName());
		        	                startActivity(intent); 
		        				}
		        				else{
		        					ThreadSafeSingleton.getInstance().totalOrderNumber++;
		        			    if(!ThreadSafeSingleton.getInstance().itemname.contains(totalTextList[m].get(position).getItemName()))        					
		        				{//if the instance has not been created before
		        					
		        			    	ThreadSafeSingleton.getInstance().itemname.add(totalTextList[m].get(position).getItemName());
		        			    	ThreadSafeSingleton.getInstance().price.add(totalTextList[m].get(position).getItemPrice());
		        				    totalTextList[m].get(position).setNumber(totalTextList[m].get(position).getNumber()+1);
		        				    ThreadSafeSingleton.getInstance().number.add(Integer.toString(totalTextList[m].get(position).getNumber()));
		        				    
		        				}else{//if the instance has already been created before
		        					totalTextList[m].get(position).setNumber(totalTextList[m].get(position).getNumber()+1);	
		        					ThreadSafeSingleton.getInstance().number.set(ThreadSafeSingleton.getInstance().itemname.indexOf(totalTextList[m].get(position).getItemName()),Integer.toString(totalTextList[m].get(position).getNumber()));
		        				}
		        				View parent = (View)view.getParent().getParent().getParent();
		        				TextView cornertext;
		        				cornertext=(TextView)parent.findViewById(R.id.corner);
		        				cornertext.setVisibility(View.VISIBLE);
		        				cornertext.setText(String.valueOf(ThreadSafeSingleton.getInstance().totalOrderNumber));	
		        				Animation a = AnimationUtils.loadAnimation(getBaseContext(), R.anim.scale_up);
		        				cornertext.startAnimation(a);
		        				}
		        		}});
		                
		            String name = totalTextList[m].get(position).getItemName();  
		            String desc = totalTextList[m].get(position).getItemDesc();  
		            String price = totalTextList[m].get(position).getItemPrice();  
		            

		            listTextItemView.textitemname.setText(name);  
		            listTextItemView.textitemdesc.setText(desc);  
		            listTextItemView.textitemprice.setText(price);    
		            
		            return convertView;  
	         }
	    }
	}


	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		//viewpager.setCurrentItem(tab.getPosition());
		m=tab.getPosition();
		adapter.notifyDataSetChanged();

	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
    class ListItemView {  
        ImageView imageView;  
        TextView textitemprice,textitemname,textitemdesc;    
        ImageButton imageButton;
    }  
    
    class ListTextItemView {  
        TextView textitemprice,textitemname,textitemdesc;  
        ImageButton imageButton;
    }     
    
    public View getActionBarView() {
        Window window = getWindow();
        View v = window.getDecorView();
        int resId = getResources().getIdentifier("action_bar_container", "id", "android");
        return v.findViewById(resId);
    }

}
