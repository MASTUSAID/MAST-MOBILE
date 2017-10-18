package com.rmsi.android.mast.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.rmsi.android.mast.activity.R.string;
import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.User;
import com.rmsi.android.mast.service.DownloadService;
import com.rmsi.android.mast.service.UploadService;
import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.ServiceResultReceiver;
import com.rmsi.android.mast.util.ServiceResultReceiver.Receiver;

public class LandingPageActivity extends ActionBarActivity implements Receiver {
    Context context = this;
    CommonFunctions cf = CommonFunctions.getInstance();
    int roleId = 0;
    ServiceResultReceiver mReceiver;
    public static final int STATUS_RUNNING = 0;
    public static final int DOWNLOAD_STATUS_FINISHED = 1;
    public static final int DOWNLOAD_STATUS_ERROR = 2;
    public static final int UPLOAD_STATUS_FINISHED = 3;
    public static final int UPLOAD_STATUS_ERROR = 4;
    public static final int UPLOAD_STATUS_NO_DATA = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initializing context in common functions in case of a crash
        try {
            CommonFunctions.getInstance().Initialize(getApplicationContext());
        } catch (Exception e) {
        }
        cf.loadLocale(getApplicationContext());

        setContentView(R.layout.activity_landing_page);

        roleId = CommonFunctions.getRoleID();
        mReceiver = new ServiceResultReceiver(new Handler());
        mReceiver.setReceiver(this);

        if (roleId == User.ROLE_TRUSTED_INTERMEDIARY) {
            findViewById(R.id.ad_menu1).setVisibility(View.GONE);
            findViewById(R.id.ad_view1).setVisibility(View.GONE);
        } else if (roleId == User.ROLE_ADJUDICATOR) {
            findViewById(R.id.ti_menu1).setVisibility(View.GONE);
            findViewById(R.id.ti_menu2).setVisibility(View.GONE);
            findViewById(R.id.ti_view1).setVisibility(View.GONE);
            findViewById(R.id.ti_view2).setVisibility(View.GONE);
        } else {
            findViewById(R.id.ad_menu1).setVisibility(View.GONE);
            findViewById(R.id.ad_view1).setVisibility(View.GONE);
        }

        Button btn_mapviewer = (Button) findViewById(R.id.mapviewer);
        btn_mapviewer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(LandingPageActivity.this, MapViewerActivity.class);
                intent.putExtra("role", roleId);
                startActivity(intent);
            }
        });

        Button btn_captureNewData = (Button) findViewById(R.id.capturedata);
        btn_captureNewData.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                DbController sqllite = DbController.getInstance(context);
                if (sqllite.getClaimTypes(false).size() > 0) {
                    Intent intent = new Intent(LandingPageActivity.this, CaptureDataMapActivity.class);
                    startActivity(intent);
                } else {
                    String info = getResources().getString(string.info);
                    String msg = getResources().getString(string.download_data_first);
                    cf.showMessage(context, info, msg);
                }
            }
        });

        Button btn_review = (Button) findViewById(R.id.reviewdata);
        btn_review.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(LandingPageActivity.this, ReviewDataActivity.class);
                startActivity(intent);
            }
        });

        Button btn_verify = (Button) findViewById(R.id.verifydata);
        btn_verify.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(LandingPageActivity.this, VerifyDataActivity.class);
                startActivity(intent);
            }
        });


        Button btn_userPref = (Button) findViewById(R.id.userpref);
        btn_userPref.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(LandingPageActivity.this, UserPreferences.class);
                startActivity(intent);
            }
        });


        Button btn_exit = (Button) findViewById(R.id.logout);
        btn_exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                DbController sqllite = DbController.getInstance(context);
                boolean pendingRecords = sqllite.checkPendingDraftAndCompletedRecordsToSync();
                sqllite.close();
                if (!pendingRecords) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setMessage(R.string.logoutWarningMsg);
                    alertDialogBuilder.setPositiveButton(R.string.continueTologout,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    boolean isDatabaseBackedup = exportDB();
                                    if (isDatabaseBackedup) {
                                        Intent i = new Intent(context, LoginActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                    } else {
                                        Toast.makeText(context, R.string.unable_logout, Toast.LENGTH_SHORT).show();
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


                } else {
                    String syncMsg = getResources().getString(R.string.sync_pending_records);
                    String warning = getResources().getString(R.string.warning);
                    cf.showMessage(context, warning, syncMsg);
                }
            }
        });

        Button btnDownloadData = (Button) findViewById(R.id.downloaddata);
        btnDownloadData.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (cf.getConnectivityStatus()) {
                    User user = DbController.getInstance(context).getLoggedUser();
                    if (user != null && user.getUserId() != null) {
                        String connectionMsg = getResources().getString(R.string.webserviceconnectMsg);
                        showToast(connectionMsg, Toast.LENGTH_SHORT);
                        Intent intent = new Intent(context, DownloadService.class);
                        intent.putExtra("userid", user.getUserId().toString());
                        intent.putExtra("downloadType", "data");
                        intent.putExtra("receiver", mReceiver);
                        startService(intent);
                    }
                } else
                    cf.showIntenetSettingsAlert(context);
            }
        });

        Button btnDownloadConfig = (Button) findViewById(R.id.downloadConfig);
        btnDownloadConfig.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (cf.getConnectivityStatus()) {
                    User user = DbController.getInstance(context).getLoggedUser();
                    if (user != null && user.getUserId() != null) {
                        String connectionMsg = getResources().getString(R.string.webserviceconnectMsg);
                        showToast(connectionMsg, Toast.LENGTH_SHORT);
                        Intent intent = new Intent(context, DownloadService.class);
                        intent.putExtra("userid", user.getUserId().toString());
                        intent.putExtra("downloadType", "config");
                        intent.putExtra("receiver", mReceiver);
                        startService(intent);
                    }
                } else
                    cf.showIntenetSettingsAlert(context);
            }
        });

        Button btn_sync = (Button) findViewById(R.id.syncdata);
        btn_sync.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (cf.getConnectivityStatus()) {
                    String connectionMsg = getResources().getString(R.string.webserviceconnectMsg);
                    showToast(connectionMsg, Toast.LENGTH_SHORT);
                    Intent intent = new Intent(context, UploadService.class);
                    intent.putExtra("receiver", mReceiver);
                    startService(intent);
                } else
                    cf.showIntenetSettingsAlert(context);
            }
        });
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        //String info=getResources().getString(R.string.info);
        String DataDownloadCompleted = getResources().getString(R.string.DataDownloadCompleted);
        String ErrorInDownloadingData = getResources().getString(R.string.ErrorInDownloadingData);
        String DataUploadedSuccessfully = getResources().getString(R.string.DataUploadedSuccessfully);
        String ErrorInUploadingData = getResources().getString(R.string.ErrorInUploadingData);
        String NoDataPendingforUpload = getResources().getString(R.string.NoDataPendingforUpload);
        switch (resultCode) {
            case STATUS_RUNNING:
                break;
            case DOWNLOAD_STATUS_FINISHED:
                showToast(DataDownloadCompleted, Toast.LENGTH_LONG);
                break;
            case DOWNLOAD_STATUS_ERROR:
                showToast(ErrorInDownloadingData, Toast.LENGTH_LONG);
                break;
            case UPLOAD_STATUS_FINISHED:
                showToast(DataUploadedSuccessfully, Toast.LENGTH_LONG);
                break;
            case UPLOAD_STATUS_ERROR:
                showToast(ErrorInUploadingData, Toast.LENGTH_LONG);
                break;
            case UPLOAD_STATUS_NO_DATA:
                showToast(NoDataPendingforUpload, Toast.LENGTH_LONG);
                break;
        }
    }

    public void showMessage(String header, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LandingPageActivity.this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setTitle(header);
        String ok = getResources().getString(R.string.btn_ok);
        alertDialogBuilder.setNegativeButton(ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        alertDialogBuilder.show();
    }

    private void showToast(String message, int length) {
        Toast toast = Toast.makeText(context, message, length);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private boolean exportDB() {
        String timeStamp = new SimpleDateFormat("MMdd_HHmmss", Locale.US).format(new Date().getTime());
        String Ext_Storage_Path = Environment.getExternalStorageDirectory().getAbsolutePath();

        String currDBPATH = "/" + CommonFunctions.parentFolderName + "/" + CommonFunctions.dbFolderName + "/mast_mobile.db";
        String backupDBPATH = "/" + CommonFunctions.parentFolderName + "/" + CommonFunctions.dbFolderName + "/Backup_mast_mobile_" + timeStamp + ".db";

        File currentDB = new File(Ext_Storage_Path, currDBPATH);
        File backupDB = new File(Ext_Storage_Path, backupDBPATH);
        try {
            boolean success = currentDB.renameTo(backupDB);
            if (success)
                cf.addErrorMessage("LOGOUT >> ", "DB Exported!!"); // logging logout time
            else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            cf.appLog("", e);
            return false;
        }
        return true;
    }
}


