package com.example.note;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// note 的数据库
public class NoteDatabase extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "notes";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String ID = "_id"; //'_'表示为主键
    public static final String TIME = "time";

    public NoteDatabase(@Nullable Context context) {
        super(context, "notes", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME
                + " ( "
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TITLE + " TEXT NOT NULL,"
                + CONTENT + " TEXT NOT NULL,"
                + TIME + " TEXT NOT NULL)"
        );
    }

    //更新版本用
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
