package com.bizfit.bizfit.decorators;

import android.content.Context;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.spans.CustomTypefaceSpan;
import com.bizfit.bizfit.utils.AssetManagerOur;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarUtils;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.List;

/**
 * Created by Käyttäjä on 18.4.2016.
 */
public class GoalMetDayViewDecorator implements DayViewDecorator {

    /**
     * Day which should NOT be decorated.
     */
    private CalendarDay today;

    private Context context;

    private List<CalendarDay> days;

    public GoalMetDayViewDecorator(Context context, List days) {
        this.context = context;
        this.today = CalendarDay.from(CalendarUtils.getInstance());
        this.days = days;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        // TODO Placeholder. Proper check needed.
        return !(today.getDate().getTime() > day.getDate().getTime() &&
                days.contains(day));
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new CustomTypefaceSpan(AssetManagerOur.getFont(AssetManagerOur.regular)));
        view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.calendar_daily_target_met));
    }
}
