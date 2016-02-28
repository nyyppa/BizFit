package com.bizfit.bizfit.views;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.activities.MainActivity;
import com.bizfit.bizfit.animations.Animations;
import com.bizfit.bizfit.utils.AssetManagerOur;

public class TrackableView extends FrameLayout {

    public final static String NAME = "com.bizfit.bizfit.views.ExpandableTrackableView";
    private Tracker tracker;
    View layout;

    private TextView trackerName;
    private TextView targetAmount;
    private TextView timeLeftAmount;
    private TextView progressPercent;

    private View infoContainer;

    public TrackableView(Context context, Tracker tracker, LayoutInflater inflater) {
        super(context);
        this.tracker = tracker;
        layout = inflater.inflate(R.layout.view_trackable_expandable, this, false);
        this.addView(layout);
        init();
    }

    public TrackableView(Context context, AttributeSet attrs, Tracker tracker) {
        super(context, attrs);
        this.tracker = tracker;
    }

    public TrackableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public TrackableView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Is the default constructor when creating this object progmatically.
     *
     * @param view    View on which data is displayed on.
     * @param tracker Tracker from which data is pulled from.
     */


    /**
     * Formats components which can be formatted without data from Tracker.
     */
    private void init() {
        this.trackerName = (TextView) layout.findViewById(R.id.tracker_name);
        this.targetAmount = (TextView) layout.findViewById(R.id.target_amount);
        this.timeLeftAmount = (TextView) layout.findViewById(R.id.time_left_amount);
        this.progressPercent = (TextView) layout.findViewById(R.id.progress_percentage);

        trackerName.setTypeface(AssetManagerOur.getFont(AssetManagerOur.medium));
        targetAmount.setTypeface(AssetManagerOur.getFont(AssetManagerOur.regular));
        timeLeftAmount.setTypeface(AssetManagerOur.getFont(AssetManagerOur.regular));
        progressPercent.setTypeface(AssetManagerOur.getFont(AssetManagerOur.regular));
        ((TextView) findViewById(R.id.target_label)).setTypeface(AssetManagerOur.getFont(AssetManagerOur.regular));
        ((TextView) findViewById(R.id.time_left_label)).setTypeface(AssetManagerOur.getFont(AssetManagerOur.regular));

        infoContainer = findViewById(R.id.top_container);

        findViewById(R.id.card_view).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((MainActivity) (getContext())).launchViewTrackerActivity(tracker);
            }
        });

        layout.findViewById(R.id.card_view).setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

        update();
    }


    /**
     * Dictates which Views are shown in the collapsed state.
     */
    private void collapse() {
        Animations.expand(infoContainer, Animations.animSpeedDef);
    }

    /**
     * Dictates which Views are shown in the expanded state.
     */
    private void expand() {
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
    public void update() {
        if (tracker != null) {
            Tracker.RemainingTime time = tracker.getTimeRemaining();
            timeLeftAmount.setText((int) time.getTimeRemaining() + " " + time.getTimeType());
            trackerName.setText(tracker.getName());
            trackerName.setTextColor(tracker.getColor());
            targetAmount.setText((int) tracker.getTargetProgress() + "");
            animatePercentage();
        }
    }
    // TODO Clean this up.
    private void animatePercentage() {
        float value, max, min;
        // Prevents the animation from targeting above the maximum
        // allowed value.
        max = (((value = tracker.getProgressPercent()) * 100) < 999)
                ? value : (999 / 100f);
        min = Float.parseFloat(progressPercent.getText().toString()) / 100;

        ValueAnimator animator = ValueAnimator.ofFloat(0, max);
        animator.setDuration(getResources().getInteger(R.integer.from_zero_anim_duration));
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                progressPercent.setText(((int) (Math.floor(value * 100))) + "");
            }
        });

        animator.start();
    }

    public void animationExpand() {
        Animations.expand(this, Animations.animSpeedDef);
    }
}
