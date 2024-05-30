package com.example.workcollab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.io.File;

public class PublicMethods {
    public static String DecryptPassword(String password){
        char[] encpass = password.toCharArray();
        StringBuilder pass = new StringBuilder();
        for(char c: encpass){
            c-=7;
            pass.append(c);
        }
        System.out.println(pass);
        return pass.toString();
    }
    @SuppressLint("Range")
    public static String getFileName(Uri uri, Context context){
        String result = null;
        if (uri.getScheme().equals("content")){
            try (Cursor cursor = context.getContentResolver().query(uri,null,null,null,null)){
                if(cursor != null && cursor.moveToFirst()){
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    int cut = result.lastIndexOf('.');
                    if (cut != -1) {
                        result = result.substring(0, cut);
                    }
                }
            }
        }
        System.out.println(result);
        return result;
    }
    @SuppressLint("Range")
    public static String getFileType(Uri uri, Context context){
        String result = null;
        if (uri.getScheme().equals("content")){
            try (Cursor cursor = context.getContentResolver().query(uri,null,null,null,null)){
                if(cursor != null && cursor.moveToFirst()){
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    int cut = result.lastIndexOf('.');
                    if (cut != -1) {
                        result = result.substring(cut-1);
                    }
                }
            }
        }
        System.out.println(result);
        return result;
    }
    @SuppressLint("Range")
    public static String getFileExtension(Uri uri,String fileName, Context context){
        String result = null;
        if (uri.getScheme().equals("content")){
            try (Cursor cursor = context.getContentResolver().query(uri,null,null,null,null)){
                if(cursor != null && cursor.moveToFirst()){
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('.');
            int cut2 = fileName.lastIndexOf('.');

            if (cut != -1) {
                result = result.substring(cut);
                if(result.equals(".jpeg")){
                    result = ".jpg";
                }
            }
            if(cut2 != -1){
                System.out.println(fileName);

                fileName = fileName.substring(0,cut2);
            }
        }
        System.out.println(fileName+result);
        return fileName+result;
    }
    @SuppressLint("Range")
    public static File getFilePath(Uri uri, Context c){
        String result = null;
        if (uri.getScheme().equals("content")){
            try (Cursor cursor = c.getContentResolver().query(uri,null,null,null,null)){
                if(cursor != null && cursor.moveToFirst()){
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('.');

            if (cut != -1) {
                result = result.substring(cut);
            }
            System.out.println(result);
        }
        System.out.println(result+"aaaaaaaaaa");
        switch (result){
            case ".jpeg":
            case ".jpg":
            case ".png":
            case ".gif":
            case ".apng":
            case ".mp4":
                return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            case ".mp3":
                return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
            default:
        }
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

    }
    public static void menuTextChange(NavigationView nv, int id, int ItemId, String text){
        TextView a = (nv.getMenu().findItem(ItemId).getActionView().findViewById(id));
        a.setText(text);
    }
}
