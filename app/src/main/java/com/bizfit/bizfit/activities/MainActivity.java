package com.bizfit.bizfit.activities;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bizfit.bizfit.SaveState;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.utils.FieldNames;
import com.bizfit.bizfit.views.ArcProgress;
import com.bizfit.bizfit.R;
import com.bizfit.bizfit.utils.Utils;
import com.bizfit.bizfit.views.TrackableView;

import java.util.ArrayList;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {
    public static Activity activity = null;
    private Timer timer;
    private ProgressBar mProgress;
    private int mProgressStatus = 0;
    private Handler mHandler = new Handler();
    private static final int GET_NEW_GOAL = 1;
    private LinearLayout layout;
    public static SaveState currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        layout = (LinearLayout) findViewById(R.id.goal_container);
        currentUser = SaveState.getLastUser();
        createTrackableViews();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeActivity();
            }
        });
    }

    private void createTrackableViews() {
        System.out.println("Current used tracker count: " + currentUser.getTrackers().size());
        for(Tracker tracker : currentUser.getTrackers()) {
            TrackableView view = new TrackableView(getBaseContext(), null);
            view.setLabel(tracker.getName());
            view.setPercentage(
                    (int) Math.floor(tracker.getProgressPercent()));

            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT
                    , (int) Utils.dp2px(getResources()
                    , 131));

            view.setLayoutParams(param);
            layout.addView(view);
        }
    }

    private void changeActivity() {
        Intent intent = new Intent(this, AddGoalActivity.class);
        startActivityForResult(intent, GET_NEW_GOAL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.settings_demo_1) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GET_NEW_GOAL && resultCode == RESULT_OK) {
            Tracker newTracker = new Tracker();
            newTracker.setName(data.getStringExtra(FieldNames.TRACKERNAME));
            newTracker.setTargetAmount(data.getFloatExtra(FieldNames.TARGET, 0));
            currentUser.addTracker(newTracker);

            TrackableView newTrackableView = new TrackableView(getBaseContext(), null);
            newTrackableView.setLabel(newTracker.getName());
            newTrackableView.setPercentage(
                    (int) Math.floor(newTracker.getProgressPercent()));

            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT
                    , (int) Utils.dp2px(getResources()
                    , 131));

            newTrackableView.setLayoutParams(param);
            layout.addView(newTrackableView);
        }
    }
}
