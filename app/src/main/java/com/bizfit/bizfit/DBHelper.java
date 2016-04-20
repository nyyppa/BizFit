package com.bizfit.bizfit;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;

import com.bizfit.bizfit.activities.MainActivity;

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


        }
        if(!isTableExists(db,"user_"+user.userNumber+"_trackerTable")){
            db.execSQL("CREATE TABLE "+"user_"+user.userNumber + "_trackerTable"+"(trackerId INTEGER PRIMARY KEY,startDate INTEGER," +
                    "lastReset INTEGER,dayInterval INTEGER, monthInterval INTEGER, yearInterval INTEGER,targetProgress REAL," +
                    "currentProgress REAL,defaultIncrement REAL, timeProgress INTEGER, timeProgressNeed INTEGER, name TEXT," +
                    "targetType TEXT, dailyProgress INTEGER, weekly INTEGER DEFAULT 0," +
                    "repeat INTEGER DEFAULT 0, completed INTEGER DEFAULT 0, tolerance REAL, color INTEGER)");

        }
        if(!isTableExists(db,"user_"+user.userNumber+"_DailyProgressTable")){
            db.execSQL("CREATE TABLE "+"user_"+user.userNumber+"_DailyProgressTable (id INTEGER PRIMARY KEY AUTOINCREMENT,DailyProgressID INTEGER, time INTEGER, amount REAL)");

        }
        if(!isTableExists(db,"user_"+user.userNumber+"_oldProgressTable")){
            db.execSQL("CREATE TABLE "+"user_"+user.userNumber + "_oldProgressTable (id INTEGER PRIMARY KEY AUTOINCREMENT,trackerID INTEGER, " +
                    "startDate INTEGER, endDate INTEGER,endProgress REAL,targetProgress REAL, dailyProgressID INTEGER)");

        }
        ContentValues values=new ContentValues();
        values.put("username", user.userName);
        values.put("trackerTable", user.userName+"_trackerTable");
        values.put("lastTrackerID",user.lastTrackerID);
        values.put("nextFreeDailyProgressID", user.nextFreeDailyProgressID);
        values.put("id",user.userNumber);
        db.insertWithOnConflict("user", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        for(int i=0;i<user.trackers.size();i++){
            Tracker t=user.trackers.get(i);
            values=new ContentValues();
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
            System.out.println(t.getName() + t.id);
            values.put("dailyProgress", t.daily.id);
            db.insertWithOnConflict("user_"+user.userNumber + "_trackerTable", null, values, SQLiteDatabase.CONFLICT_REPLACE);
            List<OldProgress> list=t.oldProgress;
            for(int j=0;j<list.size();j++){
                DailyProgress dailyProgress=list.get(j).getDailyProgress();
                List<DailyProgress.DaySingle> l=dailyProgress.prepForDataBase();
                for(int n=0;n<l.size();n++){
                    values=new ContentValues();
                    values.put("DailyProgressID", dailyProgress.id);
                    values.put("time",l.get(n).getTime());
                    values.put("amount",l.get(n).getAmount());
                    if(l.get(n).id!=-1){
                        values.put("id",l.get(n).id);
                    }
                    db.insertWithOnConflict(user.userName + "_DailyProgressTable", null, values, SQLiteDatabase.CONFLICT_REPLACE);
                }
                values=new ContentValues();
                values.put("trackerID",t.id);
                values.put("startDate",list.get(j).getStartDate());
                values.put("endDate",list.get(j).getEndDate());
                values.put("endProgress",list.get(j).getProgress());
                values.put("targetProgress",list.get(j).getTargetProgress());
                values.put("dailyProgressID",list.get(j).getDailyProgress().id);
                if(list.get(j).id!=-1){
                    values.put("id",list.get(j).id);
                }
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

    @SuppressLint("NewApi")
    public boolean isTableExists(SQLiteDatabase db,String tableName) {
        String colums[]={"tbl_name"};
        String where[]={"tbl_name"};
        String args[]={tableName};
        Cursor cursor = db.query(true, "sqlite_master", colums, "tbl_name=? ", args, null, null, null, null, null);
        //Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
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
        User user= null;
        ArrayList<Tracker> trackerList= null;
        if (isTableExists(db,"user")) {
            Cursor cursor=db.rawQuery("SELECT * FROM user",null);
            cursor.moveToFirst();
            user = new User(cursor.getString(cursor.getColumnIndex("username")));
            user.nextFreeDailyProgressID=cursor.getInt(cursor.getColumnIndex("nextFreeDailyProgressID"));
            user.lastTrackerID=cursor.getInt(cursor.getColumnIndex("lastTrackerID"));
            user.userNumber= cursor.getInt(cursor.getColumnIndex("id"));
            System.out.println(user.lastTrackerID);
            cursor=db.rawQuery("SELECT * FROM " + "user_"+user.userNumber+ "_trackerTable", null);
            cursor.moveToFirst();
            trackerList = new ArrayList<>(0);
            while (!cursor.isAfterLast()){
                Tracker.Helper h=new Tracker.Helper();
                h.color=cursor.getInt(cursor.getColumnIndex("color"));
                h.trackerID=cursor.getInt(cursor.getColumnIndex("trackerId"));
                h.startDate=cursor.getInt(cursor.getColumnIndex("startDate"));
                h.lastReset=cursor.getInt(cursor.getColumnIndex("lastReset"));
                h.dayInterval=cursor.getInt(cursor.getColumnIndex("dayInterval"));
                h.monthInterval=cursor.getInt(cursor.getColumnIndex("monthInterval"));
                h.yearInterval=cursor.getInt(cursor.getColumnIndex("yearInterval"));
                h.targetProgress=cursor.getFloat(cursor.getColumnIndex("targetProgress"));
                h.currentProgress=cursor.getFloat(cursor.getColumnIndex("currentProgress"));
                h.defaultIncrement=cursor.getFloat(cursor.getColumnIndex("defaultIncrement"));
                h.timeProgress=cursor.getLong(cursor.getColumnIndex("timeProgress"));
                h.timeProgressNeed=cursor.getLong(cursor.getColumnIndex("timeProgressNeed"));
                h.name=cursor.getString(cursor.getColumnIndex("name"));
                h.targetType=cursor.getString(cursor.getColumnIndex("targetType"));
                h.weekly=cursor.getInt(cursor.getColumnIndex("weekly"))==1?true:false;
                h.repeat=cursor.getInt(cursor.getColumnIndex("repeat"))==1?true:false;
                h.completed=cursor.getInt(cursor.getColumnIndex("completed"))==1?true:false;
                h.tolerance=cursor.getFloat(cursor.getColumnIndex("tolerance"));
                h.oldProgress=new ArrayList<OldProgress>(0);
                Cursor cursor1=db.rawQuery("SELECT "+h.trackerID+" FROM "+"user_"+user.userNumber+"_oldProgressTable",null);
                cursor1.moveToFirst();
                while (!cursor1.isAfterLast()){
                    long startDate=cursor1.getLong(cursor1.getColumnIndex("startDate"));
                    long endDate=cursor1.getLong(cursor1.getColumnIndex("endDate"));
                    float endProgress=cursor1.getFloat(cursor1.getColumnIndex("endProgress"));
                    float targetProgress=cursor1.getFloat(cursor1.getColumnIndex("targetProgress"));
                    int dailyProgressID=cursor1.getInt(cursor1.getColumnIndex("dailyProgressID"));
                    Cursor cursor2=db.rawQuery("SELECT "+dailyProgressID+" FROM "+"user_"+user.userNumber + "_DailyProgressTable",null);
                    cursor2.moveToFirst();
                    List<DailyProgress.DaySingle>list=new ArrayList<>(0);
                    DailyProgress d=new DailyProgress(true);
                    while (!cursor2.isAfterLast()){
                        long time=cursor2.getLong(cursor2.getColumnIndex("time"));
                        float amount=cursor2.getFloat(cursor2.getColumnIndex("amount"));
                        DailyProgress.DaySingle single=d.createDaySingle(time, amount);
                        single.id= cursor2.getInt(cursor2.getColumnIndex("id"));
                        list.add(single);
                        cursor2.moveToNext();
                    }
                    d=new DailyProgress(list,dailyProgressID);
                    OldProgress o=new OldProgress(startDate,endDate,endProgress,targetProgress,d);
                    o.id=cursor1.getInt(cursor1.getColumnIndex("id"));
                    h.oldProgress.add(o);
                    cursor1.moveToNext();
                }
                cursor1.close();
                Cursor cursor2=db.rawQuery("SELECT "+cursor.getInt(cursor.getColumnIndex("dailyProgress"))+" FROM user_"+user.userNumber + "_DailyProgressTable",null);
                List<DailyProgress.DaySingle>list=new ArrayList<>(0);
                DailyProgress d=new DailyProgress(true);
                while (!cursor2.isAfterLast()){
                    long time=cursor2.getLong(cursor2.getColumnIndex("time"));
                    float amount=cursor2.getFloat(cursor2.getColumnIndex("amount"));
                    DailyProgress.DaySingle single=d.createDaySingle(time, amount);
                    single.id= cursor2.getInt(cursor2.getColumnIndex("id"));
                    list.add(single);
                    cursor2.moveToNext();
                }
                cursor2.close();
                d=new DailyProgress(list,cursor.getInt(cursor.getColumnIndex("dailyProgress")));
                h.daily=d;
                h.parentUser=user;
                trackerList.add(new Tracker(h));
                cursor.moveToNext();
            }
            cursor.close();
        }else{
            String name;
            final AccountManager manager = AccountManager.get(MainActivity.activity);
            final Account[] accounts = manager.getAccountsByType("com.google");
            final int size = accounts.length;
            String[] names = new String[size];
            for (int i = 0; i < size; i++) {
                names[i] = accounts[i].name;

            }
            if(names.length>0){
                name=names[0];
            }else{
                name="default";
            }
            return new User(name);
        }
        user.trackers=trackerList;
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

        for(String tableName:tables) {
            db.execSQL("DROP TABLE IF EXISTS " + tableName);
        }

    }

}
