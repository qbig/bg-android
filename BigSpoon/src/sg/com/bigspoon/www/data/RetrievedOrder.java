package sg.com.bigspoon.www.data;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.annotations.SerializedName;

public class RetrievedOrder {
	public static class RetrievedOutlet {
		public int id;
		public String name;
	}
	
	public RetrievedOutlet outlet;
	
	@SerializedName("order_time")
	public String orderTime;
	
	public String note;
	
	public OrderItem[] orders;
	
	public Order toOrder() {
		final Order result = new Order();
		result.mGeneralNote = note;
		result.mItems = new ArrayList<OrderItem>(Arrays.asList(orders));
		return result;
	}
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