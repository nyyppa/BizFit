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
import com.bizfit.bizfit.utils.AssetManagerOur;
import com.bizfit.bizfit.utils.FieldNames;

import com.bizfit.bizfit.utils.YAxisRendererCustom;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

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

    /**
     * Initializes, populates graphs with data and adds them to containers.
     */
    private void createGraphs() {
        totalProgressChart = new LineChart(getBaseContext());
        LineDataSet dataSet = new LineDataSet(createDataSet(), "");
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        // Array containing all the data sets being added to the graph.
        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(dataSet);

        // X-axis values.
        ArrayList<String> xValues = createXValues();

        LineData data = new LineData(xValues, dataSets);
        totalProgressChart.setData(data);
        formatGraphTotalProgressChart();
        ((FrameLayout) findViewById(R.id.total_progress_container)).addView(totalProgressChart);

        // Called to ensure proper drawing.
        totalProgressChart.invalidate();
    }

    /**
     * Formats the totalProgress graph.
     * <p/>
     * The specs are defined here. No attributes for the variables are created
     * since it would lead to an excessive amount of clutter. Any changes
     * to the formatting should be hardcoded here. Only exception are the
     * dimensions which should be defined in the corresponding .xml files.
     */
    private void formatGraphTotalProgressChart() {
        (totalProgressChart.getLegend()).setEnabled(false);
        totalProgressChart.setNoDataTextDescription(getResources().getString(R.string.insufficent_data));
        totalProgressChart.setDescription("");
        totalProgressChart.setScaleYEnabled(false);
        totalProgressChart.setDrawGridBackground(false);
        totalProgressChart.setViewPortOffsets(0, 250, 0, 0);
        totalProgressChart.setRendererLeftYAxis(new YAxisRendererCustom(
                totalProgressChart.getViewPortHandler()
                , totalProgressChart.getAxisLeft()
                , totalProgressChart.getTransformer(YAxis.AxisDependency.LEFT)
                , host
        ));

        XAxis xAxis = totalProgressChart.getXAxis();
        xAxis.setDrawLabels(false);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setAxisLineColor(getResources().getColor(R.color.white));
        xAxis.setAxisLineWidth(getResources().getInteger(R.integer.x_axis_height));

        YAxis right = totalProgressChart.getAxisRight();
        right.setEnabled(false);

        YAxis left = totalProgressChart.getAxisLeft();
        left.setTypeface(AssetManagerOur.getFont(AssetManagerOur.regular));
        left.setTextSize(getResources().getInteger(R.integer.text_caption));
        left.setDrawAxisLine(false);
        left.setYOffset(getResources().getInteger(R.integer.y_axis_label_y_offset));
        left.setTextColor(getResources().getColor(R.color.white));
        left.setGridColor(getResources().getColor(R.color.white70));
        left.setAxisLineColor(getResources().getColor(R.color.white70));
        left.setStartAtZero(true);
        left.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        left.setAxisMaxValue(host.getTargetProgress() < host.getCurrentProgress() ?
                host.getCurrentProgress() : host.getTargetProgress());

        LineDataSet lineData = totalProgressChart.getData().getDataSets().get(0);
        lineData.setDrawValues(false);
        lineData.setColor(getResources().getColor(R.color.white));
        lineData.setCircleColor(getResources().getColor(R.color.white));
        lineData.setCircleSize(0);
        lineData.setLineWidth(getResources().getInteger(R.integer.total_progress_graph_line_width));
    }

    /**
     * Pulls data from the Tracker used to create x axis values.
     *
     * @return ArrayList containing x axis values.
     */
    private ArrayList<String> createXValues() {
        DailyProgress.DayPool[] data = host.getAllDayPools();
        ArrayList<String> xValues = new ArrayList<>();

        // TODO Fetch data from Tracker.
        for (int i = 0; i < 10; i++) {
            xValues.add(i + "");
        }

        return xValues;
    }

    /**
     * Pulls data from the Tracker used to create y axis values.
     * <p/>
     * Each data point is also given an index which represents their location
     * along the x axis.
     *
     * @return Data set containing points in the x - y coorinate system.
     */
    private ArrayList<Entry> createDataSet() {
        DailyProgress.DayPool[] data = host.getAllDayPools();
        ArrayList<Entry> formattedData = new ArrayList<>();


        formattedData.add(new Entry(0, 0));
        formattedData.add(new Entry(host.getCurrentProgress(), 1));


        return formattedData;
    }

    /**
     * Opens a dialogue fragment used to inquire progress from user.
     * <p/>
     * The inputted data is added to the Tracker's total progress.
     * TODO error handling and overall better fragment.
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
                System.out.println(progress);
                host.addProgress(progress);
                totalProgressChart.invalidate();
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
