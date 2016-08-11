package com.bizfit.bizfit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Atte Ylivrronen on 10.5.2016.
 */
public class Conversation implements NetworkReturn {
    String sender;
    String resipiant;
    List<Message> messages;

    public Conversation(String sender, String resipiant){
        this.sender=sender;
        this.resipiant=resipiant;
        messages=new ArrayList<>(0);
    }

    public Conversation(JSONObject jsonObject){
        messages=new ArrayList<>(0);
        try {
            this.sender=jsonObject.getString("sender");
            this.resipiant=jsonObject.getString("resipiant");
            JSONArray jsonArray =jsonObject.getJSONArray("messages");
            for(int i=0;i<jsonArray.length();i++){
                messages.add(new Message(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String text){
        Message m=new Message(sender,resipiant,text);
        messages.add(m);
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("Job","SendMessage");
            jsonObject.put("message",m.toJSON());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MyNetwork n=new MyNetwork(null,this,jsonObject);

    }

    public List<Message> getMessages(){
        Comparator<Message> t = new Comparator<Message>() {
            @Override
            public int compare(Message lhs, Message rhs) {
                return (int) (-1*(lhs.getCreationTime()-rhs.getCreationTime()));
            }
        };
        return messages;
    }

    public JSONObject toJSON(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("sender",sender);
            jsonObject.put("resipiant",resipiant);
            JSONArray jsonArray=new JSONArray();
            for(int i=0;i<messages.size();i++){
                jsonArray.put(messages.get(i).toJSON());
            }
            jsonObject.put("messages",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
    public void checkForMessages(){
        
    }

    @Override
    public void returnMessage(String message) {

    }
}

