package com.bizfit.bizfit;

/**
 * Created by Käyttäjä on 3.1.2016.
 */
import com.bizfit.bizfit.activities.MainActivity;

public class PauseableThread extends Thread {
    boolean exit = false;
    long pauseEnds;
    long millis;
    public PauseableThread(long millis){
        this.millis=millis;
    }
    public void run () {
        while (true) {
            synchronized (this) {
                try {
                    while (System.currentTimeMillis()<pauseEnds)
                        wait(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (exit) return;

            if (MainActivity.currentUser!=null) {
                Tracker[]t=MainActivity.currentUser.getTrackers();
                for(Tracker tracker:t){
                    tracker.update();
                    //MainActivity.currentUser.getTrackers().get(i).testUpdate(1000);
                }
            }
            pause(millis);
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

