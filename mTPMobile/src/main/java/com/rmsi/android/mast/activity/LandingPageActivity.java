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
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.rmsi.android.mast.activity.R.string;
import com.rmsi.android.mast.db.DBController;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.User;
import com.rmsi.android.mast.service.DownloadService;
import com.rmsi.android.mast.service.UploadService;
import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.ServiceResultReceiver;
import com.rmsi.android.mast.util.ServiceResultReceiver.Receiver;

public class LandingPageActivity extends AppCompatActivity implements Receiver
{	
	Context context = this;
	List<Attribute> attribList;
	CommonFunctions cf = CommonFunctions.getInstance();
	int roleId=0;
	ServiceResultReceiver mReceiver;
	public static final int STATUS_RUNNING = 0;
	public static final int DOWNLOAD_STATUS_FINISHED = 1;
	public static final int DOWNLOAD_STATUS_ERROR = 2;
	public static final int UPLOAD_STATUS_FINISHED = 3;
	public static final int UPLOAD_STATUS_ERROR = 4;
	public static final int UPLOAD_STATUS_NO_DATA = 5;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		//Initializing context in common functions in case of a crash
		try{CommonFunctions.getInstance().Initialize(getApplicationContext());}catch(Exception e){}
		cf.loadLocale(getApplicationContext());

		setContentView(R.layout.activity_landing_page);

		roleId=CommonFunctions.getRoleID(); 
		mReceiver = new ServiceResultReceiver(new Handler());
		mReceiver.setReceiver(this);
		
		
		if(roleId==1)  // Hardcoded Id for Role (1=Trusted Intermediary, 2=Adjudicator)
		{
			findViewById(R.id.ad_menu1).setVisibility(View.GONE);
			findViewById(R.id.ad_view1).setVisibility(View.GONE);
			//findViewById(R.id.ad_menu2).setVisibility(View.GONE);
			//findViewById(R.id.ad_view2).setVisibility(View.GONE);
		}
		else if(roleId==2)
		{
			findViewById(R.id.ti_menu1).setVisibility(View.GONE);
			findViewById(R.id.ti_menu2).setVisibility(View.GONE);
			findViewById(R.id.ti_view1).setVisibility(View.GONE);
			findViewById(R.id.ti_view2).setVisibility(View.GONE);
		}
		else
		{
			findViewById(R.id.ad_menu1).setVisibility(View.GONE);
			findViewById(R.id.ad_view1).setVisibility(View.GONE);
		}

		Button btn_mapviewer  = (Button) findViewById(R.id.mapviewer);
		btn_mapviewer.setOnClickListener(new OnClickListener() 
		{			
			@Override
			public void onClick(View arg0) 
			{
				Intent intent = new Intent(com.rmsi.android.mast.activity.LandingPageActivity.this, MapViewerActivity.class);
				intent.putExtra("role", roleId);
				startActivity(intent);
			}
		});

		Button btn_captureNewData  = (Button) findViewById(R.id.capturedata);
		btn_captureNewData.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				DBController sqllite = new DBController(context);
				attribList = sqllite.getGeneralAttribute(cf.getLocale());
				if(attribList.size()>0)
				{
					Intent intent = new Intent(com.rmsi.android.mast.activity.LandingPageActivity.this, CaptureDataMapActivity.class);
					intent.putExtra("IsReview",false);
					startActivity(intent);
				}
				else{
					String info=getResources().getString(string.info);
					String msg=getResources().getString(string.download_data_first);
					cf.showMessage(context, info, msg);
				}
			}
		});

		Button btn_review  = (Button) findViewById(R.id.reviewdata);
		btn_review.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				Intent intent = new Intent(com.rmsi.android.mast.activity.LandingPageActivity.this, ReviewDataActivity.class);
				startActivity(intent);
			}
		});



		Button btn_userPref  = (Button) findViewById(R.id.userpref);
		btn_userPref.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				Intent intent = new Intent(com.rmsi.android.mast.activity.LandingPageActivity.this, UserPreferences.class);
				startActivity(intent);
			}
		});



		Button btn_exit  = (Button) findViewById(R.id.logout);
		btn_exit.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				DBController sqllite = new DBController(context);
				boolean pendingRecords=sqllite.checkPendingDraftAndCompletedRecordsToSync();
				sqllite.close();
				if(!pendingRecords)
				{
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
					alertDialogBuilder.setMessage(string.logoutWarningMsg);
					alertDialogBuilder.setPositiveButton(string.continueTologout,
							new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1)
						{
							boolean isDatabaseBackedup=exportDB();
							if(isDatabaseBackedup)
							{
								// OLD logic of removing user & project details.
								boolean result_user = new DBController(context).removeLoggedUser();
								if(result_user)
								{
									Intent i = new Intent(context,LoginActivity.class);
									i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
									startActivity(i);
								}
								else{
									Toast.makeText(context, string.unable_logout, Toast.LENGTH_SHORT).show();
								}
							}
							else{
								Toast.makeText(context, string.unable_logout, Toast.LENGTH_SHORT).show();
							}
						}
					});

					alertDialogBuilder.setNegativeButton(string.btn_cancel,
							new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.dismiss();
						}
					});

					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();


				}
				else
				{
					String syncMsg=getResources().getString(string.sync_pending_records);
					String warning=getResources().getString(string.warning);
					cf.showMessage(context, warning, syncMsg);
				}
			}
		});

		Button btn_download  = (Button) findViewById(R.id.downloaddata);
		btn_download.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				if(cf.getConnectivityStatus())
				{
					User user = new DBController(context).getLoggedUser();
					if (user!=null && user.getUserId()!=null) {
						String connectionMsg=getResources().getString(string.webserviceconnectMsg);
						showToast(connectionMsg, Toast.LENGTH_SHORT);
						Intent intent = new Intent(context, DownloadService.class);
						intent.putExtra("userid", user.getUserId().toString());
						intent.putExtra("datadownload", "config");
						intent.putExtra("receiver", mReceiver);
						startService(intent);
					}
				}else
					cf.showIntenetSettingsAlert(context);
			}
		});

		Button btn_sync  = (Button) findViewById(R.id.syncdata);
		btn_sync.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{



				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
					alertDialogBuilder.setMessage(string.confirmation_sync);
					alertDialogBuilder.setTitle(string.syncdata);
					alertDialogBuilder.setPositiveButton(string.btn_ok,
							new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1)
						{

							if(cf.getConnectivityStatus())
							{
								String connectionMsg=getResources().getString(string.webserviceconnectMsg);
								showToast(connectionMsg, Toast.LENGTH_SHORT);
								Intent intent = new Intent(context,UploadService.class);
								intent.putExtra("receiver", mReceiver);
								startService(intent);
							}else
								cf.showIntenetSettingsAlert(context);

						}
					});

					alertDialogBuilder.setNegativeButton(string.btn_cancel,
							new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.dismiss();
						}
					});

					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();









			}
		});

		//Button btn_downloadFinal  = (Button) findViewById(R.id.downloadFinaldata);
		/*btn_downloadFinal.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				if(cf.getConnectivityStatus())
				{
					User user = new DBController(context).getLoggedUser();
					if (user!=null && user.getUserId()!=null) {
						String connectionMsg=getResources().getString(R.string.webserviceconnectMsg);
						showToast(connectionMsg, Toast.LENGTH_SHORT);
						Intent intent = new Intent(context, DownloadService.class);
						intent.putExtra("userid", user.getUserId().toString());
						intent.putExtra("datadownload", "final");
						intent.putExtra("receiver", mReceiver);
						startService(intent);
					}
				}else
					cf.showIntenetSettingsAlert(context);
			}
		});*/

	}

	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		//String info=getResources().getString(R.string.info);
		String DataDownloadCompleted=getResources().getString(string.DataDownloadCompleted);
		String ErrorInDownloadingData=getResources().getString(string.ErrorInDownloadingData);
		String DataUploadedSuccessfully=getResources().getString(string.DataUploadedSuccessfully);
		String ErrorInUploadingData=getResources().getString(string.ErrorInUploadingData);
		String NoDataPendingforUpload=getResources().getString(string.NoDataPendingforUpload);
		switch (resultCode) {
		case STATUS_RUNNING:
			break;
		case DOWNLOAD_STATUS_FINISHED:
			//showMessage(info,DataDownloadCompleted);
			//cf.showMessage(context,info,DataDownloadCompleted);
			showToast(DataDownloadCompleted,Toast.LENGTH_LONG);
			break;
		case DOWNLOAD_STATUS_ERROR:
			//showMessage(info,ErrorInDownloadingData);
//cf.showMessage(context,info,ErrorInDownloadingData);
			showToast(ErrorInDownloadingData,Toast.LENGTH_LONG);
			break;
		case UPLOAD_STATUS_FINISHED:
			//showMessage(info,DataUploadedSuccessfully);
			//cf.showMessage(context,info,DataUploadedSuccessfully);
			showToast(DataUploadedSuccessfully,Toast.LENGTH_LONG);
			break;
		case UPLOAD_STATUS_ERROR:
			//showMessage(info,ErrorInUploadingData);
			//cf.showMessage(context,info,ErrorInUploadingData);
			showToast(ErrorInUploadingData,Toast.LENGTH_LONG);
			break;
		case UPLOAD_STATUS_NO_DATA:
			//showMessage(info,NoDataPendingforUpload);
			//cf.showMessage(context,info,NoDataPendingforUpload);
			showToast(NoDataPendingforUpload,Toast.LENGTH_LONG);
			break;
		}

	}

	public void showMessage(String header,String message)
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(com.rmsi.android.mast.activity.LandingPageActivity.this);
		alertDialogBuilder.setMessage(message);
		alertDialogBuilder.setTitle(header);
		String ok=getResources().getString(string.btn_ok);
		alertDialogBuilder.setNegativeButton(ok,new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			}
		});
		alertDialogBuilder.show();
	}
	
	private void showToast(String message, int length)
	{
		Toast toast = Toast.makeText(context,message, length);
		toast.setGravity(Gravity.CENTER, 0, 0);	        	  
		toast.show();
	}
	
	/*private Handler myHandler = new Handler() {
		  @Override
		  public void handleMessage(Message msg) {
		    switch (msg.what) {
		      case DISPLAY_DLG:
		        if (!isFinishing()) {
		        showDialog(MY_DIALOG);
		        }
		      break;
		    }
		  }
		};*/
	
	private boolean exportDB()
	{
		String timeStamp = new SimpleDateFormat("MMdd_HHmmss",Locale.US).format(new Date().getTime());
		String Ext_Storage_Path = Environment.getExternalStorageDirectory().getAbsolutePath();

		String currDBPATH = "/"+CommonFunctions.parentFolderName+"/"+CommonFunctions.dbFolderName+"/mast_mobile.db";
		String backupDBPATH = "/"+CommonFunctions.parentFolderName+"/"+CommonFunctions.dbFolderName+"/Backup_mast_mobile_"+timeStamp+".db";

		// String currentDBPath = "/data/"+ "com.your.package" +"/databases/"+db_name;
		//String backupDBPath = SAMPLE_DB_NAME;
		File currentDB = new File(Ext_Storage_Path, currDBPATH);
		File backupDB = new File(Ext_Storage_Path, backupDBPATH);
		try {


			boolean success = currentDB.renameTo(backupDB);
			if(success)
				cf.addErrorMessage("LOGOUT >> ","DB Exported!!"); // logging logout time
			else
				return false;
		} catch(Exception e) {
			e.printStackTrace();
			cf.appLog("", e);
			return false;
		}
		return true;
	}
}


