package com.example.note;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

import android.graphics.BitmapFactory;
import android.util.Base64;

public class ImageUtil {

    /**
     * 使用base64方法将图片转化为字符串保存在本地，用于存储背景图片
     * @param bitmap 输入图片
     * @return 字符串输出
     */
    public static String imageToBase64(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] buffer = byteArrayOutputStream.toByteArray();
        String baseStr = Base64.encodeToString(buffer, Base64.DEFAULT);
        return  baseStr;
    }

    /**
     * 使用base64方法将字符串转化为图片
     * @param bitmap64 输入字符串
     * @return 图片输出
     */
    public static Bitmap base64ToImage(String bitmap64){
        byte[] bytes = Base64.decode(bitmap64, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }

}
