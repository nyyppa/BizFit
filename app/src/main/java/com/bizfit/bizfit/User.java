package com.bizfit.bizfit;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bizfit.bizfit.activities.MainPage;

import java.io.File;
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
    List<Tracker> trackers;
    public String userName;
    private transient static File saveDir;
    private transient static Context context;
    private transient static User currentUser;
    int lastTrackerID;
    int nextFreeDailyProgressID;
    private static final int dbVersion = 23;
    int userNumber;
    static List<UserLoadedListener> listeners = new ArrayList<>(0);
    public boolean saveUser = false;
    static List<Tracker> trackersToDelete = new ArrayList<>(0);
    static DataBaseThread thread;

    public Tracker getTrackerByIndex(int index) {
        return trackers.get(index);
    }

    static public int getNextFreeDailyProgressID() {
        currentUser.nextFreeDailyProgressID++;
        return currentUser.nextFreeDailyProgressID;
    }

    public static void update(Context c) {
        context = c;
        getLastUser(new UserLoadedListener() {
            @Override
            public void UserLoaded(User user) {
                for (int i = 0; i < user.trackers.size(); i++) {
                    user.trackers.get(i).update();
                }
            }
        });

    }


    User(String userName) {
        this.userName = userName;

        if (trackers == null) {
            trackers = new ArrayList<>(0);
        }
    }

    /**
     * Checks if userName has already Tracker with given NAME
     *
     * @param name NAME to check
     * @return true if tracker is found and false if tracker is not found
     */
    public boolean isTrackerNameAlreadyInUse(String name) {
        for (int i = 0; i < trackers.size(); i++) {
            if (name.equals(trackers.get(i).getName())) {
                return true;
            }
        }
        return false;
    }


    public SortedTrackers getTimeRemainingSortedTrackers(boolean ascending) {
        final int asc = ascending ? 1 : -1;
        SortedTrackers sorted = new SortedTrackers();
        Comparator<Tracker> t = new Comparator<Tracker>() {
            @Override
            public int compare(Tracker lhs, Tracker rhs) {
                return asc * (lhs.getRemainingTimeMillis() - rhs.getRemainingTimeMillis());
            }
        };
        Collections.sort(sorted.currentTrackers, t);
        Collections.sort(sorted.expiredTrackers, t);
        return sorted;
    }

    public SortedTrackers getProgressComparedToTimeSortedTrackers(boolean ascending) {
        final int asc = ascending ? 1 : -1;
        SortedTrackers sorted = new SortedTrackers();
        Comparator<Tracker> t = new Comparator<Tracker>() {
            @Override
            public int compare(Tracker lhs, Tracker rhs) {
                return asc * ((int) (lhs.getProgressComperedToTime() * 100) - (int) (rhs.getProgressComperedToTime() * 100));
            }
        };
        Collections.sort(sorted.currentTrackers, t);
        Collections.sort(sorted.expiredTrackers, t);
        return sorted;
    }

    public SortedTrackers getAlpapheticalSortedTrackers(boolean ascending) {
        final int asc = ascending ? 1 : -1;
        SortedTrackers sorted = new SortedTrackers();
        Comparator<Tracker> t = new Comparator<Tracker>() {
            @Override
            public int compare(Tracker lhs, Tracker rhs) {
                return asc * (lhs.getName().compareToIgnoreCase(rhs.getName()));
            }
        };
        Collections.sort(sorted.currentTrackers, t);
        Collections.sort(sorted.expiredTrackers, t);
        return sorted;
    }

    /**
     * Adds tracker to users information and then saves everything to the memory
     *
     * @param t Tracker to add for user
     */
    public void addTracker(Tracker t) {
        trackers.add(t);
        t.parentUser = this;
        t.id = lastTrackerID;
        lastTrackerID++;
        updateIndexes();
        save();
    }

    public void updateIndexes() {
        for (int i = 0; i < trackers.size(); i++) {
            trackers.get(i).index = i;
        }
    }

    /**
     * @return Returns all the users trackers
     */
    public Tracker[] getTrackers() {
        Tracker[] t = new Tracker[trackers.size()];
        return trackers.toArray(t);
    }


    public int getAmoutOfTrackes() {
        return trackers.size();
    }

    /**
     * Removes Tracker from users infromation and then saves everything
     *
     * @param t Tracker to remove
     * @return ArrayList containing all of the users Trackers
     */
    public void removeTracker(Tracker t) {
        Iterator<Tracker> iterator = trackers.iterator();
        while (iterator.hasNext()) {
            Tracker current = iterator.next();
            if (current == t) {
                iterator.remove();
                trackersToDelete.add(t);
                WakeThread();
                break;
            }
        }

        updateIndexes();
    }

    /**
     * Saves users current information
     *
     * @throws Exception Everything that can go wrong
     */
    public void save() {
        saveUser = true;
        WakeThread();
    }

    public static void getLastUser(UserLoadedListener listener) {
        listeners.add(listener);
        WakeThread();
    }

    private static void WakeThread() {
        if (thread == null) {
            thread = new DataBaseThread();
            thread.start();
        }
        synchronized (thread) {
            thread.notify();
            thread.sleepThread = false;
        }


    }


    private static class DataBaseThread extends Thread {
        DBHelper db;
        SQLiteDatabase d;
        boolean sleepThread;

        @Override
        public void run() {
            while (true) {
                super.run();
                if (db == null) {
                    db = new DBHelper(MainPage.activity, "database1", null, dbVersion);
                }
                if (d == null) {
                    d = db.getWritableDatabase();
                }
                if (currentUser == null) {
                    currentUser = db.readUser(d);
                }
                if (currentUser.saveUser) {
                    db.saveUser(d, currentUser);
                    currentUser.saveUser = false;
                }
                Iterator<Tracker> iterator = trackersToDelete.iterator();
                while (iterator.hasNext()) {
                    iterator.next();
                    iterator.remove();
                }
                Iterator<UserLoadedListener> iterator1 = listeners.iterator();
                while (iterator1.hasNext()) {
                    final UserLoadedListener userLoadedListener = iterator1.next();
                    userLoadedListener.UserLoaded(currentUser);
                    iterator1.remove();
                }
                sleepThread = true;
                while (sleepThread) {
                    synchronized (thread) {
                        try {
                            thread.wait(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }


    public class SortedTrackers {
        public List<Tracker> currentTrackers = new ArrayList<Tracker>(0);
        public List<Tracker> expiredTrackers = new ArrayList<Tracker>(0);

        SortedTrackers() {
            for (int i = 0; i < trackers.size(); i++) {
                if (trackers.get(i).completed) {
                    expiredTrackers.add(trackers.get(i));
                } else {
                    currentTrackers.add(trackers.get(i));
                }
            }
        }
    }

    public interface UserLoadedListener {
        void UserLoaded(User user);
    }
}
