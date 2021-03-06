package com.example.personalbest.fitness;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.personalbest.SaveLocal;
import com.example.personalbest.database.FirebaseAdapter;
import com.example.personalbest.database.FirebaseFactory;
import com.example.personalbest.database.IFirebase;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Calendar;

public class UpdateBackgroundListener implements OnSuccessListener<DataReadResponse>, OnFailureListener{
    SaveLocal saveLocal;
    int daysBefore;
    Activity activity;
    IFirebase firebaseAdapter;
    GoogleFitAdapter googleFitAdapter;
    public UpdateBackgroundListener(Activity activity, int daysBefore, GoogleFitAdapter googleFitAdapter){
        super();
        this.googleFitAdapter=googleFitAdapter;
        this.activity=activity;
        this.daysBefore=daysBefore;
        saveLocal=new SaveLocal(activity);
        firebaseAdapter = FirebaseFactory.getFirebase();
    }
    public void setDay(int daysBefore){
        this.daysBefore=daysBefore;
    }

    @Override
    public void onSuccess(DataReadResponse dataReadResponse) {
        int stepCount=GoogleFitAdapter.getSteps(dataReadResponse);
        int exerciseStepCount=(int)saveLocal.getExerciseStepCount(daysBefore);
        int backgroundStepCount=stepCount-exerciseStepCount;
        saveLocal.setBackgroundStepCount(backgroundStepCount,daysBefore);
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -daysBefore);
        if (googleFitAdapter.getEmail()!=null) {
            firebaseAdapter.pushStepStats(calendar, backgroundStepCount, exerciseStepCount, googleFitAdapter.getEmail());
        }
        System.out.println("NUMBER OF STEPS: "+stepCount);
        //Update data
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        Log.d("ONFAILURE","IT FAILED" );
    }
}
