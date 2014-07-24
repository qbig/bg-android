package sg.com.bigspoon.www.adapters;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.data.User;
import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

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

		TextView numberView = (TextView) convertView.findViewById(R.id.quantitytxt);
		TextView itemdescView = (TextView) convertView.findViewById(R.id.descriptiontxt);
		TextView priceView = (TextView) convertView.findViewById(R.id.descriptionitemPrice);

		numberView.setText(Integer.toString(User.getInstance(mContext).currentSession.currentOrder
				.getQuantityOfDishByIndex(groupPosition)));
		itemdescView
				.setText(User.getInstance(mContext).currentSession.currentOrder.mItems.get(groupPosition).dish.name);
		priceView.setText(Double.toString(User.getInstance(mContext).currentSession.currentOrder.mItems
				.get(groupPosition).dish.price));
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
					notifyDataSetChanged();
					final View parent = (View) view.getParent().getParent().getParent().getParent().getParent();
					TextView cornertext;
					cornertext = (TextView) parent.findViewById(R.id.corner);
					if (User.getInstance(mContext).currentSession.currentOrder.getTotalQuantity() <= 0) {

						cornertext.setVisibility(View.VISIBLE);
						cornertext.setText(User.getInstance(mContext).currentSession.currentOrder.getTotalQuantity()
								+ "");
						Animation a = AnimationUtils.loadAnimation(mContext, R.anim.scale_up);
						cornertext.startAnimation(a);
						cornertext.setVisibility(View.GONE);
					} else {
						cornertext.setVisibility(View.VISIBLE);
						cornertext.setText(User.getInstance(mContext).currentSession.currentOrder.getTotalQuantity()
								+ "");
						Animation a = AnimationUtils.loadAnimation(mContext, R.anim.scale_up);
						cornertext.startAnimation(a);
					}
				}
				if (User.getInstance(mContext).currentSession.currentOrder.getQuantityOfDishByIndex(groupPosition) == 0) {
					User.getInstance(mContext).currentSession.currentOrder.removeDishAtIndex(groupPosition);
					notifyDataSetChanged();
				}
			}
		});

		plusButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				User.getInstance(mContext).currentSession.currentOrder.incrementDishAtIndex(groupPosition);
				notifyDataSetChanged();
				final View parent = (View) view.getParent().getParent().getParent().getParent().getParent();
				TextView cornertext;
				cornertext = (TextView) parent.findViewById(R.id.corner);
				cornertext.setVisibility(View.VISIBLE);
				cornertext.setText(User.getInstance(mContext).currentSession.currentOrder.getTotalQuantity() + "");
				Animation a = AnimationUtils.loadAnimation(mContext, R.anim.scale_up);
				cornertext.startAnimation(a);
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