package com.bizfit.bizfit.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.activities.MainActivity;
import com.bizfit.bizfit.views.ExpandableTrackableView;

import java.util.ArrayList;

public class TabTrackables extends Fragment {

    ArrayList<ExpandableTrackableView> views;

    public TabTrackables() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_trackers, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populate();
    }


    public void populate() {
        views = new ArrayList<>();
        MainActivity parentActivity = (MainActivity) getActivity();
        LayoutInflater inflater = parentActivity.getLayoutInflater();
        Tracker[] trackers = parentActivity.getCurrentUser().getTrackers();
        ViewGroup viewContainer = (ViewGroup) parentActivity.findViewById(R.id.goal_container);

        for (int i = 0; i < trackers.length; i++) {
            // Inflates a new viewgroup from .xml resource file.
            View view = inflater.inflate(R.layout.view_trackable_expandable, viewContainer, false);

            // Wrapper which contains the View and it's corresponding Tracker.
            ExpandableTrackableView trackableView = new ExpandableTrackableView(view, trackers[i]);
            views.add(trackableView);
            viewContainer.addView(view);
        }

        viewContainer.invalidate();
    }

    public void addTracker(Tracker tracker, LayoutInflater inflater, ViewGroup viewContainer) {
        // Inflates a new viewgroup from .xml resource file.
        View view = inflater.inflate(R.layout.view_trackable_expandable, viewContainer, false);

        // Wrapper which contains the View and it's corresponding Tracker.
        ExpandableTrackableView trackableView = new ExpandableTrackableView(view, tracker);
        views.add(trackableView);
        viewContainer.addView(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("Over here!");
        for (ExpandableTrackableView view : views) {
            view.update();
        }
    }
}

