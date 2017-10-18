package com.rmsi.android.mast.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rmsi.android.mast.Fragment.PersonListFragment;
import com.rmsi.android.mast.adapter.PersonListAdapter;
import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.Person;
import com.rmsi.android.mast.domain.Property;
import com.rmsi.android.mast.domain.ShareType;
import com.rmsi.android.mast.domain.User;
import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.GuiUtility;

import java.util.List;

public class AddNonNaturalPersonActivity extends ActionBarActivity {

    private Long featureId = 0L;
    private Long rightId = 0L;
    private final Context context = this;
    private CommonFunctions cf = CommonFunctions.getInstance();
    private boolean readOnly = false;
    private Person nonNaturalPerson;
    private Property property;
    private Spinner spinnerResident;
    private PersonListFragment personsFragment;
    private String msg, info;
    private String shareTypeLabel;
    private TextView lblShareType;
    private boolean firstRun = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            CommonFunctions.getInstance().Initialize(getApplicationContext());
        } catch (Exception e) {
        }

        cf.loadLocale(getApplicationContext());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            featureId = extras.getLong("featureid");
            rightId = extras.getLong("rightId");
        }

        DbController db = DbController.getInstance(context);
        readOnly = CommonFunctions.isFeatureReadOnly(featureId);

        setContentView(R.layout.activity_add_non_natural_person);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_add_non_natural_person);

        if (toolbar != null)
            setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        shareTypeLabel = getResources().getString(R.string.shareType);
        lblShareType = (TextView) findViewById(R.id.tenureType_lbl);
        Button btnAddPerson = (Button) findViewById(R.id.btn_save);
        Button btnNext = (Button) findViewById(R.id.btnNext);
        spinnerResident = (Spinner) findViewById(R.id.spinnerResident);
        personsFragment = (PersonListFragment) getFragmentManager().findFragmentById(R.id.compPersonsList);
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        LinearLayout personsLayout = (LinearLayout) findViewById(R.id.personsLayout);

        property = db.getProperty(featureId);

        if (property == null || property.getRight() == null) {
            cf.showToast(context, R.string.PropertyNotFound, Toast.LENGTH_SHORT);
            return;
        }

        if (property.getRight().getShareTypeId() > 0) {
            ShareType shareType = db.getShareType(property.getRight().getShareTypeId());
            if (shareType != null)
                lblShareType.setText(shareTypeLabel + ": " + shareType.getName());
        }

        if (property.getRight().getNonNaturalPerson() != null) {
            nonNaturalPerson = property.getRight().getNonNaturalPerson();
            if (nonNaturalPerson.getResident() == 1)
                spinnerResident.setSelection(1);
            else
                spinnerResident.setSelection(2);
        } else {
            nonNaturalPerson = new Person();
            nonNaturalPerson.setFeatureId(featureId);
            nonNaturalPerson.setIsNatural(0);
            nonNaturalPerson.setRightId(rightId);

            spinnerResident.setSelection(0);
        }

        // Populate attributes
        if (nonNaturalPerson.getAttributes() == null || nonNaturalPerson.getAttributes().size() < 1) {
            // Pull attributes for non natural nonNaturalPerson
            nonNaturalPerson.setAttributes(db.getAttributesByType(Attribute.TYPE_NON_NATURAL_PERSON));
        }

        if (nonNaturalPerson.getAttributes() != null) {
            GuiUtility.appendLayoutWithAttributes(mainLayout, nonNaturalPerson.getAttributes(), readOnly);
            // Move list of persons to the end
            mainLayout.removeView(personsLayout);
            mainLayout.addView(personsLayout);
        }

        if (readOnly) {
            btnAddPerson.setVisibility(View.GONE);
            btnNext.setText(getResources().getString(R.string.back));
            spinnerResident.setEnabled(false);
        }

        GuiUtility.bindActionOnSpinnerChange(spinnerResident, new Runnable() {
            @Override
            public void run() {
                if(spinnerResident.getSelectedItemPosition() == 1)
                    nonNaturalPerson.setResident(1);
                else if(spinnerResident.getSelectedItemPosition() == 2)
                    nonNaturalPerson.setResident(0);
                else
                    nonNaturalPerson.setResident(-1);
            }
        });

        btnAddPerson.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData(true);
            }
        });

        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (readOnly) {
                    finish();
                    return;
                }

                if(saveData(false) && property.validatePersonsList(context, true)){
                    Intent myIntent = new Intent(context, MediaListActivity.class);
                    myIntent.putExtra("featureid", featureId);
                    startActivity(myIntent);
                }
            }
        });
    }

    public boolean saveData(boolean showNextScreen) {
        if (showNextScreen && property.getRight() != null && property.getRight().getNaturalPersons().size() > 0) {
            msg = getResources().getString(R.string.can_add_only_one_person_with_non_natural_person);
            info = getResources().getString(R.string.info);
            cf.showMessage(context, info, msg);
            return false;
        }

        if (!nonNaturalPerson.validate(context, property.getRight().getShareTypeId(), false, true)) {
            return false;
        }

        try {
            boolean saveResult = DbController.getInstance(context).savePerson(nonNaturalPerson);

            if (saveResult) {
                if(showNextScreen) {
                    property.getRight().setNonNaturalPerson(nonNaturalPerson);
                    Intent myIntent = new Intent(context, AddPersonActivity.class);
                    myIntent.putExtra("groupid", 0L);
                    myIntent.putExtra("featureid", featureId);
                    myIntent.putExtra("subTypeId", 0);
                    myIntent.putExtra("rightId", property.getRight().getId());
                    startActivity(myIntent);
                }
                return true;
            } else {
                Toast.makeText(context, R.string.unable_to_save_data, Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            Toast.makeText(context, R.string.unable_to_save_data, Toast.LENGTH_SHORT).show();
            return false;
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

        if (!firstRun) {
            // Refresh persons list
            List<Person> persons = DbController.getInstance(context).getNaturalPersonsByRight(property.getRight().getId());
            property.getRight().getNaturalPersons().clear();
            property.getRight().getNaturalPersons().addAll(persons);
        }
        firstRun = false;
        personsFragment.setPersons(property.getRight().getNaturalPersons(), readOnly);
    }

    public boolean validate() {
        return GuiUtility.validateAttributes(nonNaturalPerson.getAttributes(), true);
    }
}
