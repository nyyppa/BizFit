package com.bizfit.bizfit.fragments;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.activities.ViewTrackerActivity;
import com.bizfit.bizfit.views.CustomBarChart;
import com.bizfit.bizfit.views.CustomLineChart;

import java.io.Serializable;

/**
 * Created by Käyttäjä on 29.2.2016.
 */
public class ViewTrackerFragment extends Fragment {

    /**
     * Tracker, from which relevant data is pulled from.
     */
    private Tracker tracker;

    /**
     * Accumulative line chart which displays total progress.
     */
    private CustomLineChart totalProgressChart;

    /**
     * Shows values added per day.
     */
    private CustomBarChart dailyProgressChart;

    public static ViewTrackerFragment newInstance(Serializable describable) {
        ViewTrackerFragment fragment = new ViewTrackerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("TRACKER", describable);
        fragment.setArguments(bundle);

        return fragment;
    }

    public ViewTrackerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tracker = (Tracker) getArguments().getSerializable("TRACKER");
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
        ViewTrackerActivity parent = (ViewTrackerActivity) getActivity();
        Context context = parent.getBaseContext();

        // If theming changes, comment this line out.
        //toolAndStatusbarStylize(toolbar);

        createTotalProgressChart(context);
        createDailyProgressChart(context);
    }

    private void createDailyProgressChart( Context context) {
        dailyProgressChart = new CustomBarChart(context, tracker);
        ((FrameLayout) getView().findViewById(R.id.daily_progress_container)).addView(dailyProgressChart);

        // Called to ensure proper drawing.
        dailyProgressChart.invalidate();
    }

    /**
     * Initializes, populates graph with data and adds it to container.
     */
    private void createTotalProgressChart(Context context) {
        totalProgressChart = new CustomLineChart(context, tracker);
        ((FrameLayout) getView().findViewById(R.id.total_progress_container)).addView(totalProgressChart);

        // Called to ensure proper drawing.
        totalProgressChart.invalidate();
    }

    public Tracker getTracker() {
        return tracker;
    }

    public void setTracker(Tracker host) {
        this.tracker = host;
    }

    public void update() {
        if(totalProgressChart != null) totalProgressChart.update();
        if(dailyProgressChart!= null)dailyProgressChart.update();
    }
}
