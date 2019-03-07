package com.example.personalbest;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.personalbest.fitness.FitnessService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataUpdateRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

public class Exercise {
    SaveLocal saveLocal;
    FitnessService fitnessService;
    Activity activity;
    public Exercise(StepCountActivity activity, FitnessService fitnessService){
        this.activity=activity;
        this.saveLocal=new SaveLocal(activity);
        this.fitnessService=fitnessService;
    }

    public void startExercise(Calendar calendar){
        saveLocal.setStartSession(calendar);
        long dailyStepCount= fitnessService.getDailyStepCount(calendar);
        saveLocal.saveStartSessionStepCount(dailyStepCount);
    }
    //Returns the step count of this exercise

    public long stopExercise(Calendar calendar){
        Long time = calendar.getTimeInMillis();
        saveLocal.setLastExerciseTimeEnd(time);
        saveLocal.setStopSession();
        long stepDifference=fitnessService.getDailyStepCount(calendar)-saveLocal.getStartSessionStepCount();
        saveLocal.setExerciseStepCount(saveLocal.getExerciseStepCount(0)+stepDifference,0);

        saveLocal.setLastExerciseSteps(stepDifference);
        saveLocal.setLastExerciseSpeed(saveLocal.getSpeed());
        saveLocal.setLastExerciseTimeStart(saveLocal.getLastSessionStartTime());
        DataSource dataSource =
                new DataSource.Builder()
                        .setStreamName("Exercise Steps")
                        .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                        .setAppPackageName(activity)
                        .setType(DataSource.TYPE_RAW)
                        .build();

        // Create a data set
        int stepCountDelta = (int)saveLocal.getLastExerciseSteps();
        DataSet dataSet = DataSet.create(dataSource);

        // For each data point, specify a start time, end time, and the data value -- in this case,
        // the number of new steps.
        DataPoint dataPoint =
                dataSet.createDataPoint().setTimeInterval(saveLocal.getLastSessionStartTime(), calendar.getTimeInMillis(), TimeUnit.MILLISECONDS);
        dataPoint.getValue(Field.FIELD_STEPS).setInt(stepCountDelta);
        dataSet.add(dataPoint);
        DataUpdateRequest request = new DataUpdateRequest.Builder()
                .setDataSet(dataSet)
                .setTimeInterval(saveLocal.getLastSessionStartTime(), calendar.getTimeInMillis(), TimeUnit.MILLISECONDS)
                .build();


        Task<Void> response = Fitness.getHistoryClient(activity, GoogleSignIn.getLastSignedInAccount(activity)).updateData(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Successfully added 500 steps!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "There was a problem adding 500 steps.");
                    }
                });





            Log.d(TAG,"This Exercise Step Count: "+stepDifference);
            Log.d(TAG,"Daily Exercise Step Count: "+saveLocal.getExerciseStepCount(0));
            return stepDifference;
    }

    public boolean isActive(){
        return saveLocal.isLastSessionActive();
    }
}
