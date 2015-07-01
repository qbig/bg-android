package sg.com.bigspoon.www.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by qiaoliang89 on 30/6/15.
 */
public class StoryModel {
    public static class Photo {
        @SerializedName("thumbnail_large")
        public String thumbnailLarge;

        public String original;
        public String thumbnail;
    }

    public  Photo photo;

    @SerializedName("id")
    public int storyId;

    @SerializedName("outlet")
    public int outletID;

    public  String name;

    public  String subtitle;

    public  String url;

//    {
//        "photo": {
//        "thumbnail_large": "/media/restaurant/storys/HorseShoe%20Cafe/None/eat-green.640x400.jpg",
//                "original": "/media/restaurant/storys/HorseShoe%20Cafe/None/eat-green.jpg",
//                "thumbnail": "/media/restaurant/storys/HorseShoe%20Cafe/None/eat-green.320x200.jpg"
//    },
//        "id": 5,
//            "outlet": 1,
//            "name": "Eat Green",
//            "subtitle": "That's our slogan",
//            "url": "http://v.xiumi.us/stage/v3/247C3/1684285"
//    },
}
