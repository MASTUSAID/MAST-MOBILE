package com.rmsi.android.mast.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.rmsi.android.mast.activity.CollectedResourceDataSummary;
import com.rmsi.android.mast.activity.ListActivity;
import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.domain.Person;
import com.rmsi.android.mast.domain.Summary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ambar.Srivastava on 1/5/2018.
 */

public class SummaryAdapater extends BaseAdapter{
   List<Summary> attributes=new ArrayList<Summary>();
    Context context;

    private static LayoutInflater inflater=null;
    public SummaryAdapater(CollectedResourceDataSummary mainActivity, List<Summary> attributes) {
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
        TextView value;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.summary_adapter, null);
        holder.lable=(TextView) rowView.findViewById(R.id.surveyData);
        holder.value=(TextView) rowView.findViewById(R.id.surveyData1);
        holder.lable.setText(attributes.get(position).getnameLabel());
        holder.value.setText(attributes.get(position).getvalue());

        return rowView;
    }

}
