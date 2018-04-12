package com.rmsi.android.mast.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rmsi.android.mast.adapter.AttributeAdapter;
import com.rmsi.android.mast.adapter.ResourceAttributeAdapter;
import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.Option;
import com.rmsi.android.mast.domain.Property;
import com.rmsi.android.mast.domain.ResourceCustomAttribute;
import com.rmsi.android.mast.domain.TenureInformation;
import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.GuiUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ambar.srivastava on 12/22/2017.
 */

public class CaptureTenureInfo extends ActionBarActivity {
    private Long featureId = 0L;
    private boolean isDispute = false;
    private List<Attribute> attributes;
    private ListView listView;
    private final Context context = this;
    private ResourceAttributeAdapter adapterList;
    private Button btnSave, btnBack;
    private CommonFunctions cf = CommonFunctions.getInstance();
    private boolean readOnly = false;
    private String classi,subClassi,tenureType,tenureID,subID;


    private static boolean keyboardHidden = true;
    private static int reduceHeight = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Initializing context in common functions in case of a crash
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


                // Try to get list of attributes of general type
//                if (tenureType.equalsIgnoreCase("Private (jointly)")){
//                    attributes = db.getJointAttributesByFlag(tenureID);
//                }else{
//                    attributes = db.getAttributesByFlag(tenureID);
//                }
                if (tenureType.equalsIgnoreCase("Private (jointly)")){
                    attributes = db.getOwner1PropAttributesByType(featureId, tenureID);
                    if (attributes.size() < 1) {
                        attributes = db.getJointAttributesByFlag(tenureID);
                    }
                }else {
                    attributes = db.getPropAttributesByType(featureId, tenureID);
                    if (attributes.size() < 1) {
                        attributes = db.getAttributesByFlag(tenureID);
                    }
                }
                //attributes = db.getAttributesByFlag(tenureID);
            }

        readOnly = CommonFunctions.isFeatureReadOnly(featureId);

        setContentView(R.layout.capture_tenure_information);

        listView = (ListView) findViewById(android.R.id.list);
//        InputMethodManager imm =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(listView.getWindowToken(), 1);
        TextView emptyText = (TextView) findViewById(android.R.id.empty);
        listView.setEmptyView(emptyText);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (tenureType.equalsIgnoreCase("Private (jointly)")){
            toolbar.setTitle("Owner 1");
        }else{
            toolbar.setTitle("Tenure Details, "+tenureType);
            //toolbar.setTitle("Tenure Classification");
        }




        if (toolbar != null)
            setSupportActionBar(toolbar);

        try {
            adapterList = new ResourceAttributeAdapter(context, attributes, readOnly);
        } catch (Exception e) {
            e.printStackTrace();
        }
        listView.setAdapter(adapterList);

        //I resize my listView according to the height of the displayed softKeyboard and resize it back once the keyboard is gone.
        final View decorView = this.getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                decorView.getWindowVisibleDisplayFrame(rect);

                int displayHeight = rect.bottom - rect.top;
                int height = decorView.getHeight();
                boolean keyboardHiddenTemp = (double)displayHeight / height > 0.8 ;
                int mylistviewHeight = listView.getMeasuredHeight();

                if (keyboardHiddenTemp != keyboardHidden) {
                    keyboardHidden = keyboardHiddenTemp;

                    if (!keyboardHidden) {

                        reduceHeight = height - displayHeight;

                        LinearLayout.LayoutParams mParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,mylistviewHeight - reduceHeight);
                        listView.setLayoutParams(mParam);
                        listView.requestLayout();

                    } else {

                        LinearLayout.LayoutParams mParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,mylistviewHeight + reduceHeight);
                        listView.setLayoutParams(mParam);
                        listView.requestLayout();


                    }
                }

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
        if (validate()) {
            try {
                boolean saveResult = DbController.getInstance(context).savePropAttributes(attributes, featureId);


                if (saveResult) {
                    cf.showToast(context, R.string.data_saved, Toast.LENGTH_SHORT);
                    finish();

                } else {
                    cf.showToast(context, R.string.unable_to_save_data, Toast.LENGTH_SHORT);
                }
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
                cf.showToast(context, R.string.unable_to_save_data, Toast.LENGTH_SHORT);
            }
        } else {
            cf.showToast(context, R.string.fill_mandatory, Toast.LENGTH_SHORT);
        }
    }

//    private void saveData() {
//        if (validate()) {
//            try {
//                boolean saveResult = DbController.getInstance(context).savePropAttributes(attributes, featureId);
//
//                Long a=attributes.get(0).getGroupId();
//
//                if (saveResult) {
//                    cf.showToast(context, R.string.data_saved, Toast.LENGTH_SHORT);
//
//                    if (tenureType.equalsIgnoreCase("Private (jointly)")){
//                        Intent intent=new Intent(context,Owner2.class);
//                        intent.putExtra("featureid", featureId);
//                        intent.putExtra("grpID", a);
//                        intent.putExtra("classi", classi);
//                        intent.putExtra("subclassi", subClassi);
//                        intent.putExtra("tenure", tenureType);
//                        intent.putExtra("tID",tenureID);
//                        intent.putExtra("sID",subID);
//                        startActivity(intent);
//                    }else{
//                        DbController db = DbController.getInstance(context);
//                        List<ResourceCustomAttribute> attributesSize = db.getResAttributesSize(tenureID);
//                        if (attributesSize.size()>0) {
//                            Intent intent = new Intent(context, CustomAttributeChange.class);
//                            intent.putExtra("featureid", featureId);
//                            intent.putExtra("classi", classi);
//                            intent.putExtra("subclassi", subClassi);
//                            intent.putExtra("tenure", tenureType);
//                            intent.putExtra("tID",tenureID);
//                            intent.putExtra("sID", subID);
//                            startActivity(intent);
//                        }else{
//
//                            Intent intent=new Intent(CaptureTenureInfo.this,CollectedResourceDataSummary.class);
//                            intent.putExtra("featureid", featureId);
//                            intent.putExtra("classi", classi);
//                            intent.putExtra("subclassi", subClassi);
//                            intent.putExtra("tID",tenureID);
//                            intent.putExtra("tenure", tenureType);
//                            startActivity(intent);
//                        }
//                    }
//
//
//
//                } else {
//                    cf.showToast(context, R.string.unable_to_save_data, Toast.LENGTH_SHORT);
//                }
//            } catch (Exception e) {
//                cf.appLog("", e);
//                e.printStackTrace();
//                cf.showToast(context, R.string.unable_to_save_data, Toast.LENGTH_SHORT);
//            }
//        } else {
//            cf.showToast(context, R.string.fill_mandatory, Toast.LENGTH_SHORT);
//        }
//    }

    public boolean validate() {
        return GuiUtility.validateAttributes(attributes, true);
    }
//
//        if (!property.validateTenureInfo(context, true)) {
//            return false;
//        }
//        //boolean saveResult = DbController.getInstance(context).saveAttributesListValues(attributes);
//        boolean saveResult = DbController.getInstance(context).insertTenureResourceAtrr(property,featureId);
//
//        if (saveResult==true){
//            Toast.makeText(context,"DATA SAVE Successfully",Toast.LENGTH_SHORT).show();
//            Intent intent=new Intent(context,CoustomAttribute.class);
//            intent.putExtra("featureid", featureId);
//            intent.putExtra("classi", classi);
//            intent.putExtra("subclassi", subClassi);
//            intent.putExtra("tenure", tenureType);
//            startActivity(intent);
//        }
//        else {
//            Toast.makeText(context,"Unable to Save Data",Toast.LENGTH_SHORT).show();
//        }
//        return saveResult;
//    }


}
