package com.bizfit.bizfit.fragments;

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
import com.bizfit.bizfit.activities.ViewTrackerActivity;
import com.bizfit.bizfit.views.CustomBarChart;
import com.bizfit.bizfit.views.CustomLineChart;

import org.w3c.dom.Text;

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
        createTotalProgressChart(getContext());
        createDailyProgressChart(getContext());
        View root = getView();
        root.findViewById(R.id.info_text_container).setBackgroundColor(tracker.getColor());
        ((TextView)root.findViewById(R.id.time_left_amount)).setText(tracker.getEndDateMillis() + "");
        ((TextView) root.findViewById(R.id.target_amount)).setText(((int)tracker.getTargetProgress()) + "");
        ((TextView) root.findViewById(R.id.tracker_name)).setText(tracker.getName());

    }

    @Override
    public void onResume() {
        super.onResume();
        update();
        ViewTrackerActivity parent = (ViewTrackerActivity) getActivity();
        Context context = parent.getBaseContext();

        // If theming changes, comment this line out.
        //toolAndStatusbarStylize(toolbar);
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
        FrameLayout container = ((FrameLayout) getView().findViewById(R.id.total_progress_container));
        container.addView(totalProgressChart);
        container.setBackgroundColor(tracker.getColor());

        // Called to ensure proper drawing.
        totalProgressChart.invalidate();
    }

    public Tracker getTracker() {
        if (tracker == null) {
            tracker = (Tracker) getArguments().getSerializable("TRACKER");
        }

        return tracker;
    }

    public void setTracker(Tracker host) {
        this.tracker = host;
    }

    public void update() {
        if(totalProgressChart != null) {
            totalProgressChart.setTracker(tracker);
            totalProgressChart.update();
        }
        if(dailyProgressChart!= null) {
            dailyProgressChart.setTracker(tracker);
            dailyProgressChart.update();
        }
    }
}
