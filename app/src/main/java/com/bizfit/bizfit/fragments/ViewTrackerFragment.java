package com.bizfit.bizfit.fragments;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.decorators.FontDecorator;
import com.bizfit.bizfit.decorators.TodayDayViewDecorator;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Displays all the information associated with a tracker.
 */
public class ViewTrackerFragment extends Fragment implements Tracker.DataChangedListener {

    public static final String TRACKER = "TRACKER";

    /**
     * Tracker, from which relevant data is pulled from.
     */
    private Tracker tracker;

    /**
     * Is the required public empty constructor.
     */
    public ViewTrackerFragment() {}

    /**
     * Is the recommended way of creating a new instance of this Fragment.
     *
     * @param describable Arguments used to recreate this Fragment.
     * @return New instance of ViewTrackerFragment.
     */
    public static ViewTrackerFragment newInstance(Serializable describable) {
        ViewTrackerFragment fragment = new ViewTrackerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TRACKER, describable);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tracker = (Tracker) getArguments().getSerializable(TRACKER);
        tracker.setDataChangedListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragmet_view_tracker_content, container, false);
    }

    /**
     * Populates the layout with data pulled from Tracker.
     *
     * @param view The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle).
     * @param savedInstanceState  If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View root = getView();

        MaterialCalendarView mCalendar = (MaterialCalendarView) root.findViewById(R.id.calendarView);
        mCalendar.setSelectionMode(MaterialCalendarView.SELECTION_MODE_NONE);
        //mCalendar.setCalendarDisplayMode(CalendarMode.WEEKS);
        //mCalendar.setTopbarVisible(false);

        // Comment or uncomment depending on if week day labels should be visible.
        //mCalendar.setWeekDayLabels(new CharSequence[]{"", "", "", "", "", "", ""});

        mCalendar.setSelectionColor(getResources().getColor(R.color.grey_400));
        mCalendar.setCurrentDate(Calendar.getInstance());
        mCalendar.addDecorator(new TodayDayViewDecorator(getContext()));
        mCalendar.addDecorator(new FontDecorator());
        mCalendar.setSelectedDate(Calendar.getInstance());
        mCalendar.setShowOtherDates(MaterialCalendarView.SHOW_OUT_OF_RANGE);

        root.findViewById(R.id.space).setMinimumHeight(mCalendar.getHeight());


        // TODO pull colors from tracker
        mCalendar.setArrowColor(tracker.getColor());
    }

    @Override
    public void onResume() {
        super.onResume();
        // If theming changes, comment this line out.
        //toolAndStatusbarStylize(toolbar);
    }

    /**
     * Gets the Tracker associated with this Fragment.
     *
     * @return Tracker whose data is currently on display.
     */
    public Tracker getTracker() {
        if (tracker == null) {
            tracker = (Tracker) getArguments().getSerializable(TRACKER);
        }

        return tracker;
    }

    /**
     * Sets the Tracker associated with this fragment.
     *
     * @param host Tracker whose data is to be displayed.
     */
    public void setTracker(Tracker host) {
        this.tracker = host;
    }


    @Override
    public void dataChanged(Tracker tracker) {
        // TODO stuff.
    }
}
