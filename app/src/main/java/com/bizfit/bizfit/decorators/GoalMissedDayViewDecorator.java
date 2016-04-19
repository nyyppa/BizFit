package com.bizfit.bizfit.decorators;

import android.content.Context;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.spans.CustomTypefaceSpan;
import com.bizfit.bizfit.utils.AssetManagerOur;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarUtils;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

/**
 * Applies Spans to specified CalendarDays
 *
 * Currently applies decorations to all dates bu today. Tracker does not
 * yet provide functionality to check on which dates goal was met.
 */
public class GoalMissedDayViewDecorator implements DayViewDecorator {

    /**
     * Day which should NOT be decorated.
     */
    private CalendarDay today;

    private Context context;

    public GoalMissedDayViewDecorator(Context context) {
        this.context = context;
        this.today = CalendarDay.from(CalendarUtils.getInstance());
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return !(today.getDate().getTime() == day.getDate().getTime());
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new CustomTypefaceSpan(AssetManagerOur.getFont(AssetManagerOur.regular)));
        view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.calendar_daily_target_missed));
    }
}
