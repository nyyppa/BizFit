package com.bizfit.bizfit.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
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
        host = (Tracker) (getIntent().getSerializableExtra(FieldNames.TRACKER));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(host.getName());
        setSupportActionBar(toolbar);
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.fab_add_progress);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialoque();
            }
        });
        findViewById(R.id.total_progress_container).setBackgroundColor(host.getColor());
        findViewById(R.id.info_text_container).setBackgroundColor(host.getColor());

        // If theming changes, comment this line out.
        //toolAndStatusbarStylize(toolbar);

        fillTextViews();
        createGraphs();
    }

    private void toolAndStatusbarStylize(Toolbar toolbar) {
        toolbar.setBackgroundColor(getResources().getColor(R.color.gray_50));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.gray_600));
        }
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
     * Opens a dialogue fragment used to inquire progress from userName.
     * <p/>
     * The inputted data is added to the Tracker's total progress.
     * TODO error handling and overall better fragment. Option to use slider.
     */
    private void openDialoque() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Amount to add");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        //input.setHighlightColor(host.getColor());
        //input.setDrawingCacheBackgroundColor(host.getColor());
        //input.getBackground().mutate().setColorFilter(host.getColor(), PorterDuff.Mode.SRC_ATOP);
       /** try {
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(input, R.drawable.cursor);
        } catch (Exception ignored) {
        }*/

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_tracker, menu);
        return true;
    }
    /**
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.settings_demo_1) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
