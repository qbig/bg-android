package sg.com.bigspoon.www.common.Util;

import android.content.Context;

/**
 * Created by qiaoliang89 on 27/1/15.
 */
public class Util {
    public static float dpFromPx(Context context, float px)
    {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float pxFromDp(Context context, float dp)
    {
        return dp * context.getResources().getDisplayMetrics().density;
    }
}
