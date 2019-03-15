package com.example.personalbest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.example.personalbest.ChatMessageService;
import com.example.personalbest.ChatMessageServiceFactory;
import com.example.personalbest.FirebaseFirestoreAdapter;
import com.example.personalbest.FirebaseCloudMessagingAdapter;
import com.example.personalbest.NotificationService;
import com.example.personalbest.NotificationServiceFactory;
import com.google.firebase.FirebaseApp;

import android.support.v7.app.AppCompatActivity;

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

        Button returnButton = findViewById(R.id.returnBtn);
        returnButton.setOnClickListener(view -> this.finish());

        TextView nameView = findViewById((R.id.user_name));
        nameView.setText(saveLocal.getEmail());
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

}