package com.example.workcollab.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.workcollab.R;
import com.example.workcollab.databinding.FragmentSettingsBinding;
import com.google.android.material.navigation.NavigationView;

public class SettingsFragment extends Fragment {
    FragmentSettingsBinding b;

    public SettingsFragment() {

    }
    public static SettingsFragment newInstance(){
        SettingsFragment f = new SettingsFragment();
        return f;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_settings, container, false);
        b = FragmentSettingsBinding.bind(v);
        menuTextChange(R.id.menu_notifications, "Notifications settings");

        b.nvAccountMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int a = menuItem.getItemId();
                if (a == R.id.menu_notifications){
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, getContext().getPackageName());

                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
        // Inflate the layout for this fragment
        return v;
    }

    private void menuTextChange(int ItemId, String text) {
        TextView a = (b.nvAccountMenu.getMenu().findItem(ItemId).getActionView().findViewById(R.id.additionalText));
        a.setText(text);
    }
}