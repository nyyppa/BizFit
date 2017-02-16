package com.bizfit.bizfit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by Atte Ylivrronen on 16.2.2017.
 */

public class BackgroundThread extends Thread {
    private List<OurRunnable> ourRunnables;
    private List<OurRunnable> ourRunnablesToAdd;
    private static BackgroundThread backgroundThread;
    public BackgroundThread(){
        backgroundThread=this;
        ourRunnables=new ArrayList<>();
    }
    public void run(){
        while (true){
            Iterator<OurRunnable> iterator=ourRunnables.iterator();
            long repeatInterval=60000;
            Long startTime=System.currentTimeMillis();
            while (iterator.hasNext()){
                OurRunnable ourRunnable=iterator.next();
                Long temp=ourRunnable.timeToNextRunAndRun(System.currentTimeMillis());
                if(!ourRunnable.repeat){
                    iterator.remove();
                }
                if(temp<repeatInterval){
                    repeatInterval=temp;
                }
            }
            if(ourRunnablesToAdd.size()==0){
                synchronized(this){
                    try {
                        this.wait(repeatInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            ourRunnables.addAll(ourRunnablesToAdd);
            ourRunnablesToAdd.clear();
        }
    }

    public static void addOurRunnable(OurRunnable runnable){
        if(backgroundThread==null||!backgroundThread.isAlive()){
            backgroundThread=new BackgroundThread();
            backgroundThread.start();
        }
        backgroundThread.addOurRunnableInternal(runnable);
    }
    private void addOurRunnableInternal(OurRunnable runnable){
        if(runnable==null){
            return;
        }
        if (ourRunnablesToAdd==null){
            ourRunnablesToAdd=new ArrayList<>();
        }
        ourRunnablesToAdd.add(runnable);
        wake();
    }
    private void wake(){
        backgroundThread.notify();
    }
    public abstract class OurRunnable{
        Boolean repeat;
        long repeatInterval;
        private Long lastRun;
        public OurRunnable(Boolean repeat,int repeatInterval){
            this.repeat=repeat;
            this.repeatInterval=repeatInterval;
        }
        public OurRunnable(){
            repeat=false;
            repeatInterval=-1;
        }
        private long timeToNextRunAndRun(long millis){
            if(repeatInterval<0){
                repeatInterval= TimeUnit.SECONDS.toMillis(60);
            }
            if(lastRun+repeatInterval<=millis){
                run();
                lastRun=millis;
            }
            return (lastRun+repeatInterval)-millis;
        }
        public abstract void run();
    }
}
