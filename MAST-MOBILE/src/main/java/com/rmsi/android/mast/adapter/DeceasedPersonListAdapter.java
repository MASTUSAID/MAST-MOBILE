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
import com.rmsi.android.mast.activity.PersonListWithDPActivity;
import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.domain.DeceasedPerson;
import com.rmsi.android.mast.domain.Media;

import java.util.List;

public class DeceasedPersonListAdapter extends BaseAdapter {
    private List<DeceasedPerson> persons;
    private LayoutInflater lInflator;
    Context context;
    PersonListWithDPActivity activityObj;

    public DeceasedPersonListAdapter(Context context, PersonListWithDPActivity activityObj, List<DeceasedPerson> persons) {
        this.context = context;
        this.persons = persons;
        this.lInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activityObj = activityObj;
    }

    public int getCount() {
        return persons.size();
    }

    public DeceasedPerson getItem(int position) {
        return persons.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        DeceasedPerson person = getItem(position);

        if (convertView == null) {
            convertView = lInflator.inflate(R.layout.item_of_custom_array_dapter, parent, false);
            TextView textViewItem = (TextView) convertView.findViewById(R.id.labelName);
            ImageButton options = (ImageButton) convertView.findViewById(R.id.optionsButton);

            textViewItem.setText(person.getFullName());
            options.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    activityObj.showPopupForDP(v, position);
                }
            });
        }
        return convertView;
    }
}
