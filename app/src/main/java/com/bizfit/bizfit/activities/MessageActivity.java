package com.bizfit.bizfit.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.fragments.ChatFragment;
import com.bizfit.bizfit.fragments.PinnedMessages;

public class MessageActivity extends AppCompatActivity {
    /** Last updated 9.5.17 by JariJ
     * Canceling all notifications, when entering chat activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        NotificationManager nManager = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.cancelAll();
    }

    public static void startChat(Context context, String coachID){
        Intent intent=new Intent(context ,MessageActivity.class);
        intent.putExtra("coachID",coachID);
        context.startActivity(intent);
    }
    public static void startChat(Context context, Intent intent2){
        Intent intent=new Intent(context,MessageActivity.class);
        intent.putExtras(intent2);
        context.startActivity(intent);
    }
    public static void startChat(Context context, Bundle bundle){
        Intent intent=new Intent(context ,MessageActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public void switchToPinned() {
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new PinnedMessages(), "Pinned");
        ft.addToBackStack("Messages");
        ft.commit();
    }

    public void switchToRegular() {
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new ChatFragment(), "Messages");
        ft.commit();
    }

}
