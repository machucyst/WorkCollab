package com.example.workcollab.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import com.example.workcollab.DatabaseFuncs;
import com.example.workcollab.PublicMethods;
import com.example.workcollab.R;
import com.example.workcollab.activities.MainMenuActivity;
import com.example.workcollab.databinding.FragmentSubmitTaskBinding;
import com.google.gson.Gson;

import java.util.Map;


public class SubmitTaskFragment extends Fragment {
    Map<String,Object>task;
    Gson gson = new Gson();
    FragmentSubmitTaskBinding b;
    onSubmitClick listener;
    PublicMethods pb = new PublicMethods();
    Uri fileUri;
    DatabaseFuncs db = new DatabaseFuncs();
    public interface onSubmitClick{
        void onSubmitClicked(Map<String,Object>task);
    }
    public static SubmitTaskFragment newInstance(Map<String,Object>task) {
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString("task", gson.toJson(task));
        SubmitTaskFragment f = new SubmitTaskFragment();
        f.setArguments(args);
        return f;
    }
    public static SubmitTaskFragment newInstance(Map<String,Object>task, Uri fileUri) {
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString("file",fileUri.toString());
        args.putString("task", gson.toJson(task));
        SubmitTaskFragment f = new SubmitTaskFragment();
        f.setArguments(args);
        return f;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof SubmitTaskFragment.onSubmitClick) {
            listener = (SubmitTaskFragment.onSubmitClick) context;
        } else {
            throw new RuntimeException(context
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            System.out.println(getArguments().getString("user") + "awjgoiaehgoaeig");
            task = gson.fromJson(getArguments().getString("task"), Map.class);
            if(getArguments().getString("file")!=null){
                fileUri = Uri.parse(getArguments().getString("file"));
                System.out.println(23);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentSubmitTaskBinding.inflate(inflater,container,false);
        b.tvTaskName.setText(String.valueOf(task.get("TaskName"))
        );
        b.etTD.setText(String.valueOf(task.get("TaskDescription")));
        try{
        b.tvFileName.setText(PublicMethods.getFileName(fileUri,getContext()));
        }catch (Exception ex){

        }
        b.btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(task);
                listener.onSubmitClicked(task);
            }
        });
        b.btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
        b.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename = b.tvFileName.getText().toString();
                if(fileUri == null){
                    Toast.makeText(getContext(), "Upload a file before submitting",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(b.tvFileName.getText().toString().isEmpty()){
                    filename = MainMenuActivity.user.get("Username") + "'s task";
                }
                if(b.tvFileName.getText().toString().matches(".*[~\"#%&*:<>?/{|}].*")){
                    Toast.makeText(getContext(),"Invalid File Name",Toast.LENGTH_SHORT).show();
                    return;
                }
                b.btnSubmit.setBackground(AppCompatResources.getDrawable(requireContext(),R.drawable.textholderdisabled));
                b.btnSubmit.setEnabled(false);
                db.submitTask(MainMenuActivity.user,
                        fileUri,
                        String.valueOf(task.get("ParentId")),
                        String.valueOf(task.get("Id")),
                        filename+PublicMethods.getFileType(fileUri,getContext()),
                        b.btnSubmit,
                        requireContext(),
                        new DatabaseFuncs.BasicListener() {
                            @Override
                            public void basicListener() {
                                Toast.makeText(getContext(),"File Submitted Successfully",Toast.LENGTH_SHORT).show();
                                db.getGroupData(task.get("ParentId").toString(), new DatabaseFuncs.DataListener() {
                                    @Override
                                    public void onDataFound(Map<String,Object>user) {
                                        MainMenuActivity.selectedGroup = user;
                                        int vgId = ((ViewGroup) (requireView().getParent())).getId();
                                        pb.replaceFragment(requireActivity(),SelectedGroupFragment.newInstance(MainMenuActivity.selectedGroup),vgId);
                                    }

                                    @Override
                                    public void noDuplicateUser() {

                                    }
                                });
                            }
                         }
                );
            }
        });
        return b.getRoot();
    }

}