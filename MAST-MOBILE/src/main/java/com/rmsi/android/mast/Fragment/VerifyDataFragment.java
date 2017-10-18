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

import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.activity.CaptureAttributesActivity;
import com.rmsi.android.mast.activity.CaptureDataMapActivity;
import com.rmsi.android.mast.adapter.SurveyListingAdapter;
import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.domain.Feature;

public class VerifyDataFragment extends Fragment {
    ListView listView;
    SurveyListingAdapter adapter;
    Context context;
    List<Feature> features = new ArrayList<Feature>();
    static boolean listChanged = false;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_survey_review_data_list, container, false);
        listView = (ListView) view.findViewById(android.R.id.list);
        TextView emptyText = (TextView) view.findViewById(android.R.id.empty);
        listView.setEmptyView(emptyText);

        adapter = new SurveyListingAdapter(context, this, features, "verifyData");
        listView.setAdapter(adapter);

        return view;
    }

    public void showPopupVerify(View v, Object object) {
        PopupMenu popup = new PopupMenu(context, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.verify_data_listing_options, popup.getMenu());
        int position = (Integer) object;
        final Long featureId = features.get(position).getId();
        final Long server_featureId = features.get(position).getServerId();

        popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.view_map:
                        Intent intent = new Intent(context, CaptureDataMapActivity.class);
                        intent.putExtra("featureid", featureId);
                        startActivity(intent);
                        return true;

                    case R.id.view_attributes:
                        Intent myIntent = new Intent(context, CaptureAttributesActivity.class);
                        myIntent.putExtra("featureid", featureId);
                        myIntent.putExtra("Server_featureid", server_featureId);
                        startActivity(myIntent);
                        return true;

                    case R.id.mark_as_verified:
                        markFeatureAsVerified(featureId);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

    private void deleteEntry(final Long featureId) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(R.string.deleteFeatureEntryMsg);
        alertDialogBuilder.setPositiveButton(R.string.btn_ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        DbController db = DbController.getInstance(context);
                        boolean result = db.deleteFeature(featureId);
                        db.close();
                        if (result) {
                            refreshList();
                        } else {
                            Toast.makeText(context, "Error deleting feature..!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        alertDialogBuilder.setNegativeButton(R.string.btn_cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void markFeatureAsVerified(final Long featureId) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(R.string.verifiedFeatureMsg);

        alertDialogBuilder.setPositiveButton(R.string.btn_ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        DbController db = DbController.getInstance(context);
                        boolean result = db.markFeatureAsVerified(featureId);
                        db.close();
                        if (result) {
                            Toast.makeText(context, R.string.refreshing_msg, Toast.LENGTH_SHORT).show();
                            refreshList();
                            listChanged = true;
                        } else {
                            Toast.makeText(context, "Error updating feature..!!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

        alertDialogBuilder.setNegativeButton(R.string.btn_cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void refreshList() {
        DbController db = DbController.getInstance(context);
        features.clear();
        features.addAll(db.fetchSyncededFeatures());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        refreshList();
        super.onResume();
    }
}
