package com.bizfit.bizfit.utils;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Stores all the necessary information for a single store row.
 * <p/>
 * TODO Better and more flexible data structure.
 */
public class StoreRow<T> {

    /**
     * Key value used for HashMap.
     */
    public static final String TITLE = "title";

    /**
     * Contains String format data associated with this row.
     */
    protected HashMap<String, String> stringData;

    /**
     * Store row may have a background, but is not necessary.
     */
    protected Drawable background;

    /**
     * Store row may contain object type data.
     */
    protected List<T> items;

    public StoreRow() {
    }

    /**
     * @param stringData A set of String format data.
     * @param background Background used for this row.
     * @param items      A set of object type data.
     */
    public StoreRow(HashMap<String, String> stringData, Drawable background, List<T> items) {
        this.stringData = stringData;
        this.background = background;
        this.items = items;
    }

    /**
     * @param stringData A set of String format data.
     * @param items      A set of object type data.
     */
    public StoreRow(HashMap<String, String> stringData, List<T> items) {
        this.stringData = stringData;
        this.items = items;
    }

    /**
     * Gets an entry from a HashMap used to store String data.
     * <p/>
     * Regular .get(string); call on HashMap. This method exists
     * to reduce chaining of methods.
     *
     * @param key
     * @return A mapped String value if entry found. Null otherwise.
     */
    public String getString(String key) {
        return stringData.get(key);
    }

    /**
     * Gets the background drawable.
     *
     * @return Background drawable.
     */
    public Drawable getBackground() {
        return background;
    }

    /**
     * Sets the background drawable used for this row.
     *
     * @param background Background image.
     */
    public void setBackground(Drawable background) {
        this.background = background;
    }

    /**
     * Gets the set of object type data.
     *
     * @return Set of object type data.
     */
    public List<T> getItems() {
        return items;
    }

    /**
     * Sets the list of object type data.
     *
     * @param items New set of object type data.
     */
    public void setItems(List<T> items) {
        this.items = items;
    }

    /**
     * Gets the mapped String data.
     *
     * @return HashMap used to store String type data.
     */
    public HashMap<String, String> getStringData() {
        return stringData;
    }

    /**
     * Sets String type data.
     *
     * @param stringData New set of String type data.
     */
    public void setStringData(HashMap<String, String> stringData) {
        this.stringData = stringData;
    }

    /**
     * Represents a single coach in the store.
     */
    public static class StoreItem
    {
        protected String name;
        protected Drawable image;
        protected int imageId;
        protected int testimonials;
        protected String coachID;

        public StoreItem(String name, Drawable image, int imageId, int testimonials,String coachID)
        {
            this.image = image;
            this.name = name;
            this.imageId = imageId;
            this.testimonials = testimonials;
            this.coachID=coachID;
        }

        public String getName() {
            return name;
        }

        public int getImageId() {
            return imageId;
        }

        public String getCoachId(){
            return  coachID;
        }
    }
}
