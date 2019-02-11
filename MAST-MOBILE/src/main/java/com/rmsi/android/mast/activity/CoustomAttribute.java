package com.rmsi.android.mast.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rmsi.android.mast.adapter.AttributeAdapter;
import com.rmsi.android.mast.adapter.AttributeResAdapter;
import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.ResourceCustomAttribute;
import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.GuiUtility;
import com.rmsi.android.mast.util.ResGuiUtility;

import java.util.List;

/**
 * Created by Ambar.Srivastava on 12/22/2017.
 */

public class CoustomAttribute extends AppCompatActivity {

    private Long featureId = 0L;
    private boolean isDispute = false;
    private List<ResourceCustomAttribute> attributes;
    private List<ResourceCustomAttribute> attributesres;
    private ListView listViewres,listviewsub;
    private final Context context = this;
    private AttributeResAdapter adapterList;
    private AttributeResAdapter adapterListRes;
    private Button btnSave, btnBack;
    private CommonFunctions cf = CommonFunctions.getInstance();
    private boolean readOnly = false;
    private String classi,subClassi,tenureType,tenureID,subID;

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

            attributes = db.getPropResAttributesByType(featureId, tenureID,subID);
            if (attributes.size() < 1) {
                // Try to get list of attributes of general type
                attributes = db.getResAttributesByFlag(tenureID,subID);
            }

            String res="null";
            attributesres = db.getPropResAttributesByType(featureId, tenureID,res);
            if (attributesres.size() < 1) {
                // Try to get list of attributes of general type
                attributesres = db.getResAttributesByFlag(tenureID,res);
            }
        }

        readOnly = CommonFunctions.isFeatureReadOnly(featureId);

        setContentView(R.layout.custom_attributes);

        listViewres = (ListView) findViewById(R.id.list);
        TextView emptyText = (TextView) findViewById(R.id.empty);
        listViewres.setEmptyView(emptyText);

        listviewsub = (ListView) findViewById(R.id.list1);
        TextView emptyText1 = (TextView) findViewById(R.id.empty1);
        listviewsub.setEmptyView(emptyText1);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Custom Attribute");

        if (toolbar != null)
            setSupportActionBar(toolbar);
        try {
            adapterList = new AttributeResAdapter(context, attributes, readOnly);
            adapterListRes = new AttributeResAdapter(context, attributesres, readOnly);
        } catch (Exception e) {
            e.printStackTrace();
        }
        listViewres.setAdapter(adapterListRes);
        listviewsub.setAdapter(adapterList);

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
                boolean saveResult1 = DbController.getInstance(context).saveResPropAttributes(attributesres, featureId);
                boolean saveResult = DbController.getInstance(context).saveResPropAttributes(attributes, featureId);


                if (saveResult && saveResult1) {
                    cf.showToast(context, R.string.data_saved, Toast.LENGTH_SHORT);

                    Intent intent=new Intent(CoustomAttribute.this,CollectedResourceDataSummary.class);
                    intent.putExtra("featureid", featureId);
                    intent.putExtra("classi", classi);
                    intent.putExtra("subclassi", subClassi);
                    intent.putExtra("tenure", tenureType);
                    startActivity(intent);

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
        return ResGuiUtility.validateAttributes(attributes, true) && ResGuiUtility.validateAttributes(attributesres,true);
    }


}
