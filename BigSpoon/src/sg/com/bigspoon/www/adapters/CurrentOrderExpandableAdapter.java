package sg.com.bigspoon.www.adapters;

import static sg.com.bigspoon.www.data.Constants.MODIFIER_POPUP_DISH_ID;
import static sg.com.bigspoon.www.data.Constants.MODIFIER_POPUP_REQUEST;
import static sg.com.bigspoon.www.data.Constants.NOTIF_ORDER_UPDATE;
import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.activities.ItemsActivity;
import sg.com.bigspoon.www.activities.MenuPhotoListActivity;
import sg.com.bigspoon.www.activities.ModifierActivity;
import sg.com.bigspoon.www.data.DishModel;
import sg.com.bigspoon.www.data.User;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

		addNoteField.setText(User.getInstance(mContext).currentSession.currentOrder.mItems.get(groupPosition).note);
		addNoteField.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					final int itemIndex = ((Integer) v.getTag()).intValue();
					if (User.getInstance(mContext).currentSession.currentOrder.mItems.size() - 1 >= itemIndex) {
						User.getInstance(mContext).currentSession.currentOrder.mItems.get(itemIndex).note = ((EditText) v)
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
		final DishModel dish = User.getInstance(mContext).currentSession.currentOrder.mItems.get(groupPosition).dish;
		numberView.setText(Integer.toString(User.getInstance(mContext).currentSession.currentOrder
				.getQuantityOfDishByIndex(groupPosition)));
		itemdescView
				.setText(User.getInstance(mContext).currentSession.currentOrder.mItems.get(groupPosition).dish.name);
		if (dish.customizable) {
			priceView.setText(Double.toString(User.getInstance(mContext).currentSession.currentOrder
					.getModifierPriceChangeAtIndex(groupPosition) + dish.price));
		} else {
			priceView.setText(Double.toString(dish.price));
		}

		TextView modifierSubTitle = (TextView) convertView.findViewById(R.id.subTitle);
		modifierSubTitle.setVisibility(View.GONE);

		if (User.getInstance(mContext).currentSession.currentOrder.mItems.get(groupPosition).dish.customizable) {
			modifierSubTitle.setVisibility(View.VISIBLE);
			modifierSubTitle.setText(User.getInstance(mContext).currentSession.currentOrder
					.getModifierDetailsTextAtIndex(groupPosition));
		}

		ImageButton minusButton = (ImageButton) convertView.findViewById(R.id.imgMinus);
		ImageButton plusButton = (ImageButton) convertView.findViewById(R.id.imgPlus);
		minusButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (User.getInstance(mContext).currentSession.currentOrder.getQuantityOfDishByIndex(groupPosition) > 0) {
					User.getInstance(mContext).currentSession.currentOrder.decrementDishAtIndex(groupPosition);
				}
				if (User.getInstance(mContext).currentSession.currentOrder.getQuantityOfDishByIndex(groupPosition) == 0) {
					User.getInstance(mContext).currentSession.currentOrder.removeDishAtIndex(groupPosition);
				}
				notifyDataSetChanged();
				Intent intent = new Intent(NOTIF_ORDER_UPDATE);
				LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
			}
		});

		plusButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (dish.quantity <= 0) {
					Toast.makeText(mContext, "This is dish is out of stock", Toast.LENGTH_LONG).show();
					return;
				}
				
				if (dish.customizable) {
					final Intent intentForModifier = new Intent(mContext, ModifierActivity.class);
					intentForModifier.putExtra(MODIFIER_POPUP_DISH_ID, dish.id);
					((ItemsActivity) mContext).startActivity(intentForModifier);
				} else {
					User.getInstance(mContext).currentSession.currentOrder.incrementDishAtIndex(groupPosition);

					notifyDataSetChanged();
					Intent intent = new Intent(NOTIF_ORDER_UPDATE);
					LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
				}
			}
		});

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
		return User.getInstance(mContext).currentSession.currentOrder.mItems.size();
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