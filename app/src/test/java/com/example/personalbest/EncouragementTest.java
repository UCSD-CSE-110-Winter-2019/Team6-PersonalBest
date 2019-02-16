package com.example.personalbest;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.personalbest.fitness.Encouragement;
import com.example.personalbest.fitness.FitnessService;
import com.example.personalbest.fitness.FitnessServiceFactory;
import com.example.personalbest.fitness.GoogleFitAdapter;
import com.example.personalbest.fitness.WalkStats;

import org.hamcrest.Condition;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowToast;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class EncouragementTest {
    private static final String TEST_SERVICE = "TEST_SERVICE";
    private StepCountActivity activity;
    private Exercise exercise;
    private FitnessService fitnessService;
    private long nextStepCount;
    private StepCountActivity.Background testTask;
    private ShadowToast shadowToast;

    @Before
    public void setup() throws Exception {
        FitnessServiceFactory.put(TEST_SERVICE, new FitnessServiceFactory.BluePrint() {
            @Override
            public FitnessService create(StepCountActivity stepCountActivity) {
                return new TestFitnessService2(stepCountActivity);
            }
        });

        Intent intent = new Intent(RuntimeEnvironment.application, StepCountActivity.class);
        intent.putExtra(StepCountActivity.FITNESS_SERVICE_KEY, TEST_SERVICE);
        activity = Robolectric.buildActivity(StepCountActivity.class, intent).create().get();
        fitnessService = FitnessServiceFactory.create(TEST_SERVICE, activity);
        exercise = new Exercise(activity, fitnessService);
        nextStepCount = 0;
    }

    // Test that toast appears after 8pm
    @Test
    public void toastTest1() {
        Calendar cal = Calendar.getInstance();
        // Wed, February 13, 2019 02:16:40
        Date dummyStartTime = new Date(1550053000000L);
        // Wed, February 13, 2019 20:00:00
        Date dummyEndTime = new Date(1550116800000L);
        cal.setTime(dummyStartTime);

        testTask = activity.new Background(cal);
        activity.onResume(cal);

        nextStepCount = 2000;
        cal.setTime(dummyEndTime);

        activity.onResume(cal);

        String toastText = shadowToast.getTextOfLatestToast();
        assertEquals("You've increased your daily steps by over 2000 steps. Keep up the good work!", toastText);
    }

    // Test that no toast appears when a subgoal has not been met
    @Test
    public void toastTest2() {
        Calendar cal = Calendar.getInstance();
        // Wed, February 13, 2019 02:16:40
        Date dummyStartTime = new Date(1550053000000L);
        // Wed, February 13, 2019 20:00:00
        Date dummyEndTime = new Date(1550116800000L);
        cal.setTime(dummyStartTime);

        testTask = activity.new Background(cal);
        activity.onResume(cal);

        nextStepCount = 250;
        cal.setTime(dummyEndTime);

        activity.onResume(cal);

        String toastText = shadowToast.getTextOfLatestToast();
        assertEquals(null, toastText);

    }

    // Test toast after walking past midnight and meeting subgoal
    @Test
    public void toastTest3() {
        Calendar cal = Calendar.getInstance();
        // Wed, February 13, 2019 23:55:00
        Date dummyStartTime = new Date(1550053000000L);
        // Wed, February 14, 2019 00:10:00
        Date dummyEndTime = new Date(1550131800000L);
        //Wed, February 14, 2019 20:00:00
        Date dummyNotifTime = new Date(1550203200000L);

        cal.setTime(dummyStartTime);

        testTask = activity.new Background(cal);
        activity.onResume(cal);

        nextStepCount = 1000;
        cal.setTime(dummyEndTime);

        nextStepCount = 2000;

        cal.setTime(dummyNotifTime);

        activity.onResume(cal);

        String toastText = shadowToast.getTextOfLatestToast();
        assertEquals("You've increased your daily steps by over 2000 steps. Keep up the good work!", toastText);
    }

    // Test no toast appears before 8pm
    @Test
    public void toastTest4() {
        Calendar cal = Calendar.getInstance();
        // Wed, February 13, 2019 02:16:40
        Date dummyStartTime = new Date(1550053000000L);

        cal.setTime(dummyStartTime);

        testTask = activity.new Background(cal);
        activity.onResume(cal);

        nextStepCount = 2000;

        activity.onResume(cal);

        String toastText = shadowToast.getTextOfLatestToast();
        assertEquals(null, toastText);
    }

    private class TestFitnessService2 implements FitnessService {
        private static final String TAG = "[TestFitnessService]: ";
        private StepCountActivity stepCountActivity;

        public TestFitnessService2(StepCountActivity stepCountActivity) {
            this.stepCountActivity = stepCountActivity;
        }

        @Override
        public int getRequestCode() {
            return 0;
        }

        @Override
        public void setup() {
            System.out.println(TAG + "setup");
        }

        @Override
        public void updateStepCount() {
            System.out.println(TAG + "updateStepCount");
            stepCountActivity.setStepCount(nextStepCount);
        }

        @Override
        public long getDailyStepCount() {
            return nextStepCount;
        }
    }
}