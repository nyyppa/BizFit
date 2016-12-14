package com.bizfit.bizfit;

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
public class MyNewAndBetterConversation implements NetworkReturn,Serializable{

    private String owner="";
    private String other="";
    private List<MyNewAndBetterMessage> myNewAndBetterMessageList;

    private User user;
    private transient ChatFragment chatFragment;

    public MyNewAndBetterConversation(JSONObject jsonObject, User user){
        JSONArray jsonArray=null;
        this.user=user;
        try {
            if(jsonObject.has("owner"))
            {
                owner=jsonObject.getString("owner");
            }
            if(jsonObject.has("other"))
            {
                other=jsonObject.getString("other");
            }
            myNewAndBetterMessageList=new ArrayList<>();
            if(jsonObject.has("messages"))
            {
                jsonArray=jsonObject.getJSONArray("messages");
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
            jsonObject.put("owner",owner);
            jsonObject.put("other",other);
            JSONArray jsonArray=new JSONArray();
            for(int i=0;i<myNewAndBetterMessageList.size();i++){
                jsonArray.put(myNewAndBetterMessageList.get(i).toJson());
            }
            jsonObject.put("messages",jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  jsonObject;
    }
    public void getNewMessagesAndSendOldOnes(){
        JSONObject jsonObject=new JSONObject();
        //// TODO: 02/12/2016 check real job from server
        try {
            jsonObject.put("Job","get_message");
            jsonObject.put("owner",getOwner());
            jsonObject.put("other",getOther());
            jsonObject.put("creationTime",getLastReceivedMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NewAndBetterNetwork.addNetMessage(new NetMessage(null,this,jsonObject));
        for(int i=0;i<myNewAndBetterMessageList.size();i++){
            myNewAndBetterMessageList.get(i).checkToResend();
        }
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
                return (int)(myNewAndBetterMessage.getCreationTime()-t1.getCreationTime());
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
        System.out.println(chatFragment+" setChatFragment");

    }
    @Override
    public void returnMessage(String message) {
        System.out.println(message);
        if(!message.equals("failed")){
            try {
                JSONArray jsonArray=new JSONArray(message);
                boolean messagesRecieved=false;
                for(int i=0;i<jsonArray.length();i++)
                {
                    System.out.println("json "+i+" : "+jsonArray.getString(i).toString());
                    myNewAndBetterMessageList.add(0, new MyNewAndBetterMessage(new JSONObject(jsonArray.getString(i)),this));
                    messagesRecieved=true;

                }
                System.out.println(this+" Conversation");
                System.out.println(chatFragment+" chatFragment");
                if(chatFragment!=null&&messagesRecieved)
                {
                    chatFragment.getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if(chatFragment.getmAdapter().getItemCount()>0)
                            {
                                System.out.println("TestiPaikka");
                                chatFragment.getmAdapter().notifyItemInserted(0);
                                chatFragment.getmRecyclerView().smoothScrollToPosition(0);
                            }

                        }
                    });
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
