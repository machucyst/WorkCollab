package com.example.workcollab;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationManagerCompat;

public class NotificationUtils {
    public static String DEFAULT_CHANNEL = "default";
    public static int DEFAULT_GROUPER_ID = -1;
    @SuppressLint("MissingPermission")
    public static void postNotification(Context context, int id , String channel, String title, String message, int iconDrawableId, String group) {
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Notification notification = new Notification.Builder(context, channel)
                    .setSmallIcon(iconDrawableId)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setGroup(group)
                    .build();

            manager.notify(id, notification);
        }
    }

    @SuppressLint("MissingPermission")
    public static void postBigTextNotification(Context context, int id , String channel, String title, String message, int iconDrawableId, String group) {
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Notification notification = new Notification.Builder(context, channel)
                    .setSmallIcon(iconDrawableId)
                    .setStyle(new Notification.BigTextStyle()
                            .bigText(message)
                            .setBigContentTitle(title))
                    .setGroup(group)
                    .build();

            manager.notify(id, notification);
        }
    }

    @SuppressLint("MissingPermission")
    public static void postNotificationGrouper(Context context, int id , String channel, String title, String message, int iconDrawableId, String group) {
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Notification notification = new Notification.Builder(context, channel)
                    .setSmallIcon(iconDrawableId)
                    .setContentTitle(title)
                    .setGroup(group)
                    .setGroupSummary(true)
                    .setContentText(message)
                    .build();

            manager.notify(id, notification);
        }
    }

    @SuppressLint("MissingPermission")
    public static void postBigPictureNotification(Context context, int id , String channel, String title, String message, int iconDrawableId, String group, Uri image) {
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Notification notification = new Notification.Builder(context, channel)
                    .setSmallIcon(iconDrawableId)
                    .setContentTitle(title)
                    .setGroup(group)
                    .setStyle(new Notification.BigPictureStyle().bigPicture(Utils.getBitmapFromUri(context, image)))
                    .setContentText(message)
                    .build();

            manager.notify(id, notification);
        }
    }

    @SuppressLint("MissingPermission")
    public static void postBigPictureNotification(Context context, int id , String channel, String title, String message, int iconDrawableId, String group, int imageResourceId) {
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Notification notification = new Notification.Builder(context, channel)
                    .setSmallIcon(iconDrawableId)
                    .setContentTitle(title)
                    .setGroup(group)
                    .setStyle(new Notification.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(context.getResources(), imageResourceId)))
                    .setContentText(message)
                    .build();

            manager.notify(id, notification);
        }
    }

    public static void createChannel(Context context, String channelId, String channelName) {
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT));
        }
    }
}
