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
import com.example.workcollab.PublicMethods;
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
    Map group;
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
        if(group == null || getArguments() != null){
            System.out.println(getArguments().getString("user") + "awjgoiaehgoaeig");
            Gson gson = new Gson();
            group = gson.fromJson(getArguments().getString("group"),Map.class);
//            user = gson.fromJson(getArguments().getString("user"),Map.class);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentSelectedGroupBinding.inflate(inflater,container,false);
        b.tvGroupName.setText(String.valueOf(group.get("GroupName")));
        try {
            Glide.with(requireContext()).load(String.valueOf(group.get("GroupImage"))).into(b.ivGroupImage);
        }catch (Exception e){
            Glide.with(requireContext()).load(AppCompatResources.getDrawable(requireContext(), R.drawable.icon_test)).into(b.ivGroupImage);
        }
        System.out.println("Selected Group"+MainMenuActivity.user);
        System.out.println(group.get("isLeader"));
        b.btnTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainMenuActivity.selected="tasks";
                MainMenuActivity.selectedGroup = group;
                replaceFragment(TaskListFragment.newInstance(group));
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
                MainMenuActivity.selectedGroup = group;
                replaceFragment(SelectedGroupSettingsFragment.newInstance(group));
            }
        });

        adapter = new DeadlinesAdapter(new ArrayList<>(), getContext(), (position, task) -> {
            MainMenuActivity.backFlow.push("viewtask");
            if(Boolean.parseBoolean(String.valueOf(group.get("isLeader")))){
                replaceFragment(ViewMemberTasks.newInstance(task));
            }else{
                replaceFragment(SubmitTaskFragment.newInstance(task));
            }
        }, MainMenuActivity.user);
        b.rvDeadlines.setLayoutManager(new LinearLayoutManager(getContext()));
        b.rvDeadlines.setAdapter(adapter);

        db.getTasks(String.valueOf(group.get("Id")),
                String.valueOf(group.get("GroupName")),
                group.get("GroupImage") == null ? null : Uri.parse(String.valueOf(group.get("GroupImage"))),
                new DatabaseFuncs.TaskListener() {
                    @Override
                    public void onTaskReceived(List<Map<String, Object>> tasks) {
                        adapter.addRange(tasks);
                        if (!adapter.tasks.isEmpty()) {
                            b.rvDeadlines.setVisibility(View.VISIBLE);
                            b.waa.setVisibility(View.VISIBLE);
                        } else {
                            b.rvDeadlines.setVisibility(View.GONE);
                            b.waa.setVisibility(View.GONE);
                        }
                        adapter.setHeaderClickListener(new DeadlinesAdapter.HeaderClickListener() {
                            @Override
                            public void onInvitesClick() {
                                replaceFragment(GroupsFragment.newInstance(true),"groups",true);
                            }

                            @Override
                            public void onCreateGroupClick() {
                               replaceFragment(CreateGroupFragment.newInstance(),"creategroups",false);
                            }

                            @Override
                            public void onProfileClick() {
                                replaceFragment(AccountFragment.newInstance(),"profile",true);
                            }
                        });
                    }

                    @Override
                    public void getDeadline(Timestamp timestamp) {

                    }
                });
        return b.getRoot();
    }
    private void replaceFragment(Fragment fragment,String condition, boolean bool){
        PublicMethods pb = new PublicMethods();
        pb.replaceFragment(getActivity(),fragment,R.id.frame_fragment,condition,bool);
    }
    private void replaceFragment(Fragment fragment){
        PublicMethods pb = new PublicMethods();
        pb.replaceFragment(requireActivity(),fragment,R.id.frame_fragment);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
