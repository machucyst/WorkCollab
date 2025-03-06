package com.example.workcollab.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.workcollab.DatabaseFuncs;
import com.example.workcollab.activities.MainMenuActivity;
import com.example.workcollab.adapters.InvitesAdapter;
import com.example.workcollab.databinding.FragmentInvitesBinding;
import com.google.firebase.Timestamp;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InvitesSubFragment extends Fragment  {

    Gson gson = new Gson();
    DatabaseFuncs db = new DatabaseFuncs();
    FragmentInvitesBinding b;
    public interface PositionListener{
        default void onDeny(Map<String,Object>  group){}
        default void onAccept(Map<String,Object>  group){}
    }
    InvitesSubFragment.PositionListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof InvitesSubFragment.PositionListener) {
            listener = (InvitesSubFragment.PositionListener) context;
        } else {
            throw new RuntimeException(context
                    + " must implement OnFragmentInteractionListener");
        }
    }
    public InvitesSubFragment() {
        // Required empty public constructor
    }


    public static InvitesSubFragment newInstance() {
        Bundle args = new Bundle();
        Gson gson = new Gson();
//        args.putString("user", gson.toJson(user));
        InvitesSubFragment f = new InvitesSubFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(getArguments() != null){
//            System.out.println(getArguments().getString("user") + "awjgoiaehgoaeig");
//            Gson gson = new Gson();
//            user = gson.fromJson(getArguments().getString("user"),Map.class);
//        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentInvitesBinding.inflate(inflater,container,false);
        InvitesAdapter a = new InvitesAdapter(new ArrayList<>(), getContext(), new PositionListener() {
            @Override
            public void onDeny(Map<String,Object> group) {
                PositionListener.super.onDeny(group);
                db.denyInvite(String.valueOf(MainMenuActivity.user.get("Id")), String.valueOf(group.get("Id")), new DatabaseFuncs.OptionListener() {
                    @Override
                    public void onOptionPicked() {
                        Toast.makeText(requireContext(),"Invite Denied",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onAccept(Map<String,Object> group) {
                PositionListener.super.onAccept(group);
                db.acceptInvite(String.valueOf(MainMenuActivity.user.get("Id")), String.valueOf(group.get("Id")), new DatabaseFuncs.OptionListener() {
                    @Override
                    public void onOptionPicked() {
                        Toast.makeText(requireContext(), "Invite to "+group.get("GroupName")+" Accepted",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        b.rvInvites.setAdapter(a);
        b.rvInvites.setLayoutManager(new LinearLayoutManager(getContext()));
        db.getInvites(String.valueOf(MainMenuActivity.user.get("Id")), new DatabaseFuncs.GroupListener() {
            @Override
            public void onReceive(List<Map<String,Object> > groups, List<Map<String,Object> > groupLeaders) {

            }

            @Override
            public void onReceive(List<Map<String,Object> > groups) {
                a.addRange(groups);
            }

            @Override
            public void getDeadline(Timestamp timestamp) {

            }
        });
        return b.getRoot();

    }
}