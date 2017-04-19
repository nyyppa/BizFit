package com.bizfit.bizfit;


import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.Nullable;

import com.bizfit.bizfit.activities.MainPage;
import com.bizfit.bizfit.chat.Conversation;
import com.bizfit.bizfit.chat.Message;
import com.bizfit.bizfit.network.NetMessage;
import com.bizfit.bizfit.network.Network;
import com.bizfit.bizfit.network.NetworkReturn;
import com.bizfit.bizfit.tracker.SharedTracker;
import com.bizfit.bizfit.tracker.Tracker;
import com.bizfit.bizfit.utils.Constants;
import com.bizfit.bizfit.utils.DBHelper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.bizfit.bizfit.utils.Constants.conversation;


/**
 * Class that contains user information, calls local database when needed and holds user's trackers
 */

public class User  {
    /**
     *
     */
    private static final long serialVersionUID = 8425799364006222365L;

    private transient static DBT DBthread;
    private transient static Context context;
    private transient static User currentUser;
    private transient static GMT conversationRunnable;
    private transient static GetPendingChatRequestsThread getPendingChatRequestsThread;
    public String userName;
    public boolean saveUser = false;
    public List<Conversation> conversations;
    private static String userNameForLogin;
    private transient static List<UserLoadedListener> listenersForInformationUpdated;
    private static boolean dropLastUser=false;
    public List<ChatRequest> requestsForMe=new ArrayList<>();
    public UUID uuid;
    public List<ChatRequest> requestsFromMe=new ArrayList<>();

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
            if(jsonObject.has(Constants.UUID)){
                uuid=UUID.fromString(jsonObject.getString(Constants.UUID));
            }else{
                uuid=UUID.randomUUID();
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
        boolean informationUpdated= updateConversations(user.getConversations());

        //TODO: Clean list when can't find sharedtrackers from server


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

    /**
     * Runs update for every tracker to keep their internal time moving
     *
     * @param c Context for sending notifications and loading user from internal database
     */
    public static void update(final Context c)
    {
        OurRunnable runnable = new OurRunnable()
        {
            @Override
            public void run()
            {
                DBHelper db;
                db = new DBHelper(c, "database1", null, Constants.db_version);
                User user = null;
                if (currentUser != null) {
                    user = currentUser;
                } else {
                    user = db.readUser("default");
                }
                //JariJ 1.2.17
                //Checking users conversations and calling getNewMessagesAndSendOldOnes
                //Because notifications should show even when app is inactive
                for (int i = 0; i < user.getConversations().size(); i++) {
                    user.getConversations().get(i).getNewMessagesAndSendOldOnes();
                }
                db.close();

                if(currentUser==null)
                {
                    Network.onExit();
                }

            }
        };

        BackgroundThread.addOurRunnable(runnable);
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


    static GetChatRequestResponses getPendingChatRequestResponses;
    /**
     * wakes or creates new thread for local database
     */
    private static void WakeThread()
    {
        if(context!=null)
        {
            if(getPendingChatRequestsThread==null){
                getPendingChatRequestsThread=new GetPendingChatRequestsThread(context);
                BackgroundThread.addOurRunnable(getPendingChatRequestsThread);
            }
            if(getPendingChatRequestResponses==null){
                getPendingChatRequestResponses=new GetChatRequestResponses();
                BackgroundThread.addOurRunnable(getPendingChatRequestResponses);
            }
            if (DBthread == null) {

                DBthread = new DBT(context);
                BackgroundThread.addOurRunnable(DBthread);
            }
            else
            {
                DBthread.wake();
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

    public static GoogleApiClient mGoogleApiClient;
    public static void signOut(Intent intent, Context context)
    {

        if(mGoogleApiClient!=null){
            LogOutClass logOutClass=new LogOutClass(intent,context);
            logOutClass.logout();
        }

        currentUser=null;
        dropLastUser=true;
        Network.onExit();
        WakeThread();
    }
    public static void signOut()
    {

        if(mGoogleApiClient!=null){
            LogOutClass logOutClass=new LogOutClass();
            logOutClass.logout();
        }

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

            jsonObject.put(Constants.trackers, trackerArray);

            for(int i=0;conversations!=null && i<conversations.size();i++){
                conversationArray.put(conversations.get(i).toJSon());
            }
            jsonObject.put(Constants.conversations,conversationArray);


            jsonObject.put(Constants.deleted_trackers,deletedTrackers);

            jsonObject.put(Constants.UUID, uuid);






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















    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

    }




    /**
     * Saves users current information
     */
    public void save()
    {
        saveLocal();
        saveToNet();


    }

    private void saveLocal(){
        saveUser = true;
        WakeThread();
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
    private static class DBT extends OurRunnable
    {
        DBHelper dbHelper;
        Context context;
        private DBT(Context context)
        {
            super(true,10000);
            DBT.this.context=context;
        }
        @Override
        public void run() {
            if(dbHelper==null&&context!=null){
                dbHelper=new DBHelper(context, "database1", null, Constants.db_version);
            }
            if(currentUser!=null&&currentUser.saveUser){
                dbHelper.saveUser(currentUser);
                currentUser.saveUser=false;
            }
            else if (currentUser!=null)
            {
                currentUser.updateInformation( dbHelper.readUser(userNameForLogin));
                loadUserFromNet(currentUser.userName);
            }
            if(dropLastUser)
            {
                dbHelper.deleteLastUser();
                dropLastUser=false;
            }
            if (currentUser == null ) {
                dbHelper.close();
                repeat=false;
                DBthread=null;
            }
        }
    }
    //TODO make this better
   /* private static class DataBaseThread extends Thread {
        DBHelper db;
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

                    if (currentUser!=null && currentUser.saveUser) {
                        db.saveUser(currentUser);
                        currentUser.saveUser = false;
                    }
                    else if (currentUser!=null)
                    {
                        if(firstTime){
                            currentUser.updateInformation( db.readUser(userNameForLogin));
                            firstTime=false;
                            DebugPrinter.Debug("höhööööö");
                        }
                        loadUserFromNet(currentUser.userName);
                    }
                    if(dropLastUser)
                    {
                        db.deleteLastUser();
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
   /*

        }
        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            DebugPrinter.Debug("threadi tuhottu");
        }
    }
    */
    //GetMessagesThread by jariJ 6.3.17
    private class GMT extends OurRunnable
    {
        long waitTime=10000;
        long lastUpdateTime=0;

        public GMT ()
        {
            super(true, 1000);
        }
        @Override
        public void run()
        {
            boolean alreadyUpdatedLastUpdateTime=false;
            if(Conversation.isOnline(getContext()) && currentUser !=null ){
                User user=currentUser;
                List<Conversation> conversations=user.getConversations();
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
            if(currentUser == null)
            {
                repeat=false;
                conversationRunnable=null;
            }
        }
    }

    private static class GetPendingChatRequestsThread extends OurRunnable{
        Context context;
        private GetPendingChatRequestsThread(Context context){
            super(true, 10000);
            this.context=context;
        }
        @Override
        public void run() {
            if(currentUser!=null){
                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put(Constants.job,"GetChatRequests");
                    jsonObject.put(Constants.user_name,currentUser.userName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Network.addNetMessage(new NetMessage(null, new NetworkReturn() {
                    @Override
                    public void returnMessage(String message) {
                        if(!message.startsWith("no requests found") && !message.equals("failed")){
                            try {
                                DebugPrinter.Debug(message);
                                JSONArray jsonArray=new JSONArray(message);
                                if(currentUser.requestsForMe==null){
                                    currentUser.requestsForMe=new ArrayList<ChatRequest>();
                                }
                                for(int i=0;i<jsonArray.length();i++){
                                    ChatRequest.addToList(currentUser.getRequestsForMe(),new ChatRequest(jsonArray.getJSONObject(i)));
                                }
                                DebugPrinter.Debug(currentUser.requestsForMe.size()+"koko");
                                List<UserLoadedListener>listenersForInformationUpdated=getListenersForInformationUpdated();
                                for(int i=0;i<listenersForInformationUpdated.size();i++){
                                    if(listenersForInformationUpdated.get(i)!=null){
                                        listenersForInformationUpdated.get(i).informationUpdated();
                                        DebugPrinter.Debug("tallennuksessa listenerit;" + listenersForInformationUpdated.get(i).toString());
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },jsonObject));
            }else{
                repeat=false;
                getPendingChatRequestsThread=null;
            }
        }
    }



    public Conversation addConversation(final Conversation conversation)
    {
        if(conversationRunnable==null)
        {
            conversationRunnable = new GMT();
            BackgroundThread.addOurRunnable(conversationRunnable);
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
        saveLocal();

        /**
         * Instead of saving the whole user, we check what is needed to save at the moment(obj)
         * by JariJ 15.3.17
         */

        if(obj!=null && obj instanceof Conversation)
        {
            JSONObject jsonObject = new JSONObject();
            Conversation conversation = (Conversation) obj;
            //DebugPrinter.Debug("Conversation: " + conversation);
            try
            {
                jsonObject.put(Constants.job, Constants.save_conversation);
                jsonObject.put(Constants.conversation, conversation.toJSon());
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            NetMessage netMessage = new NetMessage(null, new NetworkReturn()
            {
                @Override
                public void returnMessage(String message)
                {
                    if(message.equals(Constants.networkconn_failed))
                    {
                        //save();
                    }
                }
            }, null);
            Network.addNetMessage(new NetMessage(null, null, jsonObject));
        }
        else
        {
            save();
        }

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

    public List<ChatRequest> getRequestsForMe(){
        if(requestsForMe==null){
            requestsForMe=new ArrayList<>();
        }
        return requestsForMe;
    }
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if(currentUser!=null){
            currentUser.save();
        }
    }

    private static class GetChatRequestResponses extends OurRunnable{

        GetChatRequestResponses(){
            super(true,1*1000);
        }
        @Override
        public void run() {
            if(true)
            {
                return;
            }
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put(Constants.job,"getChatResponses");
                jsonObject.put(Constants.user_name,currentUser.userName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Network.addNetMessage(new NetMessage(null, new NetworkReturn() {
                @Override
                public void returnMessage(String message) {
                    if(!message.equals(Constants.networkconn_failed)&&!message.startsWith("no responses found")){
                        JSONObject jsonObject1= null;
                        try {

                            JSONArray jsonArray=new JSONArray(message);
                            for(int i=0;i<jsonArray.length();i++){
                                new ChatRequestResponse(jsonArray.getJSONObject(i)).showChatResponse(context);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            },jsonObject));
        }
    }
    public void addChatRquestFromMe(ChatRequest chatRequest){
        requestsFromMe.add(chatRequest);
    }
    public List<ChatRequest> getMySentChatRequests(){
        return requestsFromMe;
    }

    private static class LogOutClass {
        Intent intent;
        Context context;
        public LogOutClass(){
            super();
        }
        public LogOutClass(Intent intent,Context context)
        {
            this.intent=intent;
            this.context=context;
        }
        public void logout() {
            mGoogleApiClient.connect();
            mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(@Nullable Bundle bundle) {

                    if (mGoogleApiClient.isConnected()) {
                        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                if (status.isSuccess()) {
                                    if(context!=null&&intent!=null)
                                    {
                                        context.startActivity(intent);
                                    }
                                }
                            }
                        });
                    }
                }

                @Override
                public void onConnectionSuspended(int i) {
                }
            });
        }
    }
}
