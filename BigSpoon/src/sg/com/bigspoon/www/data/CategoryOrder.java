package sg.com.bigspoon.www.data;

import com.google.gson.annotations.SerializedName;

public class CategoryOrder {
	@SerializedName("order_index")
	public int position;
	
	@SerializedName("category_id")
	public int categoryId;
	 
}
