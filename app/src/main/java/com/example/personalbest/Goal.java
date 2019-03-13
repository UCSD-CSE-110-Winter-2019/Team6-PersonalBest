package com.example.personalbest;

import android.support.annotation.NonNull;

import java.util.Date;

public class Goal
//        implements Comparable
{
    private Date date;
    private long stepGoal;

    public Goal(Date date, long stepGoal){
        this.date=date;
        this.stepGoal=stepGoal;
    }

    public Date getDate() {
        return date;
    }

    public long getStepGoal() {
        return stepGoal;
    }
    public String toString(){
        return "Date: "+date.toString()+" Goal: "+stepGoal;
    }

//    @Override
//    public int compareTo(@NonNull Object o) {
//        if(this.getDate().getTime()>((Goal) o).getDate().getTime()) {
//            return 1;
//        }
//        else if(this.getDate().getTime()==((Goal) o).getDate().getTime()){
//            return 0;
//        }
//        else return -1;
//    }
}
