package com.example.workcollab.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.workcollab.DatabaseFuncs;
import com.example.workcollab.R;
import com.example.workcollab.fragments.ViewMemberTasks;

import java.util.List;
import java.util.Map;


public class MemberTaskAdapter extends RecyclerView.Adapter<MemberTaskAdapter.MyHandler>{

    public final List<Map> tasks;
    private final Context context;
    DatabaseFuncs db = new DatabaseFuncs();
    private final ViewMemberTasks.PositionListener listener;


    @NonNull
    @Override
    public MemberTaskAdapter.MyHandler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_member_tasks,parent,false);
        return new MemberTaskAdapter.MyHandler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHandler holder, @SuppressLint("RecyclerView") int position) {
        holder.tv_head.setText((tasks.get(position).get("Username")).toString());
        Glide.with(context).asBitmap().load(tasks.get(position).get("Profile").toString()).into(holder.iv);
        holder.download.setOnClickListener(v -> {
            db.downloadFile(tasks.get(position).get("file").toString(),tasks.get(position).get("fileCreator").toString(),context);
            }

        );

    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
    public MemberTaskAdapter(List<Map> tasks, Context context, ViewMemberTasks.PositionListener listener){
        this.tasks = tasks;
        this.listener = listener;
        this.context = context;
    }

    public static class MyHandler extends RecyclerView.ViewHolder{
        TextView tv_fn, tv_head;
        View download;
        ImageView iv;
        public MyHandler(@NonNull View v){
            super(v);
            tv_head = v.findViewById(R.id.tv_memberName);
            tv_fn = v.findViewById(R.id.tv_fileName);
            iv = v.findViewById(R.id.nav_image);
            download = v.findViewById(R.id.iv_save);
        }

    }



}
