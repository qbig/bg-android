package sg.com.bigspoon.www.adapters;

import java.util.ArrayList;
import java.util.List;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.data.Modifer;
import sg.com.bigspoon.www.data.ModifierSection;
import android.content.Context;
import android.graphics.Color;
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
import android.widget.ToggleButton;
import za.co.immedia.pinnedheaderlistview.SectionedBaseAdapter;
import static sg.com.bigspoon.www.data.Constants.MODIFIER_SECTION_TYPE_COUNTER;
import static sg.com.bigspoon.www.data.Constants.MODIFIER_SECTION_TYPE_RADIO;

;

public class ModifierListViewAdapter extends SectionedBaseAdapter {
	Modifer mModifierModel;
	Context mContext;

	public ModifierListViewAdapter(Context context, Modifer model) {
		super();
		mContext = context;
		mModifierModel = model;
	}

	@Override
	public int getSectionCount() {
		return mModifierModel.sections.length;
	}

	@Override
	public int getCountForSection(int section) {
		return mModifierModel.sections[section].items.size();
	}

	static class CounterItemViewHolder {
		protected TextView text;
		public ImageButton incre;
		public ImageButton decre;
		public TextView counterTextView;
	}

	@Override
	public View getItemView(int section, int position, View convertView, final ViewGroup parent) {
		final ModifierSection currentSectionModel = mModifierModel.sections[section];
		CounterItemViewHolder counterViewHolder = null;

		if (null == convertView) {
			LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflator.inflate(R.layout.activity_modifier_addon, null);

		} else {
			LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflator.inflate(R.layout.activity_modifier_addon, null);
			counterViewHolder = (CounterItemViewHolder) convertView.getTag();
		}
		
	
		if (mModifierModel.sections[section].type.equals(MODIFIER_SECTION_TYPE_RADIO)) {

			final FrameLayout layoutholder = (FrameLayout) convertView.findViewById(R.id.frameLayout1);
			final RadioButton radioButton = new RadioButton(parent.getContext());
			final float scale = parent.getContext().getResources().getDisplayMetrics().density;
			int padding_5dp = (int) (5 * scale + 0.5f);
			int padding_25dp = (int) (25 * scale + 0.5f);
			int padding_37dp = (int) (37 * scale + 0.5f);
			int padding_35dp = (int) (35 * scale + 0.5f);
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(padding_35dp, padding_35dp);
			params.gravity = Gravity.RIGHT;
			params.setMargins(0, 0, padding_37dp, 0);
			radioButton.setLayoutParams(params);
			radioButton.setPadding(padding_5dp, padding_5dp, padding_25dp, padding_5dp);
			radioButton.setBackgroundResource(R.drawable.radiobackground);
			radioButton.setButtonDrawable(android.R.color.transparent);
			layoutholder.addView(radioButton);

			// TODO refactoring
			final String itemName = mModifierModel.sections[section].items.keySet().toArray()[position].toString();
			((TextView) convertView.findViewById(R.id.text)).setText(itemName);
			final int answer = currentSectionModel.answers.containsKey(itemName) ? currentSectionModel.answers.get(
					itemName).intValue() : 0;
			radioButton.setChecked(answer == 1);
			radioButton.setTag(R.id.ITEM_NAME, itemName);
			radioButton.setTag(R.id.POSITION, position);
			radioButton.setTag(R.id.SECTION, section);
			radioButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					final String selectedItemName = (String) view.getTag(R.id.ITEM_NAME);
					final int section = ((Integer) view.getTag(R.id.SECTION)).intValue();
					mModifierModel.sections[section].toggle(selectedItemName);
					((RadioButton) view).setChecked(mModifierModel.sections[section].answers.get(selectedItemName).intValue() == 1 );
					notifyDataSetChanged();
				}
			});

			return convertView;
		} else {

			final FrameLayout layoutholder = (FrameLayout) convertView.findViewById(R.id.frameLayout1);
			final ImageButton minusButton = new ImageButton(parent.getContext());
			final float scale = parent.getContext().getResources().getDisplayMetrics().density;
			final int padding_30dp = (int) (30 * scale + 0.5f);
			final int padding_35dp = (int) (35 * scale + 0.5f);
			final int width = padding_30dp;
			final int height = padding_30dp;
			final FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(width, height, Gravity.CENTER
					| Gravity.LEFT);
			minusButton.setLayoutParams(params1);
			minusButton.setBackgroundResource(R.drawable.btn_minus_with_circle);
			layoutholder.addView(minusButton);

			final ImageButton plusButton = new ImageButton(parent.getContext());
			final FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(width, height, Gravity.CENTER
					| Gravity.RIGHT);
			plusButton.setLayoutParams(params2);
			plusButton.setBackgroundResource(R.drawable.add_btn_with_circle);
			layoutholder.addView(plusButton);

			final TextView itemCounterText = new TextView(parent.getContext());
			final int width3 = padding_35dp;
			final int height3 = padding_35dp;
			final FrameLayout.LayoutParams params3 = new FrameLayout.LayoutParams(width3, height3);
			params3.gravity = Gravity.CENTER;
			itemCounterText.setLayoutParams(params3);
			itemCounterText.setBackgroundResource(R.drawable.circle_for_text);
			itemCounterText.setText("0");
			itemCounterText.setTextColor(parent.getContext().getResources().getColor(R.color.black));
			itemCounterText.setTextSize(19);
			itemCounterText.setGravity(Gravity.CENTER);
			itemCounterText.setId(R.id.textPicker);
			layoutholder.addView(itemCounterText);

			counterViewHolder = new CounterItemViewHolder();
			counterViewHolder.text = ((TextView) convertView.findViewById(R.id.text));
			counterViewHolder.decre = minusButton;
			counterViewHolder.incre = plusButton;
			counterViewHolder.counterTextView = itemCounterText;
			final TextView temp = counterViewHolder.counterTextView;
			final String itemName = mModifierModel.sections[section].items.keySet().toArray()[position].toString();
			counterViewHolder.decre.setTag(R.id.ITEM_NAME, itemName);
			counterViewHolder.decre.setTag(R.id.POSITION, position);
			counterViewHolder.decre.setTag(R.id.SECTION, section);
			// add listener for the text change in the textview
			counterViewHolder.decre.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					final String itemName = (String) v.getTag(R.id.ITEM_NAME);
					int value = Integer.parseInt(temp.getText().toString()) - 1;
					if (value < 0) {
						value = 0;
					}
					final int section = ((Integer) v.getTag(R.id.SECTION)).intValue();
					mModifierModel.sections[section].answers.put(itemName, value);
					temp.setText(String.valueOf(value));
				}
			});
			counterViewHolder.incre.setTag(R.id.ITEM_NAME, itemName);
			counterViewHolder.incre.setTag(R.id.POSITION, position);
			counterViewHolder.incre.setTag(R.id.SECTION, section);
			counterViewHolder.incre.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					final String itemName = (String) v.getTag(R.id.ITEM_NAME);
					final int value = Integer.parseInt(temp.getText().toString()) + 1;
					final int section = ((Integer) v.getTag(R.id.SECTION)).intValue();
					mModifierModel.sections[section].answers.put(itemName, value);
					temp.setText(String.valueOf(value));
				}
			});

			convertView.setTag(counterViewHolder);
			convertView.setTag(R.id.textPicker, counterViewHolder.counterTextView);

			counterViewHolder.text.setText(itemName);
			final int counterAnswer = currentSectionModel.answers.containsKey(itemName) ? currentSectionModel.answers.get(itemName) : 0;
			counterViewHolder.counterTextView.setText(counterAnswer + "");

			return convertView;
		}
	}

	@Override
	public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
		LinearLayout layout = null;
		if (convertView == null) {
			LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			layout = (LinearLayout) inflator.inflate(R.layout.header_item, null);
		} else {
			layout = (LinearLayout) convertView;
		}
		((TextView) layout.findViewById(R.id.maintitle)).setText(mModifierModel.sections[section].itemTitle);
		((TextView) layout.findViewById(R.id.maintitle)).setTextColor(Color.parseColor("#"
				+ mModifierModel.itemTitleColor));
		((TextView) layout.findViewById(R.id.subtitle)).setText(mModifierModel.sections[section].itemTitleDescription);
		((TextView) layout.findViewById(R.id.subtitle)).setTextColor(Color.parseColor("#"
				+ mModifierModel.itemTextColor));
		return layout;
	}

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

}
