package com.example.note;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.example.note.R;
import com.example.note.adapters.ItemTouchCallBack;
import com.example.note.adapters.NoteListAdapter;
import com.example.note.adapters.PlanBaseAdapter;
import com.example.note.adapters.PlanListAdapter;

import java.util.ArrayList;
import java.util.List;

public class editOrder extends AppCompatActivity {

    private static final String TAG = "edit order";
    LayoutInflater layoutInflater;
    private NoteDatabase noteDatabase;
    private PlanDatabase planDatabase;
    private RecyclerView recyclerView;
    private List<Item> allItems = new ArrayList<>();
    private NoteListAdapter noteAdapter;
    private PlanBaseAdapter planAdapter;
    private Context context = this;
    private Toolbar toolbar;

    SharedPreferences shp;
    int inNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_order);

        Intent intent = this.getIntent();
        inNote = intent.getIntExtra("inNote",-1);

        if (inNote == 0){
            NoteCRUB operator = new NoteCRUB(context);
            operator.open();
            allItems.addAll(operator.getAllNotes());
            operator.close();
        }
        else {
            PlanCRUB operator = new PlanCRUB(context);
            operator.open();
            allItems.addAll(operator.getAllPlans());
            operator.close();
        }


        recyclerView = (RecyclerView) findViewById(R.id.editOrder_recycleView);
        toolbar = (Toolbar) findViewById(R.id.editOrder_toolbar);
        layoutInflater = getLayoutInflater();



        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_check_24);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (inNote == 0){
            recyclerView.setAdapter(noteAdapter);
            noteAdapter = new NoteListAdapter(layoutInflater,  (List<Note>)(List<?>) allItems);
            recyclerView.setAdapter(noteAdapter);
        }
        else{
            recyclerView.setAdapter(planAdapter);
            planAdapter = new PlanListAdapter(layoutInflater,  (List<Plan>)(List<?>) allItems);
            recyclerView.setAdapter(planAdapter);
        }

        ItemTouchCallBack touchCallBack = new ItemTouchCallBack();
        if (inNote == 0){
            touchCallBack.setOnItemTouchListener(noteAdapter);
        }
        else {
            touchCallBack.setOnItemTouchListener(planAdapter);
        }
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
        Log.d(TAG, "finishEdit: in finishEdit");

        if (inNote == 0){
            NoteCRUB operator = new NoteCRUB(context);
            operator.open();
            for (Item item :
                    allItems) {
                operator.updateNote((Note)item);
            }
            operator.close();
        }
        else  {
            PlanCRUB operator = new PlanCRUB(context);
            operator.open();
            for (Item item :
                    allItems) {
                operator.updatePlan((Plan) item);
            }
            operator.close();
        }


        Intent intent = new Intent(editOrder.this,MainActivity.class);
        intent.putExtra("res", 4);
        setResult(RESULT_OK, intent);
        startActivity(intent);
        editOrder.this.finish();
    }
}