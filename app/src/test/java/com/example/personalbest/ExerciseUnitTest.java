package com.example.personalbest;

import android.content.Intent;

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
public class ExerciseUnitTest {
    private static final String TEST_SERVICE = "TEST_SERVICE";
    private StepCountActivity activity;
    private Exercise exercise;
    private FitnessService fitnessService;
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
    public void testStartExercise(){
        assertFalse(exercise.isActive());
        exercise.startExercise(currTime);
        fitnessService.updateStepCount(currTime);
        assertEquals(currTime.getTimeInMillis(),saveLocal.getLastSessionStartTime());
        assertTrue(exercise.isActive());

    }

    @Test
    public void testStopExercise(){
        dailyStepCount=5;
        assertFalse(exercise.isActive());
        exercise.startExercise(currTime);
        fitnessService.updateStepCount(currTime);
        Calendar secondTime=Calendar.getInstance();
        secondTime.set(2019,2,16,13,8,23);
        dailyStepCount=15;

        assertTrue(exercise.isActive());
        exercise.stopExercise(secondTime);
        assertEquals(currTime.getTimeInMillis(),saveLocal.getLastSessionStartTime());
        assertEquals(secondTime.getTimeInMillis(),saveLocal.getLastExerciseTimeEnd());
        assertEquals("6:18",saveLocal.getLastExerciseTime());
        assertEquals(10,saveLocal.getLastExerciseSteps());
        assertFalse(exercise.isActive());


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