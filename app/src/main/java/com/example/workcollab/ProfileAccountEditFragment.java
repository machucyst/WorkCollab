package com.example.workcollab;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.workcollab.databinding.FragmentProfileAccountEditBinding;
import com.google.gson.Gson;

import java.util.Map;

public class ProfileAccountEditFragment extends Fragment {
    Map user;
    Gson gson = new Gson();
    FragmentProfileAccountEditBinding b;
    DatabaseFuncs db = new DatabaseFuncs();
    public ProfileAccountEditFragment() {

    }

    public static ProfileAccountEditFragment newInstance(Map user) {
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString("user", gson.toJson(user));
        ProfileAccountEditFragment f = new ProfileAccountEditFragment();
        f.setArguments(args);
        return f;
    }
    public interface ButtonListeners{
        void onPressChangePFP();
    }
    private ProfileAccountEditFragment.ButtonListeners listener;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProfileAccountEditFragment.ButtonListeners) {
            listener = (ProfileAccountEditFragment.ButtonListeners) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(user == null || (user != null && getArguments() != null)) {
            System.out.println(getArguments().getString("user").toString() + "awjgoiaehgoaeig");
            user = gson.fromJson(getArguments().getString("user"), Map.class);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentProfileAccountEditBinding.inflate(inflater,container,false);
        b.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPressChangePFP();
            }
        });
        try {
            Glide.with(getContext()).asBitmap().load(Uri.parse(user.get("Profile").toString())).into(b.profileImage);

            CustomTarget<Bitmap> bitmap = Glide.with(requireContext()).asBitmap().load(user.get("Profile").toString()).into(new CustomTarget<Bitmap>() {

                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    Palette p = Palette.from(resource).generate();
                    int colorWhite = getResources().getColor(R.color.white, getActivity().getTheme());
                    switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                        case Configuration.UI_MODE_NIGHT_YES:
                            b.profileBackgroundView.setBackgroundColor(p.getDarkVibrantColor(colorWhite));
                            break;
                        case Configuration.UI_MODE_NIGHT_NO:
                            b.profileBackgroundView.setBackgroundColor(p.getVibrantColor(colorWhite));
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
}