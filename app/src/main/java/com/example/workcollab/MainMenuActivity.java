package com.example.workcollab;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.workcollab.databinding.ActivityMainMenuBinding;
import com.example.workcollab.databinding.DialogLogoutConfirmBinding;
import com.example.workcollab.databinding.NavHeaderBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class MainMenuActivity extends AppCompatActivity implements SettingsFragment.ButtonListeners,AccountEditFragment.UpdateListener{
    ActivityMainMenuBinding b;
    DialogLogoutConfirmBinding bl;
    ActivityResultLauncher<String> mGetCont;
    MainFragment mf;
    GroupsFragment gf;
    SettingsFragment sf;
    int x = 1;
    public static String selected = "main";
    private Map user;

    DatabaseFuncs db = new DatabaseFuncs();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this, R.layout.activity_main_menu);

        b.btnFab.setBackground(getDrawable(R.drawable.customfab));
        FirebaseApp.initializeApp(this);
        mGetCont=registerForActivityResult(new ActivityResultContracts.GetContent(), o ->{
            Intent intent = new Intent(MainMenuActivity.this, CropperActivity.class);
            intent.putExtra("DATA",o.toString());
            startActivityForResult(intent,101);
        });

        db.InitDB(getUserEmail(), new DatabaseFuncs.DataListener() {

            @Override
            public void onDataFound(Map user) {
                MainMenuActivity.this.user = user;
                 mf = MainFragment.newInstance(user);
                 gf = GroupsFragment.newInstance(user);
                 sf = SettingsFragment.newInstance(user);
                if(selected.equals("main")){
                    getSupportFragmentManager().beginTransaction().replace(b.frameFragment.getId(), mf).commit();
                    b.bottomNavView.setSelectedItemId(R.id.menu_home);
                    return;
                }
                if(selected.equals("groups")){
                    getSupportFragmentManager().beginTransaction().replace(b.frameFragment.getId(), gf).commit();
                    b.bottomNavView.setSelectedItemId(R.id.menu_groups);
                    return;
                }
                if(selected.equals("profile")){
                    getSupportFragmentManager().beginTransaction().replace(b.frameFragment.getId(), sf).commit();
                    b.bottomNavView.setSelectedItemId(R.id.menu_account);
                    return;
                }
            }

            @Override
            public void noDuplicateUser() {

            }
        });
        b.bottomNavView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int a = menuItem.getItemId();
                System.out.println(user.get("Email").toString());
                if(a == R.id.menu_home){
                    replaceFragment(MainFragment.newInstance(user),"main");
                }else if (a == R.id.menu_groups){
                    System.out.println(user);
                    replaceFragment(GroupsFragment.newInstance(user),"groups");
                }else if (a == R.id.menu_tasks){

                }else if (a == R.id.menu_profile){
                    replaceFragment(SettingsFragment.newInstance(user),"profile");
                }
                return true;
            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(resultCode+" "+requestCode);
        if (resultCode ==-1&&requestCode == 101){
            String result = data.getStringExtra("RESULT");
            Uri resultUri = null;
            if(result!=null){
                resultUri = Uri.parse(result);
            }
            try{
               db.SaveProfile(user,resultUri);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public void stayLogIn(String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserLogInPreferences", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("user-email", email).apply();
    }
    private String getUserEmail() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserLogInPreferences", Context.MODE_PRIVATE);
        return sharedPreferences.getString("user-email", "");
    }
    void complete(){
        SharedPreferences sharedPreferences = getSharedPreferences("UserLogInPreferences", Context.MODE_PRIVATE);
        sharedPreferences.edit().remove("user-email").apply();
    }
    public void replaceFragment(Fragment fragment, String condition){
        if(selected!=condition){
            selected = condition;
            getSupportFragmentManager().beginTransaction().replace(b.frameFragment.getId(),fragment).commit();
        }
    }
    public void logOutConfirm(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainMenuActivity.this);

        // Inflate the layout for the dialog
        bl = DialogLogoutConfirmBinding.inflate(getLayoutInflater().from(this));
        builder.setView(bl.getRoot());
        AlertDialog dialog = builder.create();
        bl.Cancel.setOnClickListener(k->{
            dialog.cancel();;
        });
        bl.Ok.setOnClickListener(k ->{
            dialog.cancel();
            Intent toLogIn = new Intent(MainMenuActivity.this, MainActivity.class);
            startActivity(toLogIn);
            complete();
            finish();

        });
        dialog.show();


    }
    @Override
    public void onPress() {
        mGetCont.launch("image/*");
    }

    @Override
    public void onLogOutPress() {
        logOutConfirm();
    }

    @Override
    public void onUpdatedEmail(Map user) {
        stayLogIn(user.get("Email").toString());
        this.user = user;
    }
}
