package com.example.workcollab.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.workcollab.DatabaseFuncs;
import com.example.workcollab.PublicMethods;
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
    Map<String,Object> group;
    PublicMethods pb = new PublicMethods();
    DialogLogoutConfirmBinding dlc;
    DialogTextInputBinding dtb;
    public interface GroupPFP{
        void onGroupChanged();
        void otherOne();
    }
    DatabaseFuncs db =new DatabaseFuncs();
    GroupPFP listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof SelectedGroupSettingsFragment.GroupPFP) {
            listener = (SelectedGroupSettingsFragment.GroupPFP) context;
        } else {
            throw new RuntimeException(context
                    + " must implement OnFragmentInteractionListener");
        }
    }

    FragmentSelectedGroupSettingsBinding b;
    public static SelectedGroupSettingsFragment newInstance(Map<String,Object> group) {
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentSelectedGroupSettingsBinding.inflate(inflater, container, false);
        PublicMethods.menuTextChange(b.nvAccountMenu, R.id.additionalText,R.id.menu_groupname,"GroupName");
        b.nvAccountMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int vgID = ((ViewGroup) (requireView().getParent())).getId();
                int a = menuItem.getItemId();
                if(a == R.id.menu_groupmembers){
                    pb.replaceFragment(requireActivity(),InviteMoreMembersFragment.newInstance(group),vgID);
                }
                if(a == R.id.menu_groupPicture){
                    listener.onGroupChanged();
                }
                if(a == R.id.menu_deletegroups){
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    dlc = DialogLogoutConfirmBinding.inflate(getLayoutInflater());
                    builder.setView(dlc.getRoot());
                    AlertDialog dialog = builder.create();
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dlc.editAccount.setText(R.string.are_you_sure_you_want_to_leave_this_group);
                    dlc.Cancel.setOnClickListener(k -> dialog.dismiss());
                    dlc.Ok.setOnClickListener(k -> {
                        db.leaveGroup(String.valueOf(MainMenuActivity.user.get("Id")), String.valueOf(group.get("Id")), new DatabaseFuncs.BasicListener() {
                            @Override
                            public void basicListener() {
                                listener.otherOne();
                                dialog.dismiss();
                            }
                        });
                        Toast.makeText(getContext(), "Left the group",Toast.LENGTH_SHORT).show();
                    });
                    dialog.show();
                }
                if(a == R.id.menu_groupname){
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    dtb = DialogTextInputBinding.inflate(getLayoutInflater());
                    builder.setView(dtb.getRoot());
                    AlertDialog dialog = builder.create();
                    dtb.editAccount.setText(R.string.rename_group);
                    dtb.Cancel.setOnClickListener(k -> dialog.dismiss());
                    dtb.Ok.setOnClickListener(k -> {
                        db.updateGroup(group, dtb.editText.getText().toString(),"GroupName", new DatabaseFuncs.UpdateListener() {
                            @Override
                            public void onUpdate(Map<String,Object> user) {

                                pb.replaceFragment(requireActivity(),SelectedGroupFragment.newInstance(user),vgID);
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
