package com.example.workcollab;

import android.app.Activity;
import android.app.Application;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.workcollab.databinding.ActivityChatBinding;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    Map user;
    Map group;
    Timestamp now;
    ChatAdapter adapter;
    boolean activityIsActive = true;
    List<Message> backlog = new ArrayList<>();
    Uri attachedFile;
    String fileType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityChatBinding bind = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

                }

                @Override
                public void onActivityStarted(@NonNull Activity activity) {

                }

                @Override
                public void onActivityResumed(@NonNull Activity activity) {
                    activityIsActive = true;
                    Collections.sort(backlog, Comparator.comparing(Message::getTimestamp));
                    if (adapter != null) adapter.addRange(backlog, bind.recyclerView);
                    backlog = new ArrayList<>();
                }

                @Override
                public void onActivityPaused(@NonNull Activity activity) {
                    activityIsActive = false;
                }

                @Override
                public void onActivityStopped(@NonNull Activity activity) {

                }

                @Override
                public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

                }

                @Override
                public void onActivityDestroyed(@NonNull Activity activity) {

                }
            });
        }

        Gson gson = new Gson();

        bind.bottomPadding.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            now = Timestamp.now();
            Rect r = new Rect();
            bind.bottomPadding.getWindowVisibleDisplayFrame(r);
            bind.bottomPadding.setPadding(0, bind.bottomPadding.getRootView().getHeight() - r.bottom - getResources().getDimensionPixelSize(getResources().getIdentifier("navigation_bar_height", "dimen", "android")), 0, 0);
        });

        user = gson.fromJson(getIntent().getStringExtra("user"), Map.class);
        group = gson.fromJson(getIntent().getStringExtra("group"), Map.class);

        DatabaseFuncs db = new DatabaseFuncs();
        db.setAllMessagesReceivedListener(group.get("Id").toString(), messages -> {
            adapter = new ChatAdapter(messages, ChatActivity.this, user);
            LinearLayoutManager layoutManager = new LinearLayoutManager(ChatActivity.this);
            layoutManager.setStackFromEnd(true);
            bind.recyclerView.setLayoutManager(layoutManager);
            bind.recyclerView.setAdapter(adapter);

            Toast.makeText(this, "awdjaljdalwkdjlakwdj", Toast.LENGTH_SHORT).show();
            db.setReceivedMessagesListener(user.get("Id").toString(), group.get("Id").toString(), new DatabaseFuncs.MessagesReceivedListener() {
                @Override
                public void onMessageReceived(List<Message> newMessages, List<Message> updatedMessages) {
                    if (activityIsActive) {
                        adapter.addRange(newMessages, bind.recyclerView);
                    } else {
                        backlog = newMessages;
                    }
                }

                @Override
                public Timestamp getCurrentTimestamp() {
                    return now;
                }
            });
        });

        bind.send.setOnClickListener(v -> {
            if (bind.chat.getText().toString().isEmpty()) return;

            if (fileType == null) {
                fileType = "";
            }

            Message message = new Message("A", bind.chat.getText().toString(), user.get("Id").toString(), user.get("Username").toString(), group.get("Id").toString(), attachedFile, fileType, now);

            bind.chat.setText("");

            db.sendMessage(message, attachedFile, () -> {
                adapter.addMessage(message, bind.recyclerView);
            });
        });
    }
}