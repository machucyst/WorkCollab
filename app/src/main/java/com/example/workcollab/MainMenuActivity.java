package com.example.workcollab;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.example.workcollab.databinding.ActivityMainMenuBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class MainMenuActivity extends AppCompatActivity {
    ActivityMainMenuBinding b;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users");;
    String userEmail;
    DatabaseFuncs db = new DatabaseFuncs();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        b = DataBindingUtil.setContentView(this, R.layout.activity_main_menu);
        db.InitDB(getUserEmail(), new DatabaseFuncs.Listener() {
            @Override
            public void onReceive(Map User) {
               b.editTextText.setText(User.get("Email").toString());
            }

        });

    }

    private String getUserEmail() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserLogInPreferences", Context.MODE_PRIVATE);
        return sharedPreferences.getString("user-email", "");
    }
}