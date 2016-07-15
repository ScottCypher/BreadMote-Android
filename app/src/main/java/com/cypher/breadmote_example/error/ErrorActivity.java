package com.cypher.breadmote_example.error;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.cypher.breadmote_example.R;
import com.cypher.breadmote_example.WebUtils;
import com.cypher.breadmote.BreadMote;
import com.cypher.breadmote.Connection;
import com.cypher.breadmote.ConnectionListener;
import com.cypher.breadmote.Error;
import com.cypher.breadmote.ErrorListener;

import java.util.List;

public class ErrorActivity extends AppCompatActivity implements ConnectionListener, ErrorListener {

    private List<Error> errors;
    private Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebUtils.promptEmail(ErrorActivity.this);
            }
        });

        BreadMote.addListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BreadMote.removeListener(this);
        if (connection != null) {
            connection.removeListener(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_error, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_copy) {
            if (errors != null && !errors.isEmpty()) {
                String errorLog = formatErrorLog();
                String label = getString(R.string.error_clipboard_label);
                copyToClipBoard(label, errorLog);
            }
            return true;
        } else if (id == R.id.action_delete) {
            if (connection != null) {
                connection.clearErrors();
            }
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String formatErrorLog() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < errors.size(); i++) {
            Error error = errors.get(i);
            String errorText = getString(R.string.error, error.getTag(), error.getMessage());
            stringBuilder.append(errorText);
            if (i < errors.size() - 1) {
                stringBuilder.append("\n\n");
            }
        }

        return stringBuilder.toString();
    }

    private void copyToClipBoard(String label, String text) {
        ClipboardManager clipMan = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipMan.setPrimaryClip(clip);
    }

    @Override
    public void onDisconnect() {
        finish();
    }

    @Override
    public void onConnect(Connection connection) {
        this.connection = connection;
        connection.addListener(this);
    }

    @Override
    public void onError(Error error) {

    }

    @Override
    public void onErrorRemoved(int index) {

    }

    @Override
    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }
}
