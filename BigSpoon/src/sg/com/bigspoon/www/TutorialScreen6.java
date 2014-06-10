package sg.com.bigspoon.www;

import com.viewpagerindicator.CirclePageIndicator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TutorialScreen6 extends Fragment {

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_tutorial_screen6, null);
		return v;
	}
	public void setUserVisibleHint(boolean isVisibleToUser) {
    // TODO Auto-generated method stub
          super.setUserVisibleHint(isVisibleToUser);
          if (isVisibleToUser == true) {
      		//CirclePageIndicator indi = (CirclePageIndicator) this.getActivity().findViewById(R.id.indicator);
      		//indi.setVisibility(View.GONE);
          }
          else if (isVisibleToUser == false) { 
           }
	}
}
