package com.example.note;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import androidx.appcompat.widget.Toolbar;

import androidx.lifecycle.Observer;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.note.adapters.NoteAdapter;
import com.example.note.adapters.RecycleViewBaseAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final String TAG = "mainActivity";
    FloatingActionButton btn;
    LayoutInflater layoutInflater;
    private NoteDatabase dbHelper;
    private RecyclerView recyclerView;
    private List<Note> allNotes = new ArrayList<>();
    private NoteAdapter adapter;
    private Context context = this;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((R.layout.new_main));

        btn = (FloatingActionButton)findViewById(R.id.floatingActionButton);
        recyclerView = (RecyclerView) findViewById(R.id.recycle_list_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        layoutInflater = getLayoutInflater();

        // 设置toolbar代替actionbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, New_note.class);
                startActivity(intent);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter = new NoteAdapter(layoutInflater,  allNotes);
        recyclerView.setAdapter(adapter);
        refreshRecyclerView();


        adapter.setOnRecyclerViewItemClickListener(new NoteAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                Intent intent = new Intent(getApplicationContext(),New_note.class);
                intent.putExtra("ids",allNotes.get(position).getId());
                startActivity(intent);
                MainActivity.this.finish();
            }

            @Override
            public void onItemLongClick(RecyclerView parent, View view, int position) {
//                showPopmenu(view, position);
            }

        });

    }


    @SuppressLint("NotifyDataSetChanged")
    public void refreshRecyclerView(){
        Log.d(TAG, "in refreshRecyclerView! ");
        CRUB operator = new CRUB(context);
        operator.open();
        if (allNotes.size() > 0) allNotes.clear();
        Log.d(TAG, "in ref 1");
        allNotes.addAll(operator.getAllNotes());
        Log.d(TAG, "in ref 2");
        operator.close();
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        refreshRecyclerView();
    }






}
