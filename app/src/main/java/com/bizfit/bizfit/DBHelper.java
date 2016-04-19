package com.bizfit.bizfit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Atte Ylivrronen on 28.3.2016.
 */
public class DBHelper extends SQLiteOpenHelper  {
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";







  //  private static final String SQL_DELETE_ENTRIES =
//            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

    /**
     *
     * @param context
     * @param name
     * @param factory
     * @param version
     */
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    public void saveUser(SQLiteDatabase db,User user) {
        if(!isTableExists(db,"user")){
            db.execSQL("CREATE TABLE user (id INTEGER PRIMARY KEY AUTOINCREMENT,username TEXT, trackerTable TEXT,lastTrackerID INTEGER,nextFreeDailyProgressID INTEGER)");
            ContentValues values=new ContentValues();
            values.put("username", user.userName);
            values.put("trackerTable", user.userName+"_trackerTable");
            values.put("lastTrackerID",user.lastTrackerID);
            values.put("nextFreeDailyProgressID",user.nextFreeDailyProgressID);
            db.insert("user", null, values);

        }
        if(!isTableExists(db,user.userName+"_trackerTable")){
            db.execSQL("CREATE TABLE " + user.userName + "_trackerTable" + " (trackerId INTEGER PRIMARY KEY,startDate INTEGER," +
                    "lastReset INTEGER,dayInterval INTEGER, monthInterval INTEGER, yearInterval INTEGER,targetProgress REAL," +
                    "currentProgress REAL,defaultIncrement REAL, timeProgress INTEGER, timeProgressNeed INTEGER, name TEXT," +
                    "targetType TEXT, dailyProgress INTEGER, weekly INTEGER DEFAULT 0," +
                    "repeat INTEGER DEFAULT 0, completed INTEGER DEFAULT 0, tolerance REAL, color INTEGER)");

        }
        if(!isTableExists(db,user.userName+"_DailyProgressTable")){
            db.execSQL("CREATE TABLE "+user.userName+"_DailyProgressTable"+" (id INTEGER PRIMARY KEY AUTOINCREMENT,DailyProgressID INTEGER, time INTEGER, amount REAL)");

        }
        if(!isTableExists(db,user.userName+"_oldProgressTable")){
            db.execSQL("CREATE TABLE "+user.userName+"_oldProgressTable"+" (id INTEGER PRIMARY KEY AUTOINCREMENT,trackerID INTEGER, " +
                    "startDate INTEGER, endDate INTEGER,endProgress REAL,targetProgress REAL)");

        }

        for(int i=0;i<user.trackers.size();i++){
            Tracker t=user.trackers.get(i);
            ContentValues values=new ContentValues();
            values.put("trackerId", user.trackers.get(i).id);
            values.put("startDate",user.trackers.get(i).startDate);
            values.put("lastReset",t.lastReset);
            values.put("dayInterval",t.dayInterval);
            values.put("monthInterval",t.monthInterval);
            values.put("yearInterval",t.yearInterval);
            values.put("targetProgress",t.targetProgress);
            values.put("currentProgress",t.currentProgress);
            values.put("defaultIncrement",t.defaultIncrement);
            values.put("timeProgress",t.timeProgress);
            values.put("timeProgressNeed",t.timeProgressNeed);
            values.put("name",t.name);
            values.put("targetType",t.targetType);
            values.put("weekly",t.weekly);
            values.put("repeat",t.repeat);
            values.put("completed",t.completed);
            values.put("tolerance",t.tolerance);
            values.put("color",t.color);
            values.put("dailyProgress", t.daily.id);
            db.insertWithOnConflict(user.userName + "_trackerTable", null, values, SQLiteDatabase.CONFLICT_REPLACE);
            List<OldProgress> list=t.oldProgress;
            for(int j=0;j<list.size();j++){
                DailyProgress dailyProgress=list.get(j).getDailyProgress();
                List<DailyProgress.DaySingle> l=dailyProgress.prepForDataBase();
                for(int n=0;n<l.size();n++){
                    values=new ContentValues();
                    values.put("DailyProgressID", dailyProgress.id);
                    values.put("time",l.get(i).getTime());
                    values.put("amount",l.get(i).getAmount());
                    db.insert(user.userName + "_DailyProgressTable",null,values);
                }
                values=new ContentValues();
                values.put("trackerID",t.id);
                values.put("startDate",list.get(j).getStartDate());
                values.put("endDate",list.get(j).getEndDate());
                values.put("endProgress",list.get(j).getProgress());
                values.put("targetProgress",list.get(j).getTargetProgress());
                db.insert(user.userName+"_oldProgressTable",null,values);
            }
            DailyProgress dailyProgress=t.daily;
            List<DailyProgress.DaySingle> l=dailyProgress.prepForDataBase();
            for(int j=0;j<l.size();j++){
                values=new ContentValues();
                values.put("DailyProgressID", t.daily.id);
                values.put("time",l.get(i).getTime());
                values.put("amount",l.get(i).getAmount());
                db.insert(user.userName + "_DailyProgressTable",null,values);
            }
        }
    }
    @Override
    public void onOpen (SQLiteDatabase db){



    }

    public boolean isTableExists(SQLiteDatabase db,String tableName) {

        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
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
    public User readUser(SQLiteDatabase db){
        //Cursor cursor=db.rawQuery("SELECT ");
        return null;
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

        for(String tableName:tables) {
            db.execSQL("DROP TABLE IF EXISTS " + tableName);
        }

    }
}
