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

import java.util.List;
import java.util.Map;


public class GroupMembersAdapter extends RecyclerView.Adapter<GroupMembersAdapter.MyHandler>{

    public final List<Map> members;
    private final Context context;
    DatabaseFuncs db = new DatabaseFuncs();
     GroupMembersAdapter.PositionListener listener;

    public interface PositionListener{
        void onMemberClicked();
    }

    @NonNull
    @Override
    public GroupMembersAdapter.MyHandler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_joined_groups, parent, false);
        return new GroupMembersAdapter.MyHandler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHandler holder, @SuppressLint("RecyclerView") int position) {
        holder.tv_g.setText((members.get(position).get("Username")).toString());
        Glide.with(context).load(members.get(position).get("Profile").toString()).into(holder.iv);
    }

    @Override
    public int getItemCount() {
        return members.size();
    }
    public GroupMembersAdapter(List<Map> members,  Context c,GroupMembersAdapter.PositionListener listener){
        this.members = members;
        this.listener = listener;
        this.context = c;
    }

    public static class MyHandler extends RecyclerView.ViewHolder{
        TextView tv_g, tv_lm;
        ImageView iv;
        View parent;
        public MyHandler(@NonNull View v){
            super(v);
            tv_g = v.findViewById(R.id.tv_groupName);
            tv_lm = v.findViewById(R.id.tv_latestMessage);
            iv = v.findViewById(R.id.nav_image);
            parent = v.findViewById(R.id.parent);
        }

        public TextView groups() {
            return tv_g;
        }
        public View parent(){return parent;}

    }



}
