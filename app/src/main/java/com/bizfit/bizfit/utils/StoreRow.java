package com.bizfit.bizfit.utils;

import android.media.Image;

import java.util.List;


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
