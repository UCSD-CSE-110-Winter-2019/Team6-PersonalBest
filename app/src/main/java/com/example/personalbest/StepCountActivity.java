package com.example.personalbest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.personalbest.fitness.FitnessService;
import com.example.personalbest.fitness.FitnessServiceFactory;
import com.example.personalbest.fitness.HeightPickerFragment;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;

import java.sql.Time;
import java.util.concurrent.TimeUnit;
import java.util.Calendar;


public class StepCountActivity extends AppCompatActivity {

    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";

    private static final String TAG = "StepCountActivity";

    private TextView textSteps;
    private long numSteps;
    private FitnessService fitnessService;
    private EncourageTask runner;
    private int currSubGoal;
    private int oldSubGoal;
    private int goal;

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

        SharedPreferences myPrefs = getSharedPreferences("height", MODE_PRIVATE);

        currSubGoal = myPrefs.getInt("currSubGoal", 500);
        oldSubGoal = myPrefs.getInt("oldSubGoal", 0);
        goal = myPrefs.getInt("goal", 5000);


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
        textSteps.setText(String.valueOf(stepCount));
        numSteps = stepCount;
    }

    public void showEncouragement(){
       // int x = Integer.parseInt(textSteps.getText().toString()) / 100;
        if(numSteps > currSubGoal) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int newSubGoal = ((int)numSteps/500)*500;
                    Toast t = Toast.makeText(StepCountActivity.this, "You've increased your daily steps by over " + (newSubGoal - oldSubGoal) + " steps. Keep up the good work!", Toast.LENGTH_LONG);
                    t.show();
                    oldSubGoal = newSubGoal;
                    currSubGoal = oldSubGoal+500;
                    SharedPreferences pref = getSharedPreferences("height", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt("oldSubGoal", oldSubGoal);
                    editor.putInt("currSubGoal", currSubGoal);
                    editor.apply();
                }
            });
        }

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
            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);

            if(hour>=20)
                publishProgress();
            return "ALL GOOD";
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }
}
