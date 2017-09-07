package com.bizfit.bizfit.decorators;

import android.content.Context;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.spans.CustomTypefaceSpan;
import com.bizfit.bizfit.utils.AssetManagerOur;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Calendar;

/**
 * Created by Atte Ylivrronen on 5.11.2016.
 */
public class EndAndStartDayViewDecorator implements DayViewDecorator {
    /**
     * Day to decorate.
     */
    private CalendarDay today;

    /**
     * Context in which the Decorator operates in.
     */
    private Context context;

    public EndAndStartDayViewDecorator(Context context, Calendar calendar) {
        this.context = context;
        this.today=CalendarDay.from(calendar);
        //this.today = CalendarDay.from(CalendarUtils.getInstance());
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return today.getDate().getTime() == day.getDate().getTime();
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new CustomTypefaceSpan(
                AssetManagerOur.getFont(AssetManagerOur.medium, context)
                , context.getResources().getColor(R.color.black87)));
        view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.calendar_daily_target_met));
    }
}
