package com.example.workcollab;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.InputStream;

public class Utils {
    public static Bitmap getBitmapFromUri(Context context, Uri uri) {
        try {
            ContentResolver contentResolver = context.getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (inputStream != null) {
                inputStream.close();
            }
            return bitmap;
        } catch (Exception e) {}

        return null;
    }
    /**
        Parameter is ServiceClassAndStuff.class
        check if service is already running for this
        check this on the start of the app
        if (!isServiceRunning) do the intent shit from {@link NotifiationsService}
    **/
    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
