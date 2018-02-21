package com.rmsi.android.mast.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.rmsi.android.mast.adapter.ResourceAttributeAdapter;
import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.GuiUtility;
import com.rmsi.android.mast.util.KeyboardUtil;

import java.util.List;

/**
 * Created by Ambar.Srivastava on 1/22/2018.
 */

public class Owner extends ActionBarActivity {
    private Long featureId = 0L;
    private List<Attribute> attributes;
    private ListView listView;
    private int iCountOwner = 0;
    private int groupId = 0;
    private final Context context = this;
    private ResourceAttributeAdapter adapterList;
    private Button btnSave, btnBack;
    private CommonFunctions cf = CommonFunctions.getInstance();
    private boolean readOnly = false;
    private String classi, subClassi, tenureType, tenureID, subID;
    private ScrollView scrollView;
    private boolean isActveKeyboard = false;
    private LinearLayout linearLayout;

    private static boolean keyboardHidden = true;
    private static int reduceHeight = 0;

    private KeyboardUtil keyboardUtil;


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
            iCountOwner = extras.getInt("ownerCount");
            groupId = extras.getInt("groupid");
            iCountOwner = iCountOwner + 1;


            //Case to find whether it's an Add event or Edit event
            boolean isAddCase = false;
            isAddCase = cf.IsEditResourceAttribute(featureId, tenureType);
            //------

            //------ Special case for multiple owner for tenure type :- Collective and community
            if ((tenureType.equalsIgnoreCase("Collective")) || (tenureType.equalsIgnoreCase("Community"))) {
                if (groupId == 0) {
                    isAddCase = true;
                } else {
                    isAddCase = false;
                }
            }
            //------ Special case for multiple owner for tenure type :- Collective and community

            //Case for Add Attribute
            if (isAddCase) {
                if (tenureType.equalsIgnoreCase("Private (jointly)")) {
                    attributes = db.getJointAttributesByFlag(tenureID);
                } else {
                    attributes = db.getAttributesByFlag(tenureID);
                }
            } else {
                attributes = db.getOwnerPropAttributesByGroupId(featureId, tenureID, groupId);
            }

        }

        readOnly = CommonFunctions.isFeatureReadOnly(featureId);

        setContentView(R.layout.ownermany);
        linearLayout = (LinearLayout) findViewById(R.id.list_container);

        listView = (ListView) findViewById(R.id.list);


        TextView emptyText = (TextView) findViewById(R.id.empty);
        listView.setEmptyView(emptyText);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Owner " + "" + iCountOwner);


        if (toolbar != null)
            setSupportActionBar(toolbar);

        try {
            adapterList = new ResourceAttributeAdapter(context, attributes, readOnly);
        } catch (Exception e) {
            e.printStackTrace();
        }

        View footerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footerlayout, null, false);
        listView.addFooterView(footerView);

        listView.setAdapter(adapterList);


       keyboardUtil = new KeyboardUtil(Owner.this, footerView);



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
