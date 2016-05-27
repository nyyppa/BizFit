package com.bizfit.bizfit.utils;

import java.util.GregorianCalendar;

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
