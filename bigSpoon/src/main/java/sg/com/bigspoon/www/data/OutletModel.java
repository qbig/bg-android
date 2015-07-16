package sg.com.bigspoon.www.data;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static sg.com.bigspoon.www.data.Constants.PERSIST_OUTLET_LIST;

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

	public static void persistList(Context context, List<OutletModel> lists) {
		Gson gson = new Gson();
		User.getInstance(context).persistPrefStringWithKey(gson.toJsonTree(lists).toString(), PERSIST_OUTLET_LIST);
	}

	public static List<OutletModel> getPersistedList(Context context) {
		String persistString = User.getInstance(context).getPrefStringWithKey(PERSIST_OUTLET_LIST);
		if (StringUtils.isNoneEmpty(persistString)) {
			Gson gson = new Gson();
			return gson.fromJson(persistString, new TypeToken<List<OutletModel>>() {}.getType());
		}
		return null;
	}
}
