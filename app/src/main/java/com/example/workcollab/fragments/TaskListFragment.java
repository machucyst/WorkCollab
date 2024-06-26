package com.example.workcollab.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.workcollab.DatabaseFuncs;
import com.example.workcollab.activities.MainMenuActivity;
import com.example.workcollab.adapters.DeadlinesAdapter;
import com.example.workcollab.adapters.TasksAdapter;
import com.example.workcollab.databinding.FragmentTaskListBinding;
import com.google.firebase.Timestamp;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TaskListFragment extends Fragment {
    FragmentTaskListBinding b;
    public static Map group;
    DatabaseFuncs db = new DatabaseFuncs();
    Gson gson = new Gson();
    boolean a;
    public interface PositionListener{
        default void taskItemClicked(Map task){}
    }
    TaskListFragment.PositionListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TaskListFragment.PositionListener) {
            listener = (TaskListFragment.PositionListener) context;
        } else {
            throw new RuntimeException(context
                    + " must implement OnFragmentInteractionListener");
        }
    }
    public static TaskListFragment newInstance(Map group) {
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString("group", gson.toJson(group));
        TaskListFragment f = new TaskListFragment();
        f.setArguments(args);
        return f;
    }
    public static TaskListFragment newInstance(boolean a) {
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putBoolean("filter",a);
        TaskListFragment f = new TaskListFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            System.out.println(getArguments().getString("user") + "awjgoiaehgoaeig");
            if (getArguments().getString("group") != null){
//                user = gson.fromJson(getArguments().getString("user"), Map.class);
//            }else{
                a = true;
                group = gson.fromJson(getArguments().getString("group"),Map.class);
            }else{
                a = getArguments().getBoolean("filter");

            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       // Inflate the layout for this fragment
       List<Object> wwa = new ArrayList<>();
        b = FragmentTaskListBinding.inflate(inflater,container,false);
        if(a){
            DeadlinesAdapter adapter = new DeadlinesAdapter(wwa, getContext(), (position, task) -> {
                MainMenuActivity.backFlow.push("viewtask");
                if(Boolean.parseBoolean(group.get("isLeader").toString())){
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), ViewMemberTasks.newInstance(task)).addToBackStack(null).commit();
                }else{
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), SubmitTaskFragment.newInstance(task)).addToBackStack(null).commit();
                }
            }, MainMenuActivity.user);
            b.rvTasks.setLayoutManager(new LinearLayoutManager(getContext()));
            b.rvTasks.setAdapter(adapter);
            db.getTasks(group.get("Id").toString(), group.get("GroupName").toString(), group.get("GroupImage") == null ? null : Uri.parse(group.get("GroupImage").toString()), new DatabaseFuncs.TaskListener() {
                @Override
                public void onTaskRecieved(List<Map> tasks) {
                    adapter.addRange(tasks);
                }

                @Override
                public void getDeadline(Timestamp timestamp) {

                }
            });
        }else{
            DeadlinesAdapter adapter = new DeadlinesAdapter(wwa, getContext(), (position, task) -> {
                MainMenuActivity.backFlow.push("viewtask");
                MainMenuActivity.selected = "viewtask";
                requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), SubmitTaskFragment.newInstance(task)).addToBackStack(null).commit();
            }, MainMenuActivity.user);
            b.rvTasks.setLayoutManager(new LinearLayoutManager(getContext()));
            b.rvTasks.setAdapter(adapter);
           db.getTasks(MainMenuActivity.user.get("Id").toString(), new DatabaseFuncs.TaskListener() {
               @Override
               public void onTaskRecieved(List<Map> tasks) {
                   adapter.addRange(tasks);

               }

               @Override
               public void getDeadline(Timestamp timestamp) {

               }
           },true);
        }
        return b.getRoot();
    }
}
