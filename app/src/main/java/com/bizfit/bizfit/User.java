package com.bizfit.bizfit;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;

import com.bizfit.bizfit.activities.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class User implements java.io.Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 8425799364006222365L;
    ArrayList<Tracker> trackers;
    public String userName;
    private transient static File saveDir;
    private transient static Context context;
    private transient static User currentUser;
    int lastTrackerID;
    int nextFreeDailyProgressID;
    private static final int dbVersion=23;
    int userNumber;

    public Tracker getTrackerByIndex(int index){
        return trackers.get(index);
    }

    public int getNextFreeDailyProgressID(){
        nextFreeDailyProgressID++;
        return nextFreeDailyProgressID;
    }
    public static void update(Context c){
        User u;
        context=c;
        u=getLastUser();
        for(int i=0;i<u.trackers.size();i++){
            u.trackers.get(i).update();
        }
    }
    /**
     * Do not manually construct new saveStates, rather call User.getInstance(String userName)
     *
     * @param userName User NAME
     */
    User(String userName) {
        this.userName =userName;

        if (trackers == null) {
            trackers = new ArrayList<Tracker>(0);
        }
    }

    /**
     * Checks if userName has already Tracker with given NAME
     * @param name  NAME to check
     * @return  true if tracker is found and false if tracker is not found
     */
    public boolean isTrackerNameAlreadyInUse(String name){
        for(int i=0;i<trackers.size();i++){
            if(name.equals(trackers.get(i).getName())){
                return true;
            }
        }
        return false;
    }






    public SortedTrackers getTimeRemainingSortedTrackers(boolean ascending){
        final int asc=ascending?1:-1;
        SortedTrackers sorted=new SortedTrackers();
        Comparator<Tracker> t=new Comparator<Tracker>() {
            @Override
            public int compare(Tracker lhs, Tracker rhs) {
                return asc*(lhs.getRemainingTimeMillis()-rhs.getRemainingTimeMillis());
            }
        };
        Collections.sort(sorted.currentTrackers, t);
        Collections.sort(sorted.expiredTrackers, t);
        return sorted;
    }

    public SortedTrackers getProgressComparedToTimeSortedTrackers(boolean ascending){
        final int asc=ascending?1:-1;
        SortedTrackers sorted=new SortedTrackers();
        Comparator<Tracker> t=new Comparator<Tracker>() {
            @Override
            public int compare(Tracker lhs, Tracker rhs) {
                return asc*((int)(lhs.getProgressComperedToTime()*100)-(int)(rhs.getProgressComperedToTime()*100));
            }
        };
        Collections.sort(sorted.currentTrackers, t);
        Collections.sort(sorted.expiredTrackers, t);
        return sorted;
    }

    public SortedTrackers getAlpapheticalSortedTrackers(boolean ascending){
        final int asc=ascending?1:-1;
        SortedTrackers sorted=new SortedTrackers();
        Comparator<Tracker> t=new Comparator<Tracker>() {
            @Override
            public int compare(Tracker lhs, Tracker rhs) {
                return asc*(lhs.getName().compareToIgnoreCase(rhs.getName()));
            }
        };
        Collections.sort(sorted.currentTrackers, t);
        Collections.sort(sorted.expiredTrackers, t);
        return sorted;
    }

    /**
     * Adds tracker to users information and then saves everything to the memory
     *
     * @param t Tracker to add for userName
     * @return ArrayList containing all of the users Trackers
     */
    public ArrayList<Tracker> addTracker(Tracker t) {
        trackers.add(t);
        t.parentUser = this;
        System.out.println(t.parentUser);
        t.id=lastTrackerID;
        lastTrackerID++;
        updateIndexes();
        try {
            save();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trackers;
    }
    public void updateIndexes(){
        for(int i=0;i<trackers.size();i++){
            trackers.get(i).index=i;
        }
    }
    /**
     * @return Returns all the users trackers
     */
    public Tracker[] getTrackers() {
        Tracker[] t=new Tracker[trackers.size()];
        return trackers.toArray(t);
    }


    public int getAmoutOfTrackes(){
        return trackers.size();
    }

    /**
     * Removes Tracker from users infromation and then saves everything
     *
     * @param t Tracker to remove
     * @return ArrayList containing all of the users Trackers
     */
    public ArrayList<Tracker> removeTracker(Tracker t) {
        Iterator<Tracker> iterator = trackers.iterator();
        while (iterator.hasNext()) {
            Tracker current = iterator.next();
            if (current == t) {
                iterator.remove();
                DBHelper db=new DBHelper(MainActivity.activity,"database1",null,dbVersion);
                SQLiteDatabase d=db.getWritableDatabase();
                db.deleteTracker(d,t);
                d.close();
                db.close();
                break;
            }
        }

        updateIndexes();
        return trackers;
    }

    /**
     * Saves users current information
     *
     * @throws Exception Everything that can go wrong
     */
    public void save() throws Exception {
        DBHelper db=new DBHelper(MainActivity.activity,"database1",null,dbVersion);
        SQLiteDatabase d=db.getWritableDatabase();
        db.saveUser(d, this);
        d.close();
        db.close();
    }

    public static User getLastUser() {


        if(currentUser!=null){
            return currentUser;
        }
        DBHelper db=new DBHelper(MainActivity.activity,"database1",null,dbVersion);
        SQLiteDatabase d=db.getWritableDatabase();
        currentUser=db.readUser(d);
        d.close();
        db.close();

        return currentUser;

    }





    public class SortedTrackers{
        public List<Tracker>currentTrackers=new ArrayList<Tracker>(0);
        public List<Tracker>expiredTrackers=new ArrayList<Tracker>(0);
        SortedTrackers(){
            for(int i=0;i<trackers.size();i++){
                if(trackers.get(i).completed){
                    expiredTrackers.add(trackers.get(i));
                }else {
                    currentTrackers.add(trackers.get(i));
                }
            }
        }
    }
}
