package com.example.personalbest;

import android.app.Activity;
import android.content.SharedPreferences;

import android.content.Context;
import android.util.Log;

import java.util.Calendar;


public class SaveLocal {

    private SharedPreferences exercisePreferences;
    private SharedPreferences.Editor editor;

    public SaveLocal(StepCountActivity activity){
        exercisePreferences= activity.getSharedPreferences("exercise", Context.MODE_PRIVATE);
        editor=exercisePreferences.edit();
    }

    public boolean isLastSessionActive(){
        return exercisePreferences.getBoolean("exerciseActive",false  );
    }
    public void saveStartSessionStepCount(long stepCount){
        editor.putLong("startSessionStepCount",stepCount);
        editor.apply();
    }

    public long getStartSessionStepCount(){
        return exercisePreferences.getLong("startSessionStepCount",0);
    }

    public void setStartSession(Calendar calendar){
        editor.putBoolean("exerciseActive", true);
        editor.putLong("sessionStartTime",calendar.getTimeInMillis());
        editor.apply();
    }
    public void setStopSession(){
        editor.putBoolean("exerciseActive", false);
        editor.apply();
    }
    public long getLastSessionStartTime(){
        return exercisePreferences.getLong("sessionStartTime",0);
    }


    //Sets the background step count of the day (dayBefore) before current day
    public void setBackgroundStepCount(long stepCount, int daysBefore){
        editor.putLong(""+daysBefore+"DaysBeforeBackgroundStepCount",stepCount);
        editor.apply();
    }
    //Returns the background step count of the day (dayBefore) before current day
    public long getBackgroundStepCount(int daysBefore){
        return exercisePreferences.getLong(""+daysBefore+"DaysBeforeBackgroundStepCount",0);
    }
    //Sets the exercise step count of the day (dayBefore) before current day
    public void setExerciseStepCount(long stepCount, int daysBefore){
        editor.putLong(""+daysBefore+"DaysBeforeExerciseStepCount",stepCount);
        editor.apply();
    }
    //Returns the exercise step count of the day (dayBefore) before current day
    public long getExerciseStepCount(int daysBefore){
        return exercisePreferences.getLong(""+daysBefore+"DaysBeforeExerciseStepCount",0);
    }
    //Method that shifts the last 7 days data when a new day begins
    public void newDayShift(){
        for(int i=7; i>0;i--){
            Log.d("Save Local", "Shifting " + i + " and " + (i - 1));
            setExerciseStepCount(getExerciseStepCount(i-1),i);
            setBackgroundStepCount(getBackgroundStepCount(i-1),i);
        }
        setExerciseStepCount(0,0);
        setBackgroundStepCount(0,0);

    }
}
