package sg.com.bigspoon.www.data;

import java.util.ArrayList;
import java.util.HashMap;
import static sg.com.bigspoon.www.data.Constants.DESSERT_CATEGORY_ID;

public class Order {
	/*
	 * Order Class contains meta data current/past orders of current session. eg. general note
	 * It is a compound data structure, which could contain several {@link OrderItem}
	 * 
	 */
	
	public String mGeneralNote;
	public ArrayList<OrderItem> mItems;
	
	public Order() {
		mItems = new ArrayList<OrderItem>();
	}
	
	public void addDish(DishModel dish){
		if(! containDishWithId(dish.id) || dish.customizable){
			final OrderItem newItem = new OrderItem();
			newItem.dish = dish;
			newItem.quantity = 1;
			if (dish.customizable) {
				newItem.modifierAnswer = dish.modifier.getAnswer();
			}
			mItems.add(newItem);
		} else {
			incrementDishWithId(dish.id);
		}
	}
	
	public void minusDish(DishModel dish){
		if (containDishWithId(dish.id)) {		
			for(OrderItem item : mItems) {
				if (item.dish.id == dish.id){
					if (item.quantity > 1) {
						item.quantity --;
					} else {
						mItems.remove(item);
					}
					
					break;
				}
			}
		}
	}
	
	public void addNoteForDishAtIndex(String note, int dishIndex){
		mItems.get(dishIndex).note = note;
	}
	
	public String getNoteForDishAtIndex(int dishIndex) {
		return mItems.get(dishIndex).note;
	}
	
	public HashMap<String, Integer> getModifierAnswerAtIndex (int dishIntex){
		return mItems.get(dishIntex).modifierAnswer;
	}
	
	public void setModifierAnswer(HashMap<String, Integer> answer, int dishIndex) {
		mItems.get(dishIndex).modifierAnswer = answer;
	}
	
	public int getQuantityOfDishByDish(DishModel dish) {
		return getQuantityOfDishByID (dish.id);
	}
	
	public int getQuantityOfDishByID(int dishID){
		for(OrderItem item : mItems) {
			if (item.dish.id == dishID){
				return item.dish.quantity;
			}
		}
		return 0;
	}

	public int getTotalQuantity() {
		int result = 0;
		for(OrderItem item : mItems) {
			result += item.quantity;
		}
		return result;
	}
	
	public double getTotalPrice() {
		double result = 0;
		for(OrderItem item : mItems) {
			result += item.quantity * item.dish.price;
		}
		return result;
	}
	
	public void mergeWithAnotherOrder(Order newOrder){
		for(OrderItem itemToMerge : newOrder.mItems) {
			if (itemToMerge.dish.customizable){
				this.mItems.add(itemToMerge);
			} else if (this.containDishWithId(itemToMerge.dish.id)) {
				getItemWithDishId(itemToMerge.dish.id).quantity += itemToMerge.dish.quantity;
			} else {
				mItems.add(itemToMerge);
			}
		}
	}

	public void incrementDishWithId (int dishID){
		getItemWithDishId(dishID).quantity++;
	}
	
	public void decrementDishWithId (int dishID){
		getItemWithDishId(dishID).quantity--;
	}
	
	public void incrementDishAtIndex(int dishIndex){
		mItems.get(dishIndex).quantity++;
	}
	
	public void decrementDishAtIndex(int dishIndex){
		mItems.get(dishIndex).quantity--;
	}
	
	public void removeDishAtIndex (int dishIndex) {
		mItems.remove(dishIndex);
	}
	
	public String getModifierDetailsTextAtIndex (int dishIndex){
		mItems.get(dishIndex).dish.modifier.setAnswer(mItems.get(dishIndex).modifierAnswer);
		return mItems.get(dishIndex).dish.modifier.getDetailsTextForDisplay();
	}
	
	public HashMap<String,String> getMergedTextForNotesAndModifier() {
		final HashMap<String, String> result = new HashMap<String, String>();
		for (int i = 0, len = mItems.size(); i < len ; i ++){
			OrderItem item = mItems.get(i);
			if (item.note != null && item.note.length() != 0 && item.dish.customizable){
				result.put(i + "", String.format("%s\nnote:%s", getModifierDetailsTextAtIndex(i), item.note ));
			} else if (item.note != null && item.note.length() != 0){
				result.put(i + "", item.note);
			} else if (item.dish.customizable){
				result.put(i + "", getModifierDetailsTextAtIndex(i));
			}
		}
		return result;
	}
	
	public void decrementDishName (String dishName){
		for(OrderItem item : mItems) {
			if (item.dish.name.equals(dishName)){
				item.quantity--;
			}
		}
	}
	
	public boolean containDessert() {
		for(OrderItem item : mItems) {
			if (item.dish.categories[0].id == DESSERT_CATEGORY_ID){
				return true;
			}
		}
		return false;
	}
	
	private boolean containDishWithId(int dishID) {
		boolean result = false;
		for(OrderItem item : mItems) {
			if (item.dish.id == dishID){
				result = true;
				break;
			}
		}
		return result;
	}
	
	public OrderItem getItemWithDishId(int dishID) {
		for(OrderItem item : mItems) {
			if (item.dish.id == dishID){
				return item;
			}
		}
		return null;
	}

}
