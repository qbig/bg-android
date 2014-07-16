package sg.com.bigspoon.www.data;

public class DiningSession {
	/*
	 * Containing all info related to current dining session
	 */
	
	public Order currentOrder;
	public Order pastOrder;
	public TableModel sittingAtTable;
	
	public DiningSession() {
		currentOrder = new Order();
		pastOrder = new Order();
		sittingAtTable = new TableModel();
	}
}
