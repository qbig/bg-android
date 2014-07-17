package sg.com.bigspoon.www.data;

import java.util.HashMap;

public class OrderItem {
	/*
	 * This class contains infos relate to a particular dish ordered, and its quantity. Its Note and modifier answers.
	 */
	
	public DishModel dish;
	public int quantity;
	public String note;
	public HashMap<String, Integer> modifierAnswer; 
			
}
