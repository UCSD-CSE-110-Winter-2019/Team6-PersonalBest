package com.example.personalbest;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.personalbest.database.FirebaseAdapter;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MonthGraph extends AppCompatActivity {

    private String email;
    private SaveLocal saveLocal;
    private FirebaseAdapter firebaseAdapter;
    private CombinedChart combinedChart;
    private CombinedData combinedData;
    private TextView waitText;
    private EditText message;
    private ChatMessageService chat;
    private String DOCUMENT_KEY;
    private String FROM_KEY = "from";
    private String TEXT_KEY = "text";
    private String TIMESTAMP_KEY = "timestamp";
    private int[] workout;
    private int[] background;
    private Goal[] goals;

    private final String[] labels = {"Exercise Steps", "Background Steps"};
    private final int[] colors = {0xff0000ff, 0xff5B2C6F};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_graph);

        saveLocal = new SaveLocal(this);
        email = getIntent().getStringExtra("email");
        String myEmail=saveLocal.getEmail();
        if(myEmail.compareTo(email)>0){
            DOCUMENT_KEY=myEmail + email;
        }else{
            DOCUMENT_KEY=email+myEmail;
        }
        String NEW_KEY="";
        String array1[] = DOCUMENT_KEY.split("@");
        for(String s : array1){
            NEW_KEY += s;
        }
        DOCUMENT_KEY = NEW_KEY;
        chat = FirebaseFirestoreAdapter.getInstance(DOCUMENT_KEY);

        combinedChart = findViewById(R.id.monthChart);
        combinedChart.setVisibility(View.GONE);
        combinedChart.getDescription().setEnabled(false);
        combinedChart.invalidate();
        combinedData = new CombinedData();

        waitText = findViewById(R.id.waitText);
        waitText.setText("Obtaining Information from Firebase");


        if(email.equals(saveLocal.getEmail())){
            View btn = findViewById(R.id.sendButton);
            btn.setVisibility(View.GONE);
            View msg = findViewById(R.id.messageGraph);
            msg.setVisibility(View.GONE);
        }

        firebaseAdapter = new FirebaseAdapter(this);
        Task<QuerySnapshot> task = firebaseAdapter.saveFriendStepLocal(email);
        task.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                queryDocumentSnapshots.forEach(doc -> {
                    Map<String, Object> map = doc.getData();
                    long exercise = (long) map.get("Exercise");
                    long background = (long) map.get("Background");
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                    try {
                        calendar.setTime(sdf.parse(doc.getId()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    saveLocal.setAccountBackgroundStep(email, (int) background, calendar);
                    saveLocal.setAccountExerciseStep(email, (int) exercise, calendar);

                    waitText.setVisibility(View.GONE);
                    combinedChart.setVisibility(View.VISIBLE);

                    int[] backgroundSteps = getBackgroundSteps(email, saveLocal);
                    int[] workoutSteps = getWorkoutSteps(email, saveLocal);

                    addBar(workoutSteps, backgroundSteps);
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Query Results", "Failed");
                    }
                });
        Task<DocumentSnapshot> doc = firebaseAdapter.saveNewGoalsLocal(email);
        doc.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                waitText.setVisibility(View.GONE);
                combinedChart.setVisibility(View.VISIBLE);



                int[] goals = getGoals(email, saveLocal);
                addLine(goals);


                //buildGraph(combinedChart, workoutSteps, backgroundSteps, goals);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                waitText.setText("Failed to obtain firebase information");
            }
        });


    }

    public void addBar(int[] workout, int[] background){
        List<BarEntry> barEntries = new ArrayList<>();

        for(int i = 0; i < workout.length; i++){
            barEntries.add(new BarEntry(i, new float[] {workout[i], background[i]}));
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "");
        barDataSet.setStackLabels(labels);
        barDataSet.setLabel("");
        barDataSet.setValueTextSize(10f);
        barDataSet.setColors(colors);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.5f);

        combinedData.setData(barData);

        combinedChart.setData(combinedData);
        combinedChart.getXAxis().setDrawLabels(false);
        combinedChart.setVisibleXRangeMaximum(14);
        combinedChart.moveViewToX(14);

        XAxis xAxis = combinedChart.getXAxis();
        xAxis.setSpaceMin(barData.getBarWidth() / 2f);
        xAxis.setSpaceMax(barData.getBarWidth() / 2f);

        combinedChart.notifyDataSetChanged();
        combinedChart.invalidate();
    }

    public void addLine(int[] goals){
        List<Entry> lineEntries = new ArrayList<>();

        for(int i = 0; i < goals.length; i++){
            lineEntries.add(new Entry(i, goals[i]));
        }

        LineDataSet lineDataSet = new LineDataSet(lineEntries, "");
        lineDataSet.setLineWidth(3f);
        lineDataSet.setColor(0xffff0000);
        lineDataSet.setCircleColor(0xffff0000);
        lineDataSet.setLabel("Goal");
        lineDataSet.setDrawValues(false);

        LineData lineData = new LineData(lineDataSet);

        combinedChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR,  CombinedChart.DrawOrder.LINE
        });


        combinedData.setData(lineData);

        combinedChart.setData(combinedData);
        combinedChart.getXAxis().setDrawLabels(false);
        combinedChart.setVisibleXRangeMaximum(14);
        combinedChart.moveViewToX(14);

        combinedChart.notifyDataSetChanged();
        combinedChart.invalidate();
    }

    public void exitGraph(View view){
        this.finish();
    }

    public void sendMessage(View view){

        EditText messageView = findViewById(R.id.messageGraph);

        Map<String, String> newMessage = new HashMap<>();
        newMessage.put(FROM_KEY, email);
        newMessage.put(TIMESTAMP_KEY, String.valueOf(new Date().getTime()));
        newMessage.put(TEXT_KEY, messageView.getText().toString());

        chat.addMessage(newMessage).addOnSuccessListener(result -> {
            messageView.setText("");
        }).addOnFailureListener(error -> {
            Log.e("Message", error.getLocalizedMessage());
        });
    }

    public int[] getBackgroundSteps(String email, SaveLocal saveLocal){
        int[] steps = new int[28];
        Calendar calendar = Calendar.getInstance();
        steps[27] = saveLocal.getAccountBackgroundStep(email, calendar);
        for(int i = 26; i >= 0; i--){
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            steps[i] = saveLocal.getAccountBackgroundStep(email, calendar);
        }
        return steps;
    }

    public int[] getWorkoutSteps(String email, SaveLocal saveLocal){
        int[] steps = new int[28];
        Calendar calendar = Calendar.getInstance();
        steps[27] = saveLocal.getAccountExerciseStep(email, calendar);
        for(int i = 26; i >= 0; i--){
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            steps[i] = saveLocal.getAccountExerciseStep(email, calendar);
        }
        return steps;
    }

    public int[] getGoals(String email, SaveLocal saveLocal){
        int[] goals = new int[28];
        ArrayList<Goal> arrayListGoals = saveLocal.getNewGoals(email);
        int pointer = arrayListGoals.size() - 1;/*
        for (int i = 27; i >= 0; i--) {
            if(pointer != -1) {
                goals[i] = arrayListGoals.get(pointer);
                pointer--;
            }
            else{
                goals[i] = new Goal(new Date(), 0);
            }
        }*/


        int counter = 0;
        Collections.sort(arrayListGoals, new Comparator<Goal>() {
            @Override
            public int compare(Goal goal1, Goal goal2)
            {

                return  goal1.compareTo(goal2);
            }
        });

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -27);
        calendar.set(Calendar.HOUR,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        Date date = calendar.getTime();

        int i=0;
        long currGoal=5000;
        while(calendar.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()){
            while(!arrayListGoals.isEmpty() && arrayListGoals.get(0).getDate().compareTo(date) <= 0){
                currGoal=arrayListGoals.get(0).getStepGoal();
                arrayListGoals.remove(0);
            }
            goals[i] = (int) currGoal;
            i++;
            calendar.add(Calendar.DAY_OF_YEAR,+1);
            date=calendar.getTime();
        }

        return goals;
    }
}