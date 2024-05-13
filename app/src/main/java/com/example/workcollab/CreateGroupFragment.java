package com.example.workcollab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.workcollab.databinding.FragmentCreateGroupBinding;
import com.google.firebase.Timestamp;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CreateGroupFragment extends Fragment {
    FragmentCreateGroupBinding b;
    public static List<Map> amabatuhavefun;
    DatabaseFuncs db = new DatabaseFuncs();
    Map user;
    CreateGroupsUsersAdapter ad;
    public CreateGroupFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentCreateGroupBinding.inflate(inflater, container, false);
        List<Map> filteredgroups = new ArrayList<>();
        List<String> filteredIds = new ArrayList<>();
        List<String> leader = new ArrayList<>();
        leader.add(user.get("Id").toString());
        b.tilAddUsers.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.GetUsers(new DatabaseFuncs.GroupListener() {

                    @Override
                    public void onReceive(List<Map> groups, List<Map> groupLeaders) {

                    }

                    @Override
                    public void onReceive(List<Map> groups) {
                        System.out.println(groups);
                        for (int i = 0; i < groups.size(); i++) {
                            if (b.etAdd.getText().toString().equals(groups.get(i).get("Email"))) {
                                System.out.println(filteredgroups);
                                if (!filteredgroups.contains(groups.get(i)) && !user.get("Email").toString().equals(groups.get(i).get("Email").toString())) {
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
        b.btnShowPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Public Static my beloved" + amabatuhavefun);
                try {
                    for (int i = 0; i < amabatuhavefun.size(); i++) {
                        filteredIds.add(amabatuhavefun.get(i).get("Id").toString());
                    }
                    String a = b.etGN.getText().toString();
                    if (a.equals("")) a = user.get("Username").toString() + "'s Group";
                    db.CreateGroup(a, leader, filteredIds, new DatabaseFuncs.UpdateListener() {
                        @Override
                        public void onUpdate(Map group) {
                            System.out.println("It worked probably");
                            requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), SelectedGroupFragment.newInstance(this.user,user)).addToBackStack(null).commit();

                        }
                    });
                } catch (Exception ex) {
                    System.out.println("it dont work");
                }

            }
        });
        return b.getRoot();
    }
    public static CreateGroupFragment newInstance(Map user){
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString("user", gson.toJson(user));
        CreateGroupFragment f = new CreateGroupFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(user == null || (user != null && getArguments() != null)){
            System.out.println(getArguments().getString("user") + "awjgoiaehgoaeig");
            Gson gson = new Gson();
            user = gson.fromJson(getArguments().getString("user"),Map.class);
        }


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
