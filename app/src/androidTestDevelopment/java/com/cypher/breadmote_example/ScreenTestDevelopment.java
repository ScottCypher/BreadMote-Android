package com.cypher.breadmote_example;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ScreenTestDevelopment {

    @Rule
    public ActivityTestRule<HomeActivityDev> mActivityTestRule = new ActivityTestRule<>(HomeActivityDev.class);

    @After
    public void resetPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mActivityTestRule.getActivity());
        preferences.edit()
                .clear()
                .commit();
    }

    @Test
    public void screenTest() throws Exception {
        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.next), isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withId(R.id.next), isDisplayed()));
        appCompatImageButton2.perform(click());

        ViewInteraction appCompatImageButton3 = onView(
                allOf(withId(R.id.next), isDisplayed()));
        appCompatImageButton3.perform(click());

        ViewInteraction appCompatImageButton4 = onView(
                allOf(withId(R.id.next), isDisplayed()));
        appCompatImageButton4.perform(click());

        ViewInteraction appCompatImageButton5 = onView(
                allOf(withId(R.id.next), isDisplayed()));
        appCompatImageButton5.perform(click());

        ViewInteraction appCompatImageButton6 = onView(
                allOf(withId(R.id.done), isDisplayed()));
        appCompatImageButton6.perform(click());

        //Will only work on development
        ViewInteraction overflowMenuButton = onView(
                allOf(withContentDescription("More options"), isDisplayed()));
        overflowMenuButton.perform(click());

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.title), withText("Mock connection"), isDisplayed()));
        appCompatTextView.perform(click());

        Thread.sleep(500);

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.action_error), withContentDescription("View error log"), isDisplayed()));
        actionMenuItemView.perform(click());

        pressBack();

        ViewInteraction switch_ = onView(
                allOf(withId(R.id.switch1), withText("Alarm"), isDisplayed()));
        switch_.perform(click());

        pressBack();

        Thread.sleep(500);
    }
}
