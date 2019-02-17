package com.example.personalbest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.*;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.personalbest.fitness.Encouragement;
import com.example.personalbest.fitness.FitnessService;
import com.example.personalbest.fitness.FitnessServiceFactory;
import com.example.personalbest.fitness.UpdateBackgroundListener;
import com.example.personalbest.fitness.WalkStats;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class StepCountActivity extends AppCompatActivity{

    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";

    private static final String TAG = "StepCountActivity";


    private TextView textSteps;
    private TextView goalView;

    private TextView exerciseSteps;
    private TextView speed;
    private TextView timeElapsed;


    public long numSteps;
    public long goalSteps;
    private FitnessService fitnessService;
    private Background runner;
    Exercise exercise;
    private boolean isRecording;
    SaveLocal saveLocal;
    EndDay endDay;
    boolean daysUpdated;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);






        setContentView(R.layout.activity_step_count);
        textSteps = findViewById(R.id.textSteps);
        goalView = findViewById(R.id.goal);
        //Object to save values
        isRecording=false;
        saveLocal = new SaveLocal(StepCountActivity.this);

        String fitnessServiceKey = getIntent().getStringExtra(FITNESS_SERVICE_KEY);
        fitnessService = FitnessServiceFactory.create(fitnessServiceKey, this);



        goalSteps = saveLocal.getGoal();
        goalView.setText("Goal: "+goalSteps);
        saveLocal.setCurrSubGoal(500);
        Calendar cal = Calendar.getInstance();
        runner = new Background(cal);
        runner.execute();
        fitnessService.setup();

        //if(!fitnessService.isSetupComplete()) fitnessService.startRecording();
        fitnessService.updateStepCount(Calendar.getInstance());
        exerciseSteps = findViewById(R.id.walkSteps);
        speed = findViewById(R.id.textSpeed);
        timeElapsed = findViewById(R.id.walkTime);

        exerciseSteps.setText("Steps: " + saveLocal.getLastExerciseSteps());
        speed.setText("MPH: " + saveLocal.getLastExerciseSpeed());
        timeElapsed.setText("Time Elapsed: " + saveLocal.getLastExerciseTime());


        //Button to start and stop exercises
        final Button startExerciseButton = findViewById(R.id.startExerciseButton);
        exercise=new Exercise(StepCountActivity.this, fitnessService);
        //While initializing, if an exercise was left active, set the button accordingly
        if(exercise.isActive()){
            startExerciseButton.setText("Stop Exercise");
        }

        startExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(exercise.isActive()){
                    //STOP EXERCISING
                    startExerciseButton.setText("Start Exercise");
                    Calendar calendar=Calendar.getInstance();
                    exercise.stopExercise(calendar);
                }
                else{
                    //START EXERCISING
                    startExerciseButton.setText("Stop Exercise");
                    Calendar calendar=Calendar.getInstance();
                    exercise.startExercise(calendar);
                }
            }
        });

        goalView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                DialogFragment goalFrag = new SetGoalFragment();
                goalFrag.show(getSupportFragmentManager(), "Set Goal");

            }
        });


        //Log.i( "TAG","hello+test " + myPrefs.getString("height_feet",""));
        if(!saveLocal.containsHeight()) {
            DialogFragment nameFrag = new HeightPickerFragment();
            nameFrag.show(getSupportFragmentManager(), "Height");
        }

        endDay=new EndDay(saveLocal);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//       If authentication was required during google fit setup, this will be called after the user authenticates
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == fitnessService.getRequestCode()) {
                fitnessService.updateStepCount(Calendar.getInstance());
            }
        } else {
            Log.e(TAG, "ERROR, google fit result code: " + resultCode);
        }
    }

    public void setStepCount(long stepCount) {
        textSteps.setText(Long.toString(stepCount)+" steps");
        goalView.setText("Goal: " + saveLocal.getGoal());
        numSteps = stepCount;
    }

    // for unit tests
    public void onResume(Calendar cal) {
        super.onResume();
        runner = new Background(cal);
        runner.execute();
    }

    public void onResume() {
        super.onResume();
        Calendar c = Calendar.getInstance();
        runner = new Background(c);
        runner.execute();
      }

    public void printSteps(View view) {
        //endDay.newDayActions(1,fitnessService);
        //endDay.updateDate(Calendar.getInstance());
        for(int i=0;i<7; i++){
            Log.d(TAG,""+i+" days before Background count: "+saveLocal.getBackgroundStepCount(i));
            Log.d(TAG,""+i+" days before Exercise count: "+saveLocal.getExerciseStepCount(i));
        }

    }

    public void updateSteps(View view) {
        onResume();

    }

    public void putData(View view){
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        //cal.add(Calendar.DAY_OF_YEAR, -1);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.SECOND, -50);
        long startTime = cal.getTimeInMillis();

// Create a data source
        DataSource dataSource =
                new DataSource.Builder()
                        .setAppPackageName(this)
                        .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                        .setStreamName(TAG + " - step count")
                        .setType(DataSource.TYPE_RAW)
                        .build();

// Create a data set
        int stepCountDelta = 500;
        DataSet dataSet = DataSet.create(dataSource);
// For each data point, specify a start time, end time, and the data value -- in this case,
// the number of new steps.
        DataPoint dataPoint =
                dataSet.createDataPoint().setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
        dataPoint.getValue(Field.FIELD_STEPS).setInt(stepCountDelta);
        dataSet.add(dataPoint);

        Task<Void> response = Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this)).insertData(dataSet)
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
                });;
    }

    public void launchGrapActivity(View view) {
        Intent intent = new Intent(this, GraphActivity.class);
        int dailySteps=(int)fitnessService.getDailyStepCount(Calendar.getInstance());
        intent.putExtra("numSteps", dailySteps);
        startActivity(intent);
    }




    public class Background extends AsyncTask<String, String, String> {
        DialogFragment goalFrag;
        Encouragement encourage;
        Calendar c;
        int hour;

        public Background(Calendar cal){
            c = cal;
        }

        @Override
        protected void onPreExecute() {
            hour = c.get(Calendar.HOUR_OF_DAY);

            encourage = new Encouragement(StepCountActivity.this);
        }

        @Override
        protected void onProgressUpdate(String... text) {
            if(!isRecording){
                isRecording=fitnessService.startRecording();
            }

            int daySkip=endDay.dayDifference(c);
            if(daySkip != 0 && isRecording){
                endDay.newDayActions(daySkip,fitnessService,c);
                endDay.updateDate(c);
            }

            hour = c.get(Calendar.HOUR_OF_DAY);
            fitnessService.updateStepCount(c);
            //if(exercise.isActive()){
                WalkStats stats = new WalkStats(StepCountActivity.this);
                stats.update();
            //}

            if (numSteps >= saveLocal.getGoal() && !saveLocal.isAchieved()){
                saveLocal.setAchieved(true);
                goalFrag = new GoalFragment();
                goalFrag.show(getSupportFragmentManager(), "Goal");
            }
            if(hour>=20) {
                encourage.showEncouragement();
            }

            //onResume(c);
        }

        @Override
        protected String doInBackground(String... strings) {
            publishProgress();

            //}

            return null;
        }


        @Override
        protected void onPostExecute(String result) {

        }
    }
}
