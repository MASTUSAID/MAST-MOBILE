package com.rmsi.android.mast.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rmsi.android.mast.activity.ListActivity;
import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.domain.Media;

import java.util.List;

public class MediaListAdapter extends BaseAdapter {
    private List<Media> mediaList;
    private LayoutInflater lInflator;
    Context context;
    ListActivity activityObj;

    public MediaListAdapter(Context context, ListActivity activityObj, List<Media> mediaList) {
        this.context = context;
        this.mediaList = mediaList;
        this.lInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activityObj = activityObj;
    }

    public int getCount() {
        return mediaList.size();
    }

    public Media getItem(int position) {
        return mediaList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Media media = getItem(position);

        if (convertView == null) {
            convertView = lInflator.inflate(R.layout.item_list_row, parent, false);
        }

        TextView textViewItem = (TextView) convertView.findViewById(R.id.surveyData);
        ImageButton options = (ImageButton) convertView.findViewById(R.id.optionsButton);

        textViewItem.setText(media.getName());
        options.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activityObj.showPopup(v, position);
            }
        });

        return convertView;
    }
}
