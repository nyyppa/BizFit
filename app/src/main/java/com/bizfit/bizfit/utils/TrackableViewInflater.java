package com.bizfit.bizfit.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.activities.MainActivity;
import com.bizfit.bizfit.views.TrackableView;

/**
 * Is responsible for creating and adding new views to MainActivity.
 */
public class TrackableViewInflater {

    LayoutInflater inflater;

    public TrackableViewInflater(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    /**
     * Inflates and adds a new View to MainActivity.
     *
     * @param tracker   Tracker from which info is pulled from.
     * @param container Where the inflated tracker should be placed in.
     * @param activity  Activity in which the View resides in.
     * @return The inflated View. Does not need to be added to it's container,
     * as it's done within the method.
     */
    public TrackableView inflate(final Tracker tracker, ViewGroup container, final MainActivity activity) {
        // Inflates a new viewgroup from .xml resource file.
        ViewGroup view = (LinearLayout) inflater.inflate(R.layout.activity_main_trackable_view, container, false);

        // Wrapper which contains the View and it's corresponding Tracker.
        TrackableView trackableView = new TrackableView(tracker, view);

        // Sets on click action
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.launchViewTrackerActivity(tracker);
            }
        });

        container.addView(view);

        return trackableView;
    }
}
