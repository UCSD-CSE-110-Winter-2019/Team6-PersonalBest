package com.example.personalbest;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("TAG", "HELLO " + remoteMessage.getData().toString());
        String from = remoteMessage.getFrom();
        String fromArray[] = from.split("/topics/");
        from = fromArray[1];
        Intent intent = new Intent(this, MessageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("DOCUMENT_KEY", from);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String channelId = getString(R.string.channel_id);
        NotificationCompat.Builder builder = new  NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody()).setAutoCancel(true).setContentIntent(pendingIntent);;
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
        String myEmail = GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getEmail()+" sent you a message";
        System.out.println("EMAIL "+myEmail);
        System.out.println("TITLE "+remoteMessage.getNotification().getTitle());
        if((remoteMessage.getNotification().getTitle()).compareTo(myEmail) != 0) {
            manager.notify(0, builder.build());
        }
    }
}
