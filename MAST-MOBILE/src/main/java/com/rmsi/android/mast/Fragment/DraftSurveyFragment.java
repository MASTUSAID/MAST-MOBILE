package com.rmsi.android.mast.Fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.rmsi.android.mast.activity.BoundaryActivity;
import com.rmsi.android.mast.activity.CapturePareclData;
import com.rmsi.android.mast.activity.CollectedResourceDataSummary;
import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.activity.R.string;
import com.rmsi.android.mast.activity.CaptureDataMapActivity;
import com.rmsi.android.mast.activity.DataSummaryActivity;
import com.rmsi.android.mast.adapter.SurveyListingAdapter;
import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.ClassificationAttribute;
import com.rmsi.android.mast.domain.Feature;
import com.rmsi.android.mast.domain.Option;
import com.rmsi.android.mast.domain.Person;
import com.rmsi.android.mast.domain.Property;
import com.rmsi.android.mast.domain.ResourceCustomAttribute;
import com.rmsi.android.mast.domain.ShareType;
import com.rmsi.android.mast.util.CommonFunctions;

public class DraftSurveyFragment extends Fragment {
    ListView listView;
    SurveyListingAdapter adapter;
    Context context;
    List<Feature> features = new ArrayList<Feature>();
    static boolean listChanged = false;
    String msg;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        context = getActivity();

        try {
            CommonFunctions.getInstance().Initialize(context.getApplicationContext());
        } catch (Exception e) {
        }

        View view = inflater.inflate(R.layout.fragment_survey_review_data_list, container, false);
        listView = (ListView) view.findViewById(android.R.id.list);
        TextView emptyText = (TextView) view.findViewById(android.R.id.empty);
        listView.setEmptyView(emptyText);

        adapter = new SurveyListingAdapter(context, this, features, "draftsurvey");
        listView.setAdapter(adapter);
        return view;
    }

    public void showPopupDraft(View v, Object object) {
        PopupMenu popup = new PopupMenu(context, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.survey_listing_options, popup.getMenu());
        int position = (Integer) object;
        final Long featureId = features.get(position).getId();
        final Long server_featureId = features.get(position).getServerId();
        popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit_spatial:

                        Property property2 = DbController.getInstance(context).getProperty(featureId);
                        if(property2.getFlag().equalsIgnoreCase("R")){
                            Intent intent = new Intent(context, CaptureDataMapActivity.class);
                            intent.putExtra("featureid", featureId);
                            startActivity(intent);
                            return true;

                        } else if (property2.getFlag().equalsIgnoreCase("P")) {
                            Intent intent = new Intent(context, CapturePareclData.class);
                            intent.putExtra("featureid", featureId);
                            startActivity(intent);
                            return true;

                        } else if (property2.getFlag().equalsIgnoreCase("B")) {
                            Intent intent = new Intent(context, CaptureDataMapActivity.class);
                            intent.putExtra("featureid", featureId);
                            intent.putExtra("IsBoundary", true);
                            startActivity(intent);
                            return true;
                        }

                    case R.id.edit_attributes:
                        Property property1 = DbController.getInstance(context).getProperty(featureId);
                        if(property1.getFlag().equalsIgnoreCase("R")){
                            Intent myIntent = new Intent(context, CollectedResourceDataSummary.class);
                            myIntent.putExtra("featureid", featureId);
                            myIntent.putExtra("Server_featureid", server_featureId);
                            myIntent.putExtra("className", "draftSurveyFragment");
                            //myIntent.putExtra("flag",true);
                            startActivity(myIntent);
                            return true;
                        } else if (property1.getFlag().equalsIgnoreCase("P")) {
                            Intent myIntent = new Intent(context, DataSummaryActivity.class);
                            myIntent.putExtra("featureid", featureId);
                            myIntent.putExtra("Server_featureid", server_featureId);
                            myIntent.putExtra("className", "draftSurveyFragment");
                            startActivity(myIntent);
                            return true;
                        } else if (property1.getFlag().equalsIgnoreCase("B")) {
                            Intent myIntent = new Intent(context, BoundaryActivity.class);
                            myIntent.putExtra("featureid", featureId);
                            myIntent.putExtra("editing", true);
                            startActivity(myIntent);
                            return true;
                        }
                        return true;
                    case R.id.delete_entry:
                        deleteEntry(featureId);
                        return true;
                    case R.id.mark_as_complete:
                        int IsNatural=DbController.getInstance(context).getpersonType(featureId);
                        Property property = DbController.getInstance(context).getProperty(featureId);
                        if(property.getFlag().equalsIgnoreCase("R")){
                            if (ValiDateAllValues(featureId)){
                                AllowToComplete(property);
                            }
                            return true;
                        } else if (property.getFlag().equalsIgnoreCase("P")) {
                            if (IsNatural==1|IsNatural==3) {
                                markFeatureAsComplete(featureId);
                            }else {
                                AllowToComplete(property);
                            }
                            return true;
                        } else if (property.getFlag().equalsIgnoreCase("B")) {
                            AllowToComplete(property);
                            return true;
                        }

                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

    private boolean ValiDateAllValues(Long featureId) {
        boolean IsValid=true;

        List<ClassificationAttribute> classificationAttributes=DbController.getInstance(context).checkTenureInfo(featureId);
        if (classificationAttributes.size()==0){
            Toast.makeText(context,"Please fill the capture resource information",Toast.LENGTH_SHORT).show();
            IsValid=false;
        }

        if (classificationAttributes.size()!=0){

                if (classificationAttributes.get(2).getAttribID().equalsIgnoreCase("9") || classificationAttributes.get(2).getAttribID().equalsIgnoreCase("15")){

                    IsValid=true;
                }else {

                    List<Attribute> attributes=DbController.getInstance(context).checkOwnerInfo(featureId);
                    if (attributes.size()==0){

                        Toast.makeText(context,"Please fill Owner information",Toast.LENGTH_SHORT).show();
                        IsValid=false;
                    }
                    List<ResourceCustomAttribute> attributesres = DbController.getInstance(context).getResAttributesByAttrbuteIDNull(classificationAttributes.get(2).getAttribID(),"null");

                    if (attributesres.size()!=0){
                        List<ResourceCustomAttribute> attributeListSize=DbController.getInstance(context).checkCustomAttributesInfo(featureId);
                        if (attributeListSize.size()==0){
                            Toast.makeText(context,"Please fill Custom attributes information",Toast.LENGTH_SHORT).show();
                            IsValid=false;
                        }

                    }else {
                        IsValid=true;
                    }
                }

        }
        return IsValid;
    }

    private void deleteEntry(final Long featureId) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(string.deleteFeatureEntryMsg);
        alertDialogBuilder.setPositiveButton(string.btn_ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        DbController db = DbController.getInstance(context);
                        boolean result = db.deleteFeature(featureId);
                        db.close();
                        if (result) {
                            getActivity().recreate();
                            //refreshList();
                        } else {
                            msg = getResources().getString(string.Error_deleting_feature);
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        }

                    }
                });

        alertDialogBuilder.setNegativeButton(string.btn_cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void markFeatureAsComplete(final Long featureId) {
        Property property = DbController.getInstance(context).getProperty(featureId);
        if(property.validateAll(context, true))
            AllowToComplete(property);
    }

    private void refreshList() {
        DbController db = DbController.getInstance(context);
        features.clear();
        features.addAll(db.fetchDraftFeatures());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        refreshList();
        super.onResume();
    }

    public void AllowToComplete(final Property property) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(string.completedFeatureMsg);

        alertDialogBuilder.setPositiveButton(string.btn_ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        boolean result = DbController.getInstance(context).markFeatureAsComplete(property.getId());
                        if (result) {
                            getActivity().recreate();
                            listChanged = true;
                        } else {
                            msg = getResources().getString(
                                    string.Error_updating_feature);
                            Toast.makeText(context, msg,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        alertDialogBuilder.setNegativeButton(string.btn_cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
