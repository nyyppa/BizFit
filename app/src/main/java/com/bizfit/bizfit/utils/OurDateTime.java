package com.bizfit.bizfit.utils;

import java.util.GregorianCalendar;

/**
 * Created by Atte Ylivrronen on 29.1.2016.
 */
public class OurDateTime extends GregorianCalendar {
    public OurDateTime(long time){
        super();
        this.setTimeInMillis(time);
    }

    public int getYear(){
        return this.get(YEAR);
    }
    public int getMonth(){
        return this.get(MONTH);
    }
    public int getDay(){
        return this.get(DAY_OF_MONTH);
    }

    public boolean isSameDate(OurDateTime date){
        return date.getDay()==this.getDay()&&date.getMonth()==this.getMonth()&&date.getYear()==this.getYear();
    }

}
