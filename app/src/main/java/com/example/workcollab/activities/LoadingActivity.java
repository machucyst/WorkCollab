package com.example.workcollab.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.workcollab.DatabaseFuncs;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Map;

public class LoadingActivity extends AppCompatActivity {

    DatabaseFuncs db = new DatabaseFuncs();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    boolean y = false;

    //TODO: design this
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getUserEmail().equals("") || mAuth.getCurrentUser() == null){
                Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
        }else{
                db.InitDB(getUserEmail(), new DatabaseFuncs.DataListener() {
                @Override
                public void onDataFound(Map user) {
                    Intent intent = new Intent(LoadingActivity.this, MainMenuActivity.class);
                    y = true;
                    startActivity(intent);
                    finish();
                }

                @Override
                public void noDuplicateUser() {
                    Toast.makeText(LoadingActivity.this, "Error Loading Account", Toast.LENGTH_SHORT);
                    Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    mAuth.signOut();
                    complete();
                }
            });
        }

    }

    private String getUserEmail() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserLogInPreferences", Context.MODE_PRIVATE);
        return sharedPreferences.getString("user-email", "");
    }

    private void complete() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserLogInPreferences", Context.MODE_PRIVATE);
        sharedPreferences.edit().remove("user-email").apply();
    }
}

