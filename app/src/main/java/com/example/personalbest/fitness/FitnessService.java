package com.example.personalbest.fitness;

import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Calendar;

public interface FitnessService {
    int getRequestCode();
    void setup();
    void updateStepCount(Calendar currentTime);
    long getDailyStepCount(Calendar cal);
    //void listenStepCount(long startMillis, long endMillis, OnSuccessListener<DataReadResponse> listener);
    void updateBackgroundCount(Calendar currentTime, int daysBefore);
    boolean isSetupComplete();
    boolean startRecording();


}
