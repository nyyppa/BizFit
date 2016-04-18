package com.bizfit.bizfit.decorators;

import com.bizfit.bizfit.spans.CustomTypefaceSpan;
import com.bizfit.bizfit.utils.AssetManagerOur;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarUtils;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

/**
 * Applies Spans to specified CalendarDays
 */
public class FontDecorator implements DayViewDecorator {

    /**
     * Day which should NOT be decorated.
     */
    private CalendarDay today;

    public FontDecorator() {
        this.today = CalendarDay.from(CalendarUtils.getInstance());
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return !(today.getDate().getTime() == day.getDate().getTime());
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new CustomTypefaceSpan(AssetManagerOur.getFont(AssetManagerOur.medium)));
    }
}
