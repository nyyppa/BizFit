package com.bizfit.bizfit;

import java.util.Date;

/**
 * Created by Atte on 17.11.2015.
 */
public class Tracker {
    Date startDate;
    Date lastUpdateDate;
    Date currentDate;
    int dayInterval;
    int monthInterval;
    int yearInterval;
    float targetProgress;
    float currentProgress;
    float defaultIncrement=1;

    Tracker(){
        startDate=new Date();
        currentDate=new Date();
    }
    Tracker(Date startDate){
        this.startDate=startDate;
        currentDate=new Date();
    }

    public void update(){
        Date today =new Date();

    }

    public float getProgressPercent(){
        return currentProgress/targetProgress;
    }

    public void autoIncrement(){
        currentProgress+=defaultIncrement;
    }
    public  float getTargetProgress(){
        return targetProgress;
    }

    public float getCurrentProgress(){
        return currentProgress;
    }
    public float getDefaultIncrement(){
        return  defaultIncrement;
    }

    public int getMonthInterval() {
        return monthInterval;
    }

    public void setMonthInterval(int monthInterval) {
        this.monthInterval = monthInterval;
    }

    public int getYearInterval() {
        return yearInterval;
    }

    public void setYearInterval(int yearInterval) {
        this.yearInterval = yearInterval;
    }


    public int getDayInterval() {
        return dayInterval;
    }

    public void setDayInterval(int dayInterval) {
        this.dayInterval = dayInterval;
    }





}
