package sg.com.bigspoon.www.fragments;

import sg.com.bigspoon.www.activities.MainActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import sg.com.bigspoon.www.R;

public class TutorialScreenLast extends Fragment {

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_tutorial_screenlast, null);
		 Intent intent = new Intent(this.getActivity(), MainActivity.class);
		  startActivity(intent);
		return v;

	}

}
