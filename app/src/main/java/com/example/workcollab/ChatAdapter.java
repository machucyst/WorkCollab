package com.example.workcollab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    List<Message> messages;
    Context context;
    Map currentUser;
    List<UserData> data = new ArrayList<>();
    DatabaseFuncs db;
    ReplyListener replyListener;

    public final static int TYPE_SELF = 1;
    public final static int TYPE_OTHER = 2;
    public interface ReplyListener {
        void onReplySwiped(String message, String messageId, String replyTo);
    }

    public ChatAdapter(List<Message> messages, Context context, Map user, DatabaseFuncs db, ReplyListener replyListener) {
        this.messages = messages;
        this.context = context;
        this.currentUser = user;
        this.db = db;
        this.replyListener = replyListener;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_SELF) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_message_self, parent, false));
        } else {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_message_other, parent, false));
        }
    }

    @Override
    @SuppressLint("RecyclerView")
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        holder.tv.setText(messages.get(position).getMessage());

        holder.sender.setVisibility(View.GONE);
        holder.imageWrapper.setVisibility(View.INVISIBLE);
        holder.wrapper2.setVisibility(View.GONE);

        String sender = "";

        Optional<UserData> searchUserData = data.stream().filter(user -> user.getId().equals(messages.get(position).getSenderId())).findFirst();

        if (!searchUserData.isPresent()) {
            UserData d1 = new UserData(messages.get(position).getSenderId(), "", Uri.parse(""));
            data.add(d1);
            db.getUserById(messages.get(position).getSenderId(), new DatabaseFuncs.DataListener() {
                @Override
                public void onDataFound(Map user) {
                    Message m = messages.get(position);
                    UserData d = new UserData(m.getSenderId(), user.get("Username").toString(), Uri.parse(user.get("Profile").toString()));
                    messages.get(position).setSenderUsername(d.getUsername());
                    data.set(data.indexOf(d1), d);
                    Glide.with(context).asBitmap().load(d.getUri()).into(holder.image);
                    holder.image.setImageURI(d.getUri());
                    holder.sender.setText(d.getUsername());

                    notifyDataSetChanged();
                }

                @Override
                public void noDuplicateUser() {

                }
            });
        } else {
            sender = searchUserData.get().getUsername();
            Glide.with(context).asBitmap().load(searchUserData.get().getUri()).into(holder.image);
            holder.sender.setText(sender);
        }

        if (!messages.get(position).getReplyId().isEmpty()) {
            Optional<Message> reply = messages.stream().filter(message -> messages.get(position).getReplyId().equals(message.getId())).findFirst();
            holder.sender.setVisibility(View.VISIBLE);
            if (reply.isPresent()) {
                holder.wrapper2.setVisibility(View.VISIBLE);
                holder.imageWrapper.setVisibility(View.VISIBLE);
                ConstraintSet cs = new ConstraintSet();
                cs.clone(holder.parent);

                cs.connect(holder.sender.getId(), ConstraintSet.BOTTOM, holder.wrapper2.getId(), ConstraintSet.TOP);

                holder.parent.setConstraintSet(cs);


                Message r = reply.get();
                holder.sender.setText(sender + " replied to " + r.getSenderUsername());
                Optional<UserData> m = data.stream().filter(user -> user.getId().equals(reply.get().getSenderId())).findFirst();
                if (m.isPresent()) {
                    holder.sender.setText(sender + " replied to " + m.get().getUsername());
                }
                holder.reply.setText(r.getMessage());
            } else {
                holder.wrapper2.setVisibility(View.GONE);
                ConstraintSet cs = new ConstraintSet();
                cs.clone(holder.parent);

                cs.connect(holder.sender.getId(), ConstraintSet.BOTTOM, holder.wrapper.getId(), ConstraintSet.TOP);

                holder.parent.setConstraintSet(cs);
            }

        } else {
            holder.wrapper2.setVisibility(View.GONE);
            ConstraintSet cs = new ConstraintSet();
            cs.clone(holder.parent);

            cs.connect(holder.sender.getId(), ConstraintSet.BOTTOM, holder.wrapper.getId(), ConstraintSet.TOP);

            holder.parent.setConstraintSet(cs);

            if (position == 0 || !messages.get(position).getSenderId().equals(messages.get(position - 1).getSenderId()) || !messages.get(position - 1).getReplyId().isEmpty()) {
                holder.sender.setVisibility(View.VISIBLE);
                cs = new ConstraintSet();
                cs.clone(holder.parent);

                cs.connect(holder.sender.getId(), ConstraintSet.BOTTOM, holder.wrapper.getId(), ConstraintSet.TOP);

                holder.parent.setConstraintSet(cs);

            }

            if (position == messages.size() - 1 || !messages.get(position).getSenderId().equals(messages.get(position + 1).getSenderId()) || !messages.get(position + 1).getReplyId().isEmpty()) {
                holder.imageWrapper.setVisibility(View.VISIBLE);
                cs = new ConstraintSet();
                cs.clone(holder.parent);

                cs.connect(holder.sender.getId(), ConstraintSet.BOTTOM, holder.wrapper.getId(), ConstraintSet.TOP);

                holder.parent.setConstraintSet(cs);
            }

            if (position == 0 && messages.size() == 1) {
                holder.imageWrapper.setVisibility(View.VISIBLE);
                holder.sender.setVisibility(View.VISIBLE);
                cs = new ConstraintSet();
                cs.clone(holder.parent);

                cs.connect(holder.sender.getId(), ConstraintSet.BOTTOM, holder.wrapper.getId(), ConstraintSet.TOP);

                holder.parent.setConstraintSet(cs);
            }
        }






    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv, sender, reply;
        ImageView image;
        CardView imageWrapper;
        ConstraintLayout parent;
        CardView wrapper2, wrapper;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv = itemView.findViewById(R.id.message);
            sender = itemView.findViewById(R.id.sender);
            image = itemView.findViewById(R.id.image);
            imageWrapper = itemView.findViewById(R.id.imageWrapper);
            wrapper2 = itemView.findViewById(R.id.wrapper1);
            parent = itemView.findViewById(R.id.parent);
            reply = itemView.findViewById(R.id.reply);
            wrapper = itemView.findViewById(R.id.wrapper);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (currentUser.get("Id").toString().equals(messages.get(position).getSenderId())) {
            return TYPE_SELF;
        } else {
            return TYPE_OTHER;
        }
    }

    public void addMessage(Message message, RecyclerView rv) {
        messages.add(message);
        notifyItemInserted(messages.size());
        if (this.messages.size() >= 2) {
            new Handler().postDelayed(() -> notifyItemChanged(this.messages.size() - 2), 300);
        }
        rv.scrollToPosition(getItemCount() - 1);
    }

    public void addRange(List<Message> messages, RecyclerView rv) {
        int pos = this.messages.size();
        this.messages.addAll(messages);
        notifyItemRangeInserted(pos, messages.size());
        if (this.messages.size() >= 2) {
            new Handler().postDelayed(() -> notifyItemChanged(this.messages.size() - 2), 300);
        }
        rv.scrollToPosition(getItemCount() - 1);
    }

    public void replyChat(int position) {
        Message m = messages.get(position);
        replyListener.onReplySwiped(m.getMessage(), m.getId(), m.getSenderUsername());
    }

    public static class SwipeReplyCallback extends ItemTouchHelper.SimpleCallback {
        private ChatAdapter adapter;
        public SwipeReplyCallback(ChatAdapter adapter) {
            super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
            this.adapter = adapter;
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAbsoluteAdapterPosition();
            adapter.replyChat(position);
        }

        @Override
        public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
            return 1f;
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            if (dX > 200 && !isCurrentlyActive) {
                onSwiped(viewHolder, ItemTouchHelper.RIGHT);
            } else if (dX < -200 && !isCurrentlyActive) {
                onSwiped(viewHolder, ItemTouchHelper.LEFT);
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            int viewType = viewHolder.getItemViewType();
            int swipeFlags;

            switch (viewType) {
                case TYPE_SELF:
                    swipeFlags = ItemTouchHelper.LEFT; // Allow only left swipe
                    break;
                case TYPE_OTHER:
                    swipeFlags = ItemTouchHelper.RIGHT; // Allow only right swipe
                    break;
                default:
                    swipeFlags = 0; // No swipe for other view types
                    break;
            }

            return makeMovementFlags(0, swipeFlags);
        }

        @Override
        public float getSwipeVelocityThreshold(float defaultValue) {
            return 2f;
        }

        @Override
        public float getSwipeEscapeVelocity(float defaultValue) {
            return 2f;
        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

        }
    }
}
