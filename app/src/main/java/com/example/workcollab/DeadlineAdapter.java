package com.example.workcollab;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DeadlineAdapter extends RecyclerView.Adapter<DeadlineAdapter.MyHandler>{

    private final List<Map> deadlines;
    DatabaseFuncs db = new DatabaseFuncs();

    @NonNull
    @Override
    public DeadlineAdapter.MyHandler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_deadlines,parent,false);
        return new DeadlineAdapter.MyHandler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHandler holder, int position) {
        holder.groups().setText((deadlines.get(position).get("GroupName")).toString());
        db.GetDeadlines(deadlines.get(position).get("GroupId").toString(), new DatabaseFuncs.GroupListener() {
            @Override
            public void onReceive(List<Map> groups, List<Map> groupLeaders) {

            }

            @Override
            public void onReceive(List<Map> groups) {

            }

            @Override
            public void getDeadline(com.google.firebase.Timestamp timestamp) {
                SimpleDateFormat sdf = new SimpleDateFormat("M/dd hh:mm a",Locale.getDefault());
                Date d = timestamp.toDate();
                holder.deadlines().setText(sdf.format(d));
            }

        });
    }

    @Override
    public int getItemCount() {
        return deadlines.size();
    }
    public DeadlineAdapter(List<Map> deadlines){
        this.deadlines = deadlines;
    }

    public static class MyHandler extends RecyclerView.ViewHolder{
        TextView tv_g, tv_d;
        public MyHandler(@NonNull View v){
                super(v);
                tv_g = v.findViewById(R.id.tv_groupName);
                tv_d = v.findViewById(R.id.tv_deadline);
        }

        public TextView groups() {
            return tv_g;
        }
        public TextView deadlines(){
            return tv_d;
        }

    }
    public interface PositionListener{
        void onPosClick();
    }

}
