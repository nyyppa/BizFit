package com.bizfit.bizfit.views;

import android.content.Context;

import com.bizfit.bizfit.DailyProgress;
import com.bizfit.bizfit.R;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.utils.AssetManagerOur;
import com.bizfit.bizfit.utils.CustomYAxisRendererLine;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.MutableDateTime;

import java.util.ArrayList;

/**
 * Displays an accumulative chart of total progress over time.
 * <p/>
 * TODO Better fetching for colors. Current method decrapted since API 23.
 */
public class CustomLineChart extends LineChart {

    /**
     * Tracker from which data is pulled from.
     */
    private Tracker host;

    /**
     * Data from the Tracker.
     */
    private LineDataSet dataSet;

    /**
     * X - axis labels. Ranges from startDate to endDate.
     */
    private ArrayList<String> xValues;

    /**
     * X - axis  minimum value.
     */
    private DateTime startDate;

    /**
     * X - axis maximum value.
     */
    private DateTime endDate;

    public CustomLineChart(Context context, Tracker host) {
        super(context);
        setTracker(host);
        update();
    }

    /**
     * Customizes the visuals.
     *
     * Calls addDataToSuperclass() as customizing the viewport requires
     * data to be set.
     */
    public void stylize() {
        getLegend().setEnabled(false);
        setNoDataText(getResources().getString(R.string.insufficent_data));
        setDescription("");
        setScaleYEnabled(false);
        setDrawGridBackground(false);

        XAxis xAxis = getXAxis();
        xAxis.setDrawLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineColor(getResources().getColor(R.color.white));
        xAxis.setAxisLineWidth(getResources().getInteger(R.integer.x_axis_height));
        xAxis.setTextColor(getResources().getColor(R.color.white));

        YAxis right = getAxisRight();
        right.setEnabled(false);

        YAxis left = getAxisLeft();
        left.setTypeface(AssetManagerOur.getFont(AssetManagerOur.regular));
        left.setTextSize(getResources().getInteger(R.integer.text_caption));
        left.setDrawAxisLine(false);
        left.setYOffset(getResources().getInteger(R.integer.y_axis_label_y_offset));
        left.setTextColor(getResources().getColor(R.color.white));
        left.setGridColor(getResources().getColor(R.color.white70));
        left.setAxisLineColor(getResources().getColor(R.color.white70));
        left.setStartAtZero(true);
        left.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        left.setAxisLineWidth(getResources().getInteger(R.integer.x_axis_height));
        left.setAxisMaxValue(host.getTargetProgress() < host.getCurrentProgress() ?
                host.getCurrentProgress() * 1.1f : host.getTargetProgress() * 1.1f);

        dataSet.setDrawValues(false);
        dataSet.setColor(getResources().getColor(R.color.white));
        dataSet.setCircleColor(getResources().getColor(R.color.white));
        dataSet.setCircleSize(getResources().getInteger(R.integer.graph_circle_size));
        dataSet.setLineWidth(getResources().getInteger(R.integer.total_progress_graph_line_width));
        dataSet.setDrawCubic(true);
        dataSet.setHighLightColor(getResources().getColor(R.color.white));

        mInfoPaint.setColor(getResources().getColor(R.color.white));
        mInfoPaint.setTextSize(getResources().getDimension(R.dimen.text_subheading));

        addDataToSuperClass();

        getViewPortHandler().setMaximumScaleX(xValues.size());
        setRendererLeftYAxis(new CustomYAxisRendererLine(
                getViewPortHandler()
                , getAxisLeft()
                , getTransformer(YAxis.AxisDependency.LEFT)
                , host
        ));
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

        // Total amount of progress for each date is added here.
        ArrayList<Entry> formattedData = new ArrayList<>();

        // X - axis labels.
        xValues = new ArrayList<>();

        // Total number of days.
        int numberOfDays = Days.daysBetween(startDate.withTimeAtStartOfDay(), endDate.withTimeAtStartOfDay()).getDays();

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

        // Pulls data and indexes data from Tracker.
        // Labels the x - axis points.
        for (int i = 0; i <= numberOfDays; i++) {
            xValues.add(date.dayOfMonth().get() + "." + date.monthOfYear().get());
            outOfData = (dayPoolIndex < 0);

            if (!outOfData) {
                dataTime.setDate(data[dayPoolIndex].getTime());

                if (sameDate(dataTime, date)) {
                    progress += data[dayPoolIndex].getTotalAmount();
                    dayPoolIndex--;
                }

                formattedData.add(i
                        , new Entry(progress, i));
            }

            date.addDays(1);
        }


        dataSet = new LineDataSet(formattedData, "");
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
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
        ArrayList<LineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);
        LineData data = new LineData(xValues, dataSets);
        super.setData(data);
    }

    /**
     * Updates the drawing field.
     */
    public void update() {
        startDate = new DateTime(host.getStartDateMillis());
        endDate = new DateTime(host.getEndDateMillis());
        pullDataFromTracker();
        stylize();
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
