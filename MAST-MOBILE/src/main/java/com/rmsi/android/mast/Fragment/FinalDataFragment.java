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
import com.rmsi.android.mast.adapter.SurveyListingAdapter;
import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.domain.Feature;
import com.rmsi.android.mast.util.CommonFunctions;

public class FinalDataFragment extends Fragment {
    Context context;
    SurveyListingAdapter adapter;
    List<Feature> features = new ArrayList<Feature>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_survey_review_data_list, container, false);
        ListView listView = (ListView) view.findViewById(android.R.id.list);
        TextView emptyText = (TextView) view.findViewById(android.R.id.empty);
        listView.setEmptyView(emptyText);

        adapter = new SurveyListingAdapter(context, this, features, "final");
        listView.setAdapter(adapter);
        return view;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && VerifyDataFragment.listChanged) {
            refreshList();
            Toast.makeText(context, R.string.refreshing_msg, Toast.LENGTH_SHORT).show();
            VerifyDataFragment.listChanged = false;
        }
    }

    private void refreshList() {
        DbController db = DbController.getInstance(context);
        features.clear();
        features.addAll(db.fetchFinalFeatures());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        refreshList();
        super.onResume();
    }

}
