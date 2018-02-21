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

import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.Fragment.DraftSurveyFragment;
import com.rmsi.android.mast.Fragment.VerifyDataFragment;
import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.domain.Feature;
import com.rmsi.android.mast.domain.Property;
import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.StringUtility;

public class SurveyListingAdapter extends BaseAdapter {
    private List<Feature> myObjs;
    private LayoutInflater lInflator;
    String classname;
    Context context;
    Object fragmentObj;
    String claimStr = "";

    public SurveyListingAdapter(Context context, Object fragmentObj, List<Feature> features, String classname) {
        this.context = context;
        this.myObjs = features;
        this.lInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.classname = classname;
        this.fragmentObj = fragmentObj;
        claimStr = context.getResources().getString(R.string.ParcelClaim);
    }

    public int getCount() {
        return myObjs.size();
    }

    public Feature getItem(int position) {
        return myObjs.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem viewHolder;
        Feature feat = myObjs.get(position);
        if (convertView == null) {
            convertView = lInflator.inflate(R.layout.item_list_row, parent, false);

            viewHolder = new ViewHolderItem();
            viewHolder.textViewItem = (TextView) convertView.findViewById(R.id.surveyData);
            ImageButton options = (ImageButton) convertView.findViewById(R.id.optionsButton);
            viewHolder.options = options;
            if(classname.equalsIgnoreCase("final")){
                options.setVisibility(View.GONE);
            }

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        Property property = DbController.getInstance(context).getProperty(feat.getId());
//        if(property.getFlag().equalsIgnoreCase("R")){
//            if (!StringUtility.isEmpty(feat.getPolygonNumber()))
//                viewHolder.textViewItem.setText("Resource "+" " + property.getiIndex());
//            else
//                viewHolder.textViewItem.setText("Resource "+" " + property.getiIndex());
//
//        }else if (property.getFlag().equalsIgnoreCase("P")) {
//            if (!StringUtility.isEmpty(feat.getPolygonNumber()))
//                viewHolder.textViewItem.setText(claimStr + " " +  property.getiIndex());
//            else
//                viewHolder.textViewItem.setText(claimStr + " " +  property.getiIndex());
//        }

        if(property.getFlag().equalsIgnoreCase("R")){
            if (!StringUtility.isEmpty(feat.getPolygonNumber()))
                viewHolder.textViewItem.setText("Resource "+" " + property.getIpNumber());
            else
                viewHolder.textViewItem.setText("Resource "+" " +  property.getIpNumber());

        }else if (property.getFlag().equalsIgnoreCase("P")) {
            if (!StringUtility.isEmpty(feat.getPolygonNumber()))
                viewHolder.textViewItem.setText(claimStr + " " +  property.getIpNumber());
            else
                viewHolder.textViewItem.setText(claimStr + " " +  property.getIpNumber());
        }


        viewHolder.options.setTag(position);

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

    private static class ViewHolderItem {
        TextView textViewItem;
        ImageButton options;
    }

}
