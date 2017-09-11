package com.bizfit.bizfit.chat.Messages;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.constraint.ConstraintLayout;
import android.view.View;

import com.bizfit.bizfit.chat.Conversation;
import com.bizfit.bizfit.chat.Message;
import com.bizfit.bizfit.network.NetworkReturn;
import com.bizfit.bizfit.utils.DBHelper;
import com.bizfit.bizfit.utils.OurDateTime;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
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

    public static Message2 createMessage(Conversation conversation, String resipient, String sender, String message,MessageType messageType,Object file){
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

    protected Message2(Conversation conversation, String resipient, String sender, String message,MessageType messageType,Object file)
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

    public void checkToResend() {
    }
    public String getSender()
    {
        return sender;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public boolean updateHasBeenSeen(boolean b) {
        return true;
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

    public enum MessageType{
        TEXT,PICTURE,VOICE,VIDEO
    }
    public enum Job{
        OUTGOING, INCOMING;
    }
    public JSONObject toJSON()
    {
        return null;
    }
}


