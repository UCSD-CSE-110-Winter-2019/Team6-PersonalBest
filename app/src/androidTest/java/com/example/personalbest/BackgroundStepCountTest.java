package com.example.personalbest;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.junit.Assert.*;


import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class BackgroundStepCountTest {

    Intent intent;
    SharedPreferences exercisePreferences;
    SharedPreferences.Editor editor;
    private UiDevice mDevice;
    Context targetContext;


    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class,
            true,
            false); // Activity is not launched immediately

    @Before
    public void setUp() {
        targetContext = getInstrumentation().getTargetContext();
        exercisePreferences = targetContext.getSharedPreferences("exercise", Context.MODE_PRIVATE);;
        editor = exercisePreferences.edit();
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    @Test
    public void mainActivityTest2() {
        editor.putInt("height_feet", 5);
        editor.putInt("height_inches", 8);
        editor.commit();

        mActivityRule.launchActivity(new Intent());

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(targetContext))) {
            UiObject appItem = mDevice.findObject(new UiSelector()
                    .resourceId("com.google.android.gms:id/account_profile_picture"));

            try {
                appItem.click();
            } catch (UiObjectNotFoundException e) {
                e.printStackTrace();
            }
        }

        long steps = -1;

        //gets the current step count
        try {
            UiObject currentStepCount = mDevice.findObject(new UiSelector().resourceId("com.example.personalbest:id/textSteps"));
            String step_word = currentStepCount.getText();
            steps = Long.parseLong(step_word.substring(0, step_word.length()-6));
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }

        assertNotEquals(steps, -1);

        //adds 500 steps
        try {
            UiObject add500steps = mDevice.findObject(new UiSelector().resourceId("com.example.personalbest:id/button2"));
            add500steps.click();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }

        //closes app
        try {
            mDevice.pressHome();
            mDevice.pressRecentApps();
            Thread.sleep(1000);

            int height = mDevice.getDisplayHeight();
            int width = mDevice.getDisplayWidth();
            mDevice.swipe(height/2, width/2, height/2, 0, 5);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //launches app
        mActivityRule.launchActivity(new Intent());

        //updates steps
        //adds 500 steps
        try {
            UiObject updateSteps = mDevice.findObject(new UiSelector().resourceId("com.example.personalbest:id/button"));
            updateSteps.click();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }


        //checks if steps increased in background by 500 steps
        long new_steps = -1;

        //gets the current step count
        try {
            UiObject currentStepCount = mDevice.findObject(new UiSelector().resourceId("com.example.personalbest:id/textSteps"));
            String step_word = currentStepCount.getText();
            new_steps = Integer.parseInt(step_word.substring(0, step_word.length()-6));
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }

        assertEquals(new_steps, steps + 500);
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
