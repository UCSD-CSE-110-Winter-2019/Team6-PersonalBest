package com.example.personalbest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.example.personalbest.database.IFirebase;
import com.example.personalbest.database.MockFirebaseAdapter;
import com.example.personalbest.fitness.FitnessService;
import com.example.personalbest.fitness.FitnessServiceFactory;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowDialog;
import org.robolectric.shadows.ShadowToast;

import java.util.Calendar;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(RobolectricTestRunner.class)
public class MessageTest {
    private static final String FIREBASE_KEY = "FIREBASE_KEY";
    private static final String TEST_SERVICE = "TEST_SERVICE";
    StepCountActivity activity;

    FitnessService fitnessService;
    SaveLocal saveLocal;
    MockFirebaseAdapter mockFirebaseAdapter;
    MockMessageAdapter mockMessageAdapter;

    @Before
    public void setup(){
        FitnessServiceFactory.put(TEST_SERVICE, new FitnessServiceFactory.BluePrint() {
            @Override
            public FitnessService create(StepCountActivity stepCountActivity) {
                return new MessageTest.TestFitnessService(stepCountActivity);
            }
        });

        Intent intent = new Intent(RuntimeEnvironment.application, StepCountActivity.class);
        intent.putExtra(StepCountActivity.FITNESS_SERVICE_KEY, TEST_SERVICE);
        intent.putExtra(StepCountActivity.FIREBASEKEY, "MockFirebase");
        activity = Robolectric.buildActivity(StepCountActivity.class, intent).create().start().resume().get();

        saveLocal=new SaveLocal(activity);
        saveLocal.setEmail("myemail@ucsd.edu");

        mockFirebaseAdapter = (MockFirebaseAdapter) activity.firebaseAdapter;

        mockFirebaseAdapter.addUser("myname", "myemail@ucsd.edu");
        mockFirebaseAdapter.addUser("friend1", "friend1email@ucsd.edu");

        mockMessageAdapter = new MockMessageAdapter();
    }

    @Test
    public void messageFriendTest() {
        // Given Ani has Orr added as a friend
        mockFirebaseAdapter.db.get("friend1email@ucsd.edu").get("pendingList").add("myemail@ucsd.edu");
        addFriendHelper();

        // When Ani long presses on Orr’s name in his friends list
        // Then a popup shows up for messages, unfriend, stats
        // When Ani selects messages
        // Then Ani is on the message with Orr Screen
        // Then a small text box appears at the bottom of Orr’s message screen
        Intent intent = new Intent(RuntimeEnvironment.application, MessageActivity.class);
        MessageActivity act = Robolectric.buildActivity(MessageActivity.class, intent).create().start().resume().get();

        act.chat = mockMessageAdapter;
        act.TEST = true;

        Button sendBtn = act.findViewById(R.id.btn_send);
        TextView textView = act.findViewById(R.id.text_message);

        // When Ani types “Run Forrest run!” to the text box
        textView.setText("Run Forrest run!");
        // And Ani presses send
        sendBtn.performClick();
        // Then Ani sees the message he just typed
        assertEquals("Run Forrest run!", mockMessageAdapter.getMessages());
    }

    @Test
    public void messageFromGraph() {
        mockFirebaseAdapter.db.get("friend1email@ucsd.edu").get("pendingList").add("myemail@ucsd.edu");
        addFriendHelper();

        Intent intent = new Intent(RuntimeEnvironment.application, MonthGraph.class);
        MonthGraph act = Robolectric.buildActivity(MonthGraph.class, intent).create().start().resume().get();

        TextView text = act.findViewById(R.id.messageGraph);
        text.setText("hi");

        act.TEST = true;

        act.sendMessage(mockMessageAdapter);

        assertEquals("hi", mockMessageAdapter.getMessages());
    }

    private void addFriendHelper() {
        Button addFriend = activity.findViewById(R.id.addFriend);
        addFriend.performClick();
        activity.getFragmentManager().executePendingTransactions();

        AlertDialog alertDialog = (AlertDialog) ShadowDialog.getLatestDialog();

        EditText et = alertDialog.findViewById(R.id.emailView2);
        et.setText("friend1email@ucsd.edu");
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
        activity.firebaseAdapter.getFriends("myemail@ucsd.edu");
    }

    private class TestFitnessService implements FitnessService {
        private static final String TAG = "[TestFitnessService]: ";
        private StepCountActivity stepCountActivity;

        public TestFitnessService(StepCountActivity stepCountActivity) {
            this.stepCountActivity = stepCountActivity;
        }

        @Override
        public int getRequestCode() {
            return 0;
        }

        @Override
        public void setup() {
            System.out.println(TAG + "setup");
        }

        @Override
        public void updateStepCount(Calendar calendar) {
            stepCountActivity.setStepCount(0);//nextStepCount);
        }

        @Override
        public long getDailyStepCount(Calendar calendar) {
            return 0;//nextStepCount;
        }

        @Override
        public void updateBackgroundCount(Calendar currentTime, int daysBefore) {

        }

        @Override
        public boolean isSetupComplete() {
            return true;
        }

        @Override
        public boolean startRecording() {
            return true;
        }

        @Override
        public String getEmail() {
            return null;
        }
    }
}
