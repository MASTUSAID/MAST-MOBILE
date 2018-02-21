package com.rmsi.android.mast.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.rmsi.android.mast.activity.CollectedResourceDataSummary;
import com.rmsi.android.mast.activity.CustomAttributeChange;
import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.domain.ResourceAttribute;
import com.rmsi.android.mast.domain.ResourceCustomAttribute;
import com.rmsi.android.mast.domain.Summary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ambar.Srivastava on 1/11/2018.
 */

public class CustomAttributeAdapter extends BaseAdapter {
    List<ResourceCustomAttribute> attributes=new ArrayList<ResourceCustomAttribute>();
    Context context;

    private static LayoutInflater inflater=null;
    public CustomAttributeAdapter(CustomAttributeChange mainActivity, List<ResourceCustomAttribute> attributes) {
        // TODO Auto-generated constructor stub
        this.attributes=attributes;
        context=mainActivity;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return attributes.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView lable;
        EditText value;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.item_edit_text, null);
        holder.lable=(TextView) rowView.findViewById(R.id.field);
        holder.value=(EditText) rowView.findViewById(R.id.fieldValue);
        holder.lable.setText(attributes.get(position).getValue());
        holder.value.setText("");

        return rowView;
    }

}
