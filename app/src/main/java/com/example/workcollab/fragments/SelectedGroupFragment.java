package com.example.workcollab.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.workcollab.DatabaseFuncs;
import com.example.workcollab.activities.ChatActivity;
import com.example.workcollab.databinding.FragmentSelectedGroupBinding;
import com.google.gson.Gson;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SelectedGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectedGroupFragment extends Fragment {
    Map user;
    static Map group;
    DatabaseFuncs db = new DatabaseFuncs();
    FragmentSelectedGroupBinding b;
    public SelectedGroupFragment() {
        // Required empty public constructor
    }

    public static SelectedGroupFragment newInstance(Map user, Map group) {
        SelectedGroupFragment fragment = new SelectedGroupFragment();
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString("user", gson.toJson(user));
        args.putString("group", gson.toJson(group));
        SelectedGroupFragment f = new SelectedGroupFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(group == null || (group != null && getArguments() != null)){
            System.out.println(getArguments().getString("user") + "awjgoiaehgoaeig");
            Gson gson = new Gson();
            group = gson.fromJson(getArguments().getString("group"),Map.class);
            user = gson.fromJson(getArguments().getString("user"),Map.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //TODO: Edit Group, Add Tasks!!!!!!!!!!, Leave Group.
        b = FragmentSelectedGroupBinding.inflate(inflater,container,false);
        b.tvGroupName.setText(group.get("GroupName").toString());
        System.out.println("Selected Group"+user);
        b.button4.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ChatActivity.class);
            Gson gson = new Gson();
            intent.putExtra("user", gson.toJson(user));
            intent.putExtra("group", gson.toJson(group));
            startActivity(intent);
        });

        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}