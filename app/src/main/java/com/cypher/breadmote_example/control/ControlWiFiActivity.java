package com.cypher.breadmote_example.control;

import android.content.Intent;

import com.cypher.breadmote.BreadMote;
import com.cypher.breadmote.Device;

/**
 * Created by scypher on 7/6/16.
 */
public class ControlWiFiActivity extends ControlActivity {
    public static final String EXTRA_DEVICE = "extra_device";
    public static final String EXTRA_PASSWORD = "extra_password";
    public static final String EXTRA_PORT = "extra_port";
    public static final String EXTRA_HOST = "extra_host";

    @Override
    protected boolean handleConnectionInfo(Intent intent) {
        Device device = intent.getParcelableExtra(EXTRA_DEVICE);
        int port = intent.getIntExtra(EXTRA_PORT, -1);
        String password = intent.getStringExtra(EXTRA_PASSWORD);
        String host = intent.getStringExtra(EXTRA_HOST);

        if (port < 0) {
            throw new RuntimeException("Invalid port number: " + port);
        } else {
            return BreadMote.connectWiFi(device, password, host, port);
        }
    }
}
