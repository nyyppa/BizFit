package com.bizfit.bizfit.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.activities.MainActivity;
import com.bizfit.bizfit.views.TrackableView;


public class TabTrackables extends Fragment {

    private final static int deleteID = 0;

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
        MainActivity parentActivity = (MainActivity) getActivity();
        Tracker[] trackers = parentActivity.getCurrentUser().getTrackers();
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

    public void addTracker(Tracker tracker) {
        ViewGroup viewContainer = (ViewGroup) getActivity().findViewById(R.id.goal_container);
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        TrackableView view = new TrackableView(getContext(), tracker, inflater);
        viewContainer.addView(view, 1);
        registerForContextMenu(view);
        view.expand();

    }

    @Override
    public void onResume() {
        super.onResume();

        ViewGroup viewContainer = (ViewGroup) getActivity().findViewById(R.id.goal_container);
        View view;
        TrackableView tmp;
        Tracker[] trackers = ((MainActivity) getActivity()).getCurrentUser().getTrackers();
        int index = trackers.length - 1;
        // TODO Clean up after fetching trackers is fixed.
        for (int i = 0; i < viewContainer.getChildCount(); i++) {
            if ((view = viewContainer.getChildAt(i)) instanceof TrackableView) {
                tmp = (TrackableView) view;
                tmp.setTracker(trackers[index]);
                tmp.update();
                index--;

            }
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v instanceof TrackableView) {
            menu.setHeaderTitle(((TrackableView) v).getTracker().getName());
            (menu.add(Menu.NONE, deleteID, Menu.NONE, getResources().getString(R.string.action_delete))).setActionView(v);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        switch (item.getItemId()) {
            case deleteID:
                // Builds and shows a toast
                (Toast.makeText(
                        getContext()
                        , getResources().getString(R.string.message_remove_successful)
                        , Toast.LENGTH_SHORT)
                ).show();
                ((TrackableView) item.getActionView()).deleteViewAndTracker();
                return true;

            default:
                (Toast.makeText(getContext(), "Something else happened!", Toast.LENGTH_SHORT)).show();
                return super.onContextItemSelected(item);
        }
    }
}

