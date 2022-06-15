package com.example.note;


import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.note.adapters.ItemTouchCallBack;
import com.example.note.adapters.NoteListAdapter;
import com.example.note.adapters.PlanBaseAdapter;
import com.example.note.adapters.PlanListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final String TAG = "mainActivity";
    FloatingActionButton btn;
    LayoutInflater layoutInflater;
    int inNote;

    private NoteDatabase dbHelper;
    private PlanDatabase planDatabase;
    private RecyclerView recyclerView;
    private List<Item> allItems = new ArrayList<>();
    private NoteListAdapter noteAdapter;
    private PlanBaseAdapter planAdapter;
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
    ImageView iv_setting_pic;
    TextView tv_setting_pic;
    ImageView iv_setting_mode;
    TextView tv_setting_mode;

    //图片
    private Uri imageUri;
    private String imageBase64;

    // 小型数据存储
    SharedPreferences shp;
    SharedPreferences.Editor editor;

    public static final int REQUEST_CODE_TAKE = 1;
    public static final int REQUEST_CODE_CHOOSE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((R.layout.new_main));

        btn = (FloatingActionButton)findViewById(R.id.floatingActionButton);
        recyclerView = (RecyclerView) findViewById(R.id.recycle_list_view);
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);

        layoutInflater = getLayoutInflater();

        // 获取保存在磁盘的数据
        shp = getPreferences(Context.MODE_PRIVATE);
        editor = shp.edit();
        setPicture(recyclerView);
        inNote = shp.getInt("inNote", 0);
//        inNote = 1;

        Log.d(TAG, "inNote is " + inNote + "\n");

        // 设置toolbar代替actionbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24);

//        Log.d("setting", "before setting");
        if (inNote == 0){
            getSupportActionBar().setTitle("Note");
            Log.d("setting", "setting in Note = 0");
        }else if(inNote == 1){
            getSupportActionBar().setTitle("Plan");
            Log.d("setting", "setting in Note = 1");
        }

        initPopUpView();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUpView();
            }
        });

        //浮动button的事件
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inNote == 0){
                    Intent intent = new Intent(MainActivity.this, New_note.class);
                    intent.putExtra("isNew", 1);
                    startActivity(intent);
                }
                else if (inNote == 1){
                    Intent intent = new Intent(MainActivity.this, New_plan.class);
                    intent.putExtra("isNew", 1);
                    startActivity(intent);
                }
            }
        });

        // 设置recyclerView的adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (inNote == 0){
            Log.d(TAG, "onCreate: allItems = " + allItems + "\n" + "list = " + (List<Note>) (List<?>)allItems);
            noteAdapter = new NoteListAdapter(layoutInflater,  (List<Note>) (List<?>)allItems);
            recyclerView.setAdapter(noteAdapter);
        }else{
            Log.d(TAG, "onCreate: allItems = " + allItems + "\n" + "list = " + (List<Plan>) (List<?>)allItems);
            planAdapter = new PlanListAdapter(layoutInflater,  (List<Plan>) (List<?>)allItems);
            recyclerView.setAdapter(planAdapter);
        }
        refreshRecyclerView();

        // 对不同的adapter进行设置
        if (inNote == 0){
            noteAdapter.setOnRecyclerViewItemClickListener(new NoteListAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(RecyclerView parent, View view, int position) {
                    Intent intent = new Intent(getApplicationContext(),New_note.class);
                    intent.putExtra("ids",allItems.get(position).getId());
                    intent.putExtra("isNew", 0);
                    startActivity(intent);
                    MainActivity.this.finish();
                }

                @Override
                public void onItemLongClick(RecyclerView parent, View view, int position) {
                    Intent intent = new Intent(getApplicationContext(),editOrder.class);
                    intent.putExtra("inNote", 0);
                    startActivity(intent);
                    MainActivity.this.finish();
                }
            });
        }
        else if(inNote == 1){
            planAdapter.setOnRecyclerViewItemClickListener(new PlanListAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(RecyclerView parent, View view, int position) {
                    Intent intent = new Intent(getApplicationContext(),New_plan.class);
                    intent.putExtra("ids",allItems.get(position).getId());
                    intent.putExtra("isNew", 0);
                    startActivity(intent);
                    MainActivity.this.finish();
                }

                @Override
                public void onItemLongClick(RecyclerView parent, View view, int position) {
                    Intent intent = new Intent(getApplicationContext(),editOrder.class);
                    intent.putExtra("inNote", 1);
                    startActivity(intent);
                    MainActivity.this.finish();
                }
            });
        }


    }

    // 刷新recyclerView
    @SuppressLint("NotifyDataSetChanged")
    public void refreshRecyclerView(){ // type == 1 表示Note， type == 2 表示plan
//        Log.d(TAG, "in refreshRecyclerView! ");
        if (allItems.size() > 0) allItems.clear();
        if (inNote == 0){
            NoteCRUB operator = new NoteCRUB(context);
            operator.open();
            allItems.addAll(operator.getAllNotes());
            operator.close();
            noteAdapter.notifyDataSetChanged();
        }else if (inNote == 1){
            PlanCRUB operator = new PlanCRUB(context);
            operator.open();
            allItems.addAll(operator.getAllPlans());
            operator.close();
            planAdapter.notifyDataSetChanged();
        }
        Log.d(TAG, "refresh: allItem = " + allItems.toString());
    }

    // 保存用户正在访问的note - plan
    public void setInNote(int Mode){
        Log.d("changing", "in setInNote");

        editor.putInt("inNote", Mode);
        Boolean flag = editor.commit();
        Log.d("setInNote", "flag is " + flag);

        Intent intent = new Intent(getApplicationContext(),BlockActivity.class);
        startActivity(intent);
        MainActivity.this.finish();
    }

    // 切换为显示plan
    public void changeMode(){
        Log.d("changing", "in showPlan");
        setInNote(inNote == 0 ? 1 : 0 );
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
                if (inNote == 0){
                    noteAdapter.getFilter().filter(newText);
                }else{
                    planAdapter.getFilter().filter(newText);
                }

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
                        .setMessage("确认删除所有" + (inNote == 0 ? "便签" : "代办") +"吗？")
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

                // 更换背景的pop菜单
                iv_setting_pic = (ImageView) viewGroup.findViewById(R.id.setting_pic_img);
                tv_setting_pic = (TextView) viewGroup.findViewById(R.id.setting_pic_txt);
                iv_setting_pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showPopmenu(view);
                    }
                });

                tv_setting_pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showPopmenu(view);
                    }
                });

                // 切换note - plan 的菜单
                iv_setting_mode = (ImageView) viewGroup.findViewById(R.id.settting_mode_img);
                tv_setting_mode = (TextView) viewGroup.findViewById(R.id.setting_mode_txt);

                if (inNote == 0){
                    iv_setting_mode.setImageResource(R.drawable.ic_baseline_plan_bulleted_24);
                    tv_setting_mode.setText("代办");
                }
                else {
                    iv_setting_mode.setImageResource(R.drawable.ic_baseline_note_list_24);
                    tv_setting_mode.setText("便签");
                }

                iv_setting_mode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        changeMode();
                    }
                });

                tv_setting_mode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        changeMode();
                    }
                });
                
                // 点击空处，回弹操作
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

    // 显示弹窗菜单
    public void showPopmenu(View view){
//        Log.d("popmenu", "show popmenu is called!");

        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.setting_background_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

//                Log.d("popmenu", "item clicked!");

                switch (item.getItemId()){
                    case R.id.byCamera:
                        takePhoto(findViewById(R.id.byCamera));
                        break;
                    case R.id.byAlbum:
                        choosePhoto(findViewById(R.id.byAlbum));
                        break;
                    case R.id.defaultBackground:
                        recyclerView.setBackground(new BitmapDrawable(ImageUtil.base64ToImage("")));
                        editor.putString("PICTURE", "");
                        editor.apply();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }



    //初始化背景照片
    public void setPicture(View view){
        String pic = shp.getString("PICTURE", "");
        view.setBackground(new BitmapDrawable(ImageUtil.base64ToImage(pic)));
    }

    //更换背景图片的操作，涉及到调用手机系统的接口等，勿动！！！！！

    // 发起请求
    public void takePhoto(View view){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED){
            dotake();
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }
    }

    // 请求权限的结果处理
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED){
                dotake();
            }else{
                Toast.makeText(this, "未获得摄像头权限", Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == 0){
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openAlbum();
            }else{
                Toast.makeText(this, "未获得访问相册的权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 获取权限后，执行访问
    private void dotake() {
        File imageTemp = new File(getExternalCacheDir(), "imageOut.jpeg");
        if(imageTemp.exists()){
            imageTemp.delete();
        }
        try {
            imageTemp.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(Build.VERSION.SDK_INT > 24){
            imageUri = FileProvider.getUriForFile(this,
                    "com.example.note.fileprovider", imageTemp);
        }else{
            imageUri = Uri.fromFile(imageTemp);
        }
        Intent intent = new Intent();
        intent.setAction("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_CODE_TAKE);

    }

    // 从其他页面返回的回调函数
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_TAKE){
            if(resultCode == RESULT_OK){
                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    recyclerView.setBackground(new BitmapDrawable(bitmap));
                    imageBase64 = ImageUtil.imageToBase64(bitmap);
                    editor.putString("PICTURE", imageBase64);
                    editor.apply();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }else if(requestCode == REQUEST_CODE_CHOOSE){

            if(Build.VERSION.SDK_INT < 19){
                handleImageBeforeApi19(data);
            }else{
                handleImageOnApi19(data);
            }
        }
        refreshRecyclerView();
    }

    // 获得图片后的处理方法
    private void handleImageBeforeApi19(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }



    // api19 上的获得图片后的处理方法
    @TargetApi(19)
    private void handleImageOnApi19(Intent data) {
        String imagePath = null;
        Uri uri =data.getData();
        if(DocumentsContract.isDocumentUri(this, uri)){
            String documentId = DocumentsContract.getDocumentId(uri);

            if(TextUtils.equals(uri.getAuthority(), "com.android.providers.media.documents")){
                String id = documentId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" +  id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);

            }else if(TextUtils.equals(uri.getAuthority(), "com.android.providers.downloads.documents")){
                if(documentId != null && documentId.startsWith("msf:")){
                    resolveMSFContent(uri, documentId);
                    return;
                }
                Uri contentUri =  ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId));
                imagePath = getImagePath(contentUri, null);

            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            imagePath = getImagePath(uri, null);
        }else if("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }

        displayImage(imagePath);

    }

    // 文件字节流转成bitmap
    private void resolveMSFContent(Uri uri, String documentId) {
        File file = new File(getCacheDir(), "temp_file"+getContentResolver().getType(uri).split("/")[1]);

        try {
            InputStream inputStream =  getContentResolver().openInputStream(uri);

            OutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[4*1024];
            int read;
            while((read = inputStream.read(buffer) ) != -1){
                outputStream.write(buffer, 0 ,read);
            }

            outputStream.flush();

            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            recyclerView.setBackground(new BitmapDrawable(bitmap));
            imageBase64 =  ImageUtil.imageToBase64(bitmap);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    // 获得图片路径
    private String getImagePath(Uri uri, String selection){
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                int num = cursor.getColumnIndex(MediaStore.Images.Media.DATA);//中间量，防报错
                path = cursor.getString(num);

            }
            cursor.close();
        }
        return  path;
    }


    // 展示选中的图片
    private void displayImage(String imagePath){
        if(imagePath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            recyclerView.setBackground(new BitmapDrawable(bitmap));
            imageBase64 = ImageUtil.imageToBase64(bitmap);
            editor.putString("PICTURE", imageBase64);
            editor.apply();
        }
    }

    // 从相册中选择图片
    public void choosePhoto(View view){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
            openAlbum();
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    // 打开相册
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_CHOOSE);
    }

}
