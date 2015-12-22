package com.bizfit.bizfit.utils;

import android.graphics.Typeface;
import android.net.Uri;

import com.bizfit.bizfit.utils.FontHolder;

import java.util.ArrayList;
import java.util.List;

import com.bizfit.bizfit.activities.MainActivity;

/**
 * Created by Käyttäjä on 22.12.2015.
 */
public class AssetManagerOur {
    public static final String robotoBlack = "Roboto-Black.ttf";
    public static final String robotoBold = "Roboto-Bold.ttf";
    public static final String robotoMedium = "Roboto-Medium.ttf";
    public static final String robotoLight = "Roboto-Light.ttf";

    private static List<FontHolder> fonts = new ArrayList<FontHolder>(0);

    public static Typeface getFont(String file) {
        for (int i = 0; i < fonts.size(); i++) {
            if (fonts.get(i).file.equals(file)) {
                return fonts.get(i).font;
            }
        }

        Typeface font = Typeface.createFromAsset(MainActivity.activity.getResources().getAssets(), file);
        FontHolder holder = new FontHolder(font, file);
        fonts.add(holder);
        return holder.font;
    }
}
