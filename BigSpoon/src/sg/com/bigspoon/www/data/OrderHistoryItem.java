package sg.com.bigspoon.www.data;

import com.google.gson.annotations.SerializedName;

public class OrderHistoryItem {
	public static class HistoryOutlet {
		public int id;
		public String name;
	}
	
	public static class HistoryDish {
		public String price;
		public String pos;
		public int id;
		public String name;
	}
	
	public static class HistoryOrder { 
		public int quantity;
		public HistoryDish dish;
		public int id;
		
		@SerializedName("is_finished")
		public boolean isFinished;
		public String note;
		
		@SerializedName("modifier_json")
		public String modifierJsonString;
	}
	
	public HistoryOutlet outlet;
	
	@SerializedName("order_time")
	public String orderTime;
	
	public String note;
	
	public HistoryOrder[] orders;
}
//{
//    "outlet": {
//        "id": 1,
//        "name": "Testinbg!"
//    },
//    "order_time": "2014/03/12 (128 days ago)",
//    "note": "",
//    "orders": [
//        {
//            "quantity": 1,
//            "dish": {
//                "price": "8.00",
//                "pos": "31",
//                "id": 140,
//                "name": "Nacho Cheese Fries"
//            },
//            "id": 1668,
//            "is_finished": true,
//            "note": "",
//            "modifier_json": null
//        },
//        {
//            "quantity": 1,
//            "dish": {
//                "price": "8.00",
//                "pos": "32",
//                "id": 141,
//                "name": "Bangers and Mash"
//            },
//            "id": 1669,
//            "is_finished": true,
//            "note": "",
//            "modifier_json": null
//        },
//        {
//            "quantity": 1,
//            "dish": {
//                "price": "3.00",
//                "pos": "51",
//                "id": 151,
//                "name": "Espresso"
//            },
//            "id": 1670,
//            "is_finished": true,
//            "note": "",
//            "modifier_json": null
//        },
//        {
//            "quantity": 1,
//            "dish": {
//                "price": "3.00",
//                "pos": "52",
//                "id": 8,
//                "name": "Babycinno (Warm Frothed Milk)"
//            },
//            "id": 1671,
//            "is_finished": true,
//            "note": "",
//            "modifier_json": null
//        }
//    ]
//},