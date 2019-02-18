package com.example.personalbest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertArrayEquals;

@RunWith(RobolectricTestRunner.class)
public class GraphUnitTest {
    GraphActivity graphActivity;
    TestSaveLocal testSaveLocal;

    @Before
    public void setup(){
        graphActivity = Robolectric.setupActivity(GraphActivity.class);
        testSaveLocal = new TestSaveLocal();
    }

    @Test
    public void testGetBackground(){
        long [] background = graphActivity.getBackground(testSaveLocal);
        long [] correct = {6, 5, 4, 3, 2, 1, 0};
        assertArrayEquals(correct, background);
    }

    @Test
    public void testGetExercise(){
        long [] exercise = graphActivity.getExercise(testSaveLocal);
        long [] correct = {6, 5, 4, 3, 2, 1, 0};
        assertArrayEquals(correct, exercise);
    }

    @Test
    public void testGetGoals(){
        long [] goals = graphActivity.getGoals(testSaveLocal);
        long [] correct = {6, 5, 4, 3, 2, 1, 0};
        assertArrayEquals(correct, goals);
    }

    private class TestSaveLocal extends SaveLocal {
        long[] array = {0, 1, 2, 3, 4, 5, 6};
        @Override
        public long getBackgroundStepCount(int i){
            return array[i];
        }

        @Override
        public long getExerciseStepCount(int i){
            return array[i];
        }

        @Override
        public long getGoals(int i){
            return array[i];
        }

        @Override
        public int getGoal(){
            return 0;
        }

    }
}
