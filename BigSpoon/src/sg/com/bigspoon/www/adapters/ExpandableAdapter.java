package sg.com.bigspoon.www.adapters;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.activities.MenuPhotoListActivity;
import sg.com.bigspoon.www.data.User;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class ExpandableAdapter extends BaseExpandableListAdapter 
{

    private Activity activity;
    private LayoutInflater inflater;
    private Context mContext;
	

    // constructor
    public ExpandableAdapter(Context context)
    {
    	this.mContext = context;
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
    	
    	numberView.setText(Integer.toString(User.getInstance(mContext).currentSession.currentOrder.getQuantityOfDishByIndex(groupPosition)));   	
    	itemdescView.setText(User.getInstance(mContext).currentSession.currentOrder.mItems.get(groupPosition).dish.name);
    	priceView.setText(Double.toString(User.getInstance(mContext).currentSession.currentOrder.mItems.get(groupPosition).dish.price));
    	TextView modifierSubTitle = (TextView) convertView.findViewById(R.id.subTitle);
    	modifierSubTitle.setVisibility(View.GONE);
    	
    	if(groupPosition==1)
    	{
    		modifierSubTitle.setVisibility(View.VISIBLE);
    		modifierSubTitle.setText("Garlic + Herbs x 1"+"\n"+"Salted x 1"+"\n"+"Maple syrup x 1"+"\n"+"Chocolate Pancakes: Yes (+$1)"+"\n"+"Corn Beef Hash (+$2.5) x 5");
    	}	
    	
    	ImageButton minusButton = (ImageButton) convertView.findViewById(R.id.imgMinus);
    	ImageButton plusButton = (ImageButton) convertView.findViewById(R.id.imgPlus);
    	minusButton.setOnClickListener(new OnClickListener(){
    		@Override
            public void onClick(View view) {
    			if(User.getInstance(mContext).currentSession.currentOrder.getQuantityOfDishByIndex(groupPosition)>0){
    			final View parent = (View) view.getParent().getParent().getParent().getParent().getParent();
				TextView cornertext;
				cornertext = (TextView) parent.findViewById(R.id.corner);
    			if(MenuPhotoListActivity.totalOrderNumber-1==0){
    				MenuPhotoListActivity.totalOrderNumber--;			
				    cornertext.setVisibility(View.VISIBLE);
				    cornertext.setText(String.valueOf(MenuPhotoListActivity.totalOrderNumber));
			        Animation a = AnimationUtils.loadAnimation(mContext, R.anim.scale_up);
				    cornertext.startAnimation(a);
    				cornertext.setVisibility(View.GONE);
    			}
    			if(MenuPhotoListActivity.totalOrderNumber-1<0)  MenuPhotoListActivity.totalOrderNumber=0;
    			else{MenuPhotoListActivity.totalOrderNumber--;			
				     cornertext.setVisibility(View.VISIBLE);
				     cornertext.setText(String.valueOf(MenuPhotoListActivity.totalOrderNumber));
			         Animation a = AnimationUtils.loadAnimation(mContext, R.anim.scale_up);
				     cornertext.startAnimation(a);
				}
    			}
    			User.getInstance(mContext).currentSession.currentOrder.decrementDishAtIndex(groupPosition);
    			notifyDataSetChanged();   		
    			if(User.getInstance(mContext).currentSession.currentOrder.getQuantityOfDishByIndex(groupPosition)==0){
    				User.getInstance(mContext).currentSession.currentOrder.removeDishAtIndex(groupPosition);
    				notifyDataSetChanged();  
    			}
    			}
    		 });
    	
    	plusButton.setOnClickListener(new OnClickListener(){
    		@Override
            public void onClick(View view) {
    			User.getInstance(mContext).currentSession.currentOrder.incrementDishAtIndex(groupPosition);
    			notifyDataSetChanged();
    			MenuPhotoListActivity.totalOrderNumber++;
				final View parent = (View) view.getParent().getParent().getParent().getParent().getParent();
				TextView cornertext;
				cornertext = (TextView) parent.findViewById(R.id.corner);
				cornertext.setVisibility(View.VISIBLE);
				cornertext.setText(String.valueOf(MenuPhotoListActivity.totalOrderNumber));
				Animation a = AnimationUtils.loadAnimation(mContext, R.anim.scale_up);
				cornertext.startAnimation(a);
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
        return User.getInstance(mContext).currentSession.currentOrder.mItems.size();
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