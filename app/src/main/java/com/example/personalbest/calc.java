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

    private double calcDistance(int steps){
        int height = inches + (feet*12);
        double strideLength = calcStrideLength(height);
        double stepsPerMile = 5280/strideLength;

        return steps/stepsPerMile;
    }

    private double calcStrideLength(long height){
        return (height*0.413)/12;
    }
}
