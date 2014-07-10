package sg.com.bigspoon.www.adapters;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.activities.MainActivity;
import sg.com.bigspoon.www.fragments.TutorialScreenFragment;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.viewpagerindicator.IconPagerAdapter;

public class TutorialFragmentAdapter extends FragmentPagerAdapter implements
		IconPagerAdapter {
	
	private Context mContext;
	
	public TutorialFragmentAdapter(Context context, FragmentManager fm) {
		super(fm);
		this.mContext = context;
	}

	@Override
	public Fragment getItem(int position) {
		final int tutScreenLayouts[] = { R.layout.activity_tutorial_screen1,
				R.layout.activity_tutorial_screen2,
				R.layout.activity_tutorial_screen3,
				R.layout.activity_tutorial_screen4,
				R.layout.activity_tutorial_screen5,
				R.layout.activity_tutorial_screen6,
				R.layout.activity_tutorial_screenlast };
		
		final int lastScreenIndex = tutScreenLayouts.length - 1;
		if (position == lastScreenIndex) {
			Intent intent = new Intent(mContext, MainActivity.class);
			mContext.startActivity(intent);
		} 
		
		TutorialScreenFragment fragment = new TutorialScreenFragment();
		fragment.setLayout(tutScreenLayouts[position]);

		return fragment;
	}

	@Override
	public int getCount() {
		return 7;
	}

	@Override
	public int getIconResId(int index) {
		return 0;
	}

	public void setCount(int count) {
		if (count > 0 && count <= 6) {
			notifyDataSetChanged();
		}
	}
}
