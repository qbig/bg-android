package sg.com.bigspoon.www;

import com.viewpagerindicator.CirclePageIndicator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TutorialScreenLast extends Fragment{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.activity_tutorial_screenlast, null);
	    //ImageView myView = (ImageView) v.findViewById(R.id.imageView);
		Intent intent = new Intent(this.getActivity(), MainActivity.class);
		startActivity(intent);
		
		return v;

	}


}
