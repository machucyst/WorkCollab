package com.example.workcollab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.workcollab.fragments.CreateGroupFragment;

import java.util.List;
import java.util.Map;

public class CreateGroupsUsersAdapter extends RecyclerView.Adapter<CreateGroupsUsersAdapter.MyHandler> {

    public final List<Map> groups;
    private final Context context;
    public List<Map> a;
    DatabaseFuncs db = new DatabaseFuncs();


    public CreateGroupsUsersAdapter(Context context, List<Map> groups) {
        this.context = context;
        this.groups = groups;
    }

    @NonNull
    @Override
    public CreateGroupsUsersAdapter.MyHandler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_creategroups_user, parent, false);
        return new CreateGroupsUsersAdapter.MyHandler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHandler holder, @SuppressLint("RecyclerView") int position) {
        System.out.println(groups);
        holder.username.setText(groups.get(position).get("Username").toString());
        holder.useremail.setText(groups.get(position).get("Email").toString());
        try {
            Glide.with(context).asBitmap().load(Uri.parse(groups.get(position).get("Profile").toString())).into(holder.img);
        } catch (Exception ex) {
            Glide.with(context).asBitmap().load(R.drawable.icon_test).into(holder.img);
        }
        holder.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != RecyclerView.NO_POSITION) {
                    removeItem(position);
                    CreateGroupFragment.amabatuhavefun = groups;
                }
            }
        });
        CreateGroupFragment.amabatuhavefun = groups;

    }

    public List<Map> getGroups() {
        System.out.println(a);
        return a;
    }

    public void removeItem(int position) {
        groups.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public static class MyHandler extends RecyclerView.ViewHolder {
        TextView username, useremail;
        ImageView img;
        CardView close;
        View parent;

        public MyHandler(@NonNull View v) {
            super(v);
            username = v.findViewById(R.id.tv_userName);
            useremail = v.findViewById(R.id.tv_userEmail);
            close = v.findViewById(R.id.cv_close);
            img = v.findViewById(R.id.nav_image);

        }


        public View parent() {
            return parent;
        }

    }


}
