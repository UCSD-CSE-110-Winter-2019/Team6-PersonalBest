package com.example.personalbest;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

public class GoalClassUnitTest {
    Goal goal1;
    Goal goal2;
    Date date1;
    Date date2;
    long step1;
    long step2;
    @Before
    public void setup(){
        date1=new Date();
        date2=new Date();
        step1=400;
        step2=600;
        date1.setTime(Calendar.getInstance().getTimeInMillis()-10000);
        date2.setTime(Calendar.getInstance().getTimeInMillis());
        goal1=new Goal(date1,step1);
        goal2=new Goal(date2,step2);
    }
    @Test
    public void toStringTest() {
        String goal1String="Date: "+date1.toString()+" Goal: "+step1;
        String goal2String="Date: "+date2.toString()+" Goal: "+step2;
        assertEquals(goal1String,goal1.toString());
        assertEquals(goal2String,goal2.toString());
    }

    @Test
    public void compareToTest() {
        assertTrue(goal1.compareTo(goal2)<0);
        assertTrue(goal2.compareTo(goal1)>0);
        assertTrue(goal1.compareTo(goal1)==0);
        assertTrue(goal2.compareTo(goal2)==0);
    }
}