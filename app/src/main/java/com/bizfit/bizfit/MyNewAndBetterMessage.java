package com.bizfit.bizfit;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by attey on 02/12/2016.
 */

public class MyNewAndBetterMessage implements NetworkReturn {

    private String resipient="";
    private String sender="";
    private String message="";
    private long creationTime=0;
    private boolean hasBeenSent=false;
    private MyNewAndBetterConversation myNewAndBetterConversation;
    private Job job;


    public MyNewAndBetterMessage(JSONObject jsonObject,MyNewAndBetterConversation myNewAndBetterConversation){
        this.myNewAndBetterConversation=myNewAndBetterConversation;
        try {
            sender=jsonObject.getString("sender");
            resipient=jsonObject.getString("resipient");
            if(jsonObject.has("message")){
                message=jsonObject.getString("message");
            }
            creationTime=jsonObject.getLong("creationTime");
            hasBeenSent=jsonObject.getBoolean("hasBeenSent");
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
            //// TODO: 02/12/2016 check what correct job was
            message.put("Job","send_message");
            message.put("message",this.toJson());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NewAndBetterNetwork.addNetMessage(new NetMessage(targetAddress,this,message));

    }
    public void checkToResend(){
        if(!getHasBeenSent()){
            sendMessage(null);
        }
    }

    @Override
    public void returnMessage(String message) {
        if (!message.equals("failed")){
            setHasBeenSent(true);
            myNewAndBetterConversation.getUser().save();
        }
    }
    public JSONObject toJson(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("sender",sender);
            jsonObject.put("resipient",resipient);
            jsonObject.put("message",message);
            jsonObject.put("creationTime",creationTime);
            jsonObject.put("hasBeenSent",hasBeenSent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public enum Job{
        OUTGOING, INCOMING;
    }
}


