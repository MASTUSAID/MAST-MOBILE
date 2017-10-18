package com.rmsi.android.mast.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.ClaimType;
import com.rmsi.android.mast.domain.DisputeType;
import com.rmsi.android.mast.domain.Property;
import com.rmsi.android.mast.domain.Right;
import com.rmsi.android.mast.domain.ShareType;
import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.StringUtility;

public class DataSummaryActivity extends ActionBarActivity {

    Long featureId = 0L;
    Context context = this;
    CommonFunctions cf = CommonFunctions.getInstance();
    TextView personCount, mediaCount, tenureRaltion, customStatus, adminCountTxt, propertyStatus,
            guardianCountTxt, countPOITxt, ownerCountTxt, nonNaturalCount, txtDisputingPersons;
    String classname = "summaryForAll";
    Button btnClose;
    TableRow rowOwners, rowAdministrators, rowGuardians, rowPoi, rowNonNatural;
    String yesStr, noStr;
    private Property property = null;
    private boolean customAttributesExist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            CommonFunctions.getInstance().Initialize(getApplicationContext());
        } catch (Exception e) {
        }
        cf.loadLocale(getApplicationContext());

        yesStr = getResources().getString(R.string.yes);
        noStr = getResources().getString(R.string.no);

        final DbController db = DbController.getInstance(context);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            featureId = extras.getLong("featureid");
            classname = StringUtility.empty(extras.getString("className"));
            property = db.getProperty(featureId);
        }

        if (property == null) {
            cf.showToast(context, R.string.PropertyNotFound, Toast.LENGTH_LONG);
            return;
        }

        if (property.getRight() == null) {
            // Create empty right to avoid checks for nullable right
            property.setRight(new Right());
        }

        setContentView(R.layout.activity_data_summary);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_data_summary);
        if (toolbar != null)
            setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        personCount = (TextView) findViewById(R.id.TextView_countNaturalPerson);
        mediaCount = (TextView) findViewById(R.id.TextView_countMultimedia);
        tenureRaltion = (TextView) findViewById(R.id.TextView_tenureRelation);
        customStatus = (TextView) findViewById(R.id.TextView_custom);
        adminCountTxt = (TextView) findViewById(R.id.TextView_admin_count);
        propertyStatus = (TextView) findViewById(R.id.TextView_propertyStatus);
        guardianCountTxt = (TextView) findViewById(R.id.TextView_countGuardian);
        countPOITxt = (TextView) findViewById(R.id.TextView_countPOI);
        ownerCountTxt = (TextView) findViewById(R.id.TextView_owner_count);
        nonNaturalCount = (TextView) findViewById(R.id.TextView_countNonNatural);
        Button btn_edit = (Button) findViewById(R.id.edit_attributes);
        btnClose = (Button) findViewById(R.id.btnClose);
        TextView txtClaimNumber = (TextView) findViewById(R.id.txtClaimNumber);
        TextView txtClaimType = (TextView) findViewById(R.id.txtClaimType);
        TextView txtClaimDate = (TextView) findViewById(R.id.txtClaimDate);
        TextView txtDisputeType = (TextView) findViewById(R.id.txtDisputeType);
        txtDisputingPersons = (TextView) findViewById(R.id.txtDisputingPersons);

        TableRow rowProperty = (TableRow) findViewById(R.id.rowProperty);
        TableRow rowOccupation = (TableRow) findViewById(R.id.rowOccupation);
        TableRow rowNaturalPersons = (TableRow) findViewById(R.id.rowNaturalPersons);
        TableRow rowMedia = (TableRow) findViewById(R.id.rowMedia);
        TableRow rowCustom = (TableRow) findViewById(R.id.rowCustom);
        TableRow rowDisputeType = (TableRow) findViewById(R.id.rowDisputeType);
        TableRow rowDisputingPersons = (TableRow) findViewById(R.id.rowDisputingPersons);
        rowNonNatural = (TableRow) findViewById(R.id.rowNonNatural);
        rowOwners = (TableRow) findViewById(R.id.rowOwners);
        rowAdministrators = (TableRow) findViewById(R.id.rowAdministrators);
        rowGuardians = (TableRow) findViewById(R.id.rowGuardians);
        rowPoi = (TableRow) findViewById(R.id.rowPoi);

        // Hide rows for unclaimed
        if (StringUtility.empty(property.getClaimTypeCode()).equalsIgnoreCase(ClaimType.TYPE_UNCLAIMED)) {
            rowProperty.setVisibility(View.GONE);
            rowOccupation.setVisibility(View.GONE);
            rowNaturalPersons.setVisibility(View.GONE);
            rowMedia.setVisibility(View.GONE);
            rowCustom.setVisibility(View.GONE);
            rowNonNatural.setVisibility(View.GONE);
            rowOwners.setVisibility(View.GONE);
            rowAdministrators.setVisibility(View.GONE);
            rowGuardians.setVisibility(View.GONE);
            rowPoi.setVisibility(View.GONE);
            rowDisputeType.setVisibility(View.GONE);
            rowDisputingPersons.setVisibility(View.GONE);
        } else if (StringUtility.empty(property.getClaimTypeCode()).equalsIgnoreCase(ClaimType.TYPE_DISPUTE)) {
            rowOccupation.setVisibility(View.GONE);
            rowNaturalPersons.setVisibility(View.GONE);
            rowCustom.setVisibility(View.GONE);
            rowNonNatural.setVisibility(View.GONE);
            rowOwners.setVisibility(View.GONE);
            rowAdministrators.setVisibility(View.GONE);
            rowGuardians.setVisibility(View.GONE);
            rowPoi.setVisibility(View.GONE);

            // Set dispute type
            if(property.getDispute() != null) {
                DisputeType disputeType = db.getDisputeType(property.getDispute().getDisputeTypeId());
                if(disputeType != null)
                    txtDisputeType.setText(disputeType.getName());
            }
        } else {
            rowDisputeType.setVisibility(View.GONE);
            rowDisputingPersons.setVisibility(View.GONE);

            if (property.getRight().getNonNaturalPerson() != null) {
                rowOwners.setVisibility(View.GONE);
                rowAdministrators.setVisibility(View.GONE);
                rowGuardians.setVisibility(View.GONE);
                rowPoi.setVisibility(View.GONE);
            } else {
                rowNonNatural.setVisibility(View.GONE);
            }
        }

        // Set claim number
        if (!StringUtility.isEmpty(property.getPolygonNumber())) {
            if (property.getServerId() != null && property.getServerId() > 0) {
                txtClaimNumber.setText("#" + property.getPolygonNumber() + ", USIN: " + property.getServerId().toString());
            } else {
                txtClaimNumber.setText("#" + property.getPolygonNumber());
            }
        } else {
            txtClaimNumber.setText("#" + featureId.toString());
        }

        // Set claim type
        ClaimType claimType = db.getPropClaimType(property.getId());

        if (claimType != null)
            txtClaimType.setText(claimType.getName());

        txtClaimDate.setText(StringUtility.empty(property.getSurveyDate()));

        customAttributesExist = db.getAttributesByType(Attribute.TYPE_CUSTOM).size() > 0;

        if (classname != null && classname.equalsIgnoreCase("draftSurveyFragment")) {
            btnClose.setVisibility(View.GONE);
        }

        btn_edit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent myIntent = new Intent(context, CaptureAttributesActivity.class);
                myIntent.putExtra("featureid", featureId);
                startActivity(myIntent);
            }
        });

        btnClose.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context, LandingPageActivity.class);
                startActivity(myIntent);
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

    private void updateCount() {
        try {
            DbController db = DbController.getInstance(context);

            if (property.hasGeneralAttributes()) {
                propertyStatus.setText(R.string.completed);
                propertyStatus.setTextColor(getResources().getColor(R.color.green));
            } else {
                propertyStatus.setText(R.string.incomplete);
                propertyStatus.setTextColor(getResources().getColor(R.color.red));
            }

            if (property.getRight().getNonNaturalPerson() != null) {
                tenureRaltion.setText(R.string.nonnatural);
                nonNaturalCount.setText("1");
                nonNaturalCount.setTextColor(getResources().getColor(R.color.green));
            } else {
                tenureRaltion.setText("");
                if (property.getRight().getShareTypeId() > 0) {
                    ShareType shareType = db.getShareType(property.getRight().getShareTypeId());
                    if (shareType != null)
                        tenureRaltion.setText(shareType.getName());
                }
                personCount.setText("0");
                personCount.setTextColor(getResources().getColor(R.color.red));
            }

            if (customAttributesExist) {
                if (property.hasCustomAttributes()) {
                    customStatus.setText(R.string.completed);
                    customStatus.setTextColor(getResources().getColor(R.color.green));   //for green color
                } else {
                    customStatus.setText(R.string.incomplete);
                    customStatus.setTextColor(getResources().getColor(R.color.red));  //red color
                }
            } else {
                customStatus.setText(R.string.not_defined);
                customStatus.setTextColor(getResources().getColor(R.color.red));
            }

            if (property.getRight().getNaturalPersons().size() == 0) {
                personCount.setTextColor(getResources().getColor(R.color.red));
            } else {
                personCount.setTextColor(getResources().getColor(R.color.green));
            }

            int mediaSize = 0;
            int disputingPartiesSize = 0;

            if (StringUtility.empty(property.getClaimTypeCode()).equalsIgnoreCase(ClaimType.TYPE_DISPUTE)
                    && property.getDispute() != null) {
                mediaSize = property.getDispute().getMedia().size();
                disputingPartiesSize = property.getDispute().getDisputingPersons().size();
            } else {
                mediaSize = property.getMedia().size();
            }

            if (disputingPartiesSize == 0) {
                txtDisputingPersons.setTextColor(getResources().getColor(R.color.red));
            } else {
                txtDisputingPersons.setTextColor(getResources().getColor(R.color.green));
            }

            if (mediaSize == 0) {
                mediaCount.setTextColor(getResources().getColor(R.color.red));
            } else {
                mediaCount.setTextColor(getResources().getColor(R.color.green));
            }

            // Update count
            personCount.setText("" + property.getRight().getNaturalPersons().size());
            mediaCount.setText("" + Integer.toString(mediaSize));
            txtDisputingPersons.setText("" + Integer.toString(disputingPartiesSize));

            if (property.getRight().getOwnersCount() == 0) {
                ownerCountTxt.setTextColor(getResources().getColor(R.color.red));
                ownerCountTxt.setText("0");
            } else {
                ownerCountTxt.setTextColor(getResources().getColor(R.color.green));
                ownerCountTxt.setText("" + property.getRight().getOwnersCount());
            }

            if (property.getRight().getAdministratorCount() == 0) {
                adminCountTxt.setTextColor(getResources().getColor(R.color.red));
                adminCountTxt.setText("0");
            } else {
                adminCountTxt.setTextColor(getResources().getColor(R.color.green));
                adminCountTxt.setText("" + property.getRight().getAdministratorCount());
            }

            if (property.getRight().getGuardianCount() == 0) {
                guardianCountTxt.setTextColor(getResources().getColor(R.color.red));
                guardianCountTxt.setText("0");
            } else {
                guardianCountTxt.setTextColor(getResources().getColor(R.color.green));
                guardianCountTxt.setText("" + property.getRight().getGuardianCount());
            }

            if (property.getPersonOfInterests().size() == 0) {
                countPOITxt.setTextColor(getResources().getColor(R.color.red));
                countPOITxt.setText("0");
            } else {
                countPOITxt.setTextColor(getResources().getColor(R.color.green));
                countPOITxt.setText("" + property.getPersonOfInterests().size());
            }
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        updateCount();
        super.onResume();
    }
}
