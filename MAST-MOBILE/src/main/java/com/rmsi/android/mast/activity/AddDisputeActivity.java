package com.rmsi.android.mast.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.rmsi.android.mast.Fragment.PersonListFragment;
import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.domain.ClaimType;
import com.rmsi.android.mast.domain.Dispute;
import com.rmsi.android.mast.domain.DisputeType;
import com.rmsi.android.mast.domain.Person;
import com.rmsi.android.mast.domain.User;
import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.DateUtility;
import com.rmsi.android.mast.util.GuiUtility;
import com.rmsi.android.mast.util.StringUtility;

import java.util.List;

public class AddDisputeActivity extends AppCompatActivity {

    private final Context context = this;
    private CommonFunctions cf = CommonFunctions.getInstance();
    private boolean readOnly = false;
    private Long featureId = 0L;
    private Dispute dispute;
    private boolean firstRun = true;
    private PersonListFragment personsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonFunctions.getInstance().Initialize(getApplicationContext());
        cf.loadLocale(getApplicationContext());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            featureId = extras.getLong("featureid");
        }

        readOnly = CommonFunctions.isFeatureReadOnly(featureId);

        setContentView(R.layout.activity_add_dispute);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.DisputeDetails);
        if (toolbar != null)
            setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Find fields
        final Spinner spinnerDisputeType = (Spinner) findViewById(R.id.spinnerDisputeType);
        final EditText txtDisputeDescription = (EditText) findViewById(R.id.txtDisputeDescription);
        personsList = (PersonListFragment) getFragmentManager().findFragmentById(R.id.compPersonsList);
        Button btnAddPerson = (Button) findViewById(R.id.btnAddPerson);
        Button btnNext = (Button) findViewById(R.id.btnNext);

        // Init fields
        List<DisputeType> disputeTypes = DbController.getInstance(context).getDisputeTypes(true);
        spinnerDisputeType.setAdapter(new ArrayAdapter(context, android.R.layout.simple_spinner_item, disputeTypes));
        ((ArrayAdapter) spinnerDisputeType.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Customize fields
        if (readOnly) {
            btnNext.setText(getResources().getString(R.string.back));
            btnAddPerson.setVisibility(View.GONE);
            spinnerDisputeType.setEnabled(false);
            txtDisputeDescription.setEnabled(false);
        }

        // Populate values from dispute
        dispute = DbController.getInstance(context).getDisputeByProp(featureId);

        if(dispute == null){
            dispute = new Dispute();
            dispute.setFeatureId(featureId);
            dispute.setRegDate(DateUtility.getCurrentStringDate());
        } else {
            for (int i = 0; i < disputeTypes.size(); i++) {
                if (disputeTypes.get(i).getCode() == dispute.getDisputeTypeId()) {
                    spinnerDisputeType.setSelection(i);
                    break;
                }
            }
            txtDisputeDescription.setText(StringUtility.empty(dispute.getDescription()));
        }

        // Handle events
        GuiUtility.bindActionOnSpinnerChange(spinnerDisputeType, new Runnable() {
            @Override
            public void run() {
                dispute.setDisputeTypeId(((DisputeType) spinnerDisputeType.getSelectedItem()).getCode());
            }
        });

        GuiUtility.bindActionOnFieldChange(txtDisputeDescription, new Runnable() {
            @Override
            public void run() {
                dispute.setDescription(txtDisputeDescription.getText().toString());
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (readOnly){
                    finish();
                    return;
                }

                if(dispute.validate(context, true)){
                    saveData();
                    Intent myIntent = new Intent(context, MediaListActivity.class);
                    myIntent.putExtra("featureid", featureId);
                    myIntent.putExtra("disputeId", dispute.getId());
                    startActivity(myIntent);
                }
            }
        });

        btnAddPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (readOnly){
                    finish();
                } else {
                    saveData();
                }
            }
        });
    }

    private void saveData(){
        if (!dispute.validateBasicInfo(context, true))
            return;

        try {
            boolean saveResult = DbController.getInstance(context).saveDispute(dispute);

            if (saveResult) {
                Intent myIntent = new Intent(context, AddPersonActivity.class);
                myIntent.putExtra("groupid", 0L);
                myIntent.putExtra("featureid", featureId);
                myIntent.putExtra("subTypeId", 0);
                myIntent.putExtra("disputeId", dispute.getId());
                myIntent.putExtra("rightId", 0);
                startActivity(myIntent);
            } else {
                Toast.makeText(context, R.string.unable_to_save_data, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            Toast.makeText(context, R.string.unable_to_save_data, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!firstRun) {
            // Refresh disputing persons list
            List<Person> persons = DbController.getInstance(context).getDisputingPersons(dispute.getId());
            dispute.getDisputingPersons().clear();
            dispute.getDisputingPersons().addAll(persons);
        }
        firstRun = false;
        personsList.setPersons(dispute.getDisputingPersons(), readOnly);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        finish();
        return true;
    }
}
