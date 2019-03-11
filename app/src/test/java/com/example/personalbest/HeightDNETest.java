package com.example.personalbest;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.personalbest.fitness.FitnessService;
import com.example.personalbest.fitness.FitnessServiceFactory;

import org.bouncycastle.asn1.crmf.POPOSigningKey;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.shadows.ShadowView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)

public class HeightDNETest {
    private static final String TEST_SERVICE = "TEST_SERVICE";
    private StepCountActivity activity;
    private Exercise exercise;
    private FitnessService fitnessService;
    private TextView textSteps;
    private TextView textTime;
    private TextView textSpeed;
    private Button walkBtn;
    private long nextStepCount;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    SaveLocal saveLocal;

    //@Rule
    //public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setup() throws Exception {
        FitnessServiceFactory.put(TEST_SERVICE, new FitnessServiceFactory.BluePrint() {
            @Override
            public FitnessService create(StepCountActivity stepCountActivity) {
                return new TestFitnessService(stepCountActivity);
            }
        });

        Intent intent = new Intent(RuntimeEnvironment.application, StepCountActivity.class);
        intent.putExtra(StepCountActivity.FITNESS_SERVICE_KEY, TEST_SERVICE);
        activity = Robolectric.buildActivity(StepCountActivity.class, intent).create().get();
        fitnessService = FitnessServiceFactory.create(TEST_SERVICE, activity);
        exercise = new Exercise(activity, fitnessService);

        saveLocal = new SaveLocal(activity);
        saveLocal.setGoal(5000);
        saveLocal.setAchieved(false);

        textSteps = activity.findViewById(R.id.textSteps);
        nextStepCount = 4500;

        activity.onResume();



        //dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
    }

    //Testing app opens without height data, prompts for height
    @Test
    public void heightDNE(){
        AlertDialog alertDialog = (AlertDialog) ShadowAlertDialog.getLatestDialog();
        assertNotNull(alertDialog);
        assertEquals("Accept", alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).getText().toString());
        assertTrue(alertDialog.isShowing());
        NumberPicker np = alertDialog.findViewById(R.id.numberPicker);
        np.setValue(5);
        np = alertDialog.findViewById(R.id.numberPicker2);
        np.setValue(6);

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();

        assertFalse(alertDialog.isShowing());
        assertEquals(5, saveLocal.getHeightFeet());
        assertEquals(6, saveLocal.getHeightInches());

        String toastText = ShadowToast.getTextOfLatestToast();
        assertEquals("Saved Height", toastText);

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
        public void updateStepCount(Calendar cal) {
            System.out.println(TAG + "updateStepCount");
            stepCountActivity.setStepCount(nextStepCount);
        }

        @Override
        public long getDailyStepCount(Calendar cal) {
            return nextStepCount;
        }

        @Override
        public boolean startRecording() {
            return true;
        }

        @Override
        public String getEmail() {
            return null;
        }

        @Override
        public void updateBackgroundCount(Calendar currentTime, int daysBefore){

        }

        @Override
        public boolean isSetupComplete() {
            return true;
        }

    }
}