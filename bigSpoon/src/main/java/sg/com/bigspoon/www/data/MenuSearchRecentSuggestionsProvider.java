package sg.com.bigspoon.www.data;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by qiaoliang89 on 12/3/15.
 */

public class MenuSearchRecentSuggestionsProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "sg.com.bigspoon.www.data.MenuSearchRecentSuggestionsProvider";
    public final static int MODE = DATABASE_MODE_QUERIES | DATABASE_MODE_2LINES;

    public MenuSearchRecentSuggestionsProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}