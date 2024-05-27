package com.example.workcollab;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

import com.example.workcollab.activities.MainMenuActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;
import java.util.Map;

public class NotifiationsService extends Service {
    DatabaseFuncs df = new DatabaseFuncs();
    DatabaseFuncs.GroupListener listener;
    boolean newLaunch = true;


    /**
        For implementing this on the activity, you will need to do:
        Intent intent = new Intent(context, NotificationsService.class);
        startForegroundAService(intent);

        Do the mentioned above after checking if the service is already running {@link Utils#isServiceRunning(Context, Class)}

        I havent tested any of the shits here yet so say if there are bugs ok adios
    **/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO: implement notification on receive document (the fucking addSnapshotListener you do it


//        listener = df.account.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {              <---- example
//
//            }
//        });

        newLaunch = true;

        NotificationUtils.createChannel(this, "invites", "invites");

        listener = new DatabaseFuncs.GroupListener() {
            @Override
            public void onReceive(List<Map> groups, List<Map> groupLeaders) {

            }

            @Override
            public void onReceive(List<Map> groups) {
                if (!newLaunch && !MainMenuActivity.isActivityRunning()) {
                    for (Map group :
                            groups) {
                        NotificationUtils.postNotificationGrouper(NotifiationsService.this, 2, "invites", "Invite Received", "You have been invited to " + group.get("GroupName").toString(), R.drawable.ic_mail, "invites");
                        NotificationUtils.postNotification(NotifiationsService.this, (int)System.currentTimeMillis(), "invites", "Invite Received", "You have been invited to " + group.get("GroupName").toString(), R.drawable.ic_mail, "invites");
                    }
                }
                newLaunch = false;
            }

            @Override
            public void getDeadline(Timestamp timestamp) {

            }
        };

        df.InitDB(checkLoggedIn(), new DatabaseFuncs.DataListener() {
            @Override
            public void onDataFound(Map user) {
                df.getInvites(user.get("Id").toString(), listener);
            }

            @Override
            public void noDuplicateUser() {

            }
        });

        Log.e("holeymoley", "running");

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

            Intent intent = new Intent(this, MainMenuActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

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
                    .setContentText("Notifications service is running...")
                    .setActions()
                    .build();

            startForeground(1, notification);

        }
    }

    private String checkLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserLogInPreferences", Context.MODE_PRIVATE);
        return sharedPreferences.getString("user-email", "");
    }
}
