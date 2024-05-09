package com.example.workcollab;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.workcollab.databinding.FragmentCreateGroupBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.util.Map;


public class CreateGroupFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FragmentCreateGroupBinding b;

    private String mParam1;
    private String mParam2;
    Map user;
    public CreateGroupFragment() {
    }
    public static CreateGroupFragment newInstance(Map user){
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString("user", gson.toJson(user));
        CreateGroupFragment f = new CreateGroupFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(user == null || (user != null && getArguments() != null)){
            System.out.println(getArguments().getString("user").toString()+"awjgoiaehgoaeig");
            Gson gson = new Gson();
            user = gson.fromJson(getArguments().getString("user"),Map.class);
        }




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentCreateGroupBinding.inflate(inflater,container,false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}