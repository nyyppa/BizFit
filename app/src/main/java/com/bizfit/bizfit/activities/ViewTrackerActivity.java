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

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.utils.FieldNames;
import com.bizfit.bizfit.utils.Utils;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 *
 */
public class ViewTrackerActivity extends AppCompatActivity {

    Activity activity;
    Tracker host;
    GraphView graph;

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

        fillInfo();
        createGraphs();
    }

    private void createGraphs() {
        graph = new GraphView(getBaseContext());
        LineGraphSeries<DataPoint> series = createDataPoints();
        ((FrameLayout) (findViewById(R.id.total_progress_container))).addView(graph);
        graph.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT
                , FrameLayout.LayoutParams.MATCH_PARENT
        ));
        stylize(graph, series);
        graph.addSeries(series);
    }

    private void stylize(GraphView graph, LineGraphSeries<DataPoint> series) {
        GridLabelRenderer renderer = graph.getGridLabelRenderer();
        renderer.setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        renderer.setHorizontalLabelsColor(R.color.colorTextPrimary);
        renderer.setVerticalLabelsColor(R.color.colorTextPrimary);
        series.setColor(R.color.colorTextPrimary);
        series.setThickness((int) Utils.dp2px(getResources(), 1));
    }

    private LineGraphSeries<DataPoint> createDataPoints() {
        return new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
    }

    private void fillInfo() {
        TextView targetAmount = (TextView) findViewById(R.id.target_amount);
        targetAmount.setText((int) host.getTargetProgress() + "");

        TextView timeLeft = (TextView) findViewById(R.id.time_left_amount);
        Tracker.RemainingTime time = host.getTimeRemaining();
        timeLeft.setText(time.getTimeRemaining() + " " + time.getTimeType());
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
