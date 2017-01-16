package com.bizfit.bizfit;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bizfit.bizfit.chat.Conversation;
import com.bizfit.bizfit.chat.Message;
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
    private transient static DataBaseThread thread;
    private transient static Context context;
    private transient static User currentUser;
    public String userName;
    public boolean saveUser = false;
    List<Tracker> trackers;
    static boolean userLoaded=false;
    List<Conversation> conversations;
    private transient static Thread GetMessagesThread;
    private static String userNameForLogin;
    private transient static List<UserLoadedListener> listenersForInformationUpdated;
    //todo remove userNumber


    private User(){
        userName="";
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
    private List<Conversation> getConversations(){
        if(conversations==null){
            conversations=new ArrayList<>();
        }
        return conversations;
    }
    private boolean updateCOnversations(List<Conversation> conversations){
        List<Conversation>newConversations=new ArrayList<>();
        for(int i=0;i<conversations.size();i++){
            Conversation conversation=conversations.get(i);
            if(!conversation.isConversationAlreadyInList(this.getConversations())){
                newConversations.add(conversation);
            }

        }
        return this.getConversations().addAll(newConversations);
    }
    private void updateInformation(User user){
        if(!user.userName.equals(this.userName)&&!this.userName.equals("")&&!this.userName.isEmpty()){
            return;
        }else{
            this.userName=user.userName;
        }
        boolean informationUpdated=updateTrackers(user.getTrackerlist());
        if(informationUpdated){
            updateCOnversations(user.getConversations());
        }else{
            informationUpdated=updateCOnversations(user.getConversations());
        }

        if(informationUpdated){
            DebugPrinter.Debug("täällä ollaan");
            List<UserLoadedListener>listenersForInformationUpdated=getListenersForInformationUpdated();
            for(int i=0;i<listenersForInformationUpdated.size();i++){
                if(listenersForInformationUpdated.get(i)!=null){
                    listenersForInformationUpdated.get(i).informationUpdated();
                }
            }
        }
    }

    private boolean updateTrackers(List<Tracker> list){

        List<Tracker> newTrackers=new ArrayList<>();
        for(int i=0;i<list.size();i++){
           Tracker t=list.get(i);
           if(!t.isTHisInList(getTrackerlist())){
               newTrackers.add(t);
           }
        }
        return getTrackerlist().addAll(newTrackers);
    }





    /**
     * Runs update for every tracker to keep their internal time moving
     *
     * @param c Context for sending notifications and loading user from internal database
     */
    public static void update(Context c)
    {
        User user=getLastUser(new UserLoadedListener() {

            @Override
            public void informationUpdated() {

            }

        }, c, null);
        List<Tracker> trackers = user.getTrackerlist();
        for (int i = 0; i < trackers.size(); i++)
        {
            trackers.get(i).update();
        }

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
    public static User getLastUser(UserLoadedListener listener, Context c, String userName) {

        if(userName!=null){
            userNameForLogin=userName;
        }else if(userNameForLogin==null||userNameForLogin.isEmpty()){
            userNameForLogin="default";
        }
        if(currentUser!=null&&userName!=null&&!currentUser.userName.equals(userName)){
            currentUser=null;
        }
        if(currentUser==null){
            currentUser=new User();
        }
        System.out.println("userNameForLogin "+userName);
        context = c;

        getListenersForInformationUpdated().add(listener);
        WakeThread();
        return currentUser;
    }
    private static List<UserLoadedListener> getListenersForInformationUpdated(){
        if(listenersForInformationUpdated==null){
            listenersForInformationUpdated=new ArrayList<>();
        }
        return listenersForInformationUpdated;
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
     * @param userName           Username to find, if null will try to find users google account and use it
     */
    public static void loadUserFromNet( String userName) {
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

                if(message.equals("failed")||message.length()==0){

                }else{
                    JSONObject jsonObject=null;
                    try {
                        jsonObject=new JSONObject(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(jsonObject!=null){
                        currentUser.updateInformation(new User(jsonObject));
                    }
                }

            }
        },jsonObject1);
        Network.addNetMessage(netMessage);
    }

    public static void singOut(){
        currentUser=null;
    }

    /**
     * this is because cloned version has correct chatfragment
     * @param conversation
     */
    public void addClonedMyNewAndBetterConversation(Conversation conversation){
        List<Conversation> conversations=getConversations();
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
        void informationUpdated();
    }

    //TODO make this better
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
                currentUser.updateInformation( db.readUser(d,userNameForLogin));
                loadUserFromNet(currentUser.userName);
                if (currentUser == null||currentUser.userName.isEmpty()||currentUser.userName.equals("")||true) {

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
                listeners.clear();
                /*Iterator<UserLoadedListener> iterator1 = listeners.iterator();
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
                */
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
                long waitTime=10000;
                long lastUpdateTime=0;
                @Override
                public void run() {

                    while (true)
                    {
                        boolean alreadyUpdatedLastUpdateTime=false;
                        if(conversation.isOnline(getContext())){

                            for(int i=0;i<conversations.size();i++)
                            {
                                Conversation conversation1=conversations.get(i);
                                if(conversation1.isActive()){
                                    conversations.get(i).getNewMessagesAndSendOldOnes();
                                }else if(lastUpdateTime+waitTime<System.currentTimeMillis()){
                                    if (!alreadyUpdatedLastUpdateTime){
                                        alreadyUpdatedLastUpdateTime=true;
                                        lastUpdateTime=System.currentTimeMillis();
                                    }
                                    conversation1.getNewMessagesAndSendOldOnes();

                                }

                            }
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
        List<Conversation> conversations=getConversations();
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
