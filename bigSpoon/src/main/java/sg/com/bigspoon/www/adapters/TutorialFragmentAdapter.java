package sg.com.bigspoon.www.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.viewpagerindicator.IconPagerAdapter;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.activities.EmailLoginActivity;
import sg.com.bigspoon.www.activities.TutorialActivity;
import sg.com.bigspoon.www.fragments.TutorialScreenFragment;

public class TutorialFragmentAdapter extends FragmentPagerAdapter implements
		IconPagerAdapter {
	
	private Context mContext;
	private final int tutScreenLayouts[] = { 
			R.layout.activity_tutorial_screen1,
			R.layout.activity_tutorial_screen2,
			R.layout.activity_tutorial_screen3,
			R.layout.activity_tutorial_screen6,
			R.layout.activity_tutorial_screenlast };
	
	private final int lastScreenIndex = tutScreenLayouts.length - 1;
	
	public TutorialFragmentAdapter(Context context, FragmentManager fm) {
		super(fm);
		this.mContext = context;
	}

	@Override
	public Fragment getItem(int position) {
		
		if (position == this.lastScreenIndex) {
			Intent intent = new Intent(mContext, EmailLoginActivity.class);
			mContext.startActivity(intent);
			((TutorialActivity) mContext).finish();
		} 
		
		TutorialScreenFragment fragment = new TutorialScreenFragment();
		fragment.setLayout(tutScreenLayouts[position]);
		return fragment;
	}

	@Override
	public int getCount() {
		return tutScreenLayouts.length;
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
