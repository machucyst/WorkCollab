package com.example.workcollab.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.workcollab.DatabaseFuncs;
import com.example.workcollab.R;
import com.example.workcollab.activities.MainMenuActivity;
import com.example.workcollab.adapters.DeadlinesAdapter;
import com.example.workcollab.adapters.TasksAdapter;
import com.example.workcollab.databinding.FragmentMainBinding;
import com.google.firebase.Timestamp;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainFragment extends Fragment {

    FragmentMainBinding b;
    Map user;
    DatabaseFuncs db = new DatabaseFuncs();
    Gson gson = new Gson();
    DeadlinesAdapter adapter;

    public MainFragment() {
    }

    public MainFragment(Map user) {
        this.user = user;
    }


    public static MainFragment newInstance() {
        Bundle args = new Bundle();
        Gson gson = new Gson();
//        args.putString("user", gson.toJson(user));
        MainFragment f = new MainFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentMainBinding.inflate(inflater, container, false);

        List<Object> tasks1 = new ArrayList<>();
        tasks1.add("");

        adapter = new DeadlinesAdapter(tasks1, getContext(), (position, task) -> {
            // TODO: Task item click
        }, user);
        b.rvDeadlines.setLayoutManager(new LinearLayoutManager(getContext()));
        b.rvDeadlines.setAdapter(adapter);
        db.getTasks(MainMenuActivity.user.get("Id").toString(), new DatabaseFuncs.TaskListener() {
            @Override
            public void onTaskRecieved(List<Map> tasks) {
                tasks1.addAll(tasks);
                adapter = new DeadlinesAdapter(tasks1, getContext(), (position, task) -> {
                    // TODO: Task item click
                }, user);
                b.rvDeadlines.setLayoutManager(new LinearLayoutManager(getContext()));
                b.rvDeadlines.setAdapter(adapter);

                adapter.setHeaderClickListener(new DeadlinesAdapter.HeaderClickListener() {
                    @Override
                    public void onInvitesClick() {
                        MainMenuActivity.backFlow.clear();
                        MainMenuActivity.backFlow.push("groups");
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragment,new GroupsFragment(true)).commit();
                    }

                    @Override
                    public void onCreateGroupClick() {
                        MainMenuActivity.backFlow.push("creategroups");
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragment,CreateGroupFragment.newInstance()).commit();
                    }

                    @Override
                    public void onProfileClick() {
                        MainMenuActivity.backFlow.clear();
                        MainMenuActivity.backFlow.push("profile");
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragment,AccountFragment.newInstance()).commit();
                    }
                });
            }

            @Override
            public void getDeadline(Timestamp timestamp) {

            }
        }, false);






//        TasksAdapter deadlineAdapter = new TasksAdapter(tasks, new TaskListFragment.PositionListener() {
//            @Override
//            public void taskItemClicked(Map task) {
//                TaskListFragment.PositionListener.super.taskItemClicked(task);
//            }
//        });
//        b.rvDeadlines.setAdapter(deadlineAdapter);
//        b.rvDeadlines.setLayoutManager(new LinearLayoutManager(getContext()));
//        b.rvDeadlines.setNestedScrollingEnabled(false);
//        db.getTasks(MainMenuActivity.user.get("Id").toString(), new DatabaseFuncs.TaskListener(){
//
//            @Override
//            public void onTaskRecieved(List<Map> tasks) {
//                TasksAdapter deadlineAdapter = new TasksAdapter(tasks, new TaskListFragment.PositionListener() {
//                    @Override
//                    public void taskItemClicked(Map task) {
//                        TaskListFragment.PositionListener.super.taskItemClicked(task);
//                    }
//                });
//                b.rvDeadlines.setAdapter(deadlineAdapter);
//                b.rvDeadlines.setLayoutManager(new LinearLayoutManager(getContext()));
//            }
//
//            @Override
//            public void getDeadline(Timestamp timestamp) {
//
//            }
//
//
//        });

//        b.btnNG.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getContext(), "wdawda", Toast.LENGTH_SHORT).show();
//                MainMenuActivity.backFlow.push("creategroups");
//                requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), CreateGroupFragment.newInstance()).addToBackStack(null).commit();
//
//            }
//        });
//        b.btnG.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainMenuActivity.backFlow.push("creategroups");
//                requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), CreateGroupFragment.newInstance()).addToBackStack(null).commit();
//            }
//        });
        return b.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



    }
    public void setHeaderText (String txt){

    }
}