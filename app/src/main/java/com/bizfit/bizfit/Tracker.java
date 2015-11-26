package com.bizfit.bizfit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Atte on 17.11.2015.
 */
public class Tracker implements java.io.Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -9151429274382287394L;
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
    String name;
    String targetType;
    List<OldProgress>oldProgress=new ArrayList<OldProgress>(0);

    Tracker(int DayInterval,int monthInterval){
        startDate=System.currentTimeMillis();
        startStuff(DayInterval,monthInterval);
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

        if(currentCalendar.after(resetCalender)){
            int day = 0;
            do{
                resetCalender.add(Calendar.DAY_OF_MONTH,1);
                day=resetCalender.get(Calendar.DATE);
            }while((currentCalendar.after(resetCalender)));
            addOldProgress(new OldProgress(lastReset, resetCalender.getTimeInMillis(), currentProgress, targetProgress));
            lastReset=resetCalender.getTimeInMillis();
            currentProgress=0;
        }
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
    public void setTargetAmount(float amount){
        this.targetProgress=amount;
    }

    public void addOldProgress(OldProgress o){
        oldProgress.add(o);
    }

    public List<OldProgress> getOldProgress(){
        return oldProgress;
    }
}