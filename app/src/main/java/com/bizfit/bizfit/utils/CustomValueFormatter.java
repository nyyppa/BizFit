package com.bizfit.bizfit.utils;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Rounds chart field values to the closest whole int value.
 *
 * Used to maintain a unified look across the graphs.
 */
public class CustomValueFormatter implements ValueFormatter {

    @Override
    public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
        return Math.round(v)+"";
    }
}
