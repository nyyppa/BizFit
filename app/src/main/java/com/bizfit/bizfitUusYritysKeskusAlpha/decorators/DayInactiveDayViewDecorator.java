package com.bizfit.bizfitUusYritysKeskusAlpha.decorators;

import android.content.Context;

import com.bizfit.bizfitUusYritysKeskusAlpha.R;
import com.bizfit.bizfitUusYritysKeskusAlpha.spans.CustomTypefaceSpan;
import com.bizfit.bizfitUusYritysKeskusAlpha.utils.AssetManagerOur;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.List;

/**
 * Decorates calendar days which are inactive.
 * <p/>
 * Inactive days are days which the user chose not to track a certain
 * goal, as well as days which are beyond the deadline or before the
 * start date.
 */
public class DayInactiveDayViewDecorator implements DayViewDecorator {

    /**
     * List of dates that are inacitve.
     */
    List<CalendarDay> days;

    /**
     * Context used to access res folder.
     */
    Context context;

    public DayInactiveDayViewDecorator(List<CalendarDay> days, Context context) {
        this.days = days;
        this.context = context;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        // TODO Placeholder. Proper check needed.
        return days.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new CustomTypefaceSpan(
                AssetManagerOur.getFont(AssetManagerOur.regular, context)
                , context.getResources().getColor(R.color.black37)
        ));
    }
}
