package com.example.workcollab;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.workcollab.databinding.FragmentGroupsBinding;
import com.google.firebase.Timestamp;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupsFragment extends Fragment {

    Map user;
    List<Map> groups;
    Gson gson = new Gson();
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
            System.out.println(getArguments().getString("user").toString()+"awjgoiaehgoaeig");
            Gson gson = new Gson();
            user = gson.fromJson(getArguments().getString("user"),Map.class);
        }
        db.GetJoinedGroups(user.get("Id").toString(), new DatabaseFuncs.GroupListener() {
            @Override
            public void onReceive(List<Map> groups) {
                GroupsAdapter ga = new GroupsAdapter(groups, new PositionListener() {
                    @Override
                    public void itemClicked(Map group) {
                        PositionListener.super.itemClicked(group);
                        requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup)(getView().getParent())).getId(),SelectedGroupFragment.newInstance(group)).addToBackStack(null).commit();
                    }
                });
                b.rvGroups.setAdapter(ga);
                b.rvGroups.setLayoutManager(new LinearLayoutManager(getContext()));

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