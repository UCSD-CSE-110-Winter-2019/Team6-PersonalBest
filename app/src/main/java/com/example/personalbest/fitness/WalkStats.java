package com.example.personalbest.fitness;

import android.widget.TextView;

import com.example.personalbest.Calc;
import com.example.personalbest.SaveLocal;
import com.example.personalbest.StepCountActivity;
import com.example.personalbest.R;

import org.w3c.dom.Text;

public class WalkStats {
    SaveLocal save;
    StepCountActivity act;
    Long startTime;
    long steps;
    Calc calc;


    public WalkStats(StepCountActivity activity){
        this.act = activity;
        this.save=new SaveLocal(activity);
    }

    public void update(){
        startTime = save.getLastSessionStartTime();
        steps = save.getStartSessionStepCount();
        long distance = calc.calcDistance(steps);
        Long speed = calc.calcSpeed(startTime, distance);
        save.setSpeed(speed);
        TextView speedText = act.findViewById(R.id.textSpeed);
        speedText.setText("MPH: "+speed);
        TextView timeText = act.findViewById(R.id.walkTime);
        String time = calc.calcTime(startTime);
        timeText.setText("Time Elapsed: "+time);
        TextView walkSteps = act.findViewById(R.id.walkSteps);
        walkSteps.setText("Steps: "+(act.numSteps-steps));

    }


}
