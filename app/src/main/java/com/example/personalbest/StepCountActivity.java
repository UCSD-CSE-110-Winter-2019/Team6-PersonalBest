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

import com.example.personalbest.fitness.Encouragement;
import com.example.personalbest.fitness.FitnessService;
import com.example.personalbest.fitness.FitnessServiceFactory;
import com.example.personalbest.fitness.HeightPickerFragment;
import com.example.personalbest.fitness.WalkStats;

import java.util.Calendar;


public class StepCountActivity extends AppCompatActivity{

    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";

    private static final String TAG = "StepCountActivity";


    private TextView textSteps;
    public long numSteps;
    private FitnessService fitnessService;
    private Background runner;
    Exercise exercise;

    SaveLocal saveLocal;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count);
        textSteps = findViewById(R.id.textSteps);

        //Object to save values
        saveLocal = new SaveLocal(StepCountActivity.this);

        String fitnessServiceKey = getIntent().getStringExtra(FITNESS_SERVICE_KEY);
        fitnessService = FitnessServiceFactory.create(fitnessServiceKey, this);

        fitnessService.updateStepCount();
        int goalSteps = saveLocal.getGoal();
        TextView goalText = findViewById(R.id.goal);
        goalText.setText("Goal: "+goalSteps);
        runner = new Background();
        runner.execute();
        fitnessService.setup();

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
        if(!myPrefs.contains("height_feet")) {
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
        textSteps.setText(stepCount+" steps");
        numSteps = stepCount;
    }




    private class Background extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(String... text) {

            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            Encouragement encourage;
            encourage = new Encouragement(StepCountActivity.this);
            fitnessService.updateStepCount();
            if(exercise.isActive()){
                WalkStats stats = new WalkStats(StepCountActivity.this);
                stats.update();
            }
            if(hour>=20)
                encourage.showEncouragement();

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
            while(true) {
                publishProgress();
            }
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }
}
