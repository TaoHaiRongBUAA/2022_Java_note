package com.example.note;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.example.note.R;
import com.example.note.adapters.ItemTouchCallBack;
import com.example.note.adapters.NoteListAdapter;

import java.util.ArrayList;
import java.util.List;

public class editOrder extends AppCompatActivity {

    LayoutInflater layoutInflater;
    private NoteDatabase dbHelper;
    private RecyclerView recyclerView;
    private List<Note> allNotes = new ArrayList<>();
    private NoteListAdapter adapter;
    private Context context = this;
    private Toolbar toolbar;
    CRUB operator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_order);

        operator = new CRUB(context);
        operator.open();
        allNotes.addAll(operator.getAllNotes());
        operator.close();

        recyclerView = (RecyclerView) findViewById(R.id.editOrder_recycleView);
        toolbar = (Toolbar) findViewById(R.id.editOrder_toolbar);
        layoutInflater = getLayoutInflater();



        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_check_24);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter = new NoteListAdapter(layoutInflater,  allNotes);
        recyclerView.setAdapter(adapter);

        ItemTouchCallBack touchCallBack = new ItemTouchCallBack();
        touchCallBack.setOnItemTouchListener(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchCallBack);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishEdit();
            }
        });


    }

    private void finishEdit() {
        for (Note note :
                allNotes) {
            operator.open();
            operator.updateNote(note);
            operator.close();
        }

        Intent intent = new Intent(editOrder.this,MainActivity.class);
        intent.putExtra("res", 4);
        setResult(RESULT_OK, intent);
        startActivity(intent);
        editOrder.this.finish();
    }
}