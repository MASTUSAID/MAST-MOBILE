package com.rmsi.android.mast.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.rmsi.android.mast.domain.ShareType;
import com.rmsi.android.mast.util.CommonFunctions;

public class PersonTypeActivity extends AppCompatActivity {

    private Long featureId = 0L;
    private int shareTypeId = 0;
    private Long rightId = 0L;
    private RadioButton radioNatural;
    private CommonFunctions cf = CommonFunctions.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            CommonFunctions.getInstance().Initialize(getApplicationContext());
        } catch (Exception e) {
        }
        cf.loadLocale(getApplicationContext());

        setContentView(R.layout.activity_person_type);

        // Get input parameters
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            featureId = extras.getLong("featureid");
            shareTypeId = extras.getInt("shareTypeId");
            rightId = extras.getLong("rightId");
        }

        // Get form elements
        Button btnSave = (Button) findViewById(R.id.btn_save);
        Button btnBack = (Button) findViewById(R.id.btn_cancel);
        radioNatural = (RadioButton) findViewById(R.id.radioNatural);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.selectPersonType);

        if (toolbar != null)
            setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Disable natural person selection if share type is non-natural
        if (shareTypeId == ShareType.TYPE_NON_NATURAL) {
            radioNatural.setEnabled(false);
            radioNatural.setChecked(false);
        } else {
            radioNatural.setChecked(true);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextScreen();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

    private void showNextScreen() {
        Intent nextScreen;

        // Derive person type based on the first record
        if (radioNatural.isChecked()) {
            if (shareTypeId == ShareType.TYPE_TENANCY_IN_PROBATE) {
                nextScreen = new Intent(this, PersonListWithDPActivity.class);
            } else {
                nextScreen = new Intent(this, PersonListActivity.class);
            }
        } else {
            nextScreen = new Intent(this, AddNonNaturalPersonActivity.class);
        }
        nextScreen.putExtra("featureid", featureId);
        nextScreen.putExtra("rightId", rightId);
        startActivity(nextScreen);
    }
}
