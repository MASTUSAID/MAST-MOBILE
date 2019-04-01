package com.rmsi.android.mast.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import com.rmsi.android.mast.adapter.MediaListAdapter;
import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.domain.ConfidenceLevel;
import com.rmsi.android.mast.domain.FeatureType;
import com.rmsi.android.mast.domain.Media;
import com.rmsi.android.mast.domain.Property;
import com.rmsi.android.mast.domain.Village;
import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.GuiUtility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.rmsi.android.mast.util.CommonFunctions.getApplicationContext;

public class BoundaryActivity extends AppCompatActivity implements ListActivity {

    private final Context ctx = this;
    private DbController db = DbController.getInstance(ctx);
    private Property prop = null;
    private Long fid = 0L;
    private boolean isEditing = false;
    private CommonFunctions cf = CommonFunctions.getInstance();
    private ListView lstPhotos;
    private List<Media> mediaList;
    private static File mediaFile;
    private Toolbar toolbar;
    private String mediaFolderName = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
            CommonFunctions.parentFolderName + File.separator + CommonFunctions.mediaFolderName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boundary);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},1024);
        }

        CommonFunctions.getInstance().Initialize(getApplicationContext());
        cf.loadLocale(getApplicationContext());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fid = extras.getLong("featureid");
            isEditing = extras.getBoolean("editing");
        }
        if (fid > 0) {
            prop = DbController.getInstance(ctx).getProperty(fid);
            setTitle("Boundary Point " + Integer.toString(prop.getIpNumber()));
        }
        if(prop == null){
            prop = new Property();
        }

        // Find fields
        final EditText txtFeatureDescription = (EditText) findViewById(R.id.txtFeatureDescription);
        Spinner cbxVillages = (Spinner) findViewById(R.id.cbxNeighborVillage);
        Spinner cbxConfidenceLevels = (Spinner) findViewById(R.id.cbxConfidenceLevel);
        Spinner cbxFeatureTypes = (Spinner) findViewById(R.id.cbxFeatureType);
        Button btnAddPhoto = (Button) findViewById(R.id.btnAddPhoto);
        lstPhotos = (ListView) findViewById(R.id.lstPhotos);
        ImageButton btnSave = (ImageButton) findViewById(R.id.btnSave);

        // Villages list
        List<Village> villages = db.getVillages(true);
        cbxVillages.setAdapter(new ArrayAdapter(ctx, android.R.layout.simple_spinner_item, villages));
        ((ArrayAdapter) cbxVillages.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Confidence levels list
        List<ConfidenceLevel> confLevels = db.getConfidenceLevels(true);
        cbxConfidenceLevels.setAdapter(new ArrayAdapter(ctx, android.R.layout.simple_spinner_item, confLevels));
        ((ArrayAdapter) cbxConfidenceLevels.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Feature types list
        List<FeatureType> featureTypes = db.getFeatureTypes(false);
        cbxFeatureTypes.setAdapter(new ArrayAdapter(ctx, android.R.layout.simple_spinner_item, featureTypes));
        ((ArrayAdapter) cbxFeatureTypes.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        // Bind fields
        cbxVillages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    prop.setVillageId(((Village) parent.getItemAtPosition(position)).getId());
                } else {
                    prop.setVillageId(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        cbxConfidenceLevels.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    prop.setConfidenceLevel(((ConfidenceLevel) parent.getItemAtPosition(position)).getCode());
                } else {
                    prop.setConfidenceLevel(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        cbxFeatureTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                prop.setFeatureType(((FeatureType) parent.getItemAtPosition(position)).getCode());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        GuiUtility.bindActionOnFieldChange(txtFeatureDescription, new Runnable() {
            @Override
            public void run() {
                prop.setFeatureDescription(txtFeatureDescription.getText().toString());
            }
        });

        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        // Set field values
        if (prop.getVillageId() != null && villages != null) {
            for (int i = 0; i < villages.size(); i++) {
                if (villages.get(i).getId().compareTo(prop.getVillageId()) == 0) {
                    cbxVillages.setSelection(i);
                    break;
                }
            }
        }

        if (prop.getConfidenceLevel() != null && confLevels != null) {
            for (int i = 0; i < confLevels.size(); i++) {
                if (confLevels.get(i).getCode() == prop.getConfidenceLevel()) {
                    cbxConfidenceLevels.setSelection(i);
                    break;
                }
            }
        }

        if (prop.getFeatureType() != null && featureTypes != null) {
            for (int i = 0; i < featureTypes.size(); i++) {
                if (featureTypes.get(i).getCode() == prop.getFeatureType()) {
                    cbxFeatureTypes.setSelection(i);
                    break;
                }
            }
        }

        txtFeatureDescription.setText(prop.getFeatureDescription());

        mediaList = DbController.getInstance(ctx).getMediaByBoundary(prop.getId());
        lstPhotos.setAdapter(new MediaListAdapter(ctx, this, mediaList));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveData(){

        if(db.updateBoundaryPoint(prop)){
            Toast.makeText(ctx, "Boundary point has been saved successfully", Toast.LENGTH_SHORT).show();
            finish();
//            if(isEditing) {
//                startActivity(new Intent(ctx, ReviewDataActivity.class));
//            } else {
//                startActivity(new Intent(ctx, LandingPageActivity.class));
//            }
        } else {
            Toast.makeText(ctx, "Failed to save boundary point", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showPopup(View v, int position) {
        PopupMenu popup = new PopupMenu(ctx, v);
        MenuInflater inflater = popup.getMenuInflater();

        inflater.inflate(R.menu.media_attribute_listing_options, popup.getMenu());
        final Media media = mediaList.get(position);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit_attributes:
                        Intent myIntent = new Intent(ctx, AddMediaActivity.class);
                        myIntent.putExtra("groupid", media.getId());
                        myIntent.putExtra("featureid", prop.getId());
                        startActivity(myIntent);
                        return true;
                    case R.id.delete_entry:
                        deleteEntry(media.getId());
                        return true;
                    case R.id.view:
                        Intent intent = new Intent();
                        intent.setAction(android.content.Intent.ACTION_VIEW);
                        File file = new File(media.getPath());
                        intent.setDataAndType(Uri.fromFile(file), "image/*");
                        startActivity(intent);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

    private void deleteEntry(final Long mediaId) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
        alertDialogBuilder.setMessage(R.string.deleteEntryMsg);
        alertDialogBuilder.setPositiveButton(R.string.btn_ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        boolean result = DbController.getInstance(ctx).deleteMedia(mediaId);
                        if (result) {
                            refreshMediaList();
                        } else {
                            String info = getResources().getString(R.string.unable_delete);
                            Toast.makeText(ctx, info, Toast.LENGTH_SHORT).show();
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
    }

    private void refreshMediaList() {
        mediaList.clear();
        mediaList.addAll(DbController.getInstance(ctx).getMediaByBoundary(prop.getId()));
        ((MediaListAdapter) lstPhotos.getAdapter()).notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        refreshMediaList();
        super.onResume();
    }

    private void takePhoto() {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                String timeStamp = new SimpleDateFormat("MMdd_HHmmss").format(new Date().getTime());
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                mediaFile = new File(mediaFolderName + File.separator + "mast_" + timeStamp + ".jpg");
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mediaFile));

                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                if (cameraIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                    startActivityForResult(cameraIntent, 1);
                }
            } else {
                String timeStamp = new SimpleDateFormat("MMdd_HHmmss").format(new Date().getTime());
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                mediaFile = new File(mediaFolderName + File.separator + "mast_" + timeStamp + ".jpg");
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mediaFile));
                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                if (cameraIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                    startActivityForResult(cameraIntent, 1);
                }
            }
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            try {
                Media media = new Media();
                if (!(mediaFile == null)) {
                    if (mediaFile.length() > 200000) {
                        //picking the mediaFile and compressing it.
                        Bitmap photo = BitmapFactory.decodeStream(new FileInputStream(mediaFile));
                        ByteArrayOutputStream outFile = new ByteArrayOutputStream();
                        photo.compress(Bitmap.CompressFormat.JPEG, 20, outFile);
                        byte[] bitmapdata = outFile.toByteArray();
                        try {
                            FileOutputStream fo = new FileOutputStream(mediaFile.getPath());
                            fo.write(bitmapdata);
                            fo.flush();
                            fo.close();
                        } catch (Exception e) {
                            Log.e("no mediaFile", e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    media.setPath(mediaFile.getAbsolutePath());
                    media.setFeatureId(prop.getId());
                    media.setType(Media.TYPE_PHOTO);
                    DbController.getInstance(ctx).saveMedia(media);

                    Intent myIntent = new Intent(ctx, AddMediaActivity.class);
                    myIntent.putExtra("groupid", media.getId());
                    myIntent.putExtra("featureid", prop.getId());
                    startActivity(myIntent);
                } else {
                    Toast.makeText(ctx, R.string.error_saving_media, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e1) {
                cf.appLog("", e1);
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {

    }
}
