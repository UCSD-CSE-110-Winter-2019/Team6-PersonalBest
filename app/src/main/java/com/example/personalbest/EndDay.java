package com.example.personalbest;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.example.personalbest.fitness.FitnessService;

import java.util.Calendar;

public class EndDay {
    private final int MILLISECONDS_IN_DAY=86400000;
    private StepCountActivity activity;
    //private Calendar calendar;
    SaveLocal saveLocal;



    public EndDay(SaveLocal saveLocal){
        this.saveLocal=saveLocal;
    }

    public int dayDifference(Calendar calendar){
        Calendar newCal=Calendar.getInstance();
        newCal.setTimeInMillis(calendar.getTimeInMillis());
        newCal.set(Calendar.HOUR_OF_DAY,0);
        newCal.set(Calendar.MINUTE,0);
        newCal.set(Calendar.SECOND,0);
        newCal.set(Calendar.MILLISECOND,0);
        long savedDate=saveLocal.getLastLogin().getTimeInMillis();
        long nowDate=newCal.getTimeInMillis();
        long differenceMillis=nowDate-savedDate;
        int differenceDays=(int)(differenceMillis/MILLISECONDS_IN_DAY);
        return differenceDays;
    }

    public void updateDate(Calendar calendar){
        saveLocal.setLastLogin(calendar);
    }

    public void newDayActions(int numDays, FitnessService fitnessService, Calendar currDay){
        if(numDays>0) {
            if (numDays >= 7) {
                saveLocal.clearStepData();
                numDays = 6;
            } else saveLocal.newDayShift(numDays);
            for (int i = 1; i <= numDays; i++) {
                fitnessService.updateBackgroundCount(currDay, i);
            }
        }
        else if (numDays<0){
            if (numDays <= -7) {
                saveLocal.clearStepData();
                numDays = 6;
            } else saveLocal.newDayShift(numDays);
            for (int i = 6; i >= 7-numDays; i++) {
                fitnessService.updateBackgroundCount(currDay, i);
            }
        }
    }
}
