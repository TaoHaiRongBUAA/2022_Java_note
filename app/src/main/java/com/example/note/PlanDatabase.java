package com.example.note;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class PlanDatabase extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "plans";
    public static final String ID = "_id"; //'_'表示为主键
    public static final String CONTENT = "content";
    public static final String ISFINISHED = "isFinished";

    public PlanDatabase(@Nullable Context context) {
        super(context, "plans", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME
                + " ( "
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CONTENT + " TEXT NOT NULL,"
                + ISFINISHED + " INTEGER NOT NULL)"
        );
    }

    //更新版本用
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
