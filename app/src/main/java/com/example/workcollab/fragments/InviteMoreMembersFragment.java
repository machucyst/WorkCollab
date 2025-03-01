package com.example.workcollab.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.workcollab.DatabaseFuncs;
import com.example.workcollab.PublicMethods;
import com.example.workcollab.R;
import com.example.workcollab.activities.MainMenuActivity;
import com.example.workcollab.adapters.CreateGroupsUsersAdapter;
import com.example.workcollab.adapters.GroupMembersAdapter;
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
    List<Map<String, Object>> filteredgroups = new ArrayList<>();
    PublicMethods pb = new PublicMethods();

    public InviteMoreMembersFragment() {

    }


    public static InviteMoreMembersFragment newInstance(Map<String,Object> group) {
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentInviteMoreMembersBinding.inflate(inflater,container,false);
        toggleButtons(false);
        System.out.println(group);
        if(!Boolean.parseBoolean(String.valueOf(group.get("isLeader")))){
            b.llAddMembers.setVisibility(View.GONE);
        }

        b.tilAddUsers.setEndIconOnClickListener(v -> db.getUsers(new DatabaseFuncs.GroupListener() {

            @Override
            public void onReceive(List<Map<String,Object>> groups, List<Map<String,Object>> groupLeaders) {

            }

            @Override
            public void onReceive(List<Map<String,Object>> groups) {
                System.out.println(groups);
                for (int i = 0; i < groups.size(); i++) {
                    if (b.etAdd.getText().toString().equals(groups.get(i).get("Email"))) {
                        System.out.println(filteredgroups);
                        if (!filteredgroups.contains(groups.get(i)) && !String.valueOf(MainMenuActivity.user.get("Email")).equals(String.valueOf(groups.get(i).get("Email")))) {
                            filteredgroups.add(groups.get(i));
                        } else {
                            Toast.makeText(requireContext(), "User already selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                CreateGroupsUsersAdapter ad = new CreateGroupsUsersAdapter(requireContext(), filteredgroups, new CreateGroupFragment.onRemoveListener() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onRemovedItem(List<Map<String, Object>> groups) {
                        CreateGroupFragment.onRemoveListener.super.onRemovedItem(groups);
                        if(groups.isEmpty()){
                            b.submit.setBackgroundDrawable(getResources().getDrawable(R.drawable.textholderdisabled));
                            b.submit.setEnabled(false);
                        }else{
                            b.submit.setBackgroundDrawable(getResources().getDrawable(R.drawable.textholder));
                            b.submit.setText(R.string.create);
                            b.submit.setEnabled(true);
                        }
                    }
                });
                b.rvAddUsers.setAdapter(ad);
                b.rvAddUsers.setLayoutManager(new LinearLayoutManager(getContext()));
            }

            @Override
            public void getDeadline(Timestamp timestamp) {

            }
        }));
        db.getMembers(String.valueOf(group.get("Id")), new DatabaseFuncs.MembersListener() {
            @Override
            public void onReceiveMembers(List<Map<String,Object>> members) {
                GroupMembersAdapter a = new GroupMembersAdapter(members, getContext(), new GroupMembersAdapter.PositionListener() {

                    @Override
                    public void onMemberClicked(Map<String,Object> user) {
                        BottomDialogViewProfileFragment bdvf = new BottomDialogViewProfileFragment(String.valueOf(user.get("Id")));
                        bdvf.show(requireActivity().getSupportFragmentManager(),new BottomDialogViewProfileFragment(String.valueOf(user.get("Id"))).getTag());
                    }
                });
                if(members!=null){
                b.tilAddUsers.setEndIconOnClickListener(v -> db.getUsers(new DatabaseFuncs.GroupListener() {
                    @Override
                    public void onReceive(List<Map<String,Object>> groups, List<Map<String,Object>> groupLeaders) {

                    }

                    @Override
                    public void onReceive(List<Map<String,Object>> groups) {

                        System.out.println(groups);
                        String input = String.valueOf(b.etAdd.getText()).strip();
                        for (int i = 0; i < groups.size(); i++) {

                            if (input.equals(groups.get(i).get("Email"))) {
                                System.out.println(filteredgroups);
                                if (!filteredgroups.contains(groups.get(i)) && !String.valueOf(MainMenuActivity.user.get("Email")).equals(String.valueOf(groups.get(i).get("Email")))) {
                                    for(Map<String,Object> m:members){
                                        if(!input.equals(String.valueOf(m.get("Id")))){
                                            if(!filteredgroups.contains(groups.get(i))){
                                                filteredgroups.add(groups.get(i));
                                            }
                                            b.etAdd.setText("");
                                        }
                                        toggleButtons(!filteredgroups.isEmpty());
                                    }
                                } else {
                                    Toast.makeText(requireContext(), "User already selected", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        CreateGroupsUsersAdapter ad = new CreateGroupsUsersAdapter(requireContext(), filteredgroups, new CreateGroupFragment.onRemoveListener() {
                            @Override
                            public void onRemovedItem(List<Map<String, Object>> groups) {
                                CreateGroupFragment.onRemoveListener.super.onRemovedItem(groups);
                                toggleButtons(!groups.isEmpty());
                            }
                        });
                        b.rvAddUsers.setAdapter(ad);
                        b.rvAddUsers.setLayoutManager(new LinearLayoutManager(getContext()));
                    }

                    @Override
                    public void getDeadline(Timestamp timestamp) {

                    }
                }));
                }
                b.rvMembers.setAdapter(a);
                b.rvMembers.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        });
        b.submit.setOnClickListener(new View.OnClickListener() {
        List<String> filteredIds;
        boolean a = true;
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                filteredIds = new ArrayList<>();
                System.out.println("Public Static my beloved" + CreateGroupFragment.amabatuhavefun);
                b.submit.setBackgroundDrawable(getResources().getDrawable(R.drawable.textholderdisabled));
                b.submit.setEnabled(false);
                try {
                    for (int i = 0; i < CreateGroupFragment.amabatuhavefun.size(); i++) {
                        filteredIds.add(String.valueOf(CreateGroupFragment.amabatuhavefun.get(i).get("Id")));
                    }

                    db.inviteMembers(String.valueOf(group.get("Id")), filteredIds, new DatabaseFuncs.BasicListener() {
                        @Override
                        public void basicListener() {
                            if(a){
//                                requireActivity().getSupportFragmentManager().popBackStack();
                                int vgId = ((ViewGroup) (requireView().getParent())).getId();
                                Toast.makeText(getContext(),"Invites sent successfully",Toast.LENGTH_SHORT).show();
                                pb.replaceFragment(getParentFragmentManager(),SelectedGroupFragment.newInstance(group),vgId);
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
    @SuppressLint("UseCompatLoadingForDrawables")
    void toggleButtons(boolean x){
        if(x){
            b.submit.setBackgroundDrawable(getResources().getDrawable(R.drawable.textholder));
            b.submit.setEnabled(true);
        }else{
            b.submit.setBackgroundDrawable(getResources().getDrawable(R.drawable.textholderdisabled));
            b.submit.setEnabled(false);
        }
    }
}