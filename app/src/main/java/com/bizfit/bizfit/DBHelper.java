package com.bizfit.bizfit;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Atte Ylivrronen on 28.3.2016.
 */
public class DBHelper extends SQLiteOpenHelper  {
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";







  //  private static final String SQL_DELETE_ENTRIES =
//            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    private static boolean doesDatabaseExist(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }
    public static void saveUser(Context context,User user) {
        if (!doesDatabaseExist(context, user.userName)) {

        }
    }
    ArrayList<Tracker> trackers;
    public String userName;
    //LastUser lastUser;

    @Override
    public void onCreate(SQLiteDatabase db) {
      /*  db.execSQL("CREATE TABLE "+ " (" +
                 "user_id"+ " INTEGER PRIMARY KEY," +
                "tracker_table" + TEXT_TYPE + COMMA_SEP +
                FeedEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                " )");
*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
