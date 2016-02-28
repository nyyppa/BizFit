package com.bizfit.bizfit.utils;

/**
 * Stores NAME space used in cross activity data exchange.
 * <p/>
 * When Intent is returned from one activity to another, these String values
 * represent the keywords used to store data within that Intent.
 * <p/>
 * Creation of objects from this class is not possible, nor are these Strings
 * used to display text.
 */
public class FieldNames {
    public static final String TRACKERNAME = "NAME";
    public static final String TARGET = "target";
    public static final String TRACKER = "tracker";
    public static final String DAY = "day";
    public static final String MONTH = "month";
    public static final String YEAR = "year";
    public static final String RECURRING = "recurring";
    public static final String SCROLL_POS = "scrollposition";
    public static final String COLOR = "color";

    private FieldNames() {
    }
}
