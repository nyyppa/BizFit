package com.bizfit.bizfit;

/**
 * Created by Atte on 22.11.2015.
 */
public class OldProgress implements java.io.Serializable{
    /**
     *
     */
    private static final long serialVersionUID = -8599112903840590954L;
    private long startDate;
    private long endDate;
    private float endProgress;
    private float targetProgress;
    private DailyProgress daily;


    /**
     *
     * @param startDate Start date of tracked time period in milliseconds
     * @param endDate   end date of tracked time period in milliseconds
     * @param endProgress   how much progress was achieved
     * @param targetProgress    what was desired progress
     */
    OldProgress(long startDate,long endDate,float endProgress,float targetProgress,DailyProgress dailyProgress){
        this.startDate=startDate;
        this.endDate=endDate;
        this.endProgress=endProgress;
        this.targetProgress=targetProgress;
        this.daily=dailyProgress;

    }

    /**
     *
     * @return start date in milliseconds
     */
    public long getStartDate() {
        return startDate;
    }

    /**
     *
     * @return  end date in milliseconds
     */
    public long getEndDate() {
        return endDate;
    }

    /**
     *
     * @return  how great progress was achieved in persents, 1=100%
     */
    public float getProgressPercent() {
        return endProgress/targetProgress;
    }

    /**
     *
     * @return achieved progress as absolute amount
     */
    public float getProgress(){
        return endProgress;
    }

    /**
     *
     * @return  desired progress as absolute amount
     */
    public float getTargetProgress(){
        return targetProgress;
    }

    public DailyProgress getDailyProgress(){
    	return daily;
    }

}

