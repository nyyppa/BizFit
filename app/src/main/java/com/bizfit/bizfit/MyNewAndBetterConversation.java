package com.bizfit.bizfit;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.os.Debug;
import android.support.v4.content.ContextCompat;
import android.telecom.Connection;
import android.telecom.ConnectionService;
import android.util.Log;

import com.bizfit.bizfit.fragments.ChatFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.google.android.gms.internal.zzs.TAG;

/**
 * Created by attey on 02/12/2016.
 */

//todo prevent android from cloning thisw
public class MyNewAndBetterConversation implements NetworkReturn,Serializable{

    private String owner="";
    private String other="";
    private List<MyNewAndBetterMessage> myNewAndBetterMessageList;

    private User user;
    private transient ChatFragment chatFragment;
    private NetworkInfo netinfo;

    public MyNewAndBetterConversation(JSONObject jsonObject, User user){
        JSONArray jsonArray=null;
        this.user=user;
        try {
            if(jsonObject.has(Constants.owner))
            {
                owner=jsonObject.getString(Constants.owner);
            }
            if(jsonObject.has(Constants.other))
            {
                other=jsonObject.getString(Constants.other);
            }
            myNewAndBetterMessageList=new ArrayList<>();
            if(jsonObject.has(Constants.messages))
            {
                jsonArray=jsonObject.getJSONArray(Constants.messages);
                for (int i=0;i<jsonArray.length();i++){
                    myNewAndBetterMessageList.add(new MyNewAndBetterMessage(jsonArray.getJSONObject(i),this));
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public MyNewAndBetterConversation(String owner,String other,User user){
        this.other=other;
        this.owner=owner;
        myNewAndBetterMessageList=new ArrayList<>();
        this.user=user;
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        user.addClonedMyNewAndBetterConversation(this);

    }

    public User getUser(){
        return user;
    }
    public List<MyNewAndBetterMessage> getMessages()
    {
        if(myNewAndBetterMessageList==null){
            myNewAndBetterMessageList=new ArrayList<>();
        }
        return myNewAndBetterMessageList;
    }
    public JSONObject toJSon(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(Constants.owner,owner);
            jsonObject.put(Constants.other,other);
            JSONArray jsonArray=new JSONArray();
            for(int i=0;i<myNewAndBetterMessageList.size();i++){
                jsonArray.put(myNewAndBetterMessageList.get(i).toJson());
            }
            jsonObject.put(Constants.messages,jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  jsonObject;
    }
    public void getNewMessagesAndSendOldOnes()
    {
        getIncomingMessages();
        getOutgoingMessages();
        for(int i=0;i<myNewAndBetterMessageList.size();i++){
            myNewAndBetterMessageList.get(i).checkToResend();
        }
    }
    private void getIncomingMessages()
    {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(Constants.job,Constants.get_message_incoming);
            jsonObject.put(Constants.owner,getOwner());
            jsonObject.put(Constants.other,getOther());
            jsonObject.put(Constants.creationTime,getLastReceivedMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NewAndBetterNetwork.addNetMessage(new NetMessage(null,this,jsonObject));
    }
    private void getOutgoingMessages()
    {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(Constants.job,Constants.get_message_outgoing);
            jsonObject.put(Constants.owner,getOwner());
            jsonObject.put(Constants.other,getOther());
            jsonObject.put(Constants.creationTime,getLastSentMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NewAndBetterNetwork.addNetMessage(new NetMessage(null,this,jsonObject));
    }
    private long getLastReceivedMessage(){
        long lastReceivedMessage=0;
        for(int i=0;i<myNewAndBetterMessageList.size();i++){
            MyNewAndBetterMessage myNewAndBetterMessage=myNewAndBetterMessageList.get(i);
            switch (myNewAndBetterMessage.getJob()){
                case OUTGOING:
                    break;
                case INCOMING:
                    if(myNewAndBetterMessage.getCreationTime()>lastReceivedMessage){
                        lastReceivedMessage=myNewAndBetterMessage.getCreationTime();
                    }
                    break;
            }
        }
        return lastReceivedMessage;
    }
    private long getLastSentMessage(){
        long lastSentMessage=0;
        for(int i=0;i<myNewAndBetterMessageList.size();i++){
            MyNewAndBetterMessage myNewAndBetterMessage=myNewAndBetterMessageList.get(i);
            switch (myNewAndBetterMessage.getJob()){
                case OUTGOING:
                    if(myNewAndBetterMessage.getCreationTime()>lastSentMessage){
                        lastSentMessage=myNewAndBetterMessage.getCreationTime();
                    }
                    break;
                case INCOMING:
                    break;
            }
        }
        return lastSentMessage;
    }

    // By JariJ 21.12.16 Solution taken from http://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out?page=2&tab=votes#tab-top
    public boolean isOnline(Context c)
    {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public String getOwner(){
        return owner;
    }
    public String getOther(){
        return other;
    }
    public List<MyNewAndBetterMessage> sortConversation(){

        Comparator<MyNewAndBetterMessage> comparator=new Comparator<MyNewAndBetterMessage>() {
            @Override
            public int compare(MyNewAndBetterMessage myNewAndBetterMessage, MyNewAndBetterMessage t1) {
                return -1*(int)(myNewAndBetterMessage.getCreationTime()-t1.getCreationTime());
            }
        };
        Collections.sort(myNewAndBetterMessageList,comparator);

        return  myNewAndBetterMessageList;
    }

    public void createMessage(String message){
        MyNewAndBetterMessage myNewAndBetterMessage=new MyNewAndBetterMessage(this,getOther(),getOwner(),message);
        myNewAndBetterMessage.sendMessage(null);
        if(myNewAndBetterMessageList==null){
            myNewAndBetterMessageList=new ArrayList<>();
        }
        myNewAndBetterMessageList.add(0,myNewAndBetterMessage);
        getUser().save(this);

    }
    @Override
    public void returnMessage(String message) {
        System.out.println(message);
        if(!message.equals("failed")){
            try {
                JSONArray jsonArray=new JSONArray(message);
                boolean messagesReceived=false;
                MyNewAndBetterMessage message1=null;
                for(int i=0;i<jsonArray.length();i++)
                {
                    myNewAndBetterMessageList.add(0, new MyNewAndBetterMessage(new JSONObject(jsonArray.getString(i)),this));
                    message1=myNewAndBetterMessageList.get(0);
                    messagesReceived=true;

                }
                if(chatFragment!=null&&messagesReceived&&chatFragment.getActivity()!=null)
                {
                    chatFragment.getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if(chatFragment.getmAdapter().getItemCount()>0)
                            {
                                //System.out.println("TestiPaikka");
                                sortConversation();
                                chatFragment.getmAdapter().notifyItemInserted(0);
                                chatFragment.getmRecyclerView().smoothScrollToPosition(0);

                            }

                        }
                    });
                }else if(messagesReceived&&message1!=null){
                    NotificationSender.sendNotification(User.getContext(),getOther(),message1.getMessage());
                    sortConversation();
                }
                //getUser().save(this);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public void setChatFragment(ChatFragment chatFragment)
    {

        this.chatFragment=chatFragment;
    }


}
