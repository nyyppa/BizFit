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
 */
public class TodayDayViewDecorator implements DayViewDecorator {

    /**
     * Day which SHOULD be decorated.
     */
    private CalendarDay today;

    /**
     * Context in which action takes place.
     */
    private Context context;

    public TodayDayViewDecorator(Context context) {
        this.context = context;
        this.today = CalendarDay.from(CalendarUtils.getInstance());
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return today.getDate().getTime() == day.getDate().getTime();
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new CustomTypefaceSpan(
                AssetManagerOur.getFont(AssetManagerOur.medium)
                , context.getResources().getColor(R.color.black87)));
        view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.calendar_today_marker));
    }
}
