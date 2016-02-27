package com.bizfit.bizfit.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.bizfit.bizfit.MyAlarmService;
import com.bizfit.bizfit.OurService;
import com.bizfit.bizfit.User;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.fragments.PagerAdapter;
import com.bizfit.bizfit.fragments.TabTrackables;
import com.bizfit.bizfit.fragments.TabCoaches;
import com.bizfit.bizfit.fragments.TabHistory;
import com.bizfit.bizfit.utils.FieldNames;
import com.bizfit.bizfit.R;

import java.util.concurrent.TimeUnit;

/**
 * Displays the content of home screen.
 */
public class MainActivity extends AppCompatActivity {
    public static Activity activity = null;
    private static final int GET_NEW_GOAL = 1;
    public static User currentUser;
    public static long lastOpen;
    public static long lastMessageTime;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private TabTrackables trackablesTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*thread = new PauseableThread(1000);
        thread.start();
        */
        Intent intent = new Intent(this, OurService.class);
        startService(intent);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void startBackGroundService() {
        Intent myIntent = new Intent(MainActivity.this, MyAlarmService.class);
        PendingIntent pendingIntent = PendingIntent.getService(MainActivity.this, 0, myIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long time = TimeUnit.SECONDS.toMillis(60);
        //alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,time, time, pendingIntent);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, time, time, pendingIntent);


    }

    protected void onStart() {
        lastOpen = System.currentTimeMillis();
        activity = this;
        currentUser = User.getLastUser();
        super.onStart();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchAddTrackerActivity();
            }
        });

        startBackGroundService();
        //NotificationSender.sendNotification("t");
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
        currentUser = null;
        activity = null;
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

            trackablesTab.addTracker(newTracker, getLayoutInflater(), (ViewGroup)findViewById(R.id.goal_container));
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        trackablesTab = new TabTrackables();
        adapter.addFragment(trackablesTab, getResources().getString(R.string.tab_my_trackers));
        adapter.addFragment(new TabCoaches(), getResources().getString(R.string.tab_coaches));
        adapter.addFragment(new TabHistory(), getResources().getString(R.string.tab_history));
        viewPager.setAdapter(adapter);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        fab.show();
                        break;

                    default:
                        fab.hide();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public static User getCurrentUser() {
        return currentUser;
    }
}
