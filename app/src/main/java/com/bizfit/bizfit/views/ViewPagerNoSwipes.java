package com.bizfit.bizfit.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Is a standard ViewPager but with an added possibility to disable paging.
 */
public class ViewPagerNoSwipes extends ViewPager {

    /**
     * Is paging enabled.
     */
    private boolean pagingEnabled = true;

    public ViewPagerNoSwipes(Context context) {
        super(context);
    }

    public ViewPagerNoSwipes(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (pagingEnabled) {
            return super.onInterceptTouchEvent(event);
        } else {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (pagingEnabled) {
            return super.onTouchEvent(event);
        } else {
            return false;
        }
    }

    /**
     * Returns whether or not paging is enabled.
     *
     * @return Can pages be changed by swiping.
     */
    public boolean isPagingEnabled() {
        return pagingEnabled;
    }

    /**
     * Sets paging.
     *
     * @param pagingEnabled Should pages be changed on swipe gesture.
     */
    public void setPagingEnabled(boolean pagingEnabled) {
        this.pagingEnabled = pagingEnabled;
    }
}

