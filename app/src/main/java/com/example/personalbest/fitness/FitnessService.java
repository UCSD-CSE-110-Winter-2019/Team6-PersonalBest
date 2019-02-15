package com.example.personalbest.fitness;

public interface FitnessService {
    int getRequestCode();
    void setup();
    void updateStepCount();
    long getDailyStepCount();
    void printStepCount(long startMillis, long endMillis);

}
