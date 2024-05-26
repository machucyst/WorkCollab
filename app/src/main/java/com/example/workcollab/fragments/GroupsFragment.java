package com.example.workcollab.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.workcollab.R;
import com.example.workcollab.databinding.FragmentGroupsBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;


public class GroupsFragment extends Fragment {
//    Map user;
    FragmentGroupsBinding b;
    Gson gson = new Gson();

    public static GroupsFragment newInstance() {
        Bundle args = new Bundle();
        Gson gson = new Gson();
//        args.putString("user", gson.toJson(user));
        GroupsFragment f = new GroupsFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            System.out.println(getArguments().getString("user") + "awjgoiaehgoaeig");
//            user = gson.fromJson(getArguments().getString("user"), Map.class);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentGroupsBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getChildFragmentManager().beginTransaction().replace(b.groupsFragmentFrame.getId(),JoinedGroupsSubFragment.newInstance()).commit();
        b.groupsMenu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int a = menuItem.getItemId();
                if(a == R.id.menu_joined){
                    getChildFragmentManager().beginTransaction().replace(b.groupsFragmentFrame.getId(),JoinedGroupsSubFragment.newInstance()).commit();
                    return true;
                }else if(a == R.id.menu_invites){
                    getChildFragmentManager().beginTransaction().replace(b.groupsFragmentFrame.getId(),InvitesSubFragment.newInstance()).commit();
                    return true;
                }
                return false;
            }
        });
    }
}