package com.bizfit.bizfit.views;

import android.content.Context;

import com.bizfit.bizfit.DailyProgress;
import com.bizfit.bizfit.R;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.utils.AssetManagerOur;
import com.bizfit.bizfit.utils.YAxisRendererCustom;

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
 *
 */
public class CustomLineChart extends LineChart {

    Tracker host;
    LineDataSet dataSet;
    ArrayList<String> xValues;
    DateTime startDate;
    DateTime endDate;

    public CustomLineChart(Context context, Tracker host) {
        super(context);
        setTracker(host);
        pullDataFromTracker();
        stylize();
        addDataToSuperClass();
    }

    /**
     * Customizes the visuals.
     */
    public void stylize() {
        getLegend().setEnabled(false);
        setNoDataText(getResources().getString(R.string.insufficent_data));
        setDescription("");
        setScaleYEnabled(false);
        setDrawGridBackground(false);
        setRendererLeftYAxis(new YAxisRendererCustom(
                getViewPortHandler()
                , getAxisLeft()
                , getTransformer(YAxis.AxisDependency.LEFT)
                , host
        ));

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
        left.setAxisMaxValue(host.getTargetProgress() < host.getCurrentProgress() ?
                host.getCurrentProgress() : host.getTargetProgress());

        dataSet.setDrawValues(false);
        dataSet.setColor(getResources().getColor(R.color.white));
        dataSet.setCircleColor(getResources().getColor(R.color.white));
        // TODO dehardcode this
        dataSet.setCircleSize(4);
        dataSet.setLineWidth(getResources().getInteger(R.integer.total_progress_graph_line_width));

        mInfoPaint.setColor(getResources().getColor(R.color.white));
        mInfoPaint.setTextSize(getResources().getDimension(R.dimen.text_subheading));
    }


    /**
     * Pulls data from the Tracker used to create x axis values.
     *
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

        int dayPoolIndex = 0;
        int progress = 0;
        MutableDateTime dataTime = new MutableDateTime();
        boolean outOfData;

        for (int i = 0; i <= numberOfDays; i++) {
            xValues.add(date.dayOfMonth().get() + "." + date.monthOfYear().get());

            outOfData = (data.length <= dayPoolIndex);

            if (!outOfData) {
                dataTime.setTime(data[dayPoolIndex].getTime());

                if (sameDate(dataTime, date)) {
                    progress += data[dayPoolIndex].getTotalAmount();
                    System.out.println("Current progress while iterating   "  + data[dayPoolIndex].getTotalAmount());
                    dayPoolIndex++;
                }

                formattedData.add(i
                        , new Entry(progress, i));
            }

            date.addDays(1);
        }


        dataSet = new LineDataSet(formattedData, "");
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
    }

    private boolean sameDate(MutableDateTime date1, MutableDateTime date2) {
        boolean matchingYears = (date1.getYear() == date2.getYear());
        boolean mathcingMonths = (date1.getMonthOfYear() == date2.getMonthOfYear());
        boolean matchingDays = (date1.getDayOfMonth() == date2.getDayOfMonth());

        return  matchingYears && mathcingMonths && matchingDays;
    }

    public void setTracker(Tracker host) {
        this.host = host;
        startDate = new DateTime(host.getStartDateMillis());
        endDate = new DateTime(host.getEndDateMillis());
    }

    private void addDataToSuperClass() {
        ArrayList<LineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);
        LineData data = new LineData(xValues, dataSets);
        super.setData(data);
    }

    public void update() {
        pullDataFromTracker();
        stylize();
        addDataToSuperClass();
        invalidate();
    }
}
