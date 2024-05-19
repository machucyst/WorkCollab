package com.example.workcollab.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workcollab.DatabaseFuncs;
import com.example.workcollab.R;
import com.example.workcollab.fragments.CreateGroupFragment;
import com.example.workcollab.fragments.InvitesSubFragment;

import java.util.List;
import java.util.Map;

public class InvitesAdapter extends RecyclerView.Adapter<InvitesAdapter.MyHandler>{

    public final List<Map> groups;
    DatabaseFuncs db = new DatabaseFuncs();
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
        holder.deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDeny(groups.get(position));
                if (position != RecyclerView.NO_POSITION) {
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
    public InvitesAdapter(List<Map> groups, InvitesSubFragment.PositionListener listener){
        this.groups = groups;
        this.listener = listener;
    }

    public static class MyHandler extends RecyclerView.ViewHolder{
        TextView tv_g;
        View parent;
        CardView accept,deny;
        public MyHandler(@NonNull View v){
            super(v);
            tv_g = v.findViewById(R.id.tv_groupName);
            parent = v.findViewById(R.id.parent);
            accept = v.findViewById(R.id.btn_accept);
            deny = v.findViewById(R.id.btn_deny);
        }

    }



}
