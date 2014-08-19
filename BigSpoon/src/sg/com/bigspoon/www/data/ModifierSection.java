package sg.com.bigspoon.www.data;

import java.util.LinkedHashMap;
import java.util.Map;
import static sg.com.bigspoon.www.data.Constants.MODIFIER_SECTION_TYPE_COUNTER;
import static sg.com.bigspoon.www.data.Constants.MODIFIER_SECTION_TYPE_RADIO;
import com.google.gson.internal.LinkedTreeMap;

public class ModifierSection {
	/*
	 * Hierarchy:
	 * 
	 * Modifier -- ModifierSection -- -- ModifierItem (Map)
	 */
	public double threshold;
	public String itemTitle;
	public String type;
	public String itemTitleDescription;
	public LinkedHashMap<String, Double> items;
	public LinkedHashMap<String, Integer> answers;

	public ModifierSection() {
		answers = new LinkedHashMap<String, Integer>();
	}

	public void setDefaultAnswerForRadio() {
		if (type != null && type.equals(MODIFIER_SECTION_TYPE_RADIO) && ! items.isEmpty()) {
			answers.put(items.keySet().iterator().next(), 1);
		}
	}

	public double getSum() {

		double result = 0;
		for (String itemNameKey : answers.keySet()) {
			result += answers.get(itemNameKey) * items.get(itemNameKey);
		}

		if (result < 0) {
			return result;
		} else {
			result -= this.threshold;
			return result > 0 ? result : 0;
		}

	}

	public void toggle(String toggledItemName) {
		if (type.equals(MODIFIER_SECTION_TYPE_RADIO)) {
			final int currentAnswerForItem = answers.containsKey(toggledItemName) ? answers.get(toggledItemName)
					.intValue() : 0;
			final int toggledAnswer = currentAnswerForItem == 0 ? 1 : 0;
			for (String itemNameKey : items.keySet()) {
				if (itemNameKey.equals(toggledItemName)) {
					answers.put(itemNameKey, toggledAnswer);
				} else {
					answers.put(itemNameKey, currentAnswerForItem);
				}
			}
		}
	}

	// {
	// \"threshold\": 0.5,
	// \"itemTitle\": \"Butter\",
	// \"type\": \"count\",
	// \"itemTitleDescription\": \"2ndbutteronwards$0.50/ea\",
	// \"items\": {
	// \"Garlic+Herbs\": 0.5,
	// \"Salted\": 0.5,
	// \"Unsalted\": 0.5,
	// \"Rum+Raisin\": 0.5
	// }
	// },
}
