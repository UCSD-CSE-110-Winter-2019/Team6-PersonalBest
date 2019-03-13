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

        import org.junit.Before;
        import org.junit.Test;
        import org.junit.runner.RunWith;
        import org.robolectric.Robolectric;
        import org.robolectric.RobolectricTestRunner;
        import org.robolectric.RuntimeEnvironment;
        import org.robolectric.shadows.ShadowAlertDialog;
        import org.robolectric.shadows.ShadowView;

        import java.util.Calendar;
        import java.util.Date;
        import java.util.List;

        import static junit.framework.Assert.assertFalse;
        import static junit.framework.Assert.assertNotNull;
        import static org.junit.Assert.assertEquals;
        import static org.junit.Assert.assertNotEquals;
        import static org.junit.Assert.assertTrue;

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

        assertEquals(false, ShadowAlertDialog.getLatestDialog().isShowing());
    }

    // test for goal prompts since goal has been met.
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

        List<Dialog> lDialog = ShadowAlertDialog.getShownDialogs();
        alertDialog = (AlertDialog) lDialog.get(1);
        String s = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).getText().toString();
        assertEquals("Add 500 steps", s );
        assertTrue(alertDialog.isShowing());
    }

    // test for goal prompts since goal has been met.
    @Test
    public void add500StepsTest() {
        metGoal();

        List<Dialog> lDialog = ShadowAlertDialog.getShownDialogs();
        AlertDialog alertDialog = (AlertDialog) lDialog.get(1);
        alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).performClick();
        assertFalse(alertDialog.isShowing());
        assertEquals(5500, saveLocal.getGoal());
    }


    // Testing setting new goal.
    @Test
    public void newGoal() {
        metGoal();

        List<Dialog> lDialog = ShadowAlertDialog.getShownDialogs();
        AlertDialog alertDialog = (AlertDialog) lDialog.get(1);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
        assertFalse(alertDialog.isShowing());
        alertDialog = (AlertDialog) lDialog.get(2);

        String s = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).getText().toString();
        assertEquals("Save New Goal", s );
        assertTrue(alertDialog.isShowing());

        NumberPicker np = alertDialog.findViewById(R.id.goalPicker);

        np.setValue(1+ np.getValue());
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
        assertFalse(alertDialog.isShowing());

        assertEquals(5005, saveLocal.getGoal());
    }

    // Testing cancelling setting a new goal.
    @Test
    public void newGoalCancel() {
        metGoal();

        List<Dialog> lDialog = ShadowAlertDialog.getShownDialogs();
        AlertDialog alertDialog = (AlertDialog) lDialog.get(1);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
        assertFalse(alertDialog.isShowing());
        alertDialog = (AlertDialog) lDialog.get(2);

        String s = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).getText().toString();
        assertEquals("Save New Goal", s );
        assertTrue(alertDialog.isShowing());

        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).performClick();
        assertFalse(alertDialog.isShowing());

        assertEquals(5000, saveLocal.getGoal());
    }


    // Testing cancel button on initial goal achieved prompt.
    @Test
    public void cancelGoal() {
        metGoal();

        List<Dialog> lDialog = ShadowAlertDialog.getShownDialogs();
        AlertDialog alertDialog = (AlertDialog) lDialog.get(1);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).performClick();
        assertFalse(alertDialog.isShowing());
        assertEquals(5000, saveLocal.getGoal());
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