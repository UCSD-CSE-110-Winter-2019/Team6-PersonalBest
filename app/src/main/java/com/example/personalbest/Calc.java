package com.example.personalbest;

import java.util.Calendar;

public class Calc {
    private int inches;
    private int feet;

    public Calc(int in, int ft){
        inches = in;
        feet = ft;
    }

    public long calcSpeed(long time, long distance){
        Calendar c = Calendar.getInstance();
        long current = c.getTimeInMillis();
        long totalTime = current-time;
        long minutes = totalTime/60000;
        long hours = minutes/60;
        return distance/hours;
    }

    public long calcDistance(long steps){
        int height = inches + (feet*12);
        long strideLength = calcStrideLength(height);
        long stepsPerMile = 5280/strideLength;

        return steps/stepsPerMile;
    }

    private long calcStrideLength(long height){
        return (long)(height*0.413)/12;
    }

    public String calcTime(long StartTime){
        Calendar c = Calendar.getInstance();
        long currentTime = c.getTimeInMillis();
        int minutes = (int)(currentTime-StartTime)/60000;
        int seconds = (int)(currentTime-StartTime)%60000;
        return minutes+":"+seconds;
    }
}
