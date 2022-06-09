package com.example.note;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;


public class New_note extends AppCompatActivity {


    EditText ed_title;
    EditText ed_content;
    FloatingActionButton floatingActionButton;
    Note note;
    int ids;
    NoteDatabase noteDatabase;
    NoteDao noteDao;
//    RatingBar urgentRatingBar;
//    RatingBar importantRatingBar;
//    float urgent = 0;
//    float important = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_note);


        ed_title = findViewById(R.id.title);
        ed_content = findViewById(R.id.content);
        floatingActionButton = findViewById(R.id.finish);
        noteDatabase = NoteDatabase.getDatabase(this);
        Intent intent = this.getIntent();
        ids = intent.getIntExtra("ids",0);
        noteDao = noteDatabase.getNoteDao();
//        urgentRatingBar = findViewById(R.id.urgentRatingBar);
//        importantRatingBar = findViewById(R.id.importantRatingBar);


        if (ids != 0){
            note = noteDatabase.getNoteDao().getNoteById(ids);
            ed_title.setText(note.getTitle());
            ed_content.setText(note.getContent());
//            urgentRatingBar.setRating(note.getUrgent());
//            importantRatingBar.setRating(note.getImportant());
        }

        //为悬浮按钮设置监听事件
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSave();
            }
        });

//        urgentRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener(){
//
//            @Override
//            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
//                urgent = v;
//            }
//        });
//        importantRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener(){
//
//            @Override
//            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
//                important = v;
//            }
//        });

    }

    //重写返回方法，如果是属于新建Note，则插入数据表并返回主页面，如果是修改Note，修改表中数据并返回主页面
    @Override
    public void onBackPressed() {
        //编辑便签的时间，格式化
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd   HH:mm");

        Date date = new Date(System.currentTimeMillis());
        String time = simpleDateFormat.format(date);
        String title = ed_title.getText().toString();
        String content = ed_content.getText().toString();
        if(ids!=0){
//            note = new Note(title, content, time, urgent, important);
            note = new Note(title, content, time);
            note.setId(ids);
            noteDao.updateNotes(note);
            Intent intent=new Intent(New_note.this,MainActivity.class);
            startActivity(intent);
            New_note.this.finish();
        }
        //若ids == 0, 新建note
        else{
//            note = new Note(title, content, time, urgent, important);
            note = new Note(title, content, time);
            noteDao.insertNotes(note);
            Intent intent=new Intent(New_note.this,MainActivity.class);
            startActivity(intent);
            New_note.this.finish();
        }

    }


    // 保存Note的方法
    private void isSave(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH：mm");
        Date date = new Date(System.currentTimeMillis());
        String time = simpleDateFormat.format(date);
        Log.d("new_note", "isSave: "+time);
        String title = ed_title.getText().toString();
        String content = ed_content.getText().toString();
        if(ids!=0){
//            note = new Note(title, content, time,urgent, important);
            note = new Note(title, content, time);
            note.setId(ids);
            noteDao.updateNotes(note);
            Intent intent=new Intent(New_note.this,MainActivity.class);
            startActivity(intent);
            New_note.this.finish();
        }
        //新建note
        else{
//            note = new Note(title, content, time, urgent, important);
            note = new Note(title, content, time);
            noteDao.insertNotes(note);
            Intent intent=new Intent(New_note.this,MainActivity.class);
            startActivity(intent);
            New_note.this.finish();
        }
    }

}