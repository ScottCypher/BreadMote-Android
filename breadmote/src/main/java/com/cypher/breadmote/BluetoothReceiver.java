package com.cypher.breadmote;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by scypher on 2/21/16.
 */
class BluetoothReceiver extends BroadcastReceiver {

    private final RadioStatusListener<BluetoothDevice> radioStatusListener;

    public BluetoothReceiver(RadioStatusListener<BluetoothDevice> radioStatusListener) {
        this.radioStatusListener = radioStatusListener;
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            radioStatusListener.onDeviceFound(bluetoothDevice);
        } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
            if (BluetoothAdapter.STATE_ON == state) {
                radioStatusListener.onRadioStateChange(true);
            } else if (BluetoothAdapter.STATE_OFF == state) {
                radioStatusListener.onRadioStateChange(false);
            }
        } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
            radioStatusListener.onDiscoveryStateChange(true);
        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            radioStatusListener.onDiscoveryStateChange(false);
        }
    }

}
