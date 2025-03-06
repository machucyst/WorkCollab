package com.example.workcollab.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.workcollab.DatabaseFuncs;
import com.example.workcollab.PublicMethods;
import com.example.workcollab.activities.MainMenuActivity;
import com.example.workcollab.adapters.GroupsAdapter;
import com.example.workcollab.databinding.FragmentJoinedGroupsBinding;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JoinedGroupsSubFragment extends Fragment {

//    Map user;
    DatabaseFuncs db = new DatabaseFuncs();
    FragmentJoinedGroupsBinding b;
    boolean test = true;
    PublicMethods pb = new PublicMethods();
    Map tesxt;

    public JoinedGroupsSubFragment() {
        // Required empty public constructor
        // machu: fair enough
    }
    public interface PositionListener{
        default void itemClicked(Map<String,Object> group){}
    }
    PositionListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof JoinedGroupsSubFragment.PositionListener) {
            listener = (JoinedGroupsSubFragment.PositionListener) context;
        } else {
            throw new RuntimeException(context
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public static JoinedGroupsSubFragment newInstance(boolean test) {
        Bundle args = new Bundle();
        args.putBoolean("test",test);
        JoinedGroupsSubFragment f = new JoinedGroupsSubFragment();
        f.setArguments(args);
        return f;
    }

    List<Map<String,Object>> filteredgroups = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
           test = getArguments().getBoolean("test");
        }
        db.getJoinedGroups(String.valueOf(MainMenuActivity.user.get("Id")), new DatabaseFuncs.GroupListener() {
            @Override
            public void onReceive(List<Map<String,Object>> groups, List<Map<String,Object>> groupLeaders) {
                List<Map<String,Object>> newList = new ArrayList<>();
                if(test){
                    newList.addAll(groups);
                    newList.addAll(groupLeaders);
                }else{
                    newList.addAll(groupLeaders);
                }
                GroupsAdapter ga = new GroupsAdapter(newList, getContext(), new PositionListener() {
                    @Override
                    public void itemClicked(Map<String,Object> group) {
                        if(test){
                            PositionListener.super.itemClicked(group);
                            MainMenuActivity.selected = "groups";
                            listener.itemClicked(group);
                        }else{
                            test = true;
                            int vgID = ((ViewGroup)(requireView().getParent())).getId();
                            pb.replaceFragment(requireActivity(),AssignTaskFragment.newInstance(group),vgID);
                        }
                    }
                });
                try {
                    b.rvGroups.setAdapter(ga);
                    b.rvGroups.setLayoutManager(new GridLayoutManager(getContext(), 2));
                    b.etSearch.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            filteredgroups = new ArrayList<>();
                            if(b.etSearch.getText().toString().isEmpty()){
                                filteredgroups = newList;
                            }else{
                                newList.forEach(map -> {
                                    if(String.valueOf(map.get("GroupName")).matches("(?i).*"+b.etSearch.getText().toString()+".*")){
                                        filteredgroups.add(map);
                                    }
                                });
                            }
                            ga.refreshList(filteredgroups);
                        }


                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                } catch (Exception ignored) {

                }
            }

            @Override
            public void onReceive(List<Map<String,Object>> groups) {


            }

            @Override
            public void getDeadline(Timestamp timestamp) {

            }
        });
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentJoinedGroupsBinding.inflate(inflater, container, false);
        if(!test) {
            b.textView2.setVisibility(View.VISIBLE);
            b.rvGroups.setPadding(0, 0, 0, 200);
        }
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }
}
