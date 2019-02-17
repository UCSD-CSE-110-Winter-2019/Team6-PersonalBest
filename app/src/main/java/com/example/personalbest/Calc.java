package com.example.personalbest;

import java.util.Calendar;

public class Calc {
    private int inches;
    private int feet;

    public Calc(int in, int ft){
        inches = in;
        feet = ft;
    }

<<<<<<< HEAD
    public float calcSpeed(long startTime, long endTime, float distance){
        long totalTime = endTime-startTime;
=======
    public float calcSpeed(long time, float distance){
        Calendar c = Calendar.getInstance();
        long current = c.getTimeInMillis();
        long totalTime = current-time;
>>>>>>> 02e738c52fc783263cb6c49706fef09924e51212
        float minutes = totalTime/60000f;
        float hours = minutes/60f;
        if (hours == 0) {
            hours =  1;
        }
        return (distance/hours);
    }

    public float calcDistance(long steps){
        int height = inches + (feet*12);
        float strideLength = calcStrideLength(height);
        float stepsPerMile = (float) (5280.0/strideLength);

        return steps/stepsPerMile;
    }

    private float calcStrideLength(long height){
        return (float)((height*0.413)/12.0);
    }

    public String calcTime(long startTime, long endTime){
        int minutes = (int)(endTime-startTime)/60000;
        int seconds = (int)(((endTime-startTime)%60000)/1000);
        if(seconds < 10){
            return minutes+":0"+seconds;
        }
        return minutes+":"+seconds;
    }
}
