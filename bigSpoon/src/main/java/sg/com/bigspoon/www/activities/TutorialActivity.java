package sg.com.bigspoon.www.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.adapters.TutorialFragmentAdapter;

public class TutorialActivity extends FragmentActivity {

	TutorialFragmentAdapter mAdapter;
	ViewPager mPager;
	PageIndicator mIndicator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_circle);
		mAdapter = new TutorialFragmentAdapter(this, getSupportFragmentManager());

		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);

		final CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
		mIndicator = indicator;
		mIndicator.setViewPager(mPager);

		final float density = getResources().getDisplayMetrics().density;
		indicator.setRadius(4 * density);
		indicator.setStrokeWidth(2 * density);
	}

}
