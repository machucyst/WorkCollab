package com.example.workcollab.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.workcollab.DatabaseFuncs;
import com.example.workcollab.Message;
import com.example.workcollab.R;
import com.example.workcollab.adapters.ChatAdapter;
import com.example.workcollab.databinding.ActivityChatBinding;
import com.example.workcollab.fragments.BottomDialogViewProfileFragment;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    Map<String,Object> user,group;
    Timestamp now;
    ChatAdapter adapter;
    boolean activityIsActive = true;
    List<Message> backlog = new ArrayList<>();
    Uri attachedFile;
    String fileType,replyId = "";
    ActivityChatBinding bind;
    DatabaseFuncs db;

    public interface onProfileLongHoldPress{
        void onHoldPressed(String id);
    }
    @SuppressLint({"InternalInsetResource", "NotifyDataSetChanged", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        bind = DataBindingUtil.setContentView(this, R.layout.activity_chat);
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
                    now = Timestamp.now();
                }

                @Override
                public void onActivityResumed(@NonNull Activity activity) {
                    activityIsActive = true;
                    now = Timestamp.now();
                    backlog.sort(Comparator.comparing(Message::getTimestamp));
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

            bind.bottomPadding.setPadding(0, getTopPadding(bind,getResources()), 0, 0);
        });

        user = gson.fromJson(getIntent().getStringExtra("user"), Map.class);
        group = gson.fromJson(getIntent().getStringExtra("group"), Map.class);

        db = new DatabaseFuncs();
        db.setAllMessagesReceivedListener(String.valueOf(group.get("Id")), messages -> {
            adapter = new ChatAdapter(messages, ChatActivity.this, user, db, ((message, messageId, replyTo) -> {
                bind.replyWrapper.setVisibility(View.VISIBLE);
                ConstraintSet cs = new ConstraintSet();
                cs.clone(bind.main1);

                cs.connect(bind.recyclerView.getId(), ConstraintSet.BOTTOM, bind.replyWrapper.getId(), ConstraintSet.TOP);
                bind.main1.setConstraintSet(cs);
                bind.reply.setText(message);
                if (replyTo.equals(String.valueOf(user.get("Username")))) replyTo = "Yourself";
                bind.replyTo.setText("Replying to: " + replyTo);
                ChatActivity.this.replyId = messageId;
            }), new onProfileLongHoldPress() {
                @Override
                public void onHoldPressed(String id) {
                    BottomDialogViewProfileFragment dba = new BottomDialogViewProfileFragment(id);
                    dba.show(getSupportFragmentManager(),new BottomDialogViewProfileFragment(id).getTag());
                }
            });
            LinearLayoutManager layoutManager = new LinearLayoutManager(ChatActivity.this);
            layoutManager.setStackFromEnd(true);
            bind.recyclerView.setLayoutManager(layoutManager);
            bind.recyclerView.setAdapter(adapter);

            ItemTouchHelper.Callback callback = new ChatAdapter.SwipeReplyCallback(adapter);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
            itemTouchHelper.attachToRecyclerView(bind.recyclerView);
            FirebaseFirestore.getInstance().disableNetwork().addOnCompleteListener(task->{
                FirebaseFirestore.getInstance().enableNetwork();
            });
            db.setReceivedMessagesListener(String.valueOf(user.get("Id")), String.valueOf(group.get("Id")), new DatabaseFuncs.MessagesReceivedListener() {
                @Override
                public void onMessageReceived(List<Message> newMessages, List<Message> updatedMessages) {
                    if (!newMessages.isEmpty()){
                        Toast.makeText(ChatActivity.this,activityIsActive + ", " + newMessages + updatedMessages,Toast.LENGTH_SHORT).show();

                    }
//                    if (activityIsActive) {
                        adapter.addRange(newMessages, bind.recyclerView);
//                    } else {
//                        backlog = newMessages;
//                    }
                }

                @Override
                public Timestamp getCurrentTimestamp() {
                    return now;
                }
            });
        });

        bind.send.setOnClickListener(v -> sendMessage(bind));

        bind.cancelReply.setOnClickListener(v -> {
            bind.replyWrapper.setVisibility(View.GONE);
            ConstraintSet cs = new ConstraintSet();
            cs.clone(bind.main1);

            cs.connect(bind.recyclerView.getId(), ConstraintSet.BOTTOM, bind.wrapper.getId(), ConstraintSet.TOP);

            bind.main1.setConstraintSet(cs);

            replyId = "";
        });

        bind.attachFile.setOnClickListener(v -> adapter.notifyDataSetChanged());
    }
    private int getTopPadding(ActivityChatBinding bind, Resources res){
        Rect r = new Rect();
        bind.bottomPadding.getWindowVisibleDisplayFrame(r);
        int bottomPaddingHeight = bind.bottomPadding.getRootView().getHeight();
        int rectangleBottom = r.bottom;
        @SuppressLint({"DiscouragedApi", "InternalInsetResource"})
        int phoneSize = res.getDimensionPixelSize(res.getIdentifier("navigation_bar_height", "dimen", "android"));

        return (bottomPaddingHeight - rectangleBottom - phoneSize);
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            sendMessage(bind);
            return true;
        }
        return super.onKeyUp(keyCode, event);

    }
    private void sendMessage(ActivityChatBinding bind){
//        if (bind.chat.getText().toString().isEmpty()) return;

        if (fileType == null) {
            fileType = "";
        }

        Message message = new Message(
                "A",
                String.valueOf(bind.chat.getText()),
                String.valueOf(user.get("Id")),
                String.valueOf(user.get("Username")),
                String.valueOf(group.get("Id")),
                attachedFile,
                fileType,
                now );

        bind.chat.setText("");

        if (!replyId.isEmpty()) message.setReplyId(replyId);

        db.sendMessage(message, attachedFile, (id) -> {
            message.setId(id);
            adapter.addMessage(message, bind.recyclerView);
            bind.replyWrapper.setVisibility(View.GONE);

            ConstraintSet cs = new ConstraintSet();
            cs.clone(bind.main1);
            cs.connect(bind.recyclerView.getId(), ConstraintSet.BOTTOM, bind.wrapper.getId(), ConstraintSet.TOP);

            bind.main1.setConstraintSet(cs);

            ChatActivity.this.replyId = "";
        });
    }
//    private void animateBottomSheet() {
//        Animation slideUpAnimation = new TranslateAnimation(
//                Animation.RELATIVE_TO_SELF, 0.0f,
//                Animation.RELATIVE_TO_SELF, 0.0f,
//                Animation.RELATIVE_TO_SELF, 1.0f,
//                Animation.RELATIVE_TO_SELF, 0.5f);
//
//        slideUpAnimation.setDuration(500);
//        slideUpAnimation.setFillAfter(true);
//        bottomSheetLayout.startAnimation(slideUpAnimation);
//        bottomSheetLayout.setVisibility(View.VISIBLE);
//    }
}