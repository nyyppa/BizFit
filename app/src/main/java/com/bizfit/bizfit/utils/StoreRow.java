package com.bizfit.bizfit.utils;

import android.media.Image;

import java.util.List;

/**
 * Stores all the necessary information for a single store row.
 *
 * TODO Better and more flexible data structure. As of now, only a placeholder.
 */
public class StoreRow {
    protected String title;
    protected String subTitle;
    protected List<StoreItem> items;

    public StoreRow(String title, String subTitle, List<StoreItem> items) {
        this.title = title;
        this.subTitle = subTitle;
        this.items = items;
    }

    public static class StoreItem {
        protected String name;
        protected Image image;

        public StoreItem(String name) {
            this.name = name;
        }
    }
}
