package sg.com.bigspoon.www.data;

import java.util.Map;

import com.google.gson.internal.LinkedTreeMap;

public class ModifierSection {
	/*
	 * Hierarchy:
	 * 
	 * Modifier
	 * -- ModifierSection
	 * -- -- ModifierItem (Map)
	 */
	public double threshold;
	public String itemTitle;
	public String type;
	public String itemTitleDescription;
	public Map<String, Double>  items;
	public Map<String, Integer> answers;
	
	public ModifierSection() {
		answers = new LinkedTreeMap<String, Integer>();
	}
	
	public double getSum() {

		double result = 0;
		for(String itemNameKey : answers.keySet()){ 
			result += answers.get(itemNameKey) * items.get(itemNameKey);
		}
		
		if(result < 0){
			return result;
		} else {
			result -= this.threshold;
			return result > 0 ? result : 0;
		}
		
	}
	
//	{
//         \"threshold\": 0.5,
//         \"itemTitle\": \"Butter\",
//         \"type\": \"count\",
//         \"itemTitleDescription\": \"2ndbutteronwards$0.50/ea\",
//         \"items\": {
//             \"Garlic+Herbs\": 0.5,
//             \"Salted\": 0.5,
//             \"Unsalted\": 0.5,
//             \"Rum+Raisin\": 0.5
//         }
//     },
}
