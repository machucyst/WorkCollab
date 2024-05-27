package com.example.workcollab.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.workcollab.R;
import com.example.workcollab.databinding.FragmentApperanceBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.util.Map;

public class AppearanceFragment extends Fragment {
    Gson gson = new Gson();
    Map group;
    FragmentApperanceBinding b;
    public static AppearanceFragment newInstance() {
        Bundle args = new Bundle();
        Gson gson = new Gson();
        AppearanceFragment f = new AppearanceFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentApperanceBinding.inflate(inflater, container, false);
        b.nvAccountMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int a = menuItem.getItemId();
                if(a == R.id.menu_light){
                    //Light
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                if(a == R.id.menu_dark){
                    //Dark
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                }
                if(a == R.id.menu_system){
                    //Dark
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                }
                return false;
            }
        });
        menuTextChange(R.id.menu_light,"Light mode");
        menuTextChange(R.id.menu_dark, "Dark mode");
        menuTextChange(R.id.menu_system, "System settings");
        return b.getRoot();
    }
    private void menuTextChange(int ItemId,String text){
        TextView a = (b.nvAccountMenu.getMenu().findItem(ItemId).getActionView().findViewById(R.id.additionalText));
        a.setText(text);
    }
}