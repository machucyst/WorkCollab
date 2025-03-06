package com.example.workcollab.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.workcollab.DatabaseFuncs;
import com.example.workcollab.PublicMethods;
import com.example.workcollab.R;
import com.example.workcollab.adapters.MembersAdapter;
import com.example.workcollab.databinding.DialogDateInputBinding;
import com.example.workcollab.databinding.FragmentAssignTaskBinding;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AssignTaskFragment extends Fragment {
    Gson gson = new Gson();
    Map<String,Object> group;
    DatabaseFuncs db = new DatabaseFuncs();
    Calendar today = Calendar.getInstance();
    long deadline;
    DialogDateInputBinding di;
    PublicMethods pb = new PublicMethods();
    MembersAdapter ma;
    FragmentAssignTaskBinding b;

    public static List<String> members = new ArrayList<>();

    public interface PositionListener{
        default void itemClicked(String id){}
    }

    public static AssignTaskFragment newInstance(Map<String,Object> group) {
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString("user", gson.toJson(group));
        AssignTaskFragment f = new AssignTaskFragment();
        f.setArguments(args);
        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            System.out.println(getArguments().getString("user") + "awjgoiaehgoaeig");
            group = gson.fromJson(getArguments().getString("user"), Map.class);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentAssignTaskBinding.inflate(inflater,container,false);
        db.getMembers(group.get("Id").toString(), new DatabaseFuncs.MembersListener() {
            @Override
            public void onReceiveMembers(List<Map<String,Object>> members) {
                System.out.println(members);
               ma = new MembersAdapter(members, requireContext(), new PositionListener() {
                   @Override
                   public void itemClicked(String id) {
                       PositionListener.super.itemClicked(id);
                   }
               });
               b.rvMembers.setAdapter(ma);
               b.rvMembers.setLayoutManager(new LinearLayoutManager(getContext()));
               b.rvMembers.setNestedScrollingEnabled(true);
            }
        });
        b.tilUntil.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                di = DialogDateInputBinding.inflate(getLayoutInflater().from(requireContext()));
                builder.setView(di.getRoot());
                AlertDialog dialog = builder.create();
                today.set(Calendar.HOUR_OF_DAY, 0);
                di.calendarView.setDate(today.getTimeInMillis());
                di.Cancel.setOnClickListener(k -> {
                    dialog.dismiss();

                });
                di.calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                        Calendar ca = Calendar.getInstance();
                        ca.set(year,month,dayOfMonth,0,0,0);
                        ca.set(Calendar.MILLISECOND,0);
                        deadline = ca.getTimeInMillis();
                    }
                });
                di.Ok.setOnClickListener(k -> {
                    if(deadline<today.getTimeInMillis()){
                        Toast.makeText(requireContext(),"Deadline invalid",Toast.LENGTH_SHORT).show();
                    }else{
                        SimpleDateFormat sdf = new SimpleDateFormat("M/dd hh:mm a", Locale.getDefault());
                        b.etUntil.setText(sdf.format(new Date(deadline)));
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        b.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String TaskName = String.valueOf(b.etTN.getText());
                System.out.println(today.getTimeInMillis());
                System.out.println(deadline);
                b.btnSubmit.setBackgroundDrawable(AppCompatResources.getDrawable(requireActivity(), R.drawable.textholderdisabled));
                b.btnSubmit.setText(R.string.loading);
                b.btnSubmit.setEnabled(false);
                if(today.getTimeInMillis()<deadline){
                    if(!TaskName.isEmpty()){
                        if(!String.valueOf(b.etTD.getText()).isEmpty()){
                             db.createTask(String.valueOf(group.get("Id")),
                                     members,
                                     TaskName,
                                     String.valueOf(b.etTD.getText()),
                                     deadline,
                                     b.btnSubmit,
                                     requireContext(),
                                     new DatabaseFuncs.CreateTaskListener() {
                                         @Override
                                         public void onCreateTaskListener() {
                                             Toast.makeText(getContext(),"Task Assigned Successfully",Toast.LENGTH_SHORT).show();
                                             int vgId = ((ViewGroup)(requireView().getParent())).getId();
                                             pb.replaceFragment(requireActivity(),SelectedGroupFragment.newInstance(group),vgId);
                                         }
                                     });
                                    return;
                        }
                            Toast.makeText(requireContext(),"Please add description to task",Toast.LENGTH_SHORT).show();
                        enableButtons();
                        return;
                    }
                    Toast.makeText(requireContext(),"Please input task name",Toast.LENGTH_SHORT).show();
                    enableButtons();
                return;
                }
                Toast.makeText(requireContext(),"Deadline Invalid",Toast.LENGTH_SHORT).show();

            }
        });


        return b.getRoot();
    }
    private void enableButtons(){
        b.btnSubmit.setBackgroundDrawable(AppCompatResources.getDrawable(requireActivity(), R.drawable.textholder));
        b.btnSubmit.setText(R.string.submit);
        b.btnSubmit.setEnabled(true);
    }
}