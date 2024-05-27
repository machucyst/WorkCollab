package com.example.workcollab.activities;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.workcollab.DatabaseFuncs;
import com.example.workcollab.R;
import com.example.workcollab.databinding.ActivitySetupAccountBinding;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.yalantis.ucrop.UCrop;

import java.io.File;
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

        b.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 101);
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
                    resultUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                            "://" + getResources().getResourcePackageName(R.drawable.icon_test)
                            + '/' + getResources().getResourceTypeName(R.drawable.icon_test)
                            + '/' + getResources().getResourceEntryName(R.drawable.icon_test));
                }
                if (StrValOf(b.etCN).length() != 11) {
                    Toast.makeText(SetupAccountActivity.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                    return; //TODO: kys
                }
                b.btnShowPass.setText("Loading...");
                b.btnShowPass.setBackgroundDrawable(AppCompatResources.getDrawable(SetupAccountActivity.this,R.drawable.textholderdisabled));
                b.btnShowPass.setEnabled(false);

                db.registerAccount(mAuth, bu.getString("user-email"), bu.getString("user-password"), getApplicationContext(), b.btnShowPass,new DatabaseFuncs.EmailAuthListener() {
                    @Override
                    public void changeLayout(boolean test) {
                        db.createAccount(bu.getString("user-name"), EncryptPassword(bu.getString("user-password")), bu.getString("user-email"), StrValOf(b.etCN), new DatabaseFuncs.UpdateListener() {
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
                    Uri sourceUri = data.getData();
                    // Destination URI
                    Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "IMG_" + System.currentTimeMillis()));
                    // Start UCrop activity
                    UCrop.Options options = new UCrop.Options();
                    options.setContrastEnabled(false);
                    options.setBrightnessEnabled(false);
                    options.setFreeStyleCropEnabled(false);
                    options.setSaturationEnabled(false);
                    options.setSharpnessEnabled(false);
                    options.setShowCropGrid(false);
                    UCrop.of(sourceUri, destinationUri)
                            .withAspectRatio(1, 1)
                            .withMaxResultSize(450, 450)
                            .withOptions(options)
                            .start(this);
                } else if (requestCode == UCrop.REQUEST_CROP) {
                    if (resultCode == RESULT_OK) {
                        resultUri = UCrop.getOutput(data);
                        try{
                            Glide.with(this).asBitmap().load(resultUri).into(b.profileImage);
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    } else if (resultCode == UCrop.RESULT_ERROR) {
                        final Throwable cropError = UCrop.getError(data);
                        // Handle the error
                    }
                    String result = data.getStringExtra("RESULT");
                    resultUri = null;
                    if (result != null) {
                        resultUri = Uri.parse(result);
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
    private String EncryptPassword(String password){
        char[] encpass = password.toCharArray();
        StringBuilder pass = new StringBuilder();
        for(char c: encpass){
            c+=7;
            pass.append(c);
        }
        return pass.toString();
    }
    private String StrValOf(TextView view){
        return view.getText().toString();
    }
}

