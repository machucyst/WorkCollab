package com.example.workcollab;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.workcollab.databinding.FragmentSelectedGroupBinding;
import com.google.gson.Gson;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SelectedGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectedGroupFragment extends Fragment {

    Map group;
    DatabaseFuncs db = new DatabaseFuncs();
    FragmentSelectedGroupBinding b;
    public SelectedGroupFragment() {
        // Required empty public constructor
    }

    public static SelectedGroupFragment newInstance(Map group) {
        SelectedGroupFragment fragment = new SelectedGroupFragment();
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString("user", gson.toJson(group));
        SelectedGroupFragment f = new SelectedGroupFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(group == null || (group != null && getArguments() != null)){
            System.out.println(getArguments().getString("user").toString()+"awjgoiaehgoaeig");
            Gson gson = new Gson();
            group = gson.fromJson(getArguments().getString("user"),Map.class);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentSelectedGroupBinding.inflate(inflater,container,false);
        b.tvGroupName.setText(group.get("GroupName").toString());
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}