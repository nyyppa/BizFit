package com.bizfit.bizfit.utils;

import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.bizfit.bizfit.R;

/**
 * Animates the CardView to have a touch effect.
 */
public class CardViewAnimator implements View.OnTouchListener {
    /**
     * Attribute name which is being animated.
     */
    private static final String ATTRIBUTE = "cardElevation";
    /**
     * CardView elevation for when it is not being interacted with.
     */
    private static float mRestElevation;
    /**
     * CardView elevation for when it is being pressed.
     */
    private static float mTargetElevation;
    /**
     * Duration of descend and ascend animation.
     */
    private static int mAnimDuration;
    /**
     * Has the necessary values been fetched.
     */
    private static boolean mResourcesFetched = false;
    /**
     * Animator for ascend animation.
     */
    private ObjectAnimator ascend;
    /**
     * Animator for descend animation.
     */
    private ObjectAnimator descend;

    /**
     * @param cardView View to animate.
     */
    public CardViewAnimator(CardView cardView) {

        if (!mResourcesFetched)
            fetchResources(cardView.getContext().getResources());

        ascend = ObjectAnimator.ofFloat(cardView
                , ATTRIBUTE
                , mRestElevation
                , mTargetElevation)
                .setDuration(mAnimDuration);

        descend = ObjectAnimator.ofFloat(cardView
                , ATTRIBUTE
                , mTargetElevation
                , mRestElevation)
                .setDuration(mAnimDuration);
    }

    /**
     * Gets the necessary resources for animation.
     *
     * @param mContext Context in which the animation operates in.
     */
    private void fetchResources(Resources mContext) {
        Log.d(CardViewAnimator.class.getName(), "Called!");
        mRestElevation = mContext.getDimension(R.dimen.button_resting_elevation);
        mTargetElevation = mContext.getDimension(R.dimen.button_elevated_elevation);
        mAnimDuration = mContext.getInteger(R.integer.button_animation_duration);
        mResourcesFetched = true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ascend.start();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                descend.start();
                break;
        }
        return false;
    }
}
