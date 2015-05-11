package sg.com.bigspoon.www.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import static sg.com.bigspoon.www.data.Constants.DESSERT_CATEGORY_ID;

public class Order {
	/*
	 * Order Class contains meta data current/past orders of current session.
	 * eg. general note It is a compound data structure, which could contain
	 * several {@link OrderItem}
	 */

	public String mGeneralNote = null;
	public ArrayList<OrderItem> mItems;
	public boolean canPop = false;
	private Stack st;
	public Order() {
		mItems = new ArrayList<OrderItem>();
		st = new Stack();
	}

	public void addDish(DishModel dish) {
		canPop = true;
		if (!containDishWithId(dish.id) || dish.customizable) {
			final OrderItem newItem = new OrderItem();
			newItem.dish = dish;
			newItem.quantity = 1;
			if (dish.customizable) {
				newItem.modifierAnswer = dish.modifier.getAnswer();
			}
			mItems.add(newItem);
			st.push(newItem);
			dish.quantity -= 1;
		} else {
			incrementDishWithId(dish.id);
			st.push(new Integer(dish.id));
		}
	}

	public void clearUndoCache() {
		canPop = false;
		st = new Stack();
	}

	public void pop(){
		if (canPop && !st.isEmpty()) {
			Object prev = st.pop();
			if (prev instanceof Integer) {
				decrementDishWithId(((Integer) prev).intValue());
			} else {
				final int lastInd = mItems.size() - 1;
				mItems.get(lastInd).dish.quantity ++;
				mItems.remove(lastInd);
			}
		}
	}

	public void minusDish(DishModel dish) {
		if (containDishWithId(dish.id)) {
			for (OrderItem item : mItems) {
				if (item.dish.id == dish.id) {
					if (item.quantity > 1) {
						item.quantity--;
						item.dish.quantity += 1;
					} else {
						mItems.remove(item);
					}

					break;
				}
			}
		}
	}

	public void addNoteForDishAtIndex(String note, int dishIndex) {
		mItems.get(dishIndex).note = note;
	}

	public String getNoteForDishAtIndex(int dishIndex) {
		return mItems.get(dishIndex).note;
	}

	public HashMap<String, Integer> getModifierAnswerAtIndex(int dishIntex) {
		return mItems.get(dishIntex).modifierAnswer;
	}

	public HashMap<String, HashMap<String, Integer>> getAllModifierAnswers() {
		HashMap<String, HashMap<String, Integer>> result = new HashMap<String, HashMap<String, Integer>>();
		for (int i = 0, len = mItems.size(); i < len; i++) {
			final OrderItem item = mItems.get(i);
			if (item.dish.customizable) {
				result.put(i + "", getModifierAnswerAtIndex(i));
			}
		}
		return result;
	}

	public void setModifierAnswer(HashMap<String, Integer> answer, int dishIndex) {
		mItems.get(dishIndex).modifierAnswer = answer;
	}

	public int getQuantityOfDishByDish(DishModel dish) {
		return getQuantityOfDishByID(dish.id);
	}

	public int getQuantityOfDishByID(int dishID) {
		for (OrderItem item : mItems) {
			if (item.dish.id == dishID) {
				return item.quantity;
			}
		}
		return 0;
	}

	public int getQuantityOfDishByIndex(int dishIndex) {
		return mItems.get(dishIndex).quantity;
	}

	public int getTotalQuantity() {
		int result = 0;
		for (OrderItem item : mItems) {
			result += item.quantity;
		}
		return result;
	}

	public double getTotalPrice() {
		double result = 0;
		for (int i = 0; i < mItems.size(); i ++) {
			OrderItem item = mItems.get(i);
			if (item.dish.customizable) {
				result += getModifierPriceChangeAtIndex(i) + item.dish.price;
			} else {
				result += item.quantity * item.dish.price;
			}
			
		}
		return result;
	}

	public void mergeWithAnotherOrder(Order newOrder) {
		for (OrderItem itemToMerge : newOrder.mItems) {
			if (itemToMerge.dish.customizable) {
				this.mItems.add(itemToMerge);
			} else if (this.containDishWithId(itemToMerge.dish.id)) {
				getItemWithDishId(itemToMerge.dish.id).quantity += itemToMerge.quantity;
			} else {
				mItems.add(itemToMerge);
			}
		}
	}

	public void incrementDishWithId(int dishID) {
		final OrderItem item = getItemWithDishId(dishID);
		item.quantity++;
		item.dish.quantity--;
	}

	public void decrementDishWithId(int dishID) {
		final OrderItem item = getItemWithDishId(dishID);
		item.quantity--;
		item.dish.quantity++;
	}

	public void incrementDishAtIndex(int dishIndex) {
		final OrderItem item = mItems.get(dishIndex);
		item.quantity++;
		item.dish.quantity--;
	}

	public void decrementDishAtIndex(int dishIndex) {
		if (mItems.get(dishIndex).quantity <= 0){
			mItems.get(dishIndex).quantity = 0;
		} else {
			mItems.get(dishIndex).quantity--;
			mItems.get(dishIndex).dish.quantity += 1;
		}
	}

	public void removeDishAtIndex(int dishIndex) {
		mItems.get(dishIndex).dish.quantity += mItems.get(dishIndex).quantity;
		mItems.remove(dishIndex);
	}

	public String getModifierDetailsTextAtIndex(int dishIndex) {
		mItems.get(dishIndex).dish.modifier.setAnswer(mItems.get(dishIndex).modifierAnswer);
		return mItems.get(dishIndex).dish.modifier.getDetailsTextForDisplay();
	}

	public double getModifierPriceChangeAtIndex(int dishIndex) {
		mItems.get(dishIndex).dish.modifier.setAnswer(mItems.get(dishIndex).modifierAnswer);
		return mItems.get(dishIndex).dish.modifier.getPriceChange();
	}

	public HashMap<String, String> getMergedTextForNotesAndModifier() {
		final HashMap<String, String> result = new HashMap<String, String>();
		for (int i = 0, len = mItems.size(); i < len; i++) {
			OrderItem item = mItems.get(i);
			if (item.note != null && item.note.length() != 0 && item.dish.customizable) {
				result.put(i + "", String.format("%s\nnote:%s", getModifierDetailsTextAtIndex(i), item.note));
			} else if (item.note != null && item.note.length() != 0) {
				result.put(i + "", item.note);
			} else if (item.dish.customizable) {
				result.put(i + "", getModifierDetailsTextAtIndex(i));
			}
		}
		return result;
	}

	public void decrementDishName(String dishName) {
		for (OrderItem item : mItems) {
			if (item.dish.name.equals(dishName)) {
				item.quantity--;
				item.dish.quantity++;
			}
		}
	}

	public boolean containDessert() {
		for (OrderItem item : mItems) {
			if (item.dish.categories[0].id == DESSERT_CATEGORY_ID) {
				return true;
			}
		}
		return false;
	}

	private boolean containDishWithId(int dishID) {
		boolean result = false;
		for (OrderItem item : mItems) {
			if (item.dish.id == dishID) {
				result = true;
				break;
			}
		}
		return result;
	}

	public OrderItem getItemWithDishId(int dishID) {
		for (OrderItem item : mItems) {
			if (item.dish.id == dishID) {
				return item;
			}
		}
		return null;
	}

	public JsonObject getJsonOrders(int tableID) {
		final JsonObject jsonOrders = new JsonObject();
		final Gson gson = new Gson();
		ArrayList<HashMap<String, Integer>> pairs = new ArrayList<HashMap<String, Integer>>();
		for (OrderItem item : mItems) {
			HashMap<String, Integer> pair = new HashMap<String, Integer>();
			pair.put(item.dish.id + "", item.quantity);
			pairs.add(pair);
		}
		jsonOrders.add("dishes", gson.toJsonTree(pairs));
		jsonOrders.addProperty("table", Integer.valueOf(tableID));

		final HashMap<String, String> notes = getMergedTextForNotesAndModifier();
		if (!notes.isEmpty()) {
			jsonOrders.add("notes", gson.toJsonTree(notes));
		}

		final HashMap<String, HashMap<String, Integer>> modifierAnswers = getAllModifierAnswers();
		if (!modifierAnswers.isEmpty()) {
			jsonOrders.add("modifiers", gson.toJsonTree(modifierAnswers));
		}

		if (mGeneralNote != null && !mGeneralNote.equals("")) {
			jsonOrders.addProperty("note", mGeneralNote);
		}

		return jsonOrders;
	}

}
