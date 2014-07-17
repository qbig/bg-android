package sg.com.bigspoon.www.activities;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.adapters.ActionBarMenuAdapter;
import sg.com.bigspoon.www.adapters.CustomItemsList;
import sg.com.bigspoon.www.adapters.ExpandableAdapter;
import sg.com.bigspoon.www.data.ThreadSafeSingleton;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.StateListDrawable;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ItemsActivity extends ExpandableListActivity{

		ListView list;
        ImageButton imageplus,imageminus;	
        Boolean isExpanded = false;
        private GridView gridView;
        
	/*Integer[] minusImage = { R.drawable.button_minus_no_circle, R.drawable.button_minus_no_circle,
			R.drawable.button_minus_no_circle ,R.drawable.button_minus_no_circle,R.drawable.button_minus_no_circle, R.drawable.button_minus_no_circle,
			R.drawable.button_minus_no_circle ,R.drawable.button_minus_no_circle};
	String[] number= {"1","1","1","1","1","1","1","1"};
	Integer[] addImage = { R.drawable.button_add_no_circle, R.drawable.button_add_no_circle,
			R.drawable.button_add_no_circle ,R.drawable.button_add_no_circle,R.drawable.button_add_no_circle, R.drawable.button_add_no_circle,
			R.drawable.button_add_no_circle ,R.drawable.button_add_no_circle };
	String[] itemdesc = { "Welcome to Kith ", "Testing !! The Groc.....",
			"Testing !! Strictly Pancakes" ,"Testing !! Strictly Pancakes","Welcome to Kith ", "Testing !! The Groc.....",
			"Testing !! Strictly Pancakes" ,"Testing !! Strictly Pancakes"};
  String[] price= {"$5","$25","$15","$12","$5","$25","$15","$12"};*/
	
		
  
  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);      
		setContentView(R.layout.activity_items);
		
		TextView cornertext1;
		cornertext1=(TextView)findViewById(R.id.corner);
		ThreadSafeSingleton.getInstance().corner=cornertext1;
		if(ThreadSafeSingleton.getInstance().totalOrderNumber!=0){
		cornertext1.setVisibility(View.VISIBLE);
		cornertext1.setText(String.valueOf(ThreadSafeSingleton.getInstance().totalOrderNumber));	
		}
		
			
        final ExpandableListView expandableList = getExpandableListView(); 
        expandableList.setDividerHeight(2);
        expandableList.setGroupIndicator(null);
        expandableList.setClickable(true);
        
        //This is to unable the function of expanding when clicking the gourp item
        expandableList.setOnGroupClickListener(new OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {           	
                return true;
            }
        });
        
        final ExpandableAdapter adapter = new ExpandableAdapter(ThreadSafeSingleton.getInstance().number,ThreadSafeSingleton.getInstance().itemname,ThreadSafeSingleton.getInstance().price);

        adapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);
        
        expandableList.setAdapter(adapter);
        expandableList.setOnChildClickListener(this);
        expandableList.setChildDivider(getResources().getDrawable(R.color.white));        
        expandableList.setDivider(getResources().getDrawable(R.color.white));
        expandableList.setDividerHeight(2);
        
        Button addNote = (Button)findViewById(R.id.button1);
        addNote.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(!isExpanded){
                    for (int i = 0; i<ThreadSafeSingleton.getInstance().itemname.size(); i++) {
                	expandableList.expandGroup(i,true);
                	isExpanded=true;
                	}
				}else{
                	for (int i = 0; i<ThreadSafeSingleton.getInstance().itemname.size(); i++) {
                    	expandableList.collapseGroup(i);
                    	isExpanded=false;          	
                	}
             }
		}});
        

		new ListViewUtil().setListViewHeightBasedOnChildren(expandableList,0);  
		
		expandableList.setOnGroupExpandListener(new OnGroupExpandListener() {
			@Override
			public void onGroupExpand(int groupPosition) {
			// TODO Auto-generated method stub
				ExpandableViewUtil.setExpandedListViewHeightBasedOnChildren(
						expandableList, groupPosition);
			}
		});
		expandableList.setOnGroupCollapseListener(new OnGroupCollapseListener() {
			@Override
			public void onGroupCollapse(int groupPosition) {
				// TODO Auto-generated method stub
					ExpandableViewUtil.setCollapseListViewHeightBasedOnChildren(
							expandableList, groupPosition);
			}
		});
		      
		loadMenu();    
		      
	    Button placeOrder = (Button)findViewById(R.id.button2);
		        placeOrder.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {

						placeOrderAlertDialog();

				}
					@SuppressWarnings("deprecation")
					private void placeOrderAlertDialog() {
						
						if(ThreadSafeSingleton.getInstance().itemname.isEmpty()){
							AlertDialog alertNoOrder = new AlertDialog.Builder(ItemsActivity.this).create();	
							alertNoOrder.setTitle("Place Order");
							alertNoOrder.setMessage("You haven't selected anything.");
							alertNoOrder.setView(null);
						 	alertNoOrder.setButton("Okay",new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,int whichButton) {
		                           //
								}
							});
							alertNoOrder.show(); 
							int divierId = alertNoOrder.getContext().getResources()
					                .getIdentifier("android:id/titleDivider", null, null);
					        View divider = alertNoOrder.findViewById(divierId);
					        divider.setBackgroundColor(getResources().getColor(android.R.color.transparent));
					        final int alertTitle =alertNoOrder.getContext().getResources().getIdentifier( "alertTitle", "id", "android" );
					        TextView titleView = (TextView) alertNoOrder.findViewById(alertTitle);
					        titleView.setGravity(Gravity.CENTER);
					        titleView.setTypeface(null,Typeface.BOLD);
					        titleView.setTextSize(19);
					        titleView.setTextColor( getResources().getColor(android.R.color.black));
					        
							TextView messageView = (TextView)alertNoOrder.findViewById(android.R.id.message);
			                messageView.setGravity(Gravity.CENTER);
			                //messageView.setHeight(140);
			                messageView.setTextSize(17);
						    
			                Button okButton = alertNoOrder.getButton(DialogInterface.BUTTON1);
			                okButton.setTextColor(Color.parseColor("#117AFE"));
			                okButton.setTypeface(null,Typeface.BOLD);
			                okButton.setTextSize(19);
							
						}else{
							
					    LayoutInflater inflater = getLayoutInflater();
					    
					    AlertDialog.Builder alertbuilder = new AlertDialog.Builder(ItemsActivity.this);
					    
						View dialoglayout = inflater.inflate(R.layout.dialog_layout, null);
						
						LinearLayout layoutholder = (LinearLayout)dialoglayout.findViewById(R.id.dialog_layout_root);
						
						TextView textTitle = new TextView(getBaseContext());
			        	LinearLayout.LayoutParams params =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			        	params.gravity=Gravity.CENTER;
			        	final float scale = getBaseContext().getResources().getDisplayMetrics().density;
			        	int padding_10dp = (int) (10 * scale + 0.5f);
			        	int padding_25dp = (int) (25 * scale + 0.5f);
			        	
			        	params.setMargins(0,0,0,padding_10dp);
			        	textTitle.setLayoutParams(params);
			        	textTitle.setText("New Order");
			        	textTitle.setTextSize(19);
			        	textTitle.setTextColor(getResources().getColor(android.R.color.black));
			        	textTitle.setTypeface(null, Typeface.BOLD);
			        	layoutholder.addView(textTitle);
			        	
			        	
			        	for(int i = 0;i<ThreadSafeSingleton.getInstance().number.size();i++)
						{
			        		FrameLayout childLayout = new FrameLayout(getBaseContext());
			        	    TextView textNumber = new TextView(getBaseContext());
			        	    //textNumber.setId(R.id.textNumber);
			        	    FrameLayout.LayoutParams params2 =new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			        	    params2.setMargins(padding_10dp,0,0,0);
			        	    textNumber.setLayoutParams(params2);
			        	    textNumber.setText(ThreadSafeSingleton.getInstance().number.get(i));
			        	    textNumber.setTextColor(getResources().getColor(android.R.color.black));
			        	    childLayout.addView(textNumber);
			        	    
			        	    
			        	    TextView xMark = new TextView(getBaseContext());
			        	    //textNumber.setId(R.id.textNumber);
			        	    FrameLayout.LayoutParams params3 =new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			        	    params3.setMargins(padding_25dp,0,0,0);
			        	    xMark.setLayoutParams(params3);
			        	    xMark.setText("x");
			        	    xMark.setTextColor(getResources().getColor(android.R.color.black));
			        	    childLayout.addView(xMark);
			        	    
			        	    
			        	    TextView itemName = new TextView(getBaseContext());
			        	    //textNumber.setId(R.id.textNumber);
			        	    FrameLayout.LayoutParams params4 =new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			        	    params4.setMargins(0,0,padding_25dp,0);
			        	    params4.gravity=Gravity.RIGHT;
			        	    itemName.setLayoutParams(params4);
			        	    itemName.setText(ThreadSafeSingleton.getInstance().itemname.get(i));
			        	    itemName.setTextColor(getResources().getColor(android.R.color.black));
			        	    childLayout.addView(itemName);
			        	    
			        	    LinearLayout.LayoutParams parentParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT); 
			        	    layoutholder.addView(childLayout,parentParams);
						}
			        	
			        	alertbuilder.setView(layoutholder);

			        	alertbuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int whichButton) {
		                      //
							}
						});
			        	alertbuilder.setPositiveButton("Okay",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int whichButton) {
								ThreadSafeSingleton.getInstance().itemnameOld.addAll(ThreadSafeSingleton.getInstance().itemname);
								ThreadSafeSingleton.getInstance().numberOld.addAll(ThreadSafeSingleton.getInstance().number);
								ThreadSafeSingleton.getInstance().priceOld.addAll(ThreadSafeSingleton.getInstance().price);
								ThreadSafeSingleton.getInstance().initialize();
								Toast.makeText(getApplicationContext(), "Your order has been sent. Our food is prepared with love, thank you for being patient.",
										   Toast.LENGTH_LONG).show();
								Intent i = new Intent(getBaseContext(), ItemsActivity.class);
								startActivity(i);
								finish();								
								}
						});
						
			        	 AlertDialog alertDialog = alertbuilder.create();
			        	 alertDialog.show();
					    
		                Button okButtom = alertDialog.getButton(DialogInterface.BUTTON1);
		                okButtom.setTextColor(Color.parseColor("#117AFE"));
		                okButtom.setTextSize(16);
		                okButtom.setTypeface(null, Typeface.BOLD);
		                Button cancelButton = alertDialog.getButton(DialogInterface.BUTTON2);
		                cancelButton.setTextColor(Color.parseColor("#117AFE"));
		                cancelButton.setTextSize(16);
		                cancelButton.setTypeface(null, Typeface.BOLD);
						}
					}
					
		        });     
 
		        
		ListView listOfOrderPlaced = (ListView) findViewById(R.id.listOfOrderPlaced);
		CustomItemsList adapterForPlaced = new CustomItemsList(this);
		listOfOrderPlaced.setAdapter(adapterForPlaced);   
		new ListViewUtil().setListViewHeightBasedOnChildren(listOfOrderPlaced,0);
		      
 }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.items, menu);
		//  MenuInflater inflater = getMenuInflater();
		 //   inflater.inflate(R.menu.items, menu);
		   
		    ActionBar actionBar = getActionBar();
		    actionBar.setDisplayShowHomeEnabled(false);
		    //addListenerOnButtonLogout();	    
		    //displaying custom ActionBar
		    View mActionBarView = getLayoutInflater().inflate(R.layout.action_bar_items_activity, null);
		    actionBar.setCustomView(mActionBarView);
		    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);    
		    ImageButton ibItem1 = (ImageButton) mActionBarView.findViewById(R.id.btn_menu);
		    ibItem1.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
			ibItem1.setPadding(22, 0, 0, 0);

			StateListDrawable states = new StateListDrawable();
			states.addState(new int[] {android.R.attr.state_pressed},
			    getResources().getDrawable(R.drawable.menu_pressed));
			states.addState(new int[] { },
			    getResources().getDrawable(R.drawable.menu));
			ibItem1.setImageDrawable(states);
			
		    ibItem1.setOnClickListener(new View.OnClickListener() {
		        @Override
		        public void onClick(View view) {
		                	//Intent intent = new Intent(getApplicationContext(),
							//MenuPhotoListActivity.class);
					//startActivity(intent);
		        	finish();
		        }
		    });
		      
		    
		    ImageButton ibItem2 = (ImageButton) mActionBarView.findViewById(R.id.order_history);
		    ibItem2.setOnClickListener(new View.OnClickListener() {
		        @Override
		        public void onClick(View view) {
		            // ...
		        	
		        	Intent intent = new Intent(getApplicationContext(),
							OrderHistoryListActivity.class);
					startActivity(intent);
		        }
		    });
		    
		    return super.onCreateOptionsMenu(menu);
	}

	public class ListViewUtil {

		public void setListViewHeightBasedOnChildren(ListView listView, int attHeight) {  
		        ListAdapter listAdapter = listView.getAdapter();   
		        if (listAdapter == null) {  
		            // pre-condition  
		            return;  
		        }  
		  
		        int totalHeight = 0;  
		        for (int i = 0; i < listAdapter.getCount(); i++) {  
		            View listItem = listAdapter.getView(i, null, listView);  
		            listItem.measure(0, 0);  
		            totalHeight += listItem.getMeasuredHeight();  
		        }  
		  
		        ViewGroup.LayoutParams params = listView.getLayoutParams();  
		        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + attHeight;  
		        listView.setLayoutParams(params);  
		    }  
		}

	
	protected void loadMenu() {

		final Context ctx = getApplicationContext();
		Resources res = ctx.getResources();
		final EditText input = new EditText(this);
		// String[] gv_options = {"Water","Waiter","Bill","Orders"};
		// int[] gv_icons =
		// {R.drawable.fb_1,R.drawable.fb_2,R.drawable.fb_3,R.drawable.fb_4};
		final AlertDialog alert = new AlertDialog.Builder(this).create();
		final AlertDialog alert2 = new AlertDialog.Builder(this).create();
		gridView = (GridView) findViewById(R.id.gv_action_menu);
		// Create the Custom Adapter Object
		ActionBarMenuAdapter actionBarMenuAdapter = new ActionBarMenuAdapter(
				this, 4);
		// Set the Adapter to GridView
		gridView.setAdapter(actionBarMenuAdapter);

		// Handling touch/click Event on GridView Item
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long arg3) {
				Intent i = null;
				switch (position) {
				case 0:
					i = new Intent(ctx, WaterServiceActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
					break;
				case 1:
					alert.setMessage("Please enter your table ID located on the BigSpoon table stand");
					// i = new Intent(ctx, StoreListActivity.class);
					// Set an EditText view to get user input

					alert.setView(input,10,0,10,0);

				    alert.setButton2("Cancel",new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,int whichButton) {
                                  //
								}
							});
					alert.setButton("Okay",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int whichButton) {
                           //
						}
					});
				    alert.show(); 
				    TextView messageView = (TextView)alert.findViewById(android.R.id.message);
	                messageView.setGravity(Gravity.CENTER);
	                //messageView.setHeight(140);
	                messageView.setTextSize(17);
				    
	                Button bq1 = alert.getButton(DialogInterface.BUTTON1);
	                bq1.setTextColor(Color.parseColor("#117AFE"));
	                bq1.setTypeface(null,Typeface.BOLD);
	                bq1.setTextSize(19);
	                Button bq2 = alert.getButton(DialogInterface.BUTTON2);
	                bq2.setTextColor(Color.parseColor("#117AFE"));
	                //bq2.setTypeface(null,Typeface.BOLD);
	                bq2.setTextSize(19);
					break;
				case 2:
					// i = new Intent(ctx, CartActivity.class);
					alert2.setMessage("Would you like your bill?");
					alert2.setView(null);
					

		            alert2.setButton2("Cancel",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int whichButton) {
                          //
						}
					});
					alert2.setButton("Yes",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int whichButton) {
                           //
						}
					});
		            alert2.show(); 
				    TextView messageView2 = (TextView)alert2.findViewById(android.R.id.message);
	                messageView2.setGravity(Gravity.CENTER);
	                //messageView.setHeight(140);
	                messageView2.setTextSize(17);
				    
	                Button bq3 = alert2.getButton(DialogInterface.BUTTON1);
	                bq3.setTextColor(Color.parseColor("#117AFE"));
	                bq3.setTypeface(null,Typeface.BOLD);
	                bq3.setTextSize(19);
	                Button bq4 = alert2.getButton(DialogInterface.BUTTON2);
	                bq4.setTextColor(Color.parseColor("#117AFE"));
	                //bq4.setTypeface(null,Typeface.BOLD);
	                bq4.setTextSize(19);
					break;
				case 3:
					//i = new Intent(ctx, ItemsActivity.class);
					//i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					//startActivity(i);					
					break;
				}
				
			}
		});

	}
}
