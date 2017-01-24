package com.bizfit.bizfit.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat.Builder;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.activities.MainPage;
import com.bizfit.bizfit.activities.MessageActivity;


/**
 * Created by Atte Ylivrronen on 30.1.2016.
 */
public class NotificationSender {


    /**
     * sends message to android notification bar
     *
     * @param title   tittle of the message
     * @param message actual message content
     */
    /*
    public static void sendNotification(String title,String message){
        NotificationCompat.Builder mBuilder =
                (Builder) new Builder(MainPage.activity)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(MainPage.activity, MainPage.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(MainPage.activity);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainPage.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) MainPage.activity.getSystemService(MainPage.activity.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        int mId=1;
        mNotificationManager.notify(mId, mBuilder.build());
    }
    */
    public static void sendNotification(Context a, String title, String message) {
        Builder mBuilder =
                (Builder) new Builder(a)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(a, MainPage.class);
        //jos viesti conversationista

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(a);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainPage.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) a.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        int mId = 1;
        mNotificationManager.notify(mId, mBuilder.build());
    }

    public static void sendNotification(Context a, String title, String message,String coach) {
        Builder mBuilder =
                (Builder) new Builder(a)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(a, MessageActivity.class);
        resultIntent.putExtra("coachID",coach);
        //jos viesti conversationista




        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(a);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainPage.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) a.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        int mId = 1;
        mNotificationManager.notify(mId, mBuilder.build());
    }
}
