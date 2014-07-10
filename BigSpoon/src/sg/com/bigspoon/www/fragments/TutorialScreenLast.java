package sg.com.bigspoon.www.fragments;

import sg.com.bigspoon.www.activities.MainActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TutorialScreenLast extends TutorialScreenFragment {

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v = super.onCreateView(inflater, container, savedInstanceState);
		Intent intent = new Intent(this.getActivity(), MainActivity.class);
		startActivity(intent);
		return v;
	}
}
