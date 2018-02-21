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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rmsi.android.mast.adapter.AttributeAdapter;
import com.rmsi.android.mast.adapter.ResourceAttributeAdapter;
import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.ResourceCustomAttribute;
import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.GuiUtility;

import java.util.List;

/**
 * Created by Ambar.Srivastava on 1/9/2018.
 */

public class Owner2 extends ActionBarActivity {
    private Long featureId = 0L;
    private Long grpID=0L;
    private boolean isDispute = false;
    private List<Attribute> attributes;
    private ListView listView;
    private final Context context = this;
    private ResourceAttributeAdapter adapterList;
    private Button btnSave, btnBack;
    private CommonFunctions cf = CommonFunctions.getInstance();
    private boolean readOnly = false;
    private String classi, subClassi, tenureType, tenureID, subID;


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
            classi = extras.getString("classi");
            subClassi = extras.getString("subclassi");
            tenureType = extras.getString("tenure");
            tenureID = extras.getString("tID");
            subID = extras.getString("sID");
            grpID=extras.getLong("grpID");

//            attributes = db.getOwner2PropAttributesByType(featureId, tenureID);
//            Long groupID=attributes.get(0).getGroupId();
//            if (groupID!=null) {
//                // Try to get list of attributes of general type
//                attributes = db.getJointOwn2AttributesByFlag(tenureID);
////                if (tenureType.equalsIgnoreCase("Private (jointly)")) {
////                    attributes = db.getJointOwn2AttributesByFlag(tenureID);
////                } else {
////                    attributes = db.getAttributesByFlag(tenureID);
////                }
//                //attributes = db.getAttributesByFlag(tenureID);
//            }
//        }

            attributes = db.getOwner2PropAttributesByType(featureId, tenureID);
            if (attributes.size() < 1) {
                attributes = db.getJointOwn2AttributesByFlag(tenureID);
            }

        }
        readOnly = CommonFunctions.isFeatureReadOnly(featureId);

        setContentView(R.layout.capture_tenure_information);

        listView = (ListView) findViewById(android.R.id.list);
        TextView emptyText = (TextView) findViewById(android.R.id.empty);
        listView.setEmptyView(emptyText);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("Owner 2");

        if (toolbar != null)
            setSupportActionBar(toolbar);

        try {
            adapterList = new ResourceAttributeAdapter(context, attributes, readOnly);
        } catch (Exception e) {
            e.printStackTrace();
        }
        listView.setAdapter(adapterList);

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

//                    DbController db = DbController.getInstance(context);
//                    List<ResourceCustomAttribute> attributesSize = db.getResAttributesSize(tenureID);
//                    if (attributesSize.size()>0) {
//                        Intent intent = new Intent(context, CustomAttributeChange.class);
//                        intent.putExtra("featureid", featureId);
//                        intent.putExtra("classi", classi);
//                        intent.putExtra("subclassi", subClassi);
//                        intent.putExtra("tenure", tenureType);
//                        intent.putExtra("sID", subID);
//                        intent.putExtra("tID",tenureID);
//                        startActivity(intent);
//                    }else{
//
//                        Intent intent=new Intent(Owner2.this,CollectedResourceDataSummary.class);
//                        intent.putExtra("featureid", featureId);
//                        intent.putExtra("classi", classi);
//                        intent.putExtra("subclassi", subClassi);
//                        intent.putExtra("tenure", tenureType);
//                        startActivity(intent);
//                    }
//                    Intent intent = new Intent(context, CoustomAttribute.class);
//                    intent.putExtra("featureid", featureId);
//                    intent.putExtra("classi", classi);
//                    intent.putExtra("subclassi", subClassi);
//                    intent.putExtra("tenure", tenureType);
//                    intent.putExtra("sID", subID);
//                    intent.putExtra("tID",tenureID);
//                    startActivity(intent);


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