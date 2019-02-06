package com.example.personalbest;

public class calc {
    private int inches;
    private int feet;

    public calc(int in, int ft){
        inches = in;
        feet = ft;
    }

    private long calcSpeed(long time, long distance){
        return distance/time;
    }

    private long calcDistance(int steps){
        int height = inches + (feet*12);
        long strideLength = calcStrideLength(height);
        long stepsPerMile = 5280/strideLength;

        return steps/stepsPerMile;
    }

    private long calcStrideLength(long height){
        return (long)(height*0.413)/12;
    }
}
