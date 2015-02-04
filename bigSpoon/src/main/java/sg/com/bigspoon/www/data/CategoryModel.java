package sg.com.bigspoon.www.data;

import com.google.gson.annotations.SerializedName;

public class CategoryModel {
	
	public int id;
	public String name;

    @SerializedName("is_list_view_only")
	public boolean isListOnly;

	@SerializedName("desc")
	public String description;
	 
}
