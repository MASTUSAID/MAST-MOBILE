package com.rmsi.android.mast.Fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.db.DBController;
import com.rmsi.android.mast.domain.Feature;
import com.rmsi.android.mast.util.CommonFunctions;

public class VerifiedDataFragment extends Fragment 
{
	Context context;
	ArrayAdapter<String> adapter;
	List <String> featureListStr = new ArrayList<String>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		context = getActivity();
		View view = inflater.inflate(R.layout.fragment_survey_review_data_list, container,false);
		ListView listView = (ListView)view.findViewById(android.R.id.list);
		TextView emptyText = (TextView)view.findViewById(android.R.id.empty);
		listView.setEmptyView(emptyText);		
		
		adapter = new ArrayAdapter<String>(context,R.layout.item_text_view, featureListStr);		
		listView.setAdapter(adapter);		
		return view;		
	}
	
	
	  @Override
	  public void setUserVisibleHint(boolean isVisibleToUser) 
	  {
	    super.setUserVisibleHint(isVisibleToUser);
	    
	    if (isVisibleToUser && VerifyDataFragment.listChanged)
		{
			refreshList();
			Toast.makeText(context, R.string.refreshing_msg, Toast.LENGTH_SHORT).show();
			VerifyDataFragment.listChanged = false;
		}
	  }
	
	private void refreshList()
	{
		DBController db = new DBController(context);
		List<Feature> features = db.fetchVerifiedFeatures();
		db.close();
		featureListStr.clear();	
		String geomtype="";
		for (int i = 0; i < features.size(); i++) 
		{
			if(features.get(i).getGeomtype().equals(CommonFunctions.GEOM_POINT))
			{
				geomtype = context.getResources().getString(R.string.point_txt);
			}
			else if(features.get(i).getGeomtype().equals(CommonFunctions.GEOM_LINE))
			{
				geomtype = context.getResources().getString(R.string.line_txt);
			}
			else if(features.get(i).getGeomtype().equals(CommonFunctions.GEOM_POLYGON))
			{
				geomtype = context.getResources().getString(R.string.polygon_txt);
			}
			featureListStr.add(geomtype+" "+features.get(i).getFeatureid());				
		} 
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onResume() 
	{
		refreshList();
		super.onResume();
	}

}
