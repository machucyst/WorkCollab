package com.example.workcollab;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.workcollab.databinding.ActivitySetupAccountBinding;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SetupAccountActivity extends AppCompatActivity {
    DatabaseFuncs db = new DatabaseFuncs();
    String userEmail,userPassword,userName;
    ActivitySetupAccountBinding b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        b = DataBindingUtil.setContentView(this,R.layout.activity_setup_account);

        Intent intent = getIntent();
        Bundle bu = intent.getExtras();



        b.btnShowPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.CreateAccount(bu.getString("user-name"), bu.getString("user-password"), bu.getString("user-email"), StrValOf(b.etCN), new DatabaseFuncs.UpdateListener() {
                    @Override
                    public void onUpdate(Map user) {
                        Intent toMenu = new Intent(SetupAccountActivity.this, MainMenuActivity.class);
                        stayLogIn(user.get("Email").toString());
                        complete();
                        startActivity(toMenu);
                        finish();

                    }
                });
            }
        });
    }
    private void stayLogIn(String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserLogInPreferences", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("user-email", email).apply();
    }
    private void incomplete(String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserLogInPreferences", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("incomplete", email).apply();                            // ah nevermind
    }
    private void complete(){
        SharedPreferences sharedPreferences = getSharedPreferences("UserLogInPreferences", Context.MODE_PRIVATE);
        sharedPreferences.edit().remove("incomplete").apply();
    }
    private String incompleteSignUp(){
        SharedPreferences sharedPreferences = getSharedPreferences("UserLogInPreferences", Context.MODE_PRIVATE);
        return sharedPreferences.getString("incomplete", "");
    }
    public boolean isEmptyField(TextView tv){
       if (tv.getText().toString().equals("")) {

           return true;
       }else{
           return false;
       }

    }
    private String StrValOf(TextView view){
        return view.getText().toString();
    }
}

