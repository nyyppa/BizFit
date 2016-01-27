package com.bizfit.bizfit.utils;

import com.github.mikephil.charting.highlight.ChartHighlighter;
import com.github.mikephil.charting.interfaces.BarLineScatterCandleBubbleDataProvider;

/**
 * Controls which items in the chart can be high lighted.
 *
 * Will only be used for total progress chart. If the values should start from 0;
 */
public class CustomChartHighlighter extends ChartHighlighter {
    public CustomChartHighlighter(BarLineScatterCandleBubbleDataProvider chart) {
        super(chart);
    }


}
