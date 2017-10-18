package com.rmsi.android.mast.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.Text;
import com.rmsi.android.mast.adapter.AttributeAdapter;
import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.domain.AcquisitionType;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.Media;
import com.rmsi.android.mast.domain.Person;
import com.rmsi.android.mast.domain.ShareType;
import com.rmsi.android.mast.domain.User;
import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.DateUtility;
import com.rmsi.android.mast.util.GuiUtility;
import com.rmsi.android.mast.util.StringUtility;

public class AddPersonActivity extends ActionBarActivity {
    private List<Media> mediaList;
    private final Context context = this;
    private CommonFunctions cf = CommonFunctions.getInstance();
    private Long featureId = 0L;
    private Long rightId = 0L;
    private Long disputeId = 0L;
    private myImageAdapter adapter;
    private List<Bitmap> imageFile = new ArrayList<Bitmap>();
    private int subTypeId;
    private Spinner spinnerResident;
    private Person person = null;
    private ShareType shareType;
    private boolean readOnly = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            CommonFunctions.getInstance().Initialize(getApplicationContext());
        } catch (Exception e) {
        }
        cf.loadLocale(getApplicationContext());

        Long personId = 0L;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            personId = extras.getLong("groupid");
            featureId = extras.getLong("featureid");
            readOnly = extras.getBoolean("readOnly", false);
            rightId = extras.getLong("rightId");
            subTypeId = extras.getInt("subTypeId");
            disputeId = extras.getLong("disputeId");
        }

        setContentView(R.layout.activity_add_person);

        Button btnSave = (Button) findViewById(R.id.btn_savePerson);
        Button btnCancel = (Button) findViewById(R.id.btn_cancelPerson);
        spinnerResident = (Spinner) findViewById(R.id.spinnerResident);
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        LinearLayout shareSizeLayout = (LinearLayout) findViewById(R.id.shareSizeLayout);
        LinearLayout acquisitionLayout = (LinearLayout) findViewById(R.id.acquisitionLayout);
        final Spinner spinnerAcquisition = (Spinner) findViewById(R.id.spinnerAcquisition);
        final EditText txtShareSize = (EditText) findViewById(R.id.txtShareSize);

        GridView gridView = (GridView) findViewById(R.id.gridView_image);
        adapter = new myImageAdapter(context);
        gridView.setAdapter(adapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.AddPerson);
        if (toolbar != null)
            setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DbController db = DbController.getInstance(context);

        // Init fields
        List<AcquisitionType> acquisitionTypes = null;
        if(disputeId > 0) {
            acquisitionTypes = DbController.getInstance(context).getAcquisitionTypes(true);
            spinnerAcquisition.setAdapter(new ArrayAdapter(context, android.R.layout.simple_spinner_item, acquisitionTypes));
            ((ArrayAdapter) spinnerAcquisition.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }

        shareType = db.getShareTypeByRight(rightId);

        if (personId != null && personId > 0) {
            person = db.getPerson(personId);
        }

        if (person == null) {
            person = new Person();
            person.setFeatureId(featureId);
            person.setIsNatural(1);
            person.setRightId(rightId);
            person.setSubTypeId(subTypeId);
            person.setDisputeId(disputeId);

            spinnerResident.setSelection(0);
        } else {
            // Set values
            if (person.getResident() == 1)
                spinnerResident.setSelection(1);
            else
                spinnerResident.setSelection(2);

            if(disputeId > 0) {
                for (int i = 0; i < acquisitionTypes.size(); i++) {
                    if (acquisitionTypes.get(i).getCode() == person.getAcquisitionTypeId()) {
                        spinnerAcquisition.setSelection(i);
                        break;
                    }
                }
            }

            txtShareSize.setText(StringUtility.empty(person.getShare()));
            processPersonPhoto();
        }

        if(disputeId < 1){
            acquisitionLayout.setVisibility(View.GONE);
        } else {
            GuiUtility.bindActionOnSpinnerChange(spinnerAcquisition, new Runnable() {
                @Override
                public void run() {
                    person.setAcquisitionTypeId(((AcquisitionType)spinnerAcquisition.getSelectedItem()).getCode());
                }
            });
        }

        if(shareType != null && shareType.getCode() == ShareType.TYPE_MUTIPLE_OCCUPANCY_IN_COMMON){
            GuiUtility.bindActionOnFieldChange(txtShareSize, new Runnable() {
                @Override
                public void run() {
                    person.setShare(StringUtility.empty(txtShareSize.getText().toString()));
                }
            });
        } else {
            shareSizeLayout.setVisibility(View.GONE);
        }

        // Populate attributes
        if (person.getAttributes() == null || person.getAttributes().size() < 1) {
            // Pull attributes for natural person
            person.setAttributes(db.getAttributesByType(Attribute.TYPE_NATURAL_PERSON));
        }

        if (person.getAttributes() != null) {
            GuiUtility.appendLayoutWithAttributes(mainLayout, person.getAttributes(), readOnly);
            // Move photo to the end
            mainLayout.removeView(gridView);
            mainLayout.addView(gridView);

            // Disable age attribute if dob exists. Set age value based on dob field
            final Attribute dob = person.getAttribute(Person.ATTRIBUTE_DOB);
            final Attribute age = person.getAttribute(Person.ATTRIBUTE_AGE);

            if(dob != null && age != null){
                age.getView().setEnabled(false);
                GuiUtility.bindActionOnLabelChange((TextView)dob.getView(), new Runnable() {
                    @Override
                    public void run() {
                        String date = ((TextView)dob.getView()).getText().toString();
                        if(StringUtility.isEmpty(date)){
                            ((EditText)age.getView()).setText("");
                        } else {
                            ((EditText)age.getView()).setText(
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

        GuiUtility.bindActionOnSpinnerChange(spinnerResident, new Runnable() {
            @Override
            public void run() {
                if (spinnerResident.getSelectedItemPosition() == 1)
                    person.setResident(1);
                else if (spinnerResident.getSelectedItemPosition() == 2)
                    person.setResident(0);
                else
                    person.setResident(-1);
            }
        });

        if (readOnly) {
            btnSave.setVisibility(View.GONE);
            spinnerResident.setEnabled(false);
            spinnerAcquisition.setEnabled(false);
        }

        btnSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
        btnCancel.setOnClickListener(new OnClickListener() {
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

    public void saveData() {
        int shareCode = 0;
        if(shareType != null){
            shareCode = shareType.getCode();
        }

        if (!person.validate(context, shareCode, disputeId > 0, true)) {
            return;
        }

        try {
            boolean saveResult = DbController.getInstance(context).savePerson(person);

            if (saveResult) {
                finish();
            } else {
                Toast.makeText(context, R.string.unable_to_save_data, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            Toast.makeText(context, R.string.unable_to_save_data, Toast.LENGTH_SHORT).show();
        }
    }

    public class myImageAdapter extends BaseAdapter {
        private Context mContext;

        public int getCount() {
            return imageFile.size();
        }

        public Object getItem(int position) {
            return imageFile.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        public myImageAdapter(Context c) {
            mContext = c;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);
                //  imageView.setRotation(90);
                imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageBitmap(imageFile.get(position));
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    File file = new File(mediaList.get(pos).getPath());
                    intent.setDataAndType(Uri.fromFile(file), "image/*");
                    startActivity(intent);
                }
            });

            imageView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View arg0) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setMessage(R.string.alert_delete_photo);
                    alertDialogBuilder.setPositiveButton(R.string.btn_ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    DbController db = DbController.getInstance(context);
                                    boolean isDeleted = db.deleteMediaByPerson(person.getId());
                                    if (isDeleted) {
                                        Toast.makeText(context, R.string.pic_delete_msg, Toast.LENGTH_LONG).show();
                                        processPersonPhoto();
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(context, "error", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                    alertDialogBuilder.setNegativeButton(R.string.btn_cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    return true;
                }
            });
            return imageView;
        }
    }

    public boolean validate() {
        return GuiUtility.validateAttributes(person.getAttributes(), true);
    }

    private void processPersonPhoto() {
        mediaList = DbController.getInstance(context).getMediaByPerson(person.getId());
        imageFile.clear();
        for (int i = 0; i < mediaList.size(); i++) {
            try {
                Bitmap ThumbImage = cf.getSampleBitmapFromFile(mediaList.get(i).getPath(), 48, 48);
                imageFile.add(ThumbImage);
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
    }
}