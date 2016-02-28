package com.bizfit.bizfit.views;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.activities.MainActivity;

/**
 *
 */
abstract public class TrackableViewBase {

    // Tracker from which data is pulled from.
    protected Tracker trackerHost;

    // View which handles displaying information.
    protected View viewHost;

    // Calculates the animation values.
    protected ValueAnimator animator;

    // Max value for the percentage indicator. Capped so that it doesn't
    // occupy excessive amount of space in fringe cases.
    protected final int maxPercentage = 999;

    /**
     * Stylizes the view.
     */
    abstract void formatView();

    /**
     * Sets the current percentage.
     *
     * Used in animation.
     *
     * @param percentage New percentage.
     */
    abstract void setPercentage(int percentage);

    /**
     * Updates progressBar.
     *
     * Used in animation.
     *
     * @param progress Current progress.
     */
    abstract void updateProgressBar(float progress);

    /**
     * Sets on click event reactions.
     */
    protected void prepFunctionality() {
        // Action for click
        /**
        viewHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)viewHost.getContext()).launchViewTrackerActivity(trackerHost,  TrackableViewBase.this);
            }
        });*/

        // Action for long click
        // TODO Make a menu appear.
        viewHost.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });
    }

    /**
     * Animates the percentage and progress bar.
     *
     * The range of values starts from 0 and extends to maxAnimatorValue.
     */
    public void animateFromZero() {
        float value, maxAnimatorValue;

        // Prevents the animation from targeting above the maximum
        // allowed value.
        maxAnimatorValue = (((value = trackerHost.getProgressPercent()) * 100) < maxPercentage)
                ? value : (maxPercentage / 100f);

        animator = ValueAnimator.ofFloat(0, maxAnimatorValue);
        animator.setDuration(viewHost.getResources().getInteger(R.integer.from_zero_anim_duration));
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                updateProgressBar(value);
                setPercentage((int) (Math.floor(value * 100)));
            }
        });

        animator.start();
    }


    /**
     * Sets the trackerHost.
     *
     * @param trackerHost New host to pull data from.
     */
    public void setTrackerHost(Tracker trackerHost) {
        this.trackerHost = trackerHost;
    }

}
