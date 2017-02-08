package com.bizfit.bizfit.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.tracker.Tracker;
import com.bizfit.bizfit.User;
import com.bizfit.bizfit.activities.AddTracker;
import com.bizfit.bizfit.activities.ViewTracker;
import com.bizfit.bizfit.utils.FieldNames;
import com.bizfit.bizfit.RecyclerViews.RecyclerViewAdapterTrackers;

/**
 * Displays visual representation of users progress.
 */
public class TabTrackables extends Fragment implements User.UserLoadedListener {

    /**
     * ID used to determine if remove option in contextual menu was clicked.
     */
    public final static int DELETE_ID = 0;

    /**
     * Used to determine if onActivityResult() callback was INCOMING
     * from AddTracker.
     */
    public static final int SET_NEW_GOAL = 1;

    /**
     * Used to determine if onActivityResult() callback was INCOMING
     * from ViewTracker.
     */
    public static final int VIEW_GOALS = 2;

    /**
     * Tag used to distinguish this fragment when accessing this fragment
     * via FragmentManager. Not yet in use.
     */
    public static final String TAG = "tab_trackables";
    public static String tag;
    /**
     * List of Tracker's loaded from SQLite database.
     */
    public static Tracker[] trackers;

    /**
     * Custom implementation of RecyclerViewAdapterTrackers.
     */
    private RecyclerViewAdapterTrackers adapter;

    /**
     * RecyclerView which holds all the Fragment's content.
     */
    private RecyclerView mRecyclerView;

    /**
     * Currently active user.
     */
    private User user;

    /**
     * Constructs a new TabTrackables object.
     *
     * Required empty public constructor.
     */
    public TabTrackables() {}

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

        // TODO Loading trackers from database with AsyncTask
        // Get latest trackers
        user=User.getLastUser(this, getContext(), null);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.tab_fragment_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new RecyclerViewAdapterTrackers();

        // Link RecyclerView with adapter.
        mRecyclerView.setAdapter(adapter);

        // Defines animations for changes made to the data set.
        // TODO Try out different Wasabeef animations.
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(650);
        mRecyclerView.getItemAnimator().setRemoveDuration(10);
        mRecyclerView.getItemAnimator().setMoveDuration(100);
        mRecyclerView.getItemAnimator().setChangeDuration(100);

        // Enable context menu
        registerForContextMenu(mRecyclerView);
        updateDataSet();

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case DELETE_ID:
                // TODO Confirmation dialogue
                trackers = user.getTrackers(User.TrackerSharedEnum.ALL);
                trackers[adapter.getPosition()].remove();
                trackers = user.getTrackers(User.TrackerSharedEnum.ALL);
                adapter.notifyItemRemoved(adapter.getPosition());


                // Confirm successful deletion
                (Toast.makeText(
                        getContext()
                        , getResources().getString(R.string.message_remove_successful)
                        , Toast.LENGTH_SHORT)
                ).show();

                // Call consumed here
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case SET_NEW_GOAL:
                if (resultCode == Activity.RESULT_OK) {
                    Log.d("Testi","testi");
                    final Intent data2=data;

                    User user=User.getLastUser(null, getContext(), null);
                    Log.d("Testi","testi");
                    final Tracker newTracker = new Tracker();

                    user.addTracker(newTracker);
                    newTracker.setName(data2.getStringExtra(FieldNames.TRACKERNAME));
                    newTracker.setTargetAmount(data2.getFloatExtra(FieldNames.TARGET, 0));
                    newTracker.setColor(data2.getIntExtra(FieldNames.COLOR, R.color.colorAccent));
                    newTracker.setTargetDate(data2.getIntExtra(FieldNames.YEAR, 2015)
                            , data2.getIntExtra(FieldNames.MONTH, 1)
                            , data2.getIntExtra(FieldNames.DAY, 1)
                            , data2.getBooleanExtra(FieldNames.RECURRING, false
                            ));
                    trackers = user.getTrackers(User.TrackerSharedEnum.ALL);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyItemInserted(newTracker.getIndex());

                            //Your code to run in GUI thread here
                        }//public void run() {
                    });





                } else {
                    (Toast.makeText(
                            getContext()
                            , getResources().getString(R.string.message_tracker_not_saved)
                            , Toast.LENGTH_SHORT)
                    ).show();
                }

                break;

            case VIEW_GOALS:
                // TODO Update each tracker wich had progress added instead of calling
                // notifyDataSetChanged() blanket notification. No animations are
                // are triggered when this method is called.
                if (user != null) {
                    trackers = user.getTrackers(User.TrackerSharedEnum.ALL);
                    adapter.notifyDataSetChanged();
                } else {
                    User.getLastUser(this, getContext(), null);
                }
                break;
        }
    }

    /**
     * Starts a new activity for setting a new goal.
     */
    public void launchAddTrackerActivity() {
        Intent intent = new Intent(getActivity(), AddTracker.class);
        startActivityForResult(intent, SET_NEW_GOAL);
    }

    /**
     * Is the current content scrollable.
     *
     * @return Is the content scrollable.
     */
    private boolean contentScrollable() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if (layoutManager == null || adapter == null)
            return false;
        return layoutManager.findLastCompletelyVisibleItemPosition() < adapter.getItemCount() - 1;
    }

    /**
     * Starts a new activity for viewing set goals.
     */
    public void launchViewTrackerActivity(RecyclerView.ViewHolder vh) {
        Intent viewTracker = new Intent(getActivity(), ViewTracker.class);
        viewTracker.putExtra(FieldNames.INDEX, vh.getAdapterPosition());
        viewTracker.putExtra(FieldNames.TRACKERS, trackers);
        // No results are actually wanted from the activity. Rather, this is a
        // way to distinguish the manner in which this Fragment is reached.
        // As the view's contained in this Fragment should be updated after
        // ViewTracker is closed.
        startActivityForResult(viewTracker, VIEW_GOALS);
    }




    @Override
    public void informationUpdated() {
        updateDataSet();
    }

    private void updateDataSet(){
        if(getActivity()!=null){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TabTrackables.this.user = user;
                    trackers = user.getTrackers(User.TrackerSharedEnum.ALL);
                    adapter.notifyDataSetChanged();
                }
            });
        }

    }
}

