package com.rmsi.android.mast.activity;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.domain.AcquisitionType;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.ClaimType;
import com.rmsi.android.mast.domain.Feature;
import com.rmsi.android.mast.domain.Option;
import com.rmsi.android.mast.domain.Person;
import com.rmsi.android.mast.domain.RelationshipType;
import com.rmsi.android.mast.domain.Right;
import com.rmsi.android.mast.domain.RightType;
import com.rmsi.android.mast.domain.ShareType;
import com.rmsi.android.mast.domain.User;
import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.DateUtility;
import com.rmsi.android.mast.util.GuiUtility;
import com.rmsi.android.mast.util.StringUtility;

public class AddSocialTenureActivity extends ActionBarActivity {

    private final Context context = this;
    private Button btnSave, btnCancel;
    private CommonFunctions cf = CommonFunctions.getInstance();
    private long featureId = 0;
    private boolean readOnly = false;
    private String infoSingleOccupantStr, infoMultipleJointStr, infoMultipleTeneancyStr, infoTenancyInProbateStr, infoGuardianMinorStr, infoStr;
    private Right right = null;
    private EditText txtCertNumber;
    private TextView txtCertDate;
    private EditText txtJuridicatlArea;
    private LinearLayout certLayout;
    private LinearLayout relationshipLayout;
    private Spinner spinnerShareType;
    private Person person = null;
    private ClaimType claimType;
    int acquisionID=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            CommonFunctions.getInstance().Initialize(getApplicationContext());
        } catch (Exception e) {
        }
        cf.loadLocale(getApplicationContext());

        infoStr = getResources().getString(R.string.info);
//        infoSingleOccupantStr = getResources().getString(R.string.infoSingleOccupantStr);
//        infoMultipleJointStr = getResources().getString(R.string.infoMultipleJointStr);
//        infoMultipleTeneancyStr = getResources().getString(R.string.infoMultipleTeneancyStr);
//        infoTenancyInProbateStr = getResources().getString(R.string.infoTenancyInProbateStr);
//        infoGuardianMinorStr = getResources().getString(R.string.infoGuardianMinorStr);

        infoSingleOccupantStr = getResources().getString(R.string.infoSingleOccupantStr);
        infoMultipleJointStr = getResources().getString(R.string.infoMultipleJointStr);
        infoMultipleTeneancyStr = getResources().getString(R.string.infoMultipleTeneancyStr);
        infoTenancyInProbateStr = getResources().getString(R.string.infoTenancyInProbateStr);
        infoGuardianMinorStr = getResources().getString(R.string.infoGuardianMinorStr);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            featureId = extras.getLong("featureid");
        }

        final DbController db = DbController.getInstance(context);
        readOnly = CommonFunctions.isFeatureReadOnly(featureId);

        setContentView(R.layout.activity_social_tenure_information);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle(R.string.AddSocialTenureInfo);
        toolbar.setTitle("Add Tenure Information");
        if (toolbar != null)
            setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        certLayout = (LinearLayout) findViewById(R.id.certLayout);
        relationshipLayout = (LinearLayout) findViewById(R.id.relationshipLayout);

        btnSave = (Button) findViewById(R.id.btn_save);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        final Spinner spinnerRightType = (Spinner) findViewById(R.id.spinnerRightType);
        spinnerShareType = (Spinner) findViewById(R.id.spinnerShareType);
        final Spinner spinnerRelationshipType = (Spinner) findViewById(R.id.spinnerRelationshipType);
        txtCertNumber = (EditText) findViewById(R.id.txtCertNumber);
        txtCertDate = (TextView) findViewById(R.id.txtCertDate);
        txtJuridicatlArea = (EditText) findViewById(R.id.txtJuridicalArea);
        final Spinner spinnerAcquisition = (Spinner) findViewById(R.id.spinnerAcquisition);
        // Get right
        if (featureId > 0) {
            right = db.getRightByProp(featureId);
        }

        // Populate and setup spinners
        claimType = db.getPropClaimType(featureId);
        final List<RightType> rightTypes;
        List<ShareType> shareTypes = db.getShareTypes(right == null || right.getShareTypeId() < 1);
        List<RelationshipType> relTypes = db.getRelationshipTypes(right == null || right.getRelationshipId() < 1);

        // for Acquisition replace from person to socila tenure in case Existin claim, new claim
        List<AcquisitionType> acquisitionTypes = null;

            acquisitionTypes = DbController.getInstance(context).getAcquisitionTypes(true);
            spinnerAcquisition.setAdapter(new ArrayAdapter(context, android.R.layout.simple_spinner_item, acquisitionTypes));
            ((ArrayAdapter) spinnerAcquisition.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // for Acquisition replace from person to socila tenure in case Existin claim, new claim
        GuiUtility.bindActionOnSpinnerChange(spinnerAcquisition, new Runnable() {
            @Override
            public void run() {
                 acquisionID=((AcquisitionType)spinnerAcquisition.getSelectedItem()).getCode();
                right.setAcquisitionTypeId(((AcquisitionType)spinnerAcquisition.getSelectedItem()).getCode());
            }
        });

        if (claimType != null) {
            rightTypes = db.getRightTypesByClaimType(claimType.getCode(), right == null || right.getRightTypeId() < 1);

            // Hide certificate and area fields for non existing claims
            if (!claimType.getCode().equals(ClaimType.TYPE_EXISTING_CLAIM)) {
                certLayout.setVisibility(View.GONE);
            }
        } else {
            rightTypes = db.getRightTypes(true);
        }

        if (right != null && right.getShareTypeId() == ShareType.TYPE_MUTIPLE_OCCUPANCY_JOINT) {
            relationshipLayout.setVisibility(View.VISIBLE);
        } else {
            relationshipLayout.setVisibility(View.GONE);
        }

        spinnerRightType.setAdapter(new ArrayAdapter(context, android.R.layout.simple_spinner_item, rightTypes));
        spinnerShareType.setAdapter(new ArrayAdapter(context, android.R.layout.simple_spinner_item, shareTypes));
        ((ArrayAdapter) spinnerShareType.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        int IsNatural=db.getpersonTypefromFeature(featureId);
        if (IsNatural==2){
            for (int i=0;i<shareTypes.size();i++) {
//                if(shareTypes.get(i).getName().equalsIgnoreCase("Collective Tenancy")){
                if(shareTypes.get(i).getName().equalsIgnoreCase("Common/Collective Tenancy")){
                    spinnerShareType.setSelection(i);


                        spinnerShareType.setEnabled(false);

                }
            }
        }



        spinnerRelationshipType.setAdapter(new ArrayAdapter(context, android.R.layout.simple_spinner_item, relTypes));
        ((ArrayAdapter) spinnerRightType.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((ArrayAdapter) spinnerRelationshipType.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Setup certificate and area fields
        if (certLayout.getVisibility() != View.GONE) {
            GuiUtility.bindActionOnFieldChange(txtCertNumber, new Runnable() {
                @Override
                public void run() {
                    right.setCertNumber(txtCertNumber.getText().toString());
                }
            });

            txtCertDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GuiUtility.showDatePicker(txtCertDate, right.getCertDate());
                }
            });

            GuiUtility.bindActionOnLabelChange(txtCertDate, new Runnable() {
                @Override
                public void run() {
                    right.setCertDate(txtCertDate.getText().toString());
                }
            });

            GuiUtility.bindActionOnFieldChange(txtJuridicatlArea, new Runnable() {
                @Override
                public void run() {
                    Double area = 0D;
                    try {
                        area = Double.parseDouble(txtJuridicatlArea.getText().toString());
                    } catch (NumberFormatException ex) {
                    }
                    right.setJuridicalArea(area);
                }
            });
        }

        // Setup relationship types
        GuiUtility.bindActionOnSpinnerChange(spinnerRelationshipType, new Runnable() {
            @Override
            public void run() {
                right.setRelationshipId(((RelationshipType) spinnerRelationshipType.getSelectedItem()).getCode());
            }
        });

        GuiUtility.bindActionOnSpinnerChange(spinnerRightType, new Runnable() {
            @Override
            public void run() {
                right.setRightTypeId(((RightType) spinnerRightType.getSelectedItem()).getCode());
            }
        });



        GuiUtility.bindActionOnSpinnerChange(spinnerShareType, new Runnable() {
            @Override
            public void run() {
                int code = ((ShareType) spinnerShareType.getSelectedItem()).getCode();
                right.setShareTypeId(code);
            }
        });

        if (right == null) {
            right = new Right();
            right.setFeatureId(featureId);
            // Set right type to customary right by default
            for (int i = 0; i < rightTypes.size(); i++) {
                if (rightTypes.get(i).getCode() == Right.RIGHT_CUSTOMARY) {
                    spinnerRightType.setSelection(i);
                    break;
                }
            }
        } else {
            // Set fields value
            for (int i = 0; i < rightTypes.size(); i++) {
                if (rightTypes.get(i).getCode() == right.getRightTypeId()) {
                    spinnerRightType.setSelection(i);
                    break;
                }
            }

            for (int i = 0; i < shareTypes.size(); i++) {
                if (shareTypes.get(i).getCode() > 0 && shareTypes.get(i).getCode() == right.getShareTypeId()) {
                    spinnerShareType.setSelection(i);
                    break;
                }
            }

            for (int i = 0; i < relTypes.size(); i++) {
                if (relTypes.get(i).getCode() > 0 && relTypes.get(i).getCode() == right.getRelationshipId()) {
                    spinnerRelationshipType.setSelection(i);
                    break;
                }
            }

            // for Acquisition replace from person to socila tenure in case Existin claim, new claim

                for (int i = 0; i < acquisitionTypes.size(); i++) {


                    if (acquisitionTypes.get(i).getCode() == right.getAcquisitionTypeId()) {
                        spinnerAcquisition.setSelection(i);
                        break;
                    }
                }

            txtCertNumber.setText(right.getCertNumber());
            txtCertDate.setText(DateUtility.formatDateString(right.getCertDate()));
            if (right.getJuridicalArea() != null)
                txtJuridicatlArea.setText(right.getJuridicalArea().toString());
        }

        // Populate attributes
        if (right.getAttributes() == null || right.getAttributes().size() < 1) {
            // Pull attributes for social tenure
            right.setAttributes(db.getAttributesByType(Attribute.TYPE_TENURE));
        }

        if (right.getAttributes() != null) {
            GuiUtility.appendLayoutWithAttributes(mainLayout, right.getAttributes(), readOnly);
        }

        // Disable fields and buttons for adjudicator
        if (readOnly) {
            btnSave.setVisibility(View.GONE);
            spinnerRightType.setEnabled(false);
            spinnerShareType.setEnabled(false);
            spinnerRelationshipType.setEnabled(false);
            txtCertDate.setEnabled(false);
            txtCertNumber.setEnabled(false);
            txtJuridicatlArea.setEnabled(false);
        }

        btnSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
    }


    public void saveData() {
        if (readOnly) {
            return;
        }



        if (!right.validate(context, claimType.getCode(), true) ) {
            return;
        }

        try {
            if (acquisionID==0){
                Toast.makeText(context,context.getResources().getString(R.string.SelectAcquisitionType),Toast.LENGTH_SHORT).show();
            }else {
                DbController db = DbController.getInstance(context);
                boolean saveResult = db.saveRight(right);

                int IsNatural = db.getpersonTypefromFeature(featureId);

                if (!saveResult) {
                    Toast.makeText(context, R.string.unable_to_save_data, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (IsNatural == 2) {
                    Intent nextScreen = new Intent(context, PersonListActivity.class);
                    nextScreen.putExtra("featureid", featureId);
                    nextScreen.putExtra("rightId", right.getId());
                    nextScreen.putExtra("acquisionID", acquisionID);
                    finish();
                    startActivity(nextScreen);
                } else {
                    final int shareTypeId = right.getShareTypeId();

//            if ( IsNatural==1 || IsNatural==3) {
                    if (shareTypeId == ShareType.TYPE_SINGLE_OCCUPANT ||
                            shareTypeId == ShareType.TYPE_MUTIPLE_OCCUPANCY_IN_COMMON ||
                            shareTypeId == ShareType.TYPE_MUTIPLE_OCCUPANCY_JOINT ||
                            shareTypeId == ShareType.TYPE_GUARDIAN ||
                            shareTypeId == ShareType.TYPE_TENANCY_IN_PROBATE ||
                            shareTypeId == ShareType.TYPE_Customary_Individual ||
                            shareTypeId == ShareType.TYPE_Customary_Collective ||
                            shareTypeId == ShareType.TYPE_Single_Tenancy ||
                            shareTypeId == ShareType.TYPE_Joint_Tenency ||
                            shareTypeId == ShareType.TYPE_Common_Tenancy ||
                            shareTypeId == ShareType.TYPE_Collective_Tenancy) {


                        String infoMsg = "No msg";
                        if (shareTypeId == ShareType.TYPE_MUTIPLE_OCCUPANCY_IN_COMMON) {
                            //cf.showMessage(context,"Info","You can add only one adult owner & multiple person of interests");
                            infoMsg = infoMultipleTeneancyStr; //for live
                        } else if (shareTypeId == ShareType.TYPE_SINGLE_OCCUPANT) {
                            //cf.showMessage(context,"Info","You can add two adult owners & multiple person of interests");
                            infoMsg = infoSingleOccupantStr; //for live
                        } else if (shareTypeId == ShareType.TYPE_MUTIPLE_OCCUPANCY_JOINT) {
                            //cf.showMessage(context,"Info","You can add two or more adult owners & multiple person of interests");
                            infoMsg = infoMultipleJointStr; //for live
                        } else if (shareTypeId == ShareType.TYPE_TENANCY_IN_PROBATE) {
                            //cf.showMessage(context,"Info","You can add multiple minor owners & two guardian");
                            infoMsg = infoTenancyInProbateStr;
                        } else if (shareTypeId == ShareType.TYPE_GUARDIAN) {
                            //cf.showMessage(context,"Info","You can add multiple minor owners & two guardian");
                            infoMsg = infoGuardianMinorStr;
                        } else if (shareTypeId == ShareType.TYPE_NON_NATURAL) {
                            Intent myIntent = new Intent(context, AddNonNaturalActivity.class);
                            myIntent.putExtra("featureid", featureId);
                            startActivity(myIntent);
                        } else if (shareTypeId == ShareType.TYPE_Customary_Individual) {
                            //cf.showMessage(context,"Info","You can add two adult owners & multiple person of interests");
                            infoMsg = "You must add one or more occupants/owners and add one or more persons of interest"; //for live
                        } else if (shareTypeId == ShareType.TYPE_Customary_Collective) {
                            //cf.showMessage(context,"Info","You can add two adult owners & multiple person of interests");
                            infoMsg = "You must add one or more occupants/owners and add one or more persons of interest"; //for live
                        } else if (shareTypeId == ShareType.TYPE_Single_Tenancy) {
                            //cf.showMessage(context,"Info","You can add two adult owners & multiple person of interests");
                            infoMsg = infoSingleOccupantStr; //for live
                        } else if (shareTypeId == ShareType.TYPE_Joint_Tenency) {
                            //cf.showMessage(context,"Info","You can add two adult owners & multiple person of interests");
                            infoMsg = infoMultipleJointStr; //for live
                        } else if (shareTypeId == ShareType.TYPE_Common_Tenancy) {
                            //cf.showMessage(context,"Info","You can add two adult owners & multiple person of interests");
                            infoMsg = "You must add one or many occupants/owners and your designated persons of interest"; //for live
                        } else if (shareTypeId == ShareType.TYPE_Collective_Tenancy) {
                            //cf.showMessage(context,"Info","You can add two adult owners & multiple person of interests");
                            infoMsg = "You must add one or more occupants/owners and add one or more persons of interest";  //for live
                        }


                        final Dialog dialog = new Dialog(context, R.style.DialogTheme);
                        dialog.setContentView(R.layout.dialog_for_info);
                        dialog.setTitle(getResources().getString(R.string.info));
                        dialog.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
                        Button proceed = (Button) dialog.findViewById(R.id.btn_proceed);
                        Button cancel = (Button) dialog.findViewById(R.id.btn_cancel);
                        final TextView txtTenureType = (TextView) dialog.findViewById(R.id.textView_tenure_type);
                        final TextView txtInfoMsg = (TextView) dialog.findViewById(R.id.textView_infoMsg);
                        final TextView cnfrmMsg = (TextView) dialog.findViewById(R.id.textView_cnfrm_msg);
                        cnfrmMsg.setVisibility(View.VISIBLE);
                        txtTenureType.setText(db.getShareType(shareTypeId).toString());
                        txtInfoMsg.setText(infoMsg);
                        proceed.setText(getResources().getText(R.string.yes));
                        cancel.setText(getResources().getText(R.string.no));

                        proceed.setOnClickListener(new OnClickListener() {
                            //Run when button is clicked
                            @Override
                            public void onClick(View v) {
                                Intent nextScreen;
                                if (right.getShareTypeId() == ShareType.TYPE_TENANCY_IN_PROBATE) {
                                    nextScreen = new Intent(context, PersonListWithDPActivity.class);
                                } else {
//                            DbController db = DbController.getInstance(context);
//                            int IsNatural=db.getpersonType(featureId);
//
//                            if (IsNatural==0) {
//                                nextScreen = new Intent(context, AddNonNaturalPersonActivity.class);
//                            }else if (IsNatural ==1 || IsNatural==3){
//                                nextScreen = new Intent(context, PersonListActivity.class);
//                            }
                               nextScreen = new Intent(context, PersonListActivity.class);//
                                }
                                nextScreen.putExtra("featureid", featureId);
                                nextScreen.putExtra("rightId", right.getId());
                                nextScreen.putExtra("acquisionID", acquisionID);
                                finish();
                                startActivity(nextScreen);

                                dialog.dismiss();
                            }
                        });

                        cancel.setOnClickListener(new OnClickListener() {
                            //Run when button is clicked
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                    }
//            }

                    else if (shareTypeId == ShareType.TYPE_NON_NATURAL) {
                        Intent nextScreen = new Intent(context, AddNonNaturalPersonActivity.class);
                        nextScreen.putExtra("featureid", featureId);
                        nextScreen.putExtra("rightId", right.getId());
                        startActivity(nextScreen);
                    }
                }
//            else {
//                Intent nextScreen = new Intent(context, AddNonNaturalPersonActivity.class);
//                nextScreen.putExtra("featureid", featureId);
//                nextScreen.putExtra("rightId", right.getId());
//                finish();
//                startActivity(nextScreen);
//            }
            }
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            Toast.makeText(context, R.string.unable_to_save_data, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Disable share type if there are persons already added
        Right rightTmp = DbController.getInstance(context).getRightByProp(featureId);
        if (rightTmp != null && (rightTmp.getNaturalPersons().size() > 0 || rightTmp.getNonNaturalPerson() != null))
            spinnerShareType.setEnabled(false);
        else if (!readOnly)
            spinnerShareType.setEnabled(true);
    }
}