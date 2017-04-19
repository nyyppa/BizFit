package com.bizfit.bizfit.utils;

import com.bizfit.bizfit.MyApplication;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Atte Ylivrronen on 29.1.2016.
 * Handy class for getting times held in Calender
 */
public class OurDateTime extends GregorianCalendar {

    /**
     * @param time Time you want to convert to Calender
     */
    public OurDateTime(long time) {
        super();
        this.setTimeInMillis(time);
    }

    /**
     * @return year
     */
    public int getYear() {
        return this.get(YEAR);
    }

    /**
     * @return month
     */
    public int getMonth() {
        return this.get(MONTH);
    }

    /**
     * @return Day Of month
     */
    public int getDay() {
        return this.get(DAY_OF_MONTH);
    }

    public int getHour(){return this.get(HOUR);}

    public int getMinute(){ return this.get(MINUTE) ;}

    public boolean isToday(){
        Calendar calendar=GregorianCalendar.getInstance();
        return calendar.get(DAY_OF_MONTH) == this.getDay() && calendar.get(MONTH) == this.getMonth() && calendar.get(YEAR) == this.getYear();
    }

    public boolean isThisYear()
    {
        Calendar calendar=GregorianCalendar.getInstance();
        return calendar.get(YEAR) == this.getYear();
    }

    public String getMonthDisplayName()
    {
        return getDisplayName(Calendar.MONTH, Calendar.LONG, MyApplication.getCurrentLocale());
    }

    public String getFullDisplayName()
    {
        DateFormat formatter=DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, MyApplication.getCurrentLocale());
        return formatter.format(getTime());
    }

    /**
     * checks if two OurDateTime's hold same date
     *
     * @param date
     * @return true if they have same dates
     */
    public boolean isSameDate(OurDateTime date) {
        return date.getDay() == this.getDay() && date.getMonth() == this.getMonth() && date.getYear() == this.getYear();
    }

}
