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
        testTask = activity.new Background();
        nextStepCount = 1000;
    }

    // Test that toast appears after 8pm
    @Test
    public void toastTimeTest() {
        Calendar cal = Calendar.getInstance();
        // Wed, February 13, 2019 02:16:40
        Date dummyStartTime = new Date(1550053000000L);
        // Wed, February 13, 2019 20:00:00
        Date dummyEndTime = new Date(1550116800000L);
        cal.setTime(dummyStartTime);

        testTask.c = cal;
        testTask.execute();

        nextStepCount = 1500;
        cal.setTime(dummyEndTime);

        testTask.c = cal;
        testTask.execute();

        String toastText = shadowToast.getTextOfLatestToast();
        assertEquals("You've increased your daily steps by over 1000 steps. Keep up the good work!", toastText);
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