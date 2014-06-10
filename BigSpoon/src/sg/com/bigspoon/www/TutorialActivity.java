package sg.com.bigspoon.www;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import com.viewpagerindicator.CirclePageIndicator;


public class TutorialActivity extends BaseSampleActivity {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle);

        mAdapter = new TestFragmentAdapter(getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator = indicator;
        mIndicator.setViewPager(mPager);
        
        
        final float density = getResources().getDisplayMetrics().density;
        //indicator.setBackgroundColor(0x#2EFEF7);
        indicator.setRadius(4 * density);
       // indicator.setPageColor(pageColor)
        //indicator.setPageColor(0x#2EFEF7);
        indicator.setFillColor(0xFF888888);
        indicator.setStrokeColor(0x21000000);
        indicator.setStrokeWidth(2 * density);
    }

}
