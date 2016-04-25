package com.bizfit.bizfit.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bizfit.bizfit.R;

public class TabCoaches extends Fragment implements PagerAdapter.TaggedFragment{

    public static final String TAG = "tab_coaches";

    public TabCoaches() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_coaches, container, false);
    }

    @Override
    public String fragmentTag() {
        return TAG;
    }
}
