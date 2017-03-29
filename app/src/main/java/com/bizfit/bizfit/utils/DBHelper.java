package com.bizfit.bizfit.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bizfit.bizfit.DebugPrinter;
import com.bizfit.bizfit.chat.Conversation;
import com.bizfit.bizfit.User;
import com.bizfit.bizfit.chat.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
     * Modified by JariJ 22.3.17
     * Saves user and all of it's dependands to given database
     *

     * @param user user to save
     *
     */
    public void saveUser(User user)
    {
        return;
        /*
        SQLiteDatabase db=getWritableDatabase();
        initDB();

        //User
        ContentValues userValues = new ContentValues();
        userValues.put("UUID", user.uuid.toString());
        userValues.put("name", user.userName);
        //System.out.println("user"+user.toJSON().toString());
        db.insertWithOnConflict("User", null, userValues, SQLiteDatabase.CONFLICT_REPLACE);
        saveLastUser(db, user.userName);
        saveConversation(user, db);


        db.close();
        */


       // if (!isTableExists(db, "User")) {
       //     db.execSQL("CREATE TABLE user (userName TEXT PRIMARY KEY,user TEXT)");
       // }
    }

    /**
     * Made by JariJ 28.3.17
     * Saves Message and all of it's dependands to give database
     * @param user
     * @param db
     */
    public void saveConversation(User user, SQLiteDatabase db)
    {
        ContentValues messagesValues = new ContentValues();
        for(int i=0; i < user.getConversations().size(); i++)
        {
            for(int j=0;j<user.getConversations().get(i).getMessages().size();j++)
            {
                saveMessage(user.getConversations().get(i).getMessages().get(j),db);
            }

            ContentValues conversationsValues = new ContentValues();
            conversationsValues.put("other",user.getConversations().get(i).getOther());
            conversationsValues.put("owner",user.getConversations().get(i).getOwner());
            db.insertWithOnConflict("Conversations", null, conversationsValues, SQLiteDatabase.CONFLICT_REPLACE);
        }

    }
    private void saveMessage(Message message,SQLiteDatabase db)
    {
        ContentValues contentValues=new ContentValues();
        contentValues.put("message",message.getMessage());
        contentValues.put("sender", message.getSender());
        contentValues.put("resipient", message.getSender());
        contentValues.put("creationTime", message.getCreationTime());
        contentValues.put("hasBeenSeen", message.getHasBeenSeen());

        db.insertWithOnConflict("Messages", null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

    }

    /**
     * Creating a new local SQL database
     * by JariJ 22.3.17
     *
     * (validated with SQLFiddle, added foreign keys)
     * Not using this table atm
     * CREATE TABLE Conversations
     (
         conversationsID INTEGER NOT NULL PRIMARY KEY,
         conversationID INTEGER NOT NULL
     );

     CREATE TABLE Conversation
     (
         conversationID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
         owner text NOT NULL,
         other int NOT NULL,
         messagesID INTEGER NOT NULL,
         FOREIGN KEY(messagesID) REFERENCES Messages(messagesID)
     );

     CREATE TABLE Pending_request
     (
         pendingRequestID INTEGER NOT NULL PRIMARY KEY,
         type INTEGER NOT NULL,
         message TEXT NOT NULL
     );

     CREATE TABLE User
     (
         name text NOT NULL PRIMARY KEY,
         type int NOT NULL,
         UUID text NOT NULL,
         conversationID INTEGER NOT NULL,
         pendingRequestID INTEGER NOT NULL,
         FOREIGN KEY(conversationID) REFERENCES Conversation(conversationID)
         FOREIGN KEY(pendingRequestID) REFERENCES Pending_request(pendingRequestID)
     );

     CREATE TABLE Messages
     (
         messagesID INTEGER NOT NULL PRIMARY KEY,
         message text NOT NULL,
         sender text NOT NULL,
         resipient text NOT NULL,
         creationTime int NOT NULL,
         hasBeenSeen bool,
         hasBeenSent bool
     );

     */
    public void initDB()
    {
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL("PRAGMA foreign_keys=ON;");
       /* if (!isTableExists(db, "Conversations"))
        {
            db.execSQL(
                    "CREATE TABLE Conversations\n" +
                    "                (\n" +
                    "    conversationsID INTEGER NOT NULL PRIMARY KEY,\n" +
                    "    conversationID INTEGER NOT NULL\n" +
                    "                " +
                    ");");
        }
        */
        if(!isTableExists(db, "Conversation"))
        {
            db.execSQL("CREATE TABLE Conversation\n" +
                    "     (\n" +
                    "         conversationID INTEGER NOT NULL PRIMARY KEY,\n" +
                    "         owner text NOT NULL,\n" +
                    "         other int NOT NULL,\n" +
                    "         messagesID INTEGER NOT NULL,\n" +
                    "         FOREIGN KEY(messagesID) REFERENCES Messages(messagesID)\n" +
                    "     );");
        }
        if(!isTableExists(db, "Pending_request"))
        {
            db.execSQL(" CREATE TABLE Pending_request\n" +
                    "     (\n" +
                    "         pendingRequestID INTEGER NOT NULL PRIMARY KEY,\n" +
                    "         type INTEGER NOT NULL,\n" +
                    "         message TEXT NOT NULL\n" +
                    "     );");
        }
        if(!isTableExists(db, "User" ))
        {
            db.execSQL("  CREATE TABLE User\n" +
                    "     (\n" +
                    "         userID INTEGER NOT NULL PRIMARY KEY,\n" +
                    "         name text NOT NULL,\n" +
                    "         type int NOT NULL,\n" +
                    "         UUID text NOT NULL,\n" +
                    "         conversationID INTEGER NOT NULL,\n" +
                    "         pendingRequestID INTEGER NOT NULL,\n" +
                    "         FOREIGN KEY(conversationID) REFERENCES Conversation(conversationID)\n" +
                    "         FOREIGN KEY(pendingRequestID) REFERENCES Pending_request(pendingRequestID)\n" +
                    "     );");
        }
        if(!isTableExists(db, "Messages"))
        {
            db.execSQL(" CREATE TABLE Messages\n" +
                    "     (\n" +
                    "         messagesID INTEGER NOT NULL PRIMARY KEY,\n" +
                    "         message text NOT NULL,\n" +
                    "         sender text NOT NULL,\n" +
                    "         resipient text NOT NULL,\n" +
                    "         creationTime int NOT NULL,\n" +
                    "         hasBeenSeen bool,\n" +
                    "         hasBeenSent bool\n" +
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
    private boolean isTableExists(SQLiteDatabase db, String tableName) {
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
    public List<Conversation> readConversations(String username, SQLiteDatabase db)
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
        if (isTableExists(db, "Conversation"))
        {
            Cursor cursor = db.rawQuery("SELECT * FROM Conversation WHERE owner = \'" +
                    username + "\'", null);
            while(cursor.moveToNext())
            {
                Conversation conversation = new Conversation();
                conversation.setOwner(username);
                conversation.setOther(cursor.getString(cursor.getColumnIndex("other")));
                conversation.messageList = readMessages(username, conversation.getOther(), db);


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
                Message message = new Message();
                message.resipient = cursor.getString(cursor.getColumnIndex("resipient"));
                message.sender = cursor.getString(cursor.getColumnIndex("sender"));


                messagesList.add(message);
            }
            cursor = db.rawQuery("SELECT * FROM Message WHERE sender = \'" +
                    sender + "\'" + " AND sender = \'" + sender + "\'", null);
            while(cursor.moveToNext())
            {
                Message message = new Message();


                messagesList.add(message);
            }

        }
        return messagesList ;

    }

    public User readUser (String username) {
        SQLiteDatabase db=getWritableDatabase();
        User user = null;
        if (username.equals("default") && isTableExists(db, lastUser)) {
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
                user.conversations = readConversations(username, db);
            }

            try {
                user = new User(new JSONObject(cursor.getString(cursor.getColumnIndex("User"))));
                cursor.close();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            cursor.close();
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
