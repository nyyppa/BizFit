package com.bizfit.bizfit.activities;

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

    public static void startChat(View v, String coachID){
        Intent intent=new Intent(v.getContext(),MessageActivity.class);
        intent.putExtra("coachID",coachID);
        v.getContext().startActivity(intent);
    }

}
