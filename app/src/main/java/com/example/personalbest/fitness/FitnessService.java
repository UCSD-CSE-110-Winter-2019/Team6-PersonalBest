package com.example.personalbest.fitness;

import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnSuccessListener;

public interface FitnessService {
    int getRequestCode();
    void setup();
    void updateStepCount();
    long getDailyStepCount();
    void listenStepCount(long startMillis, long endMillis, OnSuccessListener<DataReadResponse> listener);
    void updateBackgroundCount(int daysBefore);

}
