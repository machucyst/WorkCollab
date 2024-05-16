package com.example.workcollab;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

public class NotifiationsService extends Service {
    ListenerRegistration listener;
    DatabaseFuncs df;

    /**
        For implementing this on the activity, you will need to do:
        Intent intent = new Intent(context, NotificationsService.class);
        startForegroundAService(intent);

        I havent tested any of the shits here yet so say if there are bugs ok adios
    **/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO: implement notification on receive document (the fucking addSnapshotListener you do it
        df = new DatabaseFuncs();

//        listener = df.account.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {              <---- example
//
//            }
//        });

        showNotification();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void showNotification() {
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel-notifier", "channelNotifier", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            String appName = "";
            try {
                PackageManager packageManager = getPackageManager();
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
                appName = packageManager.getApplicationLabel(applicationInfo).toString();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            Notification notification = new Notification.Builder(this, "channel-notifier")
                    .setSmallIcon(R.drawable.ic_group)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(appName)
                    .setContentText("Service is running...")
                    .setActions()
                    .build();

            startForeground(1, notification);

        }
    }
}
