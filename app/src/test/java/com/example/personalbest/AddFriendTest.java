package com.example.personalbest;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


import com.example.personalbest.database.IFirebase;
import com.example.personalbest.database.MockFirebaseAdapter;
import com.example.personalbest.fitness.FitnessService;
import com.example.personalbest.fitness.FitnessServiceFactory;

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

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(RobolectricTestRunner.class)

public class AddFriendTest {
    private static final String FIREBASE_KEY = "FIREBASE_KEY";
    private static final String TEST_SERVICE = "TEST_SERVICE";
    StepCountActivity activity;
    FitnessService fitnessService;
    EndDay testEndDay;
    SaveLocal saveLocal;
    MockFirebaseAdapter mockFirebaseAdapter;

    @Before
    public void setup(){
        FitnessServiceFactory.put(TEST_SERVICE, new FitnessServiceFactory.BluePrint() {
            @Override
            public FitnessService create(StepCountActivity stepCountActivity) {
                return new AddFriendTest.TestFitnessService(stepCountActivity);
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
        mockFirebaseAdapter.addUser("friend2", "friend2email@ucsd.edu");


    }

    @Test
    public void addPendingFriendTest(){

        Button addFriend = activity.findViewById(R.id.addFriend);
        addFriend.performClick();
        activity.getFragmentManager().executePendingTransactions();

        AlertDialog alertDialog = (AlertDialog) ShadowDialog.getLatestDialog();;

        EditText et = alertDialog.findViewById(R.id.emailView2);
        et.setText("friend1email@ucsd.edu");
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
        activity.firebaseAdapter.getFriends("myemail@ucsd.edu");

        assertFalse(saveLocal.getFriends().contains("friend1email@ucsd.edu"));
        assertTrue(mockFirebaseAdapter.db.get("myemail@ucsd.edu").get("pendingList").contains("friend1email@ucsd.edu"));
    }

    @Test
    public void addFriendToFriendsListTest(){
        mockFirebaseAdapter.db.get("friend1email@ucsd.edu").get("pendingList").add("myemail@ucsd.edu");

        addFriendHelper();
    }

    private void addFriendHelper() {
        Button addFriend = activity.findViewById(R.id.addFriend);
        addFriend.performClick();
        activity.getFragmentManager().executePendingTransactions();

        AlertDialog alertDialog = (AlertDialog) ShadowDialog.getLatestDialog();


        EditText et = alertDialog.findViewById(R.id.emailView2);
        et.setText("friend1email@ucsd.edu");
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
        //alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
        activity.firebaseAdapter.getFriends("myemail@ucsd.edu");

        assertFalse(mockFirebaseAdapter.db.get("myemail@ucsd.edu").get("pendingList").contains("friend1email@ucsd.edu"));
        assertFalse(mockFirebaseAdapter.db.get("friend1email@ucsd.edu").get("pendingList").contains("myemail@ucsd.edu"));
        assertTrue(saveLocal.getFriends().contains("friend1email@ucsd.edu"));
    }

    @Test
    public void addExistingFriend(){
        addFriendToFriendsListTest();
        addFriendHelper();
    }

    @Test
    public void seeFriendsList() {
        addFriendToFriendsListTest();

        Button b = activity.findViewById(R.id.friendButton);
        b.performClick();
        Intent intent = new Intent(RuntimeEnvironment.application, FriendsListActivity.class);

        FriendsListActivity a = Robolectric.buildActivity(FriendsListActivity.class, intent).create().start().resume().get();
        ListView friends = a.findViewById(R.id.listView);

        assertEquals("friend1email@ucsd.edu", friends.getAdapter().getItem(0));
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
