package com.example.workcollab.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.workcollab.DatabaseFuncs;
import com.example.workcollab.activities.MainMenuActivity;
import com.example.workcollab.adapters.TasksAdapter;
import com.example.workcollab.databinding.FragmentMainBinding;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

public class MainFragment extends Fragment {

    FragmentMainBinding b;
//    Map user;
    DatabaseFuncs db = new DatabaseFuncs();
    Gson gson = new Gson();

    public MainFragment() {
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
        db.getTasks(MainMenuActivity.user.get("Id").toString(), new DatabaseFuncs.TaskListener(){

            @Override
            public void onTaskRecieved(List<Map> tasks) {
                TasksAdapter deadlineAdapter = new TasksAdapter(tasks, new TaskListFragment.PositionListener() {
                    @Override
                    public void taskItemClicked(Map task) {
                        TaskListFragment.PositionListener.super.taskItemClicked(task);
                    }
                });
                b.rvDeadlines.setAdapter(deadlineAdapter);
                b.rvDeadlines.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        });
        return b.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



    }
    public void setHeaderText (String txt){

    }
}