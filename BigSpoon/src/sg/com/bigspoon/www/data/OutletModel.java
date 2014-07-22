package sg.com.bigspoon.www.data;

import com.google.gson.annotations.SerializedName;

public class OutletModel {

	public static class Restaurant {
		public Icon icon;
		public String name;
		public int id;
	}

	public static class Icon {
		public String thumbnail;
	}
	public Restaurant restaurant;
	public String imgURL;
	public String name;
	public String address;
	
	@SerializedName("phone")
	public String phoneNumber;
	
	@SerializedName("opening")
	public String operatingHours;
	
	@SerializedName("default_dish_photo")
	public String defaultDishPhoto;
	
	@SerializedName("discount")
	public String promotionalText;
	
	@SerializedName("water_popup_text")
	public String waterText;
	
	@SerializedName("bill_popup_text")
	public String billText;
	
	@SerializedName("id")
	public int outletID;

	public double lat;

	public double lng;
	
	@SerializedName("gst")
	public double gstRate;
	
	@SerializedName("scr")
	public double serviceChargeRate;
	
	@SerializedName("location_diameter")
	public double locationThreshold;
	
	@SerializedName("is_active")
	public boolean isActive;
	
	@SerializedName("is_by_default_photo_menu")
	public boolean isDefaultPhotoMenu;
	
	@SerializedName("request_for_water_enabled")
	public boolean isWaterEnabled;
	
	@SerializedName("ask_for_bill_enabled")
	public boolean isBillEnabled;
	
	public int[] categories;
	
	public double distanceFrom() {
		return 0;
	}
}
