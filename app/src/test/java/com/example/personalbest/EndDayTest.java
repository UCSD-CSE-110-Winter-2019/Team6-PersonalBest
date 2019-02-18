package com.example.personalbest;

import android.content.Intent;

import com.example.personalbest.fitness.FitnessService;
import com.example.personalbest.fitness.FitnessServiceFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;
@RunWith(RobolectricTestRunner.class)
public class EndDayTest {
    private static final String TEST_SERVICE = "TEST_SERVICE";
    StepCountActivity activity;
    FitnessService fitnessService;
    EndDay testEndDay;
    SaveLocal saveLocal;

    @Before
    public void setup(){
        FitnessServiceFactory.put(TEST_SERVICE, new FitnessServiceFactory.BluePrint() {
            @Override
            public FitnessService create(StepCountActivity stepCountActivity) {
                return new TestFitnessService(stepCountActivity);
            }
        });

        Intent intent = new Intent(RuntimeEnvironment.application, StepCountActivity.class);
        intent.putExtra(StepCountActivity.FITNESS_SERVICE_KEY, TEST_SERVICE);
        activity = Robolectric.buildActivity(StepCountActivity.class, intent).create().get();
        //fitnessService = FitnessServiceFactory.create(TEST_SERVICE, activity);
        //exercise = new Exercise(activity, fitnessService);
        saveLocal=new SaveLocal(activity);
        testEndDay=new EndDay(saveLocal);

        saveLocal.clearGoalData();
        saveLocal.clearStepData();

        Calendar cal1=Calendar.getInstance();
        cal1.set(2019,2,16,4,35,36);
        testEndDay.updateDate(cal1);

    }

    @Test
    public void testDayDifference(){


        Calendar cal2=Calendar.getInstance();
        cal2.set(2019,2,16,13,0,53);
        assertEquals(0,testEndDay.dayDifference(cal2));

        cal2.set(2019,2,16,0,0,53);
        assertEquals(0,testEndDay.dayDifference(cal2));

        cal2.set(2019,2,15,23,59,53);
        assertEquals(-1,testEndDay.dayDifference(cal2));

        cal2.set(2019,2,17,2,59,53);
        assertEquals(1,testEndDay.dayDifference(cal2));

        cal2.set(2019,2,22,2,59,53);
        assertEquals(6,testEndDay.dayDifference(cal2));

    }

    @Test
    public void testUpdateDate(){
        Calendar cal2=Calendar.getInstance();
        cal2.set(2019,2,16,0,0,0);
        cal2.set(Calendar.MILLISECOND,0);

        assertEquals(saveLocal.getLastLogin().getTimeInMillis(),cal2.getTimeInMillis());
    }

    @Test
    public void testNewDayShift(){
        for(int i=0; i<7; i++){
            assertEquals(0,saveLocal.getBackgroundStepCount(i));
            assertEquals(0,saveLocal.getExerciseStepCount(i));
            assertEquals(0,saveLocal.getGoals(i));
            saveLocal.setPreviousDayGoal(i,i);
            saveLocal.setBackgroundStepCount(i,i);
            saveLocal.setExerciseStepCount(i*10,i);
        }
        for(int i=0; i<7; i++){
            assertEquals(i,saveLocal.getBackgroundStepCount(i));
            assertEquals(i*10,saveLocal.getExerciseStepCount(i));
            assertEquals(i,saveLocal.getGoals(i));
        }
        saveLocal.setGoal(30);

        testEndDay.newDayShift(1);
        assertEquals(0,saveLocal.getGoals(0));
        assertEquals(30,saveLocal.getGoals(1));

        assertEquals(0,saveLocal.getBackgroundStepCount(0));
        assertEquals(0,saveLocal.getExerciseStepCount(0));

        for(int i=0; i<6; i++){
            assertEquals(i,saveLocal.getBackgroundStepCount(i+1));
            assertEquals(i*10,saveLocal.getExerciseStepCount(i+1));
        }
        for(int i=1; i<6; i++){
            assertEquals(i,saveLocal.getGoals(i+1));
        }

        testEndDay.newDayShift(-3);


        for(int i=6;i>3;i--){
            assertEquals(0,saveLocal.getBackgroundStepCount(i));
            assertEquals(0,saveLocal.getBackgroundStepCount(i));
            assertEquals(0,saveLocal.getGoals(i));
        }

        assertEquals(20,saveLocal.getExerciseStepCount(0));
        assertEquals(0,saveLocal.getBackgroundStepCount(0));
        for(int i=1;i<4;i++){
            assertEquals(i+2,saveLocal.getBackgroundStepCount(i));
            assertEquals(10*(i+2),saveLocal.getExerciseStepCount(i));
            assertEquals(i+2,saveLocal.getGoals(i));
        }


    }

    @After
    public void cleanUp(){
        saveLocal.clearStepData();
        saveLocal.clearGoalData();
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
        public void updateStepCount(Calendar calendar) {
            System.out.println(TAG + "updateStepCount");
            stepCountActivity.setStepCount(0);//nextStepCount);
        }

        @Override
        public long getDailyStepCount(Calendar calendar) {
            return 0;//nextStepCount;
        }

        @Override
        public void updateBackgroundCount(Calendar currentTime, int daysBefore) {

        }

        @Override
        public boolean isSetupComplete() {
            return true;
        }

        @Override
        public boolean startRecording() {
            return true;
        }
    }
}
