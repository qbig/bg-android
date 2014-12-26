package sg.com.bigspoon.www.data;

import com.google.gson.annotations.SerializedName;

public class CategoryModel {
	
	public int id;
	public String name;
	
	@SerializedName("desc")
	public String description;
	 
}
