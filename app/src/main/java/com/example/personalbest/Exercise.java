package com.example.personalbest;

import android.app.Activity;
import android.util.Log;

import com.example.personalbest.fitness.FitnessService;

import java.util.Calendar;

import static android.content.ContentValues.TAG;

public class Exercise {
    SaveLocal saveLocal;
    FitnessService fitnessService;

    public Exercise(StepCountActivity activity, FitnessService fitnessService){
        this.saveLocal=new SaveLocal(activity);
        this.fitnessService=fitnessService;
    }

    public void startExercise(Calendar calendar){
        saveLocal.setStartSession(calendar);
        long dailyStepCount= fitnessService.getDailyStepCount();
        saveLocal.saveStartSessionStepCount(dailyStepCount);

    }
    //Returns the step count of this exercise
    public long stopExercise(){
        saveLocal.setStopSession();
        long stepDifference=fitnessService.getDailyStepCount()-saveLocal.getStartSessionStepCount();
        saveLocal.setExerciseStepCount(saveLocal.getExerciseStepCount(0)+stepDifference,0);
        Log.d(TAG,"This Exercise Step Count: "+stepDifference);
        Log.d(TAG,"Daily Exercise Step Count: "+saveLocal.getExerciseStepCount(0));
        return stepDifference;
    }
    //Gets the step count of the current exercise
    public long getCurrentExerciseStepCount(){
        if(isActive()){
            return fitnessService.getDailyStepCount()-saveLocal.getStartSessionStepCount();
        }
        else return 0;
    }

    public boolean isActive(){
        return saveLocal.isLastSessionActive();
    }
}
