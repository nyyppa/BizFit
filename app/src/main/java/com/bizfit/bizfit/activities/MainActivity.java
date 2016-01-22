package com.bizfit.bizfit.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bizfit.bizfit.PauseableThread;
import com.bizfit.bizfit.SaveState;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.utils.FieldNames;
import com.bizfit.bizfit.R;
import com.bizfit.bizfit.utils.Utils;
import com.bizfit.bizfit.views.Separator;
import com.bizfit.bizfit.views.TrackableView;

import java.util.Timer;

public class MainActivity extends AppCompatActivity {
    public static Activity activity = null;
    private static final int GET_NEW_GOAL = 1;
    private LinearLayout layout;
    public static SaveState currentUser;
    public static PauseableThread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        currentUser = SaveState.getLastUser();
        thread = new PauseableThread(1000);
        thread.start();

    }

    protected void onStart() {
        super.onStart();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        layout = (LinearLayout) findViewById(R.id.goal_container);


        Separator test = new Separator(getBaseContext(), null);
        test.setLabel("A");
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT
                , (int) Utils.dp2px(getResources()
                , 50));
        test.setLayoutParams(param);
        test.setLabelPaddingBottom((int) Utils.dp2px(getResources(), 3));
        test.setLabelPaddingBottom((int)Utils.dp2px(getResources(), 3));
        layout.addView(test);


        createTrackableViews();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchAddTrackerActivity();
            }
        });
        //animateTrackerViewsFromZero();


    }

    private void createTrackableViews() {
        for (int i = 0; i < currentUser.getTrackers().size(); i++) {
            createTrackableView(i);
        }
    }

    private void createTrackableView(final int i) {
        TrackableView view = new TrackableView(getBaseContext(), null);
        Tracker.RemainingTime time = currentUser.getTrackers().get(i).getTimeRemaining();
        view.setLabel(currentUser.getTrackers().get(i).getName());
        view.setPercentage((int) Math.floor(
                currentUser.getTrackers().get(i).getProgressPercent() * 100));
        view.setTimeLeft(time.getTimeRemaining());
        view.setTimeLeftSuffix(time.getTimeType() + "");
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT
                , (int) Utils.dp2px(getResources()
                , 75));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchViewTrackerActivity(currentUser.getTrackers().get(i));
            }
        });

        view.setLayoutParams(param);
        view.setPadding((int) Utils.dp2px(getResources(), 16)
                , (int) Utils.dp2px(getResources(), 24)
                , (int) Utils.dp2px(getResources(), 16)
                , (int) Utils.dp2px(getResources(), 24)
        );

        view.setBackgroundResource(R.drawable.ripple_effect);

        layout.addView(view);
    }

    private void launchViewTrackerActivity(Tracker tracker) {
        Intent viewTracker = new Intent(this, ViewTrackerActivity.class);
        viewTracker.putExtra(FieldNames.TRACKER, tracker);
        startActivity(viewTracker);
    }

    private void launchAddTrackerActivity() {
        Intent intent = new Intent(this, AddTrackerActivity.class);
        startActivityForResult(intent, GET_NEW_GOAL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        thread.stopThread();
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
            currentUser.addTracker(newTracker);
            newTracker.setName(data.getStringExtra(FieldNames.TRACKERNAME));
            newTracker.setTargetAmount(data.getFloatExtra(FieldNames.TARGET, 0));
            newTracker.setTargetDate(data.getIntExtra(FieldNames.YEAR, 2015)
                    , data.getIntExtra(FieldNames.MONTH, 1)
                    , data.getIntExtra(FieldNames.DAY, 1)
                    , data.getBooleanExtra(FieldNames.RECURRING, false));
            createTrackableView(currentUser.getTrackers().size() - 1);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // animateTrackerViewsFromZero();

    }

    private void animateProgressAdded() {
        int count = layout.getChildCount();
        TrackableView view = null;
        for (int i = 0; i < count; i++) {
            view = (TrackableView) layout.getChildAt(i);
            //view.animateProgressAdded();
        }
    }

    private void animateTrackerViewsFromZero() {
        int count = layout.getChildCount();
        TrackableView view = null;
        for (int i = 0; i < count; i++) {
            view = (TrackableView) layout.getChildAt(i);
            view.animateFromZero((int) Math.floor(currentUser.getTrackers().get(i).getProgressPercent() * 100));
        }
    }
}
