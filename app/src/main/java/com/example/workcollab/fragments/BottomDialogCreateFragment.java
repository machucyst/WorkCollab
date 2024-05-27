package com.example.workcollab.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.example.workcollab.DatabaseFuncs;
import com.example.workcollab.R;
import com.example.workcollab.activities.MainMenuActivity;
import com.example.workcollab.databinding.FragmentDialogBottomCreateBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

public class BottomDialogCreateFragment extends BottomSheetDialogFragment {
    FragmentDialogBottomCreateBinding b;
    DatabaseFuncs db =new DatabaseFuncs();
    String id;
    public BottomDialogCreateFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString("id");
        }
    }
    @SuppressLint("ResourceAsColor")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        b = DataBindingUtil.inflate(LayoutInflater.from(getContext()),R.layout.fragment_dialog_bottom_create,null,false);

        b.nvAccountMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int a = menuItem.getItemId();
                if(a == R.id.menu_ct){
                    MainMenuActivity.backFlow.push("Joined_Groups");
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragment, JoinedGroupsSubFragment.newInstance(false)).commit();
                    dismiss();
                }
                if(a == R.id.menu_cg){
                    MainMenuActivity.backFlow.push("creategroups");
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragment, CreateGroupFragment.newInstance()).commit();
                    dismiss();
                }
                return false;
            }
        });
        bottomSheetDialog.setContentView(b.getRoot());
        return bottomSheetDialog;
    }
}