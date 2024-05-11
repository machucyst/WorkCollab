package com.example.workcollab;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.workcollab.databinding.FragmentAccountEditBinding;
import com.google.gson.Gson;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountEditFragment extends Fragment {

    Map user;
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

    public static AccountEditFragment newInstance(Map user,String condition) {
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString("condition",condition);
        args.putString("user", gson.toJson(user));
        AccountEditFragment f = new AccountEditFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            System.out.println(getArguments().getString("user") + "awjgoiaehgoaeig");
            user = gson.fromJson(getArguments().getString("user"), Map.class);
            condition = getArguments().getString("condition");
            conditionValue = condition;
        }


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentAccountEditBinding.inflate(inflater, container, false);
        b.editTextText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals(user.get(condition).toString())){
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
        b.editTextText.setText(user.get(condition).toString());
        b.saveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (condition.equals("Emai" +
                        "l")) {
                    System.out.println(condition+"awhjfgaoiehgaoi");
                    db.InitDB(b.editTextText.getText().toString(), new DatabaseFuncs.DataListener() {
                        @Override
                        public void onDataFound(Map user) {
                            Toast.makeText(requireContext(), "Email already taken", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void noDuplicateUser() {
                            db.UpdateEmail(user.get("Email").toString(), b.editTextText.getText().toString(), new DatabaseFuncs.UpdateListener() {
                                @Override
                                public void onUpdate(Map user) {
                                    System.out.println(user.get("Email").toString()+"2141240912");
                                    listener.onUpdatedEmail(user);
                                    requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), AccountFragment.newInstance(user)).addToBackStack(null).commit();

                                }
                            });
                        }
                    });
                }else{
                    db.UpdateAccount(user.get("Email").toString(), b.editTextText.getText().toString(), condition, new DatabaseFuncs.UpdateListener() {
                        @Override
                        public void onUpdate(Map user) {
                            requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), AccountFragment.newInstance(user)).addToBackStack(null).commit();
                        }
                    });
                }

            }
        });
        return b.getRoot();
    }
}