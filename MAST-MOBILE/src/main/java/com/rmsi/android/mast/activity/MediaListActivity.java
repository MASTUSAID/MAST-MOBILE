package com.rmsi.android.mast.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;
import com.rmsi.android.mast.adapter.MediaListAdapter;
import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.Media;
import com.rmsi.android.mast.domain.User;
import com.rmsi.android.mast.util.CommonFunctions;

public class MediaListActivity extends ActionBarActivity implements ListActivity {
    private Button addNew, btnNext;
    private Context context;
    private ListView listView;
    private Long featureId = 0L;
    private Long disputeId = 0L;
    private CommonFunctions cf = CommonFunctions.getInstance();
    private String mediaFolderName = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
            CommonFunctions.parentFolderName + File.separator + CommonFunctions.mediaFolderName;
    private String timeStamp = new SimpleDateFormat("MMdd_HHmmss").format(new Date().getTime());
    private static File file;
    private FileOutputStream fo;
    private boolean readOnly = false;
    private List<Media> mediaList;
    private boolean hasCustomAttributes = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            CommonFunctions.getInstance().Initialize(getApplicationContext());
        } catch (Exception e) {
        }
        cf.loadLocale(getApplicationContext());
        context = this;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            featureId = extras.getLong("featureid");
            disputeId = extras.getLong("disputeId");
        }

        setContentView(R.layout.activity_media_list);

        readOnly = CommonFunctions.isFeatureReadOnly(featureId);
        addNew = (Button) findViewById(R.id.btn_addNew);
        btnNext = (Button) findViewById(R.id.btnNext);
        listView = (ListView) findViewById(R.id.list_view);
        TextView emptyText = (TextView) findViewById(android.R.id.empty);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_media_list);
        if (toolbar != null)
            setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(disputeId > 0)
            mediaList = DbController.getInstance(context).getMediaByDispute(disputeId);
        else
            mediaList = DbController.getInstance(context).getMediaByProp(featureId);

        listView.setEmptyView(emptyText);
        listView.setAdapter(new MediaListAdapter(context, this, mediaList));

        // Change next button caption to final if no custom attributes exist
        hasCustomAttributes = DbController.getInstance(context).getAttributesByType(Attribute.TYPE_CUSTOM).size() > 0;

        if(!hasCustomAttributes || disputeId > 0)
            btnNext.setText(getResources().getString(R.string.Finish));

        if (readOnly) {
            addNew.setVisibility(View.GONE);
            btnNext.setText(getResources().getString(R.string.back));
        }

        addNew.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (readOnly) {
                    finish();
                } else {
                    if(disputeId < 1 && hasCustomAttributes){
                        Intent myIntent = new Intent(context, AddCustomAttribActivity.class);
                        myIntent.putExtra("featureid", featureId);
                        startActivity(myIntent);
                    } else {
                        Intent myIntent = new Intent(context, DataSummaryActivity.class);
                        myIntent.putExtra("featureid", featureId);
                        myIntent.putExtra("className", "PersonListActivity");
                        startActivity(myIntent);
                    }
                }
            }
        });
    }

    @Override
    public void showPopup(View v, int position) {
        PopupMenu popup = new PopupMenu(context, v);
        MenuInflater inflater = popup.getMenuInflater();

        if (!readOnly) {
            inflater.inflate(R.menu.media_attribute_listing_options, popup.getMenu());
        } else {
            inflater.inflate(R.menu.media_attribute_listing_options_for_adjudicator, popup.getMenu());
        }

        final Media media = mediaList.get(position);

        popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit_attributes:
                        //Open attributes form to edit --------------
                        Intent myIntent = new Intent(context, AddMediaActivity.class);
                        myIntent.putExtra("groupid", media.getId());
                        myIntent.putExtra("featureid", featureId);
                        myIntent.putExtra("disputeId", disputeId);
                        startActivity(myIntent);
                        return true;
                    case R.id.delete_entry:
                        deleteEntry(media.getId());
                        return true;
                    case R.id.view:
                        if (media.getType().equalsIgnoreCase("image")) {
                            Intent intent = new Intent();
                            intent.setAction(android.content.Intent.ACTION_VIEW);
                            File file = new File(media.getPath());
                            intent.setDataAndType(Uri.fromFile(file), "image/*");
                            startActivity(intent);
                        }
                        if (media.getType().equalsIgnoreCase("video")) {
                            Intent intent = new Intent();
                            intent.setAction(android.content.Intent.ACTION_VIEW);
                            File file = new File(media.getPath());
                            intent.setDataAndType(Uri.fromFile(file), "video/*");
                            startActivity(intent);
                        }
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

    private void deleteEntry(final Long mediaId) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(R.string.deleteEntryMsg);
        alertDialogBuilder.setPositiveButton(R.string.btn_ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        boolean result = DbController.getInstance(context).deleteMedia(mediaId);
                        if (result) {
                            refreshList();
                        } else {
                            String info = getResources().getString(R.string.unable_delete);
                            Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
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

    private void refreshList() {
        mediaList.clear();
        if(disputeId > 0)
            mediaList.addAll(DbController.getInstance(context).getMediaByDispute(disputeId));
        else
            mediaList.addAll(DbController.getInstance(context).getMediaByProp(featureId));

        ((MediaListAdapter) listView.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        refreshList();
        super.onResume();
    }

    private void showDialog() {
        try {
            String[] media_options = getResources().getStringArray(R.array.media);

            final Dialog dialog = new Dialog(context, R.style.DialogTheme);
            dialog.setContentView(R.layout.dialog_show_list);
            dialog.setTitle(getResources().getString(R.string.selectOptionDialogTitle));
            dialog.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
            ListView listViewForMedia = (ListView) dialog.findViewById(R.id.commonlistview);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.item_list_single_choice, media_options);
            Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);
            btnOk.setVisibility(View.GONE);
            listViewForMedia.setAdapter(adapter);

            listViewForMedia.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent,
                                        View view, int position, long id) {
                    try {
                        int itemPosition = position;
                        if (itemPosition == 0) // Image
                        {
                            timeStamp = new SimpleDateFormat("MMdd_HHmmss").format(new Date().getTime());
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            file = new File(mediaFolderName + File.separator + "mast_" + timeStamp + ".jpg");
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                            startActivityForResult(cameraIntent, 1);
                            dialog.dismiss();
                        } else if (itemPosition == 1) // video
                        {
                            timeStamp = new SimpleDateFormat("MMdd_HHmmss").format(new Date().getTime());
                            Intent videoIntent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
                            file = new File(mediaFolderName + File.separator + "mast_" + timeStamp + ".mp4");
                            videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                            videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                            long maxsize = 1 * 1024 * 1024;
                            videoIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, maxsize);  // 3MB
                            startActivityForResult(videoIntent, 2);
                            dialog.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        cf.appLog("", e);
                    }
                }
            });

            dialog.show();
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Media.TYPE_PHOTO_CODE)
        {
            if (resultCode == RESULT_OK) {
                try {
                    //photo = rotate(photo, 90);
                    Media media = new Media();
                    if (!(file == null)) {

                        if (file.length() > 200000) {
                            //picking the file and compressing it.
                            Bitmap photo = BitmapFactory
                                    .decodeStream(new FileInputStream(file));
                            ByteArrayOutputStream outFile = new ByteArrayOutputStream();
                            photo.compress(Bitmap.CompressFormat.JPEG, 20, outFile);
                            byte[] bitmapdata = outFile.toByteArray();
                            try {
                                fo = new FileOutputStream(file.getPath());
                                fo.write(bitmapdata);
                                fo.flush();
                                fo.close();
                            } catch (Exception e) {
                                Log.e("no file", e.getMessage());
                                e.printStackTrace();
                            }
                        }
                        media.setPath(file.getAbsolutePath());
                        media.setFeatureId(featureId);
                        media.setDisputeId(disputeId);
                        media.setType(Media.TYPE_PHOTO);
                        DbController.getInstance(context).saveMedia(media);

                        Intent myIntent = new Intent(context, AddMediaActivity.class);
                        myIntent.putExtra("groupid", media.getId());
                        myIntent.putExtra("featureid", featureId);
                        myIntent.putExtra("disputeId", disputeId);
                        startActivity(myIntent);
                    } else {
                        Toast.makeText(context, R.string.error_saving_media, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e1) {
                    cf.appLog("", e1);
                    e1.printStackTrace();
                }
            }
        }
        if (requestCode == Media.TYPE_VIDEO_CODE)
        {
            if (resultCode == RESULT_OK) {
                try {
                    Media media = new Media();
                    media.setPath(file.getAbsolutePath());
                    media.setFeatureId(featureId);
                    media.setType(Media.TYPE_VIDEO);
                    media.setDisputeId(disputeId);
                    DbController.getInstance(context).saveMedia(media);

                    Intent myIntent = new Intent(context, AddMediaActivity.class);
                    myIntent.putExtra("groupid", media.getId());
                    myIntent.putExtra("featureid", featureId);
                    myIntent.putExtra("disputeId", disputeId);
                    startActivity(myIntent);
                } catch (Exception e) {
                    cf.appLog("", e);
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Video was not captured", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
