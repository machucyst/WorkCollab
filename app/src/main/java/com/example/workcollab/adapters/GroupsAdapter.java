package com.example.workcollab.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.workcollab.DatabaseFuncs;
import com.example.workcollab.R;
import com.example.workcollab.activities.MainMenuActivity;
import com.example.workcollab.fragments.JoinedGroupsSubFragment;

import java.util.List;
import java.util.Map;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.MyHandler>{

    public final List<Map> groups;
    DatabaseFuncs db = new DatabaseFuncs();
    private final JoinedGroupsSubFragment.PositionListener listener;
    Context c;


    @NonNull
    @Override
    public GroupsAdapter.MyHandler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_joined_groups,parent,false);
        return new GroupsAdapter.MyHandler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHandler holder, @SuppressLint("RecyclerView") int position) {
        holder.tv_g.setText((groups.get(position).get("GroupName")).toString());
        try{
        Glide.with(c).load(groups.get(position).get("GroupImage").toString()).into(holder.iv);
        }catch (Exception e){
        Glide.with(c).load(AppCompatResources.getDrawable(c,R.drawable.icon_test)).into(holder.iv);
        }
        holder.parent.setOnClickListener(v -> {
            listener.itemClicked(groups.get(position));
                MainMenuActivity.selected = "notGroups";
            }
        );

    }

    @Override
    public int getItemCount() {
        return groups.size();
    }
    public GroupsAdapter(List<Map> groups, Context c,JoinedGroupsSubFragment.PositionListener listener){
        this.groups = groups;
        this.c = c;
        this.listener = listener;
    }

    public static class MyHandler extends RecyclerView.ViewHolder{
        TextView tv_g, tv_lm;
        View parent;
        ImageView iv;
        public MyHandler(@NonNull View v){
            super(v);
            tv_g = v.findViewById(R.id.tv_groupName);
            tv_lm = v.findViewById(R.id.tv_latestMessage);
            parent = v.findViewById(R.id.parent);
            iv = v.findViewById(R.id.nav_image);
        }

        public TextView groups() {
            return tv_g;
        }
        public TextView deadlines(){
            return tv_lm;
        }
        public View parent(){return parent;}

    }



}
