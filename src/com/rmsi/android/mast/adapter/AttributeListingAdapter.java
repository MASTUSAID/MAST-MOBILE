package com.rmsi.android.mast.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rmsi.android.mast.activity.MediaListActivity;
import com.rmsi.android.mast.activity.PersonListActivity;
import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.activity.SocialTenureListActivity;
import com.rmsi.android.mast.domain.Attribute;

//custom adapter
public class AttributeListingAdapter extends BaseAdapter {
	private static class ViewHolderItem {
		TextView textViewItem;
		ImageButton options;
	}

	private List<Attribute> myObjs;
	private LayoutInflater lInflator;
	String classname;
	Context context;

	Object activityObj;

	public AttributeListingAdapter(Context context, Object activityObj,
			List<Attribute> attribute, String classname) {
		this.context = context;
		this.myObjs = attribute;
		this.lInflator = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.classname = classname;
		this.activityObj = activityObj;
	}

	@Override
	public int getCount() {
		return myObjs.size() / 2;
	}

	@Override
	public Attribute getItem(int position) {
		return myObjs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolderItem viewHolder;
		Attribute attrib = getItem((position * 2));
		Attribute attrib2 = getItem((position * 2) + 1);
		String value1, value2;
		if (TextUtils.isEmpty(attrib2.getFieldValue()))
			value2 = "";
		else
			value2 = attrib2.getFieldValue();

		if (TextUtils.isEmpty(attrib.getFieldValue()))
			value1 = "";
		else
			value1 = attrib.getFieldValue();

		if (convertView == null) {
			convertView = lInflator.inflate(R.layout.item_list_row, parent,
					false);

			viewHolder = new ViewHolderItem();
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolderItem) convertView.getTag();
		}

		viewHolder.textViewItem = (TextView) convertView
				.findViewById(R.id.surveyData);
		viewHolder.options = (ImageButton) convertView
				.findViewById(R.id.optionsButton);
		viewHolder.textViewItem.setText(value1 + " " + value2);

		viewHolder.options.setTag(position);
		// viewHolder.options.setFocusable(false);

		viewHolder.options.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (classname.equalsIgnoreCase("personlist")) {
					PersonListActivity obj = (PersonListActivity) activityObj;
					obj.showPopup(v, v.getTag());
				}

				else if (classname.equalsIgnoreCase("socialTenurelist")) {
					SocialTenureListActivity obj = (SocialTenureListActivity) activityObj;
					obj.showPopup(v, v.getTag());
				} else if (classname.equalsIgnoreCase("medialist")) {
					MediaListActivity obj = (MediaListActivity) activityObj;
					obj.showPopup(v, v.getTag());
				}
			}
		});

		return convertView;
	}

}
