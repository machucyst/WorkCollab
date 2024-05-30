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


public class GroupsFragment extends Fragment {
//    Map user;
    boolean a = false;
    FragmentGroupsBinding b;
    public static GroupsFragment newInstance(boolean a) {
        Bundle args = new Bundle();
        args.putBoolean("bool", a);
        GroupsFragment f = new GroupsFragment();
        f.setArguments(args);
        return f;
    }

    public GroupsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            a = getArguments().getBoolean("bool");
        }
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
        getChildFragmentManager().beginTransaction().replace(b.groupsFragmentFrame.getId(),JoinedGroupsSubFragment.newInstance(true)).commit();
        if (a) {
            getChildFragmentManager().beginTransaction().replace(b.groupsFragmentFrame.getId(),InvitesSubFragment.newInstance()).commit();
            b.groupsMenu.setSelectedItemId(R.id.menu_invites);
        }
        b.groupsMenu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int a = menuItem.getItemId();
                if(a == R.id.menu_joined){
                    getChildFragmentManager().beginTransaction().replace(b.groupsFragmentFrame.getId(),JoinedGroupsSubFragment.newInstance(true)).commit();
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