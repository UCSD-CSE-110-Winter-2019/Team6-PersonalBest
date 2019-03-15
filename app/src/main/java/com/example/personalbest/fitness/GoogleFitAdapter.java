package com.example.personalbest.fitness;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.personalbest.SaveLocal;
import com.example.personalbest.StepCountActivity;
import com.example.personalbest.database.FirebaseAdapter;
import com.example.personalbest.database.FirebaseFactory;
import com.example.personalbest.database.IFirebase;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Scope;
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
import java.util.List;
import java.util.concurrent.TimeUnit;


public class GoogleFitAdapter implements FitnessService {
    private final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = System.identityHashCode(this) & 0xFFFF;
    private final String TAG = "GoogleFitAdapter";
    private final String SERVER_CLIENT_ID="629223737879-3l5248hgqad0asogmkvr30oj3tle7vo7.apps.googleusercontent.com";
    private long dailyStepCount;
    private StepCountActivity activity;
    private boolean isCumulativeSet;
    private boolean isDeltaSet;
    private boolean isAggregateSet;
    private SaveLocal saveLocal;

    public GoogleFitAdapter(StepCountActivity activity) {
        this.activity = activity;
        isCumulativeSet=false;
        isAggregateSet=false;
        isDeltaSet=false;
        saveLocal = new SaveLocal(activity);
    }


    public void setup() {
        String serverClientId = SERVER_CLIENT_ID;




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

        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        ActivityCompat.startActivityForResult(activity,signInIntent,GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,null);


    }

    public String getEmail(){

        String email =  GoogleSignIn.getLastSignedInAccount(activity).getEmail();
        saveLocal.setEmail(email);
        return email;
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
     * Gets the number of steps of the currentTime's day and updates the variables
     * in this class and in StepCountActivity.
     */
    public void updateStepCount(Calendar currentTime) {
        GoogleSignInAccount lastSignedInAccount=GoogleSignIn.getLastSignedInAccount(activity);
//        String authCode=lastSignedInAccount.getServerAuthCode();
        if (lastSignedInAccount == null) {
            return;
        }
        //Find the end of the day
        Calendar cal=Calendar.getInstance();

        long endTime = StepCountActivity.generateEndTime(cal);
        //Find the start of the day

        long startTime = StepCountActivity.generateStartTime(cal);

        DataReadRequest readRequest =
                new DataReadRequest.Builder()
                        .read(DataType.TYPE_STEP_COUNT_DELTA)
                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build();


        Fitness.getHistoryClient(activity, lastSignedInAccount)
                .readData(readRequest)
                .addOnSuccessListener(
                        new OnSuccessListener<DataReadResponse>() {
                            IFirebase IFirebase = FirebaseFactory.getFirebase();
                            SaveLocal saveLocal=new SaveLocal(activity);
                            @Override
                            public void onSuccess(DataReadResponse dataReadResponse) {
                                int stepCount=GoogleFitAdapter.getSteps(dataReadResponse);
                                int exerciseStepCount=(int)saveLocal.getExerciseStepCount(0);
                                int backgroundStepCount=stepCount-exerciseStepCount;
                                activity.setStepCount(stepCount);
                                dailyStepCount=stepCount;
                                Log.d(TAG, "Total steps: " + stepCount);
                                Calendar calendar=Calendar.getInstance();
                                if(getEmail()!=null) {
                                    IFirebase.pushStepStats(calendar, backgroundStepCount, exerciseStepCount, getEmail());
                                }
                                System.out.println("NUMBER OF STEPS: "+stepCount);
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

    //Gets the number of steps from a given dataReadResponse.
    public static int getSteps(DataReadResponse dataReadResponse){
        List<DataSet> dataSets = dataReadResponse.getDataSets();

        for(DataSet dataSet:dataSets) {
            int stepCount = 0;
            for (DataPoint dp : dataSet.getDataPoints()) {
                Log.i("GoogleFitAdapter","STREAM NAME: "+dp.getOriginalDataSource().getStreamName());
                for (Field field : dp.getDataType().getFields()) {
                    stepCount += dp.getValue(field).asInt();
                }
            }
            return stepCount;
        }
        return 0;
    }
    //Updates the background steps of a day in the past from the current day.
    public void updateBackgroundCount(Calendar currentTime, int daysBefore){

        //Find the end of the desired day
        Calendar cal=Calendar.getInstance();

        long endTime = StepCountActivity.generateEndTime(cal);
        //Find the start of the desired day

        long startTime = StepCountActivity.generateStartTime(cal);

        listenStepCount(startTime,endTime,new UpdateBackgroundListener(activity,daysBefore,this),new UpdateBackgroundListener(activity,daysBefore,this));
    }
    //Creates a read request and sends it to the listener to update variables.
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
    //Returns true if the google fit is tracking variables.
    public boolean isSetupComplete(){
        return (isAggregateSet&&isDeltaSet&&isCumulativeSet);
    }
}
