package com.example.note;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Note {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String content;
    private String time;
//    private float urgent;
//    private float important;


//    public Note(String title, String content, String time, float urgent, float important){
    public Note(String title, String content, String time){
        this.title = title;
        this.content = content;
        this.time = time;
//        this.urgent = urgent;
//        this.important = important;
    }

//    public float getUrgent() {
//        return urgent;
//    }
//
//    public void setUrgent(int urgent) {
//        this.urgent = urgent;
//    }
//
//    public float getImportant() {
//        return important;
//    }
//
//    public void setImportant(int important) {
//        this.important = important;
//    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
