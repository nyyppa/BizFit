package com.bizfit.release.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bizfit.release.ChatRequest;
import com.bizfit.release.Contact;
import com.bizfit.release.DebugPrinter;
import com.bizfit.release.chat.Conversation;
import com.bizfit.release.User;
import com.bizfit.release.chat.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Atte Ylivrronen on 28.3.2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    final String lastUser = "lastUser";


    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }



    private void saveLastUser(SQLiteDatabase db, String user) {
        if (!isTableExists(db, lastUser)) {
            db.execSQL("CREATE TABLE " + lastUser + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " + lastUser + " TEXT)");
        }
        ContentValues values = new ContentValues();

        values.put("id", 0);
        values.put(lastUser, user);
        //System.out.println("user"+user.toJSON().toString());

        db.insertWithOnConflict(lastUser, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * Modified by JariJ 24.5.17
     * Saves user and all of it's dependands to given database
     *

     * @param user user to save
     *
     */
    public void saveUser(User user)
    {
        SQLiteDatabase db=getWritableDatabase();
        initDB();

        ContentValues userValues = new ContentValues();
        userValues.put("name", user.userName);
        userValues.put("type",1);
        //System.out.println("user"+user.toJSON().toString());
        db.insertWithOnConflict("User", null, userValues, SQLiteDatabase.CONFLICT_REPLACE);

        saveLastUser(db, user.userName);
        saveConversation(user, db);
        saveChatRequest(user, db);
        saveContact(user.myContactInfo, db);



        db.close();



       // if (!isTableExists(db, "User")) {
       //     db.execSQL("CREATE TABLE user (userName TEXT PRIMARY KEY,user TEXT)");
       // }
    }

    /**
     * Made by JariJ 28.3.17
     * Saves Message and all of it's dependands to given database
     * @param user
     * @param db
     */
    public void saveConversation(User user, SQLiteDatabase db)
    {
        for(int i=0; i < user.getConversations().size(); i++)
        {
            for(int j=0;j<user.getConversations().get(i).getMessages().size();j++)
            {
                //saveMessage(user.getConversations().get(i).getMessages().get(j),db);
            }
            ContentValues conversationsValues = new ContentValues();
            conversationsValues.put("other",user.getConversations().get(i).getOther());
            conversationsValues.put("owner",user.getConversations().get(i).getOwner());
            conversationsValues.put("conversationID",user.getConversations().get(i).getOwner()+"+"+user.getConversations().get(i).getOther());
            db.insertWithOnConflict("Conversations", null, conversationsValues, SQLiteDatabase.CONFLICT_REPLACE);
            saveContact(user.getConversations().get(i).getContact(),db);
        }

    }
    public void saveMessage(Message message,SQLiteDatabase db)
    {
        ContentValues contentValues=new ContentValues();
        contentValues.put("message",message.getMessage());
        contentValues.put("sender", message.getSender());
        contentValues.put("resipient", message.getSender());
        contentValues.put("creationTime", message.getCreationTime());
        contentValues.put("hasBeenSeen", message.getHasBeenSeen());
        contentValues.put("hasBeenSent",message.getHasBeenSent());
        contentValues.put("UUID",message.getUUID().toString());
        db.insertWithOnConflict("Messages", null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /** Made by JariJ
     * Last modified 9.5.17 by JariJ
     * @param contact
     * @param db
     */
    public void saveContact(Contact contact, SQLiteDatabase db)
    {
        if(contact==null)
        {
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("userID", contact.getUserID());
        byte[]byteArray = contact.pictureToByteArray();
        if(byteArray !=null)
        {
            contentValues.put("picture", byteArray);
        }
        contentValues.put("firstName", contact.getFirstName());
        contentValues.put("lastName", contact.getLastName());
        db.insertWithOnConflict("Contact", null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    private void saveChatRequest(User user,  SQLiteDatabase db)
    {
        ContentValues contentValues=new ContentValues();

        if(user!=null &&user.getRequestsForMe()!=null && user.getRequestsForMe().size()>0)
        {
            for(int i=0; i < user.getRequestsForMe().size(); i++)
            {
                contentValues.put("pendingRequestID", user.getRequestsForMe().get(i).getUUID());
                contentValues.put("customer", user.getRequestsForMe().get(i).getCustomer());
                contentValues.put("coach", user.getRequestsForMe().get(i).getCoach());
                contentValues.put("need", user.getRequestsForMe().get(i).getNeed());
                contentValues.put("skill", user.getRequestsForMe().get(i).getSkill());
                contentValues.put("message", user.getRequestsForMe().get(i).getMessage());

                db.insertWithOnConflict("Pending_request", null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            }
        }
        if(user!=null &&user.getMySentChatRequests()!=null && user.getMySentChatRequests().size()>0)
        {
            for(int i=0; i < user.getMySentChatRequests().size(); i++)
            {
                contentValues.put("pendingRequestID", user.getMySentChatRequests().get(i).getUUID());
                contentValues.put("customer", user.getMySentChatRequests().get(i).getCustomer());
                contentValues.put("coach", user.getMySentChatRequests().get(i).getCoach());
                contentValues.put("need", user.getMySentChatRequests().get(i).getNeed());
                contentValues.put("skill", user.getMySentChatRequests().get(i).getSkill());
                contentValues.put("message", user.getMySentChatRequests().get(i).getMessage());

                db.insertWithOnConflict("Pending_request", null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            }

        }


    }
    /**
     * Creating a new local SQL database
     * Last modified by JariJ 9.5.17
     *
     */
    public void initDB()
    {
        SQLiteDatabase db=getWritableDatabase();
        //db.execSQL("PRAGMA foreign_keys=ON;");

        if(!isTableExists(db, "Conversations"))
        {
            db.execSQL("CREATE TABLE Conversations\n" +
                    "     (\n" +
                    "         conversationID text NOT NULL PRIMARY KEY,\n" +
                    "         owner text NOT NULL,\n" +
                    "         other text NOT NULL\n" +
                    "     );");
        }

        if(!isTableExists(db, "Pending_request"))
        {
            db.execSQL(" CREATE TABLE Pending_request\n" +
                    "     (\n" +
                    "         pendingRequestID text NOT NULL PRIMARY KEY,\n" +
                    "         customer TEXT,\n" +
                    "         coach TEXT,\n" +
                    "         need TEXT,\n" +
                    "         skill TEXT,\n" +
                    "         type INTEGER,\n" +
                    "         message TEXT\n" +
                    "     );");
        }

        if(!isTableExists(db, "User" ))
        {
            db.execSQL("  CREATE TABLE User\n" +
                    "     (\n" +
                    "         name text NOT NULL PRIMARY KEY,\n" +
                    "         type int NOT NULL\n" +
                    "     );");
        }
        if(!isTableExists(db, "Messages"))
        {
            db.execSQL(" CREATE TABLE Messages\n" +
                    "     (\n" +
                    "         UUID text NOT NULL PRIMARY KEY,\n" +
                    "         message text NOT NULL,\n" +
                    "         sender text NOT NULL,\n" +
                    "         resipient text NOT NULL,\n" +
                    "         creationTime int NOT NULL,\n" +
                    "         hasBeenSeen bool,\n" +
                    "         hasBeenSent bool\n" +
                    "     );");
        }
        if(!isTableExists(db, "Contact"))
        {
            db.execSQL("  CREATE TABLE Contact\n" +
                    "     (\n" +
                    "         userID text NOT NULL PRIMARY KEY,\n" +
                    "         picture BLOB, \n" +
                    "         firstName text, \n" +
                    "         lastName text \n" +
                    "     );");
        }



    }

    @Override
    public void onOpen(SQLiteDatabase db) {

    }

    /**
     * Checks if table exists in given SQLiteDataBase
     *
     * @param db        SQLiteDatabase from where to check
     * @param tableName Table Name to check for
     * @return
     */
    @SuppressLint("NewApi")
    public boolean isTableExists(SQLiteDatabase db, String tableName) {
        String colums[] = {"tbl_name"};
        String args[] = {tableName};
        Cursor cursor = db.query(true, "sqlite_master", colums, "tbl_name=? ", args, null, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }


    @Override
    public void onCreate(SQLiteDatabase db)
    {


    }

    public SQLiteDatabase getWritableDatabase(){
        return super.getWritableDatabase();
    }
    /**
     * Made by JariJ 28.3.17
     * reads conversations from given database from currentUser (username)
     *
     * @return users own conversations
     */
    public List<Conversation> readConversations(User user, SQLiteDatabase db)
    {
        List<Conversation> conversationsList=new ArrayList<>();

        /*
        List<Conversation> getConversations(String username){
        List<Conversation> list=new ArrayList<>();
        Cursor cursor=db.rawQuerty("STUFF");
        while(cursor.moveToNext()){
            Conversation conversation=new Conversation();

            list.add(conversation);
        }
        return list;
    }
         */
        if (isTableExists(db, "Conversations"))
        {
            Cursor cursor = db.rawQuery("SELECT * FROM Conversations WHERE owner = \'" +
                    user.userName + "\'", null);
            while(cursor.moveToNext())
            {
                Conversation conversation = new Conversation();
                conversation.setOwner(user.userName);
                conversation.setOther(cursor.getString(cursor.getColumnIndex("other")));
                //conversation.messageList = readMessages(user.userName, conversation.getOther(), db);
                conversation.setUser(user);
                conversation.setContact(readContact(conversation.getOther(),db));
                conversationsList.add(conversation);
            }

        }
        return conversationsList ;
    }

    /**
     * Made by JariJ 28.3.17
     * @param resipient user who receives the message
     * @param sender user who sends the message
     * @param db local SQL-db
     * @return
     */
    private List<Message> readMessages(String resipient, String sender, SQLiteDatabase db)
    {
        List<Message> messagesList = new ArrayList<>();

        if (isTableExists(db, "Message"))
        {
            Cursor cursor = db.rawQuery("SELECT * FROM Message WHERE resipient = \'" +
                    resipient + "\'" + " AND sender = \'" + sender + "\'", null);
            while(cursor.moveToNext())
            {
                messagesList.add(readMessage(cursor));
            }
            cursor = db.rawQuery("SELECT * FROM Message WHERE sender = \'" +
                    sender + "\'" + " AND sender = \'" + sender + "\'", null);
            while(cursor.moveToNext())
            {
                messagesList.add(readMessage(cursor));
            }

        }
        return messagesList ;

    }
    private Message readMessage(Cursor cursor)
    {
        Message message = new Message();
        message.resipient = cursor.getString(cursor.getColumnIndex("resipient"));
        message.sender = cursor.getString(cursor.getColumnIndex("sender"));
        message.uuid= UUID.fromString(cursor.getString(cursor.getColumnIndex("UUID")));
        message.message=cursor.getString(cursor.getColumnIndex("message"));
        message.hasBeenSeen=intToBoolean(cursor.getInt(cursor.getColumnIndex("hasBeenSeen")));
        message.hasBeenSent=intToBoolean(cursor.getInt(cursor.getColumnIndex("hasBeenSent")));
        message.creationTime=cursor.getInt(cursor.getColumnIndex("creationTime"));
        message.setJob();
        return message;
    }
    public Contact readContact(String ID, SQLiteDatabase db)
    {
        Cursor cursor=db.rawQuery("SELECT * FROM Contact WHERE userID = \'" +
                ID + "\'" , null);
        if(cursor.moveToFirst())
        {
            Contact contact = new Contact(cursor);
            return contact;
        }
        return null;
    }

    /**
     * Made by JariJ 5.4.17
     * Modified last 7.4.17
     * Reading chatRequests from given database
     * @param username
     * @param db
     * @return
     */
    private List<ChatRequest> readChatRequestsForMe(String username, SQLiteDatabase db)
    {
        List<ChatRequest> chatRequestForMeList = new ArrayList<>();

        if(isTableExists(db, "Pending_request"))
        {
            Cursor cursor = db.rawQuery("SELECT * FROM Pending_request WHERE coach = \' " +
                  username + "\'" + " OR customer = \'" + username + "\'", null );
            while(cursor.moveToNext())
            {
                ChatRequest chatRequest = new ChatRequest();

                chatRequest.customer = cursor.getString(cursor.getColumnIndex("customer"));
                chatRequest.coach = cursor.getString(cursor.getColumnIndex("coach"));
                chatRequest.need = ChatRequest.Need.valueOf(cursor.getString(cursor.getColumnIndex("need")));
                chatRequest.skill = ChatRequest.Skill.valueOf(cursor.getString(cursor.getColumnIndex("skill")));
                chatRequest.message = cursor.getString(cursor.getColumnIndex("message"));
                chatRequest.uuid = UUID.fromString(cursor.getString(cursor.getColumnIndex("pendingRequestID")));
                chatRequestForMeList.add(chatRequest);
            }
        }

        return chatRequestForMeList;
    }
    private List<ChatRequest> readChatRequestsFromMe(String username, SQLiteDatabase db)
    {
        List<ChatRequest> chatRequestFromMeList = new ArrayList<>();

        if(isTableExists(db, "Pending_request"))
        {
            Cursor cursor = db.rawQuery("SELECT * FROM Pending_request WHERE customer = \' " +
                    username + "\'" + " OR coach = \'" + username + "\'", null);
            while(cursor.moveToNext())
            {
                ChatRequest chatRequest = new ChatRequest();

                chatRequest.customer = cursor.getString(cursor.getColumnIndex("customer"));
                chatRequest.coach = cursor.getString(cursor.getColumnIndex("coach"));
                chatRequest.need = ChatRequest.Need.valueOf(cursor.getString(cursor.getColumnIndex("need")));
                chatRequest.skill = ChatRequest.Skill.valueOf(cursor.getString(cursor.getColumnIndex("skill")));
                chatRequest.message = cursor.getString(cursor.getColumnIndex("message"));
                chatRequest.uuid = UUID.fromString(cursor.getString(cursor.getColumnIndex("pendingRequestID")));

                chatRequestFromMeList.add(chatRequest);
            }
        }
        return chatRequestFromMeList;
    }

    /**
     * Made by JariJ 5.4.17
     * Reading one chatRequest and adding its data to the list on readChatRequests()
     //* @param cursor
     * @return
     */
     private ChatRequest readChatRequest(Cursor cursor)
    {
        ChatRequest chatRequest = new ChatRequest();

        chatRequest.customer = cursor.getString(cursor.getColumnIndex("customer"));
        chatRequest.coach = cursor.getString(cursor.getColumnIndex("coach"));
        chatRequest.need = ChatRequest.Need.valueOf(cursor.getString(cursor.getColumnIndex("need")));
        chatRequest.skill = ChatRequest.Skill.valueOf(cursor.getString(cursor.getColumnIndex("skill")));
        chatRequest.message = cursor.getString(cursor.getColumnIndex("message"));
        chatRequest.uuid = UUID.fromString(cursor.getString(cursor.getColumnIndex("uuid")));

        return chatRequest;
    }
    private static boolean intToBoolean(int i)
    {
        if(i==0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public User readUser (String username) {
        SQLiteDatabase db=getWritableDatabase();
        User user = null;
        if (username==null && isTableExists(db, lastUser)) {
            Cursor cursor = db.rawQuery("SELECT * FROM " + lastUser, null);
            cursor.moveToFirst();
            username = cursor.getString(cursor.getColumnIndex(lastUser));
            cursor.close();
            saveLastUser(db, username);
        }
        if (isTableExists(db, "User"))
        {
            Cursor cursor = db.rawQuery("SELECT * FROM User WHERE name = \'" + username + "\'", null);
            if (!cursor.moveToFirst())
            {
                cursor.close();
                db.close();
                return new User(username);
            }
            else
            {
                user=new User(username);
                user.conversations = readConversations(user, db);
                user.requestsForMe = readChatRequestsForMe(username, db);
                user.requestsFromMe = readChatRequestsFromMe(username, db);
                cursor.close();
            }


        } else {
            db.close();
            return new User(username);
        }
        db.close();
        return user;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        List<String> tables = new ArrayList<String>();
        Cursor cursor = db.rawQuery("SELECT * FROM sqlite_master WHERE type='table';", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String tableName = cursor.getString(1);
            if (!tableName.equals("android_metadata") &&
                    !tableName.equals("sqlite_sequence"))
                tables.add(tableName);
            cursor.moveToNext();
        }
        cursor.close();
        for (String tableName : tables) {
            DebugPrinter.Debug(tableName);
            db.execSQL("DROP TABLE IF EXISTS " + tableName);
        }
    }

    public void deleteLastUser()
    {
        SQLiteDatabase sqlDb=getWritableDatabase();
        sqlDb.execSQL("DROP TABLE IF EXISTS " + lastUser);
        sqlDb.close();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
