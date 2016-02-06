package com.bizfit.bizfit.views;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.Tracker;

/**
 * Is responsible for creating and adding new views to MainActivity.
 */
public class TrackableViewInflater {

    LayoutInflater inflater;

    public TrackableViewInflater(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    public TrackableViewBase inflate(final Tracker tracker, ViewGroup container, ViewType type) {
        TrackableViewBase view = null;

        if (type == ViewType.NORMAL) {
            view = inflateRegular(tracker, container);
        } else if (type == ViewType.ALTERNATIVE) {
            view = inflateAlternative(tracker, container);
        }

        return view;
    }

    /**
     * Inflates and adds a new View to MainActivity.
     *
     * @param tracker   Tracker from which info is pulled from.
     * @param container Where the inflated tracker should be placed in.
     * @return The inflated View. Does not need to be added to it's container,
     * as it's done within the method.
     */
    private TrackableView inflateRegular(final Tracker tracker, ViewGroup container) {

        // Inflates a new viewgroup from .xml resource file.
        ViewGroup view = (LinearLayout) inflater.inflate(R.layout.view_trackable, container, false);

        // Wrapper which contains the View and it's corresponding Tracker.
        TrackableView trackableView = new TrackableView(tracker, view);

        // Add view to the container.
        container.addView(view);

        return trackableView;
    }

    /**
     * Inflates and adds a new View to MainActivity.
     *
     * @param tracker   Tracker from which info is pulled from.
     * @param container Where the inflated tracker should be placed in.
     * @return The inflated View. Does not need to be added to it's container,
     * as it's done within the method.
     */
    private TrackableViewAlternative inflateAlternative(final Tracker tracker, ViewGroup container) {

        // Inflates a new viewgroup from .xml resource file.
        ViewGroup view = (LinearLayout) inflater.inflate(R.layout.view_trackable_alternative, container, false);

        // Wrapper which contains the View and it's corresponding Tracker.
        TrackableViewAlternative trackableView = new TrackableViewAlternative(tracker, view);

        // Add view to the container.
        container.addView(view);

        return trackableView;
    }

    public enum ViewType {
        NORMAL, ALTERNATIVE;
    }
}
