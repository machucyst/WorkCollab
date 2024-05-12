package com.example.workcollab;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.workcollab.databinding.DialogLogoutConfirmBinding;
import com.example.workcollab.databinding.DialogTextInputBinding;
import com.example.workcollab.databinding.FragmentAccountBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AccountFragment extends Fragment{

    public AccountFragment() {

    }

    int x;
    FragmentAccountBinding b;
    DatabaseFuncs db = new DatabaseFuncs();
    DialogTextInputBinding dtb;
    DialogLogoutConfirmBinding dlc;
    Map user;
    Gson gson = new Gson();
    private AccountFragment.ButtonListeners listener;

    public AccountFragment(Map user) {
        this.user = user;
    }

    public static AccountFragment newInstance(Map user) {
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString("user", gson.toJson(user));
        AccountFragment f = new AccountFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AccountFragment.ButtonListeners) {
            listener = (AccountFragment.ButtonListeners) context;
        } else {
            throw new RuntimeException(context
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentAccountBinding.inflate(inflater,container,false);
        menuTextChange(R.id.menu_password, "");
        menuTextChange(R.id.menu_profilePicture, "");
        menuTextChange(R.id.menu_deleteAccount, "");
        ImageView v = (ImageView) b.nvAccountMenu.getMenu().findItem(R.id.menu_deleteAccount).getActionView().findViewById(R.id.nextMenuArrow);
        v.setColorFilter(ContextCompat.getColor(getContext(), R.color.warning));
        Spannable s = new SpannableString(b.nvAccountMenu.getMenu().findItem(R.id.menu_deleteAccount).getTitle().toString());
        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.warning)), 0, s.length(), 0);
        b.nvAccountMenu.getMenu().findItem(R.id.menu_deleteAccount).setTitle(s);

        db.InitDB(user.get("Email").toString(), new DatabaseFuncs.DataListener() {
            @Override
            public void onDataFound(Map user) {
                menuTextChange(R.id.menu_username, "\"" + user.get("Username").toString() + "\"");
                menuTextChange(R.id.menu_contactNumber, "\"" + user.get("ContactNumber").toString() + "\"");
                menuTextChange(R.id.menu_email, "\"" + user.get("Email").toString() + "\"");
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
                        if (a == R.id.menu_deleteAccount) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                            dlc = DialogLogoutConfirmBinding.inflate(getLayoutInflater().from(requireContext()));
                            builder.setView(dlc.getRoot());
                            AlertDialog dialog = builder.create();
                            dlc.editAccount.setText("THIS PROCESS CANNOT BE UNDONE");
                            dlc.Cancel.setOnClickListener(k -> {
                                dialog.dismiss();
                            });
//                            dlc.Ok.setEnabled(false);
                            dlc.Ok.setOnClickListener(k -> {
                                dialog.dismiss();
                                dtb = DialogTextInputBinding.inflate(getLayoutInflater().from(requireContext()));
                                builder.setView(dtb.getRoot());
                                AlertDialog qwerty = builder.create();
                                dtb.editAccount.setText("Please input password before proceeding");
                                dtb.Ok.setEnabled(false);
                                dtb.Ok.setHint("This process cannot be undone!");
                                dtb.Ok.setBackground(getResources().getDrawable(R.drawable.textholderdisabled));
                                dtb.Ok.setHeight(90);
                                dtb.Ok.setWidth(90);
                                dtb.Cancel.setWidth(90);
                                dtb.Cancel.setHeight(90);
                                dtb.Cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        qwerty.dismiss();
                                    }
                                });
                                qwerty.setOnShowListener(new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface dialog) {
                                        new CountDownTimer(6000, 100) {

                                            @Override
                                            public void onTick(long millisUntilFinished) {
                                                System.out.println(millisUntilFinished);
                                                dtb.Ok.setText(String.valueOf(TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)));
                                            }

                                            @Override
                                            public void onFinish() {
                                                dtb.Ok.setBackground(getResources().getDrawable(R.drawable.textholderwarning));
                                                dtb.Ok.setTextColor(getResources().getColor(R.color.white));
                                                dtb.Ok.setText("Delete");
                                                dtb.Ok.setEnabled(true);
                                            }
                                        }.start();
                                    }
                                });
                                dtb.Ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        db.DeleteAccount(user.get("Id").toString(), new DatabaseFuncs.DeleteListener() {
                                            @Override
                                            public void onDelete() {
                                                listener.onDeletedAccount();
                                            }
                                        });
                                    }
                                });
                                qwerty.show();
                            });
                            dialog.show();
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            System.out.println(getArguments().getString("user") + "awjgoiaehgoaeig");
            user = gson.fromJson(getArguments().getString("user"), Map.class);
        }

    }


    public interface ButtonListeners {
        void onDeletedAccount();
    }
    private void menuTextChange(int ItemId,String text){
        TextView a = (b.nvAccountMenu.getMenu().findItem(ItemId).getActionView().findViewById(R.id.additionalText));
        a.setText(text);
    }

}