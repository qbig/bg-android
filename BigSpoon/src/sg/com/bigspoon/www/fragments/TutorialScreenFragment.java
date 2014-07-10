
package sg.com.bigspoon.www.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TutorialScreenFragment extends Fragment {
	private int layout;
	
	public void setLayout (int layout) {
		this.layout = layout;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(layout, null);
	}

}
