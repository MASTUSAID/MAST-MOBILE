package com.rmsi.android.mast.service;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.activity.LandingPageActivity;
import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.domain.User;
import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.MultipartUtility;

public class UploadService extends IntentService {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 3;
    public static final int STATUS_ERROR = 4;
    public static final int STATUS_NO_DATA = 5;
    private int notificationID = 100;
    private int numMessages = 0;
    private NotificationManager mNotificationManager;
    CommonFunctions cf = CommonFunctions.getInstance();
    private static final String TAG = "UploadService";
    private String SERVER_ADDRESS = cf.getServerAddress();
    boolean nodata = false;
    static int timeout = 10000; // 10 seconds 

    public UploadService() {
        super(UploadService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String uploadSuccessmsg = getResources().getString(R.string.FieldDataUploadedSuccessfully);
        String uploadFailureMsg = getResources().getString(R.string.ErrorInUploadingFieldData);
        String multimediaSuccessmsg = getResources().getString(R.string.MultimediaUploadedSuccessfully);
        String multimediaFailuremsg = getResources().getString(R.string.ErrorInUploadingMultimedia);
        String successMsgDisplay[] = {uploadSuccessmsg, multimediaSuccessmsg};
        String failureMsgDisplay[] = {uploadFailureMsg, multimediaFailuremsg};
        StringBuffer notificationMsg = new StringBuffer();

        //Initializing context in common functions in case of a crash
        try {
            CommonFunctions.getInstance().Initialize(getApplicationContext());
        } catch (Exception e) {
        }

        String Uploading = getResources().getString(R.string.Uploading);
        String ConnectingtoWebService = getResources().getString(R.string.ConnectingtoWebService);
        String NoDataPendingforUpload = getResources().getString(R.string.NoDataPendingforUpload);
        String NoDataFoundToUpload = getResources().getString(R.string.NoDataFoundToUpload);
        String UploadFinished = getResources().getString(R.string.UploadFinished);
        String DataUploadedSuccessfully = getResources().getString(R.string.DataUploadedSuccessfully);
        String Error = getResources().getString(R.string.Error);
        String UnableToUpload = getResources().getString(R.string.UnableToUpload);
        String ErrorInUploadingData = getResources().getString(R.string.ErrorInUploadingData);
        String UploadingError = getResources().getString(R.string.UploadingError);
        int roleid = CommonFunctions.getRoleID();

        Log.d(TAG, "Upload Service Started!");
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        displayNotification("MAST", Uploading, ConnectingtoWebService);

        try {
            if (roleid == User.ROLE_TRUSTED_INTERMEDIARY) {
                boolean results = uploadData();

                if (results) {
                    notificationMsg.append(successMsgDisplay[0]);
                } else {
                    notificationMsg.append(failureMsgDisplay[0]);
                }

                if (results) {
                    results = startMultimediaUpload();

                    notificationMsg.append("\n");
                    if (results && nodata) {
                        updateNotification("MAST", NoDataPendingforUpload, NoDataFoundToUpload);
                        if (receiver != null) receiver.send(STATUS_NO_DATA, Bundle.EMPTY);
                    } else if (results) {
                        notificationMsg.append(successMsgDisplay[1]);
                        updateNotification("MAST", notificationMsg.toString(), UploadFinished);
                        receiver.send(STATUS_FINISHED, Bundle.EMPTY);
                    } else {
                        notificationMsg.append(failureMsgDisplay[1]);
                        updateNotification("MAST", notificationMsg.toString(), Error);
                        receiver.send(STATUS_ERROR, Bundle.EMPTY);
                    }
                } else {
                    updateNotification("MAST", UnableToUpload, Error);
                    if (receiver != null) receiver.send(STATUS_ERROR, Bundle.EMPTY);
                }
                //fetchRejectedFeatures();
            } else {
                boolean results = uploadverifiedData();
                if (results && nodata) {
                    updateNotification("MAST", NoDataPendingforUpload, NoDataFoundToUpload);
                    if (receiver != null) receiver.send(STATUS_NO_DATA, Bundle.EMPTY);
                } else if (results) {
                    updateNotification("MAST", DataUploadedSuccessfully, UploadFinished);
                    if (receiver != null) receiver.send(STATUS_FINISHED, Bundle.EMPTY);
                } else {
                    updateNotification("MAST", UnableToUpload, Error);
                    if (receiver != null) receiver.send(STATUS_ERROR, Bundle.EMPTY);
                }
            }
        } catch (IOException e) {
            String unableToConnect = getResources().getString(R.string.UnableToConnectToTheServer);
            String timeOut = getResources().getString(R.string.ConnectionTimeout);
            updateNotification("MAST", unableToConnect, timeOut);
            if (receiver != null) receiver.send(STATUS_ERROR, Bundle.EMPTY);
            e.printStackTrace();
            cf.syncLog("", e);
        } catch (Exception e) {
            updateNotification("MAST", ErrorInUploadingData, UploadingError);
            if (receiver != null) receiver.send(STATUS_ERROR, Bundle.EMPTY);
            e.printStackTrace();
            cf.syncLog("", e);
        }
        Log.d(TAG, "Service Stopping!");
    }

    private boolean uploadData() throws IOException {
        String requestUrl = SERVER_ADDRESS + "/mast/sync/mobile/attributes/sync/";
        DbController db = DbController.getInstance(getApplicationContext());

        InputStream is = null;
        String syncData = db.getProjectDataForUpload();
        if (!TextUtils.isEmpty(syncData)) {
            try {
                StringBuilder postData = new StringBuilder();
                postData.append(URLEncoder.encode("projectName", "UTF-8") + "=" + URLEncoder.encode(db.getProjectname(), "UTF-8"));
                postData.append("&");
                postData.append(URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(db.getLoggedUser().getUserId().toString(), "UTF-8"));
                postData.append("&");
                postData.append(URLEncoder.encode("data", "UTF-8") + "=" + URLEncoder.encode(syncData, "UTF-8"));

                HttpURLConnection conn = (HttpURLConnection) new URL(requestUrl).openConnection();
                conn.setReadTimeout(1000000);
                conn.setConnectTimeout(1000000);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("charset", "utf-8");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);

                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.write(postData.toString().getBytes("UTF-8"));
                wr.flush();
                wr.close();

                if (conn.getResponseCode() > 1) {
                    is = conn.getInputStream();
                    // Convert the InputStream into a string
                    String response = CommonFunctions.getStringFromInputStream(is);

                    if (!TextUtils.isEmpty(response) && !response.contains("Exception")) {
                        return db.updateServerFeatureId(response);
                    } else {
                        cf.addErrorMessage("UploadService", response);
                        return false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                cf.syncLog("", e);
                return false;
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        } else
            nodata = true;
        return true;
    }


    private boolean uploadMultimedia(String filepath, String attribData) {
        nodata = false;
        String requestUrl = SERVER_ADDRESS + "/mast/sync/mobile/document/upload/";
        try {
            MultipartUtility multipart = new MultipartUtility(requestUrl, "UTF-8");

            multipart.addFormField("fileattribs", attribData);

            multipart.addFilePart("file", new File(filepath));

            String json_string = multipart.finish();

            if (!TextUtils.isEmpty(json_string) && !json_string.contains("Exception") && !json_string.contains("error")) {
                boolean synced = DbController.getInstance(getApplicationContext()).updateMediaSyncedStatus(json_string, CommonFunctions.MEDIA_SYNC_COMPLETED);
                if (synced)
                    return true;
                else
                    return false;
            } else {
                cf.addErrorMessage("UploadService", json_string);
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            cf.syncLog("", e);
            return false;
        } finally {
        }
    }

    private boolean startMultimediaUpload() {
        boolean mediaAvailable = false;
        do {
            try {
                JSONArray syncDataObj = DbController.getInstance(getApplicationContext()).getMultimediaforUpload();
                if (syncDataObj.length() > 0) {
                    mediaAvailable = true;
                    String filepath = syncDataObj.getJSONArray(0).getString(3);
                    String attribData = syncDataObj.toString();
                    int mediaId = syncDataObj.getJSONArray(0).getInt(2);
                    boolean uploadResult = uploadMultimedia(filepath, attribData);

                    if (!uploadResult)
                        DbController.getInstance(getApplicationContext()).updateMediaSyncedStatus(mediaId + "", CommonFunctions.MEDIA_SYNC_ERROR);
                } else {
                    if (mediaAvailable)
                        DbController.getInstance(getApplicationContext()).resetMediaStatus();
                    mediaAvailable = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                cf.syncLog("", e);
                return false;
            }
        }
        while (mediaAvailable);
        return true;
    }

    private void fetchRejectedFeatures() {
        InputStream is = null;
        try {
            User user = DbController.getInstance(getApplicationContext()).getLoggedUser();
            if (user != null && user.getUserId() != null) {
                String requestUrl = SERVER_ADDRESS + "/mast/sync/mobile/sync/RejectedSpatialUnit/" + user.getUserId();

                URL url = new URL(requestUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(100000 /* milliseconds */);
                conn.setConnectTimeout(timeout /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();

                if (response > 1) {
                    is = conn.getInputStream();
                    // Convert the InputStream into a string
                    String json_string = CommonFunctions.getStringFromInputStream(is);

                    if (!TextUtils.isEmpty(json_string) && !json_string.contains("Exception")) {
                        DbController.getInstance(getApplicationContext()).setRejectedStatus(json_string);
                    }
                }
            }
        } catch (Exception e) {
            cf.syncLog("", e);
            e.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (Exception e) {
            }
        }
    }

    protected void displayNotification(String title, String content, String ticker) {
        Log.i("Start", "notification");

		/* Invoking the default notification service */
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this);

        mBuilder.setContentTitle(title);
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(content));
        mBuilder.setContentText(content);
        mBuilder.setTicker(ticker);
        mBuilder.setSmallIcon(R.drawable.ic_launcher);
        mBuilder.setAutoCancel(true);
        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        mBuilder.setProgress(0, 0, true);
        /* Increase notification number every time a new notification arrives */
        mBuilder.setNumber(++numMessages);
        mBuilder.setOngoing(true);
        mBuilder.setPriority(Notification.PRIORITY_HIGH);
		/* Creates an explicit intent for an Activity in your app */
        Intent resultIntent = new Intent(this, LandingPageActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(LandingPageActivity.class);

		/* Adds the Intent that starts the Activity to the top of the stack */
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		/* notificationID allows you to update the notification later on. */
        mNotificationManager.notify(notificationID, mBuilder.build());
    }

    protected void cancelNotification() {
        Log.i("Cancel", "notification");
        mNotificationManager.cancel(notificationID);
    }

    protected void updateNotification(String title, String content, String ticker) {
        Log.i("Update", "notification");

		/* Invoking the default notification service */
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this);

        mBuilder.setContentTitle(title);
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(content));
        mBuilder.setContentText(content);
        mBuilder.setTicker(ticker);
        mBuilder.setSmallIcon(R.drawable.ic_launcher);
        mBuilder.setAutoCancel(true);
        mBuilder.setPriority(Notification.PRIORITY_HIGH);
		/* Increase notification number every time a new notification arrives */
        mBuilder.setNumber(++numMessages);

		/* Creates an explicit intent for an Activity in your app */
        Intent resultIntent = new Intent(this, LandingPageActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(LandingPageActivity.class);

		/* Adds the Intent that starts the Activity to the top of the stack */
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder.setProgress(0, 0, false);
		/* Update the existing notification using same notification ID */
        mNotificationManager.notify(notificationID, mBuilder.build());
    }

    private boolean uploadverifiedData() {
        String json_string = null;
        String requestUrl = SERVER_ADDRESS + "/mast/sync/mobile/sync/adjudicatedData/";
        String syncData = DbController.getInstance(getApplicationContext()).getVerifiedFeaturesForUpload();
        if (!TextUtils.isEmpty(syncData)) {
            InputStream is = null;
            try {

                HttpURLConnection conn = (HttpURLConnection) new URL(requestUrl).openConnection();
                conn.setReadTimeout(100000 /* milliseconds */);
                conn.setConnectTimeout(timeout /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                // Starts the query
                conn.connect();

                //Setting parameters
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                String urlParameters = "adjudicatedData=" + syncData;
                writer.write(urlParameters);
                writer.flush();

                int response = conn.getResponseCode();
                if (response > 1) {
                    is = conn.getInputStream();
                    // Convert the InputStream into a string
                    json_string = CommonFunctions.getStringFromInputStream(is);

                    if (!TextUtils.isEmpty(json_string) && !json_string.contains("Exception")) {
                        boolean dbupdate = DbController.getInstance(getApplicationContext()).updateSyncedVerifiedStatus(json_string);
                        if (dbupdate) {
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        cf.addErrorMessage("UploadService", json_string);
                        return false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                cf.syncLog("", e);
                return false;
            } finally {
                try {
                    if (is != null) is.close();
                } catch (Exception e) {
                }
            }
        } else
            nodata = true;
        return true;
    }
}