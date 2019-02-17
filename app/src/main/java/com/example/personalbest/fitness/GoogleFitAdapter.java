package com.example.personalbest.fitness;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.personalbest.StepCountActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GoogleFitAdapter implements FitnessService {
    private final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = System.identityHashCode(this) & 0xFFFF;
    private final String TAG = "GoogleFitAdapter";

    private long dailyStepCount;
    private StepCountActivity activity;
    private boolean isCumulativeSet;
    private boolean isDeltaSet;
    private boolean isAggregateSet;

    public GoogleFitAdapter(StepCountActivity activity) {
        this.activity = activity;
        isCumulativeSet=false;
        isAggregateSet=false;
        isDeltaSet=false;

    }


    public void setup() {
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                .build();

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(activity), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    activity, // your activity
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(activity),
                    fitnessOptions);
                    //startRecording();


        } else {
            //updateStepCount(cal);
            //startRecording();
        }

    }

    public boolean startRecording() {
        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (lastSignedInAccount == null) {
            return false;
        }

        Fitness.getRecordingClient(activity, GoogleSignIn.getLastSignedInAccount(activity))
                .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Successfully subscribed!");
                        isCumulativeSet=true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "There was a problem subscribing.");
                    }
                });
        Fitness.getRecordingClient(activity, GoogleSignIn.getLastSignedInAccount(activity))
                .subscribe(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Successfully subscribed!");
                        isDeltaSet=true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "There was a problem subscribing.");
                    }
                });
        Fitness.getRecordingClient(activity, GoogleSignIn.getLastSignedInAccount(activity))
                .subscribe(DataType.AGGREGATE_STEP_COUNT_DELTA)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        isAggregateSet=true;
                        Log.i(TAG, "Successfully subscribed!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "There was a problem subscribing.");
                    }
                });
        return true;
    }


    /**
     * Reads the current daily step total, computed from midnight of the current day on the device's
     * current timezone.
     */
    public void updateStepCount(Calendar currentTime) {
        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (lastSignedInAccount == null) {
            return;
        }
        Calendar cal=Calendar.getInstance();
        cal.setTimeInMillis(currentTime.getTimeInMillis());
        cal.set(Calendar.HOUR_OF_DAY,23);
        cal.set(Calendar.MINUTE,59);
        cal.set(Calendar.SECOND,59);
        cal.set(Calendar.MILLISECOND,59);
        long endTime = cal.getTimeInMillis();

        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
        long startTime = cal.getTimeInMillis();

        DataReadRequest readRequest =
                new DataReadRequest.Builder()
                        .read(DataType.TYPE_STEP_COUNT_DELTA)
                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build();


        Fitness.getHistoryClient(activity, lastSignedInAccount)
                .readData(readRequest)
                .addOnSuccessListener(
                        new OnSuccessListener<DataReadResponse>() {
                            @Override
                            public void onSuccess(DataReadResponse dataReadResponse) {
                                int stepCount=GoogleFitAdapter.getSteps(dataReadResponse);

                                activity.setStepCount(stepCount);
                                dailyStepCount=stepCount;
                                Log.d(TAG, "Total steps: " + stepCount);
                                //Update data
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "There was a problem getting the step count.", e);
                            }
                        });



    }


    @Override
    public int getRequestCode() {
        return GOOGLE_FIT_PERMISSIONS_REQUEST_CODE;
    }

    public long getDailyStepCount(Calendar cal){
        updateStepCount(cal);
        return dailyStepCount;
    }


    public static int getSteps(DataReadResponse dataReadResponse){
        List<DataSet> dataSets = dataReadResponse.getDataSets();

        DataSet dataSet =dataSets.get(0);

        int stepCount=0;
        for (DataPoint dp : dataSet.getDataPoints()) {
            for (Field field : dp.getDataType().getFields()) {
                stepCount+= dp.getValue(field).asInt();
            }
        }
        return stepCount;
    }
    public void updateBackgroundCount(Calendar currentTime, int daysBefore){

        Calendar cal=Calendar.getInstance();
        cal.setTimeInMillis(currentTime.getTimeInMillis());
        cal.add(Calendar.DAY_OF_YEAR, -daysBefore);
        cal.set(Calendar.HOUR_OF_DAY,23);
        cal.set(Calendar.MINUTE,59);
        cal.set(Calendar.SECOND,59);
        cal.set(Calendar.MILLISECOND,59);
        long endTime = cal.getTimeInMillis();

        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
        long startTime = cal.getTimeInMillis();

        listenStepCount(startTime,endTime,new UpdateBackgroundListener(activity,daysBefore),new UpdateBackgroundListener(activity,daysBefore));
    }
    private void listenStepCount(long startMillis, long endMillis, OnSuccessListener<DataReadResponse> listener, OnFailureListener failureListener){
        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (lastSignedInAccount == null) {
            return;
    }
        DataReadRequest readRequest =
                new DataReadRequest.Builder()
                        .read(DataType.TYPE_STEP_COUNT_DELTA)
                        .setTimeRange(startMillis, endMillis, TimeUnit.MILLISECONDS)
                        .build();

        Fitness.getHistoryClient(activity, lastSignedInAccount)
                .readData(readRequest)
                .addOnSuccessListener(listener)
                .addOnFailureListener(failureListener);
        return;
    }

    public boolean isSetupComplete(){
        return (isAggregateSet&&isDeltaSet&&isCumulativeSet);
    }
}
