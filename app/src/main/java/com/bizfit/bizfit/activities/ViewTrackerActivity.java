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
import android.widget.TextView;

import com.bizfit.bizfit.DailyProgress;
import com.bizfit.bizfit.R;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.utils.FieldNames;

import com.bizfit.bizfit.views.CustomLineChart;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.MutableDateTime;
import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Displays information about the Tracker.
 */
public class ViewTrackerActivity extends AppCompatActivity {
    /**
     * Parent activity
     */
    Activity activity;

    /**
     * Tracker, from which relevant data is pulled from.
     */
    Tracker host;

    /**
     * Accumulative line chart which displays total progress.
     */
    CustomLineChart totalProgressChart;

    /**
     * Shows values added per day.
     */
    BarChart dailyProgressChart;

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
        fillTextViews();
        createGraphs();
    }

    /**
     * Initializes, populates graphs with data and adds them to containers.
     */
    private void createGraphs() {
        createTotalProgressChart();
        createDailyProgressChart();
    }

    private void createDailyProgressChart() {
        dailyProgressChart = new BarChart(getBaseContext());
        BarData dailyTargetReached = new BarData();
        BarData dailyTargetNotReached = new BarData();
    }

    /**
     * Initializes, populates graph with data and adds it to container.
     */
    private void createTotalProgressChart() {
        totalProgressChart = new CustomLineChart(getBaseContext(), host);
        ((FrameLayout) findViewById(R.id.total_progress_container)).addView(totalProgressChart);

        // Called to ensure proper drawing.
        totalProgressChart.invalidate();
    }

    /**
     * Opens a dialogue fragment used to inquire progress from user.
     * <p/>
     * The inputted data is added to the Tracker's total progress.
     * TODO error handling and overall better fragment. Option to use slider.
     */
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
                host.addProgress(progress);
                totalProgressChart.update();
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

    /**
     * Populates info text's with data from Tracker.
     */
    private void fillTextViews() {
        DateTime endDate = new DateTime(host.getEndDateMillis());

        ((TextView) findViewById(R.id.target_amount)).setText(
                (int) host.getTargetProgress() + "");

        ((TextView) findViewById(R.id.time_left_amount)).setText(
                endDate.getDayOfMonth() + "." +
                        endDate.getMonthOfYear() + "." +
                        endDate.getYear()
        );
    }
}
