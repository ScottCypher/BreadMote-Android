package com.cypher.breadmote_example.control;

import android.content.Intent;

import com.cypher.breadmote.BreadMote;
import com.cypher.breadmote.Device;

/**
 * Created by scypher on 7/6/16.
 */
public class ControlBluetoothActivity extends ControlActivity {
    public static final String EXTRA_DEVICE = "extra_device";

    @Override
    protected boolean handleConnectionInfo(Intent intent) {
        Device device = intent.getParcelableExtra(EXTRA_DEVICE);
        return BreadMote.connectBluetooth(device);
    }
}
