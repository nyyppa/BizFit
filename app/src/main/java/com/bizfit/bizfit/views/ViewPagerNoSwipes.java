package com.bizfit.bizfit.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ViewPagerNoSwipes extends ViewPager {

    private static boolean allowSwipes = true;

    public ViewPagerNoSwipes(Context context) {
        super(context);
    }

    public ViewPagerNoSwipes(Context context, AttributeSet attrs) {
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

    public static boolean isAllowSwipes() {
        return allowSwipes;
    }

    public static void setAllowSwipes(boolean allowSwipes) {
        ViewPagerNoSwipes.allowSwipes = allowSwipes;
    }
}

