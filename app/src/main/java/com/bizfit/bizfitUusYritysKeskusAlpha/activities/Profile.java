package com.bizfit.bizfitUusYritysKeskusAlpha.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bizfit.bizfitUusYritysKeskusAlpha.R;

/**
 * Created by iipa on 20.6.2017.
 */

public class Profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // TODO: fill user information into fields

        ImageView profilePicture = (ImageView) findViewById(R.id.profilePicture);
        TextView userName = (TextView) findViewById(R.id.user_name);
        // job is in form JOB TITLE + " in " + COMPANY
        TextView userJob = (TextView) findViewById(R.id.user_job);
        TextView userDesc = (TextView) findViewById(R.id.user_desc);

        CardView editProfile = (CardView) findViewById(R.id.edit_profile_card);

        // TODO: check if own and do stuff based on result
        // for now assumes that is own profile

        /*
        if(!omaProfiili) {
            editProfile.setVisibility(View.GONE);
        } else {
            setOnClickListener(...)
        }
        */

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EditProfile.class);
                startActivity(intent);
            }
        });
    }
}
