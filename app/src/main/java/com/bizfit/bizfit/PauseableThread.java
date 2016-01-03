package com.bizfit.bizfit;

/**
 * Created by Käyttäjä on 3.1.2016.
 */
import com.bizfit.bizfit.activities.MainActivity;

public class PauseableThread extends Thread {
    boolean exit = false;
    long pauseEnds;

    public void run () {
        while (true) {
            synchronized (this) {
                try {
                    while (System.currentTimeMillis()<pauseEnds)
                        wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (exit) return;

            for(int i=0;i<MainActivity.currentUser.getTrackers().size();i++){
                MainActivity.currentUser.getTrackers().get(i).update();
                //MainActivity.currentUser.getTrackers().get(i).testUpdate(1000);
            }
        }
    }



    public void pause(long pauseTime){
        pauseEnds=System.currentTimeMillis()+pauseTime;
    }





    /** Stops this thread */
    public void stopThread () {
        exit = true;
    }
}

