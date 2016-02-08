package com.bizfit.bizfit.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.utils.FieldNames;

import com.bizfit.bizfit.views.CustomBarChart;
import com.bizfit.bizfit.views.CustomLineChart;

import org.joda.time.DateTime;

import java.lang.reflect.Field;

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
    CustomBarChart dailyProgressChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tracker);
        activity = this;

        int sdk = android.os.Build.VERSION.SDK_INT;
        /**
        // Check which sdk is in use.
        if (sdk >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.statusBarColorViewTracker));
        }
        */

        setContentView(R.layout.activity_view_tracker);
        host = (Tracker) (getIntent().getSerializableExtra(FieldNames.TRACKER));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(host.getName());
        //toolbar.setBackgroundColor(host.getColor());
        setSupportActionBar(toolbar);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialoque();
            }
        });
        ((FrameLayout) findViewById(R.id.total_progress_container)).setBackgroundColor(host.getColor());
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
        dailyProgressChart = new CustomBarChart(getBaseContext(), host);
        ((FrameLayout) findViewById(R.id.daily_progress_container)).addView(dailyProgressChart);

        // Called to ensure proper drawing.
        dailyProgressChart.invalidate();
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
        input.setHighlightColor(host.getColor());
        input.setDrawingCacheBackgroundColor(host.getColor());
        input.getBackground().mutate().setColorFilter(host.getColor(), PorterDuff.Mode.SRC_ATOP);
        try {
            // https://github.com/android/platform_frameworks_base/blob/kitkat-release/core/java/android/widget/TextView.java#L562-564
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(input, R.drawable.cursor);
        } catch (Exception ignored) {
        }

        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                float progress = Float.parseFloat(input.getText().toString());
                host.addProgress(progress);
                totalProgressChart.update();
                dailyProgressChart.update();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // TODO Styling OUT of Java.
        AlertDialog dialog = builder.create();
        dialog.show();

        Button b1 = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        if (b1 != null)
            b1.setTextColor(host.getColor());

        Button b2 = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (b2 != null)
            b2.setTextColor(host.getColor());
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
