package com.cypher.breadmote_example.connect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.cypher.breadmote_example.R;
import com.cypher.breadmote_example.control.ControlWiFiActivity;
import com.cypher.breadmote.ConnectionType;
import com.cypher.breadmote.Device;

/**
 * Created by scypher on 7/6/16.
 */
public class ConnectWiFiActivity extends ConnectActivity implements
        WiFiConnectionInfoDialog.ConnectionInfoListener {

    private static final int MAX_UNSIGNED_SHORT = 65535;
    private static final String EXTRA_DEVICE = "extra_device";
    private Device device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            device = savedInstanceState.getParcelable(EXTRA_DEVICE);
        }
    }

    @Override
    protected ConnectionType getConnectionType() {
        return ConnectionType.WIFI;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_DEVICE, device);
    }

    @Override
    public void connect(Device device) {
        this.device = device;
        promptPassword();
    }


    private void promptPassword() {
        WiFiConnectionDbHelper wiFiConnectionDbHelper = new WiFiConnectionDbHelper(this);
        WiFiConnectionDbHelper.SavedConnectionInfo wifiConnectionInfo
                = wiFiConnectionDbHelper.getConnectionInfo(device.getAddress());

        DialogFragment dialogFragment;
        if (wifiConnectionInfo != null) {
            dialogFragment = WiFiConnectionInfoDialog.newInstance(
                    wifiConnectionInfo.getHost(),
                    wifiConnectionInfo.getPassword(),
                    wifiConnectionInfo.getPort());
        } else {
            dialogFragment = new WiFiConnectionInfoDialog();
        }
        dialogFragment.show(getSupportFragmentManager(), null);
    }


    @Override
    public void onConnectionInfoSubmitted(String host, String password, String port) {
        if (port == null || Integer.parseInt(port) > MAX_UNSIGNED_SHORT) {
            Toast.makeText(this, R.string.connect_invalid_port, Toast.LENGTH_SHORT).show();
        } else if (host == null || host.length() == 0) {
            Toast.makeText(this, R.string.connect_invalid_host, Toast.LENGTH_SHORT).show();
        } else {
            saveConnectionInfo(device.getAddress(), host, password, Integer.parseInt(port));

            Intent intent = new Intent(this, ControlWiFiActivity.class);
            intent.putExtra(ControlWiFiActivity.EXTRA_DEVICE, device);
            intent.putExtra(ControlWiFiActivity.EXTRA_PASSWORD, password);
            intent.putExtra(ControlWiFiActivity.EXTRA_PORT, Integer.parseInt(port));
            intent.putExtra(ControlWiFiActivity.EXTRA_HOST, host);
            intent.putExtra(ControlWiFiActivity.EXTRA_DEVICE_NAME, device.getName());
            startActivityForResult(intent, REQUEST_CONNECT);
        }
    }

    private void saveConnectionInfo(String deviceAddress, String host, String password, int port) {
        WiFiConnectionDbHelper wiFiConnectionDbHelper = new WiFiConnectionDbHelper(this);
        wiFiConnectionDbHelper.addConnectionInfo(deviceAddress, host, port, password);
    }
}
