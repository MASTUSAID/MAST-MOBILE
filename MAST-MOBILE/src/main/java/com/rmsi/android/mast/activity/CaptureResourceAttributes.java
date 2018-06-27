package com.rmsi.android.mast.activity;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.domain.ClaimType;
import com.rmsi.android.mast.domain.Classification;
import com.rmsi.android.mast.domain.ClassificationAttribute;
import com.rmsi.android.mast.domain.Feature;
import com.rmsi.android.mast.domain.Option;
import com.rmsi.android.mast.domain.OptionAttributes;
import com.rmsi.android.mast.domain.Property;
import com.rmsi.android.mast.domain.ResourceCustomAttribute;
import com.rmsi.android.mast.domain.ShareType;
import com.rmsi.android.mast.domain.SubClassification;
import com.rmsi.android.mast.domain.SubClassificationAttribute;
import com.rmsi.android.mast.domain.TenureType;
import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.DateUtility;
import com.rmsi.android.mast.util.StringUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ambar.srivastava on 12/21/2017.
 */

public class CaptureResourceAttributes extends ActionBarActivity {
    private final Context context = this;
    private DbController db = DbController.getInstance(context);
    Classification classification=null;
    private List<TenureType> optionsList=new ArrayList<>();
//    Property property=null;

    private Property propertyValidate=null;
    Long featureId = 0L;
    CommonFunctions cf = CommonFunctions.getInstance();

    //List<ClassificationAttribute> classificationsList=new ArrayList<>();
    List<SubClassificationAttribute> SubclassificationsList=new ArrayList<>();
    String polytype;
    private Spinner spinnerClass,spinnerSubClass,spinnertenureType;
    private boolean saveResult,saveResult1;
    List<Property> propertyList=new ArrayList<>();
    List<Property> subClassificationList=new ArrayList<>();
    List<Property> tenureList=new ArrayList<>();

    private String classi,subClassi,tenureType,tenureID,subID;
    Property property=null;

    ClassificationAttribute classificationData=new ClassificationAttribute();
    ClassificationAttribute subClassificationData=new ClassificationAttribute();
    ClassificationAttribute tenureTypenData=new ClassificationAttribute();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonFunctions.getInstance().Initialize(getApplicationContext());
        cf.loadLocale(getApplicationContext());

        setContentView(R.layout.capture_resource_attribute);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            featureId = extras.getLong("featureid");
        }
        if (featureId > 0) {
            propertyValidate = DbController.getInstance(context).getProperty(featureId);
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Capture Resource Information");

        if (toolbar != null)
            setSupportActionBar(toolbar);

        if (classification == null) {
            classification = new Classification();
        }
        if (propertyValidate == null) {
            propertyValidate = new Property();
        }


        spinnerClass= (Spinner) findViewById(R.id.classification_spinner);
        spinnerSubClass= (Spinner) findViewById(R.id.sub_classification_spinner);
        spinnertenureType= (Spinner) findViewById(R.id.tenure_spinner);


        List<ClassificationAttribute> classificationsList=  db.getClassification(true);

        if (classificationsList!=null) {
//            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(CaptureResourceAttributes.this, android.R.layout.simple_spinner_item, classificationsList);
//            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            spinnerClass.setAdapter(spinnerArrayAdapter);
            spinnerClass.setAdapter(new ArrayAdapter(context, android.R.layout.simple_spinner_item, classificationsList));
            ((ArrayAdapter) spinnerClass.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }


        optionsList=  db.gettenureType(true);


        if (optionsList!=null) {
            spinnertenureType.setAdapter(new ArrayAdapter(context, android.R.layout.simple_spinner_item, optionsList));
            ((ArrayAdapter) spinnertenureType.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }

        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


               classificationData=new ClassificationAttribute();
                classificationData.setAttribValue(((ClassificationAttribute) parent.getItemAtPosition(position)).getAttribValue());
                classificationData.setAttribID(((ClassificationAttribute) parent.getItemAtPosition(position)).getAttribID());



                classi=((ClassificationAttribute) parent.getItemAtPosition(position)).getAttribValue();
                propertyValidate.setClassificationValue(classi);
                propertyValidate.setClassificationId(((ClassificationAttribute) parent.getItemAtPosition(position)).getAttribID());

                getSubClassificationList(classificationData.getAttribID());


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        spinnerSubClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                subClassificationData=new ClassificationAttribute();
                subClassificationData.setAttribValue(((SubClassificationAttribute) parent.getItemAtPosition(position)).getAttribValue());
                subClassificationData.setAttribID(((SubClassificationAttribute) parent.getItemAtPosition(position)).getAttribID());


                subClassi=((SubClassificationAttribute) parent.getItemAtPosition(position)).getAttribValue();
                propertyValidate.setSubClassificationValue(subClassi);
                propertyValidate.setSubClassificationId(((SubClassificationAttribute) parent.getItemAtPosition(position)).getAttribID());
                subID=((SubClassificationAttribute) parent.getItemAtPosition(position)).getAttribID();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinnertenureType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                 tenureTypenData=new ClassificationAttribute();

                tenureTypenData.setAttribValue(((TenureType) parent.getItemAtPosition(position)).getAttribValue());
                tenureTypenData.setAttribID(((TenureType) parent.getItemAtPosition(position)).getAttribID().toString());
                propertyValidate.setTenureTypeValue(((TenureType) parent.getItemAtPosition(position)).getAttribValue());

                tenureID=((TenureType) parent.getItemAtPosition(position)).getAttribID().toString();
                tenureType=((TenureType) parent.getItemAtPosition(position)).getAttribValue();
                propertyValidate.setTenureTypeID(((TenureType) parent.getItemAtPosition(position)).getAttribID());


               // tenureList.add(property);
//                tenureType=(String) parent.getItemAtPosition(position);
//                propertyValidate.setSubClassificationValue(tenureType);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        for (int i = 0; i < classificationsList.size(); i++) {
            if (classificationsList.get(i).getAttribID().equalsIgnoreCase(StringUtility.empty(propertyValidate.getClassificationId()))) {
                spinnerClass.setSelection(i);
                break;
            }
        }




        for (int i = 0; i < optionsList.size(); i++) {
            if (optionsList.get(i).getAttribID().equalsIgnoreCase(StringUtility.empty(propertyValidate.getTenureTypeID()))) {
                spinnertenureType.setSelection(i);
                break;
            }
        }





    }

    private void getSubClassificationList(String classificationId) {
        SubclassificationsList=  db.getSubClassification(true,classificationId);

        if (SubclassificationsList!=null) {
            spinnerSubClass.setAdapter(new ArrayAdapter(context, android.R.layout.simple_spinner_item, SubclassificationsList));
            ((ArrayAdapter) spinnerSubClass.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        }

        for (int i = 0; i < SubclassificationsList.size(); i++) {
            if (SubclassificationsList.get(i).getAttribID().equalsIgnoreCase(StringUtility.empty(propertyValidate.getSubClassificationId()))) {
                spinnerSubClass.setSelection(i);
                break;
            }
        }


    }

//

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

            getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            saveData();
        }
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void





    saveData() {

//        if (!validateBasicInfo(context, true)) {
//            return false;
//        }
        //.add(property);
        if (validateBasicInfo(context, true)) {


            boolean deleteData=DbController.getInstance(context).deleteResource(featureId);

//            boolean saveResult = DbController.getInstance(context).insertResourceAtrr(propertyList, featureId);
            boolean saveResult = DbController.getInstance(context).insertResourceAtrrValue(classificationData, featureId);
            boolean saveResultsub = DbController.getInstance(context).insertResourceAtrrValue(subClassificationData, featureId);
            boolean saveResultTenure = DbController.getInstance(context).insertResourceAtrrValue(tenureTypenData, featureId);
            boolean saveResult1 = DbController.getInstance(context).updatePropertyBasic(propertyValidate);
            boolean saveResult3 = DbController.getInstance(context).insertFeature(featureId);
            boolean saveResult2 = DbController.getInstance(context).updateTenureBasic(propertyValidate, featureId);


            //boolean saveResult1 = DbController.getInstance(context).insertResourceSubClassAtrr(propertyValidate,featureId);
            // boolean saveResult2 = DbController.getInstance(context).insertResourceTenureAtrr(tenureList,featureId);
            //classificationsList.clear();
            subClassificationList.clear();
            tenureList.clear();

            if (saveResult == true) {
                Toast.makeText(context, "DATA SAVE Successfully", Toast.LENGTH_SHORT).show();

                //Case to find whether it's an Add event or Edit event
                boolean isAddCase = false;
                isAddCase = cf.IsEditResourceAttribute(featureId, tenureType);
                //------
                if((tenureType.equalsIgnoreCase("Collective"))||(tenureType.equalsIgnoreCase("Community"))){
                    DbController db = DbController.getInstance(context);
                    int iGrpID=db.getOwnerCount(featureId);
                    if(iGrpID==0){
                        isAddCase=true;
                    }
                    else{
                        isAddCase=false;
                    }
                }

                //Case for Add Attribute
                if (isAddCase) {

                    if ((tenureType.equalsIgnoreCase("Open")) || (tenureType.equalsIgnoreCase("Other"))) {
                        DbController db = DbController.getInstance(context);

                        List<ResourceCustomAttribute> attributesSize = db.getResAttributesSize(tenureID);
                        if (attributesSize.size() > 0) {
                            Intent intent = new Intent(context, CustomAttributeChange.class);
                            intent.putExtra("featureid", featureId);
                            intent.putExtra("classi", classi);
                            intent.putExtra("subclassi", subClassi);
                            intent.putExtra("tenure", tenureType);
                            intent.putExtra("tID", tenureID);
                            intent.putExtra("sID", subID);
                            finish();
                            startActivity(intent);

                        } else {

                            Intent intent = new Intent(context, CollectedResourceDataSummary.class);
                            intent.putExtra("featureid", featureId);
                            intent.putExtra("classi", classi);
                            intent.putExtra("subclassi", subClassi);
                            intent.putExtra("tID", tenureID);
                            intent.putExtra("tenure", tenureType);
                            finish();
                            startActivity(intent);

                        }
                    } else if ((!tenureType.equalsIgnoreCase("Open")) || (!tenureType.equalsIgnoreCase("Other"))) {

                        final Dialog dialog = new Dialog(context, R.style.DialogTheme);
                        dialog.setContentView(R.layout.dialog_for_info);
                        dialog.setTitle(getResources().getString(R.string.info));
                        dialog.getWindow().getAttributes().width = ViewGroup.LayoutParams.MATCH_PARENT;
                        Button proceed = (Button) dialog.findViewById(R.id.btn_proceed);
                        Button cancel = (Button) dialog.findViewById(R.id.btn_cancel);
                        final TextView txtTenureType = (TextView) dialog.findViewById(R.id.textView_tenure_type);
                        final TextView txtInfoMsg = (TextView) dialog.findViewById(R.id.textView_infoMsg);
                        final TextView cnfrmMsg = (TextView) dialog.findViewById(R.id.textView_cnfrm_msg);
                        cnfrmMsg.setVisibility(View.VISIBLE);
                        txtTenureType.setText(tenureType);
                        txtInfoMsg.setText(GetInfoMessage(tenureType));
                        proceed.setText(getResources().getText(R.string.yes));
                        cancel.setText(getResources().getText(R.string.no));

                        proceed.setOnClickListener(new View.OnClickListener() {
                            //Run when button is clicked
                            @Override
                            public void onClick(View v) {
                                Intent nextScreen = new Intent(context, ResourcePOI.class);

                                nextScreen.putExtra("featureid", featureId);
                                nextScreen.putExtra("classi", classi);
                                nextScreen.putExtra("subclassi", subClassi);
                                nextScreen.putExtra("tenure", tenureType);
                                nextScreen.putExtra("tID", tenureID);
                                nextScreen.putExtra("sID", subID);
                                finish();
                                startActivity(nextScreen);


                                dialog.dismiss();
                            }
                        });

                        cancel.setOnClickListener(new View.OnClickListener() {
                            //Run when button is clicked
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                    //Case for Edit Attribute

                }

                else {

                    if ((tenureType.equalsIgnoreCase("Open")) || (tenureType.equalsIgnoreCase("Other"))) {
                        DbController db = DbController.getInstance(context);

                        List<ResourceCustomAttribute> attributesSize = db.getResAttributesSize(tenureID);
                        if (attributesSize.size() > 0) {
                            Intent intent = new Intent(context, CustomAttributeChange.class);
                            intent.putExtra("featureid", featureId);
                            intent.putExtra("classi", classi);
                            intent.putExtra("subclassi", subClassi);
                            intent.putExtra("tenure", tenureType);
                            intent.putExtra("tID", tenureID);
                            intent.putExtra("sID", subID);
                            finish();
                            startActivity(intent);

                        } else {

                            Intent intent = new Intent(context, CollectedResourceDataSummary.class);
                            intent.putExtra("featureid", featureId);
                            intent.putExtra("classi", classi);
                            intent.putExtra("subclassi", subClassi);
                            intent.putExtra("tID", tenureID);
                            intent.putExtra("tenure", tenureType);
                            finish();
                            startActivity(intent);

                        }
                    }else {
                        Intent nextScreen = new Intent(context, ResourcePOI.class);

                        nextScreen.putExtra("featureid", featureId);
                        nextScreen.putExtra("classi", classi);
                        nextScreen.putExtra("subclassi", subClassi);
                        nextScreen.putExtra("tenure", tenureType);
                        nextScreen.putExtra("tID", tenureID);
                        nextScreen.putExtra("sID", subID);
                        finish();
                        startActivity(nextScreen);

                    }

                }



//          dialog.show();
//        } else if (shareTypeId == ShareType.TYPE_NON_NATURAL) {
//            Intent nextScreen = new Intent(context, AddNonNaturalPersonActivity.class);
//            nextScreen.putExtra("featureid", featureId);
//            nextScreen.putExtra("rightId", right.getId());
//            startActivity(nextScreen);

//            Toast.makeText(context,"DATA SAVE Successfully",Toast.LENGTH_SHORT).show();
//            Intent intent=new Intent(getApplicationContext(),CaptureTenureInfo.class);
//            intent.putExtra("featureid", featureId);
//            intent.putExtra("classi", classi);
//            intent.putExtra("subclassi", subClassi);
//            intent.putExtra("tenure", tenureType);
//            intent.putExtra("tID",tenureID);
//            intent.putExtra("sID",subID);
//            startActivity(intent);
            } else {
                Toast.makeText(context, "Unable to Save Data", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private String GetInfoMessage(String tenure) {

        String strInfoMessage="Info Message";
        if (tenure.equalsIgnoreCase("Private (jointly)")) {
            strInfoMessage="You must add only two occupants/owners and can add one or more persons of interest";
        }
        else if(tenure.equalsIgnoreCase("Private (individual)")) {
            strInfoMessage="You must add only one occupant/owner and can add one or more persons of interest";
        }
        else if(tenure.equalsIgnoreCase("Organization (informal)") || tenure.equalsIgnoreCase("Organization (formal)") ) {
            strInfoMessage="You must add information for the point of contact for the organization/ association/ or group and can add one or more persons of interest";
        }
        else if(tenure.equalsIgnoreCase("Community") || tenure.equalsIgnoreCase("Collective") ) {
            strInfoMessage="You must add one or more occupants/owners and add one or more persons of interest";
        }

        else if(tenure.equalsIgnoreCase("Public")) {
            strInfoMessage="Please enter information agency and point of contact information for agency/authority";
        }

        else if(tenure.equalsIgnoreCase("Open") || tenure.equalsIgnoreCase("Other") ) {
            strInfoMessage="Please enter information about how this land resource is being held or used";
        }

        return strInfoMessage;
    }


    private boolean validateBasicInfo(Context context, boolean b) {

            boolean result = true;
            String errorMessage = "";


            if (StringUtility.isEmpty(propertyValidate.getClassificationValue())) {

                errorMessage = context.getResources().getString(R.string.SelectClassificationType);
            }else if (propertyValidate.getClassificationValue().equalsIgnoreCase(context.getResources().getString(R.string.SelectOption))) {
                errorMessage = context.getResources().getString(R.string.SelectClassificationType);
            }
            else if (StringUtility.isEmpty(propertyValidate.getSubClassificationValue())) {
                errorMessage = context.getResources().getString(R.string.SelectSubClassificationType);
            }else if (propertyValidate.getSubClassificationValue().equalsIgnoreCase(context.getResources().getString(R.string.SelectOption))) {
                errorMessage = context.getResources().getString(R.string.SelectSubClassificationType);
            }else if (propertyValidate.getTenureTypeValue().equalsIgnoreCase(context.getResources().getString(R.string.SelectOption))) {
                errorMessage = context.getResources().getString(R.string.SelectTENUREType);
            }
            else if (StringUtility.isEmpty(propertyValidate.getTenureTypeValue())) {
                errorMessage = context.getResources().getString(R.string.SelectTENUREType);
            }
//        else if (getHamletId() == 0L) {
//            errorMessage = context.getResources().getString(R.string.Please_select_Hamlet);
//        } else if (StringUtility.isEmpty(getAdjudicator1())) {
//            errorMessage = context.getResources().getString(R.string.Please_select_Witness_1);
//        } else if (StringUtility.isEmpty(getAdjudicator2())) {
//            errorMessage = context.getResources().getString(R.string.Please_select_Witness_2);
//        } else if (getAdjudicator1() == getAdjudicator2()) {
//            errorMessage = context.getResources().getString(R.string.Witness_1_and_Witness_2_can_not_be_same);
//        }

            if (!errorMessage.equals("")) {
                result = false;
                if (b)
                    CommonFunctions.getInstance().showToast(context, errorMessage, Toast.LENGTH_LONG, Gravity.CENTER);
            }
            return result;
        }

    private void updateCount() {
        try {


                Property tmpProp = db.getProperty(featureId);


        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (propertyValidate != null && !StringUtility.isEmpty(propertyValidate.getClassificationId()))
            spinnerClass.setEnabled(true);

        if (propertyValidate != null && !StringUtility.isEmpty(propertyValidate.getSubClassificationId()))
            spinnerSubClass.setEnabled(true);

        if (propertyValidate != null && !StringUtility.isEmpty(propertyValidate.getTenureTypeID()))
            spinnertenureType.setEnabled(true);

        updateCount();
                    // Don't show toolbar for unclaimed parcels

    }

}
