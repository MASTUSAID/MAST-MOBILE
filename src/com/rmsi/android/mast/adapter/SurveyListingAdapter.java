package com.rmsi.android.mast.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rmsi.android.mast.Fragment.DraftSurveyFragment;
import com.rmsi.android.mast.Fragment.VerifyDataFragment;
import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.domain.Feature;
import com.rmsi.android.mast.util.CommonFunctions;

//custom adapter
public class SurveyListingAdapter extends BaseAdapter {
	private static class ViewHolderItem {
		TextView textViewItem;
		ImageButton options;
	}

	private List<Feature> myObjs;
	private LayoutInflater lInflator;
	String classname;
	Context context;

	Object fragmentObj;

	public SurveyListingAdapter(Context context, Object fragmentObj,
			List<Feature> features, String classname) {
		this.context = context;
		this.myObjs = features;
		this.lInflator = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.classname = classname;
		this.fragmentObj = fragmentObj;
	}

	@Override
	public int getCount() {
		return myObjs.size();
	}

	@Override
	public Feature getItem(int position) {
		return myObjs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolderItem viewHolder;
		String geomtype = "";
		Feature feat = myObjs.get(position);
		if (convertView == null) {
			convertView = lInflator.inflate(R.layout.item_list_row, parent,
					false);

			viewHolder = new ViewHolderItem();
			viewHolder.textViewItem = (TextView) convertView
					.findViewById(R.id.surveyData);
			viewHolder.options = (ImageButton) convertView
					.findViewById(R.id.optionsButton);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolderItem) convertView.getTag();
		}

		if (feat.getGeomtype().equals(CommonFunctions.GEOM_POINT)) {
			geomtype = context.getResources().getString(R.string.point_txt);
		} else if (feat.getGeomtype().equals(CommonFunctions.GEOM_LINE)) {
			geomtype = context.getResources().getString(R.string.line_txt);
		} else if (feat.getGeomtype().equals(CommonFunctions.GEOM_POLYGON)) {
			geomtype = context.getResources().getString(R.string.polygon_txt);
		}

		viewHolder.textViewItem.setText(geomtype + " " + feat.getFeatureid());

		viewHolder.options.setTag(position);
		// viewHolder.options.setFocusable(false);

		viewHolder.options.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (classname.equalsIgnoreCase("draftsurvey")) {
					DraftSurveyFragment obj = (DraftSurveyFragment) fragmentObj;
					obj.showPopupDraft(v, v.getTag());
				}

				if (classname.equalsIgnoreCase("verifyData")) {
					VerifyDataFragment obj = (VerifyDataFragment) fragmentObj;
					obj.showPopupVerify(v, v.getTag());
				}
			}
		});

		return convertView;
	}

}
