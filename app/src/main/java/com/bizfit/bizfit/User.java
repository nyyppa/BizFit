package com.bizfit.bizfit;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bizfit.bizfit.chat.Conversation;
import com.bizfit.bizfit.network.NetMessage;
import com.bizfit.bizfit.network.Network;
import com.bizfit.bizfit.network.NetworkReturn;
import com.bizfit.bizfit.tracker.Tracker;
import com.bizfit.bizfit.utils.Constants;
import com.bizfit.bizfit.utils.DBHelper;

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

import javax.net.ssl.HttpsURLConnection;

/**
 * Class that contains user information, calls local database when needed and holds user's trackers
 */

public class User implements java.io.Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 8425799364006222365L;
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
    public int userNumber;
    static boolean userLoaded=false;
    List<Conversation> conversations;
    private transient static Thread GetMessagesThread;
    private static String userNameForLogin;
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
            if(jsonObject.has(Constants.trackers))
            {
                trackerArray = jsonObject.getJSONArray(Constants.trackers);
                for (int i = 0; i < trackerArray.length(); i++) {
                    Tracker t = new Tracker(trackerArray.getJSONObject(i));
                    trackers.add(t);
                    t.addParentUser(this);
                }
            }
            if(jsonObject.has(Constants.user_name))
            {
                userName = jsonObject.getString(Constants.user_name);
            }
            if(jsonObject.has(Constants.last_tracker_id))
            {
                lastTrackerID = jsonObject.getInt(Constants.last_tracker_id);
            }
            if(jsonObject.has(Constants.next_free_daily_progress_id))
            {
                nextFreeDailyProgressID = jsonObject.getInt(Constants.next_free_daily_progress_id);
            }
            if(jsonObject.has(Constants.user_number))
            {
                userNumber = jsonObject.getInt(Constants.user_number);
            }

            if(jsonObject.has(Constants.conversations))
            {
                JSONArray conversationArray=jsonObject.getJSONArray(Constants.conversations);
                for(int i=0;i<conversationArray.length();i++)
                {
                    addConversation(new Conversation(conversationArray.getJSONObject(i),this));
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
    public User(String userName) {
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
    public static void update(Context c)
    {
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
        }, c, null);

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
    public static void getLastUser(UserLoadedListener listener, Context c, String userName) {
        if(userName!=null){
            userNameForLogin=userName;
        }else if(userNameForLogin==null||userNameForLogin.isEmpty()){
            userNameForLogin="default";
        }
        System.out.println("userNameForLogin "+userNameForLogin);
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
        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put(Constants.id, userName);
            jsonObject1.put(Constants.job, Constants.load);
            if(currentUser!=null){
                try {
                    jsonObject1.put(Constants.check_sum, currentUser.checksum(currentUser));
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                jsonObject1.put(Constants.check_sum,"0");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //todo merge user, add fail handling
        NetMessage netMessage=new NetMessage(null, new NetworkReturn() {
            @Override
            public void returnMessage(String message) {
                if(message.equals("failed")){

                }else{
                    JSONObject jsonObject=null;
                    try {
                        jsonObject=new JSONObject(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(jsonObject!=null){
                        currentUser=new User(jsonObject);
                    }
                    if(userLoadedListener!=null){
                        userLoadedListener.UserLoaded(currentUser);

                    }
                }

            }
        },jsonObject1);
        Network.addNetMessage(netMessage);
    }

    /**
     * this is because cloned version has correct chatfragment
     * @param conversation
     */
    public void addClonedMyNewAndBetterConversation(Conversation conversation){
        for(int i=0;i<conversations.size();i++){
            Conversation oldConversation=conversations.get(i);
            if(oldConversation.getOther().equals(conversation.getOther())){
                conversations.set(i, conversation);
                return;
            }
        }
        conversations.add(conversation);


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
            jsonObject.put(Constants.user_name, userName);
            jsonObject.put(Constants.last_tracker_id, lastTrackerID);
            jsonObject.put(Constants.next_free_daily_progress_id, nextFreeDailyProgressID);
            jsonObject.put(Constants.user_number, userNumber);

            for (int i = 0; i < trackers.size(); i++) {
                trackerArray.put(trackers.get(i).toJSON());
            }
            jsonObject.put(Constants.trackers, trackerArray);

            for(int i=0;conversations!=null && i<conversations.size();i++){
                conversationArray.put(conversations.get(i).toJSon());
            }
            jsonObject.put(Constants.conversations,conversationArray);

            try {
                jsonObject.put(Constants.check_sum,checksum(this));
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
    public void save()
    {
        saveUser = true;
        WakeThread();
        saveToNet();


    }

    public void saveToNet(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(Constants.job, Constants.save);
            jsonObject.put(Constants.user, currentUser.toJSON());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NetMessage netMessage=new NetMessage(null, new NetworkReturn() {
            @Override
            public void returnMessage(String message) {
                if(message.equals("failed")){
                    saveToNet();
                }
            }
        },jsonObject);
        Network.addNetMessage(netMessage);
    }


    public interface UserLoadedListener
    {
        void UserLoaded(User user);
    }

    //TODO KILL THIS
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

                    db = new DBHelper(context, "database1", null, Constants.db_version);

                }
                if (d == null) {
                    d = db.getWritableDatabase();
                }
                if (currentUser == null) {
                    currentUser = db.readUser(d,userNameForLogin);
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
                URL url = new URL(Constants.connection_address);
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
                    jsonObject.put(Constants.job, Constants.save);
                    jsonObject.put(Constants.user, currentUser.toJSON());
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

    public Conversation addConversation(final Conversation conversation){
        if(conversations==null){
            conversations=new ArrayList<>();
        }
        if(GetMessagesThread==null||!GetMessagesThread.isAlive()){
            GetMessagesThread=new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true && conversation.isOnline(getContext()))
                    {
                        for(int i=0;i<conversations.size();i++)
                        {
                            conversations.get(i).getNewMessagesAndSendOldOnes();
                        }
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
               jsonObject.put(Constants.job, Constants.save_tracker);
               jsonObject.put(Constants.user_name, userName);
               jsonObject.put(Constants.tracker, tracker.toJSON());
           }
           catch (JSONException e)
           {
               e.printStackTrace();
           }


       }
        else if(obj instanceof Conversation)
       {
           Conversation conversation = (Conversation) obj;
           try
           {
               jsonObject.put(Constants.job, Constants.save_conversation);
               jsonObject.put(Constants.conversation, conversation.toJSon());
           }
           catch (JSONException e)
           {
               e.printStackTrace();
           }
       }
        //TODO: Make error handling
        Network.addNetMessage(new NetMessage(null, null, jsonObject));

    }

    public String getUserName(UserLoadedListener listener, String userName)
    {
        return userName;
    }
}
