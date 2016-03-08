package com.bizfit.bizfit.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.User;
import com.bizfit.bizfit.activities.AddTrackerActivity;
import com.bizfit.bizfit.activities.MainActivity;
import com.bizfit.bizfit.activities.ViewTrackerActivity;
import com.bizfit.bizfit.utils.FieldNames;
import com.bizfit.bizfit.views.TrackableView;

// TODO MAKE USE OF setAguments(Bundle)!!!
public class TabTrackables extends Fragment {

    private final static int deleteID = 0;
    private static Tracker[] trackers;
    private static final int GET_NEW_GOAL = 1;

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
        trackers = User.getLastUser().getTrackers();
        populate();
    }


    public void populate() {
        MainActivity parentActivity = (MainActivity) getActivity();
        Context context = getContext();

        LayoutInflater inflater = (LayoutInflater) parentActivity.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);


        ViewGroup viewContainer = (ViewGroup) parentActivity.findViewById(R.id.goal_container);

        for (int i = (trackers.length - 1); i >= 0; i--) {
            TrackableView view = new TrackableView(context, trackers[i], inflater);
            viewContainer.addView(view);
            registerForContextMenu(view);
        }

        viewContainer.invalidate();
    }

    private void addTracker(Tracker tracker, ViewGroup container, LayoutInflater inflater, Context context) {
        TrackableView view = new TrackableView(context, tracker, inflater);
        container.addView(view, 0);
        registerForContextMenu(view);
        view.expand();

    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup viewContainer = (ViewGroup) getActivity().findViewById(R.id.goal_container);
        registerForContextMenu(viewContainer);

        // TODO better implementation.
        trackers = User.getLastUser().getTrackers();
        int index = 0;
        for (int i = (trackers.length - 1); i >= 0; i--) {
            ((TrackableView) viewContainer.getChildAt(i)).setTracker(trackers[index]);
            index++;
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        /**
         System.out.println("View is null: " + v);
         System.out.println("Menu is null: " + menu );
         System.out.println("MenuInfo is null: " + menuInfo);*/
        // TODO menuInfo is sometimes a null value. Investigate
        // Probably due to misuse of registerForContextMenu(View). Needs better
        // implementation with listview.
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v instanceof TrackableView) {
            menu.setHeaderTitle(((TrackableView) v).getTracker().getName());
            (menu.add(Menu.NONE, deleteID
                    , Menu.NONE
                    , getResources().getString(R.string.action_delete))).setActionView(v);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case deleteID:
                // TODO Confirmation dialogue
                // Builds and shows a toast
                (Toast.makeText(
                        getContext()
                        , getResources().getString(R.string.message_remove_successful)
                        , Toast.LENGTH_SHORT)
                ).show();

                // Removes tracker
                ((TrackableView) item.getActionView()).deleteViewAndTracker();
                return true;

            default:
                (Toast.makeText(getContext(), "Something else happened!", Toast.LENGTH_SHORT)).show();
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_NEW_GOAL && resultCode == Activity.RESULT_OK) {
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
            Activity parent = getActivity();
            addTracker(newTracker
                    , (ViewGroup) parent.findViewById(R.id.goal_container)
                    , parent.getLayoutInflater()
                    , parent);
        }
    }

    public void launchAddTrackerActivity() {

        Intent intent = new Intent(getActivity(), AddTrackerActivity.class);
        startActivityForResult(intent, GET_NEW_GOAL);
    }

    // TODO change stuff here
    public void launchViewTrackerActivity(Tracker tracker, int index) {
        Intent viewTracker = new Intent(getActivity(), ViewTrackerActivity.class);
        viewTracker.putExtra(FieldNames.INDEX, index);
        //viewTracker.putExtra(FieldNames.TRACKERS, getCurrentUser().getTrackers());
        startActivity(viewTracker);
    }
}

