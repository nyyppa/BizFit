package com.bizfit.bizfitUusYritysKeskusAlpha;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Atte Ylivrronen on 24.2.2016.
 */
public class MyAlarmService extends Service {

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
    }


    public IBinder onBind(Intent intent) {

        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }






    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub
        return super.onUnbind(intent);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        //NotificationSender.sendNotification(this, "hei", "hei");
        User.update(this);
        return START_NOT_STICKY;
    }
}
