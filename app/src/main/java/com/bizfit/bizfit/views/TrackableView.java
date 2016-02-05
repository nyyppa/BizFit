package com.bizfit.bizfit.views;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.utils.AssetManagerOur;

/**
 * Is a wrapper for Tracker and View pairs.
 *
 * The pulls information from Tracker and puts them on display in the provided
 * View, which is inflated from an .xml file.
 *
 * Even though TrackableView is mainly a wrapper class, it does provide
 * additional functions. Primarily animations and checks if certain
 * components in the View should be displayed.
 */
public class TrackableView {

    // Tracker from which data is pulled from.
    Tracker trackerHost;

    // View which handles displaying information.
    View viewHost;

    // Calculates the animation values.
    ValueAnimator animator;

    // Max value for the percentage indicator. Capped so that it doesn't
    // occupy excessive amount of space in fringe cases.
    private final int maxPercentage = 999;

    /**
     * Creates and stylizes a new TrackableView.
     *
     * @param trackerHost Tracker from which data is pulled from.
     * @param viewHost    View which displays information.
     */
    public TrackableView(Tracker trackerHost, View viewHost) {
        this.trackerHost = trackerHost;
        this.viewHost = viewHost;
        formatView();
    }

    /**
     * Sets information to View and does formatting which cant be done via xml.
     *
     * Formatting which can't be done via xml include, weather or not
     * progress indicators should be displayed, Tracker name, time left,
     * typefaces and dynamic padding. The last one is not implemented yet.
     */
    private void formatView() {

        // All the text element within the view.
        TextView label = (TextView) (viewHost.findViewById(R.id.label));
        TextView timeLeft = (TextView) (viewHost.findViewById(R.id.time_left));
        TextView percentage = (TextView) (viewHost.findViewById(R.id.percentage));
        TextView percentageSuffix = (TextView) (viewHost.findViewById(R.id.percentage_suffix));

        // Sets correct typefaces.
        label.setTypeface(AssetManagerOur.getFont(AssetManagerOur.regular));
        timeLeft.setTypeface(AssetManagerOur.getFont(AssetManagerOur.regular));
        percentage.setTypeface(AssetManagerOur.getFont(AssetManagerOur.regular));
        percentageSuffix.setTypeface(AssetManagerOur.getFont(AssetManagerOur.regular));


        // Sets correct texts.
        label.setText(trackerHost.getName());
        timeLeft.setText(trackerHost.getTimeRemaining().getTimeRemaining() + " "
                + trackerHost.getTimeRemaining().getTimeType());

        // Touch effect
        viewHost.setBackgroundResource(R.drawable.ripple_effect);

        // Graphical indicator of the current progress.
        ProgressBar progressBar = (ProgressBar) viewHost.findViewById(R.id.progressbar);

        // number_of_bar_steps effectively defines the smoothness of the animation.
        progressBar.setMax(R.integer.number_of_bar_steps);

        // Displays if the user is on time.
        ImageView indicator = (ImageView) viewHost.findViewById(R.id.percentage_container).findViewById(R.id.on_time_indicator);


        // Prepare the indicator which displays current progress rate.

        // On schedule indicator
        if (trackerHost.getProgressOnTrack().equals(Tracker.OnTrack.ahead)) {
            indicator.setVisibility(View.VISIBLE);
            indicator.setImageDrawable(viewHost.getResources().getDrawable(R.drawable.positive));
            indicator.setColorFilter(viewHost.getResources().getColor(R.color.colorBlack87));

            // Behind schedule indicator
        } else if (trackerHost.getProgressOnTrack().equals(Tracker.OnTrack.behind)) {
            indicator.setVisibility(View.VISIBLE);
            indicator.setImageDrawable(viewHost.getResources().getDrawable(R.drawable.negative));
            indicator.setColorFilter(viewHost.getResources().getColor(R.color.colorAccent));

            // Hide in all other cases
        } else {
            indicator.setVisibility(View.INVISIBLE);
        }
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
     * Sets the visible percentage number.
     * <p/>
     * The value is capped at 999.
     *
     * @param percentage Percentage number to display.
     */
    private void setPercentage(int percentage) {
        if (percentage > maxPercentage)
            percentage = maxPercentage;

        String text = percentage + "";
        ((TextView) (viewHost.findViewById(R.id.percentage_container)
                .findViewById(R.id.percentage))).setText(text);
    }

    /**
     * Updates ProgressBar during animation.
     *
     * @param progress The percentage of progress in decimals. 1 == 100%
     */
    private void updateProgressBar(float progress) {
        ProgressBar progressBar = (ProgressBar) viewHost.findViewById(R.id.progressbar);
        progressBar.setProgress((int) (progress * progressBar.getMax()));
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
