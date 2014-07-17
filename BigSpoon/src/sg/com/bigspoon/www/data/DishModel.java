package sg.com.bigspoon.www.data;

import com.google.gson.annotations.SerializedName;

public class DishModel {
	public static class Photo {
		@SerializedName("thumbnail_large")
		public String thumbnailLarge;
		
		public String original;
		public String thumbnail;
	}
	
	public Photo photo;
	
	public CategoryModel[] categories;
	
	@SerializedName("average_rating")
	public double rating;
	
	public int id;
	
	@SerializedName("outlet")
	public int fromOutlet;
	
	public String name;
	public int pos;

	@SerializedName("desc")
	public String description;
	
	@SerializedName("start_time")
	public String startTime;
	
	@SerializedName("end_time")
	public String endTime;
 
	public double price;
	public int quantity;
	
	@SerializedName("can_be_customized")
	public boolean customizable;
	
	@SerializedName("custom_order_json")
	public String modifierJsonString;
	
	@SerializedName("is_active")
	public boolean isActive;
	
	public Modifer modifier;
	
	public boolean isDummyDish(){
		return price < 0.01;
	}

}
