package com.bizfit.bizfit.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.utils.FieldNames;

/**
 *
 */
public class ViewTrackerActivity extends AppCompatActivity {

    Activity activity;
    Tracker host;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_view_tracker);
        host = (Tracker)(getIntent().getSerializableExtra(FieldNames.TRACKER));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(host.getName());
        setSupportActionBar(toolbar);
        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                host.addProgress(100);
                System.out.println(host.getProgressPercent());
            }
        });
    }
}
