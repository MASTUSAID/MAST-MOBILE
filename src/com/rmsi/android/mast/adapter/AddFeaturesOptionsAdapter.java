package com.rmsi.android.mast.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rmsi.android.mast.activity.R;


public class AddFeaturesOptionsAdapter extends BaseExpandableListAdapter 
{
	private Context context;
	private List<Object> childtems;
	private LayoutInflater inflater;
	private List<String> parentItems, child;


	public AddFeaturesOptionsAdapter(List<String> parents, List<Object> childern, Context context) 
	{
		this.parentItems = parents;
		this.childtems = childern;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context =  context;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) 
	{
		child = (List<String>) childtems.get(groupPosition);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_text_view, null);
		}
		TextView textView = (TextView) convertView;
		textView.setText(child.get(childPosition).substring(3));

		return convertView;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) 
	{
		GroupHolder groupHolder;

		if (convertView == null) 
		{
			convertView = inflater.inflate(R.layout.item_expandable_group, null);
			groupHolder = new GroupHolder();
			groupHolder.img = (ImageView) convertView.findViewById(R.id.tag_img);
			groupHolder.title = (TextView) convertView.findViewById(R.id.group_title);
			convertView.setTag(groupHolder);
		}
		else 
		{
			groupHolder = (GroupHolder) convertView.getTag();
		}

		int imageResourceId = isExpanded ? R.drawable.action_collapse : R.drawable.action_expand;
		groupHolder.img.setImageResource(imageResourceId);
		groupHolder.title.setText(parentItems.get(groupPosition));
		
		return convertView;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return ((List<String>) childtems.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return null;
	}

	@Override
	public int getGroupCount() {
		return parentItems.size();
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
	}

	@Override
	public void onGroupExpanded(int groupPosition) 
	{
		super.onGroupExpanded(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}



	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	class GroupHolder {
		ImageView img;
		TextView title;
	}

}
