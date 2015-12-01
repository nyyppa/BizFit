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
    DailyProgress daily=new DailyProgress();
    SaveState parentSaveState;
    boolean weekly;

    Tracker(Tracker t){
        if(t.weekly){
            weeklyStart();
        }else{
            this.startDate=System.currentTimeMillis();
            startStuff(new GregorianCalendar(),t.getDayInterval(),t.monthInterval);
        }


        targetProgress=t.getTargetProgress();
        defaultIncrement=t.getDefaultIncrement();
        name=t.getName();
        targetType=t.getName();


    }
    /**
     *
     * @param DayInterval   days between resets
     * @param monthInterval months between resets
     */
    Tracker(int DayInterval,int monthInterval){
        startDate=System.currentTimeMillis();
        startStuff(new GregorianCalendar(),DayInterval,monthInterval);
    }

    /**
     *
     * @param startDate start date
     * @param dayInterval   days between resets
     * @param monthInterval months between resets
     */
    Tracker(Date startDate,int dayInterval,int monthInterval){
        this.startDate=startDate.getTime();
        startStuff(new GregorianCalendar(),dayInterval,monthInterval);
    }
    Tracker(){
        weeklyStart();

    }

    private void weeklyStart(){
        weekly=true;
        GregorianCalendar c=new GregorianCalendar();
        c.setTimeInMillis(System.currentTimeMillis());
        int day=c.get(Calendar.DAY_OF_WEEK);
        if(day==1){
            c.add(Calendar.DAY_OF_MONTH, -6);
        }else{
            c.add(Calendar.DAY_OF_MONTH, -day+2);
        }
        this.startDate=c.getTimeInMillis();
        startStuff(c, 7, 0);
    }

    public String getName(){
        return name;
    }

    public String getTargetType(){
        return targetType;
    }

    public Date getStartDate(){
        return new Date(startDate);
    }

    public DailyProgress getDailyProgress(){
        return daily;
    }

    /**
     *
     * @param dayInterval
     * @param monthInterval
     */
    private void startStuff(GregorianCalendar calander,int dayInterval,int monthInterval){
        lastReset=startDate;
        this.dayInterval=dayInterval;
        this.monthInterval=monthInterval;
        calander.add(Calendar.DAY_OF_MONTH,dayInterval);
        calander.add(Calendar.MONTH,monthInterval);
        timeProgressNeed=calander.getTimeInMillis()-startDate;
    }

    /**
     * call this regularly to notify object of passing time
     */
    public void update(){
        timeProgress=System.currentTimeMillis()-lastReset;
        GregorianCalendar currentCalendar=new GregorianCalendar();
        currentCalendar.setTimeInMillis(System.currentTimeMillis());
        GregorianCalendar resetCalender=new GregorianCalendar();
        resetCalender.setTimeInMillis(lastReset);
        resetCalender.add(Calendar.MONTH, monthInterval);
        resetCalender.add(Calendar.DAY_OF_MONTH,dayInterval);
        resetCalender.add(Calendar.YEAR,yearInterval);
        if(currentCalendar.after(resetCalender)){
            do{
                resetCalender.add(Calendar.DAY_OF_MONTH,1);
            }while((currentCalendar.after(resetCalender)));
            addOldProgress(new OldProgress(lastReset, resetCalender.getTimeInMillis(), currentProgress, targetProgress,daily));
            daily=new DailyProgress();
            lastReset=resetCalender.getTimeInMillis();
            currentProgress=0;
        }
    }

    /**
     *
     * @param progress  progress that should be added to tracker
     */
    public void addProgress(float progress){
        currentProgress+=progress;
        daily.addDailyProgress(progress, System.currentTimeMillis());
        fieldUpdated();
    }

    /**
     *
     * @return  achieved progress in percents 1=100%
     */
    public float getProgressPercent(){
        return currentProgress/targetProgress;
    }

    /**
     * adds default increment to current progress
     */
    public void autoIncrement(){
        addProgress(defaultIncrement);
    }

    /**
     *
     * @return  desired progress in absolute amount
     */
    public  float getTargetProgress(){
        return targetProgress;
    }

    /**
     *
     * @return achieved progress in absolute amount
     */
    public float getCurrentProgress(){
        return currentProgress;
    }

    /**
     *
     * @return default increment for tracker
     */
    public float getDefaultIncrement(){
        return  defaultIncrement;
    }

    /**
     *
     * @return  monthly interval for tracker
     */
    public int getMonthInterval() {
        return monthInterval;
    }

    /**
     *
     * @param monthInterval new monthly interval for this tracker
     */
    public void setMonthInterval(int monthInterval) {
        this.monthInterval = monthInterval;
        fieldUpdated();
    }

    /**
     *
     * @return  year interval for tracker
     */
    public int getYearInterval() {
        return yearInterval;
    }

    /**
     *
     * @param yearInterval  new yearly interval for this tracker
     */
    public void setYearInterval(int yearInterval) {
        this.yearInterval = yearInterval;
        fieldUpdated();
    }

    /**
     *
     * @return  day interval for tracker
     */
    public int getDayInterval() {
        return dayInterval;
    }

    /**
     *
     * @param dayInterval   new day interval for tracker
     */
    public void setDayInterval(int dayInterval) {
        this.dayInterval = dayInterval;
        fieldUpdated();
    }

    /**
     *
     * @param amount    new target progress
     */
    public void setTargetAmount(float amount){
        this.targetProgress=amount;
        fieldUpdated();
    }

    /**
     *
     * @param o last periods progress
     */
    private void addOldProgress(OldProgress o){
        oldProgress.add(o);
        fieldUpdated();
    }

    /**
     *
     * @return  all passed progress for this tracker
     */
    public List<OldProgress> getOldProgress(){
        return oldProgress;
    }

    /**
     * tells tracker to delete itself from users SaveState
     */
    public void delete(){
        parentSaveState.removeTracker(this);
    }

    /**
     * tells users SaveState to save itself
     */
    private void fieldUpdated(){
        try {
            parentSaveState.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setDefaultInrement(float increment){
        this.defaultIncrement=increment;
        fieldUpdated();
    }
}