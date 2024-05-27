package com.example.workcollab.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.workcollab.DatabaseFuncs;
import com.example.workcollab.R;
import com.example.workcollab.activities.MainMenuActivity;
import com.example.workcollab.databinding.DialogLogoutConfirmBinding;
import com.example.workcollab.databinding.DialogTextInputBinding;
import com.example.workcollab.databinding.FragmentSelectedGroupSettingsBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SelectedGroupSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectedGroupSettingsFragment extends Fragment {
    Gson gson = new Gson();
    Map group;
    DialogLogoutConfirmBinding dlc;
    DialogTextInputBinding dtb;
    public interface GroupPFP{
        void onGroupChanged();
    }
    DatabaseFuncs db =new DatabaseFuncs();
    GroupPFP listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SelectedGroupSettingsFragment.GroupPFP) {
            listener = (SelectedGroupSettingsFragment.GroupPFP) context;
        } else {
            throw new RuntimeException(context
                    + " must implement OnFragmentInteractionListener");
        }
    }

    FragmentSelectedGroupSettingsBinding b;
    public static SelectedGroupSettingsFragment newInstance(Map group) {
        Bundle args = new Bundle();
        Gson gson = new Gson();
//       args.putIn("stream",inputStream);
        args.putString("group", gson.toJson(group));
        SelectedGroupSettingsFragment f = new SelectedGroupSettingsFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            group = gson.fromJson(getArguments().getString("group"),Map.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentSelectedGroupSettingsBinding.inflate(inflater, container, false);
        TextView a = b.nvAccountMenu.getMenu().findItem(R.id.menu_groupname).getActionView().findViewById(R.id.additionalText);
        a.setText(group.get("GroupName").toString());
        b.nvAccountMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int a = menuItem.getItemId();
                if(a == R.id.menu_groupmembers){
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), InviteMoreMembersFragment.newInstance(group)).addToBackStack(null).commit();
                }
                if(a == R.id.menu_groupPicture){
                    listener.onGroupChanged();
                }
                if(a == R.id.menu_deletegroups){
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    dlc = DialogLogoutConfirmBinding.inflate(getLayoutInflater());
                    builder.setView(dlc.getRoot());
                    AlertDialog dialog = builder.create();
                    dlc.editAccount.setText("Are you sure you want to leave this group?");
                    dlc.Cancel.setOnClickListener(k -> {
                        dialog.dismiss();
                    });
                    dlc.Ok.setOnClickListener(k -> {
                        db.leaveGroup(MainMenuActivity.user.get("Id").toString(), group.get("Id").toString(), new DatabaseFuncs.BasicListener() {
                            @Override
                            public void BasicListener() {
                                dialog.dismiss();
                                requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), MainFragment.newInstance()).addToBackStack(null).commit();
                            }
                        });
                    });
                    dialog.show();
                }
                if(a == R.id.menu_groupname){
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    dtb = DialogTextInputBinding.inflate(getLayoutInflater());
                    builder.setView(dtb.getRoot());
                    AlertDialog dialog = builder.create();
                    dtb.editAccount.setText("Rename Group");
                    dtb.Cancel.setOnClickListener(k -> {
                        dialog.dismiss();
                    });
                    dtb.Ok.setOnClickListener(k -> {
                        db.updateGroup(group, dtb.editText.getText().toString(),"GroupName", new DatabaseFuncs.UpdateListener() {
                            @Override
                            public void onUpdate(Map user) {
                                requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), SelectedGroupFragment.newInstance(user)).addToBackStack(null).commit();
                                dialog.dismiss();
                            }
                        },2);
                    });
                    dialog.show();
                }
                return false;
            }
        });
        return b.getRoot();
    }
}