package com.bizfit.bizfit.views;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.animations.Animations;
import com.bizfit.bizfit.utils.AssetManagerOur;

public class ExpandableTrackableView {

    private View view;
    private Tracker tracker;

    private TextView trackerName;
    private TextView targetAmount;
    private TextView timeLeftAmount;
    private TextView progressPercent;
    private Button addProgress;

    private boolean expanded;
    private View totalProgressContainer;
    private View dailyProgressContainer;
    private View infoContainer;

    /**
     * Is the default constructor when creating this object progmatically.
     *
     * @param view View on which data is displayed on.
     * @param tracker Tracker from which data is pulled from.
     */
    public ExpandableTrackableView(View view, Tracker tracker) {
        this.view = view;
        this.tracker = tracker;
        init();
        setTracker(tracker);
        updateDataFromTracker();
    }

    /**
     * Formats components which can be formatted without data from Tracker.
     */
    private void init() {
        this.trackerName = (TextView) view.findViewById(R.id.tracker_name);
        this.targetAmount = (TextView) view.findViewById(R.id.target_amount);
        this.timeLeftAmount = (TextView) view.findViewById(R.id.time_left_amount);
        this.progressPercent = (TextView) view.findViewById(R.id.progress_percentage);
        addProgress =  (Button) view.findViewById(R.id.button_add_progress);

        trackerName.setTypeface(AssetManagerOur.getFont(AssetManagerOur.medium));
        targetAmount.setTypeface(AssetManagerOur.getFont(AssetManagerOur.regular));
        timeLeftAmount.setTypeface(AssetManagerOur.getFont(AssetManagerOur.regular));
        progressPercent.setTypeface(AssetManagerOur.getFont(AssetManagerOur.regular));
        ((TextView) view.findViewById(R.id.target_label)).setTypeface(AssetManagerOur.getFont(AssetManagerOur.medium));
        ((TextView) view.findViewById(R.id.time_left_label)).setTypeface(AssetManagerOur.getFont(AssetManagerOur.medium));
        addProgress.setTypeface(AssetManagerOur.getFont(AssetManagerOur.bold));

        expanded = false;
        totalProgressContainer = view.findViewById(R.id.total_progress_container);
        dailyProgressContainer = view.findViewById(R.id.daily_progress_container);
        infoContainer = view.findViewById(R.id.top_container);

        view.findViewById(R.id.card_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!expanded) {
                    expand();

                } else {
                    collapse();
                }

                expanded = !expanded;
            }
        });
    }


    /**
     * Dictates which Views are shown in the collapsed state.
     */
    private void collapse() {
        Animations.collapse(totalProgressContainer);
        Animations.collapse(dailyProgressContainer);
        Animations.expand(infoContainer);
    }

    /**
     * Dictates which Views are shown in the expanded state.
     */
    private void expand() {
        Animations.expand(totalProgressContainer);
        Animations.expand(dailyProgressContainer);
        Animations.collapse(infoContainer);
    }

    /**
     * Returns the Tracker from which data is pulled from.
     *
     * @return Tracker from which data is pulled from.
     */
    public Tracker getTracker() {
        return tracker;
    }

    /**
     * Sets the tracker from which data is pulled.
     * <p/>
     * When creating this view directly from .xml, it is advised that after
     * having called this method updateDataFromTracker() is called. Otherwise
     * template values are shown.
     *
     * @param tracker Object from which data is pulled from.
     */
    public void setTracker(Tracker tracker) {
        this.tracker = tracker;
    }

    /**
     * Pulls data from the given tracker.
     * <p/>
     * Does nothing if tracker is not set. When creating this view directly
     * from .xml, it is advised that setTracker() is called followed by this
     * method. Otherwise template values are shown.
     */
    public void updateDataFromTracker() {
        if (tracker != null) {
            Tracker.RemainingTime time = tracker.getTimeRemaining();
            timeLeftAmount.setText((int)time.getTimeRemaining() + " " + time.getTimeType());
            trackerName.setText(tracker.getName());
            trackerName.setTextColor(tracker.getColor());
            targetAmount.setText((int) tracker.getTargetProgress() + "");
            progressPercent.setText((int)Math.floor(tracker.getProgressPercent() * 100) + "");
            totalProgressContainer.setBackgroundColor(tracker.getColor());
            addProgress.setTextColor(tracker.getColor());
        }
    }
}
