package com.example.workcollab.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.workcollab.DatabaseFuncs;
import com.example.workcollab.R;
import com.example.workcollab.activities.ChatActivity;
import com.example.workcollab.activities.MainMenuActivity;
import com.example.workcollab.adapters.DeadlinesAdapter;
import com.example.workcollab.databinding.FragmentSelectedGroupBinding;
import com.google.firebase.Timestamp;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SelectedGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectedGroupFragment extends Fragment {
//    Map user;
    static Map group;
    DatabaseFuncs db = new DatabaseFuncs();
    DeadlinesAdapter adapter;
    FragmentSelectedGroupBinding b;
    public SelectedGroupFragment() {
        // Required empty public constructor
    }

    public static SelectedGroupFragment newInstance(Map group) {
        SelectedGroupFragment fragment = new SelectedGroupFragment();
        Bundle args = new Bundle();
        Gson gson = new Gson();
//       args.putIn("stream",inputStream);
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
//            user = gson.fromJson(getArguments().getString("user"),Map.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentSelectedGroupBinding.inflate(inflater,container,false);
        b.tvGroupName.setText(group.get("GroupName").toString());
        try {
            Glide.with(getContext()).load(group.get("GroupImage").toString()).into(b.ivGroupImage);
        }catch (Exception e){
            Glide.with(getContext()).load(AppCompatResources.getDrawable(getContext(), R.drawable.icon_test)).into(b.ivGroupImage);
        }
        System.out.println("Selected Group"+MainMenuActivity.user);
        System.out.println(group.get("isLeader"));
        b.btnTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainMenuActivity.selected="tasks";
                MainMenuActivity.selectedgroup = group;
                requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), TaskListFragment.newInstance(group)).addToBackStack(null).commit();

            }
        });
        b.btnChat.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ChatActivity.class);
            Gson gson = new Gson();
            intent.putExtra("user", gson.toJson(MainMenuActivity.user));
            intent.putExtra("group", gson.toJson(group));
            startActivity(intent);
        });
        b.btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainMenuActivity.selected="tasks";
                MainMenuActivity.selectedgroup = group;
                requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), SelectedGroupSettingsFragment.newInstance(group)).addToBackStack(null).commit();

            }
        });

        adapter = new DeadlinesAdapter(new ArrayList<>(), getContext(), (position, task) -> {
            MainMenuActivity.backFlow.push("viewtask");
            if(Boolean.parseBoolean(group.get("isLeader").toString())){
                requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), ViewMemberTasks.newInstance(task)).addToBackStack(null).commit();
            }else{
                requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), SubmitTaskFragment.newInstance(task)).addToBackStack(null).commit();
            }
        }, MainMenuActivity.user);
        b.rvDeadlines.setLayoutManager(new LinearLayoutManager(getContext()));
        b.rvDeadlines.setAdapter(adapter);

        db.getTasks(group.get("Id").toString(), group.get("GroupName").toString(), group.get("GroupImage") == null ? null : Uri.parse(group.get("GroupImage").toString()), new DatabaseFuncs.TaskListener() {
            @Override
            public void onTaskRecieved(List<Map> tasks) {
                adapter.addRange(tasks);
                if (adapter.tasks.size() > 0) {
                    b.rvDeadlines.setVisibility(View.VISIBLE);
                    b.waa.setVisibility(View.VISIBLE);
                } else {
                    b.rvDeadlines.setVisibility(View.GONE);
                    b.waa.setVisibility(View.GONE);
                }

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
            }

            @Override
            public void getDeadline(Timestamp timestamp) {

            }
        });

        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
