package com.rmsi.android.mast.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Ambar.Srivastava on 6/12/2018.
 */

public class UninstallApk extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("DATA", "Apk uninstall");
        Toast.makeText(context,"APK uninstall",Toast.LENGTH_LONG).show();
    }

}