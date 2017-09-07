package com.bizfit.bizfit.chat;

import com.bizfit.bizfit.BackgroundThread;
import com.bizfit.bizfit.DebugPrinter;
import com.bizfit.bizfit.OurRunnable;
import com.bizfit.bizfit.User;
import com.bizfit.bizfit.chat.MessageObjects.MessageObject;
import com.bizfit.bizfit.utils.Constants;
import com.bizfit.bizfit.network.NetMessage;
import com.bizfit.bizfit.network.Network;
import com.bizfit.bizfit.network.NetworkReturn;
import com.bizfit.bizfit.utils.DBHelper;
import com.bizfit.bizfit.utils.OurDateTime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ListIterator;
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
    List <String> pinnedUsers;
    MessageObject messageObject;
    OurDateTime timestamp;
    MessageType messageType;



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
                timestamp = new OurDateTime(creationTime);
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
            if(jsonObject.has("pinners")) {
                JSONArray jsonArray = jsonObject.getJSONArray("pinners");
                pinnedUsers= new ArrayList<>();
                for(int i = 0; i < jsonArray.length(); i++) {
                    pinnedUsers.add(jsonArray.getString(i));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        setJob();
        codeCheck();
    }


    public Message(Conversation conversation, String resipient, String sender, String message,MessageType messageType){
        this.conversation = conversation;
        this.resipient=resipient;
        this.sender=sender;
        this.message=message;
        this.messageType=messageType;
        creationTime= GregorianCalendar.getInstance().getTimeInMillis();
        timestamp = new OurDateTime(creationTime);
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
        DebugPrinter.Debug("hasbeenseen job:" + newValue+":"+hasBeenSeen);
        if(job==Job.OUTGOING)
        {
            return false;
        }
        boolean oldValue=hasBeenSeen;
        this.hasBeenSeen=newValue;
        if(oldValue !=newValue)
        {
            updateHasBeenSeenToServer(newValue);
            BackgroundThread.addOurRunnable(new OurRunnable() {
                @Override
                public void run()
                {
                    DBHelper db = User.getDBHelper();
                    if(db !=null)
                    {

                        db.saveMessage(Message.this, db.getWritableDatabase());
                    }
                }
            });

        }
        return oldValue!=newValue;
    }

    /** Made by JariJ
     * Last updated 10.5.17 by JariJ
     * @param newValue updated HasbeenSeen boolean
     */
    public void updateHasBeenSeenToServer(final boolean newValue)
    {
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put(Constants.job,"updateMessageHasBeenSeen");
            jsonObject.put("UpdatedHasBeenSeen", newValue);
            jsonObject.put(Constants.UUID,uuid.toString());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        NetMessage netMessage=new NetMessage(null, new NetworkReturn() {
            @Override
            public void returnMessage(String message) {
                if(message.equals(Constants.networkconn_failed))
                {
                    updateHasBeenSeenToServer(newValue);
                }
            }
        },jsonObject);
        Network.addNetMessage(netMessage);
    }
    public boolean equals(Message message){
        return this.uuid.equals(message.uuid);
    }
    public void setJob(){
        if(conversation.getOwner().equals(getSender())){
            job=Job.OUTGOING;
        }else{
            job=Job.INCOMING;
        }
    }
    public boolean getHasBeenSeen()
    {
        if(job==Job.OUTGOING)
        {
            return true;
        }
        return hasBeenSeen;
    }
    private void codeCheck(){
        switch (messageType) {
            case TEXT:
                break;
            case VIDEO:
                break;
            case VOICE:
                break;
            case DOCUMENT:
                break;
            case FILE:
                break;
        }
    }
    public Job getJob(){
        return job;
    }
    public String getResipient() {
        return resipient;
    }


    /**
     * Made by JariJ 19.4.17
     * @return time - Preferred time to display on chat
     */
    public String getTimestamp()
    {
        String time="";
        if(timestamp.isToday())
        {
            time = timestamp.getClockDisplayName();
        }
        else if(timestamp.isThisYear())
        {
            time = timestamp.getDay() + "-" + timestamp.getMonthDisplayName();
        }
        else
        {
            time = timestamp.getFullDisplayName();
        }
        return time;

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
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; pinnedUsers!=null && i < pinnedUsers.size(); i++){
                jsonArray.put(pinnedUsers.get(i));
            }
            if (jsonArray.length()>0){
                jsonObject.put("pinners", jsonArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public enum Job{
        OUTGOING, INCOMING;
    }

    public enum Status{
        PENDING, ACCEPTED, CANCELLED, DELETED
    }

    public boolean hasBeenPinned(String user) {
        boolean pinned = false;
        for (int i = 0; pinnedUsers!=null && i < pinnedUsers.size() && !pinned; i++) {
            if (pinnedUsers.get(i).equals(user)) {
                pinned = true;
            }
        }

        return pinned;
    }

    public void editPinnedUser(String user) {
        DebugPrinter.Debug("pinned:"+user);
        if(pinnedUsers==null) {
            pinnedUsers = new ArrayList<>();
        }
        if(!hasBeenPinned(user)) {
           pinnedUsers.add(user);
        }
        else {
            removePinnedUser(user);
        }
    }
    private void removePinnedUser(String user) {
        if(pinnedUsers!=null) {
            ListIterator<String> iterator=pinnedUsers.listIterator();
            while (iterator.hasNext()) {
                String s = iterator.next();
                if(s.equals(user)) {
                    iterator.remove();
                    break;
                }
            }
        }
    }
    public enum MessageType{
        TEXT,VIDEO,VOICE,DOCUMENT,FILE
    }

    @Override
    public String toString()
    {
        return super.toString()+" "+message;
    }
}


