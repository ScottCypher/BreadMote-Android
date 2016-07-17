package com.cypher.breadmote_example.intro;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.cypher.breadmote_example.BreadMoteApplication;
import com.github.paolorotolo.appintro.AppIntro2;

/**
 * Created by scypher on 2/7/16.
 */
public class BreadMoteIntro extends AppIntro2 {
    @Override
    public void init(Bundle savedInstanceState) {
        for (int i = 0; i < OnboardingFragment.NUMBER_FRAGMENTS; i++) {
            addSlide(OnboardingFragment.getInstance(i));
        }
    }

    @Override
    public void onDonePressed() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putBoolean(BreadMoteApplication.PREF_ONBOARDING, true).apply();

        finish();
    }

    @Override
    public void onNextPressed() {

    }

    @Override
    public void onSlideChanged() {

    }
}
