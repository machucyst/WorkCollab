package com.example.workcollab.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.workcollab.DatabaseFuncs;
import com.example.workcollab.R;
import com.example.workcollab.activities.MainMenuActivity;
import com.example.workcollab.databinding.DialogLogoutConfirmBinding;
import com.example.workcollab.databinding.DialogTextInputBinding;
import com.example.workcollab.databinding.FragmentAccountBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AccountFragment extends Fragment {
//    Map user;
    Gson gson = new Gson();
    FragmentAccountBinding b;
    DatabaseFuncs db = new DatabaseFuncs();
    public AccountFragment() {

    }

    public static AccountFragment newInstance() {
        Bundle args = new Bundle();
        Gson gson = new Gson();
//        args.putString("user", gson.toJson(user));
        AccountFragment f = new AccountFragment();
        f.setArguments(args);
        return f;
    }
    public interface ButtonListeners{
        void onPressChangePFP();
        void onDeletedAccount();

        void onLogOutPress();
    }
    DialogTextInputBinding dtb;
    DialogLogoutConfirmBinding dlc;
    private AccountFragment.ButtonListeners listener;
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

    private void menuTextChange(NavigationView nv, int ItemId,String text){
        TextView a = (nv.getMenu().findItem(ItemId).getActionView().findViewById(R.id.additionalText));
        a.setText(text);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(user == null || (user != null && getArguments() != null)) {
//            System.out.println(getArguments().getString("user") + "awjgoiaehgoaeig");
//            user = gson.fromJson(getArguments().getString("user"), Map.class);
//
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentAccountBinding.inflate(inflater,container,false);
        menuTextChange(b.nvAccountMenu,R.id.menu_password, "");
        menuTextChange(b.nvAccountMenu, R.id.menu_deleteAccount,"");
        menuTextChange(b.nvAccountMenu, R.id.menu_profilePicture,"");
        b.nvAccountMenu.getMenu().findItem(R.id.menu_email).setEnabled(false);
        TextView a = b.nvAccountMenu.getMenu().findItem(R.id.menu_email).getActionView().findViewById(R.id.additionalText);
        a.setTextColor(AppCompatResources.getColorStateList(getContext(),R.color.stroke_gray));
        ImageView v = b.nvAccountMenu.getMenu().findItem(R.id.menu_deleteAccount).getActionView().findViewById(R.id.nextMenuArrow);
        v.setColorFilter(ContextCompat.getColor(getContext(), R.color.warning));
        Spannable s = new SpannableString(b.nvAccountMenu.getMenu().findItem(R.id.menu_deleteAccount).getTitle().toString());
        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.warning)), 0, s.length(), 0);
        b.nvAccountMenu.getMenu().findItem(R.id.menu_deleteAccount).setTitle(s);

        db.InitDB(MainMenuActivity.user.get("Email").toString(), new DatabaseFuncs.DataListener() {
            @Override
            public void onDataFound(Map user) {
                menuTextChange(b.nvAccountMenu,R.id.menu_username, "\"" + user.get("Username").toString() + "\"");
                menuTextChange(b.nvAccountMenu,R.id.menu_contactNumber, "\"" + user.get("ContactNumber").toString() + "\"");
                menuTextChange(b.nvAccountMenu,R.id.menu_email, "\"" + user.get("Email").toString() + "\"");
            }
            @Override
            public void noDuplicateUser() {

            }
        });
        b.nvAccountMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                db.InitDB(MainMenuActivity.user.get("Email").toString(), new DatabaseFuncs.DataListener() {
                    @Override
                    public void onDataFound(Map user) {
                        int a = menuItem.getItemId();
                        if (a == R.id.menu_username) {
                            MainMenuActivity.selected ="username";
                            MainMenuActivity.backFlow.push("username");
                            requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), AccountEditFragment.newInstance("Username")).addToBackStack(null).commit();

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
                                if(dtb.editText.getText().toString().equals(DecryptPassword(user.get("Password").toString()))){
                                    MainMenuActivity.selected ="password";
                                    MainMenuActivity.backFlow.push("password");
                                    requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), AccountEditFragment.newInstance("Password")).addToBackStack(null).commit();
                                    dialog.dismiss();
                                }else{
                                    Toast.makeText(requireContext(),"Incorrect Password",Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }

                            });
                            dialog.show();
                        }
                        if (a == R.id.menu_contactNumber){
                            MainMenuActivity.selected ="contactNumber";
                            MainMenuActivity.backFlow.push("contactNumber");
                            requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), AccountEditFragment.newInstance("ContactNumber")).addToBackStack(null).commit();
                        }
                        if (a == R.id.menu_profilePicture){
                            MainMenuActivity.selected ="pfp";
                            MainMenuActivity.backFlow.push("pfp");
                            listener.onPressChangePFP();
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
        b.nvSettings.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int a = menuItem.getItemId();
                if (a == R.id.menu_logOut){
                    listener.onLogOutPress();
                    return true;
                }
                if (a == R.id.menu_settings) {
                    MainMenuActivity.selected ="settings";
                    MainMenuActivity.backFlow.push("settings");
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), SettingsFragment.newInstance()).addToBackStack(null).commit();
                    return true;
                }
                if (a == R.id.menu_appearance) {
                    MainMenuActivity.selected ="appearance";
                    MainMenuActivity.backFlow.push("appearance");
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), AppearanceFragment.newInstance()).addToBackStack(null).commit();
                    return true;
                }
//                if (a == R.id.menu_account){
//                    MainMenuActivity.selected ="NotAccount";
//                    MainMenuActivity.backFlow.push("NotAccount");
//                    requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), AccountFragment.newInstance()).addToBackStack(null).commit();
//                    return true;
//                }
                return false;
            }
        });
        b.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPressChangePFP();
            }
        });
        try {
            Glide.with(getContext()).asBitmap().load(Uri.parse(MainMenuActivity.user.get("Profile").toString())).into(b.profileImage);

            CustomTarget<Bitmap> bitmap = Glide.with(requireContext()).asBitmap().load(MainMenuActivity.user.get("Profile").toString()).into(new CustomTarget<Bitmap>() {

                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    Palette p = Palette.from(resource).generate();
                    int colorWhite = getResources().getColor(R.color.white, getActivity().getTheme());
                    switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                        case Configuration.UI_MODE_NIGHT_YES:
                            b.profileBackground.setBackgroundColor(p.getDarkVibrantColor(colorWhite));
                            break;
                        case Configuration.UI_MODE_NIGHT_NO:
                            b.profileBackground.setBackgroundColor(p.getVibrantColor(colorWhite));
                            break;
                    }
                }
                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {

                }
            });
        }catch (Exception ex){

        }
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
}