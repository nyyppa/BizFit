package com.bizfit.bizfit.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.views.CustomBarChart;
import com.bizfit.bizfit.views.CustomLineChart;

import java.io.Serializable;

/**
 * Created by Käyttäjä on 29.2.2016.
 */
public class ViewTrackerFragment extends Fragment implements Tracker.DataChangedListener {

    private String TAG = this.getClass().getName();

    /**
     * Tracker, from which relevant data is pulled from.
     */
    private Tracker tracker;

    public static ViewTrackerFragment newInstance(Serializable describable) {
        ViewTrackerFragment fragment = new ViewTrackerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("TRACKER", describable);
        fragment.setArguments(bundle);
        Log.d("ViewTrackerFragment", "newInstance()");
        return fragment;
    }

    public ViewTrackerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ViewTrackerFragment", "onCreate()");
        tracker = (Tracker) getArguments().getSerializable("TRACKER");
        tracker.setDataChangedListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragmet_view_tracker_content, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View root = getView();
        root.findViewById(R.id.info_text_container).setBackgroundColor(tracker.getColor());
        root.findViewById(R.id.total_progress_container).setBackgroundColor(tracker.getColor());
        ((CustomBarChart) root.findViewById(R.id.daily_progress_chart)).setTracker(tracker);
        ((CustomLineChart) root.findViewById(R.id.total_progress_chart)).setTracker(tracker);
        ((TextView) root.findViewById(R.id.time_left_amount)).setText(tracker.getEndDateMillis() + "");
        ((TextView) root.findViewById(R.id.target_amount)).setText(((int) tracker.getTargetProgress()) + "");
        ((TextView) root.findViewById(R.id.tracker_name)).setText(tracker.getName());

    }

    @Override
    public void onResume() {
        super.onResume();
        // If theming changes, comment this line out.
        //toolAndStatusbarStylize(toolbar);
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

    private void update() {
        Log.d(TAG, "update()");
        View root = getView();
        ((CustomBarChart) root.findViewById(R.id.daily_progress_chart)).update();
        ((CustomLineChart) root.findViewById(R.id.total_progress_chart)).update();
    }


    @Override
    public void dataChanged(Tracker tracker) {
        update();
    }
}
