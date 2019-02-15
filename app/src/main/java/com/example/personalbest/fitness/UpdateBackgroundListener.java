package com.example.personalbest.fitness;

import android.app.Activity;

import com.example.personalbest.SaveLocal;
import com.example.personalbest.StepCountActivity;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Calendar;
import java.util.Date;

public class UpdateBackgroundListener implements OnSuccessListener<DataReadResponse> {
    SaveLocal saveLocal;
    int daysBefore;
    Activity activity;
    public UpdateBackgroundListener(Activity activity, int daysBefore){
        super();
        this.activity=activity;
        this.daysBefore=daysBefore;
        saveLocal=new SaveLocal(activity);

    }
    public void setDay(int daysBefore){
        this.daysBefore=daysBefore;
    }

    @Override
    public void onSuccess(DataReadResponse dataReadResponse) {
        int stepCount=GoogleFitAdapter.getSteps(dataReadResponse);
        saveLocal.setBackgroundStepCount(stepCount-saveLocal.getExerciseStepCount(daysBefore),daysBefore);
        System.out.println("NUMBER OF STEPS: "+stepCount);
        //Update data
    }

}
