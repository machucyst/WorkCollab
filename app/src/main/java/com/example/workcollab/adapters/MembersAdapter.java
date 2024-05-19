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
import com.example.workcollab.fragments.AssignTaskFragment;

import java.util.List;
import java.util.Map;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MyHandler>{

    public final List<Map> user;
    Context context;
    DatabaseFuncs db = new DatabaseFuncs();
    private final AssignTaskFragment.PositionListener listener;
    boolean k;


    @NonNull
    @Override
    public MembersAdapter.MyHandler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_assign_task,parent,false);
        return new MembersAdapter.MyHandler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHandler holder, @SuppressLint("RecyclerView") int position) {
        k = false;
        holder.tv_u.setText((user.get(position).get("Username")).toString());
        Glide.with(context).asBitmap().load(user.get(position).get("Profile").toString()).into(holder.img);
        holder.toggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                k = !k;
                if (k){
                   AssignTaskFragment.members.add(user.get(position).get("Id").toString());
                   Glide.with(context).asBitmap().load(R.drawable.ic_close).into(holder.toggleBtn);
                }else{
                    AssignTaskFragment.members.remove(user.get(position).get("Id").toString());
                    Glide.with(context).asBitmap().load(R.drawable.ic_baseline_add_24).into(holder.toggleBtn);
                }
                System.out.println(AssignTaskFragment.members);
            }
        });

    }

    @Override
    public int getItemCount() {
        return user.size();
    }
    public MembersAdapter(List<Map> user, Context context,AssignTaskFragment.PositionListener listener){
        this.user = user;
        this.context = context;
        this.listener = listener;
    }

    public static class MyHandler extends RecyclerView.ViewHolder{
        TextView tv_u;
        ImageView toggleBtn,img;
        View parent;
        public MyHandler(@NonNull View v){
            super(v);
            toggleBtn = v.findViewById(R.id.toggleAssign);
            tv_u = v.findViewById(R.id.tv_user);
            img = v.findViewById(R.id.nav_image);
        }
    }



}
