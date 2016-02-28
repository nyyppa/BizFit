package com.bizfit.bizfit.views;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.utils.AssetManagerOur;

import java.util.Random;

/**
 *
 */
class TrackableViewAlternative extends TrackableViewBase {

    /**
     * Views custom color. Reflects the type of item being tracked. Also
     * affects the style of view tracker activity.
     */
    int color;

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
        viewHost.invalidate();
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
     * progress indicators should be displayed, Tracker NAME, time left,
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



        // Graphical indicator of the current progress.
        ProgressBar progressBar = (ProgressBar) viewHost.findViewById(R.id.progressbar);

        // number_of_bar_steps effectively defines the smoothness of the animation.
        progressBar.setMax(R.integer.number_of_bar_steps);

        // Custom progressbar graph.
        final Drawable drawable;
        int sdk = android.os.Build.VERSION.SDK_INT;

        // Check which sdk is in use.
        if (sdk < 16) {
            drawable = progressBar.getContext().getResources().getDrawable(R.drawable.my_progressbar);
        } else {
            drawable = ContextCompat.getDrawable(progressBar.getContext(), R.drawable.my_progressbar);
        }

        color = trackerHost.getColor();

        // Sets all the colors which change dynamically.
        viewHost.findViewById(R.id.percentage_container).setBackgroundColor(color);
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);

        // Set the progressBarDrawable to be the custom one.
        progressBar.setProgressDrawable(drawable);
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
