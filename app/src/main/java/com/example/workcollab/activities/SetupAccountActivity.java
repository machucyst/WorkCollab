package com.example.workcollab.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.workcollab.DatabaseFuncs;
import com.example.workcollab.R;
import com.example.workcollab.databinding.ActivitySetupAccountBinding;
import com.example.workcollab.databinding.DialogAgreementFormBinding;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

public class SetupAccountActivity extends AppCompatActivity {
    DatabaseFuncs db = new DatabaseFuncs();
    Uri resultUri = null;
    FirebaseAuth mAuth;
    ActivitySetupAccountBinding b;
    DialogAgreementFormBinding dafb;
    Bundle bu;
    boolean x = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        b = DataBindingUtil.setContentView(this, R.layout.activity_setup_account);
        mAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        bu = intent.getExtras();

        //Change Profile
        b.profileImage.setOnClickListener(v -> {
            Intent intent1 = new Intent();
            intent1.setType("image/*");  // For .doc files
            intent1.setAction(Intent.ACTION_PICK);
            startActivityForResult(Intent.createChooser(intent1,"Select Picture"), 101);
        });
        //Show Password Visibility
        b.btnSubmit.setOnClickListener(v -> {
            b.btnSubmit.setText(R.string.loading);
            b.btnSubmit.setBackgroundDrawable(AppCompatResources.getDrawable(SetupAccountActivity.this,R.drawable.textholderdisabled));
            b.btnSubmit.setEnabled(false);
            AlertDialog.Builder builder = new AlertDialog.Builder(SetupAccountActivity.this);
            dafb = DialogAgreementFormBinding.inflate(getLayoutInflater());
            builder.setView(dafb.getRoot());
            AlertDialog dialog = builder.create();
            dafb.Cancel.setOnClickListener(k -> {
                dialog.dismiss();
                b.btnSubmit.setText(R.string.submit);
                b.btnSubmit.setBackgroundDrawable(AppCompatResources.getDrawable(SetupAccountActivity.this,R.drawable.textholder));
                b.btnSubmit.setEnabled(true);

            });
            dafb.Ok.setOnClickListener(k -> {
                if (resultUri == null) {
                    Toast.makeText(SetupAccountActivity.this, "Pick a profile image", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StrValOf(b.etCN).length() != 11) {
                    Toast.makeText(SetupAccountActivity.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                    return; //TODO: kys
                }
                dialog.dismiss();
                String email = bu.getString("user-email");
                String password = bu.getString("user-password");
                String username = bu.getString("user-name");
                assert password != null: "Password is null";
                db.registerAccount(mAuth, email, password, getApplicationContext(), b.btnSubmit, test ->
                        db.createAccount(
                                username,
                                EncryptPassword(password),
                                email,
                                StrValOf(b.etCN), user -> db.saveProfile(user, resultUri, user1 -> {
                                    System.out.println("Profile saved");
                                    String[] Machu = new String[1];
                                    Machu[0] = String.valueOf(user1.get("Id"));
                                    db.createGroup(user1.get("Username")+ "'s Group", Arrays.asList(Machu), String.valueOf(user1.get("Profile")), user2 -> {
                                        System.out.println("Group Created");
                                        finish();
                                    });
                                })));
            });
            dialog.show();

                });

    }
    // Crop image Activity
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(resultCode + " " + requestCode);
        switch (requestCode){
            case 101:
                if (resultCode == -1) {
                    assert data != null: "data is null";
                    Uri sourceUri = data.getData();
                    assert sourceUri != null: "sourceUri is null";
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
              }
            break;
            case UCrop.REQUEST_CROP:
                assert data != null;
                if (resultCode == RESULT_OK) {
                    resultUri = UCrop.getOutput(data);
                    try{
                        Glide.with(SetupAccountActivity.this).load(resultUri).into(b.profileImage);
                    } catch (Exception ignored){
                    }
                } else if (resultCode == UCrop.RESULT_ERROR) {
                    final Throwable cropError = UCrop.getError(data);
                    Log.d("error", String.valueOf(cropError));
                }
                String result = data.getStringExtra("RESULT");
                if (result != null) {
                    resultUri = Uri.parse(result);
                    x = true;
                }
            break;



        }
    }
    private String EncryptPassword(String password){
        assert password != null: "Password is Null";
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

