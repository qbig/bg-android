package sg.com.bigspoon.www.fragments;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.adapters.MenuTextListCustomeAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

public final class MenuTextFragment extends Fragment {
    private static final String KEY_CONTENT = "TestFragment:Content";
    String[] itemnames = { "Bratwruts Ball", "Mushroom Melt", "Breakfast Butter", "Avacado Eggs",
			"Bread", "Roasted Chicken"};
    String[] itemdesc = { "refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash ", 
    		"refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash ",
    		"refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash ",
    		"refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash ",
    		"refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash ",
    		"refined beet sugar,resin ,rice, syrup ,rosin ,rutin ,Sucralose ,saccharin ,soda, ash "};
    String[] itemprice = { "8.0", "8.0", "8.0", "8.0","8.0", "8.0"};

    public static MenuTextFragment newInstance(String content) {
        MenuTextFragment fragment = new MenuTextFragment();

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            builder.append(content).append(" ");
        }
        builder.deleteCharAt(builder.length() - 1);
        fragment.mContent = builder.toString();

        return fragment;
    }

    private String mContent = "???";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ListView list = new ListView(getActivity());
        
        ViewGroup footer = (ViewGroup) inflater.inflate(R.layout.footer, list,false);
        list.addFooterView(footer, null, false);
        
        MenuTextListCustomeAdapter menuTextListCustomeAdapter = new MenuTextListCustomeAdapter(
        		getActivity(),itemnames,itemdesc,itemprice);
        list.setAdapter(menuTextListCustomeAdapter);
//        MenuPhotoListCustomAdapter menuPhotoListCustomAdapter = new MenuPhotoListCustomAdapter(
//        		getActivity(), categories, images);
//        list.setAdapter(menuPhotoListCustomAdapter);
		//catrgoriesList.setOnItemClickListener(this);

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        layout.setGravity(Gravity.CENTER);
        layout.addView(list);

        return layout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }
}
