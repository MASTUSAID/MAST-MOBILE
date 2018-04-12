package com.rmsi.android.mast.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.rmsi.android.mast.Fragment.CompletedSurveyFragment;
import com.rmsi.android.mast.Fragment.DraftSurveyFragment;
import com.rmsi.android.mast.Fragment.RejectedSurveyFragment;
import com.rmsi.android.mast.Fragment.SyncedSurveyFragment;
import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.tabs.SlidingTabLayout;
import com.rmsi.android.mast.util.CommonFunctions;

public class ReviewDataActivity extends ActionBarActivity 
{

	CommonFunctions cf = CommonFunctions.getInstance();
	Context context=this;
	 String STATUS_DRAFT ="draft";
	 String STATUS_COMPLETE ="complete";
	 String STATUS_SYNCED ="synced";
	 String STATUS_REJECTED ="rejected";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		//Initializing context in common functions in case of a crash
		try{CommonFunctions.getInstance().Initialize(getApplicationContext());}catch(Exception e){}
		cf.loadLocale(getApplicationContext());

		setContentView(R.layout.activity_review_data);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.reviewdata);
		if(toolbar!=null)
			setSupportActionBar(toolbar);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		//TABS
		ViewPager pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

		SlidingTabLayout tabs = (SlidingTabLayout) findViewById(R.id.tabs);
		//tabs.setBackgroundColor(getResources().getColor(R.color.color_primary));
		tabs.setViewPager(pager);
		tabs.setDistributeEvenly(true);
		
		//tabs.setIndicatorColorResource(R.color.white);
		//tabs.setDividerColorResource(R.color.white);
		//tabs.setTextColorResource(R.color.white);
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
		
		 private String[] titles = getResources().getStringArray(R.array.reviewDataStatus);
		

		 public MyPagerAdapter(FragmentManager fm) {
			 super(fm);
		 }

		 @Override
		 public CharSequence getPageTitle(int position) {
			 int count = 0;
			 DbController db = DbController.getInstance(context);
			 
			 if(position==0)
			 {				
			  count=db.getCount(STATUS_DRAFT);
			 }
			 else if(position==1)
			 { count=db.getCount(STATUS_COMPLETE);
			 }
			 else if(position==2)
			 {
				 count=db.getCount(STATUS_SYNCED);
			 }
			 else if(position==3)
			 {
				 count=db.getCount(STATUS_REJECTED);
			 }
			 String strCount= Integer.toString(count);			
			 return titles[position]+" ("+strCount+") ";
			 //return titles[position];
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
				 return new DraftSurveyFragment();
			 case 1:
				 return new CompletedSurveyFragment();
			 case 2:
				 return new SyncedSurveyFragment();
			 case 3:
				 return new RejectedSurveyFragment();
			 default:
				 Fragment fragment = new DraftSurveyFragment();
				 return fragment;
			 }
		 }
	 }

}



