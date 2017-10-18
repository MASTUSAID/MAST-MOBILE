package com.rmsi.android.mast.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.Fragment.FinalDataFragment;
import com.rmsi.android.mast.Fragment.VerifiedDataFragment;
import com.rmsi.android.mast.Fragment.VerifyDataFragment;
import com.rmsi.android.mast.tabs.SlidingTabLayout;
import com.rmsi.android.mast.util.CommonFunctions;

public class VerifyDataActivity extends ActionBarActivity 
{
	CommonFunctions cf = CommonFunctions.getInstance();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		//Initializing context in common functions in case of a crash
		try{CommonFunctions.getInstance().Initialize(getApplicationContext());}catch(Exception e){}
		cf.loadLocale(getApplicationContext());

		setContentView(R.layout.activity_review_data);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.verify_data);
		if(toolbar!=null)
			setSupportActionBar(toolbar);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		//TABS
		ViewPager pager = (ViewPager) findViewById(R.id.pager);
		MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
		pager.setAdapter(adapter);

		SlidingTabLayout tabs = (SlidingTabLayout) findViewById(R.id.tabs);
		//tabs.setBackgroundColor(getResources().getColor(R.color.color_primary));
		tabs.setViewPager(pager);
		tabs.setDistributeEvenly(true);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		int id = item.getItemId();
		if(id == android.R.id.home)
		{
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	 public class MyPagerAdapter extends FragmentPagerAdapter 
	 {
		 private String[] titles = getResources().getStringArray(R.array.verifyDataStatus);

		 public MyPagerAdapter(FragmentManager fm) {
			 super(fm);
		 }

		 @Override
		 public CharSequence getPageTitle(int position) {
			 return titles[position];
		 }

		 @Override
		 public int getCount() {
			 return titles.length;
		 }

		 @Override
		 public Fragment getItem(int position) 
		 {
			 switch (position) 
			 {
			 case 0:
				 return new VerifyDataFragment();
			 case 1:
				 return new VerifiedDataFragment();
			 case 2:
				 return new FinalDataFragment();
			 default:
				 Fragment fragment = new VerifyDataFragment();
				 return fragment;
			 }
		 }
	 }

}



