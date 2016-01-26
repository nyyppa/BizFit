package com.bizfit.bizfit.utils;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.renderer.YAxisRenderer;
import com.github.mikephil.charting.utils.PointD;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Custom renderer for line graph.
 *
 * Still in the works.
 */
public class YAxisRendererCustom extends YAxisRenderer {

    private boolean customPaintEnabled;

    public YAxisRendererCustom(ViewPortHandler viewPortHandler, YAxis yAxis, Transformer trans) {
        super(viewPortHandler, yAxis, trans);
        customPaintEnabled = true;
    }


    /**
     * Computes the axis values.
     *
     * @param yMin - the minimum y-value in the data object for this axis
     * @param yMax - the maximum y-value in the data object for this axis
     */
    @Override
    public void computeAxis(float yMin, float yMax) {

        // calculate the starting and entry point of the y-labels (depending on
        // zoom / contentrect bounds)
        if (mViewPortHandler.contentWidth() > 10 && !mViewPortHandler.isFullyZoomedOutY()) {

            PointD p1 = mTrans.getValuesByTouchPoint(mViewPortHandler.contentLeft(), mViewPortHandler.contentTop());
            PointD p2 = mTrans.getValuesByTouchPoint(mViewPortHandler.contentLeft(), mViewPortHandler.contentBottom());

            if (!mYAxis.isInverted()) {
                yMin = (float) p2.y;
                yMax = (float) p1.y;
            } else {

                yMin = (float) p1.y;
                yMax = (float) p2.y;
            }
        }

        computeAxisValues(yMin, yMax);
    }

    /**
     * Sets up the y-axis labels. Computes the desired number of labels between the two given extremes. Unlike the
     * papareXLabels() method, this method needs to be called upon every refresh of the view.
     *
     * @return
     */
    @Override
    protected void computeAxisValues(float min, float max) {

        float yMin = min;
        float yMax = max;

        if(customPaintEnabled) {

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
