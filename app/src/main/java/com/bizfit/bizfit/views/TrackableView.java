package com.bizfit.bizfit.views;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.activities.MainActivity;
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
class TrackableView extends TrackableViewBase{

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
        prepFunctionality();
    }

    /**
     * Sets information to View and does formatting which cant be done via xml.
     *
     * Formatting which can't be done via xml include, weather or not
     * progress indicators should be displayed, Tracker name, time left,
     * typefaces and dynamic padding. The last one is not implemented yet.
     */
    public void formatView() {

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
     * Sets the visible percentage number.
     * <p/>
     * The value is capped at 999.
     *
     * @param percentage Percentage number to display.
     */
    protected void setPercentage(int percentage) {
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
    @Override
    public void updateProgressBar(float progress) {
        ProgressBar progressBar = (ProgressBar) viewHost.findViewById(R.id.progressbar);
        progressBar.setProgress((int) (progress * progressBar.getMax()));
    }
}
