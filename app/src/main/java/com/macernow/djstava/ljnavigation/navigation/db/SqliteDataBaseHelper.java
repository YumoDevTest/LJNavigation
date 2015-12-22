package com.macernow.djstava.ljnavigation.navigation.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.macernow.djstava.ljnavigation.utils.DJLog;

/**
 * Created by djstava on 15/8/10.
 */
public class SqliteDataBaseHelper extends SQLiteOpenHelper {
    public static int VERSION = 1;

    public SqliteDataBaseHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public SqliteDataBaseHelper(Context context, String name, int version) {
        this(context, name, null, version);
    }

    public SqliteDataBaseHelper(Context context, String name) {
        this(context, name, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        DJLog.d("onCreate.");
        String sql = "create table if not exists history_address(id integer primary key autoincrement,address text,lat double,lng double)";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        DJLog.d("onUpgrade.");
    }

}