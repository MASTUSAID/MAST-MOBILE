package com.rmsi.android.mast.util;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.rmsi.android.mast.service.UploadService;

public class InternetBroadcastReceiver extends BroadcastReceiver 
{
	CommonFunctions cf = CommonFunctions.getInstance();
	

	@Override
	public void onReceive(Context context, Intent intent) 
	{
		CommonFunctions.getInstance().Initialize(context.getApplicationContext());
		if(getConnectivityStatus(context))
		{
			if(cf!=null && cf.getAutoSync())
			processUpload(context);
		}
	}


	public boolean getConnectivityStatus(Context mContext) 
	{
		ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (null != activeNetwork) 
		{
			if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
				return true;

			if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
				return true;
		} 
		return false;
	}

	private void processUpload(Context context)
	{
		try{
			ServiceResultReceiver mReceiver=null;
			Intent intent = new Intent(context,UploadService.class);
			intent.putExtra("receiver", mReceiver);
			context.startService(intent);
		}catch(Exception e)
		{
			if(cf!=null){
				cf.addErrorMessage("BroadcastReceiver", "Error in syncing...");
				cf.syncLog("", e);
			}
		}
	}
}