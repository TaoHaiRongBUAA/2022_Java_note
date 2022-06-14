package com.example.note;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

// note 的增删改查操作
public class CRUB {
    final String TAG = "CRUB";
    SQLiteOpenHelper dbHandler;
    SQLiteDatabase db;

    private static final String[] columns = {
            NoteDatabase.ID,
            NoteDatabase.TITLE,
            NoteDatabase.CONTENT,
            NoteDatabase.TIME,
            NoteDatabase.TAG
    };

    public CRUB(Context context){
        dbHandler = new NoteDatabase(context);
    }

    public void open(){
        db = dbHandler.getWritableDatabase();
    }

    public void close(){
        dbHandler.close();
    }

    public Note addNote(Note note) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NoteDatabase.TITLE, note.getTitle());
        contentValues.put(NoteDatabase.CONTENT, note.getContent());
        contentValues.put(NoteDatabase.TIME, note.getTime());
        contentValues.put(NoteDatabase.TAG, note.getTag());
        long insertId = db.insert(NoteDatabase.TABLE_NAME, null, contentValues);
        note.setId(insertId);
        return note;
    }

    public Note getNote(long id){
        Cursor cursor = db.query(NoteDatabase.TABLE_NAME, columns, NoteDatabase.ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        Note tmp = new Note(cursor.getString(1), cursor.getString(2), cursor.getString(3),cursor.getInt(4));
        return tmp;
    }

    @SuppressLint("Range")
    public List<Note> getAllNotes(){
//        Log.d(TAG, "in getAllNotes");

        Cursor cursor = db.query(NoteDatabase.TABLE_NAME, columns,
                null, null, null, null, null);

        List<Note> notes = new ArrayList<>();

//        Log.d(TAG, "query over! with count" + cursor.getCount());

        if (cursor.getCount() > 0){
            while(cursor.moveToNext()){
                Note note = new Note();
                note.setId(cursor.getLong(cursor.getColumnIndex(NoteDatabase.ID)));
                note.setTitle(cursor.getString(cursor.getColumnIndex(NoteDatabase.TITLE)));
                note.setContent(cursor.getString(cursor.getColumnIndex(NoteDatabase.CONTENT)));
                note.setTime(cursor.getString(cursor.getColumnIndex(NoteDatabase.TIME)));
                note.setTag(cursor.getInt(cursor.getColumnIndex(NoteDatabase.TAG)));
                notes.add(note);
            }
        }

//        Log.d(TAG, "add over!");
        return notes;
    }

    public int updateNote(Note note){
        ContentValues contentValues = new ContentValues();
        contentValues.put(NoteDatabase.TITLE, note.getTitle());
        contentValues.put(NoteDatabase.CONTENT, note.getContent());
        contentValues.put(NoteDatabase.TIME, note.getTime());
        contentValues.put(NoteDatabase.TAG, note.getTag());
    return db.update(NoteDatabase.TABLE_NAME, contentValues,
            NoteDatabase.ID + "=?", new String[]{String.valueOf(note.getId())});
    }

    public void removeNote(Note note){
        db.delete(NoteDatabase.TABLE_NAME, NoteDatabase.ID + "=" + note.getId(), null);
    }

//    public LiveData<List<Note>> getAllNotesLive(){
//        return new LiveData<List<Note>>() {}
//    }
}
