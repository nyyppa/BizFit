package com.bizfit.bizfit.views;

import android.content.Context;

import com.bizfit.bizfit.DailyProgress;
import com.bizfit.bizfit.R;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.utils.AssetManagerOur;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.MutableDateTime;

import java.util.ArrayList;

/**
 *
 */
public class CustomBarChart extends BarChart {

    /**
     * Tracker from which data is pulled from.
     */
    private Tracker host;

    /**
     * Data from the Tracker.
     */
    private BarDataSet dataSet;

    /**
     * X - axis labels. Ranges from startDate to today.
     */
    private ArrayList<String> xValues;

    /**
     * X - axis  minimum value.
     */
    private DateTime startDate;

    /**
     * X - axis maximum value.
     */
    private DateTime today;

    public CustomBarChart(Context context, Tracker host) {
        super(context);
        setTracker(host);
        update();
    }

    public void stylize() {
        getLegend().setEnabled(false);
        setNoDataText(getResources().getString(R.string.insufficent_data));
        setDescription("");
        setScaleYEnabled(false);
        setDrawGridBackground(false);
        getViewPortHandler().setMaximumScaleX(20);


        XAxis xAxis = getXAxis();
        xAxis.setDrawLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineColor(getResources().getColor(R.color.colorBlack87));
        xAxis.setAxisLineWidth(getResources().getInteger(R.integer.x_axis_height));
        xAxis.setTextColor(getResources().getColor(R.color.colorBlack57));

        YAxis right = getAxisRight();
        right.setEnabled(false);

        YAxis left = getAxisLeft();
        left.setTypeface(AssetManagerOur.getFont(AssetManagerOur.regular));
        left.setTextSize(getResources().getInteger(R.integer.text_caption));
        left.setDrawAxisLine(false);
        left.setYOffset(getResources().getInteger(R.integer.y_axis_label_y_offset));
        left.setTextColor(getResources().getColor(R.color.colorBlack87));
        left.setGridColor(getResources().getColor(R.color.colorBlack87));
        left.setAxisLineColor(getResources().getColor(R.color.colorBlack87));
        left.setStartAtZero(true);
        left.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        left.setAxisLineWidth(getResources().getInteger(R.integer.x_axis_height));

        dataSet.setDrawValues(false);
        dataSet.setColor(host.getColor());
        //dataSet.setHighLightColor(getResources().getColor(R.color.white));
        dataSet.setBarSpacePercent(80f);

        mInfoPaint.setColor(getResources().getColor(R.color.colorBlack87));
        mInfoPaint.setTextSize(getResources().getDimension(R.dimen.text_subheading));
    }

    /**
     * Pulls data from the Tracker used to create x axis values.
     * <p/>
     * Used as x - axis labels.
     *
     * @return ArrayList containing x axis values.
     */
    private void pullDataFromTracker() {
        // Contains information of the item being tracked.
        DailyProgress.DayPool[] data = host.getAllDayPools();

        // X - axis labels.
        xValues = new ArrayList<>();

        // Total number of days.
        int numberOfDays = Days.daysBetween(startDate.withTimeAtStartOfDay()
                , today.withTimeAtStartOfDay()).getDays();

        // Used in assigning a label the x - axis items.
        MutableDateTime date = startDate.toMutableDateTime();

        // Look up index
        int dayPoolIndex = data.length - 1;

        // Total progress
        int progress = 0;

        // Represents that date of the DayPool object at dayPoolIndex.
        MutableDateTime dataTime = new MutableDateTime();

        // Has all the data been iterated through.
        boolean outOfData;

        ArrayList<BarEntry> pulledData = new ArrayList<>();

        // Pulls and indexes data from Tracker.
        // Labels the x - axis points.
        for (int i = 0; i <= numberOfDays; i++) {
            xValues.add(date.dayOfMonth().get() + "." + date.monthOfYear().get());

            outOfData = (dayPoolIndex < 0);
            if (!outOfData) {
                dataTime.setDate(data[dayPoolIndex].getTime());

                if (sameDate(dataTime, date)) {
                    pulledData.add(new BarEntry((int) data[dayPoolIndex].getTotalAmount(), i));
                    dayPoolIndex--;
                } else {
                    pulledData.add(new BarEntry(0, i));
                }
            } else {
                pulledData.add(new BarEntry(0, i));
            }

            date.addDays(1);
        }


        dataSet = new BarDataSet(pulledData, "");
    }

    /**
     * Checks if two dates are the same.
     *
     * @param date1 Date to check against.
     * @param date2 Date to check against.
     * @return If time's correspond to the same date.
     */
    private boolean sameDate(MutableDateTime date1, MutableDateTime date2) {
        boolean matchingYears = (date1.getYear() == date2.getYear());
        boolean mathcingMonths = (date1.getMonthOfYear() == date2.getMonthOfYear());
        boolean matchingDays = (date1.getDayOfMonth() == date2.getDayOfMonth());

        return matchingYears && mathcingMonths && matchingDays;
    }

    /**
     * Set's the Tracker from which data is pulled from.
     *
     * @param host Tracker with data.
     */
    public void setTracker(Tracker host) {
        this.host = host;
    }

    /**
     * Gives the LineData to the  superclass to play with.
     */
    private void addDataToSuperClass() {
        ArrayList<BarDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);
        BarData data = new BarData(xValues, dataSets);
        super.setData(data);
    }

    /**
     * Updates the drawing field.
     */
    public void update() {
        startDate = new DateTime(host.getStartDateMillis());
        today = new DateTime();
        pullDataFromTracker();
        stylize();
        addDataToSuperClass();
        invalidate();
    }

    /**
     * Gets an array containing the x - axis labels.
     *
     * @return X - axis labels.
     */
    public ArrayList<String> getxValues() {
        return xValues;
    }

}
