package com.bizfit.bizfit.utils;

import android.graphics.Typeface;

/**
 * Contains a true type font.
 */
public class FontHolder {
        Typeface font;
        String file;

    FontHolder(Typeface font, String file){
            this.font=font;
            this.file=file;
    }
}
