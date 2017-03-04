package com.example.a12136.calendar;

/**
 * Created by 56950 on 2016/12/17.
 */

import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class myDB extends SQLiteOpenHelper {
    private static final String DB_NAME = "myDB";
    private static final String TABLE_NAME = "activity";
    private static final int DB_VERSION = 1;

    public myDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS activity " +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, activity_name TEXT, " +
                "date TEXT, start_time TEXT, end_time TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean insert2DB(String activityName, String date, String startTime, String endTime) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("activity_name", activityName);
        cv.put("date", date);
        cv.put("start_time", startTime);
        cv.put("end_time", endTime);

        db.insert(TABLE_NAME, null, cv);
        db.close();
        return true;
    }

    public void update(int id, String activityName, String date, String startTime, String endTime) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("_id", id);
        cv.put("activity_name", activityName);
        cv.put("date", date);
        cv.put("start_time", startTime);
        cv.put("end_time", endTime);

        db.update(TABLE_NAME, cv, "_id = ?", new String[] { Integer.toString(id)});
    }

    public void delete(int _id) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "_id = "  + '"' + _id + '"';

        db.delete(TABLE_NAME, whereClause, null);
        db.close();
    }

    //return:
    //  result[0]:start-time
    //  result[1]:activity-name
    public List<Map<String, Object>> queryByDate(String date) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        SQLiteDatabase db = getWritableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM activity WHERE date = ?", new String[]{date});
        c.moveToFirst();
        int resultCount = c.getCount();

        for (int i = 0; i < resultCount; i++) {
            Log.d("db", c.getString(c.getColumnIndex("start_time"))+"-"+c.getString(c.getColumnIndex("activity_name")));

            Map<String, Object> listem = new HashMap<String, Object>();
            listem.put("_id", c.getInt(c.getColumnIndex("_id")));
            listem.put("time", c.getString(c.getColumnIndex("start_time"))
                    + "-" + c.getString(c.getColumnIndex("end_time")));
            listem.put("activity_name", c.getString(c.getColumnIndex("activity_name")));

            result.add(listem);

            c.moveToNext();
        }
        c.close();

        return result;
    }

    public SQLiteDatabase getDataBase() {
        SQLiteDatabase db = getWritableDatabase();
        return  db;
    }


}

