package com.example.personalbest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
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
    String from;
    SaveLocal saveLocal;
    String friendEmail;
    String myEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_message);
        SharedPreferences sharedpreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        this.saveLocal=new SaveLocal(this);
        from = saveLocal.getEmail();
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
        String stringExtra = getIntent().getStringExtra(CHAT_MESSAGE_SERVICE_EXTRA);
        //chat = ChatMessageServiceFactory.getInstance().getOrDefault(stringExtra, FirebaseFirestoreAdapter::getInstance);
        chat = FirebaseFirestoreAdapter.getInstance(DOCUMENT_KEY);
        initMessageUpdateListener();

        findViewById(R.id.btn_send).setOnClickListener(view -> sendMessage());
        subscribeToNotificationsTopic();

        Button returnButton = findViewById(R.id.returnBtn);
        returnButton.setOnClickListener(view -> this.finish());

        TextView nameView = findViewById((R.id.user_name));
        nameView.setText(saveLocal.getEmail());


        //DELETE
        ArrayList<Goal> goals=saveLocal.getNewGoals(friendEmail);

        for(Goal o:goals){
            Log.d("A",o.toString());
        }
        Log.d("BackgroundStep:",""+saveLocal.getAccountBackgroundStep(friendEmail, Calendar.getInstance()));
    }

    private void sendMessage() {
        if (from == null || from.isEmpty()) {
            Toast.makeText(this, "Enter your name", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText messageView = findViewById(R.id.text_message);

        Map<String, String> newMessage = new HashMap<>();
        newMessage.put(FROM_KEY, from);
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
                        if (chatMessage != null)
                            chatView.append(chatMessage.toString());
                    });
                });
    }

    private void subscribeToNotificationsTopic() {
        NotificationServiceFactory notificationServiceFactory = NotificationServiceFactory.getInstance();
        String notificationServiceKey = getIntent().getStringExtra(NOTIFICATION_SERVICE_EXTRA);
        NotificationService notificationService = notificationServiceFactory.getOrDefault(notificationServiceKey, FirebaseCloudMessagingAdapter::getInstance);

        notificationService.subscribeToNotificationsTopic(DOCUMENT_KEY, task -> {
            String msg = "Subscribed to notifications";
            if (!task.isSuccessful()) {
                msg = "Subscribe to notifications failed";
            }
            Log.d(TAG, msg);
        });
    }
}