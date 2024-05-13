package com.example.workcollab;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    List<Message> messages;
    Context context;
    Map currentUser;

    public static int TYPE_SELF = 1;
    public static int TYPE_OTHER = 2;

    public ChatAdapter(List<Message> messages, Context context, Map user) {
        this.messages = messages;
        this.context = context;
        this.currentUser = user;
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
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        holder.tv.setText(messages.get(position).getMessage());
        holder.sender.setText(messages.get(position).getSenderUsername());

        holder.sender.setVisibility(View.GONE);
        holder.imageWrapper.setVisibility(View.INVISIBLE);

        if (position > 0) {
            if (!messages.get(position).getSenderId().equals(messages.get(position-1).getSenderId())) {
                holder.sender.setVisibility(View.VISIBLE);
            }
        }

        if (position == 0) {
            holder.sender.setVisibility(View.VISIBLE);
        }

        if (position < messages.size() - 1) {
            if (!messages.get(position).getSenderId().equals(messages.get(position + 1).getSenderId())) {
                holder.imageWrapper.setVisibility(View.VISIBLE);
            }
        }
        if (position == messages.size() - 1) {
            holder.imageWrapper.setVisibility(View.VISIBLE);
        }

        if (position == 0 && messages.size() == 1) {
            holder.imageWrapper.setVisibility(View.VISIBLE);
            holder.sender.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv, sender;
        ImageView image;
        CardView imageWrapper;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv = itemView.findViewById(R.id.message);
            sender = itemView.findViewById(R.id.sender);
            image = itemView.findViewById(R.id.image);
            imageWrapper = itemView.findViewById(R.id.imageWrapper);

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
}
