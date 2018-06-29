package com.rmsi.android.mast.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rmsi.android.mast.adapter.AttributeResAdapter;
import com.rmsi.android.mast.adapter.CustomAttributeAdapter;
import com.rmsi.android.mast.adapter.ResourceCustomChangeAdapter;
import com.rmsi.android.mast.adapter.SpinnerAdapter;
import com.rmsi.android.mast.adapter.SummaryAdapater;
import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.Option;
import com.rmsi.android.mast.domain.ResourceCustomAttribute;
import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.DateUtility;
import com.rmsi.android.mast.util.KeyboardUtil;
import com.rmsi.android.mast.util.ResGuiUtility;
import com.rmsi.android.mast.util.StringUtility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

// Created by Ambar.Srivastava on 1/11/2018.



public class CustomAttributeChange extends ActionBarActivity {

    private Long featureId = 0L;
    private boolean isDispute = false;
    private List<ResourceCustomAttribute> attributes;
    private List<ResourceCustomAttribute> attributesres;
    private List<ResourceCustomAttribute> attributesresSize;
    private List<Integer> attributeListCount=new ArrayList<>();
    private List<ResourceCustomAttribute> attributeList=new ArrayList<>();
    private List<ResourceCustomAttribute> attributeListsize=new ArrayList<>();
    private List<ResourceCustomAttribute> attributeListSub=new ArrayList<>();
    private List<ResourceCustomAttribute> attributeListSubCheck=new ArrayList<>();
    private List<ResourceCustomAttribute> attributesFeatureID;
    private List<ResourceCustomAttribute> attributesresFeatureID;

    private Spinner spinnerRes,spinnerSub;
    private final Context context = this;
    private ListView listViewres,listviewsub;
    private AttributeResAdapter adapterList;
    private AttributeResAdapter adapterListRes;
    private ResourceCustomChangeAdapter customAttributeAdapterResource,customAttributeAdapterSubclassification;
    private TextView textViewSub,textViewRes;
    private EditText editTextSub,editTextRes;
    private Button btnSave, btnBack;

    private CommonFunctions cf = CommonFunctions.getInstance();
    private boolean readOnly = false;
    private String classi,subClassi,tenureType,tenureID,subID;
    private LinearLayout linearLayoutSub,linearLayoutRes;
    private KeyboardUtil keyboardUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            CommonFunctions.getInstance().Initialize(getApplicationContext());
        } catch (Exception e) {
        }
        cf.loadLocale(getApplicationContext());

        DbController db = DbController.getInstance(context);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            featureId = extras.getLong("featureid");
            classi=extras.getString("classi");
            subClassi=extras.getString("subclassi");
            tenureType=extras.getString("tenure");
            tenureID=extras.getString("tID");
            subID=extras.getString("sID");

//            attributes = db.getPropResAttributesByType(featureId, tenureID,subID);
            attributes = db.getPropResAttributesByType(featureId, tenureID,subID);
            if (attributes.size() < 1) {
                // Try to get list of attributes of general type
                attributes = db.getResAttributesByAttrbuteID(tenureID,subID);
//                attributeListsize.addAll(attributes);
            }

            String res="null";
//            attributesres = db.getPropResAttributesByType(featureId, tenureID,res);
            attributesFeatureID = DbController.getInstance(context).getResourceCustomInfoCustom(featureId, "null");
            if (attributesFeatureID.size() < 1) {
                // Try to get list of attributes of general type
                attributesres = db.getResAttributesByAttrbuteIDNull(tenureID,res);


            }
        }


        readOnly = CommonFunctions.isFeatureReadOnly(featureId);

        setContentView(R.layout.custom_attribute_change);
        linearLayoutSub= (LinearLayout) findViewById(R.id.visible2);
        textViewSub= (TextView) findViewById(R.id.labelSub);
        editTextSub= (EditText) findViewById(R.id.editSub);

        linearLayoutRes= (LinearLayout) findViewById(R.id.visible1);
        textViewRes= (TextView) findViewById(R.id.labelRes);
        editTextRes= (EditText) findViewById(R.id.editRes);

        listViewres = (ListView) findViewById(R.id.list);
        spinnerRes = (Spinner) findViewById(R.id.resCustomAttribute);
//        TextView emptyText = (TextView) findViewById(R.id.empty);
//        spinnerRes.setEmptyView(emptyText);

        listviewsub = (ListView) findViewById(R.id.list1);
        spinnerSub = (Spinner) findViewById(R.id.subCustomAttribute);
        TextView emptyText1 = (TextView) findViewById(R.id.empty1);
        spinnerSub.setEmptyView(emptyText1);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Custom Attribute");

        if (toolbar != null)
            setSupportActionBar(toolbar);

        //subAttritubute
//        for(ResourceCustomAttribute resourceCustomAttribute : attributes) {
//            createViewFromAttribute(resourceCustomAttribute,  readOnly);
//        }

        //ResourceAttribute
        if (attributesres!=null) {

            for (int i = 0; i < attributesres.size(); i++) {

                for (int j = 0; j < attributesres.get(i).getOptionsList().size(); j++) {
                    if (!attributesres.get(i).getOptionsList().get(j).getName().equalsIgnoreCase("Select custom attribute")) {
                        ResourceCustomAttribute resourceCustomAttribute = new ResourceCustomAttribute();
                        resourceCustomAttribute.setName(attributesres.get(i).getOptionsList().get(j).getName().toString());
                        resourceCustomAttribute.setId(attributesres.get(i).getOptionsList().get(j).getId());
                        resourceCustomAttribute.setResID(attributesres.get(i).getOptionsList().get(j).getOptionID());
                        attributeList.add(resourceCustomAttribute);
                    }

                }

            }
        }

        if (attributeList.size()!=0) {
            customAttributeAdapterResource = new ResourceCustomChangeAdapter(context, attributeList, readOnly);
            View footerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footerlayout, null, false);
            listViewres.addFooterView(footerView);
            listViewres.setAdapter(customAttributeAdapterResource);

            keyboardUtil = new KeyboardUtil(CustomAttributeChange.this, footerView);
        }


        if (attributesFeatureID.size() != 0) {
            attributeList.addAll(attributesFeatureID);
            customAttributeAdapterResource = new ResourceCustomChangeAdapter(context, attributeList, readOnly);
            View footerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footerlayout, null, false);
            listViewres.addFooterView(footerView);
            listViewres.setAdapter(customAttributeAdapterResource);
            keyboardUtil = new KeyboardUtil(CustomAttributeChange.this, footerView);
        }




//        for(ResourceCustomAttribute resourceCustomAttribute : attributesres) {
//
//            createViewResAttribute(resourceCustomAttribute,  readOnly);
//        }
    }

    public void createViewResAttribute(ResourceCustomAttribute attribute,  boolean readOnly) {


        if (attribute.getControlType() == Attribute.CONTROL_TYPE_SPINNER) {

            createSpinnerResViewFromArray( attribute, readOnly);
        }


    }


    //Ambar
    private void createSpinnerResViewFromArray(final ResourceCustomAttribute attribute, final boolean readOnly) {


        spinnerRes.setPrompt(attribute.getName());
        spinnerRes.setTag(attribute.getId());

        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(
                context,
                android.R.layout.simple_spinner_item,
                attribute.getOptionsList());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRes.setAdapter(spinnerAdapter);

        if (readOnly) {
            spinnerRes.setEnabled(false);
        }

        String fieldValue = attribute.getValue();

        if (!StringUtility.isEmpty(fieldValue) && !fieldValue.equalsIgnoreCase("select custom attribute")) {
//            int currentValue = Integer.parseInt(fieldValue);
//            spinnerRes.setSelection(spinnerAdapter.getPosition(currentValue));
            List<Option> list=attribute.getOptionsList();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getName().equalsIgnoreCase(StringUtility.empty(fieldValue))) {


                    spinnerRes.setSelection(i);
                    break;
                }
            }
        }

        bindActionOnSpinnerChange(spinnerRes, new Runnable() {
            @Override
            public void run() {



                ResourceCustomAttribute resourceCustomAttribute=new ResourceCustomAttribute();
                Option selecteditem = (Option) spinnerRes.getSelectedItem();

                attributesFeatureID = DbController.getInstance(context).getResourceCustomInfoCustom(featureId, "null");
                if (attributesFeatureID.size() != 0) {
                    customAttributeAdapterResource = new ResourceCustomChangeAdapter(context, attributesFeatureID, readOnly);
                    listViewres.setAdapter(customAttributeAdapterResource);
                }

                String Subvalue=selecteditem.getName().toString();

                if (!selecteditem.getName().toString().equalsIgnoreCase("Select custom attribute")) {

                    //Case to find whether it's an Add event or Edit event
                    boolean isAddCase = false;
                    isAddCase = cf.IsEditResourceAttribute(featureId, tenureType);
                    //------
                    if (isAddCase) {
                        if (attributeList.size() == 0) {
                            DbController db = DbController.getInstance(context);
                            String res = "null";
                            attributesFeatureID = db.getResourceCustomInfoCustom(featureId, res);
                            if (attributesFeatureID.size() != 0) {
                                customAttributeAdapterResource = new ResourceCustomChangeAdapter(context, attributesFeatureID, readOnly);
                                listViewres.setAdapter(customAttributeAdapterResource);
                            } else {
                                resourceCustomAttribute.setName(selecteditem.getName().toString());
                                resourceCustomAttribute.setId(selecteditem.getId());
                                resourceCustomAttribute.setResID(selecteditem.getOptionID());
                                attributeList.add(resourceCustomAttribute);

                                customAttributeAdapterResource = new ResourceCustomChangeAdapter(context, attributeList, readOnly);
                                listViewres.setAdapter(customAttributeAdapterResource);
                            }
                        }
                        for (int i = 0; i < attributeList.size(); i++) {

                            String strFlag = "True";
                            for (int j = 0; j < attributeList.size(); j++) {

                                if (attributeList.get(j).getName().equalsIgnoreCase(Subvalue)) {
                                    Toast.makeText(context, "Please Select another Option", Toast.LENGTH_SHORT).show();
                                    strFlag = "False";
                                    break;
                                }

                            }

                            if (strFlag == "False") {
                                break;
                            }


                            if (attributeList.get(i).getName().equalsIgnoreCase(Subvalue)) {
                                Toast.makeText(context, "Please Select another Option", Toast.LENGTH_SHORT).show();
                                break;
                            } else {
                                resourceCustomAttribute.setName(selecteditem.getName().toString());
                                resourceCustomAttribute.setId(selecteditem.getId());
                                resourceCustomAttribute.setResID(selecteditem.getOptionID());
                                attributeList.add(resourceCustomAttribute);


//
                                DbController db = DbController.getInstance(context);
                                String res = "null";
                                attributesFeatureID = db.getResourceCustomInfoCustom(featureId, res);
                                if (attributesFeatureID.size() != 0) {
                                    customAttributeAdapterResource = new ResourceCustomChangeAdapter(context, attributesFeatureID, readOnly);
                                    listViewres.setAdapter(customAttributeAdapterResource);
                                } else {
//

                                    customAttributeAdapterResource = new ResourceCustomChangeAdapter(context, attributeList, readOnly);
                                    listViewres.setAdapter(customAttributeAdapterResource);
                                }

                            }
                        }

                    } else {
                        if (attributeList.size() == 0) {
                            DbController db = DbController.getInstance(context);
                            String res = "null";
                            attributesFeatureID = db.getResourceCustomInfoCustom(featureId, res);
                            if (attributesFeatureID.size() != 0) {
                                customAttributeAdapterResource = new ResourceCustomChangeAdapter(context, attributesFeatureID, readOnly);
                                listViewres.setAdapter(customAttributeAdapterResource);
//                                attributeList.addAll(attributesFeatureID);

                            } else {
                                resourceCustomAttribute.setName(selecteditem.getName().toString());
                                resourceCustomAttribute.setId(selecteditem.getId());
                                resourceCustomAttribute.setResID(selecteditem.getOptionID());
                                attributeList.add(resourceCustomAttribute);

                                customAttributeAdapterResource = new ResourceCustomChangeAdapter(context, attributeList, readOnly);
                                listViewres.setAdapter(customAttributeAdapterResource);
                            }
                        } else {
                            String strIsNewAttribue = "True";
                            //------
                            for (int i = 0; i < attributeList.size(); i++) {

                                String strFlag = "True";
                                for (int j = 0; j < attributeList.size(); j++) {

                                    if (attributeList.get(j).getName().equalsIgnoreCase(Subvalue)) {
                                        Toast.makeText(context, "Please Select another Option", Toast.LENGTH_SHORT).show();
                                        strFlag = "False";
                                        strIsNewAttribue = "False";
                                        break;
                                    }

                                }

                                if (strFlag == "False") {
                                    strIsNewAttribue = "False";
                                    break;
                                }


                                if (attributeList.get(i).getName().equalsIgnoreCase(Subvalue)) {
                                    Toast.makeText(context, "Please Select another Option", Toast.LENGTH_SHORT).show();
                                    strIsNewAttribue = "False";
                                    break;
                                }
                            }
                            //------
                            if (strIsNewAttribue == "True") {
                                resourceCustomAttribute.setName(selecteditem.getName().toString());
                                resourceCustomAttribute.setId(selecteditem.getId());
                                resourceCustomAttribute.setResID(selecteditem.getOptionID());

                                attributeList.add(resourceCustomAttribute);

                                customAttributeAdapterResource = new ResourceCustomChangeAdapter(context, attributeList, readOnly);


                                listViewres.setAdapter(customAttributeAdapterResource);
                            } else {
                                Toast.makeText(context, "Please Select another Option", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }

                }
                // This line is to set Select an custom attribute again, once text area is done.
                spinnerRes.setSelection(0);

            }
        });

    }


    public void createViewFromAttribute(ResourceCustomAttribute attribute,  boolean readOnly) {


            if (attribute.getControlType() == Attribute.CONTROL_TYPE_SPINNER) {

            createSpinnerViewFromArray( attribute, readOnly);
        }


    }


    //Ambar
    private void createSpinnerViewFromArray(final ResourceCustomAttribute attribute, final boolean readOnly) {


        spinnerSub.setPrompt(attribute.getName());
        spinnerSub.setTag(attribute.getId());

        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(
                context,
                android.R.layout.simple_spinner_item,
                attribute.getOptionsList());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSub.setAdapter(spinnerAdapter);

        if (readOnly) {
            spinnerSub.setEnabled(false);
        }

        String fieldValue = attribute.getValue();

        if (!StringUtility.isEmpty(fieldValue) && !fieldValue.equalsIgnoreCase("Select an option")) {

            List<Option> list=attribute.getOptionsList();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getName().equalsIgnoreCase(StringUtility.empty(fieldValue))) {


                    spinnerSub.setSelection(i);
                    break;
                }
            }
        }

        bindActionOnSpinnerChange(spinnerSub, new Runnable() {
            @Override
            public void run() {


                ResourceCustomAttribute resourceCustomAttribute=new ResourceCustomAttribute();
                Option selecteditem = (Option) spinnerSub.getSelectedItem();

                String Subvalue=selecteditem.getName().toString();

                if (!Subvalue.equalsIgnoreCase("Select custom attribute")) {
                    if (attributeListSub.size() == 0) {
                        DbController db = DbController.getInstance(context);

                        attributesFeatureID = db.getResourceCustomInfoCustom(featureId, subID);
                        if (attributesFeatureID.size() != 0) {
                            customAttributeAdapterSubclassification = new ResourceCustomChangeAdapter(context, attributesFeatureID, readOnly);
                            listviewsub.setAdapter(customAttributeAdapterSubclassification);
                        } else {
                            resourceCustomAttribute.setName(selecteditem.getName().toString());
                            resourceCustomAttribute.setId(selecteditem.getId());
                            resourceCustomAttribute.setResID(selecteditem.getOptionID());
                            attributeListSub.add(resourceCustomAttribute);

                            customAttributeAdapterSubclassification = new ResourceCustomChangeAdapter(context, attributeListSub, readOnly);
                            listviewsub.setAdapter(customAttributeAdapterSubclassification);
                        }
                    }
                    for (int i = 0; i < attributeListSub.size(); i++) {

                        String strFlag = "True";
                        for (int j = 0; j < attributeListSub.size(); j++) {

                            if (attributeListSub.get(j).getName().equalsIgnoreCase(Subvalue)) {
                                Toast.makeText(context, "Please Select another Option", Toast.LENGTH_SHORT).show();
                                strFlag = "False";
                                break;
                            }

                        }

                        if (strFlag == "False") {
                            break;
                        }

                        if (attributeListSub.get(i).getName().equalsIgnoreCase(Subvalue)) {
                            Toast.makeText(context, "Please Select another Option", Toast.LENGTH_SHORT).show();
                            break;
                        } else {
                            resourceCustomAttribute.setName(selecteditem.getName().toString());
                            resourceCustomAttribute.setId(selecteditem.getId());
                            resourceCustomAttribute.setResID(selecteditem.getOptionID());
                            attributeListSub.add(resourceCustomAttribute);



                            DbController db = DbController.getInstance(context);

                            attributesFeatureID = db.getResourceCustomInfoCustom(featureId, subID);
                            if (attributesFeatureID.size() != 0) {
                                customAttributeAdapterSubclassification = new ResourceCustomChangeAdapter(context, attributesFeatureID, readOnly);
                                listviewsub.setAdapter(customAttributeAdapterSubclassification);
                            } else {
//

                                customAttributeAdapterSubclassification = new ResourceCustomChangeAdapter(context, attributeListSub, readOnly);
                                listviewsub.setAdapter(customAttributeAdapterSubclassification);
                            }

                        }
                    }

                    // This line is to set Select an custom attribute again, once text area is done.
                    spinnerSub.setSelection(0);
                }
            }
        });

    }

    private void createLabelSub(String label, String id) {
        linearLayoutSub.setVisibility(View.VISIBLE);
        textViewSub.setText(label);


    }


    public static void bindActionOnSpinnerChange(Spinner spinner, final Runnable action) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                action.run();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }



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
//        if (id == android.R.id.home) {
//            finish();
//        }
        return super.onOptionsItemSelected(item);
    }

    private void saveData() {

       // attributeList.clear();


        for (ResourceCustomAttribute resourceCustomAttribute: attributeList ){
            if (resourceCustomAttribute.getValue()==null){
                Toast.makeText(context,"Please Select the Value of "+resourceCustomAttribute.getName(),Toast.LENGTH_SHORT).show();
                return;
            }

            if (resourceCustomAttribute.getValue().equalsIgnoreCase("")){
                Toast.makeText(context,"Please Select the Value of "+resourceCustomAttribute.getName(),Toast.LENGTH_SHORT).show();
                return;
            }

            if (resourceCustomAttribute.getValue().equalsIgnoreCase("Select an option") ){
                Toast.makeText(context,"Please Select the Value of "+resourceCustomAttribute.getName(),Toast.LENGTH_SHORT).show();
                return;
            }

        }



        if (attributeList.size()!=0) {
            if (attributeList.size() != 9) {
                Toast.makeText(context, "Please select all the value", Toast.LENGTH_SHORT).show();
                return;
            }
        }

            try {

                boolean deleteData=DbController.getInstance(context).deleteCustomResource(featureId);
                //boolean saveResult1 = DbController.getInstance(context).saveResPropAttributes(attributesres, featureId);
                boolean saveResult1 = DbController.getInstance(context).saveResPropAttributes(attributeList, featureId);
               // boolean saveResult = DbController.getInstance(context).saveResPropAttributes(attributeListSub, featureId);


                if ( saveResult1) {
                    cf.showToast(context, R.string.data_saved, Toast.LENGTH_SHORT);

                    Intent intent=new Intent(CustomAttributeChange.this,CollectedResourceDataSummary.class);
                    intent.putExtra("featureid", featureId);
                    intent.putExtra("classi", classi);
                    intent.putExtra("subclassi", subClassi);
                    intent.putExtra("tenure", tenureType);
                    finish();
                    startActivity(intent);

                } else {
                    cf.showToast(context, R.string.unable_to_save_data, Toast.LENGTH_SHORT);
                }
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
                cf.showToast(context, R.string.unable_to_save_data, Toast.LENGTH_SHORT);
            }

//        } else {
//            cf.showToast(context, R.string.fill_mandatory, Toast.LENGTH_SHORT);
//        }

    }

    public boolean validate() {
        return ResGuiUtility.validateAttributes(attributes, true) && ResGuiUtility.validateAttributes(attributesres,true);
    }
}
