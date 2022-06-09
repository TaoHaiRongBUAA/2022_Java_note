package com.example.note;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.note.adapters.StaggerAdapter;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    FloatingActionButton floatingActionButton;
    LayoutInflater layoutInflater;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;
    ImageView imageView;

    RecyclerView recyclerView;
    StaggerAdapter adapter;


    NoteDatabase noteDatabase;
    NoteDao noteDao;
    LiveData<List<Note>>allNoteLive;


    private Uri imageUri;
    public static final int REQUEST_CODE_TAKE = 1;
    public static final int REQUEST_CODE_CHOOSE = 0;
    private String imageBase64;


    SharedPreferences shp;
    SharedPreferences.Editor editor;


    // 这是进入这个页面时执行的程序
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycle_view);
        floatingActionButton = findViewById(R.id.add_note);
        layoutInflater = getLayoutInflater();
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);
        toolbar = findViewById(R.id.toolbar);
        noteDatabase = NoteDatabase.getDatabase(this);
        noteDao = noteDatabase.getNoteDao();
        allNoteLive = noteDao.getAllNotesLive();
        imageView = findViewById(R.id.mainBackPic);

        collapsingToolbarLayout.setTitle("Note");
        toolbar.setTitle("Note");
        setSupportActionBar(toolbar);

        shp = getPreferences(Context.MODE_PRIVATE);
        editor = shp.edit();
        setPicture(imageView);


        //实现瀑布流效果
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setReverseLayout(false);

        recyclerView.setLayoutManager(layoutManager);

        adapter = new StaggerAdapter(layoutInflater,  allNoteLive.getValue());
        recyclerView.setAdapter(adapter);


        //刷新RecyclerView
        allNoteLive.observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                adapter.setAllNotes(notes);
                adapter.notifyDataSetChanged();
            }
        });


        //RecyclerView的item的点按编辑和长按删除
        adapter.setOnRecyclerViewItemClickListener(new StaggerAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                Intent intent = new Intent(getApplicationContext(),New_note.class);
                intent.putExtra("ids",allNoteLive.getValue().get(position).getId());
                startActivity(intent);
                MainActivity.this.finish();
            }

            @Override
            public void onItemLongClick(RecyclerView parent, View view, int position) {
                showPopmenu(view, position);
            }

        });

        //点击悬浮按钮时，跳转到新建页面
        floatingActionButton.setOnClickListener(v -> {
            Intent intent=new Intent(getApplicationContext(),New_note.class);
            startActivity(intent);
            MainActivity.this.finish();
        });



    }



    //初始化背景照片
    public void setPicture(ImageView imageView){
        String pic = shp.getString("PICTURE", "");
        imageView.setImageBitmap(ImageUtil.base64ToImage(pic));
    }


    //显示删除菜单并判断是否删除
    public void showPopmenu(View view, final  int position){
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.item_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = allNoteLive.getValue().get(position).getId();
                Note note = noteDatabase.getNoteDao().getNoteById(id);
                noteDao.deleteNotes(note);
                adapter.notifyItemRangeChanged(0,allNoteLive.getValue().size());
                Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener(){
            @Override
            public void onDismiss(PopupMenu menu) {

            }
        });
        popupMenu.show();
    }




    //设置菜单事件
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.byCamera:
                takePhoto(findViewById(R.id.byCamera));
                break;
            case R.id.byAlbum:
                choosePhoto(findViewById(R.id.byAlbum));
                break;
            case R.id.defaultBackground:
                imageView.setImageBitmap(null);
                editor.putString("PICTURE", "");
                editor.apply();
                break;
            default:
                break;
        }
        return  true;
        //return false;????是用哪个true or false？
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
                    "com.example.note_6.fileprovider", imageTemp);
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
                    imageView.setImageBitmap(bitmap);
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
            imageView.setImageBitmap(bitmap);
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
            imageView.setImageBitmap(bitmap);
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