package com.rmsi.android.mast.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rmsi.android.mast.Fragment.ResourcePersonListFragment;
import com.rmsi.android.mast.Fragment.ResourcePoiListFragment;
import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.domain.Gender;
import com.rmsi.android.mast.domain.Person;
import com.rmsi.android.mast.domain.Property;
import com.rmsi.android.mast.domain.RelationshipType;
import com.rmsi.android.mast.domain.ResourceCustomAttribute;
import com.rmsi.android.mast.domain.ResourceOwner;
import com.rmsi.android.mast.domain.ResourcePersonOfInterest;
import com.rmsi.android.mast.domain.ResourcePoiSync;
import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.GuiUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ambar.Srivastava on 1/13/2018.
 */

public class ResourcePOI extends AppCompatActivity {

    Button addnewPerson, btnNext, addPOI;
    Context context;
    Long featureId = 0L;
    CommonFunctions cf = CommonFunctions.getInstance();
    List<Property> resourcePoiSyncsList = new ArrayList<>();
    String msg, warning;
    int position;
    private boolean readOnly = false;
    String warningStr, infoStr, shareTypeStr;
    String saveStr, backStr;
    private Property property;
    private ResourcePersonListFragment personsFragment;
    private ResourcePoiListFragment poiFragment;
    private int personSubType;
    private List<ResourcePoiSync> resourcePoiSyncs = new ArrayList<>();
    private String classi, subClassi, tenureType, tenureID, subID;
    private TextView lblShareType;
    private int iOwnerCount=0;
    private List<ResourceOwner> listOwner=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            CommonFunctions.getInstance().Initialize(getApplicationContext());
        } catch (Exception e) {
        }
        cf.loadLocale(getApplicationContext());

        context = this;

        warningStr = getResources().getString(R.string.warning);
        infoStr = getResources().getString(R.string.info);
        shareTypeStr = getResources().getString(R.string.shareType);
        saveStr = getResources().getString(R.string.save);
        backStr = getResources().getString(R.string.back);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            featureId = extras.getLong("featureid");
            classi = extras.getString("classi");
            subClassi = extras.getString("subclassi");
            tenureType = extras.getString("tenure");
            tenureID = extras.getString("tID");
            subID = extras.getString("sID");
        }

        readOnly = CommonFunctions.isFeatureReadOnly(featureId);
        setContentView(R.layout.resource_poi);

        lblShareType = (TextView) findViewById(R.id.tenureType_lbl);
        addnewPerson = (Button) findViewById(R.id.btn_addNewPerson);
        btnNext = (Button) findViewById(R.id.btnNext);
        addPOI = (Button) findViewById(R.id.btn_addNextKin);
        personsFragment = (ResourcePersonListFragment) getFragmentManager().findFragmentById(R.id.compPersonsList);
        poiFragment = (ResourcePoiListFragment) getFragmentManager().findFragmentById(R.id.respoi);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Resource Person List");
        if (toolbar != null)
            setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lblShareType.setText("Tenure Type: "+tenureType);

        if (readOnly) {
            addnewPerson.setVisibility(View.GONE);
            addPOI.setVisibility(View.GONE);
            btnNext.setText(backStr);
        }

//        addnewPerson.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                //Case to find whether it's an Add event or Edit event
//                boolean isAddCase=false;
//                isAddCase=cf.IsEditResourceAttribute(featureId,tenureType);
//                //------
//                if(isAddCase) {
//                if (!tenureType.equalsIgnoreCase("Private (jointly)")) {
//                    if(iOwnerCount<1) {
//                        Intent intent = new Intent(getApplicationContext(), CaptureTenureInfo.class);
//                        intent.putExtra("featureid", featureId);
//                        intent.putExtra("classi", classi);
//                        intent.putExtra("subclassi", subClassi);
//                        intent.putExtra("tenure", tenureType);
//                        intent.putExtra("tID", tenureID);
//                        intent.putExtra("sID", subID);
//                        startActivity(intent);
//                        iOwnerCount = iOwnerCount + 1;
//                    }
//                    else {
//                        //Show message
//                        Toast.makeText(context,"You can add only one owner for tenure type: "+tenureType,Toast.LENGTH_SHORT).show();
//
//                    }
//
//
//
//                } else if(tenureType.equalsIgnoreCase("Private (jointly)")) {
//                    if (iOwnerCount < 2) {
////
//                        if (iOwnerCount == 0) {
//
//                            Intent intent = new Intent(context, CaptureTenureInfo.class);
//                            intent.putExtra("featureid", featureId);
//                            intent.putExtra("classi", classi);
//                            intent.putExtra("subclassi", subClassi);
//                            intent.putExtra("tenure", tenureType);
//                            intent.putExtra("tID", tenureID);
//                            intent.putExtra("sID", subID);
//                            startActivity(intent);
//                            iOwnerCount = iOwnerCount + 1;
//                        } else if (iOwnerCount == 1) {
//                            Intent intent = new Intent(context, Owner2.class);
//                            intent.putExtra("featureid", featureId);
//                            intent.putExtra("classi", classi);
//                            intent.putExtra("subclassi", subClassi);
//                            intent.putExtra("tenure", tenureType);
//                            intent.putExtra("tID", tenureID);
//                            intent.putExtra("sID", subID);
//                            startActivity(intent);
//                            iOwnerCount = iOwnerCount + 1;
//                        }
//                    } else {
//                        //Show message
//                        Toast.makeText(context, "You can add only two owner for tenure type: " + tenureType, Toast.LENGTH_SHORT).show();
//
//                    }
//                }
//                }
//                else
//                {
//                    //Show message
//                    Toast.makeText(context, "You can not add more owner for tenure type: " + tenureType, Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });



        //Ambar
        addnewPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Case to find whether it's an Add event or Edit event
                boolean isAddCase=false;
                isAddCase=cf.IsEditResourceAttribute(featureId,tenureType);
                //------
                if(isAddCase) {

                    //For Single Owner
                    if ((tenureType.equalsIgnoreCase("Private (individual)"))||(tenureType.equalsIgnoreCase("Organization (formal)"))||(tenureType.equalsIgnoreCase("Organization (informal)"))||(tenureType.equalsIgnoreCase("Public"))) {
                        if (iOwnerCount < 1) {
                            Intent intent = new Intent(getApplicationContext(), Owner.class);
                            intent.putExtra("featureid", featureId);
                            intent.putExtra("classi", classi);
                            intent.putExtra("subclassi", subClassi);
                            intent.putExtra("tenure", tenureType);
                            intent.putExtra("tID", tenureID);
                            intent.putExtra("sID", subID);
                            startActivity(intent);
                            iOwnerCount = iOwnerCount + 1;
                        } else {
                            //Show message
                            Toast.makeText(context, "You can add only one owner for tenure type: " + tenureType, Toast.LENGTH_SHORT).show();

                        }
                    }

                    //For Two Owner
                    else if(tenureType.equalsIgnoreCase("Private (jointly)")) {
                        if (iOwnerCount < 2) {
//                            if (iOwnerCount == 0) {
                                Intent intent = new Intent(context, Owner.class);
                                intent.putExtra("featureid", featureId);
                                intent.putExtra("classi", classi);
                                intent.putExtra("subclassi", subClassi);
                                intent.putExtra("tenure", tenureType);
                                intent.putExtra("tID", tenureID);
                                intent.putExtra("sID", subID);
                                intent.putExtra("ownerCount", iOwnerCount);
                                startActivity(intent);
                                iOwnerCount = iOwnerCount + 1;
//                            } else if (iOwnerCount == 1) {
//                                Intent intent = new Intent(context, Owner2.class);
//                                intent.putExtra("featureid", featureId);
//                                intent.putExtra("classi", classi);
//                                intent.putExtra("subclassi", subClassi);
//                                intent.putExtra("tenure", tenureType);
//                                intent.putExtra("tID", tenureID);
//                                intent.putExtra("sID", subID);
//                                startActivity(intent);
//                                iOwnerCount = iOwnerCount + 1;
//                            }
                        } else {
                            //Show message
                            Toast.makeText(context, "You can add only two owner for tenure type: " + tenureType, Toast.LENGTH_SHORT).show();

                        }
                    }
//                    else if(tenureType.equalsIgnoreCase("Private (jointly)")) {
//                        if (iOwnerCount < 2) {
//                            if (iOwnerCount == 0) {
//                                Intent intent = new Intent(context, Owner.class);
//                                intent.putExtra("featureid", featureId);
//                                intent.putExtra("classi", classi);
//                                intent.putExtra("subclassi", subClassi);
//                                intent.putExtra("tenure", tenureType);
//                                intent.putExtra("tID", tenureID);
//                                intent.putExtra("sID", subID);
//                                startActivity(intent);
//                                iOwnerCount = iOwnerCount + 1;
//                            } else if (iOwnerCount == 1) {
//                                Intent intent = new Intent(context, Owner2.class);
//                                intent.putExtra("featureid", featureId);
//                                intent.putExtra("classi", classi);
//                                intent.putExtra("subclassi", subClassi);
//                                intent.putExtra("tenure", tenureType);
//                                intent.putExtra("tID", tenureID);
//                                intent.putExtra("sID", subID);
//                                startActivity(intent);
//                                iOwnerCount = iOwnerCount + 1;
//                            }
//                        } else {
//                            //Show message
//                            Toast.makeText(context, "You can add only two owner for tenure type: " + tenureType, Toast.LENGTH_SHORT).show();
//
//                        }
//                    }
                    //For Multiple Owner
                    else if ((tenureType.equalsIgnoreCase("Collective"))||(tenureType.equalsIgnoreCase("Community"))) {
                            Intent intent = new Intent(getApplicationContext(), Owner.class);
                            intent.putExtra("featureid", featureId);
                            intent.putExtra("classi", classi);
                            intent.putExtra("subclassi", subClassi);
                            intent.putExtra("tenure", tenureType);
                            intent.putExtra("tID", tenureID);
                            intent.putExtra("ownerCount", iOwnerCount);
                            intent.putExtra("sID", subID);
                            startActivity(intent);
                            iOwnerCount = iOwnerCount + 1;

                    }
                    //For No More Owner
                    else
                    {
                        //Show message
                        Toast.makeText(context, "You can not add more owner for tenure type: " + tenureType, Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Case to find whether it's an Add event or Edit event

                int tenureTypeID= DbController.getInstance(context).getTenureByFeatureID(featureId);
                if (tenureTypeID!=10 && tenureTypeID!=18  && tenureTypeID!=14 && tenureTypeID!=13){
                    int checkPrimaryOccupant= DbController.getInstance(context).getPrimaryOccupant(featureId);
                    if (checkPrimaryOccupant==0){
                        Toast.makeText(context, "You should add atleast One Primary Occupant", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                boolean isAddCase=false;
                isAddCase=cf.IsEditResourceAttribute(featureId,tenureType);

                //------ Special case for multiple owner for tenure type :- Collective and community
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
                //------ Special case for multiple owner for tenure type :- Collective and community
                //------
                if(isAddCase) {
                    if (tenureType.equalsIgnoreCase("Private (jointly)")) {
                        if (iOwnerCount == 0) {
                            Toast.makeText(context, "You should add atleast Owner", Toast.LENGTH_SHORT).show();

                        }
                        if (iOwnerCount == 1) {
                            Toast.makeText(context, "You should add one more Owner", Toast.LENGTH_SHORT).show();
                        }
                        if (iOwnerCount == 2) {
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
                        }

                    }
                    if (!tenureType.equalsIgnoreCase("Private (jointly)")) {
                        if (iOwnerCount == 0) {
                            Toast.makeText(context, "You should add atleast One Owner", Toast.LENGTH_SHORT).show();

                        } else {
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
                        }
                    }
                }
                else {

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
                }




//                DbController db = DbController.getInstance(context);
//
//                List<ResourceCustomAttribute> attributesSize = db.getResAttributesSize(tenureID);
//                if (attributesSize.size() > 0) {
//                    Intent intent = new Intent(context, CustomAttributeChange.class);
//                    intent.putExtra("featureid", featureId);
//                    intent.putExtra("classi", classi);
//                    intent.putExtra("subclassi", subClassi);
//                    intent.putExtra("tenure", tenureType);
//                    intent.putExtra("tID", tenureID);
//                    intent.putExtra("sID", subID);
//                    startActivity(intent);
//                } else {
//
//                    Intent intent = new Intent(context, CollectedResourceDataSummary.class);
//                    intent.putExtra("featureid", featureId);
//                    intent.putExtra("classi", classi);
//                    intent.putExtra("subclassi", subClassi);
//                    intent.putExtra("tID", tenureID);
//                    intent.putExtra("tenure", tenureType);
//                    startActivity(intent);
//                }
            }

        });

        addPOI.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Case to find whether it's an Add event or Edit event
                boolean isAddCase=false;
                isAddCase=cf.IsEditResourceAttribute(featureId,tenureType);
                //------

                //------ Special case for multiple owner for tenure type :- Collective and community
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
                //------ Special case for multiple owner for tenure type :- Collective and community
                if(isAddCase) {

                    if (iOwnerCount == 0) {
                        Toast.makeText(context, "There should be at least one owner", Toast.LENGTH_SHORT).show();
                    } else {
                        final Dialog dialog = new Dialog(context, R.style.DialogTheme);
                        dialog.setContentView(R.layout.dialog_person_of_interest);
                        dialog.setTitle(getResources().getString(R.string.nextKin));
                        dialog.getWindow().getAttributes().width = ViewGroup.LayoutParams.MATCH_PARENT;

                        Button save = (Button) dialog.findViewById(R.id.btn_ok);
                        final EditText firstName = (EditText) dialog.findViewById(R.id.editTextFirstName);
                        final EditText middleName = (EditText) dialog.findViewById(R.id.editTextMiddleName);
                        final EditText lastName = (EditText) dialog.findViewById(R.id.editTextLastName);
                        final Spinner genderSpinner = (Spinner) dialog.findViewById(R.id.spinnerGender);
                        final Spinner relSpinner = (Spinner) dialog.findViewById(R.id.spinnerRelationshipType);
                        final TextView txtDob = (TextView) dialog.findViewById(R.id.txtDob);
                        LinearLayout extraFields = (LinearLayout) dialog.findViewById(R.id.extraLayout);
                        extraFields.setVisibility(View.VISIBLE);

                        DbController db = DbController.getInstance(context);

                        genderSpinner.setAdapter(new ArrayAdapter(context, android.R.layout.simple_spinner_item, db.getGenders(true)));
                        relSpinner.setAdapter(new ArrayAdapter(context, android.R.layout.simple_spinner_item, db.getRelationshipTypes(true)));
                        ((ArrayAdapter) genderSpinner.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        ((ArrayAdapter) relSpinner.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        txtDob.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                GuiUtility.showDatePicker(txtDob, "");
                            }
                        });

                        save.setText(saveStr);

                        save.setOnClickListener(new View.OnClickListener() {
                            //Run when button is clicked
                            @Override
                            public void onClick(View v) {
                                String poi_fName = firstName.getText().toString();
                                String poi_middleName = middleName.getText().toString();
                                String poi_lastName = lastName.getText().toString();
                                String name = firstName.getText().toString() + " " + middleName.getText().toString() + " " + lastName.getText().toString();

                                if (!TextUtils.isEmpty(poi_fName) || !TextUtils.isEmpty(poi_middleName) || !TextUtils.isEmpty(poi_lastName)) {
                                    Property property = new Property();
                                    List<ResourcePoiSync> resourcePoiSyncs = new ArrayList<>();
                                    ResourcePersonOfInterest poi = new ResourcePersonOfInterest();
                                    ResourcePoiSync resourcePoiSync1 = new ResourcePoiSync();
                                    ResourcePoiSync resourcePoiSync2 = new ResourcePoiSync();
                                    ResourcePoiSync resourcePoiSync3 = new ResourcePoiSync();
                                    ResourcePoiSync resourcePoiSync4 = new ResourcePoiSync();
                                    ResourcePoiSync resourcePoiSync5 = new ResourcePoiSync();
                                    ResourcePoiSync resourcePoiSync6 = new ResourcePoiSync();


                                    resourcePoiSync1.setId((long) 1);
                                    resourcePoiSync1.setValue(poi_fName);
                                    resourcePoiSyncs.add(resourcePoiSync1);
//                            property.setResPOI(resourcePoiSyncs);
//                            resourcePoiSyncsList.add(property);

                                    resourcePoiSync2.setId((long) 2);
                                    resourcePoiSync2.setValue(poi_middleName);
                                    resourcePoiSyncs.add(resourcePoiSync2);

                                    resourcePoiSync3.setId((long) 3);
                                    resourcePoiSync3.setValue(poi_lastName);
                                    resourcePoiSyncs.add(resourcePoiSync3);

                                    poi.setFeatureId(featureId);
                                    resourcePoiSync1
                                            .setFeatureId(featureId);
                                    if (genderSpinner.getSelectedItem() != null)
                                        poi.setGenderId(((Gender) genderSpinner.getSelectedItem()).getCode());
                                    resourcePoiSync4.setId((long) 6);
                                    resourcePoiSync4.setValue(((Gender) genderSpinner.getSelectedItem()).getName());
                                    resourcePoiSyncs.add(resourcePoiSync4);

                                    if (relSpinner.getSelectedItem() != null)
                                        poi.setRelationshipId(((RelationshipType) relSpinner.getSelectedItem()).getCode());
                                    resourcePoiSync5.setId((long) 5);
                                    resourcePoiSync5.setValue(((RelationshipType) relSpinner.getSelectedItem()).getName());
                                    resourcePoiSyncs.add(resourcePoiSync5);

                                    poi.setDob(txtDob.getText().toString());
                                    resourcePoiSync6.setId((long) 4);
                                    resourcePoiSync6.setValue(txtDob.getText().toString());
                                    resourcePoiSyncs.add(resourcePoiSync6);

                                    poi.setName(name);

                                    boolean result1 = DbController.getInstance(context).saveResPOIPropAttributes(resourcePoiSyncs, featureId);
                                    boolean result = DbController.getInstance(context).saveResPersonOfInterest(poi);

                                    if (result) {
                                        property.getResPersonOfInterests().add(poi);
                                        poiFragment.refresh();
                                        DbController db = DbController.getInstance(context);
                                        poiFragment.setPersons(db.getResPersonOfInterestsByProp(featureId), readOnly);
                                        msg = getResources().getString(R.string.AddedSuccessfully);
                                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                                    } else {
                                        warning = getResources().getString(R.string.UnableToSave);
                                        Toast.makeText(context, warning, Toast.LENGTH_LONG).show();
                                    }
                                    dialog.dismiss();
                                } else {
                                    msg = getResources().getString(R.string.enter_details);
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                                }
                            }

                        });

                        dialog.show();
                    }
                }
                else
                {
                    final Dialog dialog = new Dialog(context, R.style.DialogTheme);
                    dialog.setContentView(R.layout.dialog_person_of_interest);
                    dialog.setTitle(getResources().getString(R.string.nextKin));
                    dialog.getWindow().getAttributes().width = ViewGroup.LayoutParams.MATCH_PARENT;

                    Button save = (Button) dialog.findViewById(R.id.btn_ok);
                    final EditText firstName = (EditText) dialog.findViewById(R.id.editTextFirstName);
                    final EditText middleName = (EditText) dialog.findViewById(R.id.editTextMiddleName);
                    final EditText lastName = (EditText) dialog.findViewById(R.id.editTextLastName);
                    final Spinner genderSpinner = (Spinner) dialog.findViewById(R.id.spinnerGender);
                    final Spinner relSpinner = (Spinner) dialog.findViewById(R.id.spinnerRelationshipType);
                    final TextView txtDob = (TextView) dialog.findViewById(R.id.txtDob);
                    LinearLayout extraFields = (LinearLayout) dialog.findViewById(R.id.extraLayout);
                    extraFields.setVisibility(View.VISIBLE);

                    DbController db = DbController.getInstance(context);

                    genderSpinner.setAdapter(new ArrayAdapter(context, android.R.layout.simple_spinner_item, db.getGenders(true)));
                    relSpinner.setAdapter(new ArrayAdapter(context, android.R.layout.simple_spinner_item, db.getRelationshipTypes(true)));
                    ((ArrayAdapter) genderSpinner.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    ((ArrayAdapter) relSpinner.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    txtDob.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            GuiUtility.showDatePicker(txtDob, "");
                        }
                    });

                    save.setText(saveStr);

                    save.setOnClickListener(new View.OnClickListener() {
                        //Run when button is clicked
                        @Override
                        public void onClick(View v) {
                            String poi_fName = firstName.getText().toString();
                            String poi_middleName = middleName.getText().toString();
                            String poi_lastName = lastName.getText().toString();
                            String name = firstName.getText().toString() + " " + middleName.getText().toString() + " " + lastName.getText().toString();
                            ResourcePersonOfInterest poi = new ResourcePersonOfInterest();

                            if (!TextUtils.isEmpty(poi_fName) || !TextUtils.isEmpty(poi_middleName) || !TextUtils.isEmpty(poi_lastName)) {
                                Property property = new Property();
                                List<ResourcePoiSync> resourcePoiSyncs = new ArrayList<>();

                                ResourcePoiSync resourcePoiSync1 = new ResourcePoiSync();
                                ResourcePoiSync resourcePoiSync2 = new ResourcePoiSync();
                                ResourcePoiSync resourcePoiSync3 = new ResourcePoiSync();
                                ResourcePoiSync resourcePoiSync4 = new ResourcePoiSync();
                                ResourcePoiSync resourcePoiSync5 = new ResourcePoiSync();
                                ResourcePoiSync resourcePoiSync6 = new ResourcePoiSync();


                                resourcePoiSync1.setId((long) 1);
                                resourcePoiSync1.setValue(poi_fName);
                                resourcePoiSyncs.add(resourcePoiSync1);
//                            property.setResPOI(resourcePoiSyncs);
//                            resourcePoiSyncsList.add(property);

                                resourcePoiSync2.setId((long) 2);
                                resourcePoiSync2.setValue(poi_middleName);
                                resourcePoiSyncs.add(resourcePoiSync2);

                                resourcePoiSync3.setId((long) 3);
                                resourcePoiSync3.setValue(poi_lastName);
                                resourcePoiSyncs.add(resourcePoiSync3);

                                poi.setFeatureId(featureId);
                                resourcePoiSync1
                                        .setFeatureId(featureId);
                                if (genderSpinner.getSelectedItem() != null) {
                                    poi.setGenderId(((Gender) genderSpinner.getSelectedItem()).getCode());
                                    resourcePoiSync4.setId((long) 6);
                                    resourcePoiSync4.setValue(((Gender) genderSpinner.getSelectedItem()).getName());
                                    resourcePoiSyncs.add(resourcePoiSync4);
                                }

                                if (relSpinner.getSelectedItem() != null) {
                                    poi.setRelationshipId(((RelationshipType) relSpinner.getSelectedItem()).getCode());
                                    resourcePoiSync5.setId((long) 5);
                                    resourcePoiSync5.setValue(((RelationshipType) relSpinner.getSelectedItem()).getName());
                                    resourcePoiSyncs.add(resourcePoiSync5);
                                }

                                if (poi.getGenderId()==0 ||poi.getRelationshipId()==0) {
                                    //Toast.makeText(context,"Please Select the Gender",Toast.LENGTH_SHORT).show();
                                    msg = getResources().getString(R.string.enter_details);
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                                }else {


                                    poi.setDob(txtDob.getText().toString());
                                    resourcePoiSync6.setId((long) 4);
                                    resourcePoiSync6.setValue(txtDob.getText().toString());
                                    resourcePoiSyncs.add(resourcePoiSync6);

                                    poi.setName(name);

                                    boolean result = DbController.getInstance(context).saveResPersonOfInterest(poi);

                                    boolean result1 = DbController.getInstance(context).saveResPOIPropAttributes(resourcePoiSyncs, featureId);
                                    if (result) {
                                        property.getResPersonOfInterests().add(poi);
                                        poiFragment.refresh();
                                        DbController db = DbController.getInstance(context);
                                        poiFragment.setPersons(db.getResPersonOfInterestsByProp(featureId), readOnly);
                                        msg = getResources().getString(R.string.AddedSuccessfully);
                                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                                    } else {
                                        warning = getResources().getString(R.string.UnableToSave);
                                        Toast.makeText(context, warning, Toast.LENGTH_LONG).show();
                                    }

                                    dialog.dismiss();
                                }
                            } else {
                                msg = getResources().getString(R.string.enter_details);
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                            }
                        }

                    });

                    dialog.show();
                }


            }

        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
           }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        DbController db = DbController.getInstance(context);
        property = db.getProperty(featureId);
        personsFragment.refresh();
        listOwner=db.getResorceMultipleOwnerName(featureId);

        //personsFragment.setPersons(db.getPropAttributes(featureId), readOnly,tenureType);
        //personsFragment.setPersons(property.getAttributes(), readOnly,tenureType);
//        personsFragment.setPersons(property.getAttributes(), readOnly,tenureType,featureId,iOwnerCount);
        personsFragment.setPersons(listOwner, readOnly,tenureType,featureId,iOwnerCount);
        poiFragment.setPersons(property.getResPersonOfInterests(), readOnly);
    }

    public boolean checkSingleOccupancyType(int personSubType, int ownerCount) {
        boolean flag = false;
        if (ownerCount == 0) {
            //allow
            if (personSubType == Person.SUBTYPE_OWNER) {
                return flag = true;
            } else if (personSubType == Person.SUBTYPE_ADMINISTRATOR || personSubType == Person.SUBTYPE_GUARDIAN) {
                msg = getResources().getString(R.string.can_not_add_adminGuardian_in_SingleOccuapncy);
                cf.showMessage(context, infoStr, msg);
                return flag = false;
            }
        } else if (ownerCount > 0) {
            if (personSubType == Person.SUBTYPE_OWNER) {
                msg = getResources().getString(R.string.can_not_add_more_than_one_owner_in_SingleOccuapncy);
                cf.showMessage(context, infoStr, msg);
                return flag = false;
            } else if (personSubType == Person.SUBTYPE_ADMINISTRATOR || personSubType == Person.SUBTYPE_GUARDIAN) {
                msg = getResources().getString(R.string.can_not_add_adminGuardian_in_SingleOccuapncy);
                cf.showMessage(context, infoStr, msg);
                return flag = false;
            }
        }
        return flag;
    }

    public boolean checkMultipleOcuupany_Joint(int personSubType, int ownerCount)   //Only 2 owner can be added in case of Multiple Occupancy(Joint Tenancy)
    {
        boolean flag = false;
        if (ownerCount <= 1) {
            //allow
            if (personSubType == Person.SUBTYPE_OWNER) {
                return flag = true;
            } else if (personSubType == Person.SUBTYPE_ADMINISTRATOR || personSubType == Person.SUBTYPE_GUARDIAN) {
                msg = getResources().getString(R.string.can_not_add_adminGuardian_in_multipleOccuapncy_joint);
                cf.showMessage(context, infoStr, msg);
                return flag = false;
            }
        } else if (ownerCount == 2) {
            if (personSubType == Person.SUBTYPE_OWNER) {
                msg = getResources().getString(R.string.can_not_add_more_than_two_owners_in_multipleOccuapncy_joint);
                cf.showMessage(context, infoStr, msg);
                return flag = false;
            } else if (personSubType == Person.SUBTYPE_ADMINISTRATOR || personSubType == Person.SUBTYPE_GUARDIAN) {
                msg = getResources().getString(R.string.can_not_add_adminGuardian_in_multipleOccuapncy_joint);
                cf.showMessage(context, infoStr, msg);
                return flag = false;
            }
        }
        return flag;
    }

    public boolean checkMultipleOcuupany_TenancyInCommon(int personSubType)   //more than 2 owner only can be added in case of Multiple Occupancy(Tenancy in Common)
    {
        boolean flag = false;
        //allow
        if (personSubType == Person.SUBTYPE_OWNER) {
            return flag = true;
        } else if (personSubType == Person.SUBTYPE_ADMINISTRATOR || personSubType == Person.SUBTYPE_GUARDIAN) {
            msg = getResources().getString(R.string.can_not_add_adminGuardian_in_multipleOccuapncy_common);
            cf.showMessage(context, infoStr, msg);
            return flag = false;
        }
        return flag;
    }

    public boolean checkOccupancyType_GuardianMinor(int personSubType, int ownerCount)   //more than 2 owner only can be added in case of Multiple Occupancy(Tenancy in Common)
    {
        int minorCount = property.getRight().getMinorCount();
        int guardianCount = property.getRight().getGuardianCount();
        boolean flag = false;
        if (personSubType == Person.SUBTYPE_OWNER) {
            return flag = true;
        } else if (personSubType == Person.SUBTYPE_ADMINISTRATOR) {
            msg = getResources().getString(R.string.can_not_add_admin_in_cas_of_guardian_minor);
            cf.showMessage(context, infoStr, msg);
            return flag = false;
        } else if (personSubType == Person.SUBTYPE_GUARDIAN) {
            if (ownerCount >= 1) {
                if (guardianCount < ownerCount && guardianCount < 2) {
                    return flag = true;
                } else if (guardianCount == 2) {
                    msg = getResources().getString(R.string.guardian_can_not_more_than_two);
                    cf.showMessage(context, infoStr, msg);       //
                    return false;
                }
            } else {
                msg = getResources().getString(R.string.add_owner_first);
                cf.showMessage(context, infoStr, msg);
                return flag = false;
            }
        }
        return flag;
    }
}
