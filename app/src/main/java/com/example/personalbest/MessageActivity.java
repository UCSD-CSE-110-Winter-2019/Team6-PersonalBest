package com.example.personalbest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {
    public static final String SHARED_PREFERENCES_NAME = "FirebaseLabApp";
    public static final String CHAT_MESSAGE_SERVICE_EXTRA = "CHAT_MESSAGE_SERVICE";
    public static final String NOTIFICATION_SERVICE_EXTRA = "NOTIFICATION_SERVICE";

    String TAG = MainActivity.class.getSimpleName();

    String DOCUMENT_KEY;
    String FROM_KEY = "from";
    String TEXT_KEY = "text";
    String TIMESTAMP_KEY = "timestamp";

    ChatMessageService chat;
    SaveLocal saveLocal;
    String friendEmail;
    String myEmail;
    String lastSender;
    String lastMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_message);
        SharedPreferences sharedpreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        this.saveLocal=new SaveLocal(this);
        friendEmail=saveLocal.getLastCLickedFriend();
        myEmail=saveLocal.getEmail();
        if(myEmail.compareTo(friendEmail)>0){
            DOCUMENT_KEY=myEmail+friendEmail;
        }else{
            DOCUMENT_KEY=friendEmail+myEmail;
        }
        String NEW_KEY="";
        String array1[] = DOCUMENT_KEY.split("@");
        for(String s : array1){
            NEW_KEY += s;
        }
        DOCUMENT_KEY = NEW_KEY;

        chat = FirebaseFirestoreAdapter.getInstance(DOCUMENT_KEY);

        String intentExtra = getIntent().getStringExtra("DOCUMENT_KEY");
        if(intentExtra != null){
            DOCUMENT_KEY = intentExtra;
        }

        initMessageUpdateListener();

        findViewById(R.id.btn_send).setOnClickListener(view -> sendMessage());

        TextView nameView = findViewById((R.id.user_name));

        nameView.setText(saveLocal.getEmail());


        //DELETE
        ArrayList<Goal> goals=saveLocal.getNewGoals(friendEmail);




    }

    private void sendMessage() {
        if (myEmail == null || myEmail.isEmpty()) {
            Toast.makeText(this, "Enter your name", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText messageView = findViewById(R.id.text_message);

        Map<String, String> newMessage = new HashMap<>();
        newMessage.put(FROM_KEY, myEmail);
        newMessage.put(TIMESTAMP_KEY, String.valueOf(new Date().getTime()));
        newMessage.put(TEXT_KEY, messageView.getText().toString());

        chat.addMessage(newMessage).addOnSuccessListener(result -> {
            messageView.setText("");
        }).addOnFailureListener(error -> {
            Log.e(TAG, error.getLocalizedMessage());
        });
    }

    private void initMessageUpdateListener() {
        TextView chatView = findViewById(R.id.chat);
        chat.addOrderedMessagesListener(
                chatMessagesList -> {
                    Log.d(TAG, "msg list size:" + chatMessagesList.size());
                    chatMessagesList.forEach(chatMessage -> {
                        if (chatMessage != null) {
                            chatView.append(chatMessage.toString());
                            setSenderAndMsg(chatMessage.getFrom(), chatMessage.getText());
                        }
                    });
                    /*createNotificationChannel();
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.channel_id))
                            .setContentTitle(lastSender)
                            .setContentText(lastMsg)
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
                    notificationManagerCompat.notify(3, builder.build());*/
                });
    }


    private void setSenderAndMsg(String newString, String newMsg){
        lastSender = newString+" sent you a message";
        lastMsg = newMsg;
    }

    private void subscribeToNotificationsTopic() {
        NotificationServiceFactory notificationServiceFactory = NotificationServiceFactory.getInstance();
        String notificationServiceKey = getIntent().getStringExtra(NOTIFICATION_SERVICE_EXTRA);
        NotificationService notificationService = notificationServiceFactory.getOrDefault(notificationServiceKey, FirebaseCloudMessagingAdapter::getInstance);

        getIntent().putExtra("DOCUMENT_KEY", DOCUMENT_KEY);

        notificationService.subscribeToNotificationsTopic(DOCUMENT_KEY, task -> {
            String msg = "Subscribed to notifications";
            if (!task.isSuccessful()) {
                msg = "Subscribe to notifications failed";
            }
            Log.d(TAG, msg);
        });
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.channel_id), name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}