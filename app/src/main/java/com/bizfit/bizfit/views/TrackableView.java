package com.bizfit.bizfit.views;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.Tracker;

/**
 *
 */
public class TrackableView {

    Tracker trackerHost;
    View viewHost;
    ValueAnimator animator;
    TextView percentage;
    ProgressBar progressBar;

    public TrackableView(Tracker trackerHost, View viewHost) {
        this.trackerHost = trackerHost;
        this.viewHost = viewHost;
        formatView();
    }

    private void formatView() {
        // Sets correct texts.
        ((TextView) (viewHost.findViewById(R.id.label))).setText(trackerHost.getName());
        ((TextView) (viewHost.findViewById(R.id.time_left))).setText(
                trackerHost.getTimeRemaining().getTimeRemaining() + " "
                        + trackerHost.getTimeRemaining().getTimeType());

        // Touch effect
        viewHost.setBackgroundResource(R.drawable.ripple_effect);

        // Graphical indicator of the current progress.
        progressBar = (ProgressBar) viewHost.findViewById(R.id.progressbar);
        progressBar.setMax(R.integer.number_of_bar_steps);

        // Text indicator of the current progress percentage.
        percentage = (TextView) (viewHost.findViewById(R.id.percentage_container).findViewById(R.id.percentage));
    }


    public void animateFromZero() {
        animator = ValueAnimator.ofFloat(0, trackerHost.getProgressPercent());
        animator.setDuration(viewHost.getResources().getInteger(R.integer.from_zero_anim_duration));
        animator.setInterpolator(new DecelerateInterpolator());

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                updateProgressBar(value);
                setPercentage((int) (Math.floor(value * 100)) + "");
            }
        });

        animator.start();
    }

    private void setPercentage(String text) {
        percentage.setText(text);
    }

    /**
     * Updates ProgressBar during animation.
     *
     * @param progress The percentage of progress in decimals. 1 == 100%
     */
    private void updateProgressBar(float progress) {
        progressBar.setProgress((int) (progress * progressBar.getMax()));
    }

    public void setTrackerHost(Tracker trackerHost) {
        this.trackerHost = trackerHost;
    }
}
