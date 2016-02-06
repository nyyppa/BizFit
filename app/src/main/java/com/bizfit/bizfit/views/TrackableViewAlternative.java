package com.bizfit.bizfit.views;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.utils.AssetManagerOur;

/**
 *
 */
class TrackableViewAlternative extends TrackableViewBase {
    /**
     * Creates and stylizes a new TrackableView.
     *
     * @param trackerHost Tracker from which data is pulled from.
     * @param viewHost    View which displays information.
     */
    public TrackableViewAlternative(Tracker trackerHost, View viewHost) {
        this.trackerHost = trackerHost;
        this.viewHost = viewHost;
        formatView();
        prepFunctionality();
    }


    /**
     * Sets the visible percentage number.
     * <p/>
     * The value is capped at 999.
     *
     * @param percentage Percentage number to display.
     */
    @Override
    public void setPercentage(int percentage) {
        if (percentage > maxPercentage)
            percentage = maxPercentage;

        String text = percentage + "";
        ((TextView) (viewHost.findViewById(R.id.content_container)
                .findViewById(R.id.percentage_container)
                .findViewById(R.id.percentage))).setText(text);
    }

    /**
     * Sets information to View and does formatting which cant be done via xml.
     * <p/>
     * Formatting which can't be done via xml include, weather or not
     * progress indicators should be displayed, Tracker name, time left,
     * typefaces and dynamic padding. The last one is not implemented yet.
     */
    public void formatView() {

        // All the text element within the view.
        TextView label = (TextView) (viewHost.findViewById(R.id.label));
        TextView timeLeft = (TextView) (viewHost.findViewById(R.id.time_left));
        TextView percentage = (TextView) (viewHost.findViewById(R.id.percentage));

        // Sets correct typefaces.
        label.setTypeface(AssetManagerOur.getFont(AssetManagerOur.light));
        timeLeft.setTypeface(AssetManagerOur.getFont(AssetManagerOur.light));
        percentage.setTypeface(AssetManagerOur.getFont(AssetManagerOur.light));


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
