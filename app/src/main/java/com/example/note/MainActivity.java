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
import androidx.core.view.LayoutInflaterCompat;
import androidx.lifecycle.Observer;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.note.adapters.NoteAdapter;
import com.example.note.adapters.RecycleViewBaseAdapter;
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
    ImageView iv_setting_pic;
    TextView tv_setting_pic;

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
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        layoutInflater = getLayoutInflater();

        // 设置toolbar代替actionbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24);

        shp = getPreferences(Context.MODE_PRIVATE);
        editor = shp.edit();
        setPicture(recyclerView);

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

    public void takePhoto(View view){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED){
            dotake();
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }
    }

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

    private void handleImageBeforeApi19(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }



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

    private void displayImage(String imagePath){
        if(imagePath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            recyclerView.setBackground(new BitmapDrawable(bitmap));
            imageBase64 = ImageUtil.imageToBase64(bitmap);
            editor.putString("PICTURE", imageBase64);
            editor.apply();
        }
    }


    public void choosePhoto(View view){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
            openAlbum();
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_CHOOSE);
    }

}
