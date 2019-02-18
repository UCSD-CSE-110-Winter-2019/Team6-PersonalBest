package com.example.personalbest;

import android.app.Activity;
import android.content.SharedPreferences;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import java.util.Calendar;

public class SaveLocal {
    final int DAYS_TO_KEEP_TRACK_OF=7;

    private SharedPreferences exercisePreferences;
    private SharedPreferences.Editor editor;
    Activity activity;

    public SaveLocal(){

    }

    public SaveLocal(Activity activity){
        this.activity = activity;
        exercisePreferences= this.activity.getSharedPreferences("exercise", Context.MODE_PRIVATE);
        editor=exercisePreferences.edit();
    }

    public boolean isLastSessionActive(){
        return exercisePreferences.getBoolean("exerciseActive",false  );
    }
    public void saveStartSessionStepCount(long stepCount){
        editor.putLong("startSessionStepCount",stepCount);
        editor.apply();
    }

    public void saveHeight(int feet, int inches) {
        editor.putInt("height_feet", feet);
        editor.putInt("height_inches", inches);
        editor.apply();
    }

    public int getHeightFeet() {
        return exercisePreferences.getInt("height_feet", 5);
    }

    public int getHeightInches() {
        return exercisePreferences.getInt("height_inches", 8);
    }

    public boolean containsHeight() {
        return exercisePreferences.contains("height_feet");
    }

    public void clearHeight() {
        editor.remove("height_feet");
        editor.remove("height_inches");
        editor.commit();
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
        if(daysBefore<DAYS_TO_KEEP_TRACK_OF) {
            editor.putLong("" + daysBefore + "DaysBeforeBackgroundStepCount", stepCount);
            editor.apply();
        }
    }
    //Returns the background step count of the day (dayBefore) before current day
    public long getBackgroundStepCount(int daysBefore){
        if(daysBefore<DAYS_TO_KEEP_TRACK_OF) {
            return exercisePreferences.getLong("" + daysBefore + "DaysBeforeBackgroundStepCount", 0);
        }else return -1;
    }
    //Sets the exercise step count of the day (dayBefore) before current day
    public void setExerciseStepCount(long stepCount, int daysBefore){
        if(daysBefore<DAYS_TO_KEEP_TRACK_OF) {
            editor.putLong("" + daysBefore + "DaysBeforeExerciseStepCount", stepCount);
            editor.apply();
        }
    }
    //Returns the exercise step count of the day (dayBefore) before current day
    public long getExerciseStepCount(int daysBefore){
        if(daysBefore<DAYS_TO_KEEP_TRACK_OF) {
            return exercisePreferences.getLong("" + daysBefore + "DaysBeforeExerciseStepCount", 0);
        }else return -1;
    }

    //methods to set and get last exercise times
    public long getLastExerciseSteps() {
        return exercisePreferences.getLong("LastExerciseStep",0);
    }
    public float getLastExerciseSpeed() {
        return exercisePreferences.getFloat("LastExerciseSpeed",0);
    }
    public String getLastExerciseTime() {
        long startTime = exercisePreferences.getLong("LastExerciseTimeStart", 0);
        long endTime = exercisePreferences.getLong("LastExerciseTimeEnd", 0);
        return (new Calc(getHeightInches(), getHeightFeet())).calcTime(startTime, endTime);
    }
    public void setLastExerciseSteps(long steps) {
        editor.putLong("LastExerciseStep",steps);
        editor.apply();
    }
    public void setLastExerciseSpeed(float speed) {
        editor.putFloat("LastExerciseSpeed", speed);
        editor.apply();
    }
    public void setLastExerciseTimeStart(Long time) {
        editor.putLong("LastExerciseTimeStart", time);
        editor.apply();
    }
    public void setLastExerciseTimeEnd(Long time) {
        editor.putLong("LastExerciseTimeEnd", time);
        editor.apply();
    }
    public long getLastExerciseTimeEnd() {
         return exercisePreferences.getLong("LastExerciseTimeEnd", 0);
    }


    public void setCurrSubGoal(int subGoal){
        editor.putInt("currsubGoal", subGoal);
        editor.apply();

    }
    public void setOldSubGoal(int old){
        editor.putInt("oldSubGoal", old);
        editor.apply();
    }
    public void setSpeed(float speed){
        editor.putFloat("speed", speed);
        editor.apply();
    }
    public void setSteps(int steps){
        editor.putInt("steps", steps);
        editor.apply();
    }
    public void setTime(long time){
        editor.putLong("time", time);
        editor.apply();
    }
    public int getCurrSubGoal(){
        return exercisePreferences.getInt("currsubGoal", 500);

    }
    public int getOldSubGoal(){
        return exercisePreferences.getInt("oldSubGoal", 0);
    }
    public float getSpeed(){
        return exercisePreferences.getFloat("speed", 0);
    }
    public int getSteps(){
        return exercisePreferences.getInt("steps", 0);
    }
    public long getTime(long time){
        return exercisePreferences.getLong("time", 0);
    }
    public void setGoal(int goal) {
        editor.putInt("goal", goal);
        editor.apply();
        setAchieved(false);
    }
    public int getGoal(){
        return exercisePreferences.getInt("goal", 5000);
    }

    public  boolean isAchieved(){
        return exercisePreferences.getBoolean("goalAchieved", false);
    }

    public void setAchieved(boolean isAchieved){
        editor.putBoolean("goalAchieved", isAchieved);
        editor.apply();
    }

    public void setLastLogin(Calendar cal){
        Calendar newCal=Calendar.getInstance();
        newCal.setTime(cal.getTime());
        //newCal.setTimeInMillis(cal.getTimeInMillis());

        newCal.set(Calendar.HOUR_OF_DAY,0);
        newCal.set(Calendar.MINUTE,0);
        newCal.set(Calendar.SECOND,0);
        newCal.set(Calendar.MILLISECOND,0);
        editor.putLong("lastLoginTime",newCal.getTimeInMillis());
        editor.apply();
    }
    public Calendar getLastLogin(){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(exercisePreferences.getLong("lastLoginTime",0));
        return cal;
    }

    public void clearStepData(){
        for (int i = 0; i < DAYS_TO_KEEP_TRACK_OF; i++) {
            setExerciseStepCount(0, i);
            setBackgroundStepCount(0, i);
        }
    }
    public long getGoals(int daysBefore){
        if(daysBefore < DAYS_TO_KEEP_TRACK_OF){
            return exercisePreferences.getLong("" + daysBefore + "DaysBeforeGoal", 0);
        }else return -1;
    }

    public void setPreviousDayGoal(long goal, int daysBefore){
        if(daysBefore < DAYS_TO_KEEP_TRACK_OF){
            editor.putLong("" + daysBefore + "DaysBeforeGoal", goal);
            editor.apply();
        }
    }
    public void clearGoalData() {
        for (int i = 0; i < DAYS_TO_KEEP_TRACK_OF; i++) {
            setPreviousDayGoal(0, i);
        }
    }


}
