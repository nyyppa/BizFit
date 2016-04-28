package com.bizfit.bizfit;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import java.util.Map;

import android.util.Log;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import com.bizfit.bizfit.activities.MainPage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

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
    private static final int dbVersion = 26;
    int userNumber;
    static List<UserLoadedListener> listeners = new ArrayList<>(0);
    public boolean saveUser = false;
    static List<Tracker> trackersToDelete = new ArrayList<>(0);
    static DataBaseThread thread;


    public Tracker getTrackerByIndex(int index) {
        return trackers.get(index);
    }

    /**
     * converts user and all of it's dependensies to JSON
     * @return  JSON containing user and it's dependensies
     */
    public JSONObject toJSON(){
        JSONObject jsonObject=new JSONObject();
        JSONArray jsonArray=new JSONArray();
        try {
            jsonObject.put("userName",userName);
            jsonObject.put("lastTrackerID",lastTrackerID);
            jsonObject.put("nextFreeDailyProgressID",nextFreeDailyProgressID);
            jsonObject.put("userNumber",userNumber);
            for(int i=0;i<trackers.size();i++){
                jsonArray.put(trackers.get(i).toJSON());
            }
            jsonObject.put("trackers",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    static public int getNextFreeDailyProgressID() {
        currentUser.nextFreeDailyProgressID++;
        return currentUser.nextFreeDailyProgressID;
    }

    public static void update(Context c) {
        getLastUser(new UserLoadedListener() {
            @Override
            public void UserLoaded(User user) {
                for (int i = 0; i < user.trackers.size(); i++) {
                    user.trackers.get(i).update();
                }
            }
        },c);

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
        t.addParentUser(this);
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
     * Returs Users current Context
     * @return
     */
    public static Context getContext(){
        return context;
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

    /**
     * loads last user from database
     * @param listener  notifies this listener when user is loaded
     */
    public static void getLastUser(UserLoadedListener listener,Context c) {
        context=c;
        listeners.add(listener);
        WakeThread();
    }

    /**
     * wakes or creates new thread for local database
     */
    private static void WakeThread() {
        if (thread == null) {
            thread = new DataBaseThread(context);
            thread.start();
        }
        if(!thread.isAlive()){
            thread = new DataBaseThread(context);
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
        boolean exit=false;
        Context context;
        DataBaseThread(Context c){
            super();
            context=c;
        }

        @Override
        public void run() {
            while (true) {
                super.run();
                if (db == null) {

                    db = new DBHelper(context, "database1", null, dbVersion);
                }
                if (d == null) {
                    d = db.getWritableDatabase();
                }
                if (currentUser == null) {
                    currentUser = db.readUser(d);
                    NetWorkThread t=new NetWorkThread();
                    t.start();

                    /*try {
                        System.out.println(currentUser.toJSON().toString(4));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/

                }
                if (currentUser.saveUser) {
                    db.saveUser(d, currentUser);
                    currentUser.saveUser = false;
                }
                Iterator<Tracker> iterator = trackersToDelete.iterator();
                while (iterator.hasNext()) {
                    db.deleteTracker(d,iterator.next());
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
                if(exit){
                    db.close();
                    d.close();
                    return;
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
    private static class NetWorkThread extends Thread{


        public void run(){
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 500;

            try {
                URL url = new URL("https://bizfit-kaupunkiapina.c9users.io");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                System.out.println(conn.getURL());
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(currentUser.toJSON().toString());
                System.out.println(currentUser.toJSON().toString());
                // Starts the query
                writer.flush();
                conn.connect();
                int response = conn.getResponseCode();
                Log.d("meh", "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                BufferedReader r = new BufferedReader(new InputStreamReader(is));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    total.append(line).append('\n');
                }
                System.out.println(total);
                Map map=conn.getHeaderFields();
                for(Object key: map.keySet()){
                    System.out.println(key + " - " + map.get(key));

                }

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
