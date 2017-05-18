package com.bizfit.bizfitUusYritysKeskusAlpha.utils;

import android.graphics.Typeface;

/**
 * Contains a true type font.
 */
public class FontHolder {
    /**
     * True type font.
     */
    Typeface font;

    /**
     * Font path.
     */
    String file;

    FontHolder(Typeface font, String file) {
        this.font = font;
        this.file = file;
    }
}
