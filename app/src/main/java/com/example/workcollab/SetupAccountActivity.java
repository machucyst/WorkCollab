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

public class SetupAccountActivity extends AppCompatActivity {
    DatabaseFuncs db = new DatabaseFuncs();
    UsersDatabase userDb = new UsersDatabase(this);
    String userEmail,userPassword,userName;
    String[] groups = {"null"};
    ActivitySetupAccountBinding b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        b = DataBindingUtil.setContentView(this,R.layout.activity_setup_account);

        User user = null;
        Intent intent = getIntent();
        Bundle bu = intent.getExtras();

        if (bu != null) {
            userEmail = (String) bu.get("user-email");               // this
            userPassword = bu.getString("user-password");
            userName = bu.getString("user-name");
            incomplete(userEmail);
            user = userDb.findUserByEmail(userEmail);
        }


        b.btnShowPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (isEmptyField(b.etFN)||isEmptyField(b.etLN)||isEmptyField(b.etMI)||isEmptyField(b.etAGE)||isEmptyField(b.etCN)||isEmptyField(b.etAD)){
                        Toast.makeText(SetupAccountActivity.this, "Please Fill In All Text Fields", Toast.LENGTH_SHORT).show();
                    }else{
//                        try{
                            String test = b.etAGE.getText().toString();
                            int x = Integer.valueOf(test);
                            User user = null;
                            Intent intent = getIntent();
                            user = userDb.findUserByEmail(userEmail);

                            if (userEmail.equals("")) userEmail = incompleteSignUp();
                            db.InitDB();
                            db.CreateAccount(userName,userPassword,userEmail,Integer.valueOf(StrValOf(b.etCN)), groups);
//                            User userB = new User(user.getName(),userEmail,user.getPassword(),StrValOf(b.etFN),StrValOf(b.etLN),StrValOf(b.etMI),Integer.parseInt(StrValOf(b.etAGE)),StrValOf(b.etCN),null,StrValOf(b.etAD),null);
//                            userB.setProfile(null);
//                            userDb.updateUser(userB);
//                            userDb.addUser(userB);
//                            FirebaseAuth.getInstance().createUserWithEmailAndPassword(userEmail,user.getPassword());
//                            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users");
//                            dbRef.child("users").setValue(userB);
                            Intent toMenu = new Intent(SetupAccountActivity.this, MainMenuActivity.class);
                            stayLogIn(userEmail);
                            complete();
                            startActivity(toMenu);
                            finish();
//
//                        } catch (Exception ex){
//                            Toast.makeText(SetupAccountActivity.this, "Invalid Age", Toast.LENGTH_SHORT).show();
//                            System.out.println(ex);
//                        }

                    }


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