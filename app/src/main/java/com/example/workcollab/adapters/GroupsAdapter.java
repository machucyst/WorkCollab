package com.example.workcollab.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
    public GroupsAdapter(List<Map> groups, JoinedGroupsSubFragment.PositionListener listener){
        this.groups = groups;
        this.listener = listener;
    }

    public static class MyHandler extends RecyclerView.ViewHolder{
        TextView tv_g, tv_lm;
        View parent;
        public MyHandler(@NonNull View v){
            super(v);
            tv_g = v.findViewById(R.id.tv_groupName);
            tv_lm = v.findViewById(R.id.tv_latestMessage);
            parent = v.findViewById(R.id.parent);
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