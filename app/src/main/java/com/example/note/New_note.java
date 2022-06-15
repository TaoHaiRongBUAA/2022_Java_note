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

/**
 * 新建和编辑 Note 的页面 (Activity)
 */
public class New_note extends AppCompatActivity {

    EditText ed_title;
    EditText ed_content;
    FloatingActionButton floatingActionButton;
    Note note;
    long ids;
    NoteDatabase noteDatabase;
    NoteCRUB operator;
    Toolbar toolbar;
    int isNew;

    /**
     * 这是进入 New_note 的 Activity 时运行的初始化函数
     * 实现的功能有：<br>
     * - 设置返回键的 click 监听事件<br>
     * - 获得 mainActivity 传入的值<br>
     * - 为悬浮按钮设置监听事件<br>
     * @param savedInstanceState 保存着 Activity 的状态
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_note);

        operator = new NoteCRUB(getApplicationContext());
        ed_title = findViewById(R.id.title);
        ed_content = findViewById(R.id.content);
        floatingActionButton = findViewById(R.id.finish);
        toolbar = (Toolbar) findViewById(R.id.new_note_toolbar);


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

        if (ids != -1){
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

    /**
     * 初始化配置编辑页面的菜单
     * @param menu 输入的菜单
     * @return 菜单是否被显示
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 设置右上角的点击删除事件
     * @param item 被选择的菜单
     * @return 返回 false 允许正常菜单处理继续进行，返回 true 在这里使用。
     */
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
                                    operator.removeNote(note);
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

    /**
     * 重写返回方法，如果是属于新建Note，则插入数据表并返回主页面，
     * 如果是修改Note，修改表中数据并返回主页面
     */
    @Override
    public void onBackPressed() {
        saveNote();
    }


    /**
     * 保存Note的方法:<br>
     * 如果 Note 已存在，则更改信息及内容
     * 否则新建一个 Note
     */
    private void saveNote(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH：mm");
        Date date = new Date(System.currentTimeMillis());
        String time = simpleDateFormat.format(date);
        Log.d("new_note", "isSave: "+time);
        String title = ed_title.getText().toString();
        String content = ed_content.getText().toString();
        if(ids != -1){
            note.setTitle(title);
            note.setContent(content);
            note.setTime(time);

            operator.open();
            operator.updateNote(note);
            operator.close();
        }
        //新建note
        else if (isNew == 1 && (!title.equals("") || !content.equals(""))){
            note = new Note(title, content, time);
            operator.open();
            operator.addNote(note);
            operator.close();
        }
        goBack();
    }

    /**
     * 返回上一页
     */
    private void goBack(){
        Intent intent = new Intent(New_note.this,MainActivity.class);
        intent.putExtra("res", 4);
        setResult(RESULT_OK, intent);
        startActivity(intent);
        New_note.this.finish();
    }


}