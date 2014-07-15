package sg.com.bigspoon.www.data;

import com.google.gson.annotations.SerializedName;

public class TableModel {
	 public int id; 
	 
	 @SerializedName("outlet")
	 public int fromOutlet;
	 
	 public String name;
	 public String code;
	 
	 @SerializedName("is_for_take_away")
	 public boolean isForTakeAway;

}
