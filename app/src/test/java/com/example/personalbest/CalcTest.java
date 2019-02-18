package com.example.personalbest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class CalcTest {
    Calc testCalc = new Calc(0, 5);

    @Test
    public void testSpeed1() {
        Long startTime = 1550053000000L;
        Long endTime = 1550053900000L;
        float distance = 1;

        float speed = testCalc.calcSpeed(startTime, endTime, distance);

        assertEquals(4, (long)speed);
    }

    @Test
    public void testSpeed2() {
        Long time = 1550053000000L;
        float distance = 1;

        float speed = testCalc.calcSpeed(time, time, distance);

        assertEquals(0, (long)speed);
    }

    @Test
    public void testDistance1() {
        long steps = 10000;
        double expectedDist = 3.91098;

        float distance = testCalc.calcDistance(steps);

        assertEquals((long)expectedDist, (long)distance);
    }

    @Test
    public void testDistance2() {
        long steps = 0;
        double expectedDist = 0;

        float distance = testCalc.calcDistance(steps);

        assertEquals((long)expectedDist, (long)distance);
    }

    @Test
    public void testTime1() {
        Long startTime = 1550053000000L;
        Long endTime = 1550053900000L;
        String expectedTime = "15:00";

        String time = testCalc.calcTime(startTime, endTime);

        assertEquals(expectedTime, time);
    }

    @Test
    public void testTime2() {
        Long startTime = 1550053000000L;
        String expectedTime = "0:00";

        String time = testCalc.calcTime(startTime, startTime);

        assertEquals(expectedTime, time);
    }
}