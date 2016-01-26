package com.bizfit.bizfit.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.bizfit.bizfit.DailyProgress;
import com.bizfit.bizfit.R;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.utils.FieldNames;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

/**
 *
 */
public class ViewTrackerActivity extends AppCompatActivity {

    Activity activity;
    Tracker host;
    LineChart totalProgressChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_view_tracker);
        host = (Tracker) (getIntent().getSerializableExtra(FieldNames.TRACKER));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(host.getName());
        setSupportActionBar(toolbar);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialoque();
            }
        });
        createGraphs();
    }

    private void createGraphs() {
        totalProgressChart = new LineChart(getBaseContext());
        LineDataSet dataSet = new LineDataSet(createDataSet(), "");
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(dataSet);

        ArrayList<String> xValues = createXValues();

        LineData data = new LineData(xValues, dataSets);
        totalProgressChart.setData(data);
        formatGraphTotalProgressChart();

        ((FrameLayout) findViewById(R.id.total_progress_container)).addView(totalProgressChart);
        totalProgressChart.invalidate();
    }

    private void formatGraphTotalProgressChart() {
        (totalProgressChart.getLegend()).setEnabled(false);
        totalProgressChart.setNoDataTextDescription(getResources().getString(R.string.insufficent_data));
        totalProgressChart.setDescription("");
        totalProgressChart.setScaleYEnabled(false);
        totalProgressChart.setDrawGridBackground(false);

        XAxis xAxis = totalProgressChart.getXAxis();
        xAxis.setDrawLabels(false);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineColor(getResources().getColor(R.color.white));

        YAxis right = totalProgressChart.getAxisRight();
        right.setEnabled(false);

        YAxis left = totalProgressChart.getAxisLeft();
        left.setDrawAxisLine(false);
        left.setTextColor(getResources().getColor(R.color.white));
        left.setGridColor(getResources().getColor(R.color.white70));
        left.setAxisLineColor(getResources().getColor(R.color.white70));

        LineDataSet lineData = totalProgressChart.getData().getDataSets().get(0);
        lineData.setDrawValues(false);
        lineData.setColor(getResources().getColor(R.color.white));
        lineData.setCircleColor(getResources().getColor(R.color.white));
        lineData.setCircleSize(0);
        lineData.setLineWidth(getResources().getInteger(R.integer.total_progress_graph_line_width));
    }

    private ArrayList<String> createXValues() {
        DailyProgress.DayPool[] data = host.getAllDayPools();
        ArrayList<String> xValues = new ArrayList<>();

        // TODO Fetch data from Tracker.
        for (int i = 0; i < 10; i++) {
            xValues.add(i + "");
        }

        return xValues;
    }

    private ArrayList<Entry> createDataSet() {
        DailyProgress.DayPool[] data = host.getAllDayPools();
        ArrayList<Entry> formattedData = new ArrayList<>();

        // TODO Fetch data from Tracker.
        for (int i = 0; i < 10; i++) {
            formattedData.add(new Entry(3 * i, i));
        }

        return formattedData;
    }


    private void openDialoque() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Amount to add");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                float progress = Float.parseFloat(input.getText().toString());
                System.out.println(progress);
                host.addProgress(progress);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


}
