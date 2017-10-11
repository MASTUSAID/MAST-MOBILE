package com.rmsi.android.mast.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.domain.Option;

/**
 * @author Amreen.S
 *
 */
//custom adapter
public class CheckBoxAdapter extends ArrayAdapter<String> {
	private LayoutInflater mInflater;

	private int mViewResourceId;
	ArrayList<String> checkedStatus = new ArrayList<String>();
	ArrayList<String> checkedIds=new ArrayList<String>();
	ArrayList<String> slectedTxt=new ArrayList<String>();
	List<Option> optionsList;
	Context ctx;
	String checkedOptionIds="";
	public CheckBoxAdapter(Context ctx, int viewResourceId,
	
	ArrayList<String> list, List<Option> optionsList,ArrayList<String> checkids,ArrayList<String> checktxt,String checkedOptionIds) 
	{
		super(ctx, viewResourceId, list);
		
		this.checkedStatus = list;
		this.checkedIds=checkids;
		this.slectedTxt=checktxt;
		this.optionsList = optionsList;
		this.checkedOptionIds=checkedOptionIds;
		mInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// mStrings = list;
       this.ctx=ctx;
		mViewResourceId = viewResourceId;
	}

	public int getCount() {
		return checkedStatus.size();
	}

	public String getItem(int position) {
		return checkedStatus.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) 
	{
		try{
		if(convertView==null)
				{
		convertView = mInflater.inflate(mViewResourceId, null);
				}
		final CheckBox checkOption = (CheckBox) convertView.findViewById(R.id.checkoption);
		checkOption.setText(checkedStatus.get(position));
			if(checkedOptionIds.contains(optionsList.get(position).getOptionId().toString()))
			{	  
				int index = checkedOptionIds.indexOf(optionsList.get(position).getOptionId().toString());
				if(index>-1)
				{
					checkOption.setChecked(true);
				
				}
			}
			else
			{
				checkOption.setChecked(false);
				
				
			}
		
		}catch(Exception e)
		{
			 Log.e("CheckBoxAdapter",  e.getMessage());
		}

		return convertView;
	}
}
