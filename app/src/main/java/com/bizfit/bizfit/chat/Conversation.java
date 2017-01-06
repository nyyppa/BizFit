package com.bizfit.bizfit.chat;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.bizfit.bizfit.utils.Constants;
import com.bizfit.bizfit.network.NetMessage;
import com.bizfit.bizfit.network.Network;
import com.bizfit.bizfit.network.NetworkReturn;
import com.bizfit.bizfit.NotificationSender;
import com.bizfit.bizfit.User;
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

/**
 * Created by attey on 02/12/2016.
 */

//todo prevent android from cloning thisw
public class Conversation implements NetworkReturn,Serializable{

    private String owner="";
    private String other="";
    private List<Message> messageList;

    private User user;
    private transient ChatFragment chatFragment;
    private NetworkInfo netinfo;

    public Conversation(JSONObject jsonObject, User user){
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
            messageList =new ArrayList<>();
            if(jsonObject.has(Constants.messages))
            {
                jsonArray=jsonObject.getJSONArray(Constants.messages);
                for (int i=0;i<jsonArray.length();i++){
                    messageList.add(new Message(jsonArray.getJSONObject(i),this));
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Conversation(String owner, String other, User user){
        this.other=other;
        this.owner=owner;
        messageList =new ArrayList<>();
        this.user=user;
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        user.addClonedMyNewAndBetterConversation(this);

    }

    public User getUser(){
        return user;
    }
    public List<Message> getMessages()
    {
        if(messageList ==null){
            messageList =new ArrayList<>();
        }
        return messageList;
    }
    public JSONObject toJSon(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(Constants.owner,owner);
            jsonObject.put(Constants.other,other);
            JSONArray jsonArray=new JSONArray();
            for(int i = 0; i< messageList.size(); i++){
                jsonArray.put(messageList.get(i).toJson());
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
        for(int i = 0; i< messageList.size(); i++){
            messageList.get(i).checkToResend();
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
        Network.addNetMessage(new NetMessage(null,this,jsonObject));
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
        Network.addNetMessage(new NetMessage(null,this,jsonObject));
    }
    private long getLastReceivedMessage(){
        long lastReceivedMessage=0;
        for(int i = 0; i< messageList.size(); i++){
            Message message = messageList.get(i);
            switch (message.getJob()){
                case OUTGOING:
                    break;
                case INCOMING:
                    if(message.getCreationTime()>lastReceivedMessage){
                        lastReceivedMessage= message.getCreationTime();
                    }
                    break;
            }
        }
        return lastReceivedMessage;
    }
    private long getLastSentMessage(){
        long lastSentMessage=0;
        for(int i = 0; i< messageList.size(); i++){
            Message message = messageList.get(i);
            switch (message.getJob()){
                case OUTGOING:
                    if(message.getCreationTime()>lastSentMessage){
                        lastSentMessage= message.getCreationTime();
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
    public List<Message> sortConversation(){

        Comparator<Message> comparator=new Comparator<Message>() {
            @Override
            public int compare(Message myNewAndBetterMessage, Message t1) {
                return -1*(int)(myNewAndBetterMessage.getCreationTime()-t1.getCreationTime());
            }
        };
        Collections.sort(messageList,comparator);

        return messageList;
    }

    public void createMessage(String message){
        Message myNewAndBetterMessage=new Message(this,getOther(),getOwner(),message);
        myNewAndBetterMessage.sendMessage(null);
        if(messageList ==null){
            messageList =new ArrayList<>();
        }
        messageList.add(0,myNewAndBetterMessage);
        getUser().save(this);

    }
    @Override
    public void returnMessage(String message) {
        System.out.println(message);
        if(!message.equals("failed")){
            try {
                JSONArray jsonArray=new JSONArray(message);
                boolean messagesReceived=false;
                Message message1=null;
                for(int i=0;i<jsonArray.length();i++)
                {
                    messageList.add(0, new Message(new JSONObject(jsonArray.getString(i)),this));
                    message1= messageList.get(0);
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