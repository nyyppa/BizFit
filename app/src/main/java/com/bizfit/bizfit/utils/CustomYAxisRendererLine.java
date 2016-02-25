package com.bizfit.bizfit.utils;

import com.bizfit.bizfit.Tracker;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.renderer.YAxisRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Custom renderer for line graph.
 * <p/>
 * Still in the works. Finished product should be displaying two horizontal
 * lines at tops. One for target progress. IF that is reached, display a second
 * line for current progress.
 */
public class CustomYAxisRendererLine extends YAxisRenderer {

    private boolean customPaintEnabled;
    private Tracker host;

    public CustomYAxisRendererLine(ViewPortHandler viewPortHandler, YAxis yAxis, Transformer trans, Tracker host) {
        super(viewPortHandler, yAxis, trans);
        setCustomPaintEnabled(true);
        this.host = host;
    }

    /**
     * Computes the axis values.
     *
     * @param yMin - the minimum y-value in the data object for this axis
     * @param yMax - the maximum y-value in the data object for this axis
     */
    public void computeAxis(float yMin, float yMax) {
        if (customPaintEnabled) {
            yMin = 0;
            yMax = host.getTargetProgress() < host.getCurrentProgress() ?
                    host.getCurrentProgress() : host.getTargetProgress();

            computeAxisValues(yMin, yMax);
        } else {
            super.computeAxis(yMin, yMax);
        }
    }

    /**
     * Sets up the y-axis labels. Computes the desired number of labels between
     * the two given extremes. Unlike the prepareXLabels() method, this method
     * needs to be called upon every refresh of the view.
     *
     * @return
     */
    @Override
    protected void computeAxisValues(float min, float max) {


        if (customPaintEnabled) {
            if (host.getTargetProgress() < host.getCurrentProgress()) {
                mYAxis.setAxisMaxValue(host.getCurrentProgress());
                mYAxis.mEntryCount = 2;
                mYAxis.mEntries = new float[2];
                mYAxis.mEntries[0] = host.getCurrentProgress();
                mYAxis.mEntries[1] = host.getTargetProgress();

            } else {
                mYAxis.mEntryCount = 1;
                mYAxis.mEntries = new float[]{host.getTargetProgress()};
            }

        } else {
            super.computeAxisValues(min, max);
        }

    }

    public boolean isCustomPaintEnabled() {
        return customPaintEnabled;
    }

    public void setCustomPaintEnabled(boolean customPaintEnabled) {
        this.customPaintEnabled = customPaintEnabled;
    }
}
