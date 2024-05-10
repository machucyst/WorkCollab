package com.example.workcollab;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.workcollab.databinding.DialogTextInputBinding;
import com.example.workcollab.databinding.FragmentAccountBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.util.Map;

public class AccountFragment extends Fragment{

    public AccountFragment() {

    }

    FragmentAccountBinding b;
    DatabaseFuncs db = new DatabaseFuncs();
    DialogTextInputBinding dtb;
    Map user;
    Gson gson = new Gson();

    public static AccountFragment newInstance(Map user) {
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString("user", gson.toJson(user));
        AccountFragment f = new AccountFragment();
        f.setArguments(args);
        return f;
    }
    public AccountFragment(Map user){
        this.user = user;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            System.out.println(getArguments().getString("user").toString() + "awjgoiaehgoaeig");
            user = gson.fromJson(getArguments().getString("user"), Map.class);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentAccountBinding.inflate(inflater,container,false);
        db.InitDB(user.get("Email").toString(), new DatabaseFuncs.DataListener() {
            @Override
            public void onDataFound(Map user) {
                menuTextChange(R.id.menu_username,user.get("Username").toString());
                menuTextChange(R.id.menu_password,"");
                menuTextChange(R.id.menu_contactNumber,user.get("ContactNumber").toString());
                menuTextChange(R.id.menu_email,user.get("Email").toString());
                menuTextChange(R.id.menu_profilePicture,"");
            }

            @Override
            public void noDuplicateUser() {

            }
        });

        b.nvAccountMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                db.InitDB(user.get("Email").toString(), new DatabaseFuncs.DataListener() {
                    @Override
                    public void onDataFound(Map user) {
                        int a = menuItem.getItemId();
                        if (a == R.id.menu_username) {
                            requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), AccountEditFragment.newInstance(user, "Username")).addToBackStack(null).commit();

                        }
                        if (a == R.id.menu_password) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                            dtb = DialogTextInputBinding.inflate(getLayoutInflater().from(requireContext()));
                            builder.setView(dtb.getRoot());
                            AlertDialog dialog = builder.create();
                            dtb.editAccount.setText("Input old password before proceeding");
                            dtb.Cancel.setOnClickListener(k -> {
                                dialog.dismiss();

                            });
                            dtb.Ok.setOnClickListener(k -> {
                                if(dtb.editText.getText().toString().equals(user.get("Password"))){
                                    requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), AccountEditFragment.newInstance(user, "Password")).addToBackStack(null).commit();
                                    dialog.dismiss();
                                }else{
                                    Toast.makeText(requireContext(),"Incorrect Password",Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }

                            });
                            dialog.show();
                        }
                        if (a == R.id.menu_email){
                            requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), AccountEditFragment.newInstance(user, "Email")).addToBackStack(null).commit();

                        }
                        if (a == R.id.menu_contactNumber){
                            requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), AccountEditFragment.newInstance(user, "ContactNumber")).addToBackStack(null).commit();
                        }
                        if (a == R.id.menu_profilePicture){
                            requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), ProfileAccountEditFragment.newInstance(user)).addToBackStack(null).commit();

                        }
                    }

                    @Override
                    public void noDuplicateUser() {

                    }
                });
                return false;
            }
        });




        return b.getRoot();
    }
    private void menuTextChange(int ItemId,String text){
        TextView a = (b.nvAccountMenu.getMenu().findItem(ItemId).getActionView().findViewById(R.id.additionalText));
        a.setText(text);
    }

}