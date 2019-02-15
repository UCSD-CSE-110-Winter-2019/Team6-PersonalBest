package com.example.personalbest.fitness;

import android.app.Activity;

import com.example.personalbest.SaveLocal;
import com.example.personalbest.StepCountActivity;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnSuccessListener;

public class GoogleListener implements OnSuccessListener<DataReadResponse> {
    SaveLocal saveLocal;
    Activity activity;
    public GoogleListener(Activity activity){
        super();
        this.activity=activity;
    }
    @Override
    public void onSuccess(DataReadResponse dataReadResponse) {
        saveLocal=new SaveLocal(activity);
        int stepCount=GoogleFitAdapter.getSteps(dataReadResponse);
        System.out.println("NUMBER OF STEPS: "+stepCount);
        //Update data
    }

}
