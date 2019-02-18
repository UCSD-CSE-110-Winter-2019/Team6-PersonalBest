package com.example.personalbest;

import android.app.Activity;
import android.util.Log;

import com.example.personalbest.fitness.FitnessService;

import java.util.Calendar;

import static android.content.ContentValues.TAG;

public class Exercise {
    SaveLocal saveLocal;
    FitnessService fitnessService;
    Calendar calendar;

    public Exercise(StepCountActivity activity, FitnessService fitnessService){
            this.saveLocal=new SaveLocal(activity);
            this.fitnessService=fitnessService;
    }

    public void startExercise(Calendar calendar){
        saveLocal.setStartSession(shiftTime());
        long dailyStepCount= fitnessService.getDailyStepCount(calendar);
        saveLocal.saveStartSessionStepCount(dailyStepCount);
        this.calendar = calendar;
    }
    //Returns the step count of this exercise

    public long stopExercise(Calendar calendar){
            this.calendar = shiftTime();
            Long time = calendar.getTimeInMillis();
            saveLocal.setLastExerciseTimeEnd(time);
            saveLocal.setStopSession();
            long stepDifference=fitnessService.getDailyStepCount(calendar)-saveLocal.getStartSessionStepCount();
            saveLocal.setExerciseStepCount(saveLocal.getExerciseStepCount(0)+stepDifference,0);

            saveLocal.setLastExerciseSteps(stepDifference);
            saveLocal.setLastExerciseSpeed(saveLocal.getSpeed());
            saveLocal.setLastExerciseTimeStart(saveLocal.getLastSessionStartTime());
            //saveLocal.setLastExerciseTimeEnd((Calendar.getInstance().getTimeInMillis()));

            Log.d(TAG,"This Exercise Step Count: "+stepDifference);
            Log.d(TAG,"Daily Exercise Step Count: "+saveLocal.getExerciseStepCount(0));
            return stepDifference;
    }
    //Gets the step count of the current exercise
    public long getCurrentExerciseStepCount(){
        if(isActive()){
            return fitnessService.getDailyStepCount(shiftTime()) -saveLocal.getStartSessionStepCount();
        }
        else return 0;
    }
    public boolean isActive(){
        return saveLocal.isLastSessionActive();
    }

    public Calendar shiftTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(cal.getTimeInMillis() - saveLocal.getTimeDiff());
        return cal;
    }
}

