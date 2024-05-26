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
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.Map;

public class SetupAccountActivity extends AppCompatActivity {
    DatabaseFuncs db = new DatabaseFuncs();
    String userEmail,userPassword,userName;
    ActivityResultLauncher<String> mGetCont;
    Uri resultUri = null;
    SignInClient oneTapClient;
    BeginSignInRequest signInRequest;
    FirebaseAuth mAuth;
    ActivitySetupAccountBinding b;
    Bundle bu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        b = DataBindingUtil.setContentView(this, R.layout.activity_setup_account);
        mAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        bu = intent.getExtras();

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
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                .build();

        b.btnShowPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resultUri == null) {
                    Toast.makeText(SetupAccountActivity.this, "Pick a profile image", Toast.LENGTH_SHORT).show();
                    return; // TODO: if uri is null, itll crash
                }
                if (StrValOf(b.etCN).length() != 11) {
                    Toast.makeText(SetupAccountActivity.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                    return; //TODO: kys
                }
                db.registerAccount(mAuth, bu.getString("user-email"), bu.getString("user-password"), getApplicationContext(), new DatabaseFuncs.EmailAuthListener() {
                    @Override
                    public void changeLayout(boolean test) {
                        db.createAccount(bu.getString("user-name"), bu.getString("user-password"), bu.getString("user-email"), StrValOf(b.etCN), new DatabaseFuncs.UpdateListener() {
                            @Override
                            public void onUpdate(Map user) {
                                System.out.println("tesyseys");
                                db.saveProfile(user, resultUri, new DatabaseFuncs.UpdateListener() {
                                    @Override
                                    public void onUpdate(Map user) {
                                        System.out.println("Profile saved");
                                        String[] Machu = new String[1];
                                        Machu[0] = user.get("Id").toString();
                                        db.createGroup(user.get("Username").toString() + "'s Group", Arrays.asList(Machu), new DatabaseFuncs.UpdateListener() {
                                            @Override
                                            public void onUpdate(Map user) {
                                                System.out.println("Group Created");
                                                finish();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
                    }
                });

    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(resultCode + " " + requestCode);
        switch (requestCode){
            case 101:
                if (resultCode == -1) {
                    String result = data.getStringExtra("RESULT");
                    resultUri = null;
                    if (result != null) {
                        resultUri = Uri.parse(result);
                        Glide.with(this).asBitmap().load(resultUri).into(b.profileImage);

                    }
              }
            break;



        }
    }

    private void stayLogIn(String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserLogInPreferences", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("user-email", email).apply();
    }
    private void complete(){
        SharedPreferences sharedPreferences = getSharedPreferences("UserLogInPreferences", Context.MODE_PRIVATE);
        sharedPreferences.edit().remove("incomplete").apply();
        mAuth.signOut();
    }
    private String StrValOf(TextView view){
        return view.getText().toString();
    }
}

