package com.bizfit.bizfit.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.User;
import com.bizfit.bizfit.activities.AddTrackerActivity;
import com.bizfit.bizfit.utils.FieldNames;
import com.bizfit.bizfit.utils.RecyclerViewAdapter;

public class TabTrackables extends Fragment {

    public final static int deleteID = 0;
    public static Tracker[] trackers;
    private RecyclerViewAdapter adapter;
    private RecyclerView mRecyclerView;
    public static final int GET_NEW_GOAL = 1;

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

        // Get latest trackers
        trackers = User.getLastUser().getTrackers();

        // Context
        Activity parentActivity = getActivity();

        // Fetch RecyclerView..
        mRecyclerView = (RecyclerView) parentActivity.findViewById(R.id.tab_fragment_recycler_view);

        // Defines layout for RecyclerView.
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Custom adapter which holds data.
        adapter = new RecyclerViewAdapter();

        // Link RecyclerView with adapter.
        mRecyclerView.setAdapter(adapter);

        // Defines animations for changes made to the data set.
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(800);

        // Enable context menu
        registerForContextMenu(mRecyclerView);

        // Debug purposes.
        //Toast.makeText(getContext(), "Dataset size: " + trackers.length, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case deleteID:
                // TODO Confirmation dialogue
                trackers = User.getLastUser().getTrackers();
                trackers[adapter.getPosition()].delete();
                trackers = User.getLastUser().getTrackers();
                adapter.notifyItemRemoved(adapter.getPosition());


                // Confirm successful deletion
                (Toast.makeText(
                        getContext()
                        , getResources().getString(R.string.message_remove_successful)
                        , Toast.LENGTH_SHORT)
                ).show();

                // Consumed here
                return true;

            default:
                (Toast.makeText(getContext(), "Something else happened!", Toast.LENGTH_SHORT)).show();
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case GET_NEW_GOAL:
                if (resultCode == Activity.RESULT_OK) {
                    Tracker newTracker = new Tracker();
                    User.getLastUser().addTracker(newTracker);
                    newTracker.setName(data.getStringExtra(FieldNames.TRACKERNAME));
                    newTracker.setTargetAmount(data.getFloatExtra(FieldNames.TARGET, 0));
                    newTracker.setColor(data.getIntExtra(FieldNames.COLOR, R.color.colorAccent));
                    newTracker.setTargetDate(data.getIntExtra(FieldNames.YEAR, 2015)
                            , data.getIntExtra(FieldNames.MONTH, 1)
                            , data.getIntExtra(FieldNames.DAY, 1)
                            , data.getBooleanExtra(FieldNames.RECURRING, false
                    ));

                    trackers = User.getLastUser().getTrackers();
                    adapter.notifyItemInserted(newTracker.getIndex());

                } else {
                    (Toast.makeText(
                            getContext()
                            , getResources().getString(R.string.message_tracker_not_saved)
                            , Toast.LENGTH_SHORT)
                    ).show();
                }
        }
    }

    public void launchAddTrackerActivity() {
        Intent intent = new Intent(getActivity(), AddTrackerActivity.class);
        startActivityForResult(intent, GET_NEW_GOAL);
    }

    private boolean contentScrollable() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if (layoutManager == null || adapter == null) return false;

        return layoutManager.findLastCompletelyVisibleItemPosition() < adapter.getItemCount() - 1;
    }
}

