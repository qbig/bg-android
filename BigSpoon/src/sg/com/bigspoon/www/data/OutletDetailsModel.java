package sg.com.bigspoon.www.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import sg.com.bigspoon.www.data.CategoryModel;
import sg.com.bigspoon.www.data.CategoryOrder;
import sg.com.bigspoon.www.data.DishModel;
import sg.com.bigspoon.www.data.TableModel;

public class OutletDetailsModel {
	public DishModel[] dishes;
	public CategoryModel[] categoriesDetails;
	public CategoryOrder[] categoriesOrder;
	public TableModel[] tables;
	
	public int restaurant;
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
	
	public static OutletDetailsModel getInstanceFromJsonObject(JsonObject json){
		 final Gson gson = new Gson();
         final OutletDetailsModel outletDetails = (OutletDetailsModel) gson.fromJson(json, OutletDetailsModel.class);
         outletDetails.dishes = gson.fromJson(json.get("dishes"), DishModel[].class);
         outletDetails.tables = gson.fromJson(json.get("tables"), TableModel[].class);
         outletDetails.categoriesDetails = gson.fromJson(json.get("categories"), CategoryModel[].class);
         outletDetails.categoriesOrder = gson.fromJson(json.get("categories_order"), CategoryOrder[].class);
         
         return outletDetails;
	}
	
}
