package com.rmsi.android.mast.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;

import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.util.GuiUtility;
import com.rmsi.android.mast.util.ResOwnGuiUtility;

import java.util.List;

/**
 * Created by Ambar.Srivastava on 1/10/2018.
 */

public class ResourceAttributeAdapter extends BaseAdapter {
    private List<Attribute> attrList;
    private Context context;
    private LayoutInflater inflater;
    private boolean readOnly;

    public ResourceAttributeAdapter(Context contextAct, List<Attribute> attrList, boolean readOnly) {
        this.context = contextAct;
        this.readOnly = readOnly;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.attrList = attrList;
    }

    @Override
    public int getCount() {
        return attrList.size();
    }

    @Override
    public Object getItem(int position) {
        return attrList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return ResOwnGuiUtility.createViewFromAttribute((Attribute) getItem(position), inflater, false, readOnly);
    }

}