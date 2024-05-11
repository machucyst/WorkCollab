package com.example.workcollab;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;

import com.example.workcollab.databinding.ActivityMainBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    MotionLayout parentLayout;
    Typeface rubikFont;
    DatabaseFuncs userDb = new DatabaseFuncs();
    ActivityMainBinding b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this,R.layout.activity_main);

        userDb.InitDB(checkLoggedIn(), new DatabaseFuncs.DataListener() {
            @Override
            public void onDataFound(Map user) {
                if (checkLoggedIn().equals(user.get("Email"))) {
                    Intent toMenu = new Intent(MainActivity.this, MainMenuActivity.class);
                    toMenu.putExtra("user-name", user.get("Username").toString());
                    toMenu.putExtra("user-email", user.get("Email").toString());
                    startActivity(toMenu);
                    finish();
                }
            }

            @Override
            public void noDuplicateUser() {

            }
        });


        parentLayout = findViewById(R.id.parentLayout);


        rubikFont = ResourcesCompat.getFont(this, R.font.rubik_regular);
        setupPasswordToggle(findViewById(R.id.passwordLayoutLogIn), b.etEmailSignUp);
        setupPasswordToggle(findViewById(R.id.passwordLayoutSignUp), b.etPasswordSignUp);
    }

    public void toggleButtons(View v) {
        clearFocusFromEditText(parentLayout);
        int id = v.getId();

        if (id == R.id.toLogIn || id == R.id.logInText) {
            parentLayout.transitionToState(R.id.login);
        } else if (id == R.id.toSignUp || id == R.id.signUpText) {
            parentLayout.transitionToState(R.id.signup);
        }
    }

    public void submitLogIn(View v) {
        String email = b.etEmailLogIn.getText().toString();
        String password = b.etPasswordLogIn.getText().toString();

        if (email.equals("") || password.equals("")) {
            Toast.makeText(this, "Please fill in all text fields", Toast.LENGTH_SHORT).show();
            return;
        }
        userDb.InitDB(email, new DatabaseFuncs.DataListener() {
            @Override
            public void onDataFound(Map user) {
                if (email.equals(user.get("Email")) && (password.equals(user.get("Password")))) {
                    Intent toMenu = new Intent(MainActivity.this, MainMenuActivity.class);
                    toMenu.putExtra("user-name", user.get("Username").toString());
                    toMenu.putExtra("user-email", user.get("Email").toString());
                    if (b.cbStaySignedInLogIn.isChecked()) stayLogIn(user.get("Email").toString());
                    startActivity(toMenu);
                    finish();
                }
            }

            @Override
            public void noDuplicateUser() {
                Toast.makeText(MainActivity.this,"User not found",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void submitSignUp(View v) {
        String username = b.etUsernameSignUp.getText().toString();
        String email = b.etEmailSignUp.getText().toString();
        String password = b.etPasswordSignUp.getText().toString();

        userDb.InitDB(email, new DatabaseFuncs.DataListener() {

            @Override
            public void onDataFound(Map user) {
                Toast.makeText(MainActivity.this, "Email already exists", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void noDuplicateUser() {

                if (username.equals("") || email.equals("") || password.equals("")) {
                    Toast.makeText(MainActivity.this, "Please fill in all text fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent toMenu = new Intent(MainActivity.this, SetupAccountActivity.class);
                toMenu.putExtra("user-name", username);
                toMenu.putExtra("user-email", email);
                toMenu.putExtra("user-password",password);
                toMenu.putExtra("StayLogIn", b.cbStaySignedInSignUp.isChecked());
                startActivity(toMenu);
                finish();
            }

        });

    }

    private void clearFocusFromEditText(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                clearFocusFromEditText(viewGroup.getChildAt(i));
            }
        } else if (view instanceof EditText) {
            EditText editText = (EditText) view;
            editText.clearFocus();
            editText.setText("");
        }
    }
    private void setupPasswordToggle(TextInputLayout inputLayout, TextInputEditText inputField) {
        inputLayout.setEndIconOnClickListener(v -> {
            int inputType = inputField.getInputType();
            if (inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                inputField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                inputLayout.setEndIconDrawable(getDrawable(R.drawable.ic_visible));
            } else {
                inputField.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                inputLayout.setEndIconDrawable(getDrawable(R.drawable.ic_visible_off));
            }
            inputField.setTypeface(rubikFont);
            inputField.setSelection(inputField.getText().length());
        });
    }

    private void stayLogIn(String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserLogInPreferences", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("user-email", email).apply();
    }

    private String checkLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserLogInPreferences", Context.MODE_PRIVATE);
        return sharedPreferences.getString("user-email", "");
    }

}