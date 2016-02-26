package com.bizfit.bizfit.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.views.TrackableViewBase;

import java.util.LinkedList;

public class TabTrackables extends Fragment {

    private LinkedList<TrackableViewBase> trackableViews;
    private LinearLayout layout;

    public TabTrackables() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trackableViews = new LinkedList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_trackers, container, false);
    }
}
