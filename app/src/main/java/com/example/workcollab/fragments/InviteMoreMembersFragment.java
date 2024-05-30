package com.example.workcollab.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.workcollab.DatabaseFuncs;
import com.example.workcollab.R;
import com.example.workcollab.activities.MainMenuActivity;
import com.example.workcollab.adapters.CreateGroupsUsersAdapter;
import com.example.workcollab.adapters.GroupMembersAdapter;
import com.example.workcollab.adapters.MembersAdapter;
import com.example.workcollab.databinding.FragmentInviteMoreMembersBinding;
import com.google.firebase.Timestamp;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InviteMoreMembersFragment extends Fragment {

    Gson gson = new Gson();
    Map group;
    DatabaseFuncs db = new DatabaseFuncs();
    FragmentInviteMoreMembersBinding b;
    List<String> abae;
    List<Map> filteredgroups = new ArrayList<>();


    public static List<String> members = new ArrayList<>();

    MembersAdapter ma;
    public interface PositionListener{
        default void itemClicked(String id){}
    }


    public InviteMoreMembersFragment() {

    }


    public static InviteMoreMembersFragment newInstance(Map group) {
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString("group", gson.toJson(group));
        InviteMoreMembersFragment f = new InviteMoreMembersFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            group = gson.fromJson(getArguments().getString("group"),Map.class);
            System.out.println(group);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentInviteMoreMembersBinding.inflate(inflater,container,false);
        System.out.println(group);
        if(!Boolean.parseBoolean(group.get("isLeader").toString())){
            b.llAddMembers.setVisibility(View.GONE);
        }
        b.tilAddUsers.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.getUsers(new DatabaseFuncs.GroupListener() {

                    @Override
                    public void onReceive(List<Map> groups, List<Map> groupLeaders) {

                    }

                    @Override
                    public void onReceive(List<Map> groups) {
                        System.out.println(groups);
                        for (int i = 0; i < groups.size(); i++) {
                            if (b.etAdd.getText().toString().equals(groups.get(i).get("Email"))) {
                                System.out.println(filteredgroups);
                                if (!filteredgroups.contains(groups.get(i)) && !MainMenuActivity.user.get("Email").toString().equals(groups.get(i).get("Email").toString())) {
                                    filteredgroups.add(groups.get(i));
                                } else {
                                    Toast.makeText(requireContext(), "User already selected", Toast.LENGTH_SHORT);
                                }
                            }
                        }
                        CreateGroupsUsersAdapter ad = new CreateGroupsUsersAdapter(requireContext(), filteredgroups);
                        b.rvAddUsers.setAdapter(ad);
                        b.rvAddUsers.setLayoutManager(new LinearLayoutManager(getContext()));
                    }

                    @Override
                    public void getDeadline(Timestamp timestamp) {

                    }
                });
            }
        });
        db.getMembers(group.get("Id").toString(), new DatabaseFuncs.MembersListener() {
            @Override
            public void onReceiveMembers(List<Map> members) {
                GroupMembersAdapter a = new GroupMembersAdapter(members, getContext(), new GroupMembersAdapter.PositionListener() {

                    @Override
                    public void onMemberClicked(Map user) {
                        BottomDialogViewProfileFragment bdvf = new BottomDialogViewProfileFragment(user.get("Id").toString());
                        bdvf.show(requireActivity().getSupportFragmentManager(),new BottomDialogViewProfileFragment(user.get("Id").toString()).getTag());
                    }
                });
                if(members!=null){
                b.tilAddUsers.setEndIconOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                db.getUsers(new DatabaseFuncs.GroupListener() {
                    @Override
                    public void onReceive(List<Map> groups, List<Map> groupLeaders) {

                    }

                    @Override
                    public void onReceive(List<Map> groups) {

                        System.out.println(groups);
                        for (int i = 0; i < groups.size(); i++) {
                            if (b.etAdd.getText().toString().equals(groups.get(i).get("Email"))) {
                                System.out.println(filteredgroups);
                                if (!filteredgroups.contains(groups.get(i)) && !MainMenuActivity.user.get("Email").toString().equals(groups.get(i).get("Email").toString())) {
                                    for(Map m:members){
                                        if(!b.etAdd.getText().equals(m.get("Id").toString())){
                                            if(!filteredgroups.contains(groups.get(i))){
                                                filteredgroups.add(groups.get(i));
                                            }
                                            b.etAdd.setText("");
                                        }
                                    }
                                } else {
                                    Toast.makeText(requireContext(), "User already selected", Toast.LENGTH_SHORT);
                                }
                            }
                        }
                        CreateGroupsUsersAdapter ad = new CreateGroupsUsersAdapter(requireContext(), filteredgroups);
                        b.rvAddUsers.setAdapter(ad);
                        b.rvAddUsers.setLayoutManager(new LinearLayoutManager(getContext()));
                    }

                    @Override
                    public void getDeadline(Timestamp timestamp) {

                    }
                });
                    }
                });
                }
                b.rvMembers.setAdapter(a);
                b.rvMembers.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        });
        b.submit.setOnClickListener(new View.OnClickListener() {
        List<String> filteredIds;
        boolean a = true;
            @Override
            public void onClick(View v) {
                filteredIds = new ArrayList<>();
                System.out.println("Public Static my beloved" + CreateGroupFragment.amabatuhavefun);
                b.submit.setBackgroundDrawable(getResources().getDrawable(R.drawable.textholderdisabled));
                b.submit.setEnabled(false);
                try {
                    for (int i = 0; i < CreateGroupFragment.amabatuhavefun.size(); i++) {
                        filteredIds.add(CreateGroupFragment.amabatuhavefun.get(i).get("Id").toString());
                    }

                    db.inviteMembers(String.valueOf(group.get("Id")), filteredIds, new DatabaseFuncs.BasicListener() {
                        @Override
                        public void BasicListener() {
                            if(a){
                                requireActivity().getSupportFragmentManager().popBackStack();
                                filteredIds = new ArrayList<>();
                                a=false;
                            }
                        }
                    });
                } catch (Exception ex) {
                    System.out.println("it dont work");
                }

            }
        });


        return b.getRoot();
    }
}