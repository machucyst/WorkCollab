package com.example.workcollab;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.workcollab.databinding.ActivityMainMenuBinding;
import com.example.workcollab.databinding.DialogLogoutConfirmBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.FirebaseApp;

import java.util.Map;

public class MainMenuActivity extends AppCompatActivity implements YouFragment.ButtonListeners, AccountEditFragment.UpdateListener, ProfileAccountEditFragment.ButtonListeners, AccountFragment.ButtonListeners {
    ActivityMainMenuBinding b;
    DialogLogoutConfirmBinding bl;
    ActivityResultLauncher<String> mGetCont;
    MainFragment mf;
    GroupsFragment gf;
    YouFragment sf;
    public static Map selectedgroup;
    int x = 1;

    public static String selected = "main";
    Bundle bu = new Bundle();
    private Map user;

    DatabaseFuncs db = new DatabaseFuncs();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this, R.layout.activity_main_menu);
        String email;
        System.out.println(getUserEmail() + "EmailBeLike2");
        if (!getUserEmail().equals("")) {
            email = getUserEmail();
        } else {
            email = bu.getString("user-email");
        }
        FirebaseApp.initializeApp(this);
        mGetCont=registerForActivityResult(new ActivityResultContracts.GetContent(), o ->{
            Intent intent = new Intent(MainMenuActivity.this, CropperActivity.class);
            intent.putExtra("DATA",o.toString());
            startActivityForResult(intent,101);
        });
        System.out.println(email + "whaiohgoeihaoieug aogoiaea");
        db.InitDB(email, new DatabaseFuncs.DataListener() {

            @Override
            public void onDataFound(Map user) {
                MainMenuActivity.this.user = user;
                if(selected.equals("main")){
                    getSupportFragmentManager().beginTransaction().replace(b.frameFragment.getId(), MainFragment.newInstance(user)).commitAllowingStateLoss();
                    b.bottomNavView.setSelectedItemId(R.id.menu_home);
                    return;
                }
                if(selected.equals("groups")){
                    getSupportFragmentManager().beginTransaction().replace(b.frameFragment.getId(), MainFragment.newInstance(user)).commitAllowingStateLoss();
                    b.bottomNavView.setSelectedItemId(R.id.menu_home);
                    return;
                }
                if(selected.equals("profile")){
                    getSupportFragmentManager().beginTransaction().replace(b.frameFragment.getId(), YouFragment.newInstance(user)).commit();
                    b.bottomNavView.setSelectedItemId(R.id.menu_account);
                    return;
                }
                if (selected.equals("creategroups")) {
                    getSupportFragmentManager().beginTransaction().replace(b.frameFragment.getId(), CreateGroupFragment.newInstance(user)).commit();
                }
                if (selected.equals("selectgroup")) {
                    getSupportFragmentManager().beginTransaction().replace(b.frameFragment.getId(), SelectedGroupFragment.newInstance(user, selectedgroup)).commit();
                }
            }

            @Override
            public void noDuplicateUser() {

            }
        });
        b.btnFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected.equals("groups")) {
                    replaceFragment(CreateGroupFragment.newInstance(user), "creategroups");
                }
            }
        });
        b.bottomNavView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int a = menuItem.getItemId();
                if(a == R.id.menu_home){
                    replaceFragment(MainFragment.newInstance(user),"main");
                } else if (a == R.id.menu_groups) {
                    replaceFragment(GroupsFragment.newInstance(user),"groups");
                }else if (a == R.id.menu_tasks){

                }else if (a == R.id.menu_profile){
                    replaceFragment(YouFragment.newInstance(user), "profile");
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
               db.SaveProfile(user, resultUri, new DatabaseFuncs.UpdateListener() {
                   @Override
                   public void onUpdate(Map user) {
                       replaceFragment(ProfileAccountEditFragment.newInstance(user),"Profile");
                   }
               });

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private String getUserEmail() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserLogInPreferences", Context.MODE_PRIVATE);
        return sharedPreferences.getString("user-email", "");
    }

    public void stayLogIn(String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserLogInPreferences", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("user-email", email).apply();
    }
    void complete(){
        SharedPreferences sharedPreferences = getSharedPreferences("UserLogInPreferences", Context.MODE_PRIVATE);
        sharedPreferences.edit().remove("user-email").apply();
    }
    public void replaceFragment(Fragment fragment, String condition){
        if(!selected.equals(condition)){
            selected = condition;
            getSupportFragmentManager().beginTransaction().replace(b.frameFragment.getId(),fragment).addToBackStack(null).commit();
        }
    }
    public void logOutConfirm(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainMenuActivity.this);

        // Inflate the layout for the dialog
        bl = DialogLogoutConfirmBinding.inflate(getLayoutInflater().from(this));
        builder.setView(bl.getRoot());
        AlertDialog dialog = builder.create();
        bl.Cancel.setOnClickListener(k->{
            dialog.cancel();
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
    public void onBackPressed() {
        if (false) {
            super.onBackPressed();
        }
        replaceFragment(MainFragment.newInstance(user), "main");
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

    @Override
    public void onPressChangePFP() {
        mGetCont.launch("image/*");
    }

    @Override
    public void onDeletedAccount() {
        complete();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
