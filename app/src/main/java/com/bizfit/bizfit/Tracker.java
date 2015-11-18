package com.bizfit.bizfit;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Atte on 17.11.2015.
 */
public class Tracker {
    long startDate;
    long elapsedTime;
    long lastReset;
    int dayInterval;
    int monthInterval;
    int yearInterval;
    float targetProgress;
    float currentProgress;
    float defaultIncrement=1;
    long timeProgress;
    long timeProgressNeed;

    Tracker(int DayInterval,int monthInterval){
        startDate=System.currentTimeMillis();
        startStuff(dayInterval,monthInterval);
    }
    Tracker(Date startDate,int dayInterval,int monthInterval){
        this.startDate=startDate.getTime();
        startStuff(dayInterval,monthInterval);
    }

    public void startStuff(int dayInterval,int monthInterval){
        lastReset=startDate;
        this.dayInterval=dayInterval;
        this.monthInterval=monthInterval;
        GregorianCalendar calander=new GregorianCalendar();
        calander.add(Calendar.DAY_OF_MONTH,dayInterval);
        calander.add(Calendar.MONTH,monthInterval);
        timeProgressNeed=calander.getTimeInMillis()-startDate;

    }

    public void update(){
        GregorianCalendar currentCalendar=new GregorianCalendar();
        currentCalendar.setTimeInMillis(System.currentTimeMillis());
        GregorianCalendar resetCalender=new GregorianCalendar();
        resetCalender.setTimeInMillis(lastReset);
        resetCalender.add(Calendar.MONTH, monthInterval);
        resetCalender.add(Calendar.DAY_OF_MONTH,dayInterval);
        updateElapsedTime();
        if(currentCalendar.after(resetCalender)){
            do{
                resetCalender.add(Calendar.MONTH, monthInterval);
                resetCalender.add(Calendar.DAY_OF_MONTH,dayInterval);
            }while((currentCalendar.after(resetCalender)));
            lastReset=resetCalender.getTimeInMillis();
        }
    }
    private void updateElapsedTime(){
        elapsedTime=System.currentTimeMillis()-startDate;
    }

    public void addProgress(float progress){
        currentProgress+=progress;
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
