package com.bizfit.bizfit.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bizfit.bizfit.R;

public class MessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
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

}
