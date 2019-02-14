package com.example.personalbest.fitness;

import android.widget.TextView;

import com.example.personalbest.Calc;
import com.example.personalbest.SaveLocal;
import com.example.personalbest.StepCountActivity;
import com.example.personalbest.R;

import org.w3c.dom.Text;

import java.util.Calendar;

public class WalkStats {
    SaveLocal save;
    StepCountActivity act;
    Long startTime;
    long steps;
    Calc calc;


    public WalkStats(StepCountActivity activity){
        this.act = activity;
        this.save=new SaveLocal(activity);
        this.calc = new Calc(save.getHeightInches(),save.getHeightFeet());
    }

    public void update(){
        startTime = save.getLastSessionStartTime();
        steps = save.getStartSessionStepCount();
        float distance = calc.calcDistance(act.numSteps-steps);
        float speed = calc.calcSpeed(startTime, distance);
        save.setSpeed(speed);
        Calendar c = Calendar.getInstance();
        long currentTime = c.getTimeInMillis();
        TextView speedText = act.findViewById(R.id.textSpeed);
        speedText.setText("MPH: "+speed);
        TextView timeText = act.findViewById(R.id.walkTime);
        String time = calc.calcTime(startTime, currentTime);
        timeText.setText("Time Elapsed: "+time);
        TextView walkSteps = act.findViewById(R.id.walkSteps);
        walkSteps.setText("Steps: "+(act.numSteps-steps));

    }


}
