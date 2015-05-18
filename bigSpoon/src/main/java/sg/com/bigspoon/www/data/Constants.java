package sg.com.bigspoon.www.data;

public class Constants {
	   public static final String PREFS_NAME = "MyPrefsFile";
	   
	   public static final boolean LOG = false;

	    public static final String BASE_URL =  "http://54.251.209.132/";
	    public static final String USER_SIGNUP =  "http://54.251.209.132/api/v1/user";
	    public static final String USER_LOGIN =  "http://54.251.209.132/api/v1/login";
	    public static final String USER_LOGIN_WITH_FB =  "http://54.251.209.132/api/v1/fblogin";
	    public static final String LIST_OUTLETS =  "http://54.251.209.132/api/v1/outlets";
	    public static final String REQUEST_URL =  "http://54.251.209.132/api/v1/request";
	    public static final String PROFILE_URL =  "http://54.251.209.132/api/v1/profile";
	    public static final String ORDER_URL =  "http://54.251.209.132/api/v1/meal";
        public static final String CLEAR_BILL_URL =  "http://54.251.209.132/api/v1/clearbill";
	    public static final String BILL_URL =  "http://54.251.209.132/api/v1/askbill";
	    public static final String RATING_URL =  "http://54.251.209.132/api/v1/rating";
	    public static final String FEEDBACK_URL =  "http://54.251.209.132/api/v1/review";
	    public static final String DISH_CATEGORY_URL =  "http://54.251.209.132/api/v1/categories";
	    public static final String ORDER_HISTORY_URL =  "http://54.251.209.132/api/v1/mealhistory";
	    public static final String SOCKET_URL =  "54.251.209.132";


//	    public static final String BASE_URL =  "http://bigspoon.biz/"; // public static final String BASE_URL =  "http://54.255.0.38/";
//	    public static final String USER_SIGNUP =  "http://bigspoon.biz/api/v1/user";
//	    public static final String USER_LOGIN =  "http://bigspoon.biz/api/v1/login";
//	    public static final String USER_LOGIN_WITH_FB =  "http://bigspoon.biz/api/v1/fblogin";
//	    public static final String LIST_OUTLETS =  "http://bigspoon.biz/api/v1/outlets";
//	    public static final String REQUEST_URL =  "http://bigspoon.biz/api/v1/request";
//	    public static final String PROFILE_URL =  "http://bigspoon.biz/api/v1/profile";
//	    public static final String ORDER_URL =  "http://bigspoon.biz/api/v1/meal";
//        public static final String CLEAR_BILL_URL =  "http://bigspoon.biz/api/v1/clearbill";
//	    public static final String BILL_URL =  "http://bigspoon.biz/api/v1/askbill";
//	    public static final String RATING_URL =  "http://bigspoon.biz/api/v1/rating";
//	    public static final String FEEDBACK_URL =  "http://bigspoon.biz/api/v1/review";
//	    public static final String DISH_CATEGORY_URL =  "http://bigspoon.biz/api/v1/categories";
//	    public static final String ORDER_HISTORY_URL =  "http://bigspoon.biz/api/v1/mealhistory";
//	    public static final String SOCKET_URL =  "bigspoon.biz";

	    public static final int PORT = 8000;
	    public static final String LOGIN_INFO_EMAIL = "email";
	    public static final String LOGIN_INFO_LAST_NAME = "last_name";
	    public static final String LOGIN_INFO_FIRST_NAME = "first_name";
	    public static final String LOGIN_INFO_AUTHTOKEN = "auth_token";
	    public static final String LOGIN_INFO_AVATAR_URL = "avatar_url";
	    
	    //TYPE
	    public static final String MODIFIER_SECTION_TYPE_RADIO = "radio";
	    public static final String MODIFIER_SECTION_TYPE_COUNTER = "count";
	    
	    //KEY
	    public static final String OUTLET_ID = "outletID";
		public static final String OUTLET_NAME = "outletName";
		public static final String IMAGE_ID = "imageId";
		public static final String SHOULD_SHOW_STEPS_REMINDER = "shouldShowSteps";
	    public static final String OUTLET_ICON = "outletICon";
	    public static final String POS_FOR_CLICKED_CATEGORY = "startposition";
	    public static final String SELECTED_HISTORY_ITEM_POSITION = "selectedHisotryItemPosition";
	    public static final String SOCKET_IO_TOKEN_BILL_CLOSED = "bill has been closed";
	    public static final String TUTORIAL_SET = "Tutorial Set";
	    public static final String MODIFIER_POPUP_DISH_ID = "ModifierDishId";
	    
	    //VALUE 
	    public static final int DESSERT_CATEGORY_ID = 4;  
	    
	    //BROADCAST MESSAGE KEY
	    public static final String NOTIF_NEW_DISH_INFO_RETRIEVED = "RetrievedNewDishesAndTableInfo";
	    public static final String NOTIF_NEW_DISH_INFO_FAILED = "DishAndTableRequestNetworkFailure";
	    public static final String NOTIF_FB_LOGIN_FAILED = "fbLoginFailed";
	    public static final String NOTIF_ORDER_UPDATE = "OrderUpdated";
	    public static final String NOTIF_SHOULD_ASK_LOCATION_PERMIT_NOT = "ShouldAskLocationPermitNow";
	    public static final String SHOW_NOTE = "showNote";
	    public static final String HIDE_NOTE = "hideNote";
	    public static final String NOTIF_LOCATION_UPDATED = "location-broadcast";
	    public static final String NOTIF_LOCATION_KEY = "location-extra";
	    public static final String NOTIF_MODIFIER_OK = "modifierSelectOk";
		public static final String NOTIF_UNDO_ORDER = "undoOrder";
		public static final String NOTIF_ITEM_REMOVE_CLICK = "removeClick";
	    public static final String NOTIF_TO_START_LOCATION_SERVICE = "start-location";
	    
	    // REQUEST CODE
	    public static final int MODIFIER_POPUP_REQUEST = 999;
	    
	    //TOKEN 
	    public static final String MIXPANEL_TOKEN = "2e7017c4be6254ba2e5fae51651f476d";
	    
	    // OUTLET LOCATION FILTER
	    public static final double OUTLET_LOCATION_FILTER_DISTANCE = 100000;

        // Table number
        public static final String TABLE_ID = "tableId";

}
