package com.example.workcollab.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.workcollab.DatabaseFuncs;
import com.example.workcollab.R;
import com.example.workcollab.databinding.FragmentDialogBottomBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Map;

public class DialogBottom extends BottomSheetDialogFragment {
    FragmentDialogBottomBinding b;
    DatabaseFuncs db =new DatabaseFuncs();
    String id;
    public DialogBottom(String id){
        this.id = id;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
           id = getArguments().getString("id");
        }
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);

    }
    TextView getAction(int id){
        return b.nvAccountMenu.getMenu().findItem(id).getActionView().findViewById(R.id.additionalText);
    }
    @SuppressLint("ResourceAsColor")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        b = DataBindingUtil.inflate(LayoutInflater.from(getContext()),R.layout.fragment_dialog_bottom,null,false);
        b.profileBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        db.getUserById(id, new DatabaseFuncs.DataListener() {
            @Override
            public void onDataFound(Map user) {
                Glide.with(getContext()).asBitmap().load(Uri.parse(String.valueOf(user.get("Profile")))).into(b.profileImage);
                getAction(R.id.view_contactnumber).setText(user.get("ContactNumber").toString());
                getAction(R.id.view_email).setText(user.get("Email").toString());
                getAction(R.id.view_username).setText(user.get("Username").toString());
            }

            @Override
            public void noDuplicateUser() {

            }
        });
        bottomSheetDialog.setContentView(b.getRoot());
        return bottomSheetDialog;
    }
}