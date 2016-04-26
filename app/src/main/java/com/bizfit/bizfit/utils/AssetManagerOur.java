package com.bizfit.bizfit.utils;

import android.graphics.Typeface;

import com.bizfit.bizfit.activities.MainPage;

import java.util.ArrayList;
import java.util.List;

/**
 * Loads and hold true type fonts from the assets folder.
 *
 * If the font you are looking for isn't listed here, add a new attribute
 * which contains the font path.
 */
public class AssetManagerOur {


    /**
     * Collection paths to different .ttf files.
     */
    public static final String boldCondense = "fonts/RobotoCondensed-Bold.ttf";
    public static final String regular = "fonts/Roboto-Regular.ttf";
    public static final String light = "fonts/Roboto-Light.ttf";
    public static final String bold = "fonts/Roboto-Bold.ttf";
    public static final String medium = "fonts/Roboto-Medium.ttf";
    public static final String thin = "fonts/Roboto-Thin.ttf";

    /**
     * List of currently loaded fonts.
     */
    private static List<FontHolder> fonts = new ArrayList<>(0);

    /**
     * Loads font from assets folder.
     *
     * @param file File path. One of the AssetManagerOur's public attributes
     *             should be given as an attribute.
     * @return True type font.
     */
    public static Typeface getFont(String file) {

        // Checks if the font is already loaded.
        for (int i = 0; i < fonts.size(); i++) {
            if (fonts.get(i).file.equals(file)) {
                return fonts.get(i).font;
            }
        }

        Typeface font = Typeface.createFromAsset(MainPage.activity.getAssets(), file);

        FontHolder holder = new FontHolder(font, file);
        fonts.add(holder);
        return holder.font;
    }
}
