package com.example.personalbest;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.personalbest.fitness.GoogleFitAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class NotifyService extends Service {
    Timer timer;
    TimerTask timerTask;
    String TAG = "Timers";
    int Your_X_SECS = 5;
    Intent intent;

    final class MyThread implements Runnable {
        int startid;
        public MyThread(int startid) {
            this.startid = startid;
        }

        @Override
        public void run() {
            synchronized (this) {
                try {
                    initializeTimerTask();
                    wait(5000);
                    startTimer();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //stopSelf(startid);
            }

        }
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        this.intent = intent;

        Thread thread = new Thread(new MyThread(startId));
        thread.start();



        return START_STICKY;
    }


    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        stoptimertask();
        super.onDestroy();
    }

    //we are going to use a handler to be able to run in our TimerTask
    final Handler handler = new Handler();


    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        //initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 5000, Your_X_SECS * 1000); //
        //timer.schedule(timerTask, 5000,1000); //
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {
        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("exercise", Context.MODE_PRIVATE);


        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {

                        Intent intent = new Intent(context, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

                        NotificationCompat.Builder var = makeNotification(pendingIntent);

                        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(context);

                        if (lastSignedInAccount == null)
                            return;

                        Calendar cal = Calendar.getInstance();
                        //Find the end of the day
                        cal.setTimeInMillis(Calendar.getInstance().getTimeInMillis());
                        cal.set(Calendar.HOUR_OF_DAY, 23);
                        cal.set(Calendar.MINUTE, 59);
                        cal.set(Calendar.SECOND, 59);
                        cal.set(Calendar.MILLISECOND, 59);
                        long endTime = cal.getTimeInMillis();
                        //Find the start of the day
                        cal.set(Calendar.HOUR_OF_DAY, 0);
                        cal.set(Calendar.MINUTE, 0);
                        cal.set(Calendar.SECOND, 0);
                        cal.set(Calendar.MILLISECOND, 0);
                        long startTime = cal.getTimeInMillis();

                        DataReadRequest readRequest =
                                new DataReadRequest.Builder()
                                        .read(DataType.TYPE_STEP_COUNT_DELTA)
                                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                                        .build();

                        Fitness.getHistoryClient(context, lastSignedInAccount)
                                .readData(readRequest)
                                .addOnSuccessListener(
                                        new OnSuccessListener<DataReadResponse>() {
                                            @Override
                                            public void onSuccess(DataReadResponse dataReadResponse) {
                                                int steps = GoogleFitAdapter.getSteps(dataReadResponse);
                                                int goal = sharedPreferences.getInt("goal", -1);
                                                boolean bool = sharedPreferences.getBoolean("goalNotification", false);
                                                Log.i("Notification", "Checking if user has met goal");
                                                if (steps > goal && !bool) {
                                                    Log.i("Notification", "Notified User that goal achieved");
                                                    sendNotification(var);
                                                }
                                            }
                                        });

                    }
                });

            }

            private NotificationCompat.Builder makeNotification(PendingIntent pendingIntent) {
                return new NotificationCompat.Builder(context, getString(R.string.channel_id))
                        .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                        .setContentTitle("Goal has been Achieved")
                        .setContentText("Please tap to enter your new goal")
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            }

            private void sendNotification(NotificationCompat.Builder var) {
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                notificationManagerCompat.notify(3, var.build());
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putBoolean("goalNotification", true);
                edit.commit();
            }

        };

    }

}
