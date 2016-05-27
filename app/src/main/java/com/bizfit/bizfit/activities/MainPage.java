package com.bizfit.bizfit.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
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
import com.bizfit.bizfit.utils.RecyclerViewAdapterStoreRow;
import com.bizfit.bizfit.utils.RecyclerViewAdapterTrackers;
import com.bizfit.bizfit.views.ViewPagerNoSwipes;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Displays the content of home screen.
 */
public class MainPage extends AppCompatActivity implements
        RecyclerViewAdapterTrackers.RecyclerViewItemClicked,
        RecyclerViewAdapterStoreRow.StoreItemClicked {

    /**
     * Maximum number of cached pages.
     */
    private static final int CACHED_PAGE_LIMIT = 3;
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

                // TODO DELETE THIS ABOMINATION!
                boolean success = false;
                for (int i = 0; i < getSupportFragmentManager().getFragments().size() && !success; i++) {
                    try {
                        ((TabTrackables) getSupportFragmentManager().getFragments().get(i)).launchAddTrackerActivity();
                        success = true;
                    } catch (ClassCastException e) {

                    }
                }
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
        Intent myIntent = new Intent(MainPage.this, MyAlarmService.class);
        PendingIntent pendingIntent = PendingIntent.getService(MainPage.this, 0, myIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long time = TimeUnit.SECONDS.toMillis(60);
        //alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,time, time, pendingIntent);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, time, time, pendingIntent);
    }

    @Override
    protected void onStart() {
        lastOpen = System.currentTimeMillis();
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
        // Inflate the menu. Adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        //SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        //searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_settings:
                startActivity(new Intent(this, Settings.class));
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
        adapter.addFragment(new TabTrackables(), getResources().getString(R.string.title_tab_my_trackers));
        adapter.addFragment(new TabCoaches(), getResources().getString(R.string.title_tab_coaches));
        adapter.addFragment(new TabMessages(), getResources().getString(R.string.title_tab_messages));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(CACHED_PAGE_LIMIT);

        if (viewPager instanceof ViewPagerNoSwipes)
            ((ViewPagerNoSwipes) viewPager).setPagingEnabled(false);
    }

    @Override
    public void onItemClick(RecyclerView.ViewHolder vh) {
        if (vh instanceof RecyclerViewAdapterTrackers.ViewHolderTracker) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();

            // TODO DELETE THIS ABOMINATION!
            boolean success = false;
            for (int i = 0; i < fragments.size() && !success; i++) {
                try {
                    ((TabTrackables) getSupportFragmentManager().getFragments().get(i)).launchViewTrackerActivity(vh);
                    success = true;
                } catch (ClassCastException e) {

                }
            }
        }
    }

    @Override
    public void itemClicked(RecyclerViewAdapterStoreRow.ViewHolderStoreItem viewHolderStoreItem) {
        startActivity(new Intent(this, CoachPage.class));
    }
}