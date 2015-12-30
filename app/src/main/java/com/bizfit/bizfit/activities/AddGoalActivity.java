package com.bizfit.bizfit.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.utils.FieldNames;

/**
 * Created by Käyttäjä on 19.12.2015.
 */
public class AddGoalActivity extends AppCompatActivity {
    public static Activity activity = null;
    EditText name;
    EditText date;
    EditText target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_add_goal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        name = (EditText) findViewById(R.id.target_name);
        date = (EditText) findViewById(R.id.target_date);
        target = (EditText) findViewById(R.id.target_amount);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_goal, menu);
        return true;
    }

    @Override
    public void finish() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(FieldNames.TRACKERNAME, name.getText().toString());
        returnIntent.putExtra(FieldNames.TARGET, Float.parseFloat(target.getText().toString()));
        setResult(Activity.RESULT_OK, returnIntent);
        super.finish();
    }
}
