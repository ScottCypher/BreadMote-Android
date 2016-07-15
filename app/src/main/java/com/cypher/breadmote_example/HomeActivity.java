package com.cypher.breadmote_example;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.cypher.breadmote_example.connect.ConnectBluetoothActivity;
import com.cypher.breadmote_example.connect.ConnectWiFiActivity;
import com.cypher.breadmote_example.intro.BreadMoteIntro;
import com.google.firebase.crash.FirebaseCrash;

public class HomeActivity extends AppCompatActivity {

    private static final String SETUP_LINK = "http://scottcypher.github.io/BreadMote/";
    private static final String SHARE_URL = "breadmote.com";
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION_BT = 555,
            PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION_WIFI = 666;
    private int maxDeltaXRed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean hasBoarded = preferences.getBoolean(BreadMoteApplication.PREF_ONBOARDING, false);
        if (!hasBoarded) {
            openTutorial();
        }

        setupToolbar();
        FirebaseCrash.report(new Exception("My first Android non-fatal error"));
    }

    private void setupToolbar() {
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        final View redLed = findViewById(R.id.img_red);
        final Interpolator interpolator = new OvershootInterpolator();
        appBarLayout.post(new Runnable() {
            @Override
            public void run() {
                View greenLed = findViewById(R.id.img_green);
                maxDeltaXRed = -(int) (greenLed.getX() + greenLed.getWidth() / 2 - (redLed.getX() + redLed.getWidth()));
            }
        });

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int scrollRange = appBarLayout.getTotalScrollRange();
                float invertedPercent = Math.abs((float) verticalOffset / (float) scrollRange);
                float percent = 1 - invertedPercent;

                float interpolation = interpolator.getInterpolation(percent);
                int dX = -maxDeltaXRed + (int) (interpolation * maxDeltaXRed);
                redLed.setTranslationX(dX);
            }
        });
    }

    public void onClickBluetooth(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !hasCoarseLocationPermission()) {
            explainLocationRequest(PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION_BT);
        } else {
            startConnectActivity(findViewById(R.id.img_bt), ConnectBluetoothActivity.class);
        }
    }

    public void onClickWifi(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !hasCoarseLocationPermission()) {
            explainLocationRequest(PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION_WIFI);
        } else {
            startConnectActivity(findViewById(R.id.img_wifi), ConnectWiFiActivity.class);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void explainLocationRequest(final int permissionRequestCode) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.location_dialog_t)
                .setMessage(Html.fromHtml(getString(R.string.location_dialog_m)))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                permissionRequestCode);
                    }
                }).create();
        alertDialog.show();

        ((TextView) alertDialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean hasCoarseLocationPermission() {
        return checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void startConnectActivity(View view, Class activityClass) {
        Intent intent = new Intent(this, activityClass);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setTransitionName(getString(R.string.trans_home_connect));
            Pair<View, String> pair = Pair.create(view, view.getTransitionName());
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(this, pair);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    public void onClickSetup(View view) {
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
        int toolbarColor = getResources().getColor(R.color.colorPrimary);
        intentBuilder.setToolbarColor(toolbarColor);
        intentBuilder.build().launchUrl(this, Uri.parse(SETUP_LINK));
    }

    public void onClickShare(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String shareString = getString(R.string.share, SHARE_URL);
        intent.putExtra(Intent.EXTRA_TEXT, shareString);
        startActivity(Intent.createChooser(intent, "Share"));
    }

    public void onClickFeedback(View view) {
        WebUtils.promptEmail(this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION_WIFI) {
                onClickWifi(null);
            } else if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION_BT) {
                onClickBluetooth(null);
            }
        }
    }

    void openTutorial() {
        Intent introIntent = new Intent(this, BreadMoteIntro.class);
        startActivity(introIntent);
    }
}
