package com.example.personalbest.fitness;

import android.widget.Toast;

import com.example.personalbest.SaveLocal;
import com.example.personalbest.StepCountActivity;

public class Encouragement {
    SaveLocal saveLocal;
    StepCountActivity act;


    public Encouragement(StepCountActivity activity){
        this.act = activity;
        this.saveLocal = new SaveLocal(activity);
        this.saveLocal.setOldSubGoal(0);
        this.saveLocal.setCurrSubGoal(500);
        this.saveLocal.setTime(0);
        this.saveLocal.setSpeed(0); 
        this.saveLocal.setSteps(0);
    }


    public void showEncouragement() {

        if(act.numSteps>saveLocal.getCurrSubGoal()) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int newSubGoal = ((int) act.numSteps / 500) * 500;
                    saveLocal.setOldSubGoal(newSubGoal);
                    saveLocal.setCurrSubGoal(saveLocal.getOldSubGoal()+500);
                    Toast t = Toast.makeText(act, "You've increased your daily steps by over "+(newSubGoal-saveLocal.getOldSubGoal())+ " steps. Keep up the good work!", Toast.LENGTH_LONG);
                    t.show();
                }
            });
        }
    }
}
