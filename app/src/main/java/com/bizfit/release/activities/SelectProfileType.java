package com.bizfit.release.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;

import com.bizfit.release.R;

/**
 * Created by iipa on 3.9.2017.
 */

public class SelectProfileType extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile_select_profile);

        CardView basic = findViewById(R.id.basicProfile);
        basic.setOnClickListener(this);

        CardView expert = findViewById(R.id.expertProfile);
        expert.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.basicProfile:
                startActivityForResult(new Intent(this, CreateProfile.class), 1);
                break;

            case R.id.expertProfile:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }
}
