package com.example.personalbest;


import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.widget.Button;
import android.widget.EditText;
import com.example.personalbest.database.MockFirebaseAdapter;
import com.example.personalbest.fitness.FitnessService;
import com.example.personalbest.fitness.FitnessServiceFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowDialog;
import org.robolectric.shadows.ShadowToast;

import java.util.Calendar;
import java.util.Date;


import static junit.framework.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)

public class NoEncouragementWithFriendsTest {

    private static final String TEST_SERVICE = "TEST_SERVICE";
    StepCountActivity activity;
    FitnessService fitnessService;
    SaveLocal saveLocal;
    MockFirebaseAdapter mockFirebaseAdapter;
    private long nextStepCount;
    private Exercise exercise;
    private StepCountActivity.Background testTask;
    private ShadowToast shadowToast;

    @Before
    public void setup() {
        FitnessServiceFactory.put(TEST_SERVICE, new FitnessServiceFactory.BluePrint() {
            @Override
            public FitnessService create(StepCountActivity stepCountActivity) {
                return new NoEncouragementWithFriendsTest.TestFitnessService(stepCountActivity);
            }
        });
        FitnessServiceFactory.put(TEST_SERVICE, new FitnessServiceFactory.BluePrint() {
            @Override
            public FitnessService create(StepCountActivity stepCountActivity) {
                return new NoEncouragementWithFriendsTest.TestFitnessService2(stepCountActivity);
            }
        });


        Intent intent = new Intent(RuntimeEnvironment.application, StepCountActivity.class);
        intent.putExtra(StepCountActivity.FITNESS_SERVICE_KEY, TEST_SERVICE);
        intent.putExtra(StepCountActivity.FIREBASEKEY, "MockFirebase");
        activity = Robolectric.buildActivity(StepCountActivity.class, intent).create().start().resume().get();
        fitnessService = FitnessServiceFactory.create(TEST_SERVICE, activity);
        exercise = new Exercise(activity, fitnessService);
        nextStepCount = 0;

        saveLocal = new SaveLocal(activity);
        saveLocal.setEmail("myemail@ucsd.edu");

        mockFirebaseAdapter = (MockFirebaseAdapter) activity.firebaseAdapter;

        mockFirebaseAdapter.addUser("myname", "myemail@ucsd.edu");
        mockFirebaseAdapter.addUser("friend1", "friend1email@ucsd.edu");
        mockFirebaseAdapter.addUser("friend2", "friend2email@ucsd.edu");
        Button addFriend = activity.findViewById(R.id.addFriend);
        addFriend.performClick();
        activity.getFragmentManager().executePendingTransactions();

        AlertDialog alertDialog = (AlertDialog) ShadowDialog.getLatestDialog();
        mockFirebaseAdapter.db.get("friend1email@ucsd.edu").get("pendingList").add("myemail@ucsd.edu");

        EditText et = alertDialog.findViewById(R.id.emailView2);
        et.setText("friend1email@ucsd.edu");
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
        mockFirebaseAdapter.getFriends("myemail@ucsd.edu");
    }

    @Test
    public void testNoEncouragementWithFriends(){
        Calendar cal = Calendar.getInstance();
        // Wed, February 13, 2019 02:16:40
        Date dummyStartTime = new Date(1550053000000L);
        // Wed, February 14, 2019 20:00:00
        Date dummyEndTime = new Date(1550203200000L);
        cal.setTime(dummyStartTime);
        testTask = activity.new Background(cal);
        nextStepCount =1000;
        activity.onResume(cal);
        nextStepCount = 1500;
        cal.setTime(dummyEndTime);
        activity.onResume(cal);
        String toastText = shadowToast.getTextOfLatestToast();
        assertNull(toastText);
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

    private class TestFitnessService2 implements FitnessService {
        private static final String TAG = "[TestFitnessService]: ";
        private StepCountActivity stepCountActivity;

        public TestFitnessService2(StepCountActivity stepCountActivity) {
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
        public void updateStepCount(Calendar cal) {
            System.out.println(TAG + "updateStepCount");
            stepCountActivity.setStepCount(nextStepCount);
        }

        @Override
        public long getDailyStepCount(Calendar cal) {
            return nextStepCount;
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
