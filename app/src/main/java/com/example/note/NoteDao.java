package com.example.note;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDao {
    @Insert
    void insertNotes(Note note);

    @Update
    void updateNotes(Note note);

    @Delete
    Void deleteNotes(Note note);

    @Query("SELECT * FROM Note WHERE id == :id")
    Note getNoteById(int id);


    @Query("SELECT * FROM NOTE ORDER BY time")
//    void getAllNotes();
    LiveData<List<Note>>getAllNotesLive();
}

