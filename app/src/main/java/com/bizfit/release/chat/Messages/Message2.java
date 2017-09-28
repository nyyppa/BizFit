package com.bizfit.release.chat.Messages;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;

import com.bizfit.release.BackgroundThread;
import com.bizfit.release.OurRunnable;
import com.bizfit.release.User;
import com.bizfit.release.chat.Conversation;
import com.bizfit.release.chat.Messages.FileObjects.FileObject;
import com.bizfit.release.network.NetMessage;
import com.bizfit.release.network.Network;
import com.bizfit.release.network.NetworkReturn;
import com.bizfit.release.utils.Constants;
import com.bizfit.release.utils.DBHelper;
import com.bizfit.release.utils.OurDateTime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

/**
 * Created by attey on 02/12/2016.
 */

public abstract class Message2 implements NetworkReturn, Serializable {
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
    OurDateTime timestamp;
    MessageType messageType;
    FileObject fileObject;

    public static Message2 createMessage(Conversation conversation, String resipient, String sender, String message,MessageType messageType,FileObject file){
        switch (messageType) {
            case TEXT:
                return new MessageText(conversation,resipient,sender,message,messageType);
            case PICTURE:
                break;
            case VOICE:
                break;
            case VIDEO:
                break;
        }
        return null;
    }
    public static Message2 createMessage(JSONObject jsonObject,Conversation conversation){
        MessageType messageType= MessageType.TEXT;
        if(jsonObject.has("messageType")){
            try {
                messageType=MessageType.valueOf(jsonObject.getString("messageType"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        switch (messageType) {
            case TEXT:
                return new MessageText(jsonObject,conversation);
            case PICTURE:
                break;
            case VOICE:
                break;
            case VIDEO:
                break;
        }
        return null;
    }
    public static Message2 createMessage(Cursor cursor){
        MessageType messageType= MessageType.TEXT;
        messageType=MessageType.valueOf(cursor.getString(cursor.getInt(cursor.getColumnIndex("messageType"))));
        switch (messageType) {
            case TEXT:
                return new MessageText(cursor);
            case PICTURE:
                break;
            case VOICE:
                break;
            case VIDEO:
                break;
        }
        return null;
    }

    protected Message2(Conversation conversation, String resipient, String sender, String message,MessageType messageType,FileObject file)
    {
        this(conversation, resipient, sender, message, messageType);
    }
    protected Message2(Conversation conversation, String resipient, String sender, String message,MessageType messageType)
    {
        this.conversation=conversation;
        this.resipient=resipient;
        this.sender=sender;
        this.message=message;
        this.messageType=messageType;
        setJob();
        creationTime= GregorianCalendar.getInstance().getTimeInMillis();
        timestamp = new OurDateTime(creationTime);
        uuid=UUID.randomUUID();
    }
    protected Message2(JSONObject jsonObject,Conversation conversation){
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
    }

    protected Message2(Cursor cursor){
        /*
        Message message = new Message();
        message.resipient = cursor.getString(cursor.getColumnIndex("resipient"));
        message.sender = cursor.getString(cursor.getColumnIndex("sender"));
        message.uuid= UUID.fromString(cursor.getString(cursor.getColumnIndex("UUID")));
        message.message=cursor.getString(cursor.getColumnIndex("message"));
        message.hasBeenSeen=intToBoolean(cursor.getInt(cursor.getColumnIndex("hasBeenSeen")));
        message.hasBeenSent=intToBoolean(cursor.getInt(cursor.getColumnIndex("hasBeenSent")));
        message.creationTime=cursor.getInt(cursor.getColumnIndex("creationTime"));
        message.setJob();*/
    }

    public void setJob(){
        if(conversation.getOwner().equals(sender)){
            job=Job.OUTGOING;
        }else{
            job=Job.INCOMING;
        }
    }
    public MessageType getMessageType()
    {
        return messageType;
    }
    public abstract void drawThySelf(View view);
    public void saveToSQLite(DBHelper dbHelper,SQLiteDatabase db)
    {
        if(!dbHelper.isTableExists(db, "Messages"))
        {
            db.execSQL(" CREATE TABLE Messages\n" +
                    "     (\n" +
                    "         UUID text NOT NULL PRIMARY KEY,\n" +
                    "         message text NOT NULL,\n" +
                    "         sender text NOT NULL,\n" +
                    "         messageType text NOT NULL,\n" +
                    "         resipient text NOT NULL,\n" +
                    "         fileid text,\n" +
                    "         creationTime int NOT NULL,\n" +
                    "         hasBeenSeen bool,\n" +
                    "         hasBeenSent bool\n" +
                    "     );");
        }
        ContentValues contentValues=new ContentValues();

        /*contentValues.put("message",message.getMessage());
        contentValues.put("sender", message.getSender());
        contentValues.put("resipient", message.getSender());
        contentValues.put("creationTime", message.getCreationTime());
        contentValues.put("hasBeenSeen", message.getHasBeenSeen());
        contentValues.put("hasBeenSent",message.getHasBeenSent());
        contentValues.put("UUID",message.getUUID().toString());*/
        db.insertWithOnConflict("Messages", null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
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
    public Job getJob()
    {
        return job;
    }


    private void setHasBeenSent(boolean hasBeenSent)
    {
        this.hasBeenSent=hasBeenSent;
    }

    public String getSender()
    {
        return sender;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public boolean updateHasBeenSeen(boolean newValue) {
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

                        //db.saveMessage(Message.this, db.getWritableDatabase());
                    }
                }
            });

        }
        return oldValue!=newValue;
    }

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
    public boolean getHasBeenSeen() {
        return hasBeenSeen;
    }

    public String getMessage() {
        return message;
    }

    public boolean hasBeenPinned(String user) {
        return true;
    }

    public boolean equals(Message2 message2)
    {
        return uuid.equals(message2.uuid);
    }

    public enum MessageType{
        TEXT,PICTURE,VOICE,VIDEO
    }

    public JSONObject toJSON()
    {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(Constants.sender, sender);
            jsonObject.put(Constants.resipient ,resipient);
            jsonObject.put(Constants.message, message);
            jsonObject.put(Constants.creationTime, creationTime);
            jsonObject.put(Constants.hasBeenSent, hasBeenSent);
            jsonObject.put(Constants.hasBeenSeen,hasBeenSeen);
            if(uuid!=null)
            {
                jsonObject.put(Constants.UUID,uuid.toString());
            }
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; pinnedUsers!=null && i < pinnedUsers.size(); i++){
                jsonArray.put(pinnedUsers.get(i));
            }
            if (jsonArray.length()>0){
                jsonObject.put("pinners", jsonArray);
            }
            if(fileObject!=null)
            {
                jsonObject.put("fileObjet",fileObject.toJSON());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    public void checkToResend()
    {
        if(!hasBeenSent && getJob()==Job.OUTGOING)
        {
            sendMessage(null);
        }
    }

    public void sendMessage(String targetAddress){
        JSONObject message=new JSONObject();
        try {
            setHasBeenSent(true);
            message.put(Constants.job, Constants.send_message);
            message.put(Constants.message, this.toJSON());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        Network.addNetMessage(new NetMessage(targetAddress,this,message));

    }
    public enum Job{
        OUTGOING, INCOMING;
    }
}


