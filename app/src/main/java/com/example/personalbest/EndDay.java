package com.example.personalbest;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.Calendar;

public class EndDay {

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private StepCountActivity activity;

    public EndDay(StepCountActivity activity, int hourOfDay, int minuteOfDay, int secondOfDay){
        alarmManager = (AlarmManager)activity.getSystemService(Context.ALARM_SERVICE);

        IntentFilter intentFilter = new IntentFilter("com.example.personalbest.EndDay.Receiver");
        Receiver receiver = new Receiver();
        activity.registerReceiver(receiver, intentFilter);
        this.activity = activity;

        Intent intent = new Intent(activity, Receiver.class);
        pendingIntent = PendingIntent.getBroadcast(activity, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minuteOfDay);
        calendar.set(Calendar.SECOND, secondOfDay);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            SaveLocal saveLocal=new SaveLocal(activity);
            saveLocal.newDayShift();
        }
    }
}
