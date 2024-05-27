package com.example.workcollab.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.workcollab.R;
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
    public interface GroupPFP{
        void onGroupChanged();
    }
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
                return false;
            }
        });
        menuTextChange(R.id.menu_groupname,group.get("GroupName").toString());
        return b.getRoot();
    }
    private void menuTextChange(int ItemId,String text){
        TextView a = (b.nvAccountMenu.getMenu().findItem(ItemId).getActionView().findViewById(R.id.additionalText));
        a.setText(text);
    }
}