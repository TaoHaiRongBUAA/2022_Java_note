package com.example.note;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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



// 新建和编辑plan 的页面

public class New_plan extends AppCompatActivity {

    EditText ed_content;
    FloatingActionButton floatingActionButton;
    Plan plan;
    long ids;
    PlanDatabase planDatabase;
    PlanCRUB operator;
    Toolbar toolbar;
    int isNew;


    // 页面创建时，找到控件，设置对应事件
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_plan);

        operator = new PlanCRUB(getApplicationContext());
        ed_content = findViewById(R.id.new_plan_content);
        floatingActionButton = findViewById(R.id.finish);
        toolbar = (Toolbar) findViewById(R.id.new_plan_toolbar);


        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // 设置返回键的click监听事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePlan();
            }
        });

        // 获得mainActivity传入的值
        Intent intent = this.getIntent();
        ids = intent.getLongExtra("ids",-1);
        isNew = intent.getIntExtra("isNew", 0);

        if (ids != -1){
            operator.open();
            plan = operator.getPlan(ids);
            operator.close();

            ed_content.setText(plan.getContent());
        }

        //为悬浮按钮设置监听事件
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePlan();
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
                new AlertDialog.Builder(New_plan.this)
                        .setMessage("确认删除此便签吗？")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (isNew == 0){
                                    operator.open();
                                    operator.removePlan(plan);
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



    //重写返回方法，如果是属于新建Plan，则插入数据表并返回主页面，如果是修改Plan，修改表中数据并返回主页面
    @Override
    public void onBackPressed() {
        savePlan();
    }


    // 保存Plan的方法
    private void savePlan(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH：mm");
        Date date = new Date(System.currentTimeMillis());
        String time = simpleDateFormat.format(date);
        Log.d("new_plan", "isSave: "+time);
        String content = ed_content.getText().toString();
        if(ids != -1){
            plan.setContent(content);

            operator.open();
            operator.updatePlan(plan);
            operator.close();
        }
        //新建plan
        else if (isNew == 1 && !content.equals("")){
            plan = new Plan(content, 0);
            operator.open();
            operator.addPlan(plan);
            operator.close();
        }
        goBack();
    }

    private void goBack(){
        Intent intent = new Intent(New_plan.this,MainActivity.class);
        intent.putExtra("res", 4);
        setResult(RESULT_OK, intent);
        startActivity(intent);
        New_plan.this.finish();
    }


}