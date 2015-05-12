package sg.com.bigspoon.www.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.data.DishModel;
import sg.com.bigspoon.www.data.User;

import static sg.com.bigspoon.www.data.Constants.NOTIF_ORDER_UPDATE;

public class CurrentOrderExpandableAdapter extends BaseExpandableListAdapter {

	private LayoutInflater inflater;
	private Context mContext;

	// constructor
	public CurrentOrderExpandableAdapter(Context context) {
		this.mContext = context;
	}

	public void setInflater(LayoutInflater inflater, Activity activity) {
		this.inflater = inflater;
	}

	// method getChildView is called automatically for each child view.
	// Implement this method as per your requirement
	@Override
	public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_items_child, null);
		}
		final EditText addNoteField = (EditText) convertView.findViewById(R.id.editText1);
		addNoteField.setTag(groupPosition);

		addNoteField.setText(User.getInstance(mContext).currentSession.getCurrentOrder().mItems.get(groupPosition).note);
		addNoteField.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					final int itemIndex = ((Integer) v.getTag()).intValue();
					if (User.getInstance(mContext).currentSession.getCurrentOrder().mItems.size() - 1 >= itemIndex) {
						User.getInstance(mContext).currentSession.getCurrentOrder().mItems.get(itemIndex).note = ((EditText) v)
								.getText().toString();
					}
				}
			}
		});

		return convertView;
	}

	// method getGroupView is called automatically for each parent item
	// Implement this method as per your requirement
	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_items_parent, null);
		}

		final TextView numberView = (TextView) convertView.findViewById(R.id.quantitytxt);
		final TextView itemdescView = (TextView) convertView.findViewById(R.id.descriptiontxt);
		final TextView priceView = (TextView) convertView.findViewById(R.id.descriptionitemPrice);
		final DishModel dish = User.getInstance(mContext).currentSession.getCurrentOrder().mItems.get(groupPosition).dish;
		final int dishQuantity = User.getInstance(mContext).currentSession.getCurrentOrder()
				.getQuantityOfDishByIndex(groupPosition); 
				
		numberView.setText(Integer.toString(dishQuantity));
		itemdescView
				.setText(User.getInstance(mContext).currentSession.getCurrentOrder().mItems.get(groupPosition).dish.name);
		if (dish.customizable) {
			priceView.setText("$" + String.format("%.1f", User.getInstance(mContext).currentSession.getCurrentOrder()
					.getModifierPriceChangeAtIndex(groupPosition) + dish.price));
		} else {
			priceView.setText("$" + String.format("%.1f",dish.price * dishQuantity));
		}

		TextView modifierSubTitle = (TextView) convertView.findViewById(R.id.subTitle);
		modifierSubTitle.setVisibility(View.GONE);

		if (User.getInstance(mContext).currentSession.getCurrentOrder().mItems.get(groupPosition).dish.customizable) {
			modifierSubTitle.setVisibility(View.VISIBLE);
			modifierSubTitle.setText(User.getInstance(mContext).currentSession.getCurrentOrder()
					.getModifierDetailsTextAtIndex(groupPosition));
		}

		final OnClickListener removeDishListener = new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (User.getInstance(mContext).currentSession.getCurrentOrder().getQuantityOfDishByIndex(groupPosition) > 0) {
					User.getInstance(mContext).currentSession.getCurrentOrder().decrementDishAtIndex(groupPosition);
				}
				if (User.getInstance(mContext).currentSession.getCurrentOrder().getQuantityOfDishByIndex(groupPosition) == 0) {
					User.getInstance(mContext).currentSession.getCurrentOrder().removeDishAtIndex(groupPosition);
				}
				notifyDataSetChanged();
				Intent intent = new Intent(NOTIF_ORDER_UPDATE);
				LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
			}
		};

		TextView minusButton = (TextView) convertView.findViewById(R.id.imgMinus);
		minusButton.setOnClickListener(removeDishListener);
		numberView.setOnClickListener(removeDishListener);
		convertView.setOnClickListener(removeDishListener);
		return convertView;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return null;
	}

	@Override
	public int getGroupCount() {
        try {
            return User.getInstance(mContext).currentSession.getCurrentOrder().mItems.size();
        } catch (NullPointerException npe) {
            Crashlytics.log(npe.getMessage());
            return 0;
        }
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		super.onGroupExpanded(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

}