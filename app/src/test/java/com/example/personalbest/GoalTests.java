package com.example.personalbest;

        import android.app.Dialog;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.os.AsyncTask;
        import android.support.v4.app.DialogFragment;
        import android.support.v7.app.AlertDialog;
        import android.widget.Button;
        import android.widget.TextView;

        import com.example.personalbest.fitness.Encouragement;
        import com.example.personalbest.fitness.FitnessService;
        import com.example.personalbest.fitness.FitnessServiceFactory;
        import com.example.personalbest.fitness.GoogleFitAdapter;
        import com.example.personalbest.fitness.WalkStats;

        import org.hamcrest.Condition;
        import org.junit.Before;
        import org.junit.Rule;
        import org.junit.Test;
        import org.junit.runner.RunWith;
        import org.robolectric.Robolectric;
        import org.robolectric.RobolectricTestRunner;
        import org.robolectric.RuntimeEnvironment;
        import org.robolectric.shadows.ShadowAlertDialog;
        import org.robolectric.shadows.ShadowView;

        import java.time.LocalDateTime;
        import java.util.Calendar;
        import java.util.Date;
        import java.util.List;

        import static org.junit.Assert.assertEquals;
        import static org.junit.Assert.assertNotEquals;

@RunWith(RobolectricTestRunner.class)

public class GoalTests {
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

        SaveLocal saveLocal = new SaveLocal(activity);
        saveLocal.setGoal(5000);
        saveLocal.setAchieved(false);

        textSteps = activity.findViewById(R.id.textSteps);
        nextStepCount = 4500;

        activity.onResume();

        AlertDialog dialog = (AlertDialog) ShadowAlertDialog.getLatestDialog();
        ShadowView.clickOn(dialog.getButton(AlertDialog.BUTTON_POSITIVE));
        //dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
    }

    // test for no goal prompts since goal has not been met.
    @Test
    public void notMetGoalTest() {
        Calendar cal = Calendar.getInstance();
        // Wed, February 13, 2019 02:16:40
        Date dummyStartTime = new Date(1550053000000L);

        cal.setTime(dummyStartTime);
        activity.onResume(cal);

     //AlertDialog alertDialog = (AlertDialog) ShadowAlertDialog.getLatestDialog();
     //Dialog dialog1 = ShadowAlertDialog.getShownDialogs().get(1);
     //assertEquals(null, alertDialog);
    }

    // test for no goal prompts since goal has not been met.
    @Test
    public void metGoal() {
        Calendar cal = Calendar.getInstance();
        // Wed, February 13, 2019 02:16:40
        Date dummyStartTime = new Date(1550053000000L);

        cal.setTime(dummyStartTime);
        activity.onResume(cal);

        android.support.v7.app.AlertDialog alertDialog = (AlertDialog) ShadowAlertDialog.getLatestDialog();
        assertEquals(null, ShadowAlertDialog.getLatestAlertDialog());

        nextStepCount = 5500;
        fitnessService.getDailyStepCount(cal);
        activity.onResume();

        Dialog dialog1 = ShadowAlertDialog.getShownDialogs().get(1);
        alertDialog = (AlertDialog) dialog1;
        assertNotEquals(null, alertDialog);
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
        public void updateBackgroundCount(Calendar currentTime, int daysBefore){

        }

        @Override
        public boolean isSetupComplete() {
            return true;
        }

    }
}