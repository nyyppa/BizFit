package com.bizfit.bizfit.activities;

/**
 * Created by Atte Ylivrronen on 13.2.2016.
 */
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;

import android.content.Intent;

import android.os.IBinder;

import android.support.annotation.Nullable;
import android.widget.Toast;

import com.bizfit.bizfit.NotificationSender;

import java.util.Calendar;


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

    public void onStart(Intent intent, int startId) {

// TODO Auto-generated method stub

        super.onStart(intent, startId);


    }



    @Override

    public boolean onUnbind(Intent intent) {

// TODO Auto-generated method stub


        return super.onUnbind(intent);

    }


    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);



        NotificationSender.sendNotification(this, "hei", "hei");





        return START_NOT_STICKY;
    }


}
