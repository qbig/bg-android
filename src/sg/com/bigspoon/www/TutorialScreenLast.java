package sg.com.bigspoon.www;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

public class TutorialScreenLast extends Fragment {

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_tutorial_screenlast, null);
		 Intent intent = new Intent(this.getActivity(), MainActivity.class);
		  startActivity(intent);
		return v;

	}

}
