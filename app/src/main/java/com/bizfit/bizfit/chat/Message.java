package com.bizfit.bizfit.chat;

import com.bizfit.bizfit.DebugPrinter;
import com.bizfit.bizfit.User;
import com.bizfit.bizfit.tracker.SharedTracker;
import com.bizfit.bizfit.utils.Constants;
import com.bizfit.bizfit.network.NetMessage;
import com.bizfit.bizfit.network.Network;
import com.bizfit.bizfit.network.NetworkReturn;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.UUID;

/**
 * Created by attey on 02/12/2016.
 */

public class Message implements NetworkReturn, Serializable {

    public String resipient="";
    public String sender="";
    public String message="";
    public long creationTime=0;
    public boolean hasBeenSent=false;
    public Conversation conversation;
    public Job job;
    public boolean hasBeenSeen=false;
    public UUID uuid;
    MessageObject messageObject;


    public Message(JSONObject jsonObject, Conversation conversation){
        this.conversation = conversation;
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
            if(jsonObject.has(Constants.hasBeenSeen)){
                hasBeenSeen=jsonObject.getBoolean(Constants.hasBeenSeen);
            }
            if(jsonObject.has(Constants.UUID)){
                uuid=UUID.fromString(jsonObject.getString(Constants.UUID));
            }else{
                uuid=UUID.randomUUID();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        setJob();
        codeCheck();
    }


    public Message(Conversation conversation, String resipient, String sender, String message){
        this.conversation = conversation;
        this.resipient=resipient;
        this.sender=sender;
        this.message=message;
        creationTime= GregorianCalendar.getInstance().getTimeInMillis();
        uuid=UUID.randomUUID();
        setJob();
        codeCheck();
    }

    public Message()
    {

    }
    public UUID getUUID()
    {
        return uuid;
    }
    public boolean updateHasBeenSeen(boolean newValue)
    {
        DebugPrinter.Debug("hasbeenseen job:" + job);
        if(job==Job.OUTGOING)
        {
            return false;
        }
        boolean oldValue=hasBeenSeen;
        this.hasBeenSeen=newValue;
        DebugPrinter.Debug("newValue: " + newValue + "oldValue: " + oldValue + "message: " + getMessage());
        return oldValue!=newValue;
    }
    public boolean equals(Message message){
        return this.uuid.equals(message.uuid);
    }
    private void setJob(){
        if(conversation.getOwner().equals(getSender())){
            job=Job.OUTGOING;
        }else{
            job=Job.INCOMING;
        }
    }
    public boolean getHasBeenSeen(){
        return hasBeenSeen;
    }
    private void codeCheck(){
        if(message.startsWith("code share_tracker")){
            if(messageObject==null){
                messageObject=new MessageObject() {
                    @Override
                    public void doStuff() {

                    }

                    @Override
                    public Status getStatus() {
                        return null;
                    }

                    @Override
                    public void setStatus(Status status) {

                    }

                    @Override
                    public String getText() {
                        return "Shared Tracker";
                    }
                };
            }
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
        if(message==null){
            return "";
        }
        if(messageObject!=null){
            return messageObject.getText();
        }
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
        Network.addNetMessage(new NetMessage(targetAddress,this,message));

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
        if (!message.equals(Constants.networkconn_failed))
        {
            setHasBeenSent(true);
            conversation.getUser().save(conversation);
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
            jsonObject.put(Constants.hasBeenSeen,hasBeenSeen);
            jsonObject.put(Constants.UUID,uuid.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public enum Job{
        OUTGOING, INCOMING;
    }
    public interface MessageObject{
        public void doStuff();
        public Status getStatus();
        public void setStatus(Status status);
        public String getText();
    }
    public enum Status{
        PENDING, ACCEPTED, CANCELLED, DELETED
    }
}


