package com.example.workcollab.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.workcollab.PublicMethods;
import com.example.workcollab.R;
import com.example.workcollab.activities.MainMenuActivity;
import com.example.workcollab.databinding.FragmentGroupsBinding;
import com.google.android.material.navigation.NavigationBarView;


public class GroupsFragment extends Fragment {
    boolean a = false;
    FragmentGroupsBinding b;
    PublicMethods pb = new PublicMethods();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentGroupsBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pb.replaceFragment(getChildFragmentManager(),JoinedGroupsSubFragment.newInstance(MainMenuActivity.user),b.groupsFragmentFrame.getId());
        if (a) {
            pb.replaceFragment(getChildFragmentManager(),InvitesSubFragment.newInstance(),b.groupsFragmentFrame.getId());
            b.groupsMenu.setSelectedItemId(R.id.menu_invites);
        }
        b.groupsMenu.setOnItemSelectedListener(menuItem -> {
            int a = menuItem.getItemId();
            if(a == R.id.menu_joined){
                pb.replaceFragment(getChildFragmentManager(),JoinedGroupsSubFragment.newInstance(MainMenuActivity.user),b.groupsFragmentFrame.getId());
                return true;
            }else if(a == R.id.menu_invites){
                pb.replaceFragment(getChildFragmentManager(),InvitesSubFragment.newInstance(),b.groupsFragmentFrame.getId());
                return true;
            }
            return false;
        });
    }
}