package sg.com.bigspoon.www;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.viewpagerindicator.IconPagerAdapter;

class TestFragmentAdapter extends FragmentPagerAdapter implements IconPagerAdapter {

    public TestFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) 
    {
        // TODO Auto-generated method stub
        Fragment fragment = null;
        switch(position){
        case 0:
            fragment = new TutorialScreen1();
            break;
        case 1:
            fragment = new TutorialScreen2();
            break;
        case 2:
            fragment = new TutorialScreen3();
            break;
    	case 3:
    		fragment = new TutorialScreen4();
    		break;
    	case 4:
    		fragment = new TutorialScreen5();
    		break;
    	case 5:
    		fragment = new TutorialScreen6();
    		break;	
    	case 6:
    		fragment = new TutorialScreenLast();
    		break;
		}
        return fragment;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return 7;
    }

    @Override
    public int getIconResId(int index) {
    	return 0;
//      return ICONS[index % ICONS.length];
    }

    public void setCount(int count) {
        if (count > 0 && count <= 6) {
           // int mCount = count;
            notifyDataSetChanged();
        }
    }
}