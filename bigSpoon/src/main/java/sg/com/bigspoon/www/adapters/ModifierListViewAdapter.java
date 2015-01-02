package sg.com.bigspoon.www.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.data.Modifer;
import sg.com.bigspoon.www.data.ModifierSection;
import za.co.immedia.pinnedheaderlistview.SectionedBaseAdapter;

import static sg.com.bigspoon.www.data.Constants.MODIFIER_SECTION_TYPE_RADIO;

public class ModifierListViewAdapter extends SectionedBaseAdapter {
	private static final int ITEM_VIEW_TYPE_RADIO = 0;
	private static final int ITEM_VIEW_TYPE_COUNT = 1;

	Modifer mModifierModel;
	Context mContext;
	private LayoutInflater inflator;
	private final float mScale;
	private final int PADDING_30DP;
	private final int PADDING_35DP;
	private final int PADDING_5DP;
	private final int PADDING_25DP;
	private final int PADDING_37DP;

	public ModifierListViewAdapter(Context context, Modifer model) {
		super();
		mContext = context;
		model.initRadio();
		mModifierModel = model;
		inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mScale = context.getResources().getDisplayMetrics().density;
		PADDING_30DP = (int) (30 * mScale + 0.5f);
		PADDING_35DP = (int) (35 * mScale + 0.5f);
		PADDING_5DP = (int) (5 * mScale + 0.5f);
		PADDING_25DP = (int) (25 * mScale + 0.5f);
		PADDING_37DP = (int) (37 * mScale + 0.5f);
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
		protected TextView itemNameView;
		public ImageButton increButton;
		public ImageButton decreButton;
		public TextView counterTextView;
	}

	static class RadioItemViewHolder {
		protected TextView itemNameView;
		public RadioButton radioButton;
	}

	@Override
	public int getItemViewType(int section, int position) {
		return mModifierModel.sections[section].type.equals(MODIFIER_SECTION_TYPE_RADIO) ? ITEM_VIEW_TYPE_RADIO
				: ITEM_VIEW_TYPE_COUNT;
	}

	@Override
	public int getItemViewTypeCount() {
		return 2;
	}

	@Override
	public View getItemView(int section, int position, View convertView, final ViewGroup parent) {
		final ModifierSection currentSectionModel = mModifierModel.sections[section];
		CounterItemViewHolder counterViewHolder = null;
		RadioItemViewHolder radioViewHolder = null;

		if (mModifierModel.sections[section].type.equals(MODIFIER_SECTION_TYPE_RADIO)) {
			if (null == convertView) {
				convertView = inflator.inflate(R.layout.activity_modifier_addon, null);
				radioViewHolder = new RadioItemViewHolder();
                convertView.setBackgroundColor(Color.parseColor(mModifierModel.backgroundColor.trim()));
				final FrameLayout containerLayout = (FrameLayout) convertView.findViewById(R.id.frameLayout1);
				final RadioButton radioButton = new RadioButton(parent.getContext());
				setupRadioButton(radioButton);
				containerLayout.addView(radioButton);

				radioViewHolder.radioButton = radioButton;
				radioViewHolder.itemNameView = (TextView) convertView.findViewById(R.id.text);
                radioViewHolder.itemNameView.setTextColor(Color.parseColor(mModifierModel.itemTextColor.trim()));
                convertView.setTag(R.id.HOLDER, radioViewHolder);
			} else {
				radioViewHolder = (RadioItemViewHolder) convertView.getTag(R.id.HOLDER);
			}
			
			String itemName = "";
			for (String item : currentSectionModel.items.keySet()){
				if (currentSectionModel.itemSequences != null && currentSectionModel.itemSequences.get(item).intValue() == position){
					itemName= item;
					break;
				}
			}
			if (itemName.isEmpty()){
				itemName = (String) currentSectionModel.items.keySet().toArray()[position];
			}
			
			radioViewHolder.itemNameView.setText(itemName);
			final int answer = currentSectionModel.answers.containsKey(itemName) ? currentSectionModel.answers.get(
					itemName).intValue() : 0;
			radioViewHolder.radioButton.setChecked(answer == 1);
			radioViewHolder.radioButton.setTag(R.id.ITEM_NAME, itemName);
			radioViewHolder.radioButton.setTag(R.id.POSITION, position);
			radioViewHolder.radioButton.setTag(R.id.SECTION, section);

			return convertView;
		} else {
			if (null == convertView) {
				convertView = inflator.inflate(R.layout.activity_modifier_addon, null);
                convertView.setBackgroundColor(Color.parseColor(mModifierModel.backgroundColor.trim()));
				counterViewHolder = new CounterItemViewHolder();
				final FrameLayout containerLayout = (FrameLayout) convertView.findViewById(R.id.frameLayout1);

				final ImageButton minusButton = new ImageButton(parent.getContext());
				setupMinusButton(minusButton);
				containerLayout.addView(minusButton);

				final ImageButton plusButton = new ImageButton(parent.getContext());
				setupPlusButton(plusButton);
				containerLayout.addView(plusButton);

				final TextView itemCounterText = new TextView(parent.getContext());
				setupCounterText(itemCounterText);
				containerLayout.addView(itemCounterText);

				counterViewHolder.itemNameView = ((TextView) convertView.findViewById(R.id.text));
                counterViewHolder.itemNameView.setTextColor(Color.parseColor(mModifierModel.itemTextColor.trim()));
				counterViewHolder.increButton = plusButton;
				counterViewHolder.decreButton = minusButton;
				counterViewHolder.counterTextView = itemCounterText;
				convertView.setTag(R.id.HOLDER, counterViewHolder);
			} else {
				counterViewHolder = (CounterItemViewHolder) convertView.getTag(R.id.HOLDER);
			}

			String itemName = "";
			for (String item : currentSectionModel.items.keySet()){
				if (currentSectionModel.itemSequences != null && currentSectionModel.itemSequences.get(item).intValue() == position){
					itemName= item;
					break;
				}
			}
			if (itemName.isEmpty()){
				itemName = (String) currentSectionModel.items.keySet().toArray()[position];
			}
			
			final int counterAnswer = currentSectionModel.answers.containsKey(itemName) ? currentSectionModel.answers
					.get(itemName) : 0;
			counterViewHolder.decreButton.setTag(R.id.ITEM_NAME, itemName);
			counterViewHolder.decreButton.setTag(R.id.POSITION, position);
			counterViewHolder.decreButton.setTag(R.id.SECTION, section);
			counterViewHolder.decreButton.setTag(R.id.COUNTER_DISPLAY, counterViewHolder.counterTextView);

			counterViewHolder.increButton.setTag(R.id.ITEM_NAME, itemName);
			counterViewHolder.increButton.setTag(R.id.POSITION, position);
			counterViewHolder.increButton.setTag(R.id.SECTION, section);
			counterViewHolder.increButton.setTag(R.id.COUNTER_DISPLAY, counterViewHolder.counterTextView);

			counterViewHolder.itemNameView.setText(itemName);
			counterViewHolder.counterTextView.setText(counterAnswer + "");

			return convertView;
		}
	}

	private void setupCounterText(final TextView itemCounterText) {
		final FrameLayout.LayoutParams params3 = new FrameLayout.LayoutParams(PADDING_35DP, PADDING_35DP);
		params3.gravity = Gravity.CENTER;
		itemCounterText.setLayoutParams(params3);
		itemCounterText.setBackgroundResource(R.drawable.circle_for_text);
		itemCounterText.setText("0");
		itemCounterText.setTextColor(Color.BLACK);
		itemCounterText.setTextSize(19);
		itemCounterText.setGravity(Gravity.CENTER);
		itemCounterText.setId(R.id.textPicker);
	}

	private void setupPlusButton(final ImageButton plusButton) {
		final FrameLayout.LayoutParams paramsForPlusButton = new FrameLayout.LayoutParams(PADDING_30DP, PADDING_30DP,
				Gravity.CENTER | Gravity.RIGHT);
		plusButton.setLayoutParams(paramsForPlusButton);
		plusButton.setBackgroundResource(R.drawable.add_btn_with_circle);
		plusButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final String itemName = (String) v.getTag(R.id.ITEM_NAME);
				final TextView counterView = (TextView) v.getTag(R.id.COUNTER_DISPLAY);
				final int value = Integer.parseInt(counterView.getText().toString()) + 1;
				final int section = ((Integer) v.getTag(R.id.SECTION)).intValue();
				mModifierModel.sections[section].answers.put(itemName, value);
				counterView.setText(String.valueOf(value));
			}
		});
	}

	private void setupMinusButton(final ImageButton minusButton) {
		final FrameLayout.LayoutParams paramsForMinusButton = new FrameLayout.LayoutParams(PADDING_30DP, PADDING_30DP,
				Gravity.CENTER | Gravity.LEFT);
		minusButton.setLayoutParams(paramsForMinusButton);
		minusButton.setBackgroundResource(R.drawable.btn_minus_with_circle);
		minusButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final String itemName = (String) v.getTag(R.id.ITEM_NAME);
				final TextView counterView = (TextView) v.getTag(R.id.COUNTER_DISPLAY);
				int value = Integer.parseInt(counterView.getText().toString()) - 1;
				if (value < 0) {
					value = 0;
				}
				final int section = ((Integer) v.getTag(R.id.SECTION)).intValue();
				mModifierModel.sections[section].answers.put(itemName, value);
				counterView.setText(String.valueOf(value));
			}
		});
	}

	private void setupRadioButton(final RadioButton radioButton) {
		final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(PADDING_35DP, PADDING_35DP);
		params.gravity = Gravity.END;
		params.setMargins(0, 0, PADDING_37DP, 0);
		radioButton.setLayoutParams(params);
		radioButton.setPadding(PADDING_5DP, PADDING_5DP, PADDING_25DP, PADDING_5DP);
        radioButton.setBackgroundResource(R.drawable.radiobackground);
        radioButton.setButtonDrawable(android.R.color.transparent);

		radioButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				final String selectedItemName = (String) view.getTag(R.id.ITEM_NAME);
				final int section = ((Integer) view.getTag(R.id.SECTION)).intValue();
				mModifierModel.sections[section].toggle(selectedItemName);
				((RadioButton) view).setChecked(mModifierModel.sections[section].answers.get(selectedItemName)
						.intValue() == 1);
				notifyDataSetChanged();
			}
		});
	}

	@Override
	public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
		LinearLayout layout = null;
		if (convertView == null) {
			layout = (LinearLayout) inflator.inflate(R.layout.header_item, null);
            layout.setBackgroundColor(Color.parseColor(mModifierModel.backgroundColor.trim()));
		} else {
			layout = (LinearLayout) convertView;
		}
		((TextView) layout.findViewById(R.id.maintitle)).setText(mModifierModel.sections[section].itemTitle);
		((TextView) layout.findViewById(R.id.maintitle)).setTextColor(Color.parseColor(mModifierModel.itemTitleColor.trim()));
		((TextView) layout.findViewById(R.id.subtitle)).setText(mModifierModel.sections[section].itemTitleDescription);
		((TextView) layout.findViewById(R.id.subtitle)).setTextColor(Color.parseColor(mModifierModel.itemTextColor.trim()));
		return layout;
	}

	@Override
	public Object getItem(int section, int position) {
		return null;
	}

	@Override
	public long getItemId(int section, int position) {
		return 0;
	}

}
