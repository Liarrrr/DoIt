package com.zhuimeng.lsy.doit.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by lsy on 16-4-29.
 */
public class AppData extends SQLiteOpenHelper {
    public static  final  String CREATE_UNFINISHED_PLAN="create table unfinishedplan("
            +"id integer primary key autoincrement,"
            +"plantext text,"
            +"plantypeimage integer,"
            +"prioiv integer,"
            +"planfrom text,"
            +"colortype integer)";

    public static  final  String CREATE_FINISHED_PLAN="create table finishedplan("
            +"id integer primary key autoincrement,"
            +"plantext text,"
            +"plantypeimage integer,"
            +"prioiv integer,"
            +"planfrom text,"
            +"colortype integer)";

    private Context mContext;
    public AppData(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_UNFINISHED_PLAN);
        db.execSQL(CREATE_FINISHED_PLAN);
        Log.e("AppData","Create succeeded");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists finishedplan");
        db.execSQL("drop table if exists unfinishedplan");
        onCreate(db);
    }
}
