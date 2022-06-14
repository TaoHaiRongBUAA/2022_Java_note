package com.example.note;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;


public class New_note extends AppCompatActivity {


    EditText ed_title;
    EditText ed_content;
    FloatingActionButton floatingActionButton;
    Note note;
    long ids;
    NoteDatabase noteDatabase;
    CRUB operator;
    Toolbar toolbar;
    int isNew;
//    NoteDao noteDao;
//    RatingBar urgentRatingBar;
//    RatingBar importantRatingBar;
//    float urgent = 0;
//    float important = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_note);

        operator = new CRUB(getApplicationContext());
        ed_title = findViewById(R.id.title);
        ed_content = findViewById(R.id.content);
        floatingActionButton = findViewById(R.id.finish);
        toolbar = (Toolbar) findViewById(R.id.toolbar);


        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // 设置返回键的click监听事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });

        // 获得mainActivity传入的值
        Intent intent = this.getIntent();
        ids = intent.getLongExtra("ids",-1);
        isNew = intent.getIntExtra("isNew", 0);
//        noteDao = noteDatabase.getNoteDao();

        if (ids != -1){
//            note = noteDatabase.getNoteDao().getNoteById(ids);

            operator.open();
            note = operator.getNote(ids);
            operator.close();

            ed_title.setText(note.getTitle());
            ed_content.setText(note.getContent());
        }

        //为悬浮按钮设置监听事件
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });

    }

    // 配置编辑界面页面菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // 设置右上角的点击删除事件
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_delete:
                new AlertDialog.Builder(New_note.this)
                        .setMessage("确认删除此便签吗？")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (isNew == 0){
                                    operator.open();
                                    Note tmp = new Note();
                                    tmp.setId(ids);
                                    operator.removeNote(tmp);
                                    operator.close();
                                    goBack();
                                }
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create().show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }



    //重写返回方法，如果是属于新建Note，则插入数据表并返回主页面，如果是修改Note，修改表中数据并返回主页面
    @Override
    public void onBackPressed() {
        saveNote();
    }


    // 保存Note的方法
    private void saveNote(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH：mm");
        Date date = new Date(System.currentTimeMillis());
        String time = simpleDateFormat.format(date);
        Log.d("new_note", "isSave: "+time);
        String title = ed_title.getText().toString();
        String content = ed_content.getText().toString();
        if(ids != -1){
//            note = new Note(title, content, time,urgent, important);
            note = new Note(title, content, time, 1);
            note.setId(ids);

//            noteDao.updateNotes(note);
            operator.open();
            operator.updateNote(note);
            operator.close();
        }
        //新建note
        else if (isNew == 1 && (!title.equals("") || !content.equals(""))){
//            note = new Note(title, content, time, urgent, important);
            note = new Note(title, content, time, 1);
//            noteDao.insertNotes(note);
            operator.open();
            operator.addNote(note);
            operator.close();
        }
        goBack();
    }

    private void goBack(){
        Intent intent = new Intent(New_note.this,MainActivity.class);
        intent.putExtra("res", 4);
        setResult(RESULT_OK, intent);
        startActivity(intent);
        New_note.this.finish();
    }


}