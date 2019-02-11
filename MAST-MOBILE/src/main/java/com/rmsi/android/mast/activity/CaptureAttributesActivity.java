package com.rmsi.android.mast.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rmsi.android.mast.activity.R.string;
import com.rmsi.android.mast.adapter.SpinnerAdapter;
import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.ClaimType;
import com.rmsi.android.mast.domain.Feature;
import com.rmsi.android.mast.domain.Option;
import com.rmsi.android.mast.domain.Person;
import com.rmsi.android.mast.domain.Property;
import com.rmsi.android.mast.domain.ShareType;
import com.rmsi.android.mast.domain.User;
import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.DateUtility;
import com.rmsi.android.mast.util.GuiUtility;
import com.rmsi.android.mast.util.StringUtility;

public class CaptureAttributesActivity extends AppCompatActivity {
    private ImageView personInfo, tenureInfo, multimedia, custom, propertyInfo, disputeImg;
    private final Context context = this;
    private CommonFunctions cf = CommonFunctions.getInstance();
    private Long featureId = 0L;
    private DbController db = DbController.getInstance(context);
    private boolean readOnly = false;
    private Property property = null;
    private LinearLayout bottomToolbar;
    private Spinner spinnerClaimType;
    private TextView txtPersonCount;
    private TextView txtMediaCount;
    private RelativeLayout layoutCustom;
    private RelativeLayout layoutGeneral;
    private RelativeLayout layoutRight;
    private RelativeLayout layoutPersons;
    private RelativeLayout layoutDispute;
    private RelativeLayout layoutMedia;
    private LinearLayout layoutUkaNumber, linearLayoutlayoutExistingUSe,linearLayoutDocument;
    private EditText txtUkaNumber;
    private RadioGroup radioGroup;
    private Spinner spinnerClaimRight,spinnerDocumentType,spinnerPersonType;
    List<String> claimRights,documentTypes;
    private EditText editTextPlotNo,editTextdocumentRefNo;
    private TextView textViewDocumentDate;

    private  LinearLayout mainLayout;
     Person person = null;


    private static boolean keyboardHidden = true;
    private static int reduceHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonFunctions.getInstance().Initialize(getApplicationContext());
        cf.loadLocale(getApplicationContext());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            featureId = extras.getLong("featureid");
        }

        if (featureId > 0) {
            property = DbController.getInstance(context).getProperty(featureId);
        }

        if (property == null) {
            property = new Property();
        }
        person = new Person();
        if(CommonFunctions.getRoleID() == User.ROLE_ADJUDICATOR ||
                !StringUtility.empty(property.getStatus()).equalsIgnoreCase(Feature.CLIENT_STATUS_DRAFT))
            readOnly = true;

        setContentView(R.layout.activity_capture_attributes);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_capture_attributes);

        if (toolbar != null)
            setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        List<Option> hamletList = db.getHamletOptions();
        List<Option> witnessList = db.getAdjudicators();
        List<ClaimType> claimTypes = db.getClaimTypes(true);
         claimRights= new ArrayList<>();
        claimRights.add("Customary Right of Occupancy");
        claimRights.add("Freehold Title");
        claimRights.add("Other");

         documentTypes= new ArrayList<>();
        documentTypes.add("Land Instrument 1");
        documentTypes.add("Land Instrument 2");
        documentTypes.add("Land Instrument 3");
        documentTypes.add("Land Instrument 4");


        //for non-natural

         mainLayout = (LinearLayout) findViewById(R.id.mainLayout);



        TextView spatialunitValue = (TextView) findViewById(R.id.spatialunit_lbl);
        TextView VillageName = (TextView) findViewById(R.id.villageName_lbl);
       // Spinner spinnerHamlet = (Spinner) findViewById(R.id.spinner_hemlet);
        //Spinner spinnerWitness1 = (Spinner) findViewById(R.id.spinner_witness1);
       // Spinner spinnerWitness2 = (Spinner) findViewById(R.id.spinner_witness2);
        final TextView txtClaimDate = (TextView) findViewById(R.id.txtClaimDate);
        final EditText txtClaimNumber = (EditText) findViewById(R.id.txtPolygonNumber);
        txtUkaNumber = (EditText) findViewById(R.id.txtUkaNumber);
        layoutUkaNumber = (LinearLayout) findViewById(R.id.layoutUkaNumber);
        txtPersonCount = (TextView) findViewById(R.id.personCount);
        txtMediaCount = (TextView) findViewById(R.id.multimediaCount);
        spinnerClaimType = (Spinner) findViewById(R.id.spinnerClaimType);
        bottomToolbar = (LinearLayout) findViewById(R.id.buttonBarAttributes);
        layoutGeneral = (RelativeLayout) findViewById(R.id.layoutGeneral);
        layoutRight = (RelativeLayout) findViewById(R.id.layoutRight);
        layoutDispute = (RelativeLayout) findViewById(R.id.layoutDispute);
        layoutPersons = (RelativeLayout) findViewById(R.id.layoutPersons);
        layoutMedia = (RelativeLayout) findViewById(R.id.layoutMedia);
        layoutCustom = (RelativeLayout) findViewById(R.id.layoutCustom);
        linearLayoutlayoutExistingUSe= (LinearLayout) findViewById(R.id.layoutExistingUSe);
        linearLayoutDocument= (LinearLayout) findViewById(R.id.document);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.clearCheck();
        spinnerClaimRight= (Spinner) findViewById(R.id.claimRightType);
        spinnerDocumentType= (Spinner) findViewById(R.id.documentType);
        editTextPlotNo= (EditText) findViewById(R.id.plotNo);
        editTextdocumentRefNo= (EditText) findViewById(R.id.documentTypeRefNo);
        textViewDocumentDate= (TextView) findViewById(R.id.documentTypeDate);
        spinnerPersonType= (Spinner) findViewById(R.id.spinnerPersonType);

        String[] person_subType = getResources().getStringArray(R.array.person_natural_non);
        spinnerPersonType.setAdapter(new ArrayAdapter(context, android.R.layout.simple_spinner_item, person_subType));
        ((ArrayAdapter) spinnerPersonType.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerPersonType.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
             if (position==1){
                 //for NAtural
                 property.setIsNatural(1);
                 mainLayout.setVisibility(View.GONE);
                 person.getAttributes().clear();
                 mainLayout.removeAllViews();
             }
             else if (position==2){
                 //for Non-NAtural
                 property.setIsNatural(2);

                 openNonNaturalInstitute();
             }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this.context, android.R.layout.simple_spinner_item, hamletList);
//        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerHamlet.setAdapter(spinnerAdapter);

//        SpinnerAdapter spinnerAdapterWitness1 = new SpinnerAdapter(this.context, android.R.layout.simple_spinner_item, witnessList);
//        spinnerAdapterWitness1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerWitness1.setAdapter(spinnerAdapterWitness1);

//        SpinnerAdapter spinnerAdapterWitness2 = new SpinnerAdapter(this.context, android.R.layout.simple_spinner_item, witnessList);
//        spinnerAdapterWitness2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerWitness2.setAdapter(spinnerAdapterWitness2);

        spinnerClaimType.setAdapter(new ArrayAdapter(context, android.R.layout.simple_spinner_item, claimTypes));
        ((ArrayAdapter) spinnerClaimType.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        spinnerClaimType.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                property.setClaimTypeCode(((ClaimType) parent.getItemAtPosition(position)).getCode());
                showHideUkaNumber();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerClaimRight.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                property.setClaimRight((String) parent.getItemAtPosition(position));
                }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerDocumentType.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                property.setDocumentType((String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


//        spinnerHamlet.setOnItemSelectedListener(new OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View arg1, int pos, long arg3) {
//                property.setHamletId(((Option) parent.getItemAtPosition(pos)).getId());
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> arg0) {
//            }
//        });

//        spinnerWitness1.setOnItemSelectedListener(new OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View arg1, int pos, long arg3) {
//                Option selecteOption = (Option) parent.getItemAtPosition(pos);
//                if (selecteOption.getId() == 0L)
//                    property.setAdjudicator1("");
//                else
//                    property.setAdjudicator1(selecteOption.getName());
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> arg0) {
//            }
//        });

//        spinnerWitness2.setOnItemSelectedListener(new OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View arg1, int pos, long arg3) {
//                Option selecteOption = (Option) parent.getItemAtPosition(pos);
//                if (selecteOption.getId() == 0L)
//                    property.setAdjudicator2("");
//                else
//                    property.setAdjudicator2(selecteOption.getName());
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> arg0) {
//            }
//        });

        GuiUtility.bindActionOnFieldChange(txtClaimNumber, new Runnable() {
            @Override
            public void run() {
                property.setPolygonNumber(txtClaimNumber.getText().toString());
            }
        });

        GuiUtility.bindActionOnFieldChange(txtUkaNumber, new Runnable() {
            @Override
            public void run() {
                property.setUkaNumber(txtUkaNumber.getText().toString());
            }
        });

        GuiUtility.bindActionOnLabelChange(txtClaimDate, new Runnable() {
            @Override
            public void run() {
                property.setSurveyDate(txtClaimDate.getText().toString());
            }
        });

        txtClaimDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GuiUtility.showDatePicker(txtClaimDate, property.getSurveyDate());
            }
        });




        VillageName.setText(VillageName.getText() + ": " + db.villageName());
        String claimStr = context.getResources().getString(R.string.Claim);

        if (!StringUtility.isEmpty(property.getPolygonNumber())) {
            claimStr = claimStr + ": " + property.getIpNumber();
        } else {
            claimStr = claimStr + ": " + property.getIpNumber();
        }

//        if (!StringUtility.isEmpty(property.getPolygonNumber())) {
//            claimStr = claimStr + ": " + property.getPolygonNumber();
//        } else {
//            claimStr = claimStr + ": " + featureId.toString();
//        }

        if (property.getServerId() != null && property.getServerId() > 0) {
            claimStr = claimStr + ", USIN: " + property.getServerId().toString();
        }
        //    "CLAIM_RIGHT TEXT," +
//            "PLOT_NO TEXT," +
//            "DOCUMENT TEXT," +
//            "DOCUMENT_TYPE TEXT," +
//            "DOCUMENT_DATE TEXT," +
//            "DOCUMENT_REF_NO TEXT" +
        GuiUtility.bindActionOnFieldChange(editTextPlotNo, new Runnable() {
            @Override
            public void run() {
                property.setPlotNo(editTextPlotNo.getText().toString());
            }
        });

        GuiUtility.bindActionOnFieldChange(editTextdocumentRefNo, new Runnable() {
            @Override
            public void run() {
                property.setDocumentRefNo(editTextdocumentRefNo.getText().toString());
            }
        });

        GuiUtility.bindActionOnLabelChange(textViewDocumentDate, new Runnable() {
            @Override
            public void run() {
                property.setDocumentDate(textViewDocumentDate.getText().toString());
            }
        });

        textViewDocumentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GuiUtility.showDatePicker(textViewDocumentDate, property.getSurveyDate());
            }
        });
        spatialunitValue.setText(claimStr);

        // Populate fields
        if (property.getId() > 0) {


            if (!StringUtility.isEmpty(property.getSurveyDate())) {
                txtClaimDate.setText(DateUtility.formatDateString(property.getSurveyDate()));
            } else {
                txtClaimDate.setText(DateUtility.getCurrentStringDate());
            }

            txtClaimDate.setText(DateUtility.formatDateString(property.getSurveyDate()));
            textViewDocumentDate.setText(DateUtility.formatDateString(property.getSurveyDate()));
            txtClaimNumber.setText(StringUtility.empty(property.getPolygonNumber()));
            txtUkaNumber.setText(StringUtility.empty(property.getUkaNumber()));

            for (int i = 0; i < claimTypes.size(); i++) {
                if (!StringUtility.isEmpty(claimTypes.get(i).getCode()) &&
                        claimTypes.get(i).getCode().equalsIgnoreCase(StringUtility.empty(property.getClaimTypeCode()))) {
                    spinnerClaimType.setSelection(i);
                    break;
                }
            }

           if (property.getIsNatural()==1){
               spinnerPersonType.setSelection(1);
           }
           else if (property.getIsNatural()==2){
               spinnerPersonType.setSelection(2);
           }

//            for (int i = 0; i < hamletList.size(); i++) {
//                if (hamletList.get(i).getId() == property.getHamletId()) {
//                    spinnerHamlet.setSelection(i);
//                }
//            }

//            for (int i = 0; i < witnessList.size(); i++) {
//                if (witnessList.get(i).getName().equalsIgnoreCase(property.getAdjudicator1())) {
//                    spinnerWitness1.setSelection(i);
//                }
//                if (witnessList.get(i).getName().equalsIgnoreCase(property.getAdjudicator2())) {
//                    spinnerWitness2.setSelection(i);
//                }
//            }
        } else {
            txtClaimDate.setText(DateUtility.getCurrentStringDate());
        }

        showHideUkaNumber();

        if (readOnly) {
           // spinnerHamlet.setEnabled(false);
            //spinnerWitness1.setEnabled(false);
           // spinnerWitness2.setEnabled(false);
            spinnerClaimType.setEnabled(false);
            txtClaimDate.setEnabled(false);
            txtClaimNumber.setEnabled(false);
            txtUkaNumber.setEnabled(false);
        }

        personInfo = (ImageView) findViewById(R.id.btn_personlist);
        propertyInfo = (ImageView) findViewById(R.id.btn_propertyInfo);
        tenureInfo = (ImageView) findViewById(R.id.btn_tenureInfo);
        multimedia = (ImageView) findViewById(R.id.btn_addMultimedia);
        custom = (ImageView) findViewById(R.id.btn_addcustom);
        disputeImg = (ImageView) findViewById(R.id.btnDisputeInfo);

        //For tooltip text
        View viewForTenureToolTip = tenureInfo;
        View viewForPersonToolTip = personInfo;
        View viewForMediaToolTip = multimedia;
        View viewForCustomToolTip = custom;
        View viewForPropertyDetailsToolTip = propertyInfo;
        View viewForDisputeToolTip = disputeImg;

        String add_person = getResources().getString(R.string.AddPerson);
        String add_social_tenure = getResources().getString(R.string.AddSocialTenureInfo);
        String add_multimedia = getResources().getString(R.string.AddNewMultimedia);
        String add_custom_attrib = getResources().getString(R.string.add_custom_attributes);
        String add_property_details = getResources().getString(R.string.AddNewPropertyDetails);
        String add_dispute_details = getResources().getString(string.AddDisputeDetails);

        cf.setup(viewForPersonToolTip, add_person);
        cf.setup(viewForTenureToolTip, add_social_tenure);
        cf.setup(viewForMediaToolTip, add_multimedia);
        cf.setup(viewForCustomToolTip, add_custom_attrib);
        cf.setup(viewForPropertyDetailsToolTip, add_property_details);
        cf.setup(viewForDisputeToolTip, add_dispute_details);

        // Customize custom attributes button
        if (bottomToolbar != null) {
            if (!StringUtility.isEmpty(property.getClaimTypeCode())) {
                // Check if custom fields are defined for the project
                if (db.getAttributesByType(Attribute.TYPE_CUSTOM).size() < 1)
                    layoutCustom.setVisibility(View.GONE);
                else
                    layoutCustom.setVisibility(View.VISIBLE);
            }
        }

        disputeImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (property.hasGeneralAttributes() && saveData(false)) {
                    Intent myIntent = new Intent(context, AddDisputeActivity.class);
                    myIntent.putExtra("featureid", featureId);
                    startActivity(myIntent);
                } else {
                    String msg = getResources().getString(string.save_genral_attrribute);
                    String warning = getResources().getString(string.warning);
                    cf.showMessage(context, warning, msg);
                }
            }
        });

        personInfo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (property.getRight() == null || property.getRight().getShareTypeId() < 1) {
                    String msg = getResources().getString(string.fill_tenure);
                    String warning = getResources().getString(string.warning);
                    cf.showMessage(context, warning, msg);
                    return;
                }

                // Validate and save
                if (!saveData(false)) {
                    return;
                }

                Intent nextScreen;

                if (property.getRight().getShareTypeId() == ShareType.TYPE_TENANCY_IN_PROBATE) {
                    nextScreen = new Intent(context, PersonListWithDPActivity.class);
                } else if (property.getRight().getShareTypeId() == ShareType.TYPE_NON_NATURAL) {
                    nextScreen = new Intent(context, AddNonNaturalActivity.class);
                } else {
                    nextScreen = new Intent(context, PersonListActivity.class);
                }
                nextScreen.putExtra("featureid", featureId);
                nextScreen.putExtra("rightId", property.getRight().getId());
                startActivity(nextScreen);
            }
        });

        propertyInfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saveData(false)) {
                    Intent myIntent = new Intent(context, AddGeneralPropertyActivity.class);
                    myIntent.putExtra("featureid", featureId);
                    myIntent.putExtra("isDispute", StringUtility.empty(property.getClaimTypeCode())
                            .equalsIgnoreCase(ClaimType.TYPE_DISPUTE));
                    startActivity(myIntent);
                } else {
                    String msg = getResources().getString(string.save_genral_attrribute);
                    String warning = getResources().getString(string.warning);
                    cf.showMessage(context, warning, msg);
                }
            }
        });

        tenureInfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (property.hasGeneralAttributes() && saveData(false)) {
                    Intent myIntent = new Intent(context, AddSocialTenureActivity.class);
                    myIntent.putExtra("featureid", featureId);
                    myIntent.putExtra("serverFeaterID", property.getServerId());
                    startActivity(myIntent);
                } else {
                    String msg = getResources().getString(string.save_genral_attrribute);
                    String warning = getResources().getString(string.warning);
                    cf.showMessage(context, warning, msg);
                }
            }
        });

        multimedia.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (property.hasGeneralAttributes() && saveData(false)) {
                    Long disputeId = 0L;
                    if (StringUtility.empty(property.getClaimTypeCode()).equalsIgnoreCase(ClaimType.TYPE_DISPUTE)) {
                        if (property.getDispute() == null || property.getDispute().getId() < 1) {
                            String msg = getResources().getString(R.string.AddDisputeDetails);
                            String warning = getResources().getString(string.warning);
                            cf.showMessage(context, warning, msg);
                            return;
                        } else {
                            disputeId = property.getDispute().getId();
                        }
                    }

                    Intent myIntent = new Intent(context, MediaListActivity.class);
                    myIntent.putExtra("featureid", featureId);
                    myIntent.putExtra("disputeId", disputeId);
                    myIntent.putExtra("serverFeaterID", property.getServerId());
                    startActivity(myIntent);
                } else {
                    String msg = getResources().getString(string.save_genral_attrribute);
                    String warning = getResources().getString(string.warning);
                    cf.showMessage(context, warning, msg);
                }
            }
        });

        custom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (property.hasGeneralAttributes() && saveData(false)) {
                    Intent myIntent = new Intent(context, AddCustomAttribActivity.class);
                    myIntent.putExtra("featureid", featureId);
                    startActivity(myIntent);
                } else {
                    String msg = getResources().getString(string.save_genral_attrribute);
                    String warning = getResources().getString(string.warning);
                    cf.showMessage(context, warning, msg);
                }
            }
        });
    }

    private void openNonNaturalInstitute() {
        mainLayout.setVisibility(View.VISIBLE);

        if (featureId!=0) {
            person.setAttributes(db.getDataFromNonNatural(featureId));
        }

        if (person.getAttributes() == null || person.getAttributes().size() < 1) {
            // Pull attributes for non-natural person(Institution name,Institution Type)
            person.setAttributes(db.getAttributesByTypeNonNatural(Attribute.TYPE_NON_NATURAL_PERSON));
        }

        if (person.getAttributes() != null) {
            GuiUtility.appendLayoutWithAttributes(mainLayout, person.getAttributes(), readOnly);



            // Disable age attribute if dob exists. Set age value based on dob field
            final Attribute dob = person.getAttribute(Person.ATTRIBUTE_DOB);
            final Attribute age = person.getAttribute(Person.ATTRIBUTE_AGE);

            if (dob != null && age != null) {
                age.getView().setEnabled(false);
                GuiUtility.bindActionOnLabelChange((TextView) dob.getView(), new Runnable() {
                    @Override
                    public void run() {
                        String date = ((TextView) dob.getView()).getText().toString();
                        if (StringUtility.isEmpty(date)) {
                            ((EditText) age.getView()).setText("");
                        } else {
                            ((EditText) age.getView()).setText(
                                    Integer.toString(
                                            DateUtility.getDiffYears(
                                                    DateUtility.getDate(date),
                                                    DateUtility.getCurrentDate()
                                            )
                                    )
                            );
                        }
                    }
                });
            }
        }

    }

    public boolean validate() {
        return GuiUtility.validateAttributes(person.getAttributes(), true);
    }

    private void showHideUkaNumber(){
        if(StringUtility.empty(property.getClaimTypeCode()).equalsIgnoreCase(ClaimType.TYPE_EXISTING_CLAIM)) {
            DisplayExistingClaimFields(true);
            layoutUkaNumber.setVisibility(View.GONE);

        }

        else {
            DisplayExistingClaimFields(false);
            layoutUkaNumber.setVisibility(View.GONE);
            txtUkaNumber.setText("");
            property.setUkaNumber(null);

        }
    }

    public void DisplayExistingClaimFields(boolean isVisible) {
        if(isVisible) {

            linearLayoutlayoutExistingUSe.setVisibility(View.VISIBLE);
            spinnerClaimRight.setAdapter(new ArrayAdapter(context, android.R.layout.simple_spinner_item, claimRights));
            ((ArrayAdapter) spinnerClaimRight.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton rb = (RadioButton) group.findViewById(checkedId);
//                    String str=rb.getText().toString();
                    property.setDocument(rb.getText().toString());
                    if (null != rb && checkedId > -1) {
                        if (rb.getText().toString().equalsIgnoreCase("Yes")){
                            linearLayoutDocument.setVisibility(View.VISIBLE);
                            //Ambar


                            spinnerDocumentType.setAdapter(new ArrayAdapter(context, android.R.layout.simple_spinner_item, documentTypes));
                            ((ArrayAdapter) spinnerDocumentType.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            //Ambar

                        }else {
                            linearLayoutDocument.setVisibility(View.GONE);
                            property.setDocumentType(null);
                            property.setDocumentDate(null);
                            property.setDocumentRefNo(null);
                        }

                    }

                }
            });
        }
        else{
            linearLayoutDocument.setVisibility(View.GONE);

            linearLayoutlayoutExistingUSe.setVisibility(View.GONE);
            property.setClaimRight(null);
            property.setPlotNo(null);
            property.setDocument("No");
            property.setDocumentType(null);
            property.setDocumentDate(null);
            property.setDocumentRefNo(null);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!readOnly)
            getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            saveData(true);
        }
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean saveData(boolean goToNextScreen) {
        if (readOnly) {
            return true;
        }

        if (!GuiUtility.validateAttributes(person.getAttributes(), true)) {
             String   errorMessage = context.getResources().getString(R.string.FillRequiredFieldsOnNonPerson);
            Toast.makeText(context,errorMessage,Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!property.validateBasicInfo(context, true)) {
            return false;
        }

        try {

            if (person.getAttributes() != null && person.getAttributes().size() > 0) {
                for (Attribute attribute : person.getAttributes()) {

                    attribute.setFeatureId(featureId);
                }
            }
            boolean saveResult1 = DbController.getInstance(context).saveNonNaturalInstitute(person.getAttributes());
            boolean saveResult = DbController.getInstance(context).updatePropertyBasicInfo(property);
            if (saveResult) {
                if (goToNextScreen) {

                    cf.showToast(context, R.string.data_saved, Toast.LENGTH_SHORT);

                    // For unclaimed go to the final page
                    if (StringUtility.empty(property.getClaimTypeCode()).equalsIgnoreCase(ClaimType.TYPE_UNCLAIMED)) {
                        Intent myIntent = new Intent(context, DataSummaryActivity.class);
                        myIntent.putExtra("featureid", featureId);
                        finish();
                        startActivity(myIntent);
                    } else {
                        Intent myIntent = new Intent(context, AddGeneralPropertyActivity.class);
                        myIntent.putExtra("featureid", featureId);
                        myIntent.putExtra("isDispute", StringUtility.empty(property.getClaimTypeCode())
                                .equalsIgnoreCase(ClaimType.TYPE_DISPUTE));
                        finish();
                        startActivity(myIntent);
                    }
                }
                return true;
            } else {
                cf.showToast(context, R.string.unable_to_save_data, Toast.LENGTH_SHORT);
                return false;
            }
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            return false;
        }

    }

    private void updateCount() {
        try {
            if (bottomToolbar.getVisibility() != View.GONE) {
                int persons = 0;
                int media = 0;

                Property tmpProp = db.getProperty(featureId);
                if (tmpProp.getRight() != null && tmpProp.getRight().getNaturalPersons() != null)
                    persons = tmpProp.getRight().getNaturalPersons().size();

                if (StringUtility.empty(tmpProp.getClaimTypeCode()).equalsIgnoreCase(ClaimType.TYPE_DISPUTE)) {
                    if (tmpProp.getDispute() != null)
                        media = tmpProp.getDispute().getMedia().size();
                } else {
                    media = tmpProp.getMedia().size();
                }

                txtPersonCount.setText( Integer.toString(persons));
                txtMediaCount.setText(Integer.toString(media));
            }
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (property != null && !StringUtility.isEmpty(property.getClaimTypeCode())) {
            spinnerClaimType.setEnabled(false);
            txtUkaNumber.setEnabled(false);

            // Don't show toolbar for unclaimed parcels
            if (StringUtility.empty(property.getClaimTypeCode()).equalsIgnoreCase(ClaimType.TYPE_UNCLAIMED))
                bottomToolbar.setVisibility(View.GONE);
            else if (StringUtility.empty(property.getClaimTypeCode()).equalsIgnoreCase(ClaimType.TYPE_DISPUTE)) {
                // Customize toolbar for dispute
                bottomToolbar.setVisibility(View.VISIBLE);
                layoutRight.setVisibility(View.GONE);
                layoutPersons.setVisibility(View.GONE);
                layoutCustom.setVisibility(View.GONE);
            } else {
                bottomToolbar.setVisibility(View.VISIBLE);
                layoutDispute.setVisibility(View.GONE);
            }
        } else {
            bottomToolbar.setVisibility(View.GONE);
        }
        updateCount();
    }
}
