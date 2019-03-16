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
        long numSteps = act.numSteps;
        if(numSteps>=currSubGoal) {
            act.runOnUiThread(() -> {
                int goalMet = ((int) numSteps / 500) * 500;
                System.out.println("GOAL MET: "+goalMet);
                int oldSubGoal = saveLocal.getOldSubGoal();
                System.out.println("OLDSUBGOAL: "+oldSubGoal);
                saveLocal.setOldSubGoal(goalMet);
                saveLocal.setCurrSubGoal((int)act.numSteps+500);
                int stepsIncreased = goalMet-oldSubGoal;
                System.out.println("STEPS INCREASED: "+stepsIncreased);
                Toast t = Toast.makeText(act, "You've increased your daily steps by over "
                        +stepsIncreased+ " steps. Keep up the good work!", Toast.LENGTH_LONG);
                t.show();
            });
        }
    }
}
