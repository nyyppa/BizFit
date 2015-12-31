package com.bizfit.bizfit.utils;

/**
 * Stores name space used in cross activity data exchange.
 *
 * When Intent is returned from one activity to another, these String values
 * represent the keywords used to store data within that Intent.
 *
 * Creation of objects from this class is not possible.
 */
public class FieldNames {
    public static final String TRACKERNAME = "name";
    public static final String TARGET = "target";
    public static final String TRACKER = "tracker";

    private FieldNames() {}
}
