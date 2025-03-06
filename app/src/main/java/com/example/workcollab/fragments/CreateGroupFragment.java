package com.example.workcollab.fragments;

import static android.view.View.GONE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.workcollab.DatabaseFuncs;
import com.example.workcollab.PublicMethods;
import com.example.workcollab.R;
import com.example.workcollab.activities.MainMenuActivity;
import com.example.workcollab.adapters.CreateGroupsUsersAdapter;
import com.example.workcollab.databinding.FragmentCreateGroupBinding;
import com.google.firebase.Timestamp;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CreateGroupFragment extends Fragment {
    FragmentCreateGroupBinding b;
    public static List<Map<String,Object>> amabatuhavefun;
    PublicMethods pb = new PublicMethods();
    DatabaseFuncs db = new DatabaseFuncs();
    public CreateGroupFragment() {
    }
    public interface onRemoveListener{
        default void onRemovedItem(List<Map<String,Object>> groups){}
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentCreateGroupBinding.inflate(inflater, container, false);
        List<Map<String,Object>> filteredgroups = new ArrayList<>();
        List<String> filteredIds = new ArrayList<>();
        List<String> leader = new ArrayList<>();
        leader.add(String.valueOf(MainMenuActivity.user.get("Id")));
        toggleButtons(false);
        b.tilAddUsers.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.getUsers(new DatabaseFuncs.GroupListener() {

                    @Override
                    public void onReceive(List<Map<String,Object>> groups, List<Map<String,Object>> groupLeaders) {

                    }

                    @Override
                    public void onReceive(List<Map<String,Object>> groups) {
                        System.out.println(groups);
                        for (int i = 0; i < groups.size(); i++) {
                            if (String.valueOf(b.etAdd.getText()).strip().equals(groups.get(i).get("Email"))) {
                                System.out.println(filteredgroups);
                                if (!filteredgroups.contains(groups.get(i)) && !String.valueOf(MainMenuActivity.user.get("Email")).equals(String.valueOf(groups.get(i).get("Email")))) {
                                    filteredgroups.add(groups.get(i));
                                    b.textView2.setVisibility(GONE);
                                    b.etAdd.setText("");
                                    toggleButtons(!filteredgroups.isEmpty());
                                } else {
                                    Toast.makeText(requireContext(), "User already selected", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        CreateGroupsUsersAdapter ad = new CreateGroupsUsersAdapter(requireContext(), filteredgroups, new onRemoveListener() {
                            @Override
                            public void onRemovedItem(List<Map<String, Object>> groups) {
                                onRemoveListener.super.onRemovedItem(groups);
                                if(groups.isEmpty()||amabatuhavefun.isEmpty()){
                                    toggleButtons(false);
                                    b.btnSubmit.setText(R.string.create);
                                }else{
                                    toggleButtons(true);
                                }
                            }
                        });
                        b.rvAddUsers.setAdapter(ad);
                        b.rvAddUsers.setLayoutManager(new LinearLayoutManager(getContext()));
                    }

                    @Override
                    public void getDeadline(Timestamp timestamp) {

                    }
                });
            }
        });

        b.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Public Static my beloved" + amabatuhavefun);
                toggleButtons(false);
                try {
                    for (int i = 0; i < amabatuhavefun.size(); i++) {
                        filteredIds.add(String.valueOf(amabatuhavefun.get(i).get("Id")));
                    }
                    String a = b.etGN.getText().toString();
                    if (a.isEmpty()) a = MainMenuActivity.user.get("Username") + "'s Group";

                    db.createGroup(a, leader, filteredIds,requireContext(),b.btnSubmit,String.valueOf(MainMenuActivity.user.get("Profile")), new DatabaseFuncs.UpdateListener() {
                        @Override
                        public void onUpdate(Map<String,Object> group) {
                            System.out.println("It worked probably");
                            int id = ((ViewGroup) requireView().getParent()).getId();
                            pb.replaceFragment(requireActivity(),SelectedGroupFragment.newInstance(group),id);
                        }
                    });
                } catch (Exception ex) {
                    System.out.println("it dont work");
                }

            }
        });
        return b.getRoot();
    }
    public void toggleButtons(boolean t){
        if(t){
            b.btnSubmit.setBackgroundDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.textholder));
            b.btnSubmit.setEnabled(true);
            b.btnSubmit.setText(R.string.create);
            return;
        }
        b.btnSubmit.setBackgroundDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.textholderdisabled));
        b.btnSubmit.setEnabled(false);
        b.btnSubmit.setText(R.string.loading);
    }
    public static CreateGroupFragment newInstance(){
        Bundle args = new Bundle();
        Gson gson = new Gson();
//        args.putString("user", gson.toJson(user));
        CreateGroupFragment f = new CreateGroupFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(user == null || (user != null && getArguments() != null)){
//            System.out.println(getArguments().getString("user") + "awjgoiaehgoaeig");
//            Gson gson = new Gson();
//            user = gson.fromJson(getArguments().getString("user"),Map.class);
//        }


    }

    public interface PositionListener {
        default void position(int position) {
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
