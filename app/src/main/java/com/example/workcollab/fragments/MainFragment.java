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
import com.example.workcollab.R;
import com.example.workcollab.activities.MainMenuActivity;
import com.example.workcollab.adapters.DeadlinesAdapter;
import com.example.workcollab.databinding.FragmentMainBinding;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainFragment extends Fragment {

    FragmentMainBinding b;
    DatabaseFuncs db = new DatabaseFuncs();
    DeadlinesAdapter adapter;



    public static MainFragment newInstance() {
        return new MainFragment();
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
            MainMenuActivity.backFlow.push("viewtask");
            db.getGroupData(task.get("ParentId").toString(), new DatabaseFuncs.DataListener() {
                @Override
                public void onDataFound(Map group) {
                    if(Boolean.parseBoolean(group.get("isLeader").toString())){
                        requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), ViewMemberTasks.newInstance(task)).addToBackStack(null).commit();
                    }else{
                        requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), SubmitTaskFragment.newInstance(task)).addToBackStack(null).commit();
                    }
                }

                @Override
                public void noDuplicateUser() {

                }
            });

        }, MainMenuActivity.user);
        b.rvDeadlines.setLayoutManager(new LinearLayoutManager(getContext()));
        b.rvDeadlines.setAdapter(adapter);
        adapter.setHeaderClickListener(new DeadlinesAdapter.HeaderClickListener() {
            @Override
            public void onInvitesClick() {
                MainMenuActivity.backFlow.clear();
                MainMenuActivity.backFlow.push("groups");
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragment,GroupsFragment.newInstance(true)).commit();
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
        db.getTasks(MainMenuActivity.user.get("Id").toString(), new DatabaseFuncs.TaskListener() {
            @Override
            public void onTaskRecieved(List<Map> tasks) {
//                tasks1.addAll(tasks);
//                adapter = new DeadlinesAdapter(tasks1, getContext(), (position, task) -> {
//                    // TODO: Task item click
//                }, user);
//                b.rvDeadlines.setLayoutManager(new LinearLayoutManager(getContext()));
//                b.rvDeadlines.setAdapter(adapter);

                adapter.addRange(tasks);

            }

            @Override
            public void getDeadline(Timestamp timestamp) {

            }
        }, false);
        return b.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



    }
    public void setHeaderText (String txt){

    }
}