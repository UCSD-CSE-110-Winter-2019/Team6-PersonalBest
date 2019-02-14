package com.example.personalbest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.personalbest.fitness.FitnessService;
import com.example.personalbest.fitness.FitnessServiceFactory;

import java.util.Calendar;


public class StepCountActivity extends AppCompatActivity{

    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";

    private static final String TAG = "StepCountActivity";

    private TextView textSteps;
    private long numSteps;
    private FitnessService fitnessService;
    private EncourageTask runner;
    Exercise exercise;

    SaveLocal saveLocal;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count);
        textSteps = findViewById(R.id.textSteps);


        String fitnessServiceKey = getIntent().getStringExtra(FITNESS_SERVICE_KEY);
        fitnessService = FitnessServiceFactory.create(fitnessServiceKey, this);
        Button btnUpdateSteps = findViewById(R.id.buttonUpdateSteps);
        btnUpdateSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fitnessService.updateStepCount();
                runner = new EncourageTask();
                runner.execute();
            }
        });
        fitnessService.setup();

        //Button to start and stop exercises
        final Button startExerciseButton = findViewById(R.id.startExerciseButton);
        //Object to save values
        saveLocal = new SaveLocal(this);
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
                    exercise.stopExercise();
                }
                else{
                    //START EXERCISING
                    startExerciseButton.setText("Stop Exercise");
                    Calendar calendar=Calendar.getInstance();
                    exercise.startExercise(calendar);
                }
            }
        });

        SharedPreferences myPrefs = getSharedPreferences("height", MODE_PRIVATE);

        //Log.i( "TAG","hello+test " + myPrefs.getString("height_feet",""));
        if(!saveLocal.containsHeight()) {
            DialogFragment nameFrag = new HeightPickerFragment();
            nameFrag.show(getSupportFragmentManager(), "Height");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//       If authentication was required during google fit setup, this will be called after the user authenticates
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == fitnessService.getRequestCode()) {
                fitnessService.updateStepCount();
            }
        } else {
            Log.e(TAG, "ERROR, google fit result code: " + resultCode);
        }
    }

    public void setStepCount(long stepCount) {
        textSteps.setText(String.valueOf(stepCount));
        numSteps = stepCount;
    }

    public void showEncouragement(){
       // int x = Integer.parseInt(textSteps.getText().toString()) / 100;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast t = Toast.makeText(StepCountActivity.this,"Good job! You're already at " + (numSteps/100) + "% of the daily recommended number of steps.", Toast.LENGTH_LONG);
                t.show();
            }
        });

    }


    private class EncourageTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(String... text) {
            showEncouragement();

        }

        @Override
        protected String doInBackground(String... strings) {
          /*  while (true) {
                if (isCancelled()) {
                    break;
                }
                if(numSteps >= 1000){
                    publishProgress();
                    break;
                }
            }*/

            if(numSteps>=1000)
                publishProgress();
            return "ALL GOOD";
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }
}
