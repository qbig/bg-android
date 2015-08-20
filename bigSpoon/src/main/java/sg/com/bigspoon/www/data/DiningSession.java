package sg.com.bigspoon.www.data;

import java.util.HashMap;

public class DiningSession {
	/*
	 * Containing all info related to current dining session
	 */
	private HashMap<String, Order> currentOrders;
	private HashMap<String, Order> pastOrders;
	private String currentOutletName;
	public String pickupTime;
	
	public void swithToOulet(String outletName) {
		this.currentOutletName = outletName;
	}
	
	public Order getCurrentOrder(String currentOutletName) {
		this.currentOutletName = currentOutletName;
		if (currentOrders.containsKey(currentOutletName)){
			return currentOrders.get(currentOutletName);
		} else {
			final Order currentOrder = new Order();
			currentOrders.put(currentOutletName, currentOrder);
			return currentOrder;
		}
	}
	
	public Order getPastOrder(String currentOutletName) {
		this.currentOutletName = currentOutletName;
		if (pastOrders.containsKey(currentOutletName)){
			return pastOrders.get(currentOutletName);
		} else {
			final Order newPastOrder = new Order();
			pastOrders.put(currentOutletName, newPastOrder);
			return newPastOrder;
		}
	}
	
	public Order getCurrentOrder() {
		return getCurrentOrder(this.currentOutletName);
	}
	
	public Order getPastOrder() {
		return getPastOrder(currentOutletName);
	}
	
	public void setCurrentOrder(Order order) {
		currentOrders.put(currentOutletName, order);
	}
	
	public void setPastOrder(Order order) {
		pastOrders.put(currentOutletName, order);
	}
	
	public void clearCurrentOrder() {
		currentOrders.put(currentOutletName, new Order());
	}
	
	public void clearPastOrder() {
		pastOrders.put(currentOutletName, new Order());
	}
	
	public TableModel sittingAtTable;
	
	public DiningSession(String currentOutletName) {
		this.currentOutletName = currentOutletName;
		currentOrders = new HashMap<String, Order>();
		pastOrders = new HashMap<String, Order>();
		sittingAtTable = new TableModel();
	}
	
	public void closeCurrentSession() {
		clearPastOrder();
		sittingAtTable = new TableModel();
	}
	
}
