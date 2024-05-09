package com.example.workcollab;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.workcollab.databinding.FragmentMainBinding;
import com.google.firebase.Timestamp;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

public class MainFragment extends Fragment {

    FragmentMainBinding b;
    Map user;
    DatabaseFuncs db = new DatabaseFuncs();
    Gson gson = new Gson();

    public MainFragment() {
    }

    public MainFragment(Map user) {
        this.user = user;
    }

    public static MainFragment newInstance(Map user) {
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString("user", gson.toJson(user));
        MainFragment f = new MainFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (user == null || (user != null && getArguments() != null)) {
            System.out.println(getArguments().getString("user").toString() + "awjgoiaehgoaeig");
            user = gson.fromJson(getArguments().getString("user"), Map.class);

        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentMainBinding.inflate(inflater, container, false);
        db.GetProjects(user.get("Id").toString(), new DatabaseFuncs.GroupListener() {
            @Override
            public void onReceive(List<Map> groups) {
                DeadlineAdapter deadlineAdapter = new DeadlineAdapter(groups);
                b.rvDeadlines.setAdapter(deadlineAdapter);
                b.rvDeadlines.setLayoutManager(new LinearLayoutManager(getContext()));
            }

            @Override
            public void getDeadline(Timestamp timestamp) {

            }


        });

        b.btnNG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), CreateGroupFragment.newInstance(user)).addToBackStack(null).commit();

            }
        });
        b.btnG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), CreateGroupFragment.newInstance(user)).addToBackStack(null).commit();
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