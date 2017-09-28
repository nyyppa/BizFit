package com.bizfit.release.views;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Attempts to fix a bug int the NestedScrollView.
 * <p/>
 * Bug occurs when the NestedScrollView is flung and the scroll reaches
 * the end / start. The next touch is then intercepted even when the view
 * is not scrolling.
 */
public class WorkaroundNestedScrollView extends NestedScrollView {

    public WorkaroundNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            computeScroll();
        }
        return super.onInterceptTouchEvent(ev);
    }
}