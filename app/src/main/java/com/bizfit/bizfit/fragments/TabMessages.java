package com.bizfit.bizfit.fragments;

/**
 *
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.utils.RecyclerViewAdapterMessages;
import com.bizfit.bizfit.utils.RecyclerViewAdapterTabMessages;

public class TabMessages extends Fragment {

    public static String tag;

    public TabMessages() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.tab_fragment_messages, container, false);
        RecyclerView mRecyclerView = (RecyclerView) v.findViewById(R.id.tab_messages_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new RecyclerViewAdapterTabMessages());

        return v;
    }
}
