package com.example.note;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Note 的增删改查操作
 */
public class NoteCRUB {
    final String TAG = "NoteCRUB";
    SQLiteOpenHelper dbHandler;
    SQLiteDatabase db;

    private static final String[] columns = {
            NoteDatabase.ID,
            NoteDatabase.TITLE,
            NoteDatabase.CONTENT,
            NoteDatabase.TIME,
    };

    public NoteCRUB(Context context){
        dbHandler = new NoteDatabase(context);
    }

    public void open(){
        db = dbHandler.getWritableDatabase();
    }

    public void close(){
        dbHandler.close();
    }

    /**
     * 添加 Note
     * @param note 被添加的 Note
     * @return 添加后的 Note
     */
    public Note addNote(Note note) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NoteDatabase.TITLE, note.getTitle());
        contentValues.put(NoteDatabase.CONTENT, note.getContent());
        contentValues.put(NoteDatabase.TIME, note.getTime());
        long insertId = db.insert(NoteDatabase.TABLE_NAME, null, contentValues);
        note.setId(insertId);
        return note;
    }

    /**
     * 通过 id 查找 Note
     * @param id is
     * @return 找到的 Note
     */
    public Note getNote(long id){
        Cursor cursor = db.query(NoteDatabase.TABLE_NAME, columns, NoteDatabase.ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        Note tmp = new Note(cursor.getString(1), cursor.getString(2), cursor.getString(3));
        tmp.setId(id);
        return tmp;
    }

    /**
     * 查找并列举所有 Note
     * @return 所有 Note 的列表
     */
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
                notes.add(note);
            }
        }
//        Log.d(TAG, "add over!");
        return notes;
    }

    /**
     * 更新一个 Note
     * @param note 新的 Note
     * @return 数据库更新结果
     */
    public int updateNote(Note note){
        ContentValues contentValues = new ContentValues();
        contentValues.put(NoteDatabase.TITLE, note.getTitle());
        contentValues.put(NoteDatabase.CONTENT, note.getContent());
        contentValues.put(NoteDatabase.TIME, note.getTime());
    return db.update(NoteDatabase.TABLE_NAME, contentValues,
            NoteDatabase.ID + "=?", new String[]{String.valueOf(note.getId())});
    }

    /**
     * 删除 Note
     * @param note 被删除的 Note
     */
    public void removeNote(Note note){
        db.delete(NoteDatabase.TABLE_NAME, NoteDatabase.ID + "=" + note.getId(), null);
    }

}
