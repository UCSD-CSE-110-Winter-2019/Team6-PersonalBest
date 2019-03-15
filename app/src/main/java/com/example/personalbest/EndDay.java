package com.example.personalbest;


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
    //Calculates the number of days between the saved last login date and the given calendar date.
    public int dayDifference(Calendar calendar){
        //Calculate the start of the calendar day given
        Calendar newCal=Calendar.getInstance();
        newCal.setTimeInMillis(calendar.getTimeInMillis());
        newCal.set(Calendar.HOUR_OF_DAY,0);
        newCal.set(Calendar.MINUTE,0);
        newCal.set(Calendar.SECOND,0);
        newCal.set(Calendar.MILLISECOND,0);
        //Get the last login date (start of the day based on the way it is saved)
        Calendar savedDate=saveLocal.getLastLogin();
        long savedNumber=savedDate.getTimeInMillis();
        long nowDate=newCal.getTimeInMillis();
        //Calculate the difference in time and the amount of days it corresponds to.
        long differenceMillis=nowDate-savedNumber;
        int differenceDays=(int)(differenceMillis/MILLISECONDS_IN_DAY);
        return differenceDays;
    }

    public void updateDate(Calendar calendar){
        saveLocal.setLastLogin(calendar);
    }
    //Method that handles data shifts when a new day is detected.
    public void newDayActions(int numDays, FitnessService fitnessService, Calendar currDay){
        //If the shift is into the future
        if(numDays>0) {
            //If the shift is more than 6 days, clear data
            if (numDays >= 28) {
                saveLocal.clearStepData();
                saveLocal.clearGoalData();
            } else newDayShift(numDays);
            for (int i = 1; i <= 27; i++) {
                fitnessService.updateBackgroundCount(currDay, i);
            }
        }
        //If the shift is backwards in time
        else if (numDays<0){
            //If the shift is more than 6 days, clear data
            if (numDays <= -28) {
                saveLocal.clearStepData();
                saveLocal.clearGoalData();
            } else newDayShift(numDays);
            for (int i = 1; i <= 27; i++) {
                fitnessService.updateBackgroundCount(currDay, i);
            }
        }
    }
    //Method that shifts the last 7 days data when a new day begins
    public void newDayShift(int dayCount){
        if(dayCount>0) {
            Log.d("Save Local", "Shifting data left");
            for (int j = 0; j < dayCount; j++) {
                for (int i = saveLocal.DAYS_TO_KEEP_TRACK_OF - 1; i > 0; i--) {
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
            Log.d("Save Local", "Shifting data right");
            for (int j = 0; j < -dayCount; j++) {
                for (int i = 0; i < saveLocal.DAYS_TO_KEEP_TRACK_OF-1; i++) {

                    saveLocal.setExerciseStepCount(saveLocal.getExerciseStepCount(i +1), i);
                    saveLocal.setBackgroundStepCount(saveLocal.getBackgroundStepCount(i + 1), i);
                    saveLocal.setPreviousDayGoal(saveLocal.getGoals(i+1),i);
                }
                saveLocal.setExerciseStepCount(0,saveLocal.DAYS_TO_KEEP_TRACK_OF-1);
                saveLocal.setBackgroundStepCount(0, 0);
                saveLocal.setBackgroundStepCount(0,saveLocal.DAYS_TO_KEEP_TRACK_OF-1);
                saveLocal.setPreviousDayGoal(5000,saveLocal.DAYS_TO_KEEP_TRACK_OF-1);

            }
        }



    }
}
