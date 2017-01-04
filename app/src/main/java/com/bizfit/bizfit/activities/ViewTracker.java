package com.bizfit.bizfit.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.User;
import com.bizfit.bizfit.fragments.PagerAdapter;
import com.bizfit.bizfit.fragments.ViewTrackerFragment;
import com.bizfit.bizfit.utils.FieldNames;

/**
 * Displays information about the Tracker.
 */
public class ViewTracker extends AppCompatActivity implements User.UserLoadedListener {

    private ViewPager viewPager;
    private PagerAdapter adapter;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tracker);
        this.index = (int) getIntent().getSerializableExtra(FieldNames.INDEX);
        viewPager = (ViewPager) findViewById(R.id.pager_view_tracker);
        adapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        User.getLastUser(this, this, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.grey_600));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_tracker, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void UserLoaded(final User user) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ViewTrackerFragment fragment;
                Tracker[] trackers = user.getTrackers();
                for (int i = 0; i < trackers.length; i++) {
                    fragment = ViewTrackerFragment.newInstance(trackers[i]);
                    adapter.addFragment(fragment, trackers[i].getName());
                }

                adapter.notifyDataSetChanged();
                viewPager.setCurrentItem(index);
            }
        });
    }
}
