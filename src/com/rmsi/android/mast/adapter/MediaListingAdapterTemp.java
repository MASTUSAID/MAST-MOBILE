package com.rmsi.android.mast.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.rmsi.android.mast.activity.NonNaturalPersonListActivity;
import com.rmsi.android.mast.activity.PersonListWithDPActivity;
import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.activity.MediaListActivity;
import com.rmsi.android.mast.activity.PersonListActivity;
import com.rmsi.android.mast.activity.SocialTenureListActivity;
import com.rmsi.android.mast.db.DBController;
import com.rmsi.android.mast.domain.Attribute;


//custom adapter
public class MediaListingAdapterTemp extends BaseAdapter
{
	private List<Attribute> myObjs;
	private LayoutInflater lInflator;
	String classname;
	Context context;
	Object activityObj;
	 
	
	public MediaListingAdapterTemp(Context context, Object activityObj, List<Attribute> attribute,String classname) 
	{
		this.context = context;
		this.myObjs = attribute;
		this.lInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.classname = classname;
		this.activityObj = activityObj;
	}

	public int getCount(){
		return myObjs.size();
	}

	public Attribute getItem(int position)
	{
		return myObjs.get(position);
	}

	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolderItem viewHolder;
		Attribute attrib = getItem(position);
		
		
		if(convertView==null)
		{
			if(classname.equalsIgnoreCase("personlist") || classname.equalsIgnoreCase("PersonListWithDP") || classname.equalsIgnoreCase("NonNaturalPersonlist"))
			{
				convertView = lInflator.inflate(R.layout.item_list_row_with_attachment,parent,false);
			}
			else{
			convertView = lInflator.inflate(R.layout.item_list_row,parent,false);
			}

			viewHolder = new ViewHolderItem();
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ViewHolderItem) convertView.getTag();		   
		}
		viewHolder.textViewItem = (TextView) convertView.findViewById(R.id.surveyData);
		viewHolder.options = (ImageButton) convertView.findViewById(R.id.optionsButton);		
		viewHolder.textViewItem.setText(attrib.getFieldValue());
		viewHolder.options.setTag(position);
		//viewHolder.options.setFocusable(false);
				
		
		
		if(classname.equalsIgnoreCase("personlist") || classname.equalsIgnoreCase("PersonListWithDP") || classname.equalsIgnoreCase("NonNaturalPersonlist"))
		{
			String personSubType=attrib.getPersonSubType();
			long featureId=attrib.getFEATURE_ID();
			int groupId=attrib.getGroupId();
			viewHolder.imgViewItem = (ImageView) convertView.findViewById(R.id.img_attachment);	
			viewHolder.imgViewItem.setVisibility(View.INVISIBLE);
				DBController db = new DBController(context);
			   boolean imgExist=db.isOwnerOrGuardianImageExist(featureId, groupId, personSubType);
			   if(imgExist)
			   {
				viewHolder.imgViewItem.setVisibility(View.VISIBLE);
		     	}
			   else{
				  viewHolder.imgViewItem.setVisibility(View.INVISIBLE);

			   }
		
		}
		
		viewHolder.options.setOnClickListener(new OnClickListener() 
		{			
			@Override
			public void onClick(View v) 
			{
				if(classname.equalsIgnoreCase("personlist"))
				{
					PersonListActivity obj = (PersonListActivity) activityObj;
					obj.showPopup(v, v.getTag());
				}
				if(classname.equalsIgnoreCase("NonNaturalPersonlist"))
				{
					NonNaturalPersonListActivity obj = (NonNaturalPersonListActivity) activityObj;
					obj.showPopup(v, v.getTag());
				}
				
				else if(classname.equalsIgnoreCase("socialTenurelist"))
				{
					SocialTenureListActivity obj = (SocialTenureListActivity) activityObj;
					obj.showPopup(v, v.getTag());
				}
				else if(classname.equalsIgnoreCase("medialist"))
				{
					MediaListActivity obj = (MediaListActivity) activityObj;
					obj.showPopup(v, v.getTag());
				}
				else if(classname.equalsIgnoreCase("PersonListWithDP"))
				{
					PersonListWithDPActivity obj = (PersonListWithDPActivity) activityObj;
					obj.showPopup(v, v.getTag());
				}
			}
		});

		return convertView;
	}

	private static class ViewHolderItem 
	{
		TextView textViewItem;
		ImageButton options;
		ImageView imgViewItem;
	}

}
