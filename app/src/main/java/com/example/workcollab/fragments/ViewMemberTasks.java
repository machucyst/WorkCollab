package com.example.workcollab.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.workcollab.DatabaseFuncs;
import com.example.workcollab.adapters.MemberTaskAdapter;
import com.example.workcollab.databinding.FragmentViewMemberTasksBinding;
import com.google.firebase.Timestamp;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewMemberTasks#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewMemberTasks extends Fragment {

    public interface PositionListener{
        default void viewMemberTask(Map task){}
    }
    ViewMemberTasks.PositionListener listener;
    FragmentViewMemberTasksBinding b;
    DatabaseFuncs db = new DatabaseFuncs();
    Gson gson = new Gson();
    Map task;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ViewMemberTasks.PositionListener) {
            listener = (ViewMemberTasks.PositionListener) context;
        } else {
            throw new RuntimeException(context
                    + " must implement OnFragmentInteractionListener");
        }
    }
    public static ViewMemberTasks newInstance(Map task) {
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString("task", gson.toJson(task));
        ViewMemberTasks f = new ViewMemberTasks();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            task = gson.fromJson(getArguments().getString("task"),Map.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        b = FragmentViewMemberTasksBinding.inflate(inflater,container,false);
        db.getMemberSubmissions(task, new DatabaseFuncs.TaskListener() {
            @Override
            public void onTaskRecieved(List<Map> tasks) {
                MemberTaskAdapter mta = new MemberTaskAdapter(tasks, getContext(), new PositionListener() {
                    @Override
                    public void viewMemberTask(Map task) {

                    }
                });
                b.rvMemberTasks.setAdapter(mta);
                b.rvMemberTasks.setLayoutManager(new LinearLayoutManager(getContext()));
            }

            @Override
            public void getDeadline(Timestamp timestamp) {

            }
        });
        return b.getRoot();
    }
}