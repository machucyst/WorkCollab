package com.example.workcollab.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.workcollab.DatabaseFuncs;
import com.example.workcollab.R;
import com.example.workcollab.fragments.CreateGroupFragment;
import com.example.workcollab.fragments.InvitesSubFragment;

import java.util.List;
import java.util.Map;

public class InvitesAdapter extends RecyclerView.Adapter<InvitesAdapter.MyHandler>{

    public final List<Map> groups;
    DatabaseFuncs db = new DatabaseFuncs();
    Context context;
    private final InvitesSubFragment.PositionListener listener;


    @NonNull
    @Override
    public InvitesAdapter.MyHandler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_invites,parent,false);
        return new InvitesAdapter.MyHandler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHandler holder, @SuppressLint("RecyclerView") int position) {
        holder.tv_g.setText((groups.get(position).get("GroupName")).toString());
//        holder.waa.setVisibility(View.GONE);
        if (groups.get(position).get("GroupImage") != null) {
            Glide.with(context).load(groups.get(position).get("GroupImage")).into(holder.iv);
        }
        holder.deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != RecyclerView.NO_POSITION) {
                    listener.onDeny(groups.get(position));
                    removeItem(position);
                    CreateGroupFragment.amabatuhavefun = groups;
                }
            }
        });
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAccept(groups.get(position));
                if (position != RecyclerView.NO_POSITION) {
                    removeItem(position);
                    CreateGroupFragment.amabatuhavefun = groups;
                }

            }
        });
    }
    public void removeItem(int position) {
        groups.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }
    public InvitesAdapter(List<Map> groups, Context context, InvitesSubFragment.PositionListener listener){
        this.groups = groups;
        this.listener = listener;
        this.context = context;
    }

    public static class MyHandler extends RecyclerView.ViewHolder{
        TextView tv_g, waa;
        View parent;
        CardView accept,deny;
        ImageView iv;
        public MyHandler(@NonNull View v){
            super(v);
            tv_g = v.findViewById(R.id.tv_groupName);
            parent = v.findViewById(R.id.parent);
            accept = v.findViewById(R.id.btn_accept);
            deny = v.findViewById(R.id.btn_deny);
            waa = v.findViewById(R.id.tv_latestMessage);
            iv = v.findViewById(R.id.nav_image);
        }

    }

    public void addRange(List<Map> group) {
        int pos = groups.size();
        groups.addAll(group);
        notifyItemRangeInserted(pos, group.size());
    }



}
