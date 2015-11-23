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

    OldProgress(long startDate,long endDate,float endProgress,float targetProgress){
        this.startDate=startDate;
        this.endDate=endDate;
        this.endProgress=endProgress;
        this.targetProgress=targetProgress;

    }

    public long getStartDate() {
        return startDate;
    }
    public long getEndDate() {
        return endDate;
    }
    public float getProgressPercent() {
        return endProgress/targetProgress;
    }
    public float getProgress(){
        return endProgress;
    }
    public float getTargetProgress(){
        return targetProgress;
    }

}

