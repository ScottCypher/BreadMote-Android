package com.cypher.breadmote_example.connect;

import android.content.Intent;

import com.cypher.breadmote_example.control.ControlBluetoothActivity;
import com.cypher.breadmote.ConnectionType;
import com.cypher.breadmote.Device;

/**
 * Created by scypher on 7/6/16.
 */
public class ConnectBluetoothActivity extends ConnectActivity {
    @Override
    protected ConnectionType getConnectionType() {
        return ConnectionType.BLUETOOTH;
    }

    @Override
    public void connect(Device device) {
        Intent intent = new Intent(this, ControlBluetoothActivity.class);
        intent.putExtra(ControlBluetoothActivity.EXTRA_DEVICE, device);
        intent.putExtra(ControlBluetoothActivity.EXTRA_DEVICE_NAME, device.getName());
        startActivityForResult(intent, REQUEST_CONNECT);
    }
}
