package com.bizfit.bizfit.fragments;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.decorators.EndAndStartDayViewDecorator;
import com.bizfit.bizfit.decorators.TodayDayViewDecorator;
import com.bizfit.bizfit.utils.OurDateTime;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Displays all the information associated with a tracker.
 */
public class ViewTrackerFragment extends Fragment implements Tracker.DataChangedListener, View.OnClickListener {

    /**
     * Key used to identify Tracker stored in a bundle.
     */
    public static final String TRACKER = "TRACKER";

    /**
     * Tracker, from which relevant data is pulled from.
     */
    private Tracker tracker;

    /**
     * Calendar which displays target success on daily basis.
     */
    private MaterialCalendarView mCalendar;

    /**
     * Circular progress bar that displays daily progress.
     */
    private ProgressBar mProgressBar;

    /**
     * Displays total accumulated progress.
     */
    private TextView mTotalProgress;

    /**
     * Today's progress.
     */
    private TextView mDailyProgress;

    /**
     * Today's target.
     */
    private TextView mTarget;

    /**
     * Time left until the goal expires.
     */
    private TextView mTimeLeft;

    /**
     * Name of the tracked goal.
     */
    private TextView mName;

    /**
     * Is the required public empty constructor.
     */
    public ViewTrackerFragment() {
    }

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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View root = getView();
        Resources resources = getResources();
        mCalendar = (MaterialCalendarView) root.findViewById(R.id.calendarView);
        mCalendar.setSelectionMode(MaterialCalendarView.SELECTION_MODE_NONE);
        mCalendar.setCurrentDate(Calendar.getInstance());
        mCalendar.addDecorator(new TodayDayViewDecorator(getContext()));
        mCalendar.addDecorator(new EndAndStartDayViewDecorator(getContext(),new OurDateTime(tracker.getStartDateMillis())));
        mCalendar.addDecorator(new EndAndStartDayViewDecorator(getContext(),new OurDateTime(tracker.getEndDateMillis())));
        // Tracker does not yet provide functionality to check if a goal  was met on
        // a specific date. Hence the pseudo code.
        //mCalendar.addDecorator(new GoalMissedDayViewDecorator(getContext()));
        //mCalendar.addDecorator(new GoalMetDayViewDecorator(*LIST OF DATES*, this, tracker.getColor()));
        //mCalendar.addDecorator(new DayInactiveDayViewDecorator(*LIST OF DATES*, this));
        mCalendar.setShowOtherDates(MaterialCalendarView.SHOW_OUT_OF_RANGE);

        mCalendar.setLeftArrowMask(resources.getDrawable(R.drawable.ic_chevron_left_black_24dp));
        mCalendar.setRightArrowMask(resources.getDrawable(R.drawable.ic_chevron_right_black_24dp));
        root.findViewById(R.id.button_today).setOnClickListener(this);
        root.findViewById(R.id.imageView).setOnClickListener(this);

        View mAddProgressBtn = root.findViewById(R.id.button_done);
        mAddProgressBtn.getBackground().setColorFilter(tracker.getColor(), PorterDuff.Mode.SRC_IN);
        mAddProgressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        mProgressBar = (ProgressBar) root.findViewById(R.id.progressBar_circle);
        mProgressBar.getProgressDrawable().setColorFilter(tracker.getColor(), PorterDuff.Mode.SRC_IN);
        mProgressBar.setProgress((int) (tracker.getCurrentProgress() / tracker.getTargetProgress() * mProgressBar.getMax()));

        mDailyProgress = (TextView) root.findViewById(R.id.textView_daily_progress);
        // TODO get todays progress
        mDailyProgress.setText(String.valueOf((int) tracker.getCurrentProgress()));

        TextView t= (TextView) root.findViewById(R.id.textView_daily_target);
        t.setText(String.valueOf(tracker.getDailyTarget()));

        mTotalProgress = (TextView) root.findViewById(R.id.textView_total_progress);
        mTotalProgress.setText(String.valueOf((int) tracker.getCurrentProgress()));

        mTimeLeft = (TextView) root.findViewById(R.id.textView_time_left);
        mTimeLeft.setText(tracker.getTimeRemaining().getTimeRemaining() + tracker.getTimeRemaining().getTimeType());

        mName = (TextView) root.findViewById(R.id.textView_name);
        mName.setText(tracker.getName());

        mTarget = (TextView) root.findViewById(R.id.textView_total_target);
        mTarget.setText(String.valueOf((int) tracker.getTargetProgress()));
    }

    @Override
    public void onResume() {
        super.onResume();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // Sets calendars currently displayed month to current month.
            case R.id.button_today:
                mCalendar.setCurrentDate(Calendar.getInstance());
                // Flashes current
                mCalendar.setSelectedDate(Calendar.getInstance());
                mCalendar.clearSelection();
                break;

            case R.id.imageView:
                PopupMenu popup = new PopupMenu(getContext(), v);
                popup.getMenuInflater().inflate(R.menu.menu_view_tracker,
                        popup.getMenu());
                popup.show();
                break;
        }
    }

    /**
     * Opens a dialogue for inputting progress.
     */
    private void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.title_amount_to_add));
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        builder.setView(input);
        builder.setPositiveButton(getString(R.string.action_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // TODO Progressbar sometimes does not animate fully.
                float progress = Float.parseFloat(input.getText().toString());
                float start = tracker.getCurrentProgress() / tracker.getTargetProgress();
                tracker.addProgress(progress);
                float end = tracker.getCurrentProgress() / tracker.getTargetProgress();

                mTotalProgress.setText(String.valueOf((int) tracker.getCurrentProgress()));
                mDailyProgress.setText(String.valueOf((int) tracker.getCurrentProgress()));

                ValueAnimator mAnimator = ValueAnimator.ofFloat(start, end);
                mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mProgressBar.setProgress((int) (mProgressBar.getMax() * ((Float) animation.getAnimatedValue()).floatValue()));
                    }
                });

                mAnimator.start();
            }
        });

        builder.setNegativeButton(getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }

        });

        builder.create().show();
    }
}
