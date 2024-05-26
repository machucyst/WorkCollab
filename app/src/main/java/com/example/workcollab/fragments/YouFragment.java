package com.example.workcollab.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.workcollab.databinding.FragmentYouBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.util.Map;


public class YouFragment extends Fragment {

    Map user;
    Menu menu;
    FragmentYouBinding b;
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
            throw new RuntimeException(context
                    + " must implement OnFragmentInteractionListener");
        }
    }

    DatabaseFuncs db = new DatabaseFuncs();

    public YouFragment() {

    }

    public static YouFragment newInstance(Map user) {
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString("user", gson.toJson(user));
        YouFragment f = new YouFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(user == null || (user != null && getArguments() != null)) {
//            System.out.println(getArguments().getString("user") + "awjgoiaehgoaeig");
//            user = gson.fromJson(getArguments().getString("user"), Map.class);
//        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        b = FragmentYouBinding.inflate(inflater, container, false);
        menuTextChange(R.id.menu_settings, "Settings");
        menuTextChange(R.id.menu_account, "Account");
        menuTextChange(R.id.menu_appearance, "Appearance");
        menuTextChange(R.id.menu_logOut, "Log Out");


        db.InitDB(MainMenuActivity.user.get("Email").toString(), new DatabaseFuncs.DataListener() {
            @Override
            public void onDataFound(Map user) {
                b.tvUsername.setText(user.get("Username").toString());
                b.tvAccount.setText(user.get("Email").toString());
                b.profileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainMenuActivity.selected = "NotProfile";
                        requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), ProfileAccountEditFragment.newInstance()).addToBackStack(null).commit();

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
                if (a == R.id.menu_settings) {
                    MainMenuActivity.selected ="settings";
                    MainMenuActivity.backFlow.push("settings");
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), new SettingsFragment(user)).addToBackStack(null).commit();
                    return true;
                }
                if (a == R.id.menu_appearance) {
                    //TODO: change themes
                }
                if (a == R.id.menu_account){
                    MainMenuActivity.selected ="NotAccount";
                    MainMenuActivity.backFlow.push("NotAccount");
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) (getView().getParent())).getId(), AccountFragment.newInstance()).addToBackStack(null).commit();
                    return true;
                }
                return false;
            }
        });

    }
    public void onButtonPress(MainMenuActivity acty){
        this.acty = acty;
    }

    private void menuTextChange(int ItemId, String text) {
        TextView a = (b.nvAccountInfo.getMenu().findItem(ItemId).getActionView().findViewById(R.id.additionalText));
        a.setText(text);
    }
}