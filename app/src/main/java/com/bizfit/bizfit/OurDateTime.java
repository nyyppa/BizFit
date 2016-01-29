package com.bizfit.bizfit;

import java.util.GregorianCalendar;

/**
 * Created by Atte Ylivrronen on 29.1.2016.
 */
public class OurDateTime extends GregorianCalendar {
    OurDateTime(long time){
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

}
