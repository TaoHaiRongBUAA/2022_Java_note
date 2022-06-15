package com.example.note;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;


// 空页面，供activity的重新加载使用
public class BlockActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block);

        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        BlockActivity.this.finish();
    }
}