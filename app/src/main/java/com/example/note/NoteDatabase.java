package com.example.note;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Note.class}, version = 1, exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase{
    private static NoteDatabase INSTANCE;

    public static  synchronized NoteDatabase getDatabase(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    NoteDatabase.class, "note_database")
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }
    public abstract NoteDao getNoteDao();


}
