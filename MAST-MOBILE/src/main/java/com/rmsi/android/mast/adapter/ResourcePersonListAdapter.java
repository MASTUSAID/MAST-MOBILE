package com.rmsi.android.mast.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rmsi.android.mast.activity.CollectedResourceDataSummary;
import com.rmsi.android.mast.activity.CustomAttributeChange;
import com.rmsi.android.mast.activity.ListActivity;
import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.Person;
import com.rmsi.android.mast.domain.ResourceCustomAttribute;
import com.rmsi.android.mast.domain.ResourceOwner;

import java.util.List;

/**
 * Created by Ambar.Srivastava on 1/15/2018.
 */

public class ResourcePersonListAdapter extends BaseAdapter {
    private List<ResourceOwner> persons;
    private LayoutInflater lInflator;
    Context context;
    private int iCount=0;
    ListActivity activityObj;
    private long featureID;
    private String tenureType;
    private String fullName;
    int iOwnerCount=0;


    public ResourcePersonListAdapter(Context context, ListActivity activityObj, List<ResourceOwner> persons, String tenureType, long featureID, int iOwnerCount) {
        this.context = context;
        this.persons = persons;
        this.tenureType=tenureType;
        this.featureID=featureID;
        this.iOwnerCount=iOwnerCount;
        this.lInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activityObj = activityObj;
    }

    public int getCount() {
        return persons.size();
    }

    public ResourceOwner getItem(int position) {
        return persons.get(position);
    }

//    public int getCount() {
//        return 1;
//    }
//
//    public Attribute getItem(int position) {
//        return persons.get(0);
//    }

    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        DbController db = DbController.getInstance(context);
        //String ownerName=db.getResorceOwnerName(tenureType,iCount,featureID);
        ResourceOwner person = getItem(position);
        if (convertView == null) {
            convertView = lInflator.inflate(R.layout.res_person_adapter, parent, false);
        }

        TextView textViewItem = (TextView) convertView.findViewById(R.id.surveyData);
        ImageButton options = (ImageButton) convertView.findViewById(R.id.optionsButton);
        ImageView imgViewItem = (ImageView) convertView.findViewById(R.id.img_attachment);
        textViewItem.setVisibility(View.VISIBLE);
        options.setVisibility(View.VISIBLE);
       // imgViewItem.setVisibility(View.VISIBLE);
        if (person.getMedia() != null && person.getMedia().size() > 0) {
            imgViewItem.setVisibility(View.VISIBLE);
        } else {
            imgViewItem.setVisibility(View.INVISIBLE);
        }
        //fullName=db.getResorceOwner1FName(tenureType,iOwnerCount,featureID)+" "+db.getResorceOwner1MName(tenureType,iOwnerCount,featureID)+" "+db.getResorceOwner1LName(tenureType,iOwnerCount,featureID);
        fullName=persons.get(position).getOwnerName();
        textViewItem.setText(fullName);
        //textViewItem.setText(persons.get(0).getValue());
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityObj.showPopup(v, position);
            }
        });

        db.close();
        return convertView;
    }

    public View getView_Temp2(final int position, View convertView, ViewGroup parent) {

        DbController db = DbController.getInstance(context);
        //String ownerName=db.getResorceOwnerName(tenureType,iCount,featureID);
        //Attribute person = getItem(position);
        if (convertView == null) {
            convertView = lInflator.inflate(R.layout.res_person_adapter, parent, false);
        }

        if (iCount==0){



            TextView textViewItem = (TextView) convertView.findViewById(R.id.surveyData);
            ImageButton options = (ImageButton) convertView.findViewById(R.id.optionsButton);
            ImageView imgViewItem = (ImageView) convertView.findViewById(R.id.img_attachment);
            textViewItem.setVisibility(View.VISIBLE);
            options.setVisibility(View.VISIBLE);
            imgViewItem.setVisibility(View.VISIBLE);
            fullName=db.getResorceOwner1FName(tenureType,iOwnerCount,featureID)+" "+db.getResorceOwner1MName(tenureType,iOwnerCount,featureID)+" "+db.getResorceOwner1LName(tenureType,iOwnerCount,featureID);
            textViewItem.setText(fullName);
            //textViewItem.setText(persons.get(0).getValue());
            options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activityObj.showPopup(v, position);
                }
            });
            iCount=iCount+1;
        }
        if (iOwnerCount==2) {
            if (position==1) {
                TextView textViewItem = (TextView) convertView.findViewById(R.id.surveyData);
                ImageButton options = (ImageButton) convertView.findViewById(R.id.optionsButton);
                ImageView imgViewItem = (ImageView) convertView.findViewById(R.id.img_attachment);
                textViewItem.setVisibility(View.VISIBLE);
                options.setVisibility(View.VISIBLE);
                imgViewItem.setVisibility(View.VISIBLE);
                fullName=db.getResorceOwner2FName(tenureType,iOwnerCount,featureID)+" "+db.getResorceOwner2MName(tenureType,iOwnerCount,featureID)+" "+db.getResorceOwner2LName(tenureType,iOwnerCount,featureID);
                textViewItem.setText(fullName);
                //textViewItem.setText(persons.get(0).getValue());
                options.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activityObj.showPopup(v, position);
                    }
                });
                iCount=iCount+1;

            }
        }


        db.close();
        return convertView;
    }

    public View getView_TEMP(final int position, View convertView, ViewGroup parent) {

         DbController db = DbController.getInstance(context);
        //String ownerName=db.getResorceOwnerName(tenureType,iCount,featureID);
        //Attribute person = getItem(position);
        if (convertView == null) {
              convertView = lInflator.inflate(R.layout.res_person_adapter, parent, false);
        }
        if (tenureType.equalsIgnoreCase("Private (jointly)")){

            if(iCount<2) {
//                if (convertView == null) {
//                    convertView = lInflator.inflate(R.layout.res_person_adapter, parent, false);
//                }
            }
            if (iCount==0)
            {


                TextView textViewItem = (TextView) convertView.findViewById(R.id.surveyData);
                ImageButton options = (ImageButton) convertView.findViewById(R.id.optionsButton);
                ImageView imgViewItem = (ImageView) convertView.findViewById(R.id.img_attachment);
                textViewItem.setVisibility(View.VISIBLE);
                options.setVisibility(View.VISIBLE);
                imgViewItem.setVisibility(View.VISIBLE);

                fullName=db.getResorceOwner2FName(tenureType,iOwnerCount,featureID)+" "+db.getResorceOwner1MName(tenureType,iOwnerCount,featureID)+" "+db.getResorceOwner1LName(tenureType,iOwnerCount,featureID);
                textViewItem.setText(fullName);
                //textViewItem.setText(persons.get(0).getValue());
                options.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activityObj.showPopup(v, position);
                    }
                });
                iCount=iCount+1;

            }else if (iCount==1){



                TextView textViewItem = (TextView) convertView.findViewById(R.id.surveyData);
                ImageButton options = (ImageButton) convertView.findViewById(R.id.optionsButton);
                ImageView imgViewItem = (ImageView) convertView.findViewById(R.id.img_attachment);
                textViewItem.setVisibility(View.VISIBLE);
                options.setVisibility(View.VISIBLE);
                imgViewItem.setVisibility(View.VISIBLE);
                fullName=db.getResorceOwner2FName(tenureType,iOwnerCount,featureID)+" "+db.getResorceOwner1MName(tenureType,iOwnerCount,featureID)+" "+db.getResorceOwner1LName(tenureType,iOwnerCount,featureID);
                textViewItem.setText(fullName);
                //textViewItem.setText(persons.get(0).getValue());
                options.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activityObj.showPopup(v, position);
                    }
                });
                iCount=iCount+1;
            }
//            else{
//                convertView.setVisibility(View.GONE);
//                iCount=iCount+1;
//            }
            //return convertView;
            }else{
            if(iCount<1) {
//                if (convertView == null) {
//                    convertView = lInflator.inflate(R.layout.item_list_row_with_attachment, parent, false);
//                }
            }
            if (iCount==0)
            {



                TextView textViewItem = (TextView) convertView.findViewById(R.id.surveyData);
                ImageButton options = (ImageButton) convertView.findViewById(R.id.optionsButton);
                ImageView imgViewItem = (ImageView) convertView.findViewById(R.id.img_attachment);
                textViewItem.setVisibility(View.VISIBLE);
                options.setVisibility(View.VISIBLE);
                imgViewItem.setVisibility(View.VISIBLE);

                fullName=db.getResorceOwner2FName(tenureType,iOwnerCount,featureID)+" "+db.getResorceOwner1MName(tenureType,iOwnerCount,featureID)+" "+db.getResorceOwner1LName(tenureType,iOwnerCount,featureID);
                //fullName=persons.get(0).getValue()+" "+persons.get(1).getValue()+" "+persons.get(2).getValue();
                textViewItem.setText(fullName);
                //textViewItem.setText(persons.get(0).getValue());
                options.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activityObj.showPopup(v, position);
                    }
                });

                iCount=iCount+1;
            }
//            else {
//                convertView.setVisibility(View.GONE);
//                iCount=iCount+1;
//            }
            //return convertView;
        }



//        if (convertView == null) {
//            convertView = lInflator.inflate(R.layout.item_list_row_with_attachment, parent, false);
//        }
//        if (iCount==0)
//        {
//
//            TextView textViewItem = (TextView) convertView.findViewById(R.id.surveyData);
//            ImageButton options = (ImageButton) convertView.findViewById(R.id.optionsButton);
//            ImageView imgViewItem = (ImageView) convertView.findViewById(R.id.img_attachment);
//            fullName=persons.get(0).getValue()+" "+persons.get(1).getValue()+" "+persons.get(2).getValue();
//            textViewItem.setText(fullName);
//            //textViewItem.setText(persons.get(0).getValue());
//            options.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    activityObj.showPopup(v, position);
//                }
//            });
//            iCount=iCount+1;
//
//        }
//        else if (iCount==1){
//
//            TextView textViewItem = (TextView) convertView.findViewById(R.id.surveyData);
//            ImageButton options = (ImageButton) convertView.findViewById(R.id.optionsButton);
//            ImageView imgViewItem = (ImageView) convertView.findViewById(R.id.img_attachment);
//            fullName=persons.get(1).getValue()+" "+persons.get(3).getValue()+" "+persons.get(5).getValue();
//            textViewItem.setText(fullName);
//            //textViewItem.setText(persons.get(0).getValue());
//            options.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    activityObj.showPopup(v, position);
//                }
//            });
//            iCount=iCount+1;
//        }
//        else{
//            convertView.setVisibility(View.GONE);
//            iCount=iCount+1;
//        }



        db.close();
        return convertView;
    }
}
