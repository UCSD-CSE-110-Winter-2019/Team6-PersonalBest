package com.example.personalbest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.annotation.NonNull;

import android.support.v4.app.*;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.personalbest.database.FirebaseAdapter;
import com.example.personalbest.database.FirebaseFactory;
import com.example.personalbest.database.IFirebase;
import com.example.personalbest.fitness.Encouragement;
import com.example.personalbest.fitness.FitnessService;
import com.example.personalbest.fitness.FitnessServiceFactory;
import com.example.personalbest.fitness.WalkStats;
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

//import com.google.auth.oauth2.GoogleCredentials;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class StepCountActivity extends AppCompatActivity{

    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";

    public static final String FIREBASEKEY = "FIREBASE_KEY";
    private static final String TAG = "StepCountActivity";

    public IFirebase firebaseAdapter;
    private TextView textSteps;
    private TextView goalView;

    private TextView exerciseSteps;
    private TextView speed;
    private TextView timeElapsed;

    public WalkStats stats;


    public long numSteps;
    public long goalSteps;
    private FitnessService fitnessService;
    private Background runner;
    Exercise exercise;
    private boolean isRecording;
    SaveLocal saveLocal;
    EndDay endDay;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //FirebaseApp.initializeApp(this);

        String firebaseType = this.getIntent().getStringExtra(FIREBASEKEY);


       FirebaseFactory.createFirebase(firebaseType, this);

        firebaseAdapter = FirebaseFactory.getFirebase();

        setContentView(R.layout.activity_step_count);
        textSteps = findViewById(R.id.textSteps);
        goalView = findViewById(R.id.goal);
        //Variable that turns true if FitnessService starts recording.
        isRecording=false;
        //Object to save values
        saveLocal = new SaveLocal(StepCountActivity.this);
        //Set up the fitness service
        String fitnessServiceKey = getIntent().getStringExtra(FITNESS_SERVICE_KEY);
        fitnessService = FitnessServiceFactory.create(fitnessServiceKey, this);
        fitnessService.setup();

        //Set the goal and subgoal.
        goalSteps = saveLocal.getGoal();
        setGoal(goalSteps);

        Calendar cal = Calendar.getInstance();

        //Create and start the background activity
        runner = new Background(cal);
        runner.execute();

        //Update the step count and set the screen variables
        fitnessService.updateStepCount(Calendar.getInstance());
        exerciseSteps = findViewById(R.id.walkSteps);
        speed = findViewById(R.id.textSpeed);
        timeElapsed = findViewById(R.id.walkTime);



        exerciseSteps.setText("Steps: " + saveLocal.getLastExerciseSteps());
        speed.setText("MPH: " + saveLocal.getLastExerciseSpeed());
        timeElapsed.setText("Time Elapsed: " + saveLocal.getLastExerciseTime());

        stats = new WalkStats(StepCountActivity.this);


        //Button to start and stop exercises
        final Button startExerciseButton = findViewById(R.id.startExerciseButton);
        exercise=new Exercise(StepCountActivity.this, fitnessService);
        //While initializing, if an exercise was left active, set the button accordingly
        if(exercise.isActive()){
            startExerciseButton.setText("Stop Exercise");
            startExerciseButton.setBackgroundColor(Color.parseColor("#FF0000"));

        }

        startExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(exercise.isActive()){

                    //STOP EXERCISING
                    startExerciseButton.setText("Start Exercise");
                    startExerciseButton.setBackgroundColor(Color.parseColor("#06A62B"));
                    Calendar calendar=Calendar.getInstance();
                    exercise.stopExercise(calendar);

                    stats.update();

                }
                else{
                    //START EXERCISING
                    startExerciseButton.setText("Stop Exercise");
                    startExerciseButton.setBackgroundColor(Color.parseColor("#FF0000"));
                    Calendar calendar=Calendar.getInstance();
                    exercise.startExercise(calendar);
                    stats.update();
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

    public void setGoal(long goalSteps) {
        goalView.setText("Goal: "+goalSteps);
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

    public void printSteps() {
        for(int i=0;i<7; i++){
            Log.d(TAG,""+i+" days before Background count: "+saveLocal.getBackgroundStepCount(i));
            Log.d(TAG,""+i+" days before Exercise count: "+saveLocal.getExerciseStepCount(i));
            Log.d(TAG,""+i+" days Current count: "+numSteps);

        }

    }

    public void updateSteps(View view) {
        firebaseAdapter.getUsers();

        ArrayList<String> arr = saveLocal.getFriends();
        for (String s: arr) {
            Log.d("TAGTAG", s);
        }
        //firebaseAdapter.saveFriendStepLocal("anilermi@gmail.com", Calendar.getInstance());

        onResume();
        //printSteps();


    }
    private void insert500Steps(Calendar currTime){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(currTime.getTimeInMillis());

        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.SECOND, -50);
        long startTime = cal.getTimeInMillis();

        // Create a data source
        DataSource dataSource =
                new DataSource.Builder()
                        .setStreamName("Manual Insert")
                        .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                        .setAppPackageName(this)
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
//        System.out.println("SCA UPDATE : "+dataSet.getDataSource().getName());
        dataSet.add(dataPoint);
        DataUpdateRequest request = new DataUpdateRequest.Builder()
                .setDataSet(dataSet)
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();


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
                });
        firebaseAdapter.getUsers();
    }

    public void putData(View view){
        insert500Steps(Calendar.getInstance());


    }

    public void launchGraphActivity(View view) {
        Intent intent = new Intent(this, GraphActivity.class);
        int dailySteps=(int)fitnessService.getDailyStepCount(Calendar.getInstance());
        intent.putExtra("numSteps", dailySteps);
        startActivity(intent);
    }

    public void launchFriendsList(View v){
        Intent intent = new Intent(this, FriendsListActivity.class);
        startActivity(intent);
    }

    public void addFriend(View view) {
        String email = saveLocal.getEmail();
        if (!email.equals("NO EMAIL")) {
            DialogFragment friendFrag = new AddFriendFragment();
            friendFrag.show(getSupportFragmentManager(), "Add Friend");
        }
        else {
            DialogFragment nameFrag = new NameFragment();
            nameFrag.show(getSupportFragmentManager(), "Set Name");
        }
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

            stats = new WalkStats(StepCountActivity.this);

            if (exercise.isActive()) {
                stats.update();
            }

            if (fitnessService.getDailyStepCount(c) >= saveLocal.getGoal() && !saveLocal.isAchieved()){
                saveLocal.setAchieved(true);
                goalFrag = new GoalFragment();
                goalFrag.show(getSupportFragmentManager(), "Goal");
            }
            ArrayList<String> arrayList = saveLocal.getFriends();
            if(hour>=20 && arrayList.size() == 0) {
            //if(hour>=20) {
                encourage.showEncouragement();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            publishProgress();
            return null;
        }
        @Override
        protected void onPostExecute(String result) {

        }
    }





}
