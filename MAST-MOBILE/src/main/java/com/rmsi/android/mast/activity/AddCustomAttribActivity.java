package com.rmsi.android.mast.activity;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rmsi.android.mast.adapter.AttributeAdapter;
import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.Option;
import com.rmsi.android.mast.domain.User;
import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.GuiUtility;

public class AddCustomAttribActivity extends ActionBarActivity {

    Long featureId = 0L;
    List<Attribute> attributes;
    final Context context = this;
    AttributeAdapter adapterList;
    Button btnSave, btnBack;
    String FieldValue;
    CommonFunctions cf = CommonFunctions.getInstance();
    private boolean readOnly = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            CommonFunctions.getInstance().Initialize(getApplicationContext());
        } catch (Exception e) {
        }
        cf.loadLocale(getApplicationContext());

        setContentView(R.layout.activity_add_property_info);


        ListView listView = (ListView) findViewById(android.R.id.list);
        TextView emptyText = (TextView) findViewById(android.R.id.empty);
        listView.setEmptyView(emptyText);

        btnSave = (Button) findViewById(R.id.btn_save);
        btnBack = (Button) findViewById(R.id.btn_cancel);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.add_custom_attributes);

        if (toolbar != null)
            setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DbController db = DbController.getInstance(context);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            featureId = extras.getLong("featureid");
            attributes = db.getPropAttributesByType(featureId, Attribute.TYPE_CUSTOM);
            if (attributes.size() < 1) {
                // Try to get list of attributes of custom type
                attributes = db.getAttributesByType(Attribute.TYPE_CUSTOM);
            }

            if (attributes.size() < 1) {
                findViewById(R.id.btn_container).setVisibility(View.GONE);
            }
        }

        readOnly = CommonFunctions.isFeatureReadOnly(featureId);

        try {
            adapterList = new AttributeAdapter(context, attributes, readOnly);
        } catch (Exception e) {
            e.printStackTrace();
        }

        listView.setAdapter(adapterList);

        // Change next button caption to finish label since it's a last screen
        btnSave.setText(getResources().getString(R.string.Finish));

        if (readOnly) {
            btnSave.setVisibility(View.GONE);
        }

        btnSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saveData()) {
                    String savedMsg = getResources().getString(R.string.data_saved);
                    Toast toast = Toast.makeText(context, savedMsg, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                    Intent myIntent = new Intent(context, DataSummaryActivity.class);
                    myIntent.putExtra("featureid", featureId);
                    myIntent.putExtra("className", "PersonListActivity");
                    startActivity(myIntent);
                } else {
                    String fillMandatory = getResources().getString(R.string.fill_mandatory);
                    Toast.makeText(context, fillMandatory, Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public boolean saveData() {
        if (validate()) {
            try {
                if (DbController.getInstance(context).savePropAttributes(attributes, featureId)) {
                    adapterList.notifyDataSetChanged();
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
                return false;
            }
        } else {
            String fillMandatory = getResources().getString(R.string.fill_mandatory);
            Toast.makeText(context, fillMandatory, Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    public boolean validate() {
        return GuiUtility.validateAttributes(attributes, true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
