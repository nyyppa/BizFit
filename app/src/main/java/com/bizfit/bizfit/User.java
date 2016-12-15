package com.bizfit.bizfit;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Class that contains user information, calls local database when needed and holds user's trackers
 */

public class User implements java.io.Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 8425799364006222365L;
    private static final int dbVersion = 39;
    static List<UserLoadedListener> listeners = new ArrayList<>(0);
    static List<Tracker> trackersToDelete = new ArrayList<>(0);
    private transient static DataBaseThread thread;
    private transient static Context context;
    private transient static User currentUser;
    public String userName;
    public boolean saveUser = false;
    List<Tracker> trackers;
    int lastTrackerID;
    int nextFreeDailyProgressID;
    int userNumber;
    static boolean userLoaded=false;
    List<MyNewAndBetterConversation> conversations;
    private transient static Thread GetMessagesThread;
    //todo remove userNumber

    /**
     * Constructs user and it's dependencies from given JSONObject
     *
     * @param jsonObject JSONObject containing all the nessessary information
     */
    public User(JSONObject jsonObject)
    {
        JSONArray trackerArray=null;
        try
        {
            trackers = new ArrayList<>(0);
            if(jsonObject.has("trackers"))
            {
                System.out.println("Terse");
                trackerArray = jsonObject.getJSONArray("trackers");
                System.out.println("trackers"+jsonObject.getString("trackers"));

                for (int i = 0; i < trackerArray.length(); i++) {
                    System.out.println("Terse"+i);
                    Tracker t = new Tracker(trackerArray.getJSONObject(i));
                    trackers.add(t);
                    t.addParentUser(this);
                }
            }
            if(jsonObject.has("_id"))
            {
                userName = jsonObject.getString("_id");
            }
            if(jsonObject.has("lastTrackerID"))
            {
                lastTrackerID = jsonObject.getInt("lastTrackerID");
            }
            if(jsonObject.has("nextFreeDailyProgressID"))
            {
                nextFreeDailyProgressID = jsonObject.getInt("nextFreeDailyProgressID");
            }
            if(jsonObject.has("userNumber"))
            {
                userNumber = jsonObject.getInt("userNumber");
            }

            if(jsonObject.has("conversations"))
            {
                JSONArray conversationArray=jsonObject.getJSONArray("conversations");
                for(int i=0;i<conversationArray.length();i++)
                {
                    //MyNewAndBetterConversation conversation=new MyNewAndBetterConversation(conversationArray.getJSONObject(i),this);
                    addConversation(new MyNewAndBetterConversation(conversationArray.getJSONObject(i),this));
                }
            }



        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructs user with given username
     *
     * @param userName Username for user
     */
    User(String userName) {
        this.userName = userName;

        if (trackers == null) {
            trackers = new ArrayList<>(0);
        }
    }

    static public int getNextFreeDailyProgressID() {
        if (currentUser == null) {
            return -1;
        }
        currentUser.nextFreeDailyProgressID++;
        return currentUser.nextFreeDailyProgressID;
    }

    /**
     * Runs update for every tracker to keep their internal time moving
     *
     * @param c Context for sending notifications and loading user from internal database
     */
    public static void update(Context c) {
        getLastUser(new UserLoadedListener() {
            @Override
            public void UserLoaded(User user)
            {
                List<Tracker> trackers = user.getTrackerlist();
                for (int i = 0; i < trackers.size(); i++)
                {
                    trackers.get(i).update();
                }
            }
        }, c);

    }
    private List<Tracker> getTrackerlist()
    {
        if(trackers==null)
        {
            trackers = new ArrayList<>();
        }
        return trackers;
    }
    /**
     * Returns Users current Context
     *
     * @return
     */
    public static Context getContext() {
        return context;
    }

    /**
     * loads last user from database
     *
     * @param listener notifies this listener when user is loaded
     */
    public static void getLastUser(UserLoadedListener listener, Context c) {
        context = c;
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
        if (!thread.isAlive()) {
            thread = new DataBaseThread(context);
            thread.start();
        }
        synchronized (thread) {
            thread.notify();
            thread.sleepThread = false;
        }
    }

    /**
     * Loads user with given username from server database and notifies given listener when it's loaded
     *
     * @param userLoadedListener Listener to notify when ready, if null wont notify anything
     * @param userName           Username to find, if null will try to find users google account and use it
     */
    public static void loadUserFromNet(final UserLoadedListener userLoadedListener, String userName) {
        if (userName == null) {
            String name;
            final AccountManager manager = AccountManager.get(User.getContext());
            final Account[] accounts = manager.getAccountsByType("com.google");
            final int size = accounts.length;
            String[] names = new String[size];
            for (int i = 0; i < size; i++) {
                names[i] = accounts[i].name;

            }
            if (names.length > 0) {
                name = names[0];
            } else {
                name = "default";
            }
            userName = name;
        }
        System.out.println(userName+" userName");
        final String name = userName;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream is = null;
                // Only display the first 500 characters of the retrieved
                // web page content.
                int len = 500;

                try {
                    URL url = new URL("https://bizfit-nyyppa.c9users.io");
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    JSONObject jsonObject1 = new JSONObject();
                    try {
                        jsonObject1.put("_id", name);
                        jsonObject1.put("Job","load");
                        if(currentUser!=null){
                            try {
                                jsonObject1.put("checkSum",currentUser.checksum(currentUser));
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            }
                        }else{
                            jsonObject1.put("checkSum","0");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    writer.write(jsonObject1.toString());
                    writer.flush();
                    conn.connect();
                    int response = conn.getResponseCode();
                    if (response==200) {
                        is = conn.getInputStream();
                        // Convert the InputStream into a string
                        BufferedReader r = new BufferedReader(new InputStreamReader(is));
                        StringBuilder total = new StringBuilder();
                        String line;
                        while ((line = r.readLine()) != null) {
                            System.out.println(line);
                            total.append(line).append('\n');
                        }
                        System.out.println(total.toString());
                        try {
                            JSONObject jsonObject2 = new JSONObject(total.toString());
                            if(jsonObject2.has("user")){
                                currentUser = new User(jsonObject2.getJSONObject("user"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                    userLoaded=true;
                }
                if (userLoadedListener != null) {
                    userLoadedListener.UserLoaded(currentUser);
                }

            }
        });
        t.start();
    }

    /**
     * this is becouse cloned version has correct chatfragment
     * @param myNewAndBetterConversation
     */
    public void addClonedMyNewAndBetterConversation(MyNewAndBetterConversation myNewAndBetterConversation){
        for(int i=0;i<conversations.size();i++){
            MyNewAndBetterConversation oldConversation=conversations.get(i);
            if(oldConversation.getOther().equals(myNewAndBetterConversation.getOther())){
                conversations.set(i,myNewAndBetterConversation);
                return;
            }
        }
        conversations.add(myNewAndBetterConversation);
    }



    private BigInteger checksum(Object obj) throws IOException, NoSuchAlgorithmException {

        if (obj == null) {
            return BigInteger.ZERO;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.close();

        MessageDigest m = MessageDigest.getInstance("SHA1");
        m.update(baos.toByteArray());

        return new BigInteger(1, m.digest());
    }



    /**
     * returs tracker with given index
     *
     * @param index
     * @return
     */
    public Tracker getTrackerByIndex(int index) {
        return trackers.get(index);
    }

    /**
     * converts user and all of it's dependensies to JSON
     *
     * @return JSON containing user and it's dependensies
     */
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        JSONArray trackerArray = new JSONArray();
        JSONArray conversationArray=new JSONArray();
        try {
            jsonObject.put("_id", userName);
            jsonObject.put("lastTrackerID", lastTrackerID);
            jsonObject.put("nextFreeDailyProgressID", nextFreeDailyProgressID);
            jsonObject.put("userNumber", userNumber);

            for (int i = 0; i < trackers.size(); i++) {
                trackerArray.put(trackers.get(i).toJSON());
                System.out.println(i+"TrackerToJSON");
            }
            jsonObject.put("trackers", trackerArray);

            for(int i=0;conversations!=null && i<conversations.size();i++){
                conversationArray.put(conversations.get(i).toJSon());
            }
            jsonObject.put("conversations",conversationArray);

            try {
                jsonObject.put("checkSum",checksum(this));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
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

    /**
     * Sorts Tracker's according to remaining time and splits them to expired and ongoing groups
     *
     * @param ascending
     * @return
     */
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
        if (trackers == null) {
            trackers = new ArrayList<>(0);
        }
        trackers.add(t);
        t.addParentUser(this);
        t.id = lastTrackerID;
        lastTrackerID++;
        updateIndexes();
        save(t);

    }

    public void updateIndexes() {
        for (int i = 0; i < trackers.size(); i++) {
            trackers.get(i).index = i;
        }
    }

    /**
     * @return Returns all the users trackers
     */
    public Tracker[] getTrackers()
    {
        if(trackers==null)
        {
            trackers = new ArrayList<>();
        }
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
                //trackersToDelete.add(t);
                save();
                WakeThread();
                break;
            }
        }

        updateIndexes();
    }

    /**
     * Saves users current information
     */
    public void save() {
        saveUser = true;
        WakeThread();
        Thread t=new NetWorkThread();
        t.start();
    }

    public interface UserLoadedListener {
        void UserLoaded(User user);
    }

    private static class DataBaseThread extends Thread {
        DBHelper db;
        SQLiteDatabase d;
        boolean sleepThread;
        boolean exit = false;
        Context context;

        DataBaseThread(Context c) {
            super();
            context = c;
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
                    loadUserFromNet(new UserLoadedListener() {
                        @Override
                        public void UserLoaded(User user) {
                            currentUser=user;

                        }
                    },currentUser.userName);
                    while (!userLoaded){

                    }
                    try {
                        System.out.println(currentUser.checksum(currentUser));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }

                    //t.start();
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
                    db.deleteTracker(d, iterator.next());
                    iterator.remove();
                }
                for(int i=0;i<listeners.size();i++){
                    listeners.get(i).UserLoaded(currentUser);
                    //listeners.remove(i);
                }
                listeners.clear();
                Iterator<UserLoadedListener> iterator1 = listeners.iterator();
                while (iterator1.hasNext()) {

                    //// TODO: 11.8.2016 keksi parempi ratkasu
                    try {
                        //final UserLoadedListener userLoadedListener = iterator1.next();
                        //userLoadedListener.UserLoaded(currentUser);
                        //iterator1.remove();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
                if (exit) {
                    db.close();
                    d.close();
                    return;
                }
            }
        }
    }

    private static class NetWorkThread extends Thread {


        public void run() {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 500;

            try {
                URL url = new URL("https://bizfit-nyyppa.c9users.io");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("Job","save");
                    jsonObject.put("user",currentUser.toJSON());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                writer.write(jsonObject.toString());
                //System.out.println(currentUser.toJSON().toString());
                // Starts the query
                writer.flush();
                conn.connect();
                int response = conn.getResponseCode();
                if (response==200) {
                    is = conn.getInputStream();

                    // Convert the InputStream into a string
                    BufferedReader r = new BufferedReader(new InputStreamReader(is));
                    StringBuilder total = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        total.append(line).append('\n');
                    }
                    System.out.println(total);
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

    public MyNewAndBetterConversation addConversation(MyNewAndBetterConversation conversation){
        if(conversations==null){
            conversations=new ArrayList<>();
        }
        if(GetMessagesThread==null||!GetMessagesThread.isAlive()){
            GetMessagesThread=new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        for(int i=0;i<conversations.size();i++){
                            conversations.get(i).getNewMessagesAndSendOldOnes();
                        }

                        System.out.println(conversations.size()+"conversations koko");
                        synchronized (this){
                            try {
                                wait(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                }
            });
            GetMessagesThread.start();
        }
        for(int i=0;i<conversations.size();i++){
            if(conversations.get(i).getOwner().equals(conversation.getOwner())&&conversations.get(i).getOther().equals(conversation.getOther())){
                return conversations.get(i);
            }
        }
        conversations.add(conversation);
        return  conversation;
    }

    public void save(Object obj)
    {
        saveUser = true;
        WakeThread();
        JSONObject jsonObject = new JSONObject();
       if (obj instanceof Tracker)
       {
           Tracker tracker = (Tracker) obj;

           try
           {
               jsonObject.put("Job", "save_tracker");
               jsonObject.put("User", userName);
               jsonObject.put("Tracker", tracker.toJSON());
           }
           catch (JSONException e)
           {
               e.printStackTrace();
           }


       }
        else if(obj instanceof MyNewAndBetterConversation)
       {
           MyNewAndBetterConversation conversation = (MyNewAndBetterConversation) obj;
           try
           {
               jsonObject.put("Job", "save_conversation");
               jsonObject.put("Conversation", conversation.toJSon());
           }
           catch (JSONException e)
           {
               e.printStackTrace();
           }
       }
        //TODO: Make error handling
        NewAndBetterNetwork.addNetMessage(new NetMessage(null, null, jsonObject));





    }
}
