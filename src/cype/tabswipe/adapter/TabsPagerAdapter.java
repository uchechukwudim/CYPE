package cype.tabswipe.adapter;


import java.util.HashMap;
import java.util.Map;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;



public class TabsPagerAdapter extends FragmentStatePagerAdapter{

	Map<Integer, Fragment> cpaper;
	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
		clubsFragment clubF = new clubsFragment();
		cPeoplFragment cpeoplF = new cPeoplFragment();
		historyFragment historyF = new historyFragment();
		cpaper = new HashMap<Integer, Fragment>();
		
	}

	
	@Override
	public Fragment getItem(int index) {

			return cpaper.get(index);
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 3;
	}

}
