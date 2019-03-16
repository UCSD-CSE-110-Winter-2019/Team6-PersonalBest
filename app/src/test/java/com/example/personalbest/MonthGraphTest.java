package com.example.personalbest;

import android.content.Intent;

import com.example.personalbest.database.FirebaseFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.assertArrayEquals;

@RunWith(RobolectricTestRunner.class)
public class MonthGraphTest {
    private MonthGraph monthGraph;
    private SaveLocal saveLocal;

    @Before
    public void init(){
        Intent intent = new Intent(RuntimeEnvironment.application, StepCountActivity.class);
        intent.putExtra(StepCountActivity.FIREBASEKEY, "MockFirebase");
        monthGraph = Robolectric.buildActivity(MonthGraph.class, intent).create().start().resume().get();
        saveLocal = new MockSaveLocal();
    }

    @Test
    public void testBackgroundSteps(){
        int[] correct = {1, 2, 3, 4, 5};
        int[] received = monthGraph.getBackgroundSteps("TestEmail", saveLocal, 5);
        assertArrayEquals(correct, received);
    }

    @Test
    public void testExerciseSteps(){
        int[] correct = {1, 2, 3, 4, 5};
        int[] received = monthGraph.getWorkoutSteps("TestEmail", saveLocal, 5);
        assertArrayEquals(correct, received);
    }

    @Test
    public void testGetGoals(){
        int[] correct = {1, 2, 3, 4, 5};
        int[] received = monthGraph.getGoals("TestEmail", saveLocal, 5);
        assertArrayEquals(correct, received);
    }

    private class MockSaveLocal extends SaveLocal{
        HashMap<String, Integer> map;

        public MockSaveLocal(){
            map = new HashMap<String, Integer>(){{
                Calendar calendar = Calendar.getInstance();
                for(int i = 5; i >= 1; i--){
                    calendar.clear(Calendar.HOUR_OF_DAY);
                    calendar.clear(Calendar.AM_PM);
                    calendar.clear(Calendar.MINUTE);
                    calendar.clear(Calendar.SECOND);
                    calendar.clear(Calendar.MILLISECOND);
                    put(calendar.toString(), i);
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                }
            }};
        }

        @Override
        public int getAccountBackgroundStep(String accountEmail, Calendar calendar){
            return returnArrayValue(calendar);
        }

        @Override
        public int getAccountExerciseStep(String accountEmail, Calendar calendar){
            return returnArrayValue(calendar);
        }

        public int returnArrayValue(Calendar calendar){
            calendar.clear(Calendar.HOUR_OF_DAY);
            calendar.clear(Calendar.AM_PM);
            calendar.clear(Calendar.MINUTE);
            calendar.clear(Calendar.SECOND);
            calendar.clear(Calendar.MILLISECOND);
            if(map.get(calendar.toString()) == null){
                return 0;
            }
            else{
                return map.get(calendar.toString());
            }
        }

        @Override
        public ArrayList<Goal> getNewGoals(String email){
            ArrayList<Goal> goals = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();
            for(int i = 5; i >= 1; i--){
                calendar.set(Calendar.HOUR,0);
                calendar.set(Calendar.MINUTE,0);
                calendar.set(Calendar.SECOND,0);
                calendar.set(Calendar.MILLISECOND,0);
                Date date = calendar.getTime();
                goals.add(new Goal(date, i));
                calendar.add(Calendar.DAY_OF_YEAR, -1);
            }
            return goals;
        }
    }
}
