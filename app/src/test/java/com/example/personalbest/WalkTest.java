package com.example.personalbest;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.TextView;

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

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class WalkTest {
    private static final String TEST_SERVICE = "TEST_SERVICE";
    private StepCountActivity activity;
    private Exercise exercise;
    private FitnessService fitnessService;
    private TextView textSteps;
    private TextView textTime;
    private TextView textSpeed;
    private Button walkBtn;
    private long nextStepCount;
    private WalkStats stats;
    //@Rule
    //public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setup() throws Exception {
        FitnessServiceFactory.put(TEST_SERVICE, new FitnessServiceFactory.BluePrint() {
            @Override
            public FitnessService create(StepCountActivity stepCountActivity) {
                return new WalkTest.TestFitnessService(stepCountActivity);
            }
        });

        Intent intent = new Intent(RuntimeEnvironment.application, StepCountActivity.class);
        intent.putExtra(StepCountActivity.FITNESS_SERVICE_KEY, TEST_SERVICE);
        activity = Robolectric.buildActivity(StepCountActivity.class, intent).create().get();
        fitnessService = FitnessServiceFactory.create(TEST_SERVICE, activity);
        exercise = new Exercise(activity, fitnessService);

        textSteps = activity.findViewById(R.id.walkSteps);
        textTime = activity.findViewById(R.id.walkTime);
        textSpeed = activity.findViewById(R.id.textSpeed);
        walkBtn = activity.findViewById(R.id.startExerciseButton);
        stats=new WalkStats(activity);
        nextStepCount = 563;
    }

    @Test
    public void walkStoryTest() {
        Calendar cal = Calendar.getInstance();
        // Wed, February 13, 2019 02:16:40
        Date dummyStartTime = new Date(1550053000000L);
        //Wed, February 13, 2019 03:16:40
        Date dummyEndTime = new Date(1550056600000L);

        stats = new WalkStats(activity);

        cal.setTime(dummyStartTime);
        exercise.startExercise(cal);
        nextStepCount = 3786;

        cal.setTime(dummyEndTime);
        exercise.stopExercise(cal);

        activity.onResume(cal);
        stats.update();

        assertEquals("Time Elapsed: 60:00", textTime.getText());
        assertEquals("Steps: 3223", textSteps.getText());
        assertEquals("MPH: 1.4285785", textSpeed.getText());
    }

    private class TestFitnessService implements FitnessService {
        private static final String TAG = "[TestFitnessService]: ";
        private StepCountActivity stepCountActivity;

        public TestFitnessService(StepCountActivity stepCountActivity) {
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
        public void updateStepCount(Calendar cal) {
            System.out.println(TAG + "updateStepCount");
            stepCountActivity.setStepCount(nextStepCount);
        }

        @Override
        public long getDailyStepCount(Calendar cal) {
            return nextStepCount;
        }

        @Override
        public boolean startRecording() {
            return true;
        }

        @Override
        public String getEmail() {
            return null;
        }

        @Override
        public void updateBackgroundCount(Calendar currentTime, int daysBefore){

        }

        @Override
        public boolean isSetupComplete() {
            return true;
        }
    }
}
