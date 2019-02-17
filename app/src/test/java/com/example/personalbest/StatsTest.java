package com.example.personalbest;

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
public class StatsTest {
    private static final String TEST_SERVICE = "TEST_SERVICE";
    private StepCountActivity activity;
    private Exercise exercise;
    private FitnessService fitnessService;
    private TextView textSteps;
    private TextView textTime;
    private TextView textSpeed;
    private Button walkBtn;
    private long nextStepCount;

    //@Rule
    //public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setup() throws Exception {
        FitnessServiceFactory.put(TEST_SERVICE, new FitnessServiceFactory.BluePrint() {
            @Override
            public FitnessService create(StepCountActivity stepCountActivity) {
                return new TestFitnessService(stepCountActivity);
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

        nextStepCount = 1000;
    }

    // test for correct time after stopping exercise
    @Test
    public void timeTest() {
        Calendar cal = Calendar.getInstance();
        // Wed, February 13, 2019 02:16:40
        Date dummyStartTime = new Date(1550053000000L);
        //Wed, February 13, 2019 02:31:40
        Date dummyEndTime = new Date(1550053900000L);

        cal.setTime(dummyStartTime);
        exercise.startExercise(cal);
        cal.setTime(dummyEndTime);
        exercise.stopExercise(cal);
        activity.onResume(cal);

        assertEquals("Time Elapsed: 15:00", textTime.getText());
    }

    @Test
    public void speedTest() {
        Calendar cal = Calendar.getInstance();
        // Wed, February 13, 2019 02:16:40
        Date dummyStartTime = new Date(1550053000000L);
        //Wed, February 13, 2019 02:31:40
        Date dummyEndTime = new Date(1550053900000L);

        cal.setTime(dummyStartTime);
        exercise.startExercise(cal);
        cal.setTime(dummyEndTime);
        nextStepCount = 2000;
        exercise.stopExercise(cal);
        activity.onResume(cal);

        assertEquals("MPH: 1.7729799", textSpeed.getText());

    }

    @Test
    public void stepsTest() {
        Calendar cal = Calendar.getInstance();
        walkBtn.performClick();
        nextStepCount = 2000;
        activity.onResume(cal);
        walkBtn.performClick();

        assertEquals("Steps: 1000", textSteps.getText());
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
        public void updateBackgroundCount(Calendar currentTime, int daysBefore){

        }

        @Override
        public boolean isSetupComplete() {
            return false;
        }

        @Override
        public boolean startRecording() {
            return false;
        }
    }
}