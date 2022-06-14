package com.example.note;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import androidx.core.view.LayoutInflaterCompat;
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
    // 右侧弹出菜单
    private PopupWindow popupWindow;
    private ViewGroup viewGroup;
    private WindowManager windowManager;
    private DisplayMetrics displayMetrics;
    private LayoutInflater c_layoutInflater;
    private RelativeLayout relativeLayout;
    private PopupWindow popupCover;
    private ViewGroup coverView;


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
        toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24);

        initPopUpView();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUpView();
            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, New_note.class);
                intent.putExtra("isNew", 1);
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
                intent.putExtra("isNew", 0);
                startActivity(intent);
                MainActivity.this.finish();
            }

            @Override
            public void onItemLongClick(RecyclerView parent, View view, int position) {
//                showPopmenu(view, position);
            }
        });

        
    }

    // 刷新界面
    @SuppressLint("NotifyDataSetChanged")
    public void refreshRecyclerView(){
//        Log.d(TAG, "in refreshRecyclerView! ");
        CRUB operator = new CRUB(context);
        operator.open();
        if (allNotes.size() > 0) allNotes.clear();
//        Log.d(TAG, "in ref 1");
        allNotes.addAll(operator.getAllNotes());
//        Log.d(TAG, "in ref 2");
        operator.close();
        adapter.notifyDataSetChanged();
    }


    // 从编辑页面返回
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        refreshRecyclerView();
    }


    // 配置主页面导航栏
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 删除全部按钮
        getMenuInflater().inflate(R.menu.main_menu, menu);
        //搜索按钮
        MenuItem search  = menu.findItem(R.id.search_note);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setQueryHint("搜索便签");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { // 无法触发
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                refreshRecyclerView();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    // 设置右上角的点击删除事件
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_clear:
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("确认删除所有便签吗？")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dbHelper = new NoteDatabase(context);
                                SQLiteDatabase db = dbHelper.getWritableDatabase();
                                db.delete("notes", null, null);
                                db.execSQL("update sqlite_sequence set seq = 0 where name = 'notes'"); //使id重新开始
                                refreshRecyclerView();
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

    // 右侧弹出菜单
    public void initPopUpView(){
        relativeLayout = (RelativeLayout) findViewById(R.id.main_layout);
        c_layoutInflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewGroup = (ViewGroup) layoutInflater.inflate(R.layout.setting_layout, null);
        coverView = (ViewGroup) layoutInflater.inflate(R.layout.cover_layout, null);
        windowManager = getWindowManager();
        displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
    }

    public void showPopUpView(){
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        popupWindow = new PopupWindow(viewGroup, (int)(width * 0.7), height, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popupCover = new PopupWindow(coverView, width, height, false);



        //在主页面加载完成后，再进行弹出
        findViewById(R.id.main_layout).post(new Runnable() {
            @Override
            public void run() {
                popupCover.showAtLocation(relativeLayout, Gravity.NO_GRAVITY, 0, 0);
                popupWindow.showAtLocation(relativeLayout, Gravity.NO_GRAVITY, 0, 0);

                coverView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        popupWindow.dismiss();
                        return true;
                    }
                });

                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        popupCover.dismiss();
                    }
                });

            }
        });
    }


}
