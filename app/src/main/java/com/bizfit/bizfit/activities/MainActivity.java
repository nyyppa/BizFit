package com.bizfit.bizfit.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bizfit.bizfit.MyAlarmService;
import com.bizfit.bizfit.R;
import com.bizfit.bizfit.fragments.PagerAdapter;
import com.bizfit.bizfit.fragments.TabCoaches;
import com.bizfit.bizfit.fragments.TabMessages;
import com.bizfit.bizfit.fragments.TabTrackables;
import com.bizfit.bizfit.utils.RecyclerViewAdapter;
import com.bizfit.bizfit.views.ViewPagerNoSwipes;

import java.util.concurrent.TimeUnit;

/**
 * Displays the content of home screen.
 */
public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.RecyclerViewItemClicked {
    private static final int PAGE_LIMIT = 3;
    public static Activity activity = null;
    public static long lastOpen;
    public static long lastMessageTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //SQLiteDatabase db=this.openOrCreateDatabase("database",MODE_PRIVATE,null);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_view_tracker);
        setSupportActionBar(toolbar);
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager_main);
        setupViewPager(viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TabTrackables) getSupportFragmentManager().getFragments().get(0)).launchAddTrackerActivity();
            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                fab.clearAnimation();

                switch (position) {
                    case 0:
                        fab.show();
                        ((AppBarLayout) (findViewById(R.id.app_bar_main))).setExpanded(true);
                        break;

                    default:
                        ((AppBarLayout) (findViewById(R.id.app_bar_main))).setExpanded(true);
                        fab.hide();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void startBackGroundService() {
        Intent myIntent = new Intent(MainActivity.this, MyAlarmService.class);
        PendingIntent pendingIntent = PendingIntent.getService(MainActivity.this, 0, myIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long time = TimeUnit.SECONDS.toMillis(60);
        //alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,time, time, pendingIntent);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, time, time, pendingIntent);


    }

    @Override
    protected void onStart() {
        lastOpen = System.currentTimeMillis();
        activity = this;
        super.onStart();
        startBackGroundService();
        /* DBHelper db=new DBHelper(this,"database1",null,5);
        SQLiteDatabase d=db.getWritableDatabase();
        db.saveUser(d, User.getLastUser());
        db.readUser(d);
*/
        //NotificationSender.sendNotification("t");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activity = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Populates the ViewPager with tabs.
     *
     * @param viewPager ViewPager to populate.
     */
    private void setupViewPager(ViewPager viewPager) {
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TabTrackables(), getResources().getString(R.string.tab_my_trackers));
        adapter.addFragment(new TabCoaches(), getResources().getString(R.string.tab_coaches));
        adapter.addFragment(new TabMessages(), getResources().getString(R.string.tab_messages));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(PAGE_LIMIT);

        if (viewPager instanceof ViewPagerNoSwipes)
            ((ViewPagerNoSwipes) viewPager).setPagingEnabled(false);
    }

    @Override
    public void onItemClick(RecyclerView.ViewHolder vh) {
        if (vh instanceof RecyclerViewAdapter.ViewHolder) {
            ((TabTrackables) getSupportFragmentManager().getFragments().get(0)).launchViewTrackerActivity(vh);
        }
    }
}
