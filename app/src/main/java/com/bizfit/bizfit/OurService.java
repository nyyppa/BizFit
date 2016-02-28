package com.bizfit.bizfit;


import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Atte Ylivrronen on 30.1.2016.
 */
public class OurService extends IntentService {
    /**
     * A constructor is required, and must call the super IntentService(String)
     * constructor with a NAME for the worker thread.
     */
    public static PauseableThread thread;


    public OurService() {
        super("OurService");
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        // Normally we would do some work here, like download a file.
        // For our sample, we just sleep for 5 seconds.
        long endTime = System.currentTimeMillis() + 5*1000;
        while (System.currentTimeMillis() < endTime) {
            synchronized (this) {
                try {
                    wait(endTime - System.currentTimeMillis());
                } catch (Exception e) {
                }
            }
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        if(thread==null){
            thread=new PauseableThread(1000);
            thread.start();
        }else if(thread!=null&&!thread.isAlive()){
            thread=new PauseableThread(1000);
            thread.start();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
