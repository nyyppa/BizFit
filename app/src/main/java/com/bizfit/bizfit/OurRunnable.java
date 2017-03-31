package com.bizfit.bizfit;

import java.util.concurrent.TimeUnit;

/**
 * Created by Atte Ylivrronen on 17.2.2017.
 */

public abstract class OurRunnable{
    Boolean repeat;
    long repeatInterval;
    private Long lastRun=0l;
    public BackgroundThread backgroundThread;
    public OurRunnable(Boolean repeat,int repeatInterval){
        this.repeat=repeat;
        this.repeatInterval=repeatInterval;
    }
    public OurRunnable(){
        repeat=false;
        repeatInterval=-1;
    }
    protected long timeToNextRunAndRun(long millis){
        if(repeatInterval<=0){
            repeatInterval= TimeUnit.SECONDS.toMillis(60);
        }
        if(lastRun+repeatInterval<=millis){
            run();
            lastRun=millis;
        }
        return (lastRun+repeatInterval)-millis;
    }
    public abstract void run();
    public void stop()
    {
        repeat=false;
    }
    protected void setRepeatInterval(Long interval){
        this.repeatInterval=interval;
    }

    public void wake()
    {
        lastRun=0l;
        backgroundThread.wake();
    }
}
