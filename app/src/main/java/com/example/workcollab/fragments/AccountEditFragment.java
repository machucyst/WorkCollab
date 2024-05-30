package com.example.workcollab.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.workcollab.DatabaseFuncs;
import com.example.workcollab.activities.MainMenuActivity;
import com.example.workcollab.databinding.FragmentAccountEditBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.Map;


public class AccountEditFragment extends Fragment {

//    Map user;
    FragmentAccountEditBinding b;
    DatabaseFuncs db =new DatabaseFuncs();
    Gson gson = new Gson();
    String condition;
    String conditionValue = condition;

    UpdateListener listener;
    public interface UpdateListener{
        void onUpdatedEmail(Map user);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AccountEditFragment.UpdateListener) {
            listener = (AccountEditFragment.UpdateListener) context;
        } else {
            throw new RuntimeException(context
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public AccountEditFragment() {

    }

    public static AccountEditFragment newInstance(String condition) {
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString("condition",condition);
//        args.putString("user", gson.toJson(user));
        AccountEditFragment f = new AccountEditFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            System.out.println(getArguments().getString("user") + "awjgoiaehgoaeig");
//            user = gson.fromJson(getArguments().getString("user"), Map.class);
            condition = getArguments().getString("condition");
            conditionValue = condition;
        }


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentAccountEditBinding.inflate(inflater, container, false);
        b.backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainMenuActivity.backFlow.pop();
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
        b.editTextText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals(MainMenuActivity.user.get(condition).toString())){
                    b.saveEdit.setVisibility(View.GONE);
                }else{
                    b.saveEdit.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        if (condition.equals("ContactNumber")){
            conditionValue = "Contact Number";
            b.editTextText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_TEXT_VARIATION_NORMAL);
        }
        b.editHeader.setText("Edit "+ conditionValue);
        b.tvHelper.setText(conditionValue);
        if(condition.equals("Password")){
            b.editTextText.setText(DecryptPassword(MainMenuActivity.user.get(condition).toString()));
        }else{
            b.editTextText.setText(MainMenuActivity.user.get(condition).toString());
        }
        b.saveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (condition.equals("Email")) {
                    System.out.println(condition+"awhjfgaoiehgaoi");
                    db.InitDB(b.editTextText.getText().toString(), new DatabaseFuncs.DataListener() {
                        @Override
                        public void onDataFound(Map user) {
                            Toast.makeText(requireContext(), "Email already taken", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void noDuplicateUser() {

                            db.updateEmail(MainMenuActivity.user.get("Email").toString(), EncryptPassword(b.editTextText.getText().toString()), new DatabaseFuncs.UpdateListener() {
                                @Override
                                public void onUpdate(Map user) {
                                    System.out.println(user.get("Email").toString()+"2141240912");
                                    listener.onUpdatedEmail(user);
                                    requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), AccountFragment.newInstance()).addToBackStack(null).commit();

                                }
                            });
                        }
                    });
                }else{
                    String pass = b.editTextText.getText().toString();
                    if (condition.equals("Password")){
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        FirebaseUser user = mAuth.getCurrentUser();
                            user.updatePassword(EncryptPassword(pass)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                }
                            });
                    }
                    db.updateAccount(MainMenuActivity.user.get("Email").toString(), pass, condition, new DatabaseFuncs.UpdateListener() {
                        @Override
                        public void onUpdate(Map user) {
                            MainMenuActivity.user = user;
                            requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), AccountFragment.newInstance()).addToBackStack(null).commit();
                        }
                    });
                }

            }
        });

        return b.getRoot();
    }

    private String DecryptPassword(String password){
        char[] encpass = password.toCharArray();
        StringBuilder pass = new StringBuilder();
        for(char c: encpass){
            c-=7;
            pass.append(c);
        }
        return pass.toString();
    }
    private String EncryptPassword(String password){
        char[] encpass = password.toCharArray();
        StringBuilder pass = new StringBuilder();
        for(char c: encpass){
            c+=7;
            pass.append(c);
        }
        return pass.toString();
    }
}