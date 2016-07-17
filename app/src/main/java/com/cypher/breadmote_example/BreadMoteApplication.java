package com.cypher.breadmote_example;

import android.app.Application;

import com.cypher.breadmote.BreadMote;

/**
 * Created by cypher1 on 1/23/16.
 */
public class BreadMoteApplication extends Application {

    public static final String PREF_ONBOARDING = "pref_onboarding";

    @Override
    public void onCreate() {
        super.onCreate();
        BreadMote.initialize(this);
    }
}
