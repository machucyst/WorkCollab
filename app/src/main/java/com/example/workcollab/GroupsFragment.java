package com.example.workcollab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.workcollab.databinding.FragmentGroupsBinding;
import com.google.firebase.Timestamp;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GroupsFragment extends Fragment {

    Map user;
    DatabaseFuncs db = new DatabaseFuncs();
    FragmentGroupsBinding b;

    public GroupsFragment() {
        // Required empty public constructor
    }
    public interface PositionListener{
        default void itemClicked(Map group){}
    }

    public static GroupsFragment newInstance(Map user) {
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString("user", gson.toJson(user));
        GroupsFragment f = new GroupsFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(user == null || (user != null && getArguments() != null)){
            System.out.println(getArguments().getString("user") + "awjgoiaehgoaeig");
            Gson gson = new Gson();
            user = gson.fromJson(getArguments().getString("user"),Map.class);
        }
        System.out.println(user);
        //TODO: create Invites recycler view
        db.GetJoinedGroups(user.get("Id").toString(), new DatabaseFuncs.GroupListener() {
            @Override
            public void onReceive(List<Map> groups, List<Map> groupLeaders) {
                List<Map> newList = new ArrayList<Map>();
                newList.addAll(groups);
                newList.addAll(groupLeaders);
                GroupsAdapter ga = new GroupsAdapter(newList, new PositionListener() {
                    @Override
                    public void itemClicked(Map group) {
                        PositionListener.super.itemClicked(group);
                        MainMenuActivity.selected = "groups";
                        requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup)(getView().getParent())).getId(),SelectedGroupFragment.newInstance(group)).addToBackStack(null).commit();
                    }
                });
                try {
                    b.rvGroups.setAdapter(ga);
                    b.rvGroups.setLayoutManager(new LinearLayoutManager(getContext()));
                } catch (Exception ex) {

                }
            }

            @Override
            public void onReceive(List<Map> groups) {


            }

            @Override
            public void getDeadline(Timestamp timestamp) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentGroupsBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }
}