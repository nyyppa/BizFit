package com.bizfit.bizfit.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Alters the scrolling behaviour of otherwise standard RecyclerView.
 *
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

    @Override
    public boolean fling(int velocityX, int velocityY) {

        if (Math.abs(velocityX) >= MAX_VELOCITY) {
            // Retaing sign but reduce to velocity to match defined limits.
            velocityX = (int) Math.signum(velocityX) * MAX_VELOCITY;
        } else {
            velocityX *= FLING_FRICTION;
        }
        Log.d(CustomRecyclerView.class.getName(), "velocityX: " + velocityX);
        return super.fling(velocityX, velocityY);
    }
}
