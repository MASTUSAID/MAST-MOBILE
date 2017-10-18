package com.rmsi.android.mast.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.rmsi.android.mast.adapter.AttributeAdapter;
import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.Media;
import com.rmsi.android.mast.domain.User;
import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.GuiUtility;

public class AddMediaActivity extends ActionBarActivity {
    private ListView listView;
    private final Context context = this;
    private CommonFunctions cf = CommonFunctions.getInstance();
    private Long mediaId = 0L;
    private Long featureId = 0L;
    private boolean readOnly = false;
    private Media media = null;
    private Long disputeId = 0L;

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
            mediaId = extras.getLong("groupid");
            featureId = extras.getLong("featureid");
            disputeId = extras.getLong("disputeId");
        }

        readOnly = CommonFunctions.isFeatureReadOnly(featureId);

        setContentView(R.layout.activity_add_media);
        listView = (ListView) findViewById(R.id.list_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_add_media);
        if (toolbar != null)
            setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button btnSave = (Button) findViewById(R.id.btn_save);

        if (readOnly) {
            btnSave.setEnabled(false);
        }

        if(mediaId != null && mediaId > 0){
            media = db.getMedia(mediaId);
        }

        if(media == null){
            media = new Media();
            media.setFeatureId(featureId);
            media.setDisputeId(disputeId);
        }

        // Populate attributes
        if (media.getAttributes() == null || media.getAttributes().size() < 1) {
            // Pull attributes for natural person
            media.setAttributes(db.getAttributesByType(Attribute.TYPE_MULTIMEDIA));
        }

        try {
            listView.setAdapter(new AttributeAdapter(context, media.getAttributes(), readOnly));
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        }

        btnSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    public void saveData() {
        if (media.validate(context, true)) {
            try {
                boolean saveResult = DbController.getInstance(context).saveMedia(media);

                if (saveResult) {
                    cf.showToast(context, R.string.data_saved, Toast.LENGTH_SHORT);
                    finish();
                } else {
                    cf.showToast(context, R.string.unable_to_save_data, Toast.LENGTH_SHORT);
                }
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
                Toast.makeText(context, R.string.unable_to_save_data, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, R.string.fill_mandatory, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (readOnly) {
            finish();
        }
    }
}
