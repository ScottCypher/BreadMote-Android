package com.cypher.breadmote_example.connect;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.cypher.breadmote_example.CustomSnackbar;
import com.cypher.breadmote_example.R;
import com.cypher.breadmote_example.WebUtils;
import com.cypher.breadmote_example.control.ControlActivity;
import com.cypher.breadmote.BreadMote;
import com.cypher.breadmote.ConnectionType;
import com.cypher.breadmote.Device;
import com.cypher.breadmote.ScanListener;

import java.util.List;

abstract class ConnectActivity extends AppCompatActivity
        implements DeviceAdapter.ConnectListener,
        SwipeRefreshLayout.OnRefreshListener,
        ScanListener {

    static final int REQUEST_CONNECT = 300;
    private DeviceAdapter deviceAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView imageView;
    private ConnectionType connectionType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebUtils.promptEmail(ConnectActivity.this);
            }
        });

        connectionType = getConnectionType();
        if (savedInstanceState == null) {
            BreadMote.scan(connectionType);
        }

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        setupSwipeRefresh();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        deviceAdapter = new DeviceAdapter(this);
        recyclerView.setAdapter(deviceAdapter);

        imageView = (ImageView) findViewById(R.id.img_connect);
        imageView.setImageResource(getImageResId());
        setupToolbar();
    }

    protected abstract ConnectionType getConnectionType();

    private int getImageResId() {
        switch (connectionType) {
            case WIFI:
                return R.drawable.ic_wifi;
            case BLUETOOTH:
                return R.drawable.ic_bluetooth;
            default:
                throw new RuntimeException("Unknown type: " + connectionType);
        }
    }

    private void setupToolbar() {
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int scrollRange = appBarLayout.getTotalScrollRange();
                float invertedPercent = Math.abs((float) verticalOffset / (float) scrollRange);
                float percent = 1 - invertedPercent;

                imageView.setPivotY((float) (imageView.getHeight() * 0.8));

                imageView.setScaleX(percent);
                imageView.setScaleY(percent);

                imageView.setAlpha(percent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        BreadMote.addListener(deviceAdapter);
        BreadMote.addListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BreadMote.removeListener(deviceAdapter);
        BreadMote.removeListener(this);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.refreshColor);
        swipeRefreshLayout.getViewTreeObserver()
                .addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                swipeRefreshLayout
                                        .getViewTreeObserver()
                                        .removeGlobalOnLayoutListener(this);
                                BreadMote.addListener(ConnectActivity.this);
                            }
                        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (isFinishing()) {
            BreadMote.terminate();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        BreadMote.scan(connectionType);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CONNECT) {
            if (resultCode == ControlActivity.RESULT_CONNECTION_FAILED) {
                View view = findViewById(R.id.main);
                String name = data.getStringExtra(ControlActivity.EXTRA_DEVICE_NAME);
                String message = getString(R.string.connect_device_failed, name);
                CustomSnackbar.make(view, message, Snackbar.LENGTH_LONG)
                        .show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onScanStateChange(final boolean isScanning) {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(isScanning);
            }
        });
    }

    @Override
    public void onDeviceFound(Device device) {
    }

    @Override
    public void setCurrentDevices(List<Device> devices) {
    }
}
