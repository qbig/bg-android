package sg.com.bigspoon.www.data;

import java.util.ArrayList;

public class Order {
	/*
	 * Order Class contains meta data current/past orders of current session. eg. general note
	 * It is a compound data structure, which could contain several {@link OrderItem}
	 * 
	 */
	
	public String generalNote;
	public ArrayList<OrderItem> items;
	
	public Order() {
		items = new ArrayList<OrderItem>();
	}

}
