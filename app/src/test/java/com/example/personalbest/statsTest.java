package com.example.personalbest;

import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import com.example.personalbest.fitness.FitnessService;
import com.example.personalbest.fitness.FitnessServiceFactory;
import com.example.personalbest.fitness.GoogleFitAdapter;

import org.hamcrest.Condition;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class statsTest {
    private static final String TEST_SERVICE = "TEST_SERVICE";
    private StepCountActivity activity;
    private TextView textSteps;
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

        textSteps = activity.findViewById(R.id.walkSteps);
        walkBtn = activity.findViewById(R.id.startExerciseButton);

        nextStepCount = 1000;
    }

    @Test
    public void timeTest() {
        /*LocalDateTime dummyStartTime = LocalDateTime.of(2017, 2, 7, 11, 00, 00);
        LocalDateTime dummyEndTime = LocalDateTime.of(2017, 2, 7, 11, 15, 00);
        //Button walkBtn = mainActivity.getActivity().findViewById(R.id.startExerciseButton);
        //TextView walkTime = mainActivity.getActivity().findViewById(R.id.walkTime);
        String expectedText = "Time Elapsed:15:00";
        //Calendar c = Calendar.getInstance();

        //c.set(2017, 2, 7, 11, 00, 00);
        Time.useFixedClockAt(dummyStartTime);
        walkBtn.performClick();

        Time.useFixedClockAt(dummyEndTime);
        walkBtn.performClick();

        String actualText = String.valueOf(walkTime.getText());
        assertEquals(expectedText, actualText);*/
    }

    @Test
    public void speedTest() {
        //Button walkBtn = mainActivity.getActivity().findViewById(R.id.startExerciseButton);
        walkBtn.performClick();

    }

    @Test
    public void stepsTest() {
        //Button walkBtn = mainActivity.getActivity().findViewById(R.id.startExerciseButton);
        //TextView stepsText = mainActivity.getActivity().findViewById(R.id.textSteps);
        //mainActivity.getActivity().

        walkBtn.performClick();
        nextStepCount = 2000;
        activity.onResume();
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