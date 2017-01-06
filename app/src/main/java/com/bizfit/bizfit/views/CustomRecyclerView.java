package com.bizfit.bizfit.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Alters the scrolling behaviour of otherwise standard RecyclerView.
 * <p/>
 * Horizontal scrolling speed has a set velocity limit as well as higher
 * friction than standard RecyclerView.
 */
public class CustomRecyclerView extends RecyclerView {

    /**
     * Rate at which speed diminishes after a fling.
     */
    private static final float FLING_FRICTION = 0.7f;

    /**
     * Maximum velocity of scroll. May need revising for better  consistency
     * across different devices.
     */
    private static final int MAX_VELOCITY = 10000;

    public CustomRecyclerView(Context context) {
        super(context);
    }

    public CustomRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Limits max speed and applies friction before super.fling() is called.
     *
     * @param velocityX Initial horizontal velocity in pixels per second.
     * @param velocityY Initial vertical velocity in pixels per second
     * @return true if the fling was started, false if the velocity was
     * too low to fling or LayoutManager does not support scrolling in
     * the axis fling is issued.
     */
    @Override
    public boolean fling(int velocityX, int velocityY) {

        if (Math.abs(velocityX) >= MAX_VELOCITY) {
            // Retain sign but reduce velocity to match defined limits.
            velocityX = (int) Math.signum(velocityX) * MAX_VELOCITY;
        } else {
            velocityX *= FLING_FRICTION;
        }

        return super.fling(velocityX, velocityY);
    }
}
