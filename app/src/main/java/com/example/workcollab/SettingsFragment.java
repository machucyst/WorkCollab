package com.example.workcollab;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.workcollab.databinding.DialogLogoutConfirmBinding;
import com.example.workcollab.databinding.DialogTextInputBinding;
import com.example.workcollab.databinding.FragmentSettingsBinding;
import com.example.workcollab.databinding.FragmentSettingsBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.util.Map;


public class SettingsFragment extends Fragment{

    Map user;
    Menu menu;
    FragmentSettingsBinding b;
    DialogTextInputBinding dtb;
    ActivityResultLauncher<String> mGetCont;
    Bitmap bitmap;
    MainMenuActivity acty;
    Uri fileUri;
    Gson gson = new Gson();
    DialogLogoutConfirmBinding bl;

    public interface ButtonListeners{
        void onPress();
        void onLogOutPress();
    }
    private  ButtonListeners listener;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ButtonListeners) {
            listener = (ButtonListeners) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    DatabaseFuncs db = new DatabaseFuncs();
    public SettingsFragment() {

    }

    public static SettingsFragment newInstance(Map user) {
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString("user", gson.toJson(user));
        SettingsFragment f = new SettingsFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(user == null || (user != null && getArguments() != null)) {
            System.out.println(getArguments().getString("user").toString() + "awjgoiaehgoaeig");
            user = gson.fromJson(getArguments().getString("user"), Map.class);

        }
        mGetCont=registerForActivityResult(new ActivityResultContracts.GetContent(), o ->{
            Intent intent = new Intent(getActivity(), CropperActivity.class);
            intent.putExtra("DATA",o.toString());
            startActivityForResult(intent,101);
        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        b = FragmentSettingsBinding.inflate(inflater,container,false);


        db.InitDB(user.get("Email").toString(), new DatabaseFuncs.DataListener() {
            @Override
            public void onDataFound(Map user) {
                b.tvUsername.setText(user.get("Username").toString());
                b.tvAccount.setText(user.get("Email").toString());
                b.profileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainMenuActivity.selected = "NotProfile";
                        requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), ProfileAccountEditFragment.newInstance(user)).addToBackStack(null).commit();

                    }
                });
                try{
                    Glide.with(getContext()).asBitmap().load(Uri.parse(user.get("Profile").toString())).into(b.profileImage);
                    CustomTarget<Bitmap> bitmap = Glide.with(requireContext()).asBitmap().load(user.get("Profile").toString()).into(new CustomTarget<Bitmap>() {

                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            Palette p = Palette.from(resource).generate();
                            int colorWhite = getResources().getColor(R.color.white, getActivity().getTheme());
                            switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK){
                                case Configuration.UI_MODE_NIGHT_YES:
                                    b.profileBackgroundView.setBackgroundColor(p.getDarkVibrantColor(colorWhite));
                                    b.tvUsername.setTextColor(p.getLightMutedColor(colorWhite));
                                    b.tvAccount.setTextColor(p.getLightMutedColor(colorWhite));
                                    break;
                                case Configuration.UI_MODE_NIGHT_NO:
                                    b.profileBackgroundView.setBackgroundColor(p.getVibrantColor(colorWhite));
                                    b.tvUsername.setTextColor(p.getDarkMutedColor(colorWhite));
                                    b.tvAccount.setTextColor(p.getDarkMutedColor(colorWhite));
                                    break;
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
                } catch (Exception ex){
                    ex.printStackTrace();
                }

            }

            @Override
            public void noDuplicateUser() {

            }
        });
        return b.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        b.nvAccountInfo.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int a = menuItem.getItemId();
                if (a == R.id.menu_logOut){
                    listener.onLogOutPress();
                    return true;
                }
                if (a == R.id.menu_account){
                    MainMenuActivity.selected ="NotAccount";
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), AccountFragment.newInstance(user)).addToBackStack(null).commit();
                    return true;
                }
                return false;
            }
        });

    }
    public void onButtonPress(MainMenuActivity acty){
        this.acty = acty;
    }
}