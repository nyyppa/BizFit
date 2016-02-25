package com.bizfit.bizfit.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;

import com.bizfit.bizfit.MyAlarmService;
import com.bizfit.bizfit.OurService;
import com.bizfit.bizfit.User;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.fragments.PagerAdapter;
import com.bizfit.bizfit.fragments.TabFragment1;
import com.bizfit.bizfit.fragments.TabFragment2;
import com.bizfit.bizfit.fragments.TabFragment3;
import com.bizfit.bizfit.utils.FieldNames;
import com.bizfit.bizfit.R;
import com.bizfit.bizfit.views.TrackableViewInflater;
import com.bizfit.bizfit.views.Separator;
import com.bizfit.bizfit.views.TrackableViewBase;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 * Displays the content of home screen.
 */
public class MainActivity extends AppCompatActivity {
    public static Activity activity = null;
    private static final int GET_NEW_GOAL = 1;
    private LinearLayout layout;
    public static User currentUser;
    private LinkedList<TrackableViewBase> trackableViews;

    public static long lastOpen;
    public static long lastMessageTime;
    private static TrackableViewInflater inflater;

    // Which kind of view should be displayed on the home screen.
    private static final TrackableViewInflater.ViewType type = TrackableViewInflater.ViewType.ALTERNATIVE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inflater = new TrackableViewInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        trackableViews = new LinkedList<>();
        /*thread = new PauseableThread(1000);
        thread.start();
        */
        Intent intent = new Intent(this, OurService.class);
        startService(intent);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void startBackGroundService(){
        Intent myIntent = new Intent(MainActivity.this, MyAlarmService.class);

        PendingIntent pendingIntent = PendingIntent.getService(MainActivity.this, 0, myIntent, 0);



        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);


        long time= TimeUnit.SECONDS.toMillis(60);
        //alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,time, time, pendingIntent);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, time, time, pendingIntent);


    }
    protected void onStart() {
        super.onStart();
        lastOpen = System.currentTimeMillis();
        activity = this;
        currentUser = User.getLastUser();
        layout = (LinearLayout) findViewById(R.id.goal_container);
        createTrackableViews();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchAddTrackerActivity();
            }
        });

        startBackGroundService();

        //animateTrackerViewsFromZero();
        //NotificationSender.sendNotification("t");
    }

    /**
     * Creates content to the screen which needs to be created dynamically.
     */
    private void createTrackableViews() {

        User.SortedTrackers trakers = currentUser.getAlpapheticalSortedTrackers(true);

        for (int i = 0; i < trakers.currentTrackers.size(); i++) {
            if (i == 0 || trakers.currentTrackers.get(i).getName().charAt(0) !=
                    trakers.currentTrackers.get(i - 1).getName().charAt(0)) {
                layout.addView(createSeparator(
                        trakers.currentTrackers.get(i).getName().charAt(0) + ""
                ));
            }

            trackableViews.add(inflater.inflate(trakers.currentTrackers.get(i), layout, type));
        }

        for (int i = 0; i < trakers.expiredTrackers.size(); i++) {

            if (i == 0 || trakers.expiredTrackers.get(i).getName().charAt(0) !=
                    trakers.expiredTrackers.get(i - 1).getName().charAt(0)) {
                layout.addView(createSeparator(
                        trakers.expiredTrackers.get(i).getName().charAt(0) + ""
                ));
            }

            trackableViews.add(inflater.inflate(trakers.expiredTrackers.get(i), layout, type));
        }

        // Make the bottom empty space visible if content is present.
        if (trackableViews.size() > 0)
            ((Space) findViewById(R.id.empty_space)).setVisibility(View.VISIBLE);
    }

    private Separator createSeparator(String label) {

        //TODO Make Separator .xml based
        Separator separator = new Separator(getBaseContext(), null);
        separator.setLabel(label);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT
                , (int) (getResources().getDimension(R.dimen.separator_height)));
        separator.setLayoutParams(param);
        separator.setPadding((int) (getResources().getDimension(R.dimen.activity_vertical_margin))
                , (int) (getResources().getDimension(R.dimen.separator_padding_top))
                , (int) (getResources().getDimension(R.dimen.activity_vertical_margin))
                , (int) (getResources().getDimension(R.dimen.separator_padding_bottom)));

        separator.setLabelPaddingBottom((int) (getResources().getDimension(R.dimen.separator_label_padding_bottom)));
        separator.setClickable(false);

        return separator;
    }

    public void launchViewTrackerActivity(Tracker tracker) {
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
        currentUser=null;
        activity=null;
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
            newTracker.setColor(data.getIntExtra(FieldNames.COLOR, R.color.colorAccent));
            newTracker.setTargetDate(data.getIntExtra(FieldNames.YEAR, 2015)
                    , data.getIntExtra(FieldNames.MONTH, 1)
                    , data.getIntExtra(FieldNames.DAY, 1)
                    , data.getBooleanExtra(FieldNames.RECURRING, false
            ));

            inflater.inflate(newTracker, layout, type);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        animateTrackerViewsFromZero();
    }

    private void animateTrackerViewsFromZero() {
        for (int i = 0; i < trackableViews.size(); i++) {
            trackableViews.get(i).animateFromZero();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        final ScrollView view = (ScrollView) findViewById(R.id.scroll_view);

        // outState.putIntArray(FieldNames.SCROLL_POS,
        //       new int[]{view.getScrollX(), view.getScrollY()});
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final ScrollView view = (ScrollView) findViewById(R.id.scroll_view);

        final int[] position = savedInstanceState.getIntArray(FieldNames.SCROLL_POS);
        if (position != null)
            layout.post(new Runnable() {
                public void run() {
                    view.scrollTo(position[0], position[1]);
                }
            });
    }

    private void setupViewPager(ViewPager viewPager) {
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TabFragment1(), "ONE");
        adapter.addFragment(new TabFragment2(), "TWO");
        adapter.addFragment(new TabFragment3(), "THREE");
        viewPager.setAdapter(adapter);
    }
}
