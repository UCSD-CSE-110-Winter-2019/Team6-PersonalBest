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
        //this.saveLocal.setOldSubGoal(0);
        //this.saveLocal.setCurrSubGoal(500);
        this.saveLocal.setTime(0);
        this.saveLocal.setSpeed(0); 
        this.saveLocal.setSteps(0);
    }


    public void showEncouragement() {
            int currSubGoal = saveLocal.getCurrSubGoal();
            if(act.numSteps>=currSubGoal) {
                act.runOnUiThread(() -> {
                    int goalMet = ((int) act.numSteps / 500) * 500;
                    int oldSubGoal = saveLocal.getOldSubGoal();
                    saveLocal.setOldSubGoal(goalMet);
                    saveLocal.setCurrSubGoal(goalMet + 500);
                    int stepsIncreased = goalMet - oldSubGoal;
                    Toast t = Toast.makeText(act, "You've increased your daily steps by over "
                            + stepsIncreased + " steps. Keep up the good work!", Toast.LENGTH_LONG);
                    t.show();
                });
            }
    }
}
