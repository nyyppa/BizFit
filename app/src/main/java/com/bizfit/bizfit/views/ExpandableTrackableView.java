package com.bizfit.bizfit.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.utils.AssetManagerOur;

public class ExpandableTrackableView extends FrameLayout {

    private Tracker tracker;
    private TextView trackerName;
    private TextView targetAmount;
    private TextView timeLeftAmount;
    private TextView progressPercent;

    private boolean expanded;
    private FrameLayout totalProgressContainer;
    private FrameLayout dailyProgressContainer;
    private LinearLayout infoContainer;

    public ExpandableTrackableView(Context context) {
        super(context);
        init();
    }

    public ExpandableTrackableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExpandableTrackableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public ExpandableTrackableView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private void init() {
        if (!isInEditMode()) {
            inflate(getContext(), R.layout.view_trackable_expandable, this);
            this.trackerName = (TextView) findViewById(R.id.tracker_name);
            this.targetAmount = (TextView) findViewById(R.id.target_amount);
            this.timeLeftAmount = (TextView) findViewById(R.id.time_left_amount);
            this.progressPercent = (TextView) findViewById(R.id.progress_percentage);

            trackerName.setTypeface(AssetManagerOur.getFont(AssetManagerOur.medium));
            targetAmount.setTypeface(AssetManagerOur.getFont(AssetManagerOur.regular));
            timeLeftAmount.setTypeface(AssetManagerOur.getFont(AssetManagerOur.regular));
            progressPercent.setTypeface(AssetManagerOur.getFont(AssetManagerOur.regular));
            ((TextView) findViewById(R.id.target_label)).setTypeface(AssetManagerOur.getFont(AssetManagerOur.medium));
            ((TextView) findViewById(R.id.time_left_label)).setTypeface(AssetManagerOur.getFont(AssetManagerOur.medium));

            expanded = false;
            totalProgressContainer = (FrameLayout)findViewById(R.id.total_progress_container);
            dailyProgressContainer = (FrameLayout)findViewById(R.id.daily_progress_container);
            infoContainer = (LinearLayout) findViewById(R.id.top_container);

            findViewById(R.id.card_view).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (!expanded) {
                        expand(totalProgressContainer);
                        expand(dailyProgressContainer);
                        collapse(infoContainer);
                    } else {
                        collapse(totalProgressContainer);
                        collapse(dailyProgressContainer);
                        expand(infoContainer);
                    }

                    expanded = !expanded;
                }
            });
        }
    }

    public static void expand(final View v) {
        v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }
}
