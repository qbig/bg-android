package sg.com.bigspoon.www.data;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

	@SerializedName("position_index")
	public int positionIndex;

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

	public boolean isDummyDish() {
		return price < 0.01;
	}

	public boolean containsCategoryWithId(int catId) {
		for(int i = 0; i < this.categories.length; i++) {
			if (this.categories[i].id == catId) {
				return true;
			}
		}
		return false;
	}

	public boolean isServedNow() {
		final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");

		Date from;
		Date to;
		try {
			final Date now = new Date();
			from = sdf.parse(startTime);
			from.setDate(now.getDate());
			from.setMonth(now.getMonth());
			from.setYear(now.getYear());
			to = sdf.parse(endTime);
			to.setDate(now.getDate());
			to.setMonth(now.getMonth());
			to.setYear(now.getYear());
			
			return from.before(now) && to.after(now);
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}

}
