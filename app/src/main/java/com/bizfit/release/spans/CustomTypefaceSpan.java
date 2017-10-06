package com.bizfit.release.spans;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

/**
 * Is a test span for imported Calendar module.
 * <p/>
 * Alters the look of text components within the Calendar module.
 */
public class CustomTypefaceSpan extends TypefaceSpan {

    /**
     * The font family for this typeface.
     */
    private static final String FAMILY = "";

    /**
     * True type font used for this span.
     */
    private Typeface font;

    /**
     * Color used for this span.
     */
    private int color;

    /**
     * Has the Span been given a custom font.
     */
    private boolean fontSet = false;

    /**
     * Has the Span been given a custom color.
     */
    private boolean colorSet = false;

    /**
     * Creates a new Span with custom Typeface.
     *
     * @param font Font to be used in span.
     */
    public CustomTypefaceSpan(Typeface font) {
        super(FAMILY);
        this.font = font;
        fontSet = true;
    }

    /**
     * Creates a new Span with custom Typeface and color.
     *
     * @param font  Font to be used in span.
     * @param color Color to be used in span
     */
    public CustomTypefaceSpan(Typeface font, int color) {
        super(FAMILY);

        fontSet = true;
        this.font = font;

        colorSet = true;
        this.color = color;
    }

    @Override
    public void updateMeasureState(TextPaint paint) {
        if (fontSet)
            paint.setTypeface(font);
        if (colorSet)
            paint.setColor(color);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        if (fontSet)
            ds.setTypeface(font);
        if (colorSet)
            ds.setColor(color);
    }
}
