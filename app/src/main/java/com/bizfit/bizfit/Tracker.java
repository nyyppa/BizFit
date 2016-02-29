package com.bizfit.bizfit;

import com.bizfit.bizfit.activities.MainActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

/**
 * Created by Atte on 17.11.2015.
 */
public class Tracker implements java.io.Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -9151429274382287394L;
    long startDate;
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
    User parentUser;
    boolean weekly;
    boolean repeat;
    boolean completed=false;
    public float tolerance=10;
    List<Change> changes=new ArrayList<Change>(0);
    int color;
    int index;
    long lastTestUpdate;

    transient ChangeListener listener;

    public int getIndex(){
        return index;
    }

    public int daysFromStart(){
        return (int)(TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis()-startDate));
    }

    /**
     * set Color reference for this tracker
     * @param color int reference to the color
     * @return  color it was changed to
     */
    public int setColor(int color){
        changes.add(0, new Change(this.color+"",lastModification.color));
        this.color=color;
        return color;
    }

    /**
     * Give changeListener to be notified when progress is added
     * @param listener
     */
    public void setChangeListener(ChangeListener listener){
        this.listener=listener;
    }

    /**
     * get Color reference for this tracker
     * @return  int reference
     */
    public int getColor(){
        return  color;
    }
    public void undo(){
        ListIterator<Change>iterator=changes.listIterator();
        if (iterator.hasNext()) {
            Change last=iterator.next();
            switch (last.getEnum()) {
                case currentProgress:
                    removeProgress(Float.parseFloat(last.getValue()));
                    break;
                case daily:
                    this.daily.undoLast();
                    break;
                case dayInterval:
                    this.dayInterval=Integer.parseInt(last.getValue());
                    break;
                case defaultIncrement:
                    this.defaultIncrement=Float.parseFloat(last.getValue());
                    break;
                case lastReset:
                    this.lastReset=Long.parseLong(last.getValue());
                    break;
                case monthInterval:
                    this.monthInterval=Integer.parseInt(last.getValue());
                    break;
                case name:
                    this.name=last.getValue();
                    break;
                case targetProgress:
                    this.targetProgress=Float.parseFloat(last.getValue());
                    break;
                case targetType:
                    this.targetType=last.getValue();
                    break;
                case timeProgress:
                    this.timeProgress=Long.parseLong(last.getValue());
                    break;
                case weekly:
                    this.weekly=Boolean.parseBoolean(last.getValue());
                    break;
                case yearInterval:
                    this.yearInterval=Integer.parseInt(last.getValue());
                    break;
                case repeat:
                    this.repeat=Boolean.parseBoolean(last.getValue());
                    break;
                case color:
                    this.color=Integer.parseInt(last.getValue());
                    break;
                default:
                    break;

            }
            iterator.remove();
        }
        fieldUpdated();
    }
    public int getDaysRemaining(){
        return (int) (TimeUnit.MILLISECONDS.toDays(timeProgressNeed-timeProgress));
    }
    public RemainingTime getTimeRemaining(){
       return new RemainingTime(timeProgressNeed-timeProgress);
    }
    public OnTrack getProgressOnTrack(){
        double timeProgressPersent=(double)(timeProgress)/(double)(timeProgressNeed);
        if(getCurrentProgress()<timeProgressPersent-tolerance/100){
            return OnTrack.behind;
        }else if(getCurrentProgress()>timeProgressPersent+tolerance/100){
            return OnTrack.ahead;
        }else{
            return OnTrack.onTime;
        }
    }

    public DailyProgress.DayPool[] getCurrentDayPools(){
        DailyProgress.DayPool[] d=new DailyProgress.DayPool[daily.getDayPoolList().size()];
        return daily.getDayPoolList().toArray(d);
    }


    public DailyProgress.DayPool[] getAllDayPools(){
        List<DailyProgress.DayPool> lista=new ArrayList<DailyProgress.DayPool>(0);
        for(int i=0;i<oldProgress.size();i++){
            lista.addAll(oldProgress.get(i).getDailyProgress().getDayPoolList());
        }
        lista.addAll(daily.getDayPoolList());
        Comparator<DailyProgress.DayPool> t=new Comparator<DailyProgress.DayPool>() {
            @Override
            public int compare(DailyProgress.DayPool lhs, DailyProgress.DayPool rhs) {
                return (int)(-1*(lhs.getTime()-rhs.getTime()));
            }
        };
        Collections.sort(lista,t);
        DailyProgress.DayPool[] d=new DailyProgress.DayPool[lista.size()];


        return lista.toArray(d);
    }

    public double getProgressComperedToTime(){
        double timeProgressPersent=(double)(timeProgress)/(double)(timeProgressNeed);

        return getCurrentProgress()-timeProgressPersent;
    }
    private void setAttributes(Tracker t){
        this.dayInterval=t.dayInterval;
        this.monthInterval=t.monthInterval;
        this.yearInterval=t.yearInterval;
        this.targetProgress=t.targetProgress;
        this.currentProgress=t.currentProgress;
        this.defaultIncrement=t.defaultIncrement;
        this.name=t.name;
        this.targetType=t.targetType;

        this.daily=t.daily;
        this.weekly=t.weekly;
    }

    public Tracker(Tracker t){
        setAttributes(t);
        if(t.weekly){
            weeklyStart();
        }else{
            this.startDate=System.currentTimeMillis();
            startStuff(new GregorianCalendar(),t.getDayInterval(),t.monthInterval);
        }
    }

    /**
     *
     * @param DayInterval   days between resets
     * @param monthInterval months between resets
     */
    public Tracker(int DayInterval,int monthInterval){
        startDate=System.currentTimeMillis();
        startStuff(new GregorianCalendar(), DayInterval, monthInterval);
    }

    /**
     *
     * @param startDate start date
     * @param dayInterval   days between resets
     * @param monthInterval months between resets
     */
    public Tracker(Date startDate,int dayInterval,int monthInterval){
        this.startDate=startDate.getTime();
        startStuff(new GregorianCalendar(), dayInterval, monthInterval);
    }

    public Tracker(){
        weeklyStart();
    }

    //tarvii undon
    public void setTargetDate(int year, int month, int day, boolean repeat){
        this.repeat=repeat;
        GregorianCalendar c=new GregorianCalendar(year,month,day,23,59);
        GregorianCalendar a=new GregorianCalendar();
        startDate=System.currentTimeMillis();
        int dayInterval=(int) (TimeUnit.MILLISECONDS.toDays(c.getTimeInMillis()-a.getTimeInMillis()));
        System.out.println("tunniste: "+dayInterval);
        startStuff(a, dayInterval, 0);
        fieldUpdated();

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

    public long getStartDateMillis(){
        return startDate;
    }

    public long getEndDateMillis(){
        return lastReset+timeProgressNeed;
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

    public int getRemainingTimeMillis(){
        return (int)timeProgressNeed;
    }

    public void testUpdate(Long millis){
        if (!completed) {
            if(lastTestUpdate==0){
                lastTestUpdate=System.currentTimeMillis();
            }
            lastTestUpdate+=millis;
            timeProgress = System.currentTimeMillis() - lastReset+lastTestUpdate;
            GregorianCalendar currentCalendar = new GregorianCalendar();
            currentCalendar.setTimeInMillis(lastTestUpdate);
            GregorianCalendar resetCalender = new GregorianCalendar();
            resetCalender.setTimeInMillis(lastReset);
            resetCalender.add(Calendar.MONTH, monthInterval);
            resetCalender.add(Calendar.DAY_OF_MONTH, dayInterval);
            resetCalender.add(Calendar.YEAR, yearInterval);
            if (currentCalendar.after(resetCalender)) {
                do {
                    resetCalender.add(Calendar.DAY_OF_MONTH, 1);
                } while ((currentCalendar.after(resetCalender)));
                addOldProgress(new OldProgress(lastReset,
                        resetCalender.getTimeInMillis(), currentProgress,
                        targetProgress, daily));
                daily = new DailyProgress();
                lastReset = resetCalender.getTimeInMillis();
                currentProgress = 0;
                if(!repeat){
                    completed=true;
                }
                fieldUpdated();
            }
        }
    }

    /**
     * call this regularly to notify object of passing time
     */
    public void update(){
        if (!completed) {
            timeProgress = System.currentTimeMillis() - lastReset;
            GregorianCalendar currentCalendar = new GregorianCalendar();
            currentCalendar.setTimeInMillis(System.currentTimeMillis());
            GregorianCalendar resetCalender = new GregorianCalendar();
            resetCalender.setTimeInMillis(lastReset);
            resetCalender.add(Calendar.MONTH, monthInterval);
            resetCalender.add(Calendar.DAY_OF_MONTH, dayInterval);
            resetCalender.add(Calendar.YEAR, yearInterval);
            if (currentCalendar.after(resetCalender)) {
                do {
                    resetCalender.add(Calendar.DAY_OF_MONTH, 1);
                } while ((currentCalendar.after(resetCalender)));
                addOldProgress(new OldProgress(lastReset,
                        resetCalender.getTimeInMillis(), currentProgress,
                        targetProgress, daily));
                daily = new DailyProgress();
                lastReset = resetCalender.getTimeInMillis();
                currentProgress = 0;
                if(!repeat){
                    completed=true;
                }
                fieldUpdated();
            }
        }
    }
    private void removeProgress(float progress){
        currentProgress-=progress;
        daily.undoLast();
        fieldUpdated();
    }
    /**
     *
     * @param progress  progress that should be added to tracker
     */
    public void addProgress(float progress){
        currentProgress+=progress;
        daily.addDailyProgress(progress, System.currentTimeMillis());
        changes.add(0, new Change(progress + "", lastModification.currentProgress));
        listener.changeAmount(progress);
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
        changes.add(0,new Change(monthInterval+"",lastModification.monthInterval));
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
        changes.add(0,new Change(yearInterval+"",lastModification.yearInterval));
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
        changes.add(0,new Change(dayInterval+"",lastModification.dayInterval));
        fieldUpdated();
    }

    /**
     *
     * @param amount    new target progress
     */
    public void setTargetAmount(float amount){
        this.targetProgress=amount;
        changes.add(0,new Change(amount+"",lastModification.targetProgress));
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
    public OldProgress[] getOldProgress(){
        OldProgress[]o=new OldProgress[oldProgress.size()];
        return oldProgress.toArray(o);
    }

    /**
     * tells tracker to delete itself from users User
     */
    public void delete(){
        parentUser.removeTracker(this);
    }

    public void setRepeat(boolean repeat){
        this.repeat=repeat;
    }

    public boolean getRepeat(boolean repeat){
        return this.repeat;
    }

    /**
     * tells users User to save itself
     */
    private void fieldUpdated(){
        try {
            parentUser.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setDefaultInrement(float increment){
        this.defaultIncrement=increment;
        changes.add(0,new Change(increment+"",lastModification.defaultIncrement));
        fieldUpdated();
    }

    /**
     * get minimum progress userName needs to achieve to achieve his/her object
     * @return minimum progress needed to achieve to pass his/her goal
     */
    public float getMinimumProgressNeededPerDay(){
        long daysD=TimeUnit.MICROSECONDS.toDays(timeProgressNeed);
        float f=(float)(getTargetProgress()/daysD);
        return f;
    }

    public enum lastModification {
        lastReset,dayInterval,monthInterval,yearInterval,
        targetProgress,currentProgress,defaultIncrement,timeProgress
        ,name,targetType,daily,weekly,repeat,color;

    }
    public class Change implements java.io.Serializable{

        lastModification mod;
        String value;
        Change(String value, lastModification l){
            this.value=value;
            this.mod=l;
        }

        String getValue(){
            return value;
        }

        lastModification getEnum(){
            return mod;
        }
    }

    public class RemainingTime{
        RemaininTimeType timeType;
        int time;
        RemainingTime(long millis){
            //System.out.println("tunniste: ");
            float daysRemaining=(float)TimeUnit.MILLISECONDS.toDays(millis);
            if(daysRemaining>30){
                timeType=RemaininTimeType.months;
                daysRemaining/=30;
                time=Math.round(daysRemaining);
            }else{
                timeType=RemaininTimeType.days;
                time=(int)daysRemaining;
            }
        }
        public String getTimeType(){
            switch (timeType){
                case days:
                    if(time==1){
                        return MainActivity.activity.getResources().getString(R.string.timeType_days_one);
                    }
                    return MainActivity.activity.getResources().getString(R.string.timeType_days);
                case months:
                    if(time==1){
                        return MainActivity.activity.getResources().getString(R.string.timeType_months_one);
                    }
                    return MainActivity.activity.getResources().getString(R.string.timeType_months);
            }
            return null;

        }
        public int getTimeRemaining(){
            return time;
        }
    }

    public enum RemaininTimeType{
        days,months;
    }

    public void setName(String name) {
        this.name = name;
    }

    public enum OnTrack{
        behind,onTime,ahead;
    }

    public DailyProgress getDaily() {
        return daily;
    }

    public interface ChangeListener {
        public void changeAmount(float amount);
    }
}