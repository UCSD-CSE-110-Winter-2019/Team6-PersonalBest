package com.example.personalbest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class GraphActivity extends AppCompatActivity {
    private final String[] labels = {"Exercise Steps", "Background Steps"};
    private final int[] colors = {0xff0000ff, 0xff5B2C6F};
    private int numSteps;
    private SaveLocal saveLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);


        numSteps = getIntent().getIntExtra("numSteps", 0);

        Button returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnMain();
            }
        });

        CombinedChart combinedChart = findViewById(R.id.combinedChart);

        saveLocal = new SaveLocal(this);

        long[] exercise = getExercise(saveLocal);
        long[] background = getBackground(saveLocal);
        long[] goals = getGoals(saveLocal);

        buildGraph(combinedChart, exercise, background, goals);
    }

    public void buildGraph(CombinedChart combinedChart, long[] workout, long[] background, long[] goals){
        combinedChart.getDescription().setEnabled(false);

        List<BarEntry> barEntries = new ArrayList<>();

        for(int i = 0; i < 7; i++){
            barEntries.add(new BarEntry(i, new float[] {workout[i], background[i]}));
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "");
        barDataSet.setStackLabels(labels);
        barDataSet.setLabel("");
        barDataSet.setValueTextSize(10f);
        barDataSet.setColors(colors);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.5f);


        List<Entry> lineEntries = new ArrayList<>();

        for(int i = 0; i < 7; i++){
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

        CombinedData combinedData = new CombinedData();
        combinedData.setData(barData);
        combinedData.setData(lineData);

        combinedChart.setData(combinedData);
        combinedChart.getXAxis().setDrawLabels(false);
        combinedChart.setVisibleXRangeMaximum(4);
        combinedChart.moveViewToX(2);

        XAxis xAxis = combinedChart.getXAxis();
        xAxis.setSpaceMin(barData.getBarWidth() / 2f);
        xAxis.setSpaceMax(barData.getBarWidth() / 2f);

        combinedChart.invalidate();

    }

    public void returnMain(){
        this.finish();
    }

    public long[] getBackground(SaveLocal saveLocal){
        long[] background = new long[7];
        for(int i = 1; i < 7; i++){
            background[6-i] = saveLocal.getBackgroundStepCount(i);
        }
        background[6] = numSteps - saveLocal.getExerciseStepCount(0);
        return background;
    }
    public long[] getExercise(SaveLocal saveLocal){
        long[] exercise = new long[7];
        for(int i = 0; i < 7; i++){
            exercise[6-i] = saveLocal.getExerciseStepCount(i);
        }
        return exercise;
    }
    public long[] getGoals(SaveLocal saveLocal){
        long[] goals = new long[7];
        for(int i = 1; i < 7; i++){
            goals[6 - i] = saveLocal.getGoals(i);
        }
        goals[6] = saveLocal.getGoal();
        return goals;
    }

    public void launchMonth(View view){
        Intent intent = new Intent(this, MonthGraph.class);
        intent.putExtra("email", saveLocal.getEmail());
        startActivity(intent);
    }
}