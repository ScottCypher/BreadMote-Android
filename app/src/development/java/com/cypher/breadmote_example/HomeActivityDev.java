package com.cypher.breadmote_example;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.cypher.breadmote_example.control.ControlActivity;
import com.cypher.breadmote_example.control.ControlActivityDev;

/**
 * Created by cypher on 6/25/16.
 */
public class HomeActivityDev extends HomeActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    public void openTutorial(MenuItem item) {
        openTutorial();
    }

    public void mockConnection(MenuItem item) {
        Intent connectIntent = new Intent(this, ControlActivityDev.class);
        connectIntent.putExtra(ControlActivity.EXTRA_DEVICE_NAME, "<Your Device>");
        startActivity(connectIntent);
    }
}
