package com.bizfit.bizfitUusYritysKeskusAlpha.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.bizfit.bizfitUusYritysKeskusAlpha.Coach;
import com.bizfit.bizfitUusYritysKeskusAlpha.Contact;
import com.bizfit.bizfitUusYritysKeskusAlpha.DebugPrinter;
import com.bizfit.bizfitUusYritysKeskusAlpha.MyAlarmService;
import com.bizfit.bizfitUusYritysKeskusAlpha.R;
import com.bizfit.bizfitUusYritysKeskusAlpha.User;
import com.bizfit.bizfitUusYritysKeskusAlpha.fragments.PagerAdapter;
import com.bizfit.bizfitUusYritysKeskusAlpha.fragments.TabCoaches2;
import com.bizfit.bizfitUusYritysKeskusAlpha.fragments.TabConversationList;

import com.bizfit.bizfitUusYritysKeskusAlpha.RecyclerViews.RecyclerViewAdapterStoreRow;
import com.bizfit.bizfitUusYritysKeskusAlpha.fragments.TabConversationRequests;
import com.bizfit.bizfitUusYritysKeskusAlpha.utils.Constants;
import com.bizfit.bizfitUusYritysKeskusAlpha.utils.DBHelper;
import com.bizfit.bizfitUusYritysKeskusAlpha.utils.StoreRow;
import com.bizfit.bizfitUusYritysKeskusAlpha.views.ViewPagerNoSwipes;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Displays the content of home screen.
 */
public class MainPage extends AppCompatActivity implements
        RecyclerViewAdapterStoreRow.StoreItemClicked {

    /**
     * Maximum number of cached pages.
     */
    private static final int CACHED_PAGE_LIMIT = 3;
    public static long lastOpen;
    public static long lastMessageTime;
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //SQLiteDatabase db=this.openOrCreateDatabase("database",MODE_PRIVATE,null);
        findUser();
        setContentView(R.layout.activity_main_v2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_view_tracker);
        toolbar.setTitle("Bizfit "+ Constants.version);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Bizfit "+ Constants.version);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager_main);
        setupViewPager(viewPager);

        BottomNavigationView bottomNavigation = (BottomNavigationView) findViewById(R.id.bottomNavigationItemView);
        bottomNavigation.setSelectedItemId(R.id.action_coaches);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {


            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                boolean switched = false;

                switch(item.getItemId()) {
                    case R.id.action_profile:
                        break;

                    case R.id.action_coaches:
                        viewPager.setCurrentItem(0);
                        switched = true;
                        break;

                    case R.id.action_messages:
                        viewPager.setCurrentItem(1);
                        switched = true;
                        break;

                    case R.id.action_settings:
                        break;
                }

                return switched;
            }
        });

        //TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        //tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(getIntent().getIntExtra("goToTab", 0));
        //final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.hide(); //because tab is not switched, activated in onPageSelected

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position) {
                //fab.clearAnimation();
                switch (position) {
                    case 0:
                        //fab.show();
                        ((AppBarLayout) (findViewById(R.id.app_bar_main))).setExpanded(true);
                        break;

                    default:
                        ((AppBarLayout) (findViewById(R.id.app_bar_main))).setExpanded(true);
                        //fab.hide();
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
        long time = TimeUnit.SECONDS.toMillis(10);

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,time, time, pendingIntent);

        //alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, time, time, pendingIntent);
    }

    @Override
    protected void onStart() {
        lastOpen = System.currentTimeMillis();
        super.onStart();
        startBackGroundService();
        if(getIntent().hasExtra("coachID"))
        {
            String s = getIntent().getStringExtra("coachID");
            getIntent().removeExtra("coachID");
            MessageActivity.startChat(this, s);
        }
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

        // search hidden until works
        /*

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        */

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
                PopupWindow popUp = popupWindowsort();
                popUp.showAsDropDown(findViewById(R.id.toolbar_settings), 0, 0); // show popup like dropdown list
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
        adapter.addFragment(new TabCoaches2(), getResources().getString(R.string.title_tab_coaches));
        adapter.addFragment(new TabConversationList(), getResources().getString(R.string.title_tab_messages));
        adapter.addFragment(new TabConversationRequests(),"Requests");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(CACHED_PAGE_LIMIT);

        if (viewPager instanceof ViewPagerNoSwipes)
            ((ViewPagerNoSwipes) viewPager).setPagingEnabled(false);
    }



    @Override
    public void itemClicked(StoreRow.StoreItem data) {
        Intent intent = new Intent(this, CoachPage.class);
        data.fillIntent(intent);
        startActivity(intent);
    }

    public void coachSelected(Coach coach) {
        Intent intent = new Intent(this, CoachPage.class);
        coach.fillIntent(intent);
        startActivity(intent);
    }

	/**
     * if user has assigned userName it's logged in, otherwise we will use the default user, so its not logged in
     */
    private void findUser()
    {
        if(getIntent().hasExtra("userName"))
        {
            User user=User.getLastUser(null, this, getIntent().getStringExtra("userName"));
            user.setMyContactInfo(new Contact(getIntent().getStringExtra("firstName"),getIntent().getStringExtra("lastName"),getIntent().getStringExtra("userName")));
            if(getIntent().getStringExtra("userName").equals("jari.myllymaki@gmail.com+UAHUARGAYGEYAGEHAGDASDHJKA")){
                MediaPlayer mediaPlayer=MediaPlayer.create(this,R.raw.good_morning_vietnam);
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        System.gc();
                    }

                    @Override
                    protected void finalize() throws Throwable {
                        super.finalize();
                    }
                });
            }
        }
        /*else
        {
            User.getLastUser(null, this, "default");
        }
        */
    }

    /**
     * show popup window method reuturn PopupWindow
     */
    private PopupWindow popupWindowsort() {

        // initialize a pop up window type
        popupWindow = new PopupWindow(this);

        ArrayList<String> sortList = new ArrayList<String>();
        sortList.add(getString(R.string.logout));
        sortList.add(getString(R.string.support));
        sortList.add(getString(R.string.security));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,
                sortList);
        // the drop down list is a list view
        ListView listViewSort = new ListView(this);

        // set our adapter and pass our pop up window contents
        listViewSort.setAdapter(adapter);

        // set on item selected
        listViewSort.setOnItemClickListener(onItemClickListener());

        // some other visual settings for popup window
        popupWindow.setFocusable(true);
        popupWindow.setWidth(500);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.dialog_holo_light_frame));

        // set the listview as popup content
        popupWindow.setContentView(listViewSort);

        return popupWindow;
    }
    
    private AdapterView.OnItemClickListener onItemClickListener() {
        return new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {

                switch (position) {
                    // logout
                    case 0:
                        Intent intent = new Intent(MainPage.this,LoginActivity2.class);
                        User.signOut(intent,MainPage.this);
                        break;

                    // support
                    case 1:
                        Intent intent2 = new Intent(MainPage.this, Support.class);
                        startActivity(intent2);
                        break;

                    // security
                    case 2:
                        Intent intent3 = new Intent(MainPage.this, Security.class);
                        startActivity(intent3);
                        break;
                }

                dismissPopup();
            }
        };
    }

    private void dismissPopup() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        /*moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);*/
    }

}
