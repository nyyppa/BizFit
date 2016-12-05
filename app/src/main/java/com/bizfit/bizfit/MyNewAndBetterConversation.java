package com.bizfit.bizfit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by attey on 02/12/2016.
 */

public class MyNewAndBetterConversation implements NetworkReturn {

    String owner="";
    String other="";
    List<MyNewAndBetterMessage> myNewAndBetterMessageList;
    public MyNewAndBetterConversation(JSONObject jsonObject){
        try {
            owner=jsonObject.getString("owner");
            other=jsonObject.getString("other");
            JSONArray jsonArray=jsonObject.getJSONArray("messages");
            myNewAndBetterMessageList=new ArrayList<>();
            for (int i=0;i<jsonArray.length();i++){
                myNewAndBetterMessageList.add(new MyNewAndBetterMessage(jsonArray.getJSONObject(i),this));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public MyNewAndBetterConversation(String owner,String other){
        this.other=other;
        this.owner=owner;
    }
    public List<MyNewAndBetterMessage> getMessages(){
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
    public void getNewMessagesAndSentOldOnes(){
        JSONObject jsonObject=new JSONObject();
        //// TODO: 02/12/2016 check real job from server
        try {
            jsonObject.put("job","getMessages");
            jsonObject.put("owner",getOwner());
            jsonObject.put("other",getOther());
            jsonObject.put("creationTime",getLastReceivedMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new MyNetwork(null,this,jsonObject)).start();
        for(int i=0;i<myNewAndBetterMessageList.size();i++){
            myNewAndBetterMessageList.get(i).checkToResend();
        }
    }
    private long getLastReceivedMessage(){
        long lastReceivedMessage=0;
        for(int i=0;i<myNewAndBetterMessageList.size();i++){
            MyNewAndBetterMessage myNewAndBetterMessage=myNewAndBetterMessageList.get(i);
            switch (myNewAndBetterMessage.getJob()){
                case sent:
                    break;
                case received:
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

    }
    @Override
    public void returnMessage(String message) {
        if(!message.equals("failed")){
            try {
                myNewAndBetterMessageList.add(new MyNewAndBetterMessage(new JSONObject(message),this));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
