package com.cypher.breadmote_example.control;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;

import com.cypher.breadmote_example.CustomSnackbar;
import com.cypher.breadmote_example.R;
import com.cypher.breadmote_example.WebUtils;
import com.cypher.breadmote_example.error.ErrorActivity;
import com.cypher.breadmote.BreadMote;
import com.cypher.breadmote.Component;
import com.cypher.breadmote.ComponentListener;
import com.cypher.breadmote.Connection;
import com.cypher.breadmote.ConnectionListener;
import com.cypher.breadmote.Error;
import com.cypher.breadmote.ErrorListener;

import java.util.List;

public abstract class ControlActivity extends AppCompatActivity
        implements ErrorListener,
        SwipeRefreshLayout.OnRefreshListener,
        ComponentListener,
        ConnectionListener,
        InteractionListener {

    public static final String EXTRA_DEVICE_NAME = "extra_device_name";
    public static final int RESULT_CONNECTION_FAILED = 200;

    ComponentAdapter componentAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    private Snackbar errorBar;
    private String deviceName;
    private Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        deviceName = getIntent().getStringExtra(EXTRA_DEVICE_NAME);
        toolbar.setTitle(deviceName);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        setupSwipeRefresh(savedInstanceState);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebUtils.promptEmail(ControlActivity.this);
            }
        });

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (!handleConnectionInfo(intent)) {
                onDisconnect();
            }
        }

        setupComponentView();
        BreadMote.addListener(this);
    }

    protected abstract boolean handleConnectionInfo(Intent intent);

    private void setupSwipeRefresh(Bundle savedInstanceState) {
        final boolean firstLayout = savedInstanceState == null;

        swipeRefreshLayout.setColorSchemeResources(R.color.refreshColor);
        swipeRefreshLayout.setOnRefreshListener(this);
        if (firstLayout) {
            swipeRefreshLayout.getViewTreeObserver()
                    .addOnGlobalLayoutListener(
                            new ViewTreeObserver.OnGlobalLayoutListener() {
                                @Override
                                public void onGlobalLayout() {
                                    swipeRefreshLayout
                                            .getViewTreeObserver()
                                            .removeGlobalOnLayoutListener(this);
                                    onRefresh();
                                }
                            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        BreadMote.removeListener(this);

        if (connection != null) {
            connection.removeListener(componentAdapter);
            connection.removeListener((ComponentListener) this);
            connection.removeListener((ErrorListener) this);

            if (isFinishing()) {
                connection.disconnect();
            }
        }
    }

    private void setupComponentView() {
        componentAdapter = new ComponentAdapter(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setAdapter(componentAdapter);
        final int span = getResources().getInteger(R.integer.recycler_span);
        GridLayoutManager layoutManager = new GridLayoutManager(this, span);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int type = componentAdapter.getItemViewType(position);
                switch (type) {
                    case Connection.TYPE_BUTTON:
                    case Connection.TYPE_CHECKBOX:
                    case Connection.TYPE_SWITCH:
                    case Connection.TYPE_TIMEPICKER:
                        return 1;
                    case Connection.TYPE_TEXTFIELD:
                    case Connection.TYPE_LABEL:
                    case Connection.TYPE_SLIDER:
                    default:
                        return span;
                }
            }
        });
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_error) {
            if (errorBar != null) {
                errorBar.dismiss();
            }
            startErrorActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onError(Error error) {
        String errorText = error.getTag();
        errorBar = CustomSnackbar.make(findViewById(R.id.main), errorText, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.control_show_error, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startErrorActivity();
                    }
                });
        errorBar.show();
    }

    @Override
    public void onErrorRemoved(int index) {

    }

    @Override
    public void setErrors(List<Error> errors) {

    }

    private void startErrorActivity() {
        Intent intent = new Intent(this, ErrorActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        if (connection != null) {
            connection.refreshComponents();
        }
    }

    @Override
    public void onComponentAdded(Component component) {
    }

    @Override
    public void onComponentUpdated(int index) {

    }

    @Override
    public void onComponentRemoved(int index) {
    }

    @Override
    public void setComponents(List<Component> components) {
        if (components.isEmpty()) {
            swipeRefreshLayout.setRefreshing(true);
        }
    }

    @Override
    public void onComponentCreation(boolean isCreating) {
        swipeRefreshLayout.setRefreshing(isCreating);
    }

    @Override
    public void onDisconnect() {
        Intent data = new Intent();
        data.putExtra(EXTRA_DEVICE_NAME, deviceName);
        setResult(RESULT_CONNECTION_FAILED, data);
        finish();
    }

    @Override
    public void onConnect(Connection connection) {
        this.connection = connection;
        connection.addListener(componentAdapter);
        connection.addListener((ErrorListener) this);
        connection.addListener((ComponentListener) this);
    }

    @Override
    public void onRemoteCommand(Component component) {
        connection.sendComponentChange(component);
    }
}
