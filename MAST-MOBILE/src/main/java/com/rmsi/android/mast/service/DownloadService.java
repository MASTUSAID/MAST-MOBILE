package com.rmsi.android.mast.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.activity.LandingPageActivity;
import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.domain.ProjectSpatialDataDto;
import com.rmsi.android.mast.util.CommonFunctions;

public class DownloadService extends IntentService {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;
    private int notificationID = 100;
    private NotificationManager mNotificationManager;
    private static final String TAG = "DownloadService";
    CommonFunctions cf = CommonFunctions.getInstance();
    private String SERVER_ADDRESS = cf.getServerAddress();
    int roleId = 0;
    static int timeout = 10000; // 10 seconds

    public DownloadService() {
        super(DownloadService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Initializing context in common functions in case of a crash
        try {
            cf.Initialize(getApplicationContext());
        } catch (Exception e) {
        }

        roleId = CommonFunctions.getRoleID();
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        String userid = intent.getStringExtra("userid");
        String downloadType = intent.getStringExtra("downloadType");

        if (!TextUtils.isEmpty(userid)) {
            String downloading = getResources().getString(R.string.Downloading);
            String connectingToWevServer = getResources().getString(R.string.ConnectingtoWebService);
            displayNotification("MAST", downloading, connectingToWevServer);

            try {
                if(downloadType.equalsIgnoreCase("config")){
                    // Download configuration
                    startConfigDownloading(receiver, userid);
                } else {
                    // Download data
                    startDataDownloading(receiver, userid);
                }

            } catch (IOException e) {
                String unableToConnect = getResources().getString(R.string.UnableToConnectToTheServer);
                String timeout = getResources().getString(R.string.ConnectionTimeout);
                updateNotification("MAST", unableToConnect, timeout);
                receiver.send(STATUS_ERROR, Bundle.EMPTY);
                e.printStackTrace();
            }
        }
        Log.d(TAG, "Service Stopping!");
    }

    private void startDataDownloading(ResultReceiver receiver, String userid) throws IOException {
        // First check for config being downloaded
        DbController db = DbController.getInstance(getApplicationContext());
        if (db.getClaimTypes(false).size() < 1) {
            if(!startConfigDownloading(null, userid)){
                receiver.send(STATUS_ERROR, Bundle.EMPTY);
                return;
            }
        }

        displayNotification("MAST", getResources().getString(R.string.DownloadingData),
                getResources().getString(R.string.Downloading));
        if(downloadProperties(userid)){
            updateNotification("MAST", getResources().getString(R.string.DataDownloadSuccessful),
                    getResources().getString(R.string.DownloadFinished));
            receiver.send(STATUS_FINISHED, Bundle.EMPTY);
        } else {
            updateNotification("MAST", getResources().getString(R.string.DataDownloadFailed),
                    getResources().getString(R.string.DownloadError));
            receiver.send(STATUS_ERROR, Bundle.EMPTY);
        }
    }

    private boolean startConfigDownloading(ResultReceiver receiver, String userid) throws IOException {
        displayNotification("MAST", getResources().getString(R.string.DownloadingConfig),
                getResources().getString(R.string.Downloading));
        if(downloadConfiguration(userid) && downloadMBtiles()){
            updateNotification("MAST", getResources().getString(R.string.ConfigDownloadSuccessful),
                    getResources().getString(R.string.DownloadFinished));
            if(receiver != null)
                receiver.send(STATUS_FINISHED, Bundle.EMPTY);
            return true;
        } else {
            updateNotification("MAST", getResources().getString(R.string.ConfigDonwloadFailed),
                    getResources().getString(R.string.DownloadError));
            if(receiver != null)
                receiver.send(STATUS_ERROR, Bundle.EMPTY);
            return false;
        }
    }

    /**
     * @param userid
     * @return
     * @throws IOException
     */
    private boolean downloadConfiguration(String userid) throws IOException {
        String requestUrl = SERVER_ADDRESS + "/mast/sync/mobile/user/download/configuration/" + userid;
        DbController database = DbController.getInstance(getApplicationContext());
        boolean result = false;
        InputStream is = null;

        URL url = new URL(requestUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(100000 /* milliseconds */);
        conn.setConnectTimeout(timeout /* milliseconds */);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();

        try {
            int response = conn.getResponseCode();

            if (response > 1) {
                is = conn.getInputStream();
                // Convert the InputStream into a string
                String json_string = CommonFunctions.getStringFromInputStream(is);
                if (!TextUtils.isEmpty(json_string) && !json_string.contains("Exception")) {
                    result = database.saveProjectData(json_string);
                }
            }
        } catch (Exception e) {
            cf.syncLog("DownloadService", e);
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
        }
        return result;
    }

    /**
     * @return
     * @throws IOException
     */
    private boolean downloadMBtiles() throws IOException {
        int count;
        try {
            String requestUrl = SERVER_ADDRESS + "/mast/sync/mobile/user/download/mbTiles/";
            DbController database = null;
            database = DbController.getInstance(getApplicationContext());
            List<ProjectSpatialDataDto> projectSpatialData = database.getProjectSpatialData();
            database.close();
            if (projectSpatialData.size() > 0) {
                for (int i = 0; i < projectSpatialData.size(); i++) {
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                            "/" + CommonFunctions.parentFolderName + "/" + CommonFunctions.dataFolderName + "/" + projectSpatialData.get(i).getFile_Name();
                    File file = new File(path);
                    if (!file.exists()) {
                        URL url = new URL(requestUrl + projectSpatialData.get(i).getServer_Pk().toString());
                        URLConnection connection = url.openConnection();
                        connection.setConnectTimeout(timeout);
                        connection.connect();
                        int lenghtOfFile = connection.getContentLength();
                        Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);
                        InputStream input = new BufferedInputStream(url.openStream());
                        Log.i("DownloadService", "downloading file: " + projectSpatialData.get(i).getFile_Name());
                        OutputStream output = new FileOutputStream(path); //save file in SD Card
                        byte data[] = new byte[1024];
                        //long total = 0;
                        while ((count = input.read(data)) != -1) {
                            //total += count;
                            //publishProgress(""+(int)((total*100)/lenghtOfFile));
                            output.write(data, 0, count);
                        }
                        output.flush();
                        output.close();
                        input.close();
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            cf.syncLog("", e);
            return false;
        }
    }

    /**
     * @param title
     * @param content
     * @param ticker
     */
    protected void displayNotification(String title, String content, String ticker) {
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
        mBuilder.setPriority(Notification.PRIORITY_HIGH);
		/* Increase notification number every time a new notification arrives */
        //mBuilder.setNumber(++numMessages);
        mBuilder.setOngoing(true);
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
        mNotificationManager.cancel(notificationID);
    }

    /**
     * @param title
     * @param content
     * @param ticker
     */
    protected void updateNotification(String title, String content, String ticker) {
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
        //mBuilder.setNumber(++numMessages);

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

    /**
     * @param userid
     * @return result
     * @throws IOException
     */
    private boolean downloadProperties(String userid) throws IOException {
        String requestUrl = SERVER_ADDRESS + "/mast/sync/mobile/project/getProperties/" + userid;
        boolean result = false;
        InputStream is = null;

        URL url = new URL(requestUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(450000);
        conn.setConnectTimeout(timeout);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();

        try {
            int response = conn.getResponseCode();

            if (response > 1) {
                is = conn.getInputStream();
                // Convert the InputStream into a string
                String json_string = CommonFunctions.getStringFromInputStream(is);

                if (!TextUtils.isEmpty(json_string) && !json_string.contains("Exception")) {
                    result = DbController.getInstance(getApplicationContext()).saveDownloadedProperties(json_string);
                }
            }
        } catch (SocketTimeoutException e) {
            cf.syncLog("DownloadService", e);
            e.printStackTrace();
        } catch (Exception e) {
            cf.syncLog("DownloadService", e);
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
        }
        return result;
    }
}