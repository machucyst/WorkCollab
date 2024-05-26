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
import com.example.workcollab.fragments.TaskListFragment;

import java.util.List;
import java.util.Map;


public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.MyHandler>{

    public final List<Map> tasks;
    DatabaseFuncs db = new DatabaseFuncs();
    private TaskListFragment.PositionListener listener;


    @NonNull
    @Override
    public TasksAdapter.MyHandler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_joined_groups,parent,false);
        return new TasksAdapter.MyHandler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHandler holder, @SuppressLint("RecyclerView") int position) {
        holder.tv_g.setText((tasks.get(position).get("TaskName")).toString());
        holder.parent.setOnClickListener(v -> {
            try {
            listener.taskItemClicked(tasks.get(position));
            }catch (Exception ex){
                System.out.println("im going to sleep");
            }
                MainMenuActivity.selected = "Tasks";

            }

        );

    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
    public TasksAdapter(List<Map> tasks, TaskListFragment.PositionListener listener){
        this.tasks = tasks;
        this.listener = listener;
    }
    public TasksAdapter(List<Map> tasks){
        this.tasks = tasks;
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
