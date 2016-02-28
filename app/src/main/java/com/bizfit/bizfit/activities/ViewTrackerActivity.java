package com.bizfit.bizfit.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.fragments.PagerAdapter;
import com.bizfit.bizfit.fragments.ViewTrackerFragment;
import com.bizfit.bizfit.utils.FieldNames;

/**
 * Displays information about the Tracker.
 */
public class ViewTrackerActivity extends AppCompatActivity {
    /**
     * Parent activity
     */
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tracker);
        activity = this;
        Tracker[] trackers = (Tracker[]) (getIntent().getSerializableExtra(FieldNames.TRACKERS));
        int index = (int) getIntent().getSerializableExtra(FieldNames.INDEX);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.fab_add_progress);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialoque();
            }
        });
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager_view_tracker);
        setupViewPager(viewPager, trackers);
        viewPager.setCurrentItem(index);
    }

    private void setupViewPager(ViewPager viewPager, Tracker[] trackers) {
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());

        for(int i = 0; i < trackers.length; i++) {
            adapter.addFragment(new ViewTrackerFragment(), trackers[i].getName());
        }

        viewPager.setAdapter(adapter);
    }

    private void toolAndStatusbarStylize(Toolbar toolbar) {
        toolbar.setBackgroundColor(getResources().getColor(R.color.gray_50));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.gray_600));
        }
    }

    /**
     * Opens a dialogue fragment used to inquire progress from userName.
     * <p/>
     * The inputted data is added to the Tracker's total progress.
     * TODO error handling and overall better fragment. Option to use slider.
     */
    private void openDialoque() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Amount to add");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        //input.setHighlightColor(host.getColor());
        //input.setDrawingCacheBackgroundColor(host.getColor());
        //input.getBackground().mutate().setColorFilter(host.getColor(), PorterDuff.Mode.SRC_ATOP);
       /** try {
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(input, R.drawable.cursor);
        } catch (Exception ignored) {
        }*/
        /**
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                float progress = Float.parseFloat(input.getText().toString());
                host.addProgress(progress);
                totalProgressChart.update();
                dailyProgressChart.update();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }

        });

        // TODO Styling OUT of Java.
        AlertDialog dialog = builder.create();
        dialog.show();

        Button b1 = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        if (b1 != null)
            b1.setTextColor(host.getColor());

        Button b2 = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (b2 != null)
            b2.setTextColor(host.getColor());
         */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_tracker, menu);
        return true;
    }
    /**
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
    }*/
}
