package com.example.personalbest;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

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
        Calendar savedDate=saveLocal.getLastLogin();
        long savedNumber=savedDate.getTimeInMillis();
        long nowDate=newCal.getTimeInMillis();
        long differenceMillis=nowDate-savedNumber;
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
            } else newDayShift(numDays);
            for (int i = 1; i <= numDays; i++) {
                fitnessService.updateBackgroundCount(currDay, i);
            }
        }
        else if (numDays<0){
            if (numDays <= -7) {
                saveLocal.clearStepData();
                numDays = 6;
            } else newDayShift(numDays);
            for (int i = 6; i >= 7-numDays; i++) {
                fitnessService.updateBackgroundCount(currDay, i);
            }
        }
    }
    //Method that shifts the last 7 days data when a new day begins
    public void newDayShift(int dayCount){
        if(dayCount>0) {
            for (int j = 0; j < dayCount; j++) {
                for (int i = saveLocal.DAYS_TO_KEEP_TRACK_OF - 1; i > 0; i--) {
                    //Log.d("Save Local", "Shifting " + i + " and " + (i - 1));
                    saveLocal.setExerciseStepCount(saveLocal.getExerciseStepCount(i - 1), i);
                    saveLocal.setBackgroundStepCount(saveLocal.getBackgroundStepCount(i - 1), i);
                    saveLocal.setPreviousDayGoal(saveLocal.getGoals(i-1),i);
                }
                saveLocal.setExerciseStepCount(0, 0);
                saveLocal.setBackgroundStepCount(0, 0);
                int goal=saveLocal.getGoal();
                saveLocal.setPreviousDayGoal(goal,1);
            }
        }
        else if(dayCount<0){
            for (int j = 0; j < -dayCount; j++) {
                for (int i = 0; i < saveLocal.DAYS_TO_KEEP_TRACK_OF-1; i++) {
                    Log.d("Save Local", "Shifting " + (i+1) + " and " + (i));
                    saveLocal.setExerciseStepCount(saveLocal.getExerciseStepCount(i +1), i);
                    saveLocal.setBackgroundStepCount(saveLocal.getBackgroundStepCount(i + 1), i);
                    saveLocal.setPreviousDayGoal(saveLocal.getGoals(i+1),i);
                }
                //saveLocal.setExerciseStepCount(0, 0);
                saveLocal.setExerciseStepCount(0,saveLocal.DAYS_TO_KEEP_TRACK_OF-1);
                saveLocal.setBackgroundStepCount(0, 0);
                saveLocal.setBackgroundStepCount(0,saveLocal.DAYS_TO_KEEP_TRACK_OF-1);
                saveLocal.setPreviousDayGoal(0,saveLocal.DAYS_TO_KEEP_TRACK_OF-1);

            }
        }



    }
}
