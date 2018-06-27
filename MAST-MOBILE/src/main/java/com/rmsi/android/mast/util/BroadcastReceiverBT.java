package com.rmsi.android.mast.util;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by ambar.srivastava on 6/5/2018.
 */

public class BroadcastReceiverBT extends android.content.BroadcastReceiver {

//    Context context;
//    public BroadcastReceiverBT(Context context){
//        this.context=context;
//    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            //Do something if connected
            Toast.makeText(context, "Device Connected", Toast.LENGTH_SHORT).show();
        }
        else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            //Do something if disconnected
            Toast.makeText(context, "Device Disconnected", Toast.LENGTH_SHORT).show();
            CommonFunctions.bluetoothSocket=null;

        }


//        else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//            // Get the BluetoothDevice object from the Intent
//            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
//                // CONNECT
//                CommonFunctions.connectBluetoothDevice(device.getName());
//            }
//        }
        //else if...
    }

}
