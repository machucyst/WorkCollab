package com.example.workcollab.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.workcollab.DatabaseFuncs;
import com.example.workcollab.InvitesAdapter;
import com.example.workcollab.databinding.FragmentInvitesBinding;
import com.google.firebase.Timestamp;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

public class InvitesSubFragment extends Fragment  {

    Gson gson = new Gson();
    Map user;
    DatabaseFuncs db = new DatabaseFuncs();
    FragmentInvitesBinding b;
    public interface PositionListener{
        default void onDeny(Map group){}
        default void onAccept(Map group){}
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


    public static InvitesSubFragment newInstance(Map user) {
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString("user", gson.toJson(user));
        InvitesSubFragment f = new InvitesSubFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            System.out.println(getArguments().getString("user") + "awjgoiaehgoaeig");
            Gson gson = new Gson();
            user = gson.fromJson(getArguments().getString("user"),Map.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentInvitesBinding.inflate(inflater,container,false);
        db.GetInvites(user.get("Id").toString(), new DatabaseFuncs.GroupListener() {
            @Override
            public void onReceive(List<Map> groups, List<Map> groupLeaders) {

            }

            @Override
            public void onReceive(List<Map> groups) {
                InvitesAdapter a = new InvitesAdapter(groups, new PositionListener() {
                    @Override
                    public void onDeny(Map group) {
                        PositionListener.super.onDeny(group);
                        db.denyInvite(user.get("Id").toString(), group.get("Id").toString(), new DatabaseFuncs.OptionListener() {
                            @Override
                            public void onOptionPicked() {
                                System.out.println("Invite Denied");
                            }
                        });
                    }

                    @Override
                    public void onAccept(Map group) {
                        PositionListener.super.onAccept(group);
                        db.acceptInvite(user.get("Id").toString(), group.get("Id").toString(), new DatabaseFuncs.OptionListener() {
                            @Override
                            public void onOptionPicked() {
                                System.out.println("Invite to "+group.get("Id").toString()+" Accepted");
                            }
                        });
                    }
                });
                b.rvInvites.setAdapter(a);
                b.rvInvites.setLayoutManager(new LinearLayoutManager(getContext()));
            }

            @Override
            public void getDeadline(Timestamp timestamp) {

            }
        });
        return b.getRoot();

    }
}