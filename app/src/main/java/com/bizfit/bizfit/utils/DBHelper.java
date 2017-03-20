package com.bizfit.bizfit.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bizfit.bizfit.DebugPrinter;
import com.bizfit.bizfit.tracker.Tracker;
import com.bizfit.bizfit.User;

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
     * Saves user and all of it's dependands to give database
     *

     * @param user user to save
     */
    public void saveUser( User user)
    {
        SQLiteDatabase db=getWritableDatabase();
        if (!isTableExists(db, "user")) {
            db.execSQL("CREATE TABLE user (userName TEXT PRIMARY KEY,user TEXT)");
        }
        ContentValues values = new ContentValues();
        values.put("user", user.toJSON(true).toString());
        values.put("userName", user.userName);
        //System.out.println("user"+user.toJSON().toString());

        db.insertWithOnConflict("user", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        saveLastUser(db, user.userName);
        db.close();
    }

    /**
     * Creating a new local SQL database
     * by JariJ 20.3.17
     *
     * (validated with SQLFiddle, added foreign keys)
     * CREATE TABLE Conversations
     (
     conversationsID INTEGER NOT NULL PRIMARY KEY,
     conversationID INTEGER NOT NULL
     );

     CREATE TABLE Conversation
     (
     conversationID INTEGER NOT NULL PRIMARY KEY,
     owner text NOT NULL,
     other int NOT NULL,
     conversationsID INTEGER NOT NULL,
     messagesID INTEGER NOT NULL,
     FOREIGN KEY(conversationsID) REFERENCES Conversations(conversationsID)
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
     userID INTEGER NOT NULL PRIMARY KEY,
     name text NOT NULL,
     type int NOT NULL,
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
    public void initDB() {
        SQLiteDatabase db=getWritableDatabase();
        if (!isTableExists(db, "user"))
        {
            db.execSQL("CREATE TABLE user (userName TEXT PRIMARY KEY,user TEXT)");
        }
        ContentValues values = new ContentValues();
        //values.put("user", user.toJSON(true).toString());
       //values.put("userName", user.userName);
        //System.out.println("user"+user.toJSON().toString());
        db.close();
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
    public void onCreate(SQLiteDatabase db) {

    }

    public SQLiteDatabase getWritableDatabase(){
        return super.getWritableDatabase();
    }
    /**
     * reads user from given database or if it's not possible creates new one from google account
     *
     * @return Created user
     */
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
        if (isTableExists(db, "user")) {
            Cursor cursor = db.rawQuery("SELECT * FROM user WHERE userName = \'" + username + "\'", null);
            if (!cursor.moveToFirst()) {
                cursor.close();
                db.close();
                return new User(username);
            }

            try {
                user = new User(new JSONObject(cursor.getString(cursor.getColumnIndex("user"))));
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
