package sg.com.bigspoon.www.data;

import static sg.com.bigspoon.www.data.Constants.MODIFIER_SECTION_TYPE_RADIO;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Modifer {
	/*
	 * Hierarchy:
	 * 
	 * Modifier
	 * -- ModifierSection
	 * -- -- ModifierItem (Map)
	 */
	public String itemTextColor;
	public String itemTitleColor;
	public String backgroundColor;
	public ModifierSection[] sections;
	
	public HashMap<String, Integer> getAnswer(){
		final HashMap<String, Integer> result = new HashMap<String, Integer>();
		for( int i = 0, len = sections.length; i < len; i++){
			for (String itemNameKey : sections[i].items.keySet()) {
			    final Integer answer = sections[i].answers.containsKey(itemNameKey) ? sections[i].answers.get(itemNameKey) : 0;
			    if (answer.intValue() > 0){
			    	result.put(String.format("%s-%s", sections[i].itemTitle, itemNameKey), answer); 
			    }
			}
		}
		return result;	
	}
	
	public void setAnswer(HashMap<String, Integer> answer){
		cleanUp();
		for( int i = 0, len = sections.length; i < len; i++){
			for (String itemNameKey : sections[i].items.keySet()) {
			    Integer itemCount = answer.get(String.format("%s-%s", sections[i].itemTitle, itemNameKey));
			    if (itemCount != null){
			    	sections[i].answers.put(itemNameKey, itemCount); 
			    }
			}
		}	
	}
	
	public double getPriceChange() {
		double result = 0;
		for( int i = 0, len = sections.length; i < len; i++){
			result += sections[i].getSum();
		}
		return result;
	}
	
	public String getDetailsTextForDisplay() {
		StringBuilder result = new StringBuilder();
		for( int i = 0, len = sections.length; i < len; i++){
			for (String itemNameKey : sections[i].items.keySet()) {
				
			    final Integer answer = sections[i].answers.get(itemNameKey);
			    if (answer != null && answer.intValue() > 0){
			    	if (sections[i].type.equals(MODIFIER_SECTION_TYPE_RADIO)){
			    		result.append(String.format("%s: %s\n", sections[i].itemTitle, itemNameKey));
			    	} else {
			    		result.append(String.format("%s x %d\n", itemNameKey, answer.intValue()));
			    	} 
			    }
			}
		}
		return result.toString();
	}
	
	private void cleanUp() {
		for (int i = 0, len = sections.length; i < len; i++) {
			sections[i].answers = new LinkedHashMap<String, Integer>();
		}
	}
//	"custom_order_json": "{
//	    \"itemTextColor\": \"FFFFFF\",
//	    \"itemTitleColor\": \"E29B5C\",
//	    \"backgroundColor\": \"434851\",
//	    \"sections\": [
//	        {
//	            \"threshold\": 0.5,
//	            \"itemTitle\": \"Butter\",
//	            \"type\": \"count\",
//	            \"itemTitleDescription\": \"2ndbutteronwards$0.50/ea\",
//	            \"items\": {
//	                \"Garlic+Herbs\": 0.5,
//	                \"Salted\": 0.5,
//	                \"Unsalted\": 0.5,
//	                \"Rum+Raisin\": 0.5
//	            }
//	        },
//	        {
//	            \"threshold\": 1,
//	            \"itemTitle\": \"Maplesyrup\",
//	            \"type\": \"count\",
//	            \"itemTitleDescription\": \"2ndsyruponwards$1/ea\",
//	            \"items\": {
//	                \"\": 1
//	            }
//	        },
//	        {
//	            \"itemTitle\": \"ChocolatePancakes\",
//	            \"type\": \"radio\",
//	            \"itemTitleDescription\": \"\",
//	            \"items\": {
//	                \"Yes(+$1)\": 1,
//	                \"No\": 0
//	            }
//	        },
//	        {
//	            \"threshold\": 0,
//	            \"itemTitle\": \"PerfectCompanions\",
//	            \"type\": \"count\",
//	            \"itemTitleDescription\": \"\",
//	            \"items\": {
//	                \"ChickenChipolata(+$2)\": 2,
//	                \"ScrambledEggs(+$2.5)\": 2.5,
//	                \"Strawberry(+$1.5)\": 1.5,
//	                \"SauteedMushrooms(+$2.5)\": 2.5,
//	                \"Banana(+$1.5)\": 1.5,
//	                \"CornBeefHash(+$2.5)\": 2.5,
//	                \"Bacon(+$2.5)\": 2.5
//	            }
//	        }
//	    ],
//	   
//	}"
}
