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

import org.apache.commons.lang3.StringUtils;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.activities.ItemsActivity;
import sg.com.bigspoon.www.data.DishModel;
import sg.com.bigspoon.www.data.Order;
import sg.com.bigspoon.www.data.OrderItem;
import sg.com.bigspoon.www.data.User;

import static sg.com.bigspoon.www.data.Constants.NOTIF_ITEM_REMOVE_CLICK;
import static sg.com.bigspoon.www.data.Constants.NOTIF_ORDER_UPDATE;

public class CurrentOrderExpandableAdapter extends BaseExpandableListAdapter {

	private LayoutInflater inflater;
	private Context mContext;

	public CurrentOrderExpandableAdapter(Context context) {
		this.mContext = context;
	}

	public void setInflater(LayoutInflater inflater, Activity activity) {
		this.inflater = inflater;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_items_child, null);
		}
		convertView.setVisibility(View.VISIBLE);
		final EditText addNoteField = (EditText) convertView.findViewById(R.id.addNoteTextField);
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
		//modifierSubTitle.setVisibility(View.GONE);
		final OrderItem currentItem = User.getInstance(mContext).currentSession.getCurrentOrder().mItems.get(groupPosition);
		if (currentItem.dish.customizable || (currentItem.note != null && !currentItem.note.isEmpty())) {
			modifierSubTitle.setVisibility(View.VISIBLE);
			String text = "";

			if (currentItem.dish.customizable){
				text += User.getInstance(mContext).currentSession.getCurrentOrder()
						.getModifierDetailsTextAtIndex(groupPosition);
			}
			if (StringUtils.isNotEmpty(currentItem.note)){
				if (StringUtils.isEmpty(text)) {
					text = currentItem.note;
				} else {
					text += ("\n" + currentItem.note);
				}
			}
			modifierSubTitle.setText(text);
		} else {
			modifierSubTitle.setText("");
		}


		final OnClickListener removeDishListener = new OnClickListener() {
			@Override
			public void onClick(View view) {
				LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(NOTIF_ITEM_REMOVE_CLICK));
				((ItemsActivity)mContext).handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						final Order currentOrder = User.getInstance(mContext).currentSession.getCurrentOrder();
						if (currentOrder.getQuantityOfDishByIndex(groupPosition) > 0) {
							currentOrder.decrementDishAtIndex(groupPosition);
						}
						if (currentOrder.getQuantityOfDishByIndex(groupPosition) == 0) {
							currentOrder.removeDishAtIndex(groupPosition);
						}

						LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(NOTIF_ORDER_UPDATE));
						notifyDataSetChanged();
					}
				}, 100);
			}
		};

		TextView minusButton = (TextView) convertView.findViewById(R.id.imgMinus);
		minusButton.setOnClickListener(removeDishListener);
		numberView.setOnClickListener(removeDishListener);

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
		return User.getInstance(mContext).currentSession.getCurrentOrder().mItems.get(groupPosition);
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