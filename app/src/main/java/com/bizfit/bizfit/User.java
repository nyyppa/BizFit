package com.bizfit.bizfit;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.bizfit.bizfit.chat.Conversation;
import com.bizfit.bizfit.chat.Message;
import com.bizfit.bizfit.network.NetMessage;
import com.bizfit.bizfit.network.Network;
import com.bizfit.bizfit.network.NetworkReturn;
import com.bizfit.bizfit.tracker.SharedTracker;
import com.bizfit.bizfit.tracker.Tracker;
import com.bizfit.bizfit.utils.Constants;
import com.bizfit.bizfit.utils.DBHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;


/**
 * Class that contains user information, calls local database when needed and holds user's trackers
 */

public class User  {
    /**
     *
     */
    private static final long serialVersionUID = 8425799364006222365L;

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
    private List<Long> deletedTrackers=new ArrayList<>(0);
    private static boolean dropLastUser=false;
    private List<SharedTracker> sharedTrackerList;
    private List<Tracker> trackersSharedWithMe=new ArrayList<>();


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
            if(jsonObject.has(Constants.getUser_Name()))
            {
                userName = jsonObject.getString(Constants.getUser_Name());
            }
            if(jsonObject.has(Constants.conversations))
            {
                JSONArray conversationArray=jsonObject.getJSONArray(Constants.conversations);
                for(int i=0;i<conversationArray.length();i++)
                {
                    addConversation(new Conversation(conversationArray.getJSONObject(i),this));
                }
            }
            if(this.deletedTrackers==null){
                deletedTrackers=new ArrayList<>();
            }
            if(jsonObject.has("DeletedTrackers")){
                JSONArray deletedTrackers=jsonObject.getJSONArray("DeletedTrackers");
                for(int i=0;i<deletedTrackers.length();i++){
                    this.deletedTrackers.add(deletedTrackers.getLong(i));
                }
            }
            if(jsonObject.has("SharedTrackers")){
                JSONArray sharedTrackers=jsonObject.getJSONArray("SharedTrackers");
                for(int i=0;i<sharedTrackers.length();i++){
                    getSharedTrackerList().add(new SharedTracker(sharedTrackers.getJSONObject(i)));
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public List<Conversation> getConversations(){
        if(conversations==null){
            conversations=new ArrayList<>();
        }
        return conversations;
    }
    public List<SharedTracker> getSharedTrackerList(){
        if (sharedTrackerList==null){
            sharedTrackerList=new ArrayList<>();
        }
        return sharedTrackerList;
    }
    public void getSharedTrackersFromNet(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(Constants.job,"getSharedTrackers");
            JSONArray jsonArray=new JSONArray();
            for(int i=0;i<getSharedTrackerList().size();i++){
                jsonArray.put(getSharedTrackerList().get(i).toJSON());
            }
            jsonObject.put("list",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Network.addNetMessage(new NetMessage(null, new NetworkReturn() {
            @Override
            public void returnMessage(String message) {
                if(message.equals(Constants.networkconn_failed)){

                }else{

                }
            }
        },jsonObject));
    }

    private void updateInformation(User user){
        if(user.userName!=null && this.userName!=null && !user.userName.equals(this.userName)&&!this.userName.equals("")&&!this.userName.isEmpty())
        {
            return;
        }
        else if(user.userName==null)
        {
            return;
        }
        else {
            this.userName=user.userName;
        }
        DebugPrinter.Debug("jaetut trackerit: "+user.getSharedTrackerList().size());
        getTrackersSharedWithMe();
        boolean informationUpdated=updateTrackers(user.getTrackerlist());
        if(informationUpdated)
        {
            updateConversations(user.getConversations());
            DebugPrinter.Debug("tallennuksessa conversationit;" + this.getConversations());
        }else
        {
            informationUpdated= updateConversations(user.getConversations());
        }
        if(informationUpdated){
            SharedTracker.combineLists(getSharedTrackerList(),user.getSharedTrackerList());
            DebugPrinter.Debug("tallennuksessa jaetut trackerit;" + this.getSharedTrackerList().size());
        }else{
            informationUpdated=SharedTracker.combineLists(getSharedTrackerList(),user.getSharedTrackerList());
            DebugPrinter.Debug("tallennuksessa jaetut trackerit2;" + this.getSharedTrackerList().size());
        }
        //TODO: Clean list when can't find sharedtrackers from server
        DebugPrinter.Debug("Koko lista: "+getSharedTrackerList().size());
        DebugPrinter.Debug("Jaetut minulle lista" + getTrackersSharedWithMeList().size());

        if(informationUpdated){
            List<UserLoadedListener>listenersForInformationUpdated=getListenersForInformationUpdated();
            for(int i=0;i<listenersForInformationUpdated.size();i++){
                if(listenersForInformationUpdated.get(i)!=null){
                    listenersForInformationUpdated.get(i).informationUpdated();
                    DebugPrinter.Debug("tallennuksessa listenerit;" + listenersForInformationUpdated.get(i).toString());
                }
            }
        }
    }
    //TODO Support for messages
    private boolean updateConversations(List<Conversation> conversations){
        List<Conversation>newConversations=new ArrayList<>();
        for(int i=0;i<conversations.size();i++){
            Conversation conversation=conversations.get(i);
            if(!conversation.isConversationAlreadyInList(this.getConversations()))
            {
                newConversations.add(conversation);
            }

        }
        return this.getConversations().addAll(newConversations);
    }

    private boolean updateTrackers(List<Tracker> list){

        List<Tracker> newTrackers=new ArrayList<>();
        for(int i=0;i<list.size();i++){
           Tracker t=list.get(i);
           if(!t.isTHisInList(getTrackerlist())&&!t.hasThisBeenRemoved(deletedTrackers)){
               newTrackers.add(t);
           }else{
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
        DBHelper db;
        SQLiteDatabase d;
        db = new DBHelper(c, "database1", null, Constants.db_version);
        d = db.getWritableDatabase();
        User user=db.readUser(d,"default");

        User.getLastUser(null,c,null);
        DebugPrinter.Debug("userAlarm"+user.userName);

        //JariJ 1.2.17
        //Checking users conversations and calling getNewMessagesAndSendOldOnes
        //Because notifications should show even when app is inactive
        for(int i=0; i < user.getConversations().size();i++)
        {
            user.getConversations().get(i).getNewMessagesAndSendOldOnes();
        }

        List<Tracker> trackers = user.getTrackerlist();
        for (int i = 0; i < trackers.size(); i++)
        {
            trackers.get(i).update();
        }
        Network.onExit();
        Network network = Network.getNetwork();
        /*
        try
        {
            if(network!=null)
            {
                Network.getNetwork().join();
            }

        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }*/


    }

    public void addSharedTracker(SharedTracker sharedTracker){
        List<SharedTracker> list=getSharedTrackerList();
        if(sharedTracker.getUserName()!=null&&sharedTracker.getUserName().equals(userName)){
            return;
        }

        if(!sharedTracker.alreadyInList(list)){
            list.add(sharedTracker);


            //TODO updatedInformationCall
        }
        sharedTracker.setStatus(Message.Status.ACCEPTED);
        getTrackersSharedWithMe();
    }
    public void getTrackersSharedWithMe(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(Constants.job,"getSharedTrackers");
            JSONArray jsonArray=new JSONArray();
            for(int i=0;i<getSharedTrackerList().size()&&getSharedTrackerList().get(i).getStatus()== Message.Status.ACCEPTED;i++){
                jsonArray.put(getSharedTrackerList().get(i).toJSON());
            }
            jsonObject.put("list",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NetMessage netMessage=new NetMessage(null, new NetworkReturn() {
            @Override
            public void returnMessage(String message) {
                if(!message.equals(Constants.networkconn_failed)){
                    try {
                        JSONArray jsonArray=new JSONArray(message);
                        boolean informationUpdated=false;
                        for(int i=0;i<jsonArray.length();i++){
                            Tracker t=new Tracker(new JSONObject(jsonArray.getString(i)));
                            if(!t.updateInList(trackersSharedWithMe)){
                                trackersSharedWithMe.add(t);
                                informationUpdated=true;
                            }
                        }
                        List<UserLoadedListener>listenersForInformationUpdated=getListenersForInformationUpdated();
                        for(int i=0;i<listenersForInformationUpdated.size()&&informationUpdated;i++){
                            if(listenersForInformationUpdated.get(i)!=null){
                                listenersForInformationUpdated.get(i).informationUpdated();
                            }
                            DebugPrinter.Debug("time"+System.currentTimeMillis());
                            DebugPrinter.Debug("ListenersSize"+listenersForInformationUpdated.size());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        },jsonObject);
        Network.addNetMessage(netMessage);
    }

    public List<Tracker> getTrackersSharedWithMeList()
    {
        if(trackersSharedWithMe==null)
        {
            trackersSharedWithMe=new ArrayList<>();
        }
        return trackersSharedWithMe;
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
        if(c!=null){
            context = c;
        }
        if (listener!=null){
            getListenersForInformationUpdated().add(listener);
        }
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
    private static void WakeThread()
    {
        if(context!=null)
        {
            if (thread == null) {
                thread = new DataBaseThread(context);
                thread.start();
            }
            if (!thread.isAlive()) {
                thread = new DataBaseThread(context);
                thread.start();
            }
            synchronized (thread) {
                thread.setName("DatabaseThread");
                thread.notify();
                thread.sleepThread = false;
            }
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
            jsonObject1.put(Constants.getUser_Name(), userName);
            jsonObject1.put(Constants.job, Constants.load);
            if(currentUser!=null){
                jsonObject1.put(Constants.check_sum, 0);
                /*try {

                    //jsonObject1.put(Constants.check_sum, currentUser.checksum(currentUser));
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
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
                if(message.equals(Constants.networkconn_failed)||message.length()==0){

                }else{
                    JSONObject jsonObject=null;
                    try {
                        jsonObject=new JSONObject(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(jsonObject!=null && currentUser !=null ){
                        currentUser.updateInformation(new User(jsonObject));
                    }
                }

            }
        },jsonObject1);
        Network.addNetMessage(netMessage);
    }

    public static void signOut()
    {
        currentUser=null;
        dropLastUser=true;
        Network.onExit();
        WakeThread();


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
    public JSONObject toJSON(boolean toNet) {
        JSONObject jsonObject = new JSONObject();
        JSONArray trackerArray = new JSONArray();
        JSONArray conversationArray=new JSONArray();
        JSONArray deletedTrackers=new JSONArray();
        JSONArray sharedTrackers = new JSONArray();
        try {
            jsonObject.put(Constants.getUser_Name(), userName);
            if(trackers!=null)
            {
                for (int i = 0; i < trackers.size(); i++)
                {
                    trackerArray.put(trackers.get(i).toJSON());
                }
            }
            jsonObject.put(Constants.trackers, trackerArray);

            for(int i=0;conversations!=null && i<conversations.size();i++){
                conversationArray.put(conversations.get(i).toJSon());
            }
            jsonObject.put(Constants.conversations,conversationArray);

            for(int i=0;this.deletedTrackers!=null&&i<this.deletedTrackers.size();i++){
                deletedTrackers.put(this.deletedTrackers.get(i).longValue());
            }
            jsonObject.put(Constants.deleted_trackers,deletedTrackers);

            if(getSharedTrackerList()!=null&&!toNet)
            {
                for(int i = 0; i < getSharedTrackerList().size(); i++)
                {
                    sharedTrackers.put(getSharedTrackerList().get(i).toJSON());
                }
                jsonObject.put(Constants.shared_trackers,sharedTrackers);
            }
            /*
            try {
                jsonObject.put(Constants.check_sum,checksum(this));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    public Tracker getTrackerWithGivenUUID(UUID uuid){
        Tracker[]trackers=getTrackers(TrackerSharedEnum.ALL);
        for(int i=0;i<trackers.length;i++){
            if(trackers[i].equals(uuid)){
                return trackers [i];
            }
        }
        return null;
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
    public Tracker[] getTrackers(TrackerSharedEnum trackerSharedEnum)
    {
        if(trackers==null)
        {
            trackers = new ArrayList<>();
        }
        Tracker[] t = new Tracker[getTrackerlist().size()];
        getTrackerlist().toArray(t);
        Tracker[] p=new Tracker[trackersSharedWithMe.size()];
        trackersSharedWithMe.toArray(p);
        if (trackerSharedEnum==TrackerSharedEnum.SHARED){
            return p;
        }else if(trackerSharedEnum==TrackerSharedEnum.OWN){
            return t;
        }
        return concat(t,p);
    }
    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

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
            if (current.equals(t)) {
                iterator.remove();
                deletedTrackers.add(t.creationTime);
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
            jsonObject.put(Constants.user, currentUser.toJSON(true));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NetMessage netMessage=new NetMessage(null, new NetworkReturn() {
            @Override
            public void returnMessage(String message) {
                if(message.equals(Constants.networkconn_failed)){
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
    private class DBT extends OurRunnable{
        DBHelper dbHelper;
        SQLiteDatabase database;
        Context context;
        private DBT(Context context){
            super(true,10000);
            this.context=context;
        }
        @Override
        public void run() {
            if(dbHelper==null&&context!=null){
                dbHelper=new DBHelper(context, "database1", null, Constants.db_version);
            }
            if(database==null&&dbHelper!=null){
                database=dbHelper.getWritableDatabase();
            }
            if(currentUser!=null&&currentUser.saveUser){
                dbHelper.saveUser(database,currentUser);
                currentUser.saveUser=false;
            }
            else if (currentUser!=null)
            {
                currentUser.updateInformation( dbHelper.readUser(database,userNameForLogin));
                loadUserFromNet(currentUser.userName);
            }
            if(dropLastUser)
            {
                dbHelper.deleteLastUser(database);
                dropLastUser=false;
            }
            if (currentUser == null ) {
                dbHelper.close();
                database.close();
                repeat=false;
            }
        }
    }
    //TODO make this better
    private static class DataBaseThread extends Thread {
        DBHelper db;
        SQLiteDatabase d;
        boolean sleepThread;
        boolean exit = false;
        Context context;
        boolean firstTime=true;

        DataBaseThread(Context c) {
            super();
            context = c;
        }

        @Override
        public void run()
        {
            try
            {
                while (true)
                {
                    super.run();
                    if (db == null && context!=null)
                    {
                        db = new DBHelper(context, "database1", null, Constants.db_version);
                    }
                    if (d == null && db!=null) {
                        d = db.getWritableDatabase();
                    }

                    if (currentUser!=null && currentUser.saveUser) {
                        db.saveUser(d, currentUser);
                        currentUser.saveUser = false;
                    }
                    else if (currentUser!=null)
                    {
                        if(firstTime){
                            currentUser.updateInformation( db.readUser(d,userNameForLogin));
                            firstTime=false;
                            DebugPrinter.Debug("höhööööö");
                        }
                        loadUserFromNet(currentUser.userName);
                    }
                    if(dropLastUser)
                    {
                        db.deleteLastUser(d);
                        dropLastUser=false;
                    }
                    sleepThread = true;
                    while (sleepThread && currentUser !=null && !currentUser.saveUser) {
                        synchronized (thread) {
                            try {
                                thread.wait(10000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (exit || currentUser == null ) {
                        db.close();
                        d.close();
                        return;
                    }
                }
            }
            finally
            {
                DebugPrinter.Debug("Threadi");
            }


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

        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            DebugPrinter.Debug("threadi tuhottu");
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
        if(GetMessagesThread==null||!GetMessagesThread.isAlive()){
            GetMessagesThread=new Thread(new Runnable() {
                long waitTime=10000;
                long lastUpdateTime=0;
                @Override
                public void run() {
                    while (true)
                    {
                        boolean alreadyUpdatedLastUpdateTime=false;
                        if(conversation.isOnline(getContext()) && currentUser !=null ){
                            List<Conversation> conversations=currentUser.getConversations();
                            for(int i=0;i<conversations.size();i++)
                            {
                                Conversation conversation1=conversations.get(i);
                                if(conversation1.isActive()){
                                    conversations.get(i).getNewMessagesAndSendOldOnes();
                                }else if(lastUpdateTime+waitTime<System.currentTimeMillis()||alreadyUpdatedLastUpdateTime){
                                    if (!alreadyUpdatedLastUpdateTime){
                                        alreadyUpdatedLastUpdateTime=true;
                                        lastUpdateTime=System.currentTimeMillis();
                                    }
                                    conversation1.getNewMessagesAndSendOldOnes();
                                }
                            }
                        }
                        synchronized (GetMessagesThread){
                            //DebugPrinter.Debug("pää");
                            try {
                                GetMessagesThread.wait(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        if(currentUser == null)
                        {
                            return;
                        }
                    }
                }
            });
            GetMessagesThread.setName("MessageThread");
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
        save();
        /*
        saveUser = true;
        WakeThread();

       if (obj instanceof Tracker)
       {
           Tracker tracker = (Tracker) obj;
           tracker.saveToServer(userName,tracker.toJSON());
               //jsonObject.put(Constants.job, Constants.save_tracker);
               //jsonObject.put(Constants.user_name, userName);
               //jsonObject.put(Constants.tracker, tracker.toJSON());
       }
        else if(obj instanceof Conversation)
       {
           JSONObject jsonObject = new JSONObject();
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
           Network.addNetMessage(new NetMessage(null, null, jsonObject));
       }
        */

        //TODO: Make error handling


    }

    public String getUserName(UserLoadedListener listener, String userName)
    {
        return userName;
    }

    public enum TrackerSharedEnum{
        ALL,OWN,SHARED;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        currentUser.save();
    }
}
