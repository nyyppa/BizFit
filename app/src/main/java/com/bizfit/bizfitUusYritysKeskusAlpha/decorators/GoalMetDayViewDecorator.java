package com.bizfit.bizfitUusYritysKeskusAlpha.decorators;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import com.bizfit.bizfitUusYritysKeskusAlpha.R;
import com.bizfit.bizfitUusYritysKeskusAlpha.spans.CustomTypefaceSpan;
import com.bizfit.bizfitUusYritysKeskusAlpha.utils.AssetManagerOur;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarUtils;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.List;

/**
 * Decorates calendar days on which daily target was met.
 */
public class GoalMetDayViewDecorator implements DayViewDecorator {

    /**
     * Day which should NOT be decorated. Only a placeholder for testing purposes.
     * Will be deleted in a future release.
     */
    private CalendarDay today;

    /**
     * Context in which the Decorator operates in.
     */
    private Context context;

    /**
     * List of dates that need to be decorated.
     */
    private List<CalendarDay> days;

    /**
     * Decorated text color.
     */
    private int color;

    public GoalMetDayViewDecorator(Context context, List days, int color) {
        this.context = context;
        this.today = CalendarDay.from(CalendarUtils.getInstance());
        this.days = days;
        this.color = color;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        // TODO Placeholder. Proper check needed.
        return !(today.getDate().getTime() > day.getDate().getTime() &&
                days.contains(day));
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new CustomTypefaceSpan(AssetManagerOur.getFont(AssetManagerOur.regular, context)));
        Drawable bg = context.getResources().getDrawable(R.drawable.calendar_daily_target_met);
        bg.setColorFilter(color, PorterDuff.Mode.SRC_OVER);
        view.setBackgroundDrawable(bg);
    }
}
