package com.example.workcollab.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.workcollab.DatabaseFuncs;
import com.example.workcollab.R;
import com.example.workcollab.databinding.ActivitySetupAccountBinding;
import com.google.firebase.FirebaseApp;

import java.util.Arrays;
import java.util.Map;

public class SetupAccountActivity extends AppCompatActivity {
    DatabaseFuncs db = new DatabaseFuncs();
    String userEmail,userPassword,userName;
    ActivityResultLauncher<String> mGetCont;
    Uri resultUri = null;
    ActivitySetupAccountBinding b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        b = DataBindingUtil.setContentView(this, R.layout.activity_setup_account);

        Intent intent = getIntent();
        Bundle bu = intent.getExtras();

        mGetCont = registerForActivityResult(new ActivityResultContracts.GetContent(), o -> {
            Intent a = new Intent(SetupAccountActivity.this, CropperActivity.class);
            a.putExtra("DATA", o.toString());
            startActivityForResult(a, 101);
        });
        b.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetCont.launch("image/*");
            }
        });

        b.btnShowPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (resultUri == null) {
                    Toast.makeText(SetupAccountActivity.this, "Pick a profile image", Toast.LENGTH_SHORT).show();
                    return; // TODO: if uri is null, itll crash
                }

                db.CreateAccount(bu.getString("user-name"), bu.getString("user-password"), bu.getString("user-email"), StrValOf(b.etCN), new DatabaseFuncs.UpdateListener() {
                    @Override
                    public void onUpdate(Map user) {
                        db.SaveProfile(user, resultUri, new DatabaseFuncs.UpdateListener() {
                            @Override
                            public void onUpdate(Map user) {
                                System.out.println("Profile saved");
                            }
                        });

                        String[] Machu = new String[1];
                        Machu[0] = user.get("Id").toString();
                        db.CreateGroup(user.get("Username").toString() + "'s Group", Arrays.asList(Machu), new DatabaseFuncs.UpdateListener() {
                            @Override
                            public void onUpdate(Map user) {
                                System.out.println("Group Created");
                            }
                        });

                        Intent toMenu = new Intent(SetupAccountActivity.this, MainMenuActivity.class);
                        toMenu.putExtra("Email", user.get("Email").toString());
                        if (bu.getBoolean("StayLogIn")) {
                            stayLogIn(user.get("Email").toString());
                        }
                        complete();
                        startActivity(toMenu);
                        finish();

                    }
                });
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(resultCode + " " + requestCode);
        if (resultCode == -1 && requestCode == 101) {
            String result = data.getStringExtra("RESULT");
            resultUri = null;
            if (result != null) {
                resultUri = Uri.parse(result);
                Glide.with(this).asBitmap().load(resultUri).into(b.profileImage);

            }
        }
    }

    private void stayLogIn(String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserLogInPreferences", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("user-email", email).apply();
    }
    private void complete(){
        SharedPreferences sharedPreferences = getSharedPreferences("UserLogInPreferences", Context.MODE_PRIVATE);
        sharedPreferences.edit().remove("incomplete").apply();
    }
    private String StrValOf(TextView view){
        return view.getText().toString();
    }
}

