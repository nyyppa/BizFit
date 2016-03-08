package com.bizfit.bizfit.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.User;
import com.bizfit.bizfit.activities.ViewTrackerActivity;
import com.bizfit.bizfit.views.CustomBarChart;
import com.bizfit.bizfit.views.CustomLineChart;

import org.joda.time.DateTime;

/**
 * Created by Käyttäjä on 29.2.2016.
 */
public class ViewTrackerFragment extends Fragment {

    /**
     * Tracker, from which relevant data is pulled from.
     */
    private Tracker host;

    /**
     * Accumulative line chart which displays total progress.
     */
    private CustomLineChart totalProgressChart;

    /**
     * Shows values added per day.
     */
    private CustomBarChart dailyProgressChart;

    public ViewTrackerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragmet_view_tracker_content, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        update();
        //ViewTrackerActivity parent = (ViewTrackerActivity) getActivity();
        //Context context = parent.getBaseContext();
        //parent.getSupportActionBar().setTitle(host.getName());
        //parent.findViewById(R.id.total_progress_container).setBackgroundColor(host.getColor());
        //parent.findViewById(R.id.info_text_container).setBackgroundColor(host.getColor());

        // If theming changes, comment this line out.
        //toolAndStatusbarStylize(toolbar);

        //fillTextViews(parent);
        //createTotalProgressChart(parent, context);
        //createDailyProgressChart(parent, context);
    }

    private void createDailyProgressChart(Activity parent, Context context) {
        dailyProgressChart = new CustomBarChart(context, host);
        ((FrameLayout) parent.findViewById(R.id.daily_progress_container)).addView(dailyProgressChart);

        // Called to ensure proper drawing.
        dailyProgressChart.invalidate();
    }

    /**
     * Initializes, populates graph with data and adds it to container.
     */
    private void createTotalProgressChart(Activity parent, Context context) {
        totalProgressChart = new CustomLineChart(context, host);
        ((FrameLayout) parent.findViewById(R.id.total_progress_container)).addView(totalProgressChart);

        // Called to ensure proper drawing.
        totalProgressChart.invalidate();
    }

    public Tracker getTracker() {
        return host;
    }

    public void setTracker(Tracker host) {
        this.host = host;
    }

    public void update() {
        //totalProgressChart.update();
        //dailyProgressChart.update();
    }
}
