package com.bizfit.bizfit.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Space;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.User;
import com.bizfit.bizfit.activities.MainActivity;
import com.bizfit.bizfit.views.TrackableViewBase;
import com.bizfit.bizfit.views.TrackableViewInflater;

import java.util.LinkedList;

public class TabTrackables extends Fragment {

    private LinkedList<TrackableViewBase> trackableViews;
    private static TrackableViewInflater inflater;
    private LinearLayout layout;

    // Which kind of view should be displayed on the home screen.
    private static final TrackableViewInflater.ViewType type = TrackableViewInflater.ViewType.ALTERNATIVE;

    public TabTrackables() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = new TrackableViewInflater((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        trackableViews = new LinkedList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_trackers, container, false);
    }
}
