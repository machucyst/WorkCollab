package com.example.workcollab.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.workcollab.DeadlineModel;
import com.example.workcollab.databinding.CardDeadlineBinding;
import com.example.workcollab.databinding.CardMainHeaderBinding;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DeadlinesAdapter extends RecyclerView.Adapter<DeadlinesAdapter.VH> {
    public List<Object> tasks;
    Context context;
    HeaderClickListener headerClickListener;
    ClickItemListener listener;
    Map user;

    public DeadlinesAdapter(List<Object> tasks, Context context, ClickItemListener listener, Map user) {
        this.tasks = tasks;
        this.context = context;
        this.listener = listener;
        this.user = user;
    }

    @NonNull
    @Override
    public DeadlinesAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) return new VH(CardMainHeaderBinding.inflate(LayoutInflater.from(context), parent, false));
        return new VH(CardDeadlineBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DeadlinesAdapter.VH holder, int position) {
        if (holder.bind instanceof CardDeadlineBinding) {
            Map task = (Map) tasks.get(position);
            if(task.get("TaskName").toString().equals("No tasks :)")){
                return;
            }
            DeadlineModel d;
            try {
                d = new DeadlineModel(task.get("ParentId").toString(), task.get("GroupName").toString(), Uri.parse(task.get("GroupImage").toString()), task);
            } catch (Exception e) {
                d = new DeadlineModel(task.get("ParentId").toString(), task.get("GroupName").toString(), Uri.parse(""), task);
            }
            // deadline card
            CardDeadlineBinding bind = (CardDeadlineBinding) holder.bind;
            bind.deadline.setText("Submit before " + new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(((Timestamp)d.getTask().get("TaskDeadline")).toDate()));
            bind.groupName.setText("Group: "+d.getGroupName());
            bind.taskName.setText(d.getTask().get("TaskName").toString());

            if (!(task.get("GroupImage") == null)) {
                Glide.with(context).load(d.getImage().toString()).into(bind.image);
            }

            bind.parent.setOnClickListener(v -> {
                listener.onItemClick(position, task);
            });
        } else if (holder.bind instanceof CardMainHeaderBinding) {
            CardMainHeaderBinding bind = (CardMainHeaderBinding) holder.bind;
            // add a string on the list so that a header will show up

            // header layout

            if (tasks.size() == 1) {
                bind.waa.setVisibility(View.GONE);
            } else {
                bind.waa.setVisibility(View.VISIBLE);
            }

            bind.toCreateGroup.setOnClickListener(v -> {
                headerClickListener.onCreateGroupClick();
            });

            bind.toInvites.setOnClickListener(v -> {
                headerClickListener.onInvitesClick();
            });

            bind.userImage.setOnClickListener(v -> {
                headerClickListener.onProfileClick();
            });

            Glide.with(context).load(user.get("Profile").toString()).into(bind.userImage);
            bind.username.setText("Welcome\nto Work Collab, " + user.get("Username"));
        }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (tasks.get(position) instanceof String) return 1;
        return 0;
    }

    public static class VH extends RecyclerView.ViewHolder {
        ViewDataBinding bind;

        public VH(@NonNull ViewDataBinding itemView) {
            super(itemView.getRoot());
            this.bind = itemView;
        }
    }

    public interface ClickItemListener {
        void onItemClick(int position, Map task);
    }

    public interface HeaderClickListener {
        void onInvitesClick();
        void onCreateGroupClick();
        void onProfileClick();
    }

    public void setHeaderClickListener(HeaderClickListener headerClickListener) {
        this.headerClickListener = headerClickListener;
    }

    public void addRange(List<Map> tasks) {
        int pos = this.tasks.size();
        this.tasks.addAll(tasks);
        notifyItemRangeInserted(pos, tasks.size());
        notifyItemChanged(0);
    }
}
