package com.bizfit.bizfit.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ViewPagerCustom extends ViewPager {

    private static final boolean allowSwipes = false;

    public ViewPagerCustom(Context context) {
        super(context);
    }

    public ViewPagerCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (allowSwipes) {
            return super.onInterceptTouchEvent(event);
        } else {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (allowSwipes) {
            return super.onTouchEvent(event);
        } else {
            return false;
        }
    }
}

