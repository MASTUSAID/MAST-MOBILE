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
import android.widget.Toast;

import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.activity.LandingPageActivity;
import com.rmsi.android.mast.db.DBController;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.ProjectSpatialDataDto;
import com.rmsi.android.mast.util.CommonFunctions;

/**
 * @author Prashant.Nigam
 */
public class DownloadService extends IntentService {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;
    private int notificationID = 100;
	//private int numMessages = 0;
	private NotificationManager mNotificationManager;
    private static final String TAG = "DownloadService";
	private String SERVER_IP = CommonFunctions.SERVER_IP;
	CommonFunctions cf = CommonFunctions.getInstance();
	int roleId=0;
   static String STATUS_COMPLETE ="complete";
    static String STATUS_FINAL ="final";
    List<Attribute> attribList;
    static int timeout = 10000; // 10 seconds 
    
    public DownloadService() {
        super(DownloadService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) 
    {
		//Initializing context in common functions in case of a crash
		try{CommonFunctions.getInstance().Initialize(getApplicationContext());}catch(Exception e){}

		roleId=CommonFunctions.getRoleID(); 
        Log.d(TAG, "Service Started!");
        boolean result = false;
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        String userid = intent.getStringExtra("userid");
        String dataToDownload = intent.getStringExtra("datadownload");
        String downloadSuccessMsg=getResources().getString(R.string.ProjectDataDownloadedSuccessfully);
        String offlineDataSuccessMsg=getResources().getString(R.string.OfflineDataDownloadedSuccessfully);
        String downladFailureMsg=getResources().getString(R.string.ErrorDownloadingProjectdata);
        String offlineDownloadFailureMsg=getResources().getResourceEntryName(R.string.ErrorDownloadingOfflineData);
        String successMsgDisplay[] = {downloadSuccessMsg,offlineDataSuccessMsg};
        String failureMsgDisplay[] = {downladFailureMsg,offlineDownloadFailureMsg};
        StringBuffer notificationMsg = new StringBuffer();
        
        if (!TextUtils.isEmpty(userid)) 
        {
        String downloading=getResources().getString(R.string.Downloading);	
        String connectingToWevServer=getResources().getString(R.string.ConnectingtoWebService);
        	displayNotification("MAST", downloading, connectingToWevServer);

            try {
            	//################ ADD methods to download DATA here ##########################

                //String results = downloadDatausingGet(url);
            	//boolean result =  downloadProjectData(userid);
            	DBController database=null;
            	database=new DBController(getApplicationContext());
            	attribList = database.getGeneralAttribute(cf.getLocale());
				
            	if(roleId==1)  // Hardcoded Id for Role (1=Trusted Intermediary, 2=Adjudicator)
            	{
            		if(!dataToDownload.equalsIgnoreCase("final"))
            			result =  downloadProjectData(userid);
            		
            		if(result){
            			notificationMsg.append(successMsgDisplay[0]);
            		}else{
            			notificationMsg.append(failureMsgDisplay[0]);
            		}
            	}
        		else if(roleId==2)
        		{

        			if(attribList.size()>0)
        			{
        				if(dataToDownload.equalsIgnoreCase("final"))
        					result =  downloadFinalData(userid);
        				else
        					result =  downloadProjectDataForAdjudicator(userid);
        			}
        			else{
        				result =  downloadProjectData(userid);
        				if(result)
        				{
        					if(dataToDownload.equalsIgnoreCase("final"))
        						result =  downloadFinalData(userid);
        					else
        						result =  downloadProjectDataForAdjudicator(userid);
        				}
        				else{
        					String unableToDownload=getResources().getString(R.string.unableToDownloadAttributeData);
        					Toast.makeText(getApplicationContext(), unableToDownload, Toast.LENGTH_LONG).show();
        				}
        			}

        		}
            	if(result)            		
            	result = downloadMBtiles();
            	
        		notificationMsg.append("\n");
        		
            	if(result){
        			notificationMsg.append(successMsgDisplay[1]);
        			String downloadFinished=getResources().getString(R.string.DownloadFinished);
        			updateNotification("MAST", notificationMsg.toString(), downloadFinished);
       			 	receiver.send(STATUS_FINISHED, Bundle.EMPTY);
        		}else{
        			notificationMsg.append(failureMsgDisplay[1]);
        			String downloadError=getResources().getString(R.string.DownloadError);
        			updateNotification("MAST",notificationMsg.toString(),downloadError);
       			 	receiver.send(STATUS_ERROR, Bundle.EMPTY);
        		}
            } catch (IOException e) {
            	String unableToConnect=getResources().getString(R.string.UnableToConnectToTheServer);
            	String timeout=getResources().getString(R.string.ConnectionTimeout);
                updateNotification("MAST",unableToConnect,timeout);
                receiver.send(STATUS_ERROR, Bundle.EMPTY);
                e.printStackTrace();
            }
        }    	Log.d(TAG, "Service Stopping!");
    }
    
    /**
     * @param userid
     * @return
     * @throws IOException
     */
    private boolean downloadProjectData(String userid) throws IOException 
    {
    	String requestUrl = "http://"+SERVER_IP+"/mast/sync/mobile/user/download/configuration/"+userid; 
    	DBController database=new DBController(getApplicationContext());
    	boolean result=false;
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
    		
    		if(response>1)
    		{
    			is  = conn.getInputStream();
    			// Convert the InputStream into a string
    			String json_string = CommonFunctions.getStringFromInputStream(is);
    			System.out.println("JSon Data-->"+json_string);
    			if(!TextUtils.isEmpty(json_string) && !json_string.contains("Exception"))
    			{
    				result=database.saveProjectData(json_string);
    			}
    			
    		}
    	} catch (Exception e) {
    		cf.syncLog("DownloadService", e);e.printStackTrace();
    	}finally
    	{
    		database.close();
    		if (is != null) {try {is.close();} catch (Exception e) {}} 
    	}
    	return result;
    }    
    
    /**
     * @return
     * @throws IOException
     */
    private boolean downloadMBtiles() throws IOException 
    {
    	int count;
    	try {
    		String requestUrl = "http://"+SERVER_IP+"/mast/sync/mobile/user/download/mbTiles/";
    		DBController database=null;
    		database=new DBController(getApplicationContext());
    		List<ProjectSpatialDataDto> projectSpatialData=database.getProjectSpatialData();
    		database.close();
    		if(projectSpatialData.size()>0)
    		{
    			for (int i = 0; i<projectSpatialData.size(); i++) 
    			{
    				String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ 
    						"/"+CommonFunctions.parentFolderName+"/"+CommonFunctions.dataFolderName+"/"+projectSpatialData.get(i).getFile_Name();
    				File file = new File(path);
    				if(!file.exists())
    				{
    					URL url = new URL(requestUrl+projectSpatialData.get(i).getServer_Pk().toString());
    					URLConnection connection = url.openConnection();
    					connection.setConnectTimeout(timeout);
    					connection.connect();
    					int lenghtOfFile = connection.getContentLength();
    					Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);
    					InputStream input = new BufferedInputStream(url.openStream());
    					Log.i("DownloadService","downloading file: "+projectSpatialData.get(i).getFile_Name());
    					OutputStream output = new FileOutputStream(path); //save file in SD Card
    					byte data[] = new byte[1024];
    					//long total = 0;
    					while ((count = input.read(data)) != -1) 
    					{
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
    		e.printStackTrace();cf.syncLog("", e);return false;
    	}
    }
    
    /**
     * @param userid
     * @return
     * @throws IOException
     */
    private boolean downloadFinalData(String userid) throws IOException  
    {
    	String requestUrl = "http://"+SERVER_IP+"/mast/sync/mobile/download/FinalDataSet/"+userid; 
    	DBController database=new DBController(getApplicationContext());
    	boolean result=false;

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
    		if(response>1)
    		{
    			is  = conn.getInputStream();
    			// Convert the InputStream into a string
    			String json_string = CommonFunctions.getStringFromInputStream(is);

    			if(!TextUtils.isEmpty(json_string) && !json_string.contains("Exception"))
    			{
    				result=database.saveProjectDataForAdjuticator(json_string,STATUS_FINAL);
    			}
    		}		
    	} catch (Exception e) {
    		cf.syncLog("DownloadService", e);e.printStackTrace();
    	}finally
    	{
    		database.close();
    		if (is != null){ try {is.close();} catch (Exception e) {}}
    	}
		return result;
    }
    
    /**
     * @param title
     * @param content
     * @param ticker
     */
    protected void displayNotification(String title,String content,String ticker) 
	{
		Log.i("Start", "notification");

		/* Invoking the default notification service */
		NotificationCompat.Builder  mBuilder = 
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
				stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

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

	/**
	 * @param title
	 * @param content
	 * @param ticker
	 */
	protected void updateNotification(String title,String content,String ticker) 
	{
		Log.i("Update", "notification");

		/* Invoking the default notification service */
		NotificationCompat.Builder  mBuilder = 
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
		PendingIntent resultPendingIntent =	stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

		mBuilder.setContentIntent(resultPendingIntent);

		mNotificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mBuilder.setProgress(0, 0, false);
		/* Update the existing notification using same notification ID */
		mNotificationManager.notify(notificationID, mBuilder.build());
	}
	
	//http://localhost:8080/mast/studio/mobile/project/attributeValues/{userId}
	
	  /**
	 * @param userid
	 * @return result
	 * @throws IOException
	 */
	private boolean downloadProjectDataForAdjudicator(String userid) throws IOException 
	  {
		  String requestUrl = "http://"+SERVER_IP+"/mast/sync/mobile/project/attributeValues/"+userid; 
		  boolean result=false;
		  DBController database=new DBController(getApplicationContext());

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

			  if(response>1)
			  {
				  is  =  conn.getInputStream();
				  // Convert the InputStream into a string
				  String json_string = CommonFunctions.getStringFromInputStream(is);

				  if(!TextUtils.isEmpty(json_string) && !json_string.contains("Exception"))
				  {					
					  result=database.saveProjectDataForAdjuticator(json_string,STATUS_COMPLETE);
				  }
			  }			  
		  } catch (SocketTimeoutException e) {
			  
		  } catch (Exception e) {
			  cf.syncLog("DownloadService", e);e.printStackTrace();
		  }finally
		  {
			  database.close();
			  if (is != null) {try {is.close();} catch(Exception e){}}
		  }
		  return result;
	  }
}