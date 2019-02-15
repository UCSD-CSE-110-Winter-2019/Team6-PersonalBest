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

    private StepCountActivity activity;
    private Calendar calendar;
    SaveLocal saveLocal;



    public EndDay(StepCountActivity activity, Calendar calendar){
        this.activity=activity;
        this.saveLocal=new SaveLocal(activity);
        this.calendar=calendar;
    }

    public int isNewDay(Calendar calendar){
        return calendar.get(Calendar.DAY_OF_YEAR)-this.calendar.get(Calendar.DAY_OF_YEAR);
    }

    public void updateDate(Calendar calendar){
        this.calendar=calendar;
    }

    public void newDayActions(int numDays, FitnessService fitnessService){
        if(numDays>=7) saveLocal.clearStepData();
        else saveLocal.newDayShift(numDays);
        for(int i=1; i<=numDays;i++) {
            fitnessService.updateBackgroundCount(i);
        }
    }

//    public void updateLastActiveDayBackgroundSteps(Calendar recordStopTime){
//        calendar.set(Calendar.HOUR_OF_DAY, 23);
//        calendar.set(Calendar.MINUTE, 59);
//        calendar.set(Calendar.SECOND, 59);
//        calendar.set(Calendar.MILLISECOND, 999);
//        int daysBefore=Calendar.getInstance().get(Calendar.DAY_OF_YEAR)-recordStopTime.get(Calendar.DAY_OF_YEAR);
//
//
//        //saveLocal.setBackgroundStepCount(,daysBefore);
//    }

}
