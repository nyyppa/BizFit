package com.bizfit.bizfit;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by attey on 02/12/2016.
 */

public class MyNewAndBetterMessage implements NetworkReturn, Serializable {

    private String resipient="";
    private String sender="";
    private String message="";
    private long creationTime=0;
    private boolean hasBeenSent=false;
    private MyNewAndBetterConversation myNewAndBetterConversation;
    private Job job;


    public MyNewAndBetterMessage(JSONObject jsonObject,MyNewAndBetterConversation myNewAndBetterConversation){
        this.myNewAndBetterConversation=myNewAndBetterConversation;
        try
        {
            if(jsonObject.has(Constants.sender))
            {
                sender=jsonObject.getString(Constants.sender);
            }
            if(jsonObject.has(Constants.resipient))
            {
                resipient=jsonObject.getString(Constants.resipient);
            }
            if(jsonObject.has(Constants.message))
            {
                message=jsonObject.getString(Constants.message);
            }

            if(jsonObject.has(Constants.creationTime))
            {
                creationTime=jsonObject.getLong(Constants.creationTime);
            }
            if(jsonObject.has(Constants.hasBeenSent))
            {
                hasBeenSent=jsonObject.getBoolean(Constants.hasBeenSent);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        setJob();
    }

    public MyNewAndBetterMessage(MyNewAndBetterConversation myNewAndBetterConversation,String resipient,String sender,String message){
        this.myNewAndBetterConversation=myNewAndBetterConversation;
        this.resipient=resipient;
        this.sender=sender;
        this.message=message;
        creationTime=System.currentTimeMillis();
        setJob();
    }

    private void setJob(){
        if(myNewAndBetterConversation.getOwner().equals(getSender())){
            job=Job.OUTGOING;
        }else{
            job=Job.INCOMING;
        }
    }
    public Job getJob(){
        return job;
    }
    public String getResipient() {
        return resipient;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public boolean getHasBeenSent() {
        return hasBeenSent;
    }

    private void setHasBeenSent(boolean hasBeenSent)
    {
        this.hasBeenSent=hasBeenSent;
    }


    public void sendMessage(String targetAddress){
        JSONObject message=new JSONObject();
        try {
            setHasBeenSent(true);
            message.put(Constants.job, Constants.send_message);
            message.put(Constants.message, this.toJson());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        NewAndBetterNetwork.addNetMessage(new NetMessage(targetAddress,this,message));

    }
    public void checkToResend()
    {
        if(!getHasBeenSent() && getJob()==Job.OUTGOING)
        {
            sendMessage(null);
        }
    }

    @Override
    public void returnMessage(String message) {
        if (!message.equals("failed"))
        {
            setHasBeenSent(true);
            myNewAndBetterConversation.getUser().save();
        }
        else
        {
            setHasBeenSent(false);
        }
    }
    public JSONObject toJson(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(Constants.sender, sender);
            jsonObject.put(Constants.resipient ,resipient);
            jsonObject.put(Constants.message, message);
            jsonObject.put(Constants.creationTime, creationTime);
            jsonObject.put(Constants.hasBeenSent, hasBeenSent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public enum Job{
        OUTGOING, INCOMING;
    }
}


