package com.bizfit.bizfit.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.User;
import com.bizfit.bizfit.fragments.PagerAdapter;
import com.bizfit.bizfit.fragments.ViewTrackerFragment;
import com.bizfit.bizfit.utils.FieldNames;

/**
 * Displays information about the Tracker.
 */
public class ViewTrackerActivity extends AppCompatActivity implements User.UserLoadedListener {

    ViewPager viewPager;
    PagerAdapter adapter;
    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tracker);
        this.index = (int) getIntent().getSerializableExtra(FieldNames.INDEX);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_progress);
        viewPager = (ViewPager) findViewById(R.id.pager_view_tracker);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialoq();
            }
        });
        adapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        User.getLastUser(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.grey_600));
        }
    }

    /**
     * Opens a dialogue fragment used to inquire progress from userName.
     * <p/>
     * The inputted data is added to the Tracker's total progress.
     */
    private void openDialoq() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // TODO reference res.
        builder.setTitle("Amount to add");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        final PagerAdapter adapter = (PagerAdapter) viewPager.getAdapter();

        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                float progress = Float.parseFloat(input.getText().toString());
                ViewTrackerFragment fragment = (ViewTrackerFragment) adapter.getItem(viewPager.getCurrentItem());
                fragment.getTracker().setDataChangedListener(fragment);
                fragment.getTracker().addProgress(progress);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }

        });


        AlertDialog dialog = builder.create();
        dialog.show();
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
                Log.d("ViewTrackerActivity", "User load callback");
                for (int i = 0; i < trackers.length; i++) {
                    fragment = ViewTrackerFragment.newInstance(trackers[i]);
                    adapter.addFragment(fragment, trackers[i].getName());
                }
                viewPager.setCurrentItem(index);
            }
        });
    }
}
