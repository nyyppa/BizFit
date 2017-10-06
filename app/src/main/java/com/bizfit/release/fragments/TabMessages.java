package com.bizfit.release.fragments;

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

import com.bizfit.release.R;
import com.bizfit.release.scrollCoordinators.EndlessRecyclerOnScrollListener;
import com.bizfit.release.RecyclerViews.RecyclerViewAdapterTabMessages;

public class TabMessages extends Fragment {

    public static String tag;
    private RecyclerViewAdapterTabMessages adapter;

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
        mRecyclerView.setAdapter(new RecyclerViewAdapterTabMessages(null));

        return v;
    }
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.tab_messages_recyclerView);
        LinearLayoutManager mManager = new LinearLayoutManager(getContext());
        adapter = new RecyclerViewAdapterTabMessages(null);

        mRecyclerView.setLayoutManager(mManager);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mManager)
        {
            @Override
            public void onLoadMore(int current_page) {
                // TODO Request data from server.
            }
        });





    }

}
