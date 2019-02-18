package com.example.personalbest;

import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import com.example.personalbest.fitness.FitnessService;
import com.example.personalbest.fitness.FitnessServiceFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class SaveLocalUnitTest {
    private static final String TEST_SERVICE = "TEST_SERVICE";
    private StepCountActivity activity;
    private Exercise exercise;
    private FitnessService fitnessService;
    private TextView textSteps;
    private TextView textTime;
    private TextView textSpeed;
    private Button walkBtn;
    private long dailyStepCount;
    SaveLocal saveLocal;
    private Calendar currTime;
    //@Rule
    //public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setup() {
        FitnessServiceFactory.put(TEST_SERVICE, new FitnessServiceFactory.BluePrint() {
            @Override
            public FitnessService create(StepCountActivity stepCountActivity) {
                return new TestFitnessService(stepCountActivity);
            }
        });

        Intent intent = new Intent(RuntimeEnvironment.application, StepCountActivity.class);
        intent.putExtra(StepCountActivity.FITNESS_SERVICE_KEY, TEST_SERVICE);
        ActivityController<StepCountActivity> controller = Robolectric.buildActivity(StepCountActivity.class, intent);
        saveLocal = new SaveLocal(controller.get());
        activity = controller.create().get();
        activity.onResume();
        currTime=Calendar.getInstance();
        currTime.set(2019,2,16,13,2,5);
        currTime.set(Calendar.MILLISECOND,35);
        fitnessService=new TestFitnessService(activity);
        exercise=new Exercise(activity,fitnessService);

    }

    @Test
    public void testIsLastSessionActive(){
        assertFalse(saveLocal.isLastSessionActive());
        exercise.startExercise(currTime);
        assertTrue(saveLocal.isLastSessionActive());

    }

    @Test
    public void testStartSessionStepCount(){
        assertEquals(0,saveLocal.getStartSessionStepCount());
        saveLocal.saveStartSessionStepCount(46);
        assertEquals(46,saveLocal.getStartSessionStepCount());
    }
    @Test
    public void testHeightSave(){
        assertFalse(saveLocal.containsHeight());
        saveLocal.saveHeight(5,11);
        assertEquals(5,saveLocal.getHeightFeet());
        assertEquals(11,saveLocal.getHeightInches());
        assertTrue(saveLocal.containsHeight());
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
        public void setup() {}

        @Override
        public void updateStepCount(Calendar cal) {
            stepCountActivity.setStepCount(dailyStepCount);
        }

        @Override
        public long getDailyStepCount(Calendar cal) {
            return dailyStepCount;
        }

        @Override
        public boolean startRecording() {
            return true;
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